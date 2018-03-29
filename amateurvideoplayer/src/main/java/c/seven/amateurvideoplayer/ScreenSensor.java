package c.seven.amateurvideoplayer;

import android.content.pm.ActivityInfo;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.os.Build;
import c.seven.amateurvideoplayer.control.ScreenModel;

/**
 * Created by j-songsaihua-ol on 2018/3/29.
 */

public class ScreenSensor implements SensorEventListener {
    private ScreenChangeListener screenChangeListener;
    private ScreenModel currentScreen = ScreenModel.HALF;
    private int portrait,landscape,reverseLandscape;
    private int currentScreentState = -1;

    public ScreenSensor() {
        portrait = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;
        landscape = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE;
        if (Build.VERSION.SDK_INT > 8)
        {
            try
            {
                reverseLandscape = ActivityInfo.class.getDeclaredField(
                        "SCREEN_ORIENTATION_REVERSE_LANDSCAPE").getInt(ActivityInfo.class);
            }
            catch (Exception e)
            {
                reverseLandscape = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE;
            }
        }
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (screenChangeListener == null) {
            return;
        }
        float[] values = event.values;
        float X = values[0];
        float Y = values[1];
        float Z = values[2];
        int rotate = -1;
        if (currentScreen == ScreenModel.HALF) {
            if (Math.abs(X) > Math.abs(Y) && Y / X < Math.atan(Math.PI / 100)
                    && (Math.abs(Y) > Math.abs(Z) || Math.abs(X) > Math.abs(Z))) {
                if (X > 5) {
                    rotate = landscape;
                } else if (X < -5) {
                    rotate = reverseLandscape;
                } else {
                    rotate = reverseLandscape;
                }
                if (rotate < 0 || currentScreentState == rotate) {
                    return;
                }
                currentScreentState = rotate;
                currentScreen = ScreenModel.FULL;
                screenChangeListener.screenChange(currentScreen,currentScreentState);
            } else {
                if (currentScreentState != portrait && currentScreentState != -1) {
                    currentScreentState = portrait;
                    currentScreen = ScreenModel.HALF;
                    screenChangeListener.screenChange(currentScreen,currentScreentState);
                }
            }
        } else if (currentScreen == ScreenModel.FULL) {
            if (Math.abs(X) < Math.abs(Y) && X / Y < Math.atan(Math.PI / 100)
                    && (Math.abs(Y) > Math.abs(Z) || Math.abs(X) > Math.abs(Z))) {
                if (Y > 5) {
                    rotate = portrait;
                }
                if (rotate < 0 || currentScreentState == rotate) {
                    return;
                }

                currentScreentState = rotate;
                currentScreen = ScreenModel.HALF;
                screenChangeListener.screenChange(currentScreen,currentScreentState);
            } else if (Math.abs(X) > Math.abs(Y) && Y / X < Math.atan(Math.PI / 100)
                    && (Math.abs(Y) > Math.abs(Z) || Math.abs(X) > Math.abs(Z))) {
                if (X > 5) {
                    rotate = landscape;
                } else if (X < -5) {
                    rotate = reverseLandscape;
                }
                if (rotate < 0 || currentScreentState == rotate) {
                    return;
                }

                currentScreentState = rotate;
                currentScreen = ScreenModel.FULL;
                screenChangeListener.screenChange(currentScreen,currentScreentState);
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    public void setScreenChangeListener(ScreenChangeListener screenChangeListener) {
        this.screenChangeListener = screenChangeListener;
    }

    public void setCurrentScreen(ScreenModel currentScreen) {
        this.currentScreen = currentScreen;
        if (currentScreen == ScreenModel.FULL) {
            this.currentScreentState = landscape;
        } else if (currentScreen == ScreenModel.HALF) {
            this.currentScreentState = portrait;
        }
    }

    public interface ScreenChangeListener{
        void screenChange(ScreenModel screenModel, int rotate);
    }
}
