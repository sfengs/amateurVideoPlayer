package c.seven.amateurvideoplayer.control;

/**
 * Created by j-songsaihua-ol on 2018/4/2.
 */

public interface UIControlListener {
    long getCurrentPosition();
    long getTotal();
    void changeScreen(ScreenModel screenModel);
    void retryPlay();
    void onBack();
    boolean isHasNext();
    void playNext();
    void playClick();
    int getPlayerState();
    void seekBarChange(long position);
}
