package c.seven.amateurvideoplayer.view;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.SurfaceHolder;
import android.widget.RelativeLayout;

import c.seven.amateurvideoplayer.PlayerConfig;
import tv.danmaku.ijk.media.player.IMediaPlayer;

/**
 * Created by j-songsaihua-ol on 2018/3/23.
 */

public abstract class BaseVideoPlayer extends RelativeLayout implements IMediaPlayer.OnPreparedListener,IMediaPlayer.OnBufferingUpdateListener
        ,IMediaPlayer.OnCompletionListener,IMediaPlayer.OnErrorListener,IMediaPlayer.OnInfoListener
        ,IMediaPlayer.OnSeekCompleteListener,IMediaPlayer.OnVideoSizeChangedListener,SurfaceHolder.Callback{

    private boolean isSurfaceDestroy = false;
    private static final String TAG = "BaseVideoPlayer";
    public BaseVideoPlayer(Context context) {
        super(context);
        isSurfaceDestroy = false;
    }

    public BaseVideoPlayer(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        isSurfaceDestroy = false;
    }

    abstract void prepare();

    abstract void prepared();

    abstract void render();

    abstract void release();

    abstract void onVideoSize();

    abstract void onBuffering(int percent);

    abstract void onCompletion();

    abstract void onError(int what, int extra);

    abstract void onInfo(int what, int extra);

    abstract void onSeekComplete();

    abstract void resetSurfaceHolder(SurfaceHolder holder);

    @Override
    public void onPrepared(IMediaPlayer iMediaPlayer) {
        prepared();
    }

    @Override
    public void onBufferingUpdate(IMediaPlayer iMediaPlayer, int percent) {
        onBuffering(percent);
    }

    @Override
    public void onCompletion(IMediaPlayer iMediaPlayer) {
        onCompletion();
    }

    @Override
    public boolean onError(IMediaPlayer iMediaPlayer, int what, int extra) {
        onError(what,extra);
        return true;
    }

    @Override
    public boolean onInfo(IMediaPlayer iMediaPlayer, int what, int extra) {
        if (what == IMediaPlayer.MEDIA_INFO_VIDEO_RENDERING_START) {
            render();
        }
        onInfo(what,extra);
        return false;
    }

    @Override
    public void onSeekComplete(IMediaPlayer iMediaPlayer) {
        onSeekComplete();
    }

    @Override
    public void onVideoSizeChanged(IMediaPlayer iMediaPlayer, int i, int i1, int i2, int i3) {
        onVideoSize();
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        if (PlayerConfig.isPrintLog) {
            Log.i(TAG,"surfaceCreated");
        }
        if (isSurfaceDestroy) {
            isSurfaceDestroy = false;
            resetSurfaceHolder(holder);
        } else {
            prepare();
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        if (PlayerConfig.isPrintLog) {
            Log.i(TAG,"surfaceDestroyed");
        }
        holder.getSurface().release();
        isSurfaceDestroy = true;
    }

}
