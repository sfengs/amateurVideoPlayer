package com.seven.amateurexample;

import android.app.Activity;
import android.os.Bundle;

import c.seven.amateurvideoplayer.VideoBean;
import c.seven.amateurvideoplayer.view.AmateurVideoPlayer;

public class MainActivity extends Activity {
    String playUrl = "http://jzvd.nathen.cn/342a5f7ef6124a4a8faf00e738b8bee4/cf6d9db0bd4d41f59d09ea0a81e918fd-5287d2089db37e62345123a1be272f8b.mp4";
//    String playUrl = "http://jzvd.nathen.cn/6340efd1962946ad80eeffd19b3be89c/65b499c0f16e4dd8900497e51ffa0949-5287d2089db37e62345123a1be272f8b.mp4";
//    String playUrl = "http://jzvd.nathen.cn/384d341e000145fb82295bdc54ecef88/103eab5afca34baebc970378dd484942-5287d2089db37e62345123a1be272f8b.mp4";
//    String playUrl = "http://jzvd.nathen.cn/6ea7357bc3fa4658b29b7933ba575008/fbbba953374248eb913cb1408dc61d85-5287d2089db37e62345123a1be272f8b.mp4";
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
