package c.seven.amateurvideoplayer.view;

import android.content.Context;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.SurfaceHolder;
import android.view.ViewGroup;

import java.io.IOException;
import java.lang.ref.WeakReference;

import c.seven.amateurvideoplayer.PlayerConfig;
import c.seven.amateurvideoplayer.VideoBean;
import c.seven.amateurvideoplayer.control.MediaStateListener;
import tv.danmaku.ijk.media.player.AndroidMediaPlayer;
import tv.danmaku.ijk.media.player.IMediaPlayer;
import tv.danmaku.ijk.media.player.IjkMediaPlayer;

/**
 * Created by j-songsaihua-ol on 2018/3/23.
 */

public class VideoPlayer extends BaseVideoPlayer {
    private static final String TAG = "VideoPlayer";
    private IMediaPlayer iMediaPlayer;
    private PlayerSurfaceView playerSurfaceView;
    private MediaStateListener mediaStateListener;
    private int currentState = -1;
    public static final int PREPARE_STATE = 0;
    public static final int PREPARED_STATE = 1;
    public static final int RANDER_STATE = 2;
    public static final int PLAYING_STATE = 3;
    public static final int PAUSE_STATE = 4;
    public static final int COMPLETE_STATE = 5;
    public static final int ERROR_STATE = 6;
    private VideoBean currentVideo;
    private HandlerThread videoPlayerThread;
    private VideoPlayerHandler videoPlayerHandler;
    private static final int PREPARE_PLAYER = 1;
    private static final int RELASE_PLAYER = 2;
    public VideoPlayer(Context context) {
        super(context);
        initView();
    }

    public VideoPlayer(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initView();
    }

    private void initView() {
        videoPlayerThread = new HandlerThread(TAG);
        videoPlayerThread.start();
        videoPlayerHandler = new VideoPlayerHandler(this,videoPlayerThread.getLooper());
    }

    private void initPlayer() {
        if (PlayerConfig.isUseIjkPlayer) {
            iMediaPlayer = new IjkMediaPlayer();
        } else {
            iMediaPlayer = new AndroidMediaPlayer();
        }
        playerSurfaceView = new PlayerSurfaceView(getContext());
        playerSurfaceView.getHolder().addCallback(this);
        LayoutParams lp = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        lp.addRule(CENTER_IN_PARENT);
        playerSurfaceView.setLayoutParams(lp);
        addView(playerSurfaceView);
    }

    public void setMediaStateListener(MediaStateListener mediaStateListener) {
        this.mediaStateListener = mediaStateListener;
    }

    public void startVideo(VideoBean videoBean) {
        if (videoBean != null) {
            currentVideo = videoBean;
            initPlayer();
        }
    }

    public void retryPlay() {
        if (currentVideo != null) {
            initPlayer();
        }
    }

    @Override
    void prepare() {
        try {
            currentState = PREPARE_STATE;
            setState(currentState);
            Message message = Message.obtain();
            message.what = PREPARE_PLAYER;
            videoPlayerHandler.sendMessage(message);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void initPayerSetting(){
        try {
            iMediaPlayer.setOnPreparedListener(this);
            iMediaPlayer.setOnVideoSizeChangedListener(this);
            iMediaPlayer.setOnBufferingUpdateListener(this);
            iMediaPlayer.setOnInfoListener(this);
            iMediaPlayer.setOnSeekCompleteListener(this);
            iMediaPlayer.setOnErrorListener(this);
            iMediaPlayer.setOnCompletionListener(this);
            iMediaPlayer.setScreenOnWhilePlaying(true);
            iMediaPlayer.setDataSource(currentVideo.getPlayUrl());
            iMediaPlayer.setDisplay(playerSurfaceView.getHolder());
            iMediaPlayer.prepareAsync();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    void prepared() {
        currentState = PREPARED_STATE;
        setState(currentState);
        iMediaPlayer.start();
    }

    @Override
    void render() {
        currentState = RANDER_STATE;
        setState(currentState);
    }

    void start() {
        if (isPause()) {
            iMediaPlayer.start();
            currentState = PLAYING_STATE;
            setState(currentState);
        }
    }

    void pause() {
        if (isPlaying()) {
            iMediaPlayer.pause();
            currentState = PAUSE_STATE;
            setState(currentState);
        }
    }

    boolean isPlaying() {
        return currentState == PLAYING_STATE || currentState == RANDER_STATE;
    }

    public boolean isPause() {
        return currentState == PAUSE_STATE;
    }

    public void release() {
        currentState = -1;
        setState(currentState);
        Message message = Message.obtain();
        message.what = RELASE_PLAYER;
        videoPlayerHandler.sendMessage(message);
    }

    private void handleRelasePlayer() {
        if (playerSurfaceView != null) {
            SurfaceHolder holder = playerSurfaceView.getHolder();
            if (holder != null) {
                if (holder.getSurface().isValid()) {
                    holder.getSurface().release();
                }
                holder.removeCallback(this);
            }
        }
        if (iMediaPlayer != null) {
            final IMediaPlayer player = iMediaPlayer;
            if (player.isPlaying()) {
                player.stop();
            }
            player.reset();
            player.release();
        }
    }

    void seekTo(long time) {
        if (iMediaPlayer != null && currentState == PLAYING_STATE ||
                currentState == PAUSE_STATE || currentState == PREPARED_STATE ||
                currentState == COMPLETE_STATE || currentState == RANDER_STATE) {
            iMediaPlayer.seekTo(time);
        }
    }

    long getDuration() {
        return iMediaPlayer == null ? 0 : iMediaPlayer.getDuration();
    }

    long getCurrentPosition() {
        return iMediaPlayer == null ? 0 : iMediaPlayer.getCurrentPosition();
    }

    @Override
    void onVideoSize() {
        if (playerSurfaceView != null && iMediaPlayer != null) {
            playerSurfaceView.setVideoSize(iMediaPlayer.getVideoWidth(),iMediaPlayer.getVideoHeight());
        }
    }

    @Override
    void onBuffering(int percent) {

    }

    @Override
    void onCompletion() {
        currentState = COMPLETE_STATE;
        setState(currentState);
    }

    @Override
    void onError(int what, int extra) {
        if (what != 38 && extra != -38 && what != -38 && extra != 38 && extra != -19) {
            currentState = ERROR_STATE;
            setState(currentState);
            Message message = Message.obtain();
            message.what = RELASE_PLAYER;
            videoPlayerHandler.sendMessage(message);
        }
    }

    @Override
    void onInfo(int what, int extra) {

    }

    @Override
    void onSeekComplete() {

    }

    @Override
    void resetSurfaceHolder(SurfaceHolder holder) {
        if (iMediaPlayer != null) {
            iMediaPlayer.setDisplay(null);
            iMediaPlayer.setDisplay(holder);
        }
    }

    public int getCurrentState() {
        return currentState;
    }

    private void setState(int state) {
        if (mediaStateListener == null) {
            return;
        }
        switch (state) {
            case PREPARE_STATE:
                mediaStateListener.prepareState();
                break;
            case PREPARED_STATE:
                mediaStateListener.preparedState();
                break;
            case RANDER_STATE:
                mediaStateListener.renderState();
                break;
            case PLAYING_STATE:
                mediaStateListener.playingState();
                break;
            case PAUSE_STATE:
                mediaStateListener.pauseState();
                break;
            case COMPLETE_STATE:
                mediaStateListener.completeState();
                break;
            case ERROR_STATE:
                mediaStateListener.errorState();
                break;
        }
    }

    private static class VideoPlayerHandler extends Handler {
        private WeakReference<VideoPlayer> view;
        public VideoPlayerHandler(VideoPlayer videoPlayer, Looper looper) {
            super(looper);
            view = new WeakReference<VideoPlayer>(videoPlayer);
        }

        @Override
        public void handleMessage(Message msg) {
            VideoPlayer player = view.get();
            if (player == null) {
                return;
            }
            if (msg.what == PREPARE_PLAYER) {
                player.initPayerSetting();
            } else if (msg.what == RELASE_PLAYER) {
                player.handleRelasePlayer();
            }
            super.handleMessage(msg);
        }
    }
}
