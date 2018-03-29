package com.seven.amateurexample;

import android.app.Activity;
import android.os.Bundle;

import c.seven.amateurvideoplayer.VideoBean;
import c.seven.amateurvideoplayer.view.AmateurVideoPlayer;

public class MainActivity extends Activity {
    String playUrl = "http://www.plandogo.com/ProgramDoGo/resources/mm_English/1011.mp4";
    String coverUrl = "http://www.plandogo.com/ProgramDoGo/resources/test_image/76.jpg";
    private AmateurVideoPlayer videoPlayer;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        videoPlayer = findViewById(R.id.videoplayer);
        VideoBean videoBean =  new VideoBean();
        videoBean.setId(System.currentTimeMillis() + "");
        videoBean.setCoverUrl(coverUrl);
        videoBean.setPlayUrl(playUrl);
        videoPlayer.startPlayer(videoBean);
    }

    @Override
    protected void onResume() {
        super.onResume();
        videoPlayer.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        videoPlayer.onPause();
    }

    @Override
    public void onBackPressed() {
        if (videoPlayer.onBackPressed()) {
            return;
        }
        super.onBackPressed();
    }
}
