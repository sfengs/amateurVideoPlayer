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

import java.util.List;

import c.seven.amateurvideoplayer.AmateurUtils;
import c.seven.amateurvideoplayer.R;
import c.seven.amateurvideoplayer.ScreenSensor;
import c.seven.amateurvideoplayer.VideoBean;
import c.seven.amateurvideoplayer.control.ScreenModel;
import c.seven.amateurvideoplayer.control.UIControlListener;

/**
 * Created by j-songsaihua-ol on 2018/3/28.
 */

public class AmateurVideoPlayer extends FrameLayout implements UIControlListener,
        ScreenSensor.ScreenChangeListener{
    private Context mContext;
    private VideoPlayer mVideoPlayer;
    private GestureView mGestureView;
    private UIControlView uiControlView;
    private ScreenModel currentScreen = ScreenModel.HALF;
    private ScreenSensor screenSensor;
    private int halfWidth,halfheight;
    private List<VideoBean> videos;
    private int playIndex = 0;
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
        uiControlView.setUiControlListener(this);
        mVideoPlayer.setMediaStateListener(uiControlView.getMediaStateListener());
    }

    public void startPlayer(VideoBean videoBean) {
        if (videoBean != null) {
            uiControlView.loadData(videoBean);
            mVideoPlayer.startVideo(videoBean);
        }
    }

    public void startPlayer(List<VideoBean> list) {
        if (list != null && list.size() > 0) {
            videos = list;
            startPlayer(list.get(0));
        }
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
            changeScreen(ScreenModel.HALF);
            return true;
        } else if (currentScreen == ScreenModel.HALF) {
            if (!AmateurUtils.isFinishing(mContext)) {
                if (uiControlView != null) {
                    uiControlView.cancelUpdateSeekBarTask();
                }
                if (mVideoPlayer != null) {
                    mVideoPlayer.release();
                }
                AmateurUtils.finish(mContext);
            }
        }
        return false;
    }
    @Override
    public long getCurrentPosition() {
        if (mVideoPlayer != null) {
            return mVideoPlayer.getCurrentPosition();
        }
        return 0;
    }

    @Override
    public long getTotal() {
        if (mVideoPlayer != null) {
            return mVideoPlayer.getDuration();
        }
        return 0;
    }

    @Override
    public void changeScreen(ScreenModel screenModel) {
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
    public void retryPlay() {
        if (mVideoPlayer != null) {
            mVideoPlayer.retryPlay();
        }
    }

    @Override
    public void onBack() {
        onBackPressed();
    }

    @Override
    public boolean isHasNext() {
        if (videos != null && videos.size() > 0 && playIndex < videos.size() -1) {
            return true;
        }
        return false;
    }

    @Override
    public void playNext() {
        if (isHasNext()) {
            playIndex++;
//            startPlayer(videos.get(playIndex));
        }
    }

    @Override
    public void playClick() {
        if (mVideoPlayer != null) {
            if (mVideoPlayer.isPlaying()) {
                mVideoPlayer.pause();
            } else if (mVideoPlayer.isPause()) {
                mVideoPlayer.start();
            }
        }
    }

    @Override
    public int getPlayerState() {
        if (mVideoPlayer != null) {
            return mVideoPlayer.getCurrentState();
        }
        return -1;
    }
}
