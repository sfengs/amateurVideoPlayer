package c.seven.amateurvideoplayer.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.RelativeLayout;

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

    public void showErrorView() {
        this.removeAllViews();

    }
}
