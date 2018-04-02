package c.seven.amateurvideoplayer.control;

/**
 * Created by j-songsaihua-ol on 2018/3/30.
 */

public interface MediaStateListener {
    void prepareState();

    void preparedState();

    void errorState();

    void completeState();

    void playingState();

    void pauseState();

    void renderState();//播放的第一帧
}
