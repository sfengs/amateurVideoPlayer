package c.seven.amateurvideoplayer.view;

import android.content.Context;
import android.support.v4.util.ArrayMap;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import java.lang.ref.WeakReference;
import java.util.Timer;
import java.util.TimerTask;

import c.seven.amateurvideoplayer.AmateurUtils;
import c.seven.amateurvideoplayer.R;
import c.seven.amateurvideoplayer.VideoBean;
import c.seven.amateurvideoplayer.control.MediaStateListener;
import c.seven.amateurvideoplayer.control.ScreenModel;
import c.seven.amateurvideoplayer.control.UIControlListener;

/**
 * Created by j-songsaihua-ol on 2018/3/28.
 */

public class UIControlView extends RelativeLayout implements View.OnClickListener{
    private static Timer sUpdateSeekBarTimer;
    private UpdateSeekBarTask updateSeekBarTimerTask;
    private static final long UPDATE_SEEK_PERIOD = 300;
    private ScreenModel currentScreen;
    private boolean isViewDestroy = false;

    private ImageView cover,back,share;
    private TextView title;
    private PlayerCenterView centerView;
    private LinearLayout batteryLayout;
    private ImageView batteryImg;
    private TextView batteryTxt;
    private ImageView play,playNext,fullBtn;
    private TextView startTime,endTime;
    private SeekBar seekBar;
    private ProgressBar bottomProgress;

    private RelativeLayout topLayout,bottomLayout;

    private static Timer sDisMissUITimer;
    private DisMissUITask disMissUITask;
    private static final long DISMISS_UI_DELAY = 3000;

    private UIControlListener uiControlListener;

    private MediaStateListener mediaStateListener = new MediaStateListener() {
        @Override
        public void prepareState() {
            hideView();
            centerView.showLoading();
        }

        @Override
        public void preparedState() {
            cover.setVisibility(GONE);
            startUpdateSeekBarTask();
        }

        @Override
        public void errorState() {
            cancelUpdateSeekBarTask();
            hideView();
            centerView.showErrorView(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (uiControlListener != null) {
                        uiControlListener.retryPlay();
                    }
                }
            });
        }

        @Override
        public void completeState() {
            seekBar.setProgress(100);
            bottomProgress.setProgress(100);
            cancelUpdateSeekBarTask();
            cancelDisMissUITask();
            showView();
        }

        @Override
        public void playingState() {
            updatePlayUI();
        }

        @Override
        public void pauseState() {
            showView();
            updatePlayUI();
        }

        @Override
        public void renderState() {
            centerView.removeAllViews();
            showView();
            startDisMissUITask();
        }
    };
    public UIControlView(Context context) {
        super(context);
        initView(context);
    }

    public UIControlView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }
    private void initView(Context context) {
        View.inflate(context, R.layout.amateur_full_layout,this);
        cover = findViewById(R.id.videoplayer_cover);
        back = findViewById(R.id.videoplayer_back);
        share = findViewById(R.id.videoplayer_share);
        title = findViewById(R.id.videoplayer_title);
        centerView = findViewById(R.id.videoplayer_center);
        batteryLayout = findViewById(R.id.videoplayer_battery_layout);
        batteryImg = findViewById(R.id.videoplayer_battery_img);
        batteryTxt = findViewById(R.id.videoplayer_battery_txt);
        play = findViewById(R.id.videoplayer_bottom_play);
        playNext = findViewById(R.id.videoplayer_next);
        fullBtn = findViewById(R.id.videoplayer_fullbtn);
        startTime = findViewById(R.id.videoplayer_start_time);
        endTime = findViewById(R.id.videoplayer_end_time);
        seekBar = findViewById(R.id.videoplayer_seekbar);
        bottomProgress = findViewById(R.id.videoplayer_bottom_progress);

        topLayout = findViewById(R.id.videoplayer_top_layout);
        bottomLayout = findViewById(R.id.videoplayer_bottom);

        back.setOnClickListener(this);
        share.setOnClickListener(this);
        play.setOnClickListener(this);
        playNext.setOnClickListener(this);
    }

    public void setUiControlListener(UIControlListener uiControlListener) {
        this.uiControlListener = uiControlListener;
    }

    public void loadData(VideoBean videoBean) {
        if (!AmateurUtils.isFinishing(getContext())) {
            Glide.with(getContext()).load(videoBean.getCoverUrl())
                    .apply(new RequestOptions().centerCrop()).into(cover);
            title.setText(videoBean.getTitle());
        }
    }

    public void setScreenModel(ScreenModel screenModel) {
        if (currentScreen == screenModel) {
            return;
        }
        currentScreen = screenModel;
        updateUIByScreenChang();
    }


    public MediaStateListener getMediaStateListener() {
        return mediaStateListener;
    }

    public void startUpdateSeekBarTask() {
        cancelUpdateSeekBarTask();
        sUpdateSeekBarTimer = new Timer();
        updateSeekBarTimerTask = new UpdateSeekBarTask(getContext());
        sUpdateSeekBarTimer.schedule(updateSeekBarTimerTask,0,UPDATE_SEEK_PERIOD);
    }


    public void cancelUpdateSeekBarTask() {
        if (sUpdateSeekBarTimer != null) {
            sUpdateSeekBarTimer.cancel();
            sUpdateSeekBarTimer = null;
        }
        if (updateSeekBarTimerTask != null) {
            updateSeekBarTimerTask.cancel();
            updateSeekBarTimerTask = null;
        }
    }

    public void startDisMissUITask() {
        cancelDisMissUITask();
        sDisMissUITimer = new Timer();
        disMissUITask = new DisMissUITask(getContext());
        sDisMissUITimer.schedule(disMissUITask,DISMISS_UI_DELAY);
    }

    public void cancelDisMissUITask() {
        if (sDisMissUITimer != null) {
            sDisMissUITimer.cancel();
            sDisMissUITimer = null;
        }
        if (disMissUITask != null) {
            disMissUITask.cancel();
            disMissUITask = null;
        }
    }

    @Override
    public void onClick(View v) {
        if (v == back) {
            if (uiControlListener != null) {
                uiControlListener.onBack();
            }
        } else if (v == play) {
            if (uiControlListener != null) {
                uiControlListener.playClick();
                updatePlayUI();
            }
        } else if (v == playNext) {
            if (uiControlListener != null) {
                uiControlListener.playNext();
            }
        }
    }

    private class UpdateSeekBarTask extends TimerTask{
        private WeakReference<Context> context;
        public UpdateSeekBarTask(Context context) {
            this.context = new WeakReference<Context>(context);
        }

        @Override
        public void run() {
            post(new Runnable() {
                @Override
                public void run() {
                    if (!AmateurUtils.isFinishing(context.get()) && uiControlListener != null && !isViewDestroy) {
                        updateSeekBarAndTime(uiControlListener.getCurrentPosition(),uiControlListener.getTotal());
                    }
                }
            });
        }
    }

    private class DisMissUITask extends TimerTask{
        private WeakReference<Context> context;
        public DisMissUITask(Context context) {
            this.context = new WeakReference<Context>(context);
        }
        @Override
        public void run() {
            post(new Runnable() {
                @Override
                public void run() {
                    if (!AmateurUtils.isFinishing(context.get()) && !isViewDestroy) {
                        hideView();
                    }
                }
            });
        }
    }

    private void updateSeekBarAndTime(long currentPosition,long duration) {
        if (duration <= 0) {
            return;
        }
        int progress = (int) ((double)currentPosition/duration * 100);
        seekBar.setProgress(progress);
        bottomProgress.setProgress(progress);
        startTime.setText(AmateurUtils.stringFormatTime(currentPosition));
        endTime.setText(AmateurUtils.stringFormatTime(duration));
    }

    private void updateUIByScreenChang() {
        if (currentScreen == ScreenModel.HALF) {
            title.setVisibility(GONE);
            batteryLayout.setVisibility(GONE);
            playNext.setVisibility(GONE);
            fullBtn.setVisibility(VISIBLE);
        } else if (currentScreen == ScreenModel.FULL) {
            title.setVisibility(VISIBLE);
            batteryLayout.setVisibility(VISIBLE);
            playNext.setVisibility(VISIBLE);
            fullBtn.setVisibility(GONE);
        }
    }

    private void hideView() {
        topLayout.setVisibility(GONE);
        bottomLayout.setVisibility(GONE);
    }

    private void showView() {
        topLayout.setVisibility(VISIBLE);
        bottomLayout.setVisibility(VISIBLE);
    }

    @Override
    protected void onDetachedFromWindow() {
        isViewDestroy = true;
        cancelUpdateSeekBarTask();
        cancelDisMissUITask();
        super.onDetachedFromWindow();
    }

    private void updatePlayUI() {
        if (uiControlListener != null) {
            int state = uiControlListener.getPlayerState();
            if (state == VideoPlayer.PAUSE_STATE) {
                play.setImageResource(R.drawable.amateur_pause);
            } else {
                play.setImageResource(R.drawable.amateur_pause);
            }
        }
    }
}
