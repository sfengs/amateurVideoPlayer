package c.seven.amateurvideoplayer.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import c.seven.amateurvideoplayer.AmateurUtils;
import c.seven.amateurvideoplayer.R;

/**
 * Created by j-songsaihua-ol on 2018/3/29.
 */

public class PlayerCenterView extends RelativeLayout {
    private View errorLayout,volumeLayout,brightnessLayout,positionLayout,loadingLayout;
    public PlayerCenterView(Context context) {
        super(context);
        initView(context);
    }

    public PlayerCenterView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }

    private void initView(Context context) {
        View.inflate(getContext(), R.layout.amateur_error_layout,this);
        View.inflate(getContext(),R.layout.amate_volume_layout,this);
        View.inflate(getContext(),R.layout.amateur_brightness_layout,this);
        View.inflate(getContext(),R.layout.amateur_position_layout,this);
        View.inflate(getContext(),R.layout.amateur_loading_layout,this);
        errorLayout = findViewById(R.id.player_error_layout);
        volumeLayout = findViewById(R.id.player_volume_layout);
        brightnessLayout = findViewById(R.id.player_brightness_layout);
        positionLayout = findViewById(R.id.player_position_layout);
        loadingLayout = findViewById(R.id.player_loading_layout);
        hideCenter();
    }

    public void showErrorView(OnClickListener retryClick) {
        volumeLayout.setVisibility(GONE);
        brightnessLayout.setVisibility(GONE);
        positionLayout.setVisibility(GONE);
        loadingLayout.setVisibility(GONE);
        errorLayout.setVisibility(VISIBLE);
        findViewById(R.id.player_retry).setOnClickListener(retryClick);
    }

    public void showVolume(int value) {
        brightnessLayout.setVisibility(GONE);
        positionLayout.setVisibility(GONE);
        errorLayout.setVisibility(GONE);
        loadingLayout.setVisibility(GONE);
        volumeLayout.setVisibility(VISIBLE);
        ProgressBar progressBar = findViewById(R.id.player_volume_progress);
        progressBar.setProgress(value);
    }


    public void showBrightness(int value) {
        positionLayout.setVisibility(GONE);
        errorLayout.setVisibility(GONE);
        volumeLayout.setVisibility(GONE);
        loadingLayout.setVisibility(GONE);
        brightnessLayout.setVisibility(VISIBLE);
        ProgressBar progressBar = findViewById(R.id.player_brightness_progress);
        progressBar.setProgress(value);
    }

    public void showPosition(long position, long duration) {
        errorLayout.setVisibility(GONE);
        volumeLayout.setVisibility(GONE);
        brightnessLayout.setVisibility(GONE);
        loadingLayout.setVisibility(GONE);
        positionLayout.setVisibility(VISIBLE);
        TextView positionTxt = findViewById(R.id.player_position_position);
        TextView durationTxt = findViewById(R.id.player_position_duration);
        positionTxt.setText(AmateurUtils.stringFormatTime(position));
        durationTxt.setText(" / "+AmateurUtils.stringFormatTime(duration));
    }

    public void showLoading() {
        errorLayout.setVisibility(GONE);
        volumeLayout.setVisibility(GONE);
        brightnessLayout.setVisibility(GONE);
        positionLayout.setVisibility(GONE);
        loadingLayout.setVisibility(VISIBLE);
    }

    public void hideCenter() {
        errorLayout.setVisibility(GONE);
        volumeLayout.setVisibility(GONE);
        brightnessLayout.setVisibility(GONE);
        positionLayout.setVisibility(GONE);
        loadingLayout.setVisibility(GONE);
    }
}
