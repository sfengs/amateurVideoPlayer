package c.seven.amateurvideoplayer.view;

import android.app.Activity;
import android.content.Context;
import android.media.AudioManager;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.Window;
import android.view.WindowManager;

import c.seven.amateurvideoplayer.control.GestureListener;

/**
 * Created by j-songsaihua-ol on 2018/3/28.
 */

public class GestureView extends View implements GestureDetector.OnGestureListener,GestureDetector.OnDoubleTapListener{
    private GestureDetector gestureDetector;
    private Context mContext;
    private GestureListener gestureListener;
    private boolean isPosition = false;
    private boolean isVolume = false;
    private boolean isBrightness = false;
    private AudioManager audioManager;
    public GestureView(Context context) {
        super(context);
        init(context);
    }

    public GestureView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    private void init(Context context) {
        gestureDetector = new GestureDetector(context,this);
        mContext = context;
        audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
    }

    public void setGestureListener(GestureListener gestureListener) {
        this.gestureListener = gestureListener;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (gestureDetector.onTouchEvent(event)) {
            return true;
        }
        if (event.getAction() == MotionEvent.ACTION_UP ||
                event.getAction() == MotionEvent.ACTION_CANCEL) {
            isPosition = false;
            isVolume = false;
            isBrightness = false;
        }
        return super.onTouchEvent(event);
    }

    @Override
    public boolean onSingleTapConfirmed(MotionEvent e) {
        //单击
        if (gestureListener != null) {
            gestureListener.onSingleTap();
        }
        return true;
    }

    @Override
    public boolean onDoubleTap(MotionEvent e) {
        //双击
        if (gestureListener != null) {
            gestureListener.onDoubleTap();
        }
        return false;
    }

    @Override
    public boolean onDoubleTapEvent(MotionEvent e) {
        return false;
    }

    @Override
    public boolean onDown(MotionEvent e) {
        //手指按下
        return true;
    }

    @Override
    public void onShowPress(MotionEvent e) {

    }

    @Override
    public boolean onSingleTapUp(MotionEvent e) {
        return false;
    }

    @Override
    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
        if (e2.getEventTime() - e1.getDownTime() < ViewConfiguration.getTapTimeout()) {
            return false;
        }
        float absDisX = Math.abs(distanceX);
        float absDisY = Math.abs(distanceY);
        int viewWidth = getWidth();
        int viewHeight = getHeight();
        float disRateX = Math.abs(e2.getX()/viewWidth - e1.getX()/viewWidth);
        float disRateY = Math.abs(e2.getY()/viewHeight - e1.getY()/viewHeight);
        if (absDisX > absDisY && disRateX > 0.1 && !isBrightness && !isVolume) {
            changePosition(distanceX/viewWidth);
        } else if (absDisY > absDisX && disRateY > 0.1) {
            //调整音量和亮度，x位置在两侧
            float rateX1 = e1.getX()/viewWidth;
            float rateX2 = e2.getX()/viewWidth;
            double percentY = distanceY/viewHeight;
            if (rateX1 > 0 && rateX1 < 0.4 && rateX2 > 0 && rateX2 < 0.4 && !isPosition && !isVolume) {
                //左侧 调整亮度
                changeBrightness(percentY);
            } else if (rateX1 > 0.6 && rateX1 < 1 && rateX2 > 0.6 && rateX2 < 1 && !isPosition && isBrightness) {
                //右侧。调整声音
                changeVolume(percentY);
            }
        }
        return true;
    }

    @Override
    public void onLongPress(MotionEvent e) {

    }

    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
        return false;
    }

    private void changePosition(double percent) {
        if (gestureListener != null) {
            isPosition = true;
            long position = (long) (gestureListener.getCurrentPosition() + percent * gestureListener.getDuration());
            if (position > gestureListener.getDuration()) {
                position = gestureListener.getDuration();
            } else if (position < 0) {
                position = 0;
            }
            gestureListener.showPosition(position);
        }
    }

    private void changeVolume(double percent) {
        if (gestureListener != null) {
            isVolume = true;
            int maxVolume = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
            int currentVolume = (int) (gestureListener.getCurrentVolume() + percent * maxVolume);
            if (currentVolume > maxVolume) {
                currentVolume = maxVolume;
            } else if (currentVolume < 0) {
                currentVolume = 0;
            }
            audioManager.setStreamVolume(AudioManager.STREAM_MUSIC,currentVolume,0);
            int volume = (int) (currentVolume / (float)maxVolume * 100);
            gestureListener.showVolume(volume);
        }

    }

    private void changeBrightness(double percent) {
        if (gestureListener != null) {
            isBrightness = true;
            Window window = ((Activity) getContext()).getWindow();
            WindowManager.LayoutParams lps = window.getAttributes();
            float currentBrightness = 0;
            if (lps.screenBrightness < 0) {
                try {
                    currentBrightness = Settings.System.getInt(
                            getContext().getContentResolver(), Settings.System.SCREEN_BRIGHTNESS)/255;
                } catch (Settings.SettingNotFoundException e) {
                    e.printStackTrace();
                    currentBrightness = 0.5f;
                }
            } else {
                currentBrightness = lps.screenBrightness;
            }
            float brightness = (float) (currentBrightness + percent);
            if (brightness > 1.0f) {
                brightness = 1.0f;
            } else if (brightness < 0) {
                brightness = 0.01f;
            }
            lps.screenBrightness = brightness;
            gestureListener.showBrightness((int) (brightness * 100));
        }
    }
}
