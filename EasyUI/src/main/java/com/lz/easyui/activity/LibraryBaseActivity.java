package com.lz.easyui.activity;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;

import com.lz.easyui.event.BaseEvent;
import com.lz.easyui.util.RelayoutViewTool;
import com.lz.easyui.util.SystemBarTintManager;
import com.lz.easyui.widget.LibraryActionBar;

import java.io.Serializable;

import butterknife.ButterKnife;
import de.greenrobot.event.EventBus;
import me.imid.swipebacklayout.lib.SwipeBackLayout;
import me.imid.swipebacklayout.lib.app.SwipeBackActivity;

/**
 *
 */
public abstract class LibraryBaseActivity<ActionBar extends LibraryActionBar> extends SwipeBackActivity implements LibraryActionBar.OnNavigationClickListener {

    private View baseActView;

    private ActionBar actionBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventBus.getDefault().register(this);
        if (getFragment() != null) {
            FragmentManager fm = getSupportFragmentManager();
            if (fm.findFragmentById(android.R.id.content) == null) {
                fm.beginTransaction().add(android.R.id.content, getFragment()).commit();
            }
        }
        actionBar = initActionBar();
        if (actionBar != null) {
            actionBar.setOnNavigationListener(this);
        }
    }

    public final ActionBar getBaseActionBar() {
        return actionBar;
    }

    //如果需要navigation 则需要重写此方法
    public ActionBar initActionBar() {
        return null;
    }

    @Override
    public void onNavigationItemClick(int itemPosition, int itemId) {
        //todo 如果使用navigation 需要重写此方法
    }

    @TargetApi(19)
    private void setTranslucentStatus(boolean on) {
        Window win = getWindow();
        WindowManager.LayoutParams winParams = win.getAttributes();
        final int bits = WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS;
        if (on) {
            winParams.flags |= bits;
        } else {
            winParams.flags &= ~bits;
        }
        win.setAttributes(winParams);
    }

    @Override
    public void setContentView(View view) {
        this.baseActView = view;
        super.setContentView(view);
        ButterKnife.bind(this);
        int edge = getEdgeTrackingEnabled();
        setSwipeBackEnable(edge > -1);
        if (edge > -1) {
            getSwipeBackLayout().setEdgeTrackingEnabled(edge);
        }

        initHeader();
        initWidget();
        setWidgetState();
    }

    @Override
    public void setContentView(int layoutResID) {
        if (getSystemBarColor() > -1) {
            setContentView(layoutResID, getSystemBarColor());
        } else {
            View view = View.inflate(this, layoutResID, null);
            if (isRelayout()) {
                RelayoutViewTool.relayoutViewWithScale(view, getApplicationContext().getResources().getDisplayMetrics().widthPixels);
            }
            this.setContentView(view);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    public void onEvent(BaseEvent event) {

    }

    public void setContentView(int layoutResID, int systemBarColor) {
        View view = View.inflate(this, layoutResID, null);
        if (isRelayout()) {
            RelayoutViewTool.relayoutViewWithScale(view, getApplicationContext().getResources().getDisplayMetrics().widthPixels);
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            LinearLayout layout = new LinearLayout(getApplicationContext());
            layout.addView(view);
            setTranslucentStatus(true);
            SystemBarTintManager tintManager = new SystemBarTintManager(this);
            tintManager.setStatusBarTintEnabled(true);

            tintManager.setStatusBarTintResource(systemBarColor);
            SystemBarTintManager.SystemBarConfig config = tintManager.getConfig();
            layout.setPadding(0, config.getPixelInsetTop(true), 0, config.getPixelInsetBottom());

            this.setContentView(layout);
        } else {
            this.setContentView(view);
        }
    }

    protected View getBaseActView() {
        return baseActView;
    }

    protected void setBackgroundColor(int color) {
        baseActView.setBackgroundColor(color);
    }

    protected void setBackgroundResource(int resid) {
        baseActView.setBackgroundResource(resid);
    }


    protected boolean isRelayout() {
        return false;
    }

    protected LibraryBaseFragment getFragment() {
        return null;
    }

    protected int getEdgeTrackingEnabled() {
        return SwipeBackLayout.EDGE_LEFT;
    }

    protected int getSystemBarColor() {
        return -1;
    }

    protected abstract void initHeader();// 初始化头部

    protected abstract void initWidget();// 初始化控件

    protected abstract void setWidgetState();// 设置控件状态（注册监听or设置设配器）

    protected <T> T getExtra(String key, T value) {
        Object o = null;
        if (value instanceof String) {
            o = this.getIntent().getStringExtra(key);
        } else if (value instanceof Boolean) {
            o = this.getIntent().getBooleanExtra(key, ((Boolean) value).booleanValue());
        } else if (value instanceof Integer) {
            o = this.getIntent().getIntExtra(key, ((Integer) value).intValue());
        } else if (value instanceof Float) {
            o = this.getIntent().getFloatExtra(key, ((Float) value).floatValue());
        } else if (value instanceof Long) {
            o = this.getIntent().getLongExtra(key, ((Long) value).longValue());
        } else if (value instanceof Serializable) {
            o = this.getIntent().getSerializableExtra(key);
        }
        T t = (T) o;
        return t;
    }

    public Activity getTopActivity() {
        Activity top = this;
        while (top.getParent() != null) {
            top = top.getParent();
        }
        return top;
    }

    public void startActivity(Intent it) {
        super.startActivity(it);
    }

    @Override
    public void finish() {
        super.finish();
    }
}
