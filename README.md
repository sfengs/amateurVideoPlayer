# amateurVideoPlayer
android videoplayer mediaplayer 业余时间写的Android播放器，支持全屏半屏，手势调整声音、亮度、快退快进

使用
===
xml:高度固定
--
```
<c.seven.amateurvideoplayer.view.AmateurVideoPlayer
android:layout_width="match_parent"
android:layout_height="200dp"
android:id="@+id/videoplayer"
/>
```

Activity:
---
在onResume()，onPause()，onBackPressed() 重写方法
```
AmateurVideoPlayer videoPlayer = findViewById(R.id.videoplayer);

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
```
播放单个视频
```
VideoBean videoBean =  new VideoBean();
videoBean.setId(id);
videoBean.setCoverUrl(coverUrl);
videoBean.setPlayUrl(playUrl);
videoBean.setTitle(title);
videoPlayer.startPlayer(videoBean);
```
播放多个视频：全屏状态下有播放下一个的按钮
```
List<VideoBean> list = new ArrayList<VideoBean>();
VideoBean videoBean1 =  new VideoBean();
VideoBean videoBean2 =  new VideoBean();
list.add(videoBean1);
list.add(videoBean2);
videoPlayer.startPlayer(list);
```
AndroidManifest:
---
添加<br>
```
android:configChanges="orientation|screenSize|keyboardHidden"
android:screenOrientation="portrait"
```
联系：
---
```
QQ：178285852
```


