package c.seven.amateurvideoplayer.view;

import android.app.Activity;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import c.seven.amateurvideoplayer.AmateurUtils;
import c.seven.amateurvideoplayer.R;
import c.seven.amateurvideoplayer.ScreenSensor;
import c.seven.amateurvideoplayer.VideoBean;
import c.seven.amateurvideoplayer.control.ScreenModel;
import c.seven.amateurvideoplayer.control.UIControlViewCallback;

/**
 * Created by j-songsaihua-ol on 2018/3/28.
 */

public class AmateurVideoPlayer extends FrameLayout implements UIControlViewCallback,ScreenSensor.ScreenChangeListener{
    private Context mContext;
    private VideoPlayer mVideoPlayer;
    private GestureView mGestureView;
    private UIControlView uiControlView;
    private ScreenModel currentScreen = ScreenModel.HALF;
    private ScreenSensor screenSensor;
    private int halfWidth,halfheight;
    public AmateurVideoPlayer(@NonNull Context context) {
        super(context);
        initView(context);
    }

    public AmateurVideoPlayer(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }

    private void initView(Context context){
        mContext = context;
        View.inflate(context, R.layout.amateur_video_player,this);
        mVideoPlayer = findViewById(R.id.view_videoplayer);
        mGestureView = findViewById(R.id.view_gestureview);
        uiControlView = findViewById(R.id.view_uicontrol);
        screenSensor = new ScreenSensor();
        screenSensor.setCurrentScreen(currentScreen);
        screenSensor.setScreenChangeListener(this);
        uiControlView.setScreenModel(currentScreen);
    }

    public void startPlayer(VideoBean videoBean) {
        if (videoBean != null) {
            mVideoPlayer.startVideo(videoBean);
        }
    }

    @Override
    public void screenChange(ScreenModel screenModel) {
        int rotate = ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED;
        if (screenModel == ScreenModel.FULL) {
            rotate = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE;
        } else if (screenModel == ScreenModel.HALF) {
            rotate = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;
        }
        if (screenSensor != null) {
            screenSensor.setCurrentScreen(screenModel);
        }
        changeOrientation(screenModel,rotate);
    }

    @Override
    public ScreenModel getCurrentScreen() {
        return currentScreen;
    }

    public void onResume() {
        registScreenSensor();
        mVideoPlayer.start();
    }

    public void onPause() {
        unregistScreenSensor();
        mVideoPlayer.pause();
    }

    private void registScreenSensor() {
        if (screenSensor != null) {
            SensorManager mSensorManager = (SensorManager) mContext.getSystemService(Context.SENSOR_SERVICE);
            Sensor sensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
            mSensorManager.registerListener(screenSensor,sensor,SensorManager.SENSOR_DELAY_NORMAL);
        }
    }

    public void unregistScreenSensor() {
        if (screenSensor != null) {
            SensorManager mSensorManager = (SensorManager) mContext.getSystemService(Context.SENSOR_SERVICE);
            mSensorManager.unregisterListener(screenSensor);
        }
    }

    @Override
    public void screenChange(ScreenModel screenModel, int rotate) {
        int state = mVideoPlayer.getCurrentState();
        if (state >= 1 && state <= 5) {
            changeOrientation(screenModel,rotate);
        }
    }

    private void changeOrientation(ScreenModel screenModel,int rotate) {
        if (mContext instanceof Activity) {
            if (currentScreen == ScreenModel.HALF) {
                if (halfWidth == 0 && halfheight == 0) {
                    halfWidth = getWidth();
                    halfheight = getHeight();
                }
            }
            currentScreen = screenModel;
            ((Activity)mContext).setRequestedOrientation(rotate);
            ViewGroup.LayoutParams lp = getLayoutParams();
            if (currentScreen == ScreenModel.HALF) {
                lp.height = halfheight;
                lp.width = halfWidth;
                if (mVideoPlayer != null) {
                    mVideoPlayer.requestLayout();
                }
            } else if (currentScreen == ScreenModel.FULL) {
                lp.width = ViewGroup.LayoutParams.MATCH_PARENT;
                lp.height = ViewGroup.LayoutParams.MATCH_PARENT;
                if (mVideoPlayer != null) {
                    mVideoPlayer.requestLayout();
                }
            }
            uiControlView.setScreenModel(screenModel);
        }
    }

    public boolean onBackPressed() {
        if (currentScreen == ScreenModel.FULL) {
            screenChange(ScreenModel.HALF);
            return true;
        } else if (currentScreen == ScreenModel.HALF) {
            if (AmateurUtils.isFinishing(mContext)) {
                AmateurUtils.finish(mContext);
            }
        }
        return false;
    }
}
