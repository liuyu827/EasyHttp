package com.lz.easyhttp.samples;

import android.app.Activity;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;

import com.liulishuo.filedownloader.BaseDownloadTask;
import com.lz.easyhttp.request.Easy;
import com.lz.easyhttp.request.EasyDownloadListener;
import com.lz.easyhttp.samples.model.HomeModel;

import java.io.File;

public class MainActivity extends Activity {

    private ProgressBar progressBar_1;

    private final String downloadUrl = "http://124.202.164.14/files/2147000007F9967E/dl.wandoujia.com/files/phoenix/latest/wandoujia-wandoujia-web_direct_binded.apk";
    private final String filePath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/.easy-download";
    private final String name = "web_direct_binded.apk";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        progressBar_1 = (ProgressBar) findViewById(R.id.progressBar_1);


        if (!new File(filePath).exists()) {
            new File(filePath).mkdirs();
        }
    }

    public void toRequest(View view) {

        HomeModel.getHome(this, new HomeModel.HomeCallback() {
            @Override
            public void success(HomeModel home) {
                Log.d("=====main====", "home: " + home.toString());
            }
        });
    }


    public void toStart(View view) {




        Easy.load(this, downloadUrl)
                .asDownload(filePath + "/" + name)
                .execute(new EasyDownloadListener() {
                    @Override
                    public void blockComplete(BaseDownloadTask task) {

                    }

                    @Override
                    public void completed(BaseDownloadTask task) {

                    }

                    @Override
                    public void error(BaseDownloadTask task, Throwable e) {

                    }

                    @Override
                    public void warn(BaseDownloadTask task) {

                    }

                    @Override
                    public void pending(BaseDownloadTask task, int soFarBytes, int totalBytes) {

                    }

                    @Override
                    public void progress(BaseDownloadTask task, int soFarBytes, int totalBytes) {

                    }

                    @Override
                    public void paused(BaseDownloadTask task, int soFarBytes, int totalBytes) {

                    }
                });

    }

    public void toPause(View view) {
        Easy.pauseDownload(downloadUrl);

    }

    public void toDelete(View view) {
        new File(filePath + "/" + name).delete();

    }

    /*new EasyDownloadListener() {
                    @Override
                    public void blockComplete(BaseDownloadTask baseDownloadTask) {
                        Log.d("=======blockComplete====", "baseDownloadTask: " + baseDownloadTask.getUrl());

                    }

                    @Override
                    public void completed(BaseDownloadTask baseDownloadTask) {
                        Log.d("=======completed====", "baseDownloadTask: " + baseDownloadTask.getUrl());

                    }

                    @Override
                    public void error(BaseDownloadTask baseDownloadTask, Throwable throwable) {
                        Log.d("=======error====", "baseDownloadTask: " + baseDownloadTask.getUrl() + " throwable: " + throwable.getMessage());

                    }

                    @Override
                    public void warn(BaseDownloadTask baseDownloadTask) {
                        Log.d("=======warn====", "baseDownloadTask: " + baseDownloadTask.getUrl());

                    }

                    @Override
                    public void pending(BaseDownloadTask baseDownloadTask, int i, int i1) {
                        progressBar_1.setMax(i1);
                        Log.d("=======pending====", "baseDownloadTask: " + baseDownloadTask.getUrl() + " i: " + " i1: " + i1);

                    }

                    @Override
                    public void progress(BaseDownloadTask baseDownloadTask, int i, int i1) {
                        progressBar_1.setMax(i1);
                        progressBar_1.setProgress(i);
                        Log.d("=======progress====", "baseDownloadTask: " + baseDownloadTask.getUrl() + " i: " + i + " i1: " + i1);
                    }

                    @Override
                    public void paused(BaseDownloadTask baseDownloadTask, int i, int i1) {
                        Log.d("=======paused====", "baseDownloadTask: " + baseDownloadTask.getUrl() + " i: " + " i1: " + i1);

                    }
                }*/

}
