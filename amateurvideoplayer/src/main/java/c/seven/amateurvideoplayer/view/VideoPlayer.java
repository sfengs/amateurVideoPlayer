package c.seven.amateurvideoplayer.view;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.SurfaceHolder;
import android.view.ViewGroup;

import c.seven.amateurvideoplayer.PlayerConfig;
import c.seven.amateurvideoplayer.VideoBean;
import tv.danmaku.ijk.media.player.AndroidMediaPlayer;
import tv.danmaku.ijk.media.player.IMediaPlayer;
import tv.danmaku.ijk.media.player.IjkMediaPlayer;

/**
 * Created by j-songsaihua-ol on 2018/3/23.
 */

public class VideoPlayer extends BaseVideoPlayer {
    private IMediaPlayer iMediaPlayer;
    private PlayerSurfaceView playerSurfaceView;
    private int currentState = -1;
    public static final int PREPARE_STATE = 0;
    public static final int PREPARED_STATE = 1;
    public static final int RANDER_STATE = 2;
    public static final int PLAYING_STATE = 3;
    public static final int PAUSE_STATE = 4;
    public static final int COMPLETE_STATE = 5;
    public static final int ERROR_STATE = 6;
    private VideoBean currentVideo;
    public VideoPlayer(Context context) {
        super(context);
        initView();
    }

    public VideoPlayer(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initView();
    }

    private void initView() {

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

    public void startVideo(VideoBean videoBean) {
        if (videoBean != null) {
            currentVideo = videoBean;
            initPlayer();
        }
    }

    @Override
    void prepare() {
        try {
            currentState = PREPARE_STATE;
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
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    void prepared() {
        currentState = PREPARED_STATE;
        iMediaPlayer.start();
    }

    @Override
    void render() {
        currentState = RANDER_STATE;
    }

    void start() {
        if (isPause()) {
            iMediaPlayer.start();
            currentState = PLAYING_STATE;
        }
    }

    void pause() {
        if (isPlaying()) {
            iMediaPlayer.pause();
            currentState = PAUSE_STATE;
        }
    }

    boolean isPlaying() {
        return currentState == PLAYING_STATE;
    }

    public boolean isPause() {
        return currentState == PAUSE_STATE;
    }

    @Override
    void release() {
        currentState = -1;
        if (iMediaPlayer != null) {
            if (iMediaPlayer.isPlaying()) {
                iMediaPlayer.stop();
            }
            iMediaPlayer.reset();
            iMediaPlayer.release();
            if (playerSurfaceView != null) {
                SurfaceHolder holder = playerSurfaceView.getHolder();
                if (holder != null) {
                    if (holder.getSurface().isValid()) {
                        holder.getSurface().release();
                    }
                    holder.removeCallback(this);
                }
            }
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
    }

    @Override
    void onError(int what, int extra) {
        currentState = ERROR_STATE;
    }

    @Override
    void onInfo(int what, int extra) {

    }

    @Override
    void onSeekComplete() {

    }

    public int getCurrentState() {
        return currentState;
    }
}
