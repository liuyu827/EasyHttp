package com.lz.easyhttp.request;//package com.witclass.common.net.loadbuilder;

import android.os.AsyncTask;

import com.lz.easyhttp.db.EasyKvDb;
import com.lz.easyhttp.tools.CheckTool;
import com.lz.easyhttp.tools.ExecutorTool;

/**
 *
 */
public class EasyDbLoading {

    protected interface LibraryDbCallBack {
        public void dbCallBack(String data, boolean isToGetNet, boolean isHaveCache);
    }

    private static EasyDbLoading dbLoading;

    private EasyDbLoading() {
    }

    protected static synchronized EasyDbLoading getInstance() {
        if (dbLoading == null) {
            dbLoading = new EasyDbLoading();
        }
        return dbLoading;
    }

    void getLocalDBData(final EasyBuilder builder, final LibraryDbCallBack libraryDbCallBack) {
        if (builder.async) {
            AsyncTask<Void, Void, String> task = new AsyncTask<Void, Void, String>() {
                boolean isToGetNet = true;

                @Override
                protected String doInBackground(Void... params) {
                    //获取缓存数据
//                String resultDb = EasyKvDb.read(dataKey);
//                if (!CheckTool.isEmpty(resultDb)) {
                    //如果有缓存数据，则获取上次缓存的时间
//                    long lastDate = Long.parseLong(EasyKvDb.read(timeKey));
                    //通过上次缓存的时间，判断当前是否需要请求网络，默认时间是 EXCEED_TIME
//                    isToGetNet = isExceedForNowDateTime(lastDate, exceed_time);
//                }
                    return EasyKvDb.read(builder.toMd5() + "-data");
                }

                @Override
                protected void onPostExecute(String resultDb) {
                    libraryDbCallBack.dbCallBack(resultDb, isToGetNet, !CheckTool.isEmpty(resultDb));
                }
            };
            ExecutorTool.executeTask(task);
        } else {
            String resultDb = EasyKvDb.read(builder.toMd5() + "-data");
            libraryDbCallBack.dbCallBack(resultDb, true, !CheckTool.isEmpty(resultDb));
        }
    }

    /**
     * 存储本地数据
     *
     * @param builder 请求数据
     * @param data    缓存的数据
     */
    void saveLoace(final EasyBuilder builder, String data) {
        EasyKvDb.save(builder.toMd5() + "-data", data);
        EasyKvDb.save(builder.toMd5() + "-time", System.currentTimeMillis() + "");
    }


    /**
     * 判断是否超过当前时间一段距离
     *
     * @param from 要计算的时间
     * @param time 时间的距离
     */
    private boolean isExceedForNowDateTime(long from, long time) {
        long nowTime = System.currentTimeMillis();
        return nowTime - from > time;
    }

}
