package c.seven.amateurvideoplayer.control;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import c.seven.amateurvideoplayer.R;
/**
 * Created by j-songsaihua-ol on 2018/3/28.
 */

public class FullScreenUIControl extends HalfScreenUIControl{
    private TextView title;
    private LinearLayout batteryLayout;
    private ImageView batteryImg;
    private TextView batteryTxt;
    private ImageView nextBtn;

    public FullScreenUIControl(Context context, ViewGroup root) {
        super(context, root);
        title = findViewById(R.id.videoplayer_title);
        batteryLayout = findViewById(R.id.videoplayer_battery_layout);
        batteryImg = findViewById(R.id.videoplayer_battery_img);
        batteryTxt = findViewById(R.id.videoplayer_battery_txt);
        nextBtn = findViewById(R.id.videoplayer_next);
        nextBtn.setOnClickListener(this);
    }

    @Override
    int getLayoutId() {
        return R.layout.amateur_full_layout;
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
    public void onClick(View v) {
        if (v == nextBtn) {

        }
    }
}
