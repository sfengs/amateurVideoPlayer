package c.seven.amateurvideoplayer.control;

import android.content.Context;
import android.support.annotation.IdRes;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import c.seven.amateurvideoplayer.R;
import c.seven.amateurvideoplayer.view.AmateurVideoPlayer;
import c.seven.amateurvideoplayer.view.PlayerCenterView;
import c.seven.amateurvideoplayer.view.UIControlView;
/**
 * Created by j-songsaihua-ol on 2018/3/28.
 */

public abstract class UIControl {
    private View contentView;
    private ViewGroup root;
    Context mContext;
    public UIControl(Context context, ViewGroup root) {
        mContext = context;
        this.root = root;
        contentView = View.inflate(context,getLayoutId(),root);
    }

    abstract int getLayoutId();

    public <T extends View> T findViewById(@IdRes int id) {
        return contentView.findViewById(id);
    }

    View getContentView() {
        return contentView;
    }

    UIControlView getParentView() {
        if (root instanceof UIControlView) {
            return (UIControlView) root;
        }
        return null;
    }

    public PlayerCenterView getCenterView() {
        if (contentView != null) {
            return findViewById(R.id.videoplayer_center);
        }
        return null;
    }

    abstract void showView();

    abstract void hideView();

    abstract void setProgressAndTime(long position);

    public abstract void showError(View.OnClickListener retryClick);

    public abstract void showVolume(int value);

    public abstract void showBrightness(int value);

    public abstract void showPosition(long position,long duration);
}
