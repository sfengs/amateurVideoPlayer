package c.seven.amateurvideoplayer.control;

/**
 * Created by j-songsaihua-ol on 2018/3/28.
 */

public interface GestureListener {

    void showPosition(long position);

    void showVolume(int value);

    void showBrightness(int value);

    long getCurrentPosition();

    void onSingleTap();

    void onDoubleTap();

    long getDuration();

    int getCurrentVolume();
}
