package com.example.guantimber.service;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.PowerManager;
import android.os.RemoteException;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.palette.graphics.Palette;

import com.example.guantimber.IMusicPlaybackService;
import com.example.guantimber.R;
import com.example.guantimber.data.SongTrack;
import com.example.guantimber.dataloaders.ArtworkLoader;
import com.example.guantimber.dataloaders.TrackLoader;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Arrays;

public class MusicPlaybackService extends Service {
    //TAG for logging
    public static final String EXIT_ACTION = "com.guantimber.exit";
    public static final String TAG = "MusicPlaybackService";
    private static final String SHUTDOWN = "com.naman14.timber.shutdown";
    private static final int TRACK_ENDED = 1;
    private static final int TRACK_WENT_TO_NEXT = 2;
    private static final int RELEASE_WAKELOCK = 3;
    private static final int SERVER_DIED = 4;
    private static final int FOCUSCHANGE = 5;
    private static final int FADEDOWN = 6;
    private static final int FADEUP = 7;
    private static final String CHANNEL_ID = "guan_timber_channel_01";

    private int mServiceStartId = -1;
    private boolean mShutdownScheduled;

    private final IBinder mBinder = new ServiceStub(this);

    //播放歌曲相关的记录信息
    private int mPlayPos = -1;
    private ArrayList<Long> mPlaylist = new ArrayList<Long>(100);
    private SongTrack mCurrentSong;

    private MultiPlayer mPlayer;
    private HandlerThread mHandlerThread;

    private final BroadcastReceiver mIntentReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            if (EXIT_ACTION.equals(intent.getAction())) {
                stopForeground(true);
                stopSelf();
            }

        }
    };


    @Override
    public void onCreate() {
        super.onCreate();
        createNotificationChannel();

        mHandlerThread = new HandlerThread("MusicPlayerHander",
                android.os.Process.THREAD_PRIORITY_BACKGROUND);
        mHandlerThread.start();

        mPlayer = new MultiPlayer(this);
        mPlayer.setHandler(new MusicPlayerHandler(this,mHandlerThread.getLooper()));

        // get Wake Lock to keep servie running while device is in sleeping mode
        final PowerManager powerManager = (PowerManager) getSystemService(Context.POWER_SERVICE);
        mWakeLock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, getClass().getName());
        mWakeLock.setReferenceCounted(false);


        final IntentFilter filter = new IntentFilter();
        filter.addAction(EXIT_ACTION);
        registerReceiver(mIntentReceiver, filter);
    }


    /**
     * What && Why should we do it here
     *
     * @param intent
     * @param flags
     * @param startId
     * @return
     */
    @Override
    public int onStartCommand(final Intent intent, final int flags, final int startId) {

        mServiceStartId = startId;

        if (intent != null) {
            final String action = intent.getAction();
            Log.d(TAG, "onStartCommand: action = " + action);

            if (SHUTDOWN.equals(action)) {
                mShutdownScheduled = false;
//                releaseServiceUiAndStop();
                return START_NOT_STICKY;
            }

//            handleCommandIntent(intent);
        }

//        scheduleDelayedShutdown();

//        if (intent != null && intent.getBooleanExtra(FROM_MEDIA_BUTTON, false)) {
//            MediaButtonIntentReceiver.completeWakefulIntent(intent);
//        }

        return START_NOT_STICKY; //no sense to use START_STICKY with using startForeground
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy: ");

        mPlayer.release();

        unregisterReceiver(mIntentReceiver);
    }


    private PowerManager.WakeLock mWakeLock;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }


    private void updateNotification() {
        int notificationId = hashCode();

        startForeground(notificationId, buildNotification());
    }


    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "G-Music";
            int importance = NotificationManager.IMPORTANCE_LOW;
            NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            NotificationChannel mChannel = new NotificationChannel(CHANNEL_ID, name, importance);
            mChannel.setShowBadge(false);
            manager.createNotificationChannel(mChannel);
        }
    }

    private Notification buildNotification() {
        String artistName = getArtistName();
        String albumName = getAlbumName();
        Bitmap artwork = null;
        String text = TextUtils.isEmpty(albumName)
                ? artistName : artistName + " - " + albumName;


        artwork = ArtworkLoader.loadImageSync(this, getAudioId());

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_notification)
                .setLargeIcon(artwork)
                .setContentTitle(getTrackName())
                .setContentText(text)
                .setShowWhen(false)
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC);

        Intent intent = new Intent();
        intent.setAction(EXIT_ACTION);
        builder.addAction(new NotificationCompat.Action(R.drawable.ic_notification,
                "Exit",
                PendingIntent.getBroadcast(this, 1, intent, 0)));

        //style the notification
        androidx.media.app.NotificationCompat.MediaStyle style = new androidx.media.app.NotificationCompat.MediaStyle();
        style.setShowActionsInCompactView(0, 1, 2, 3);
//        builder.setStyle(style);

        builder.setColor(Palette.from(artwork).generate().getVibrantColor(Color.parseColor("#403f4d")));
        builder.setColorized(true);

        return builder.build();
    }

    private static final class TrackErrorInfo {
        public long mId;
        public String mTrackName;

        public TrackErrorInfo(long id, String trackName) {
            mId = id;
            mTrackName = trackName;
        }
    }

    private final class ServiceStub extends IMusicPlaybackService.Stub {

        private final WeakReference<MusicPlaybackService> mService;

        public ServiceStub(final MusicPlaybackService service) {
            mService = new WeakReference<MusicPlaybackService>(service);
        }

        @Override
        public void openFile(String path) throws RemoteException {

        }

        @Override
        public void open(long[] list, int position, long sourceId, int sourceType) throws RemoteException {

            mService.get().open(list, position, sourceId, sourceType);
        }

        @Override
        public void stop() throws RemoteException {

        }

        @Override
        public void pause() throws RemoteException {

        }

        @Override
        public void play() throws RemoteException {
            mService.get().play();
        }

        @Override
        public void prev(boolean forcePrevious) throws RemoteException {

        }

        @Override
        public void next() throws RemoteException {

        }

        @Override
        public void enqueue(long[] list, int action, long sourceId, int sourceType) throws RemoteException {

        }

        @Override
        public void setQueuePosition(int index) throws RemoteException {

        }

        @Override
        public void setShuffleMode(int shufflemode) throws RemoteException {

        }

        @Override
        public void setRepeatMode(int repeatmode) throws RemoteException {

        }

        @Override
        public void moveQueueItem(int from, int to) throws RemoteException {

        }

        @Override
        public void refresh() throws RemoteException {

        }

        @Override
        public void playlistChanged() throws RemoteException {

        }

        @Override
        public boolean isPlaying() throws RemoteException {
            return false;
        }

        @Override
        public long[] getQueue() throws RemoteException {
            return new long[0];
        }

        @Override
        public long getQueueItemAtPosition(int position) throws RemoteException {
            return 0;
        }

        @Override
        public int getQueueSize() throws RemoteException {
            return 0;
        }

        @Override
        public int getQueuePosition() throws RemoteException {
            return 0;
        }

        @Override
        public int getQueueHistoryPosition(int position) throws RemoteException {
            return 0;
        }

        @Override
        public int getQueueHistorySize() throws RemoteException {
            return 0;
        }

        @Override
        public int[] getQueueHistoryList() throws RemoteException {
            return new int[0];
        }

        @Override
        public long duration() throws RemoteException {
            return 0;
        }

        @Override
        public long position() throws RemoteException {
            return 0;
        }

        @Override
        public long seek(long pos) throws RemoteException {
            return 0;
        }

        @Override
        public void seekRelative(long deltaInMs) throws RemoteException {

        }

        @Override
        public long getAudioId() throws RemoteException {
            return mService.get().getAudioId();
        }

        @Override
        public long getNextAudioId() throws RemoteException {
            return 0;
        }

        @Override
        public long getPreviousAudioId() throws RemoteException {
            return 0;
        }

        @Override
        public long getArtistId() throws RemoteException {
            return 0;
        }

        @Override
        public long getAlbumId() throws RemoteException {
            return 0;
        }

        @Override
        public String getArtistName() throws RemoteException {
            return mService.get().getArtistName();
        }

        @Override
        public String getTrackName() throws RemoteException {
            return mService.get().getTrackName();
        }

        @Override
        public String getAlbumName() throws RemoteException {
            return null;
        }

        @Override
        public String getPath() throws RemoteException {
            return null;
        }

        @Override
        public int getShuffleMode() throws RemoteException {
            return 0;
        }

        @Override
        public int removeTracks(int first, int last) throws RemoteException {
            return 0;
        }

        @Override
        public int removeTrack(long id) throws RemoteException {
            return 0;
        }

        @Override
        public boolean removeTrackAtPosition(long id, int position) throws RemoteException {
            return false;
        }

        @Override
        public int getRepeatMode() throws RemoteException {
            return 0;
        }

        @Override
        public int getMediaMountedCount() throws RemoteException {
            return 0;
        }

        @Override
        public int getAudioSessionId() throws RemoteException {
            return 0;
        }
    }

    private void play() {
        mPlayer.start();

        updateNotification();
    }

    private void open(long[] list, int position, long sourceId, int sourceType) {
        synchronized (this) {
            Log.d(TAG, "open: list (size = " + list.length + ")" + Arrays.toString(list) + " , position = " + position);
            //update current position in playlist
            mPlayPos = position;
            //clean playlist
            mPlaylist.clear();
            for (int i = 0; i < list.length; i++) {
                mPlaylist.add(list[i]);
            }

            //open current file and be ready to play
            openCurrentAndNext();
        }
    }

    private void openCurrentAndNext() {
        synchronized (this) {
            if (mPlaylist.size() == 0) return;

            Long id = mPlaylist.get(mPlayPos);
            String filePath = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI + "/" + id;

            //update nowplaying track with the id
            mCurrentSong = TrackLoader.getTrackWithId(this, id);

            Log.d(TAG, "openCurrentAndNext: current song is : " + mCurrentSong.getTitle());
            mPlayer.setDataSource(filePath);
            if (mPlayer.isInitialized()) {
                return;
            }

        }

    }

    private static final class MultiPlayer implements MediaPlayer.OnErrorListener,
            MediaPlayer.OnCompletionListener {

        private final WeakReference<MusicPlaybackService> mService;

        private MediaPlayer mCurrentMediaPlayer = new MediaPlayer();

        private MediaPlayer mNextMediaPlayer;

        /**
         * deviler media file event(Error happen in MediaPlayer or completed playing ) with Handler
         */
        private Handler mHandler;

        private boolean mIsInitialized = false;

        private String mNextMediaPath;


        public MultiPlayer(final MusicPlaybackService service) {
            mService = new WeakReference<MusicPlaybackService>(service);
            mCurrentMediaPlayer.setWakeMode(mService.get(), PowerManager.PARTIAL_WAKE_LOCK);
        }

        public void setDataSource(final String path) {
            try {
                mIsInitialized = setDataSourceImpl(mCurrentMediaPlayer, path);
                if (mIsInitialized) {
                    setNextDataSource(null);
                }
            } catch (IllegalStateException e) {
                e.printStackTrace();
            }
        }


        // Set data srouce for a MediaPlayer, current player or next player.
        //we have reset the player before setData on it ,so this will be safe to memory leak
        private boolean setDataSourceImpl(final MediaPlayer player, final String path) {
            try {
                player.reset();
                player.setOnPreparedListener(null);
                if (path.startsWith("content://")) {
                    player.setDataSource(mService.get(), Uri.parse(path));
                } else {
                    player.setDataSource(path);
                }
                player.setAudioStreamType(AudioManager.STREAM_MUSIC);

                player.prepare();
            } catch (final IOException todo) {

                return false;
            } catch (final IllegalArgumentException todo) {

                return false;
            }
            player.setOnCompletionListener(this);
            player.setOnErrorListener(this);
            return true;
        }


        public void setNextDataSource(final String path) {
            mNextMediaPath = null;
            try {
                mCurrentMediaPlayer.setNextMediaPlayer(null);
            } catch (IllegalArgumentException e) {
                Log.i(TAG, "Next media player is current one, continuing");
            } catch (IllegalStateException e) {
                Log.e(TAG, "Media player not initialized!");
                return;
            }
            if (mNextMediaPlayer != null) {
                mNextMediaPlayer.release();
                mNextMediaPlayer = null;
            }
            if (path == null) {
                return;
            }
            mNextMediaPlayer = new MediaPlayer();
            mNextMediaPlayer.setWakeMode(mService.get(), PowerManager.PARTIAL_WAKE_LOCK);
            mNextMediaPlayer.setAudioSessionId(getAudioSessionId());
            try {
                if (setDataSourceImpl(mNextMediaPlayer, path)) {
                    mNextMediaPath = path;
                    mCurrentMediaPlayer.setNextMediaPlayer(mNextMediaPlayer);
                } else {
                    if (mNextMediaPlayer != null) {
                        mNextMediaPlayer.release();
                        mNextMediaPlayer = null;
                    }
                }
            } catch (IllegalStateException e) {
                e.printStackTrace();
            }
        }


        public void setHandler(final Handler handler) {
            mHandler = handler;
        }


        public boolean isInitialized() {
            return mIsInitialized;
        }


        public void start() {
            mCurrentMediaPlayer.start();
        }


        public void stop() {
            mCurrentMediaPlayer.reset();
            mIsInitialized = false;
        }


        public void release() {
            mCurrentMediaPlayer.release();
        }


        public void pause() {
            mCurrentMediaPlayer.pause();
        }


        public long duration() {
            return mCurrentMediaPlayer.getDuration();
        }


        public long position() {
            return mCurrentMediaPlayer.getCurrentPosition();
        }


        public long seek(final long whereto) {
            mCurrentMediaPlayer.seekTo((int) whereto);
            return whereto;
        }


        public void setVolume(final float vol) {
            try {
                mCurrentMediaPlayer.setVolume(vol, vol);
            } catch (IllegalStateException e) {
                e.printStackTrace();
            }
        }

        public int getAudioSessionId() {
            return mCurrentMediaPlayer.getAudioSessionId();
        }

        public void setAudioSessionId(final int sessionId) {
            mCurrentMediaPlayer.setAudioSessionId(sessionId);
        }

        @Override
        public boolean onError(final MediaPlayer mp, final int what, final int extra) {
            Log.w(TAG, "Music Server Error what: " + what + " extra: " + extra);
            switch (what) {
                case MediaPlayer.MEDIA_ERROR_SERVER_DIED:
                    final MusicPlaybackService service = mService.get();
                    final TrackErrorInfo errorInfo = new TrackErrorInfo(service.getAudioId(),
                            service.getTrackName());

                    mIsInitialized = false;
                    mCurrentMediaPlayer.release();
                    mCurrentMediaPlayer = new MediaPlayer();
                    mCurrentMediaPlayer.setWakeMode(service, PowerManager.PARTIAL_WAKE_LOCK);

                    Message msg = mHandler.obtainMessage(SERVER_DIED, errorInfo);
                    mHandler.sendMessageDelayed(msg, 2000);
                    return true;
                default:
                    break;
            }
            return false;
        }


        @Override
        public void onCompletion(final MediaPlayer mp) {
            if (mp == mCurrentMediaPlayer && mNextMediaPlayer != null) {
                mCurrentMediaPlayer.release();
                mCurrentMediaPlayer = mNextMediaPlayer;
                mNextMediaPath = null;
                mNextMediaPlayer = null;
                mHandler.sendEmptyMessage(TRACK_WENT_TO_NEXT);
            } else {
                mService.get().mWakeLock.acquire(30000);
                mHandler.sendEmptyMessage(TRACK_ENDED);
                mHandler.sendEmptyMessage(RELEASE_WAKELOCK);
            }
        }
    }

    private static final class MusicPlayerHandler extends Handler {

        private final MusicPlaybackService mService;

        public MusicPlayerHandler(final MusicPlaybackService service, final Looper looper) {
            super(looper);
            mService = service;
        }

        @Override
        public void handleMessage(@NonNull Message msg) {
            switch (msg.what) {
                case TRACK_ENDED:

                    mService.stopSelf();
                    break;

                default:
                    break;


            }
        }
    }

    private String getTrackName() {
        return mCurrentSong.getTitle();
    }

    private String getArtistName() {
        return mCurrentSong.getArtist();
    }

    private String getAlbumName() {
        return mCurrentSong.getAlbum();
    }

    private long getAudioId() {
        return mCurrentSong.getId();
    }
}
