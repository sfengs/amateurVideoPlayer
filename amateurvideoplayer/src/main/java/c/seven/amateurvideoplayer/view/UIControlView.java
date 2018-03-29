package c.seven.amateurvideoplayer.view;

import android.content.Context;
import android.support.v4.util.ArrayMap;
import android.util.AttributeSet;
import android.widget.RelativeLayout;

import c.seven.amateurvideoplayer.control.FullScreenUIControl;
import c.seven.amateurvideoplayer.control.HalfScreenUIControl;
import c.seven.amateurvideoplayer.control.ScreenModel;
import c.seven.amateurvideoplayer.control.UIControl;
import c.seven.amateurvideoplayer.control.UIControlViewCallback;

/**
 * Created by j-songsaihua-ol on 2018/3/28.
 */

public class UIControlView extends RelativeLayout {
    private UIControl uiControl;
    private UIControlViewCallback uiControlViewCallback;
    private ScreenModel currentScreen;
    private ArrayMap<ScreenModel,UIControl> uiControlArrayMap;
    public UIControlView(Context context) {
        super(context);
        initView(context);
    }

    public UIControlView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }
    private void initView(Context context) {
        uiControlArrayMap = new ArrayMap<>(2);
    }

    public void toFullScreen() {
        if (uiControlViewCallback != null) {
            uiControlViewCallback.screenChange(ScreenModel.FULL);
        }
    }

    public void toHalfScreen() {
        if (uiControlViewCallback != null) {
            uiControlViewCallback.screenChange(ScreenModel.HALF);
        }
    }

    public ScreenModel getScreenModel() {
        if (uiControlViewCallback != null) {
            return uiControlViewCallback.getCurrentScreen();
        }
        return ScreenModel.HALF;
    }

    public void setScreenModel(ScreenModel screenModel) {
        if (currentScreen == screenModel) {
            return;
        }
        currentScreen = screenModel;
        uiControl = getUIControlByScreen(screenModel);
    }

    private UIControl getUIControlByScreen(ScreenModel screenModel) {
        this.removeAllViews();
        UIControl uiControl = uiControlArrayMap.get(screenModel);
        if (uiControl == null) {
            if (screenModel == ScreenModel.FULL) {
                uiControl = new FullScreenUIControl(getContext(),this);
            } else {
                uiControl = new HalfScreenUIControl(getContext(),this);
            }
        }
        return uiControl;
    }

}
