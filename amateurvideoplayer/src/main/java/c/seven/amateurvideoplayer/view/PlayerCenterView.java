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

    public PlayerCenterView(Context context) {
        super(context);
    }

    public PlayerCenterView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void showErrorView(OnClickListener retryClick) {
        this.removeAllViews();
        View.inflate(getContext(), R.layout.amateur_error_layout,this);
        findViewById(R.id.player_retry).setOnClickListener(retryClick);
    }

    public void showVolume(int value) {
        removeAllViews();
        View.inflate(getContext(),R.layout.amate_volume_layout,this);
        ProgressBar progressBar = findViewById(R.id.player_volume_progress);
        progressBar.setProgress(value);
    }


    public void showBrightness(int value) {
        removeAllViews();
        View.inflate(getContext(),R.layout.amateur_brightness_layout,this);
        ProgressBar progressBar = findViewById(R.id.player_brightness_progress);
        progressBar.setProgress(value);
    }

    public void showPosition(long position, long duration) {
        removeAllViews();
        View.inflate(getContext(),R.layout.amateur_position_layout,this);
        TextView positionTxt = findViewById(R.id.player_position_position);
        TextView durationTxt = findViewById(R.id.player_position_duration);
        positionTxt.setText(AmateurUtils.stringFormatTime(position));
        durationTxt.setText(" / "+AmateurUtils.stringFormatTime(duration));
    }
}
