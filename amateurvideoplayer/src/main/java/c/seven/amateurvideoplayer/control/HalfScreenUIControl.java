package c.seven.amateurvideoplayer.control;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import c.seven.amateurvideoplayer.AmateurUtils;
import c.seven.amateurvideoplayer.R;
import c.seven.amateurvideoplayer.view.PlayerCenterView;


/**
 * Created by j-songsaihua-ol on 2018/3/28.
 */

public class HalfScreenUIControl extends UIControl implements View.OnClickListener{
    private ImageView fullBtn;
    ImageView back;
    ImageView share;
    ImageView play;
    SeekBar seekBar;
    TextView startTime,endTime;
    PlayerCenterView centerView;

    public HalfScreenUIControl(Context context, ViewGroup root) {
        super(context, root);
        back = findViewById(R.id.videoplayer_back);
        share = findViewById(R.id.videoplayer_share);
        play = findViewById(R.id.videoplayer_bottom_play);
        seekBar = findViewById(R.id.videoplayer_seekbar);
        startTime = findViewById(R.id.videoplayer_start_time);
        endTime = findViewById(R.id.videoplayer_end_time);
        fullBtn = findViewById(R.id.videoplayer_full);
        centerView = findViewById(R.id.videoplayer_center);

        back.setOnClickListener(this);
        share.setOnClickListener(this);
        play.setOnClickListener(this);
        if (fullBtn != null) {
            fullBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (getParentView() != null) {
                        getParentView().toFullScreen();
                    }
                }
            });
        }
    }

    @Override
    int getLayoutId() {
        return R.layout.amateur_half_layout;
    }

    @Override
    void showView() {

    }

    @Override
    void hideView() {

    }

    @Override
    void setProgressAndTime(long position) {

    }

    @Override
    public void showError(View.OnClickListener retryClick) {
        centerView.showErrorView(retryClick);
    }

    @Override
    public void showVolume(int value) {
        centerView.showVolume(value);
    }

    @Override
    public void showBrightness(int value) {
        centerView.showBrightness(value);
    }

    @Override
    public void showPosition(long position, long duration) {
        centerView.showPosition(position,duration);
    }

    @Override
    public void onClick(View v) {
        if (v == back) {
            if (getParentView() != null) {
                if (getParentView().getScreenModel() == ScreenModel.HALF) {
                    if (!AmateurUtils.isFinishing(mContext)) {
                        ((Activity)mContext).finish();
                    }
                } else {
                    getParentView().toHalfScreen();
                }
            }
        } else if (v == share) {

        } else if (v == play) {

        }
    }
}
