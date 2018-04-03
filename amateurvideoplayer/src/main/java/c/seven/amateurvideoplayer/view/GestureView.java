package c.seven.amateurvideoplayer.view;

import android.app.Activity;
import android.content.Context;
import android.media.AudioManager;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.Window;
import android.view.WindowManager;

import c.seven.amateurvideoplayer.PlayerConfig;
import c.seven.amateurvideoplayer.control.GestureListener;

/**
 * Created by j-songsaihua-ol on 2018/3/28.
 */

public class GestureView extends View implements GestureDetector.OnGestureListener,GestureDetector.OnDoubleTapListener{
    private static final String TAG = "GestureView";
    private GestureDetector gestureDetector;
    private Context mContext;
    private GestureListener gestureListener;
    private boolean isPosition = false;
    private boolean isVolume = false;
    private boolean isBrightness = false;
    private AudioManager audioManager;
    private long currentPosition;
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
            if (isPosition) {
                gestureListener.gestureSeekTo(currentPosition);
            }
            isPosition = false;
            isVolume = false;
            isBrightness = false;
            if (gestureListener != null) {
                postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        gestureListener.gestureUp();
                    }
                },500);
            }
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
        if (gestureListener != null) {
            currentPosition = gestureListener.getCurrentPosition();
            return gestureListener.gestureEnable();
        }
        return false;
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
        if (!gestureListener.gestureEnable()) {
            return false;
        }
        float absDisX = Math.abs(distanceX);
        float absDisY = Math.abs(distanceY);
        int viewWidth = getWidth();
        int viewHeight = getHeight();
        float disRateX = Math.abs(e2.getX()/viewWidth - e1.getX()/viewWidth);
        float disRateY = Math.abs(e2.getY()/viewHeight - e1.getY()/viewHeight);
        if (absDisX > absDisY && disRateX > 0.1 && !isBrightness && !isVolume) {
            float progress = -distanceX/4;
            if (Math.abs(progress) > 5) {
                progress = Math.abs(progress)/progress * 5;
            }
            changePosition(progress);
        } else if (absDisY > absDisX && disRateY > 0.1) {
            //调整音量和亮度，x位置在两侧
            float rateX1 = e1.getX()/viewWidth;
            float rateX2 = e2.getX()/viewWidth;
            float progress = distanceY/2;
            if (Math.abs(progress) > 5) {
                progress = progress > 0 ? 5 : -5;
            }
            if (rateX1 > 0 && rateX1 < 0.4 && rateX2 > 0 && rateX2 < 0.4 && !isPosition && !isVolume) {
                //左侧 调整亮度
                changeBrightness(progress);
            } else if (rateX1 > 0.6 && rateX1 < 1 && rateX2 > 0.6 && rateX2 < 1 && !isPosition && !isBrightness) {
                //右侧。调整声音
                changeVolume(progress);
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

    private void changePosition(float progress) {
        if (gestureListener != null) {
            isPosition = true;
            long position = (long) (currentPosition + progress * 1000);
            if (position > gestureListener.getDuration()) {
                position = gestureListener.getDuration();
            } else if (position < 0) {
                position = 0;
            }
            currentPosition = position;
            gestureListener.showPosition(position);
        }
    }

    private void changeVolume(float progress) {
        if (gestureListener != null) {
            isVolume = true;
            int maxVolume = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
            int currentVolume = (int) ((gestureListener.getCurrentVolume() + progress));
            if (PlayerConfig.isPrintLog) {
                Log.i(TAG,"maxVolume : "+maxVolume+", currentVolume : "+currentVolume+", percent : "+progress);
            }
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

    private void changeBrightness(float progress) {
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
            float brightness = (float) (currentBrightness + progress/100);
            if (PlayerConfig.isPrintLog) {
                Log.i(TAG,"changeBrightness : brightness : "+brightness);
            }
            if (brightness > 1.0f) {
                brightness = 1.0f;
            } else if (brightness < 0) {
                brightness = 0.01f;
            }
            lps.screenBrightness = brightness;
            window.setAttributes(lps);
            gestureListener.showBrightness((int) (brightness * 100));
        }
    }
}
