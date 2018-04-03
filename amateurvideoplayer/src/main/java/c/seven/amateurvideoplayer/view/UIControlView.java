package c.seven.amateurvideoplayer.view;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.BatteryManager;
import android.util.AttributeSet;
import android.util.Log;
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

public class UIControlView extends RelativeLayout implements View.OnClickListener,SeekBar.OnSeekBarChangeListener{
    private Context mContext;
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
    private TextView batteryTxt,currentTime;
    private ImageView play,playNext,fullBtn;
    private TextView startTime,endTime;
    private SeekBar seekBar;
    private ProgressBar bottomProgress;

    private RelativeLayout topLayout,bottomLayout;

    private static Timer sDisMissUITimer;
    private DisMissUITask disMissUITask;
    private static final long DISMISS_UI_DELAY = 3000;

    private UIControlListener uiControlListener;

    private int halfVisibility;

    private BatteryReceiver batteryReceiver;

    private static int sBatteryPct = 50;

    private boolean isSeekBarTouch = false;

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
            cancelDisMissUITask();
            showErrorUI();
            centerView.showErrorView(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (uiControlListener != null) {
                        uiControlListener.retryPlay();
                    }
                }
            });
            if (uiControlListener != null && currentScreen == ScreenModel.FULL) {
                uiControlListener.changeScreen(ScreenModel.HALF);
            }
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
            startDisMissUITask();
        }

        @Override
        public void pauseState() {
            cancelDisMissUITask();
            showView();
            updatePlayUI();
        }

        @Override
        public void renderState() {
            hideCenter();
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
        mContext = context;
        View.inflate(context, R.layout.amateur_ui_layout,this);
        cover = findViewById(R.id.videoplayer_cover);
        back = findViewById(R.id.videoplayer_back);
        share = findViewById(R.id.videoplayer_share);
        title = findViewById(R.id.videoplayer_title);
        centerView = findViewById(R.id.videoplayer_center);
        batteryLayout = findViewById(R.id.videoplayer_battery_layout);
        batteryImg = findViewById(R.id.videoplayer_battery_img);
        batteryTxt = findViewById(R.id.videoplayer_battery_txt);
        currentTime = findViewById(R.id.videoplayer_time);
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
        fullBtn.setOnClickListener(this);
        seekBar.setOnSeekBarChangeListener(this);
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
        systemUIUpdate(screenModel);
        if (currentScreen == screenModel) {
            return;
        }
        currentScreen = screenModel;
        updateUIByScreenChang();
    }


    private void systemUIUpdate(ScreenModel screenModel) {
        if (screenModel == ScreenModel.FULL) {
            halfVisibility = this.getSystemUiVisibility();
            setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY | View.SYSTEM_UI_FLAG_FULLSCREEN);
        } else if (screenModel == ScreenModel.HALF){
            setSystemUiVisibility(halfVisibility);
        }
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
        } else if (v == fullBtn) {
            if (uiControlListener != null) {
                uiControlListener.changeScreen(ScreenModel.FULL);
            }
        }
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        if (isSeekBarTouch) {
            long position = progress * uiControlListener.getTotal()/100;
            showPosition(position,uiControlListener.getTotal());
        }
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
        cancelDisMissUITask();
        isSeekBarTouch = true;
    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        isSeekBarTouch = false;
        startDisMissUITask();
        long seekTo = seekBar.getProgress() * uiControlListener.getTotal()/100;
        uiControlListener.seekBarChange(seekTo);
        centerView.hideCenter();

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


    private class BatteryReceiver extends BroadcastReceiver{

        @Override
        public void onReceive(Context context, Intent intent) {
            if (AmateurUtils.isFinishing(mContext) || isViewDestroy) {
                return;
            }
            int level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
            int scale = intent.getIntExtra(BatteryManager.EXTRA_SCALE, -1);

            int batteryPct = (int) ((level / (float)scale) * 100);
            sBatteryPct = batteryPct;
            updateBatteryAndTime();
            unregistBattery();
        }
    }

    private void registBattery() {
        IntentFilter intentFilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
        if (batteryReceiver == null) {
            batteryReceiver = new BatteryReceiver();
        }
        mContext.registerReceiver(batteryReceiver,intentFilter);

    }

    private void unregistBattery() {
        if (batteryReceiver != null) {
            mContext.unregisterReceiver(batteryReceiver);
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
        registBattery();
    }

    private void showErrorUI() {
        topLayout.setVisibility(VISIBLE);
        back.setVisibility(VISIBLE);
        title.setVisibility(GONE);
        share.setVisibility(GONE);
        batteryLayout.setVisibility(GONE);
        bottomLayout.setVisibility(GONE);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
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
                play.setImageResource(R.drawable.amateur_play);
            }
        }
    }

    private void updateBatteryAndTime() {
        int pct = sBatteryPct;
        if (pct <= 10) {
            batteryImg.setImageResource(R.drawable.amateur_battery_level_10);
        } else if (pct > 10 && pct <= 30) {
            batteryImg.setImageResource(R.drawable.amateur_battery_level_30);
        } else if (pct > 30 && pct <= 50) {
            batteryImg.setImageResource(R.drawable.amateur_battery_level_50);
        } else if (pct > 50 && pct <= 70) {
            batteryImg.setImageResource(R.drawable.amateur_battery_level_70);
        } else if (pct > 70 && pct <= 90) {
            batteryImg.setImageResource(R.drawable.amateur_battery_level_90);
        } else if (pct > 90 && pct <= 100) {
            batteryImg.setImageResource(R.drawable.amateur_battery_level_100);
        }
        batteryTxt.setText(pct+"%");
        currentTime.setText(AmateurUtils.formatCurrentTime(mContext,System.currentTimeMillis()));
    }

    public void hideCenter() {
        centerView.hideCenter();
    }

    public void singleTap() {
        cancelDisMissUITask();
        if (bottomLayout.getVisibility() != VISIBLE) {
            showView();
            if (uiControlListener.getPlayerState() != VideoPlayer.PAUSE_STATE) {
                startDisMissUITask();
            }
        } else {
            hideView();
        }
    }
    public void showVolume(int v) {
        centerView.showVolume(v);
    }

    public void showBrightness(int v) {
        centerView.showBrightness(v);
    }

    public void showPosition(long position,long total) {
        centerView.showPosition(position,total);
        updateSeekBarAndTime(position,total);
    }
}
