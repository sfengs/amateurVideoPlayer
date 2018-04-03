package c.seven.amateurvideoplayer.view;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.SurfaceView;
import android.view.View;

import c.seven.amateurvideoplayer.PlayerConfig;

/**
 * Created by j-songsaihua-ol on 2018/3/23.
 */

public class PlayerSurfaceView extends SurfaceView {
    private static final String TAG = "PlayerSurfaceView";
    private int mVideoWidth = 0;
    private int mVideoHeight = 0;
    public PlayerSurfaceView(Context context) {
        super(context);
    }

    public PlayerSurfaceView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void setVideoSize(int videoWidth,int videoHight) {
        if (mVideoWidth != videoWidth || mVideoHeight != videoHight) {
            mVideoWidth = videoWidth;
            mVideoHeight = videoHight;
            requestLayout();
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int videoWidth = MeasureSpec.getSize(widthMeasureSpec);
        int videoHeight = MeasureSpec.getSize(heightMeasureSpec);
        int parentHeight = ((View) getParent()).getMeasuredHeight();
        int parentWidth = ((View) getParent()).getMeasuredWidth();
        if (mVideoWidth > 0 && mVideoHeight > 0 && parentHeight > 0 && parentWidth > 0) {
            if (PlayerConfig.isPrintLog) {
                Log.i(TAG,"mVideoWidth : "+mVideoWidth+", mVideoHeight : "+mVideoHeight);
            }
            float videoRate = (float)mVideoWidth/(float)mVideoHeight;
            float parentRate = (float)parentWidth/(float)parentHeight;
            if (videoRate < 1) {
                //高大于宽，视频是竖屏的
                if (mVideoHeight > parentHeight) {
                    int h = parentHeight;//高度缩小
                    float w = mVideoWidth * ((float)parentHeight/mVideoHeight);//宽度缩小相应比例
                    if (w > parentWidth) {
                        //在宽度已经缩小的情况下，还是比屏幕宽度大
                        //所以高度在上次的基础上再缩小（宽度将要缩小的）比例
                        h = (int) (h * parentWidth/w);
                        //所以屏幕宽度再缩小
                        w = parentWidth;
                    }
                    videoHeight = h;
                    videoWidth = (int) w;
                } else {
                    videoHeight = mVideoHeight;
                    videoWidth = mVideoWidth;
                }
            } else if (videoRate > 1) {
                //宽大于高，视频是横屏视频
                if (mVideoWidth <= parentWidth) {
                    videoWidth = parentWidth;
                    videoHeight = parentHeight;
                } else {
                    //比屏幕还大的视频(屏幕的宽比高大一点，差不多正方形)
                    //也就是说，当视频的宽缩小到和屏幕的宽一样，但是视频的高比屏幕的高还高
                    //比如，视频是16:15，而屏幕是16::9
                    int w = parentWidth;
                    float h = mVideoHeight * ((float)parentWidth/mVideoWidth);
                    if (h > parentHeight) {
                        //宽度在上次的基础上再缩小‘高度将要缩小的比例’
                        w = (int) (w * parentHeight/h);
                        //高度再缩小
                        h = parentHeight;
                    }
                    videoWidth = w;
                    videoHeight = (int) h;
                }
            } else {
                //宽高一样的视频
                if (parentRate > 1) {
                    //父类view是宽大于高，为了不让视频超出屏幕，和高度比较
                    if (mVideoHeight <= parentHeight) {
                        //视频小于屏幕
                        videoWidth = mVideoWidth;
                        videoHeight = mVideoHeight;
                    } else {
                        //视频大于屏幕
                        videoHeight = parentHeight;
                        videoWidth = (int) (mVideoWidth * ((float)parentHeight/mVideoHeight));
                    }
                } else if (parentRate < 1) {
                    //父类view是宽小于高,为了不让视频超出屏幕，和宽度比较
                    if (mVideoWidth <= parentWidth) {
                        //视频的小于屏
                        videoWidth = mVideoWidth;
                        videoHeight = mVideoHeight;
                    } else {
                        //视频大于屏幕
                        videoWidth = parentWidth;
                        videoHeight = (int) (mVideoHeight * ((float)parentWidth/mVideoHeight));
                    }
                } else {
                    //父类view宽高一样
                    videoHeight = mVideoHeight;
                    videoWidth = mVideoWidth;
                }
            }
            if (PlayerConfig.isPrintLog) {
                Log.i(TAG,"videoHeight : "+videoHeight+", videoWidth : "+videoWidth);
            }
        }
        setMeasuredDimension(videoWidth,videoHeight);
    }
}
