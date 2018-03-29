package c.seven.amateurvideoplayer.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.SurfaceView;
import android.view.View;

/**
 * Created by j-songsaihua-ol on 2018/3/23.
 */

public class PlayerSurfaceView extends SurfaceView {
    private int mVideoWidth = 0;
    private int mVideoHight = 0;
    public PlayerSurfaceView(Context context) {
        super(context);
    }

    public PlayerSurfaceView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void setVideoSize(int videoWidth,int videoHight) {
        if (mVideoWidth != videoWidth || mVideoHight != videoHight) {
            mVideoWidth = videoWidth;
            mVideoHight = videoHight;
            requestLayout();
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int videoWidth = MeasureSpec.getSize(widthMeasureSpec);
        int videoHight = MeasureSpec.getSize(heightMeasureSpec);
        int parentHeight = ((View) getParent()).getMeasuredHeight();
        int parentWidth = ((View) getParent()).getMeasuredWidth();
        if (mVideoWidth > 0 && mVideoHight > 0 && parentHeight > 0 && parentWidth > 0) {
            if ((float)mVideoHight/(float)mVideoHight > (float)parentWidth/(float)parentHeight) {
                videoWidth = parentWidth;
                videoHight = parentHeight * videoWidth / parentWidth;
            } else {
                videoHight = parentHeight;
                videoWidth = videoWidth * videoHight / parentHeight;
            }
        }

        setMeasuredDimension(videoWidth,videoHight);
    }
}
