package c.seven.amateurvideoplayer.control;

/**
 * Created by j-songsaihua-ol on 2018/3/28.
 */

public interface UIControlViewCallback {
    void screenChange(ScreenModel screenModel);
    ScreenModel getCurrentScreen();
}
