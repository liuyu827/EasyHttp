package com.lz.easyhttp.ui;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Handler;
import android.os.Looper;
import android.view.KeyEvent;
import android.view.View;
import android.widget.TextView;

import com.lz.easyhttp.R;
import com.lz.easyhttp.tools.CheckTool;

import okhttp3.Call;

/**
 * 转轮
 */
public class EasyProgressBar {

    private Handler maniHandler = new Handler(Looper.getMainLooper());

    private static EasyProgressBar easyProgressBar;

    private EasyProgressBar() {
    }

    public static synchronized EasyProgressBar getInstance() {
        if (easyProgressBar == null) {
            easyProgressBar = new EasyProgressBar();
        }
        return easyProgressBar;
    }


    private Dialog progressBarDialog;

    private Call call;

    public synchronized void setCall(Call call) {
        this.call = call;
    }

    /**
     * 启动加载进度条
     *
     * @param message   提示文字int 不接收String
     * @param canCancel 是否可关闭进度条状态
     * @param canFinish 是否可关闭当前Activity
     */
    public synchronized void startProgressBar(final Activity act, String message, final boolean canCancel, final boolean canFinish) {
        startProgressBar(act, message, canCancel, canFinish, true);
    }

    /**
     * 启动加载进度条
     *
     * @param message   提示文字int 不接收String
     * @param canCancel 是否可关闭进度条状态
     * @param canFinish 是否可关闭当前Activity
     * @param relayout  是否使用适配
     */
    public synchronized void startProgressBar(final Activity act, final String message, final boolean canCancel, final boolean canFinish, boolean relayout) {
        call = null;
        if (progressBarDialog != null && progressBarDialog.isShowing())
            return;

        maniHandler.post(new Runnable() {
            @Override
            public void run() {
                View view = View.inflate(act, R.layout.easy_dialog_progressbar, null);
                progressBarDialog = EasyDialog.getInstance().buildDialog(act, view, false);
                TextView titleTest = (TextView) view.findViewById(R.id.easy_dialog_loading_txt);
                if (CheckTool.isEmpty(message)) {
                    titleTest.setVisibility(View.GONE);
                } else {
                    titleTest.setVisibility(View.VISIBLE);
                    titleTest.setText(message);
                }

                progressBarDialog.setOnKeyListener(new DialogInterface.OnKeyListener() {
                    @Override
                    public boolean onKey(DialogInterface dialogInterface, int i, KeyEvent keyEvent) {
                        if (i == KeyEvent.KEYCODE_BACK && (canCancel || canFinish)) {
                            if (call != null) {
                                call.cancel();
                            }
                            if (canFinish) {
                                act.finish();
                            }
                            closeProgressBar();
                            return true;
                        }
                        return false;
                    }
                });
            }
        });
    }


    // 进度条 关
    public synchronized boolean closeProgressBar() {
        if (progressBarDialog != null && progressBarDialog.isShowing()) {
            maniHandler.post(new Runnable() {
                @Override
                public void run() {
                    progressBarDialog.dismiss();
                }
            });
            return true;
        }
        return false;
    }
}
