package com.lz.easyhttp.request;

import android.app.Activity;
import android.os.Handler;
import android.os.Looper;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.lz.easyhttp.cookie.ClearableCookieJar;
import com.lz.easyhttp.cookie.PersistentCookieJar;
import com.lz.easyhttp.cookie.cache.SetCookieCache;
import com.lz.easyhttp.cookie.persistence.SharedPrefsCookiePersistor;
import com.lz.easyhttp.tools.EasyLog;
import com.lz.easyhttp.tools.NetWorkTool;
import com.lz.easyhttp.ui.EasyProgressBar;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Headers;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * 文件上传
 */
public class EasyUpload {

    private final EasyBuilder builder;

    private final Handler maniHandler = new Handler(Looper.getMainLooper());

    private final Gson gson = new Gson();

    private final JsonParser jsonParser = new JsonParser();

    private final OkHttpClient client;


    private Set<String> uploadSet = new HashSet<>();

    public EasyUpload(EasyBuilder builder) {
        this.builder = builder;
        ClearableCookieJar cookieJar = new PersistentCookieJar(new SetCookieCache(), new SharedPrefsCookiePersistor(builder.context.getApplicationContext()));
        this.client = new OkHttpClient.Builder().connectTimeout(builder.timeOut, TimeUnit.MILLISECONDS).cookieJar(cookieJar).build();
    }

    private static Hashtable<String, String> mContentTypes = new Hashtable<String, String>();

    {
        mContentTypes.put("js", "application/javascript");
        mContentTypes.put("json", "application/json");
        mContentTypes.put("png", "image/png");
        mContentTypes.put("jpg", "image/jpeg");
        mContentTypes.put("jpeg", "image/jpeg");
        mContentTypes.put("html", "text/html");
        mContentTypes.put("css", "text/css");
        mContentTypes.put("mp4", "video/mp4");
        mContentTypes.put("mov", "video/quicktime");
        mContentTypes.put("wmv", "video/x-ms-wmv");
    }

    private String getContentType(String path) {
        String type = tryGetContentType(path);
        if (type != null)
            return type;
        return "text/plain";
    }

    private String tryGetContentType(String path) {
        int index = path.lastIndexOf(".");
        if (index != -1) {
            String e = path.substring(index + 1);
            String ct = mContentTypes.get(e);
            if (ct != null)
                return ct;
        }
        return null;
    }

    public <T> void executeAsync(String name, final EasyUploadListener<T> uploadListener) {
        this.builder.async = true;

        if (builder.isShowBar && builder.context instanceof Activity) {
            EasyProgressBar.getInstance().startProgressBar((Activity) builder.context, builder.barMessage, builder.canCancel, builder.canFinish);
        }

        for (File file : builder.uploadFiles) {
            uploadSet.add(file.getPath());
            upload(name, file, uploadListener);
        }
    }

    public <T> void executeSync(String name, final EasyUploadListener<T> uploadListener) {
        this.builder.async = false;

        if (builder.isShowBar && builder.context instanceof Activity) {
            EasyProgressBar.getInstance().startProgressBar((Activity) builder.context, builder.barMessage, builder.canCancel, builder.canFinish);
        }

        for (File file : builder.uploadFiles) {
            uploadSet.add(file.getPath());
            upload(name, file, uploadListener);
        }
    }

    private <T> void upload(String name, final File file, final EasyUploadListener<T> uploadListener) {
        EasyLog.i("EasyUpload", "upload: " + this.builder.toString() + "\nfile: " + file.getPath());
        //判断网络是否正常
        if (!NetWorkTool.networkCanUse(builder.context.getApplicationContext())) {
            uploadListener.netError();
            removeUpload(file);
            return;
        }

        MultipartBody.Builder builder = new MultipartBody.Builder();
        builder.setType(MultipartBody.FORM);

        builder.addPart(Headers.of("Content-Disposition", "form-data; name=\"" + name + "\"; filename=\"" + file.getName() + "\""), RequestBody.create(MediaType.parse(getContentType(file.getPath())), file));

        MultipartBody body = builder.build();

        Request.Builder requestBuilder = new Request.Builder();

        final Request request = requestBuilder.url(this.builder.requestUrl).method("POST", body).build();
        Call call = client.newCall(request);
        EasyProgressBar.getInstance().addCall(call);
        if (this.builder.async) {
            call.enqueue(new Callback() {
                @Override
                public void onFailure(Call call, final IOException e) {
                    EasyLog.e("EasyUpload", "upload error", e);
                    maniHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            if (uploadListener != null) {
                                uploadListener.error(file, e, -1, "上传失败", null, null);
                            }
                            removeUpload(file);
                        }
                    });
                }

                @Override
                public void onResponse(Call call, final Response response) throws IOException {
                    final String responseBody = response.body().string();
                    EasyLog.i("EasyUpload", "async upload success: " + responseBody);
                    if (!response.isSuccessful()) {
                        maniHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                if (uploadListener != null) {
                                    uploadListener.error(file, null, response.code(), response.message(), responseBody, response.headers().toMultimap());
                                }
                                removeUpload(file);
                            }
                        });
                        return;
                    }
                    requestCallBack(responseBody, file, response.headers().toMultimap(), true, uploadListener);
                }
            });
        } else {
            try {
                Response response = call.execute();
                final String responseBody = response.body().string();
                EasyLog.i("EasyUpload", "sync upload success: " + responseBody);
                if (response.isSuccessful()) {
                    requestCallBack(responseBody, file, response.headers().toMultimap(), false, uploadListener);
                } else {
                    if (uploadListener != null) {
                        uploadListener.error(file, null, response.code(), response.message(), responseBody, null);
                    }
                    removeUpload(file);
                }
            } catch (IOException e) {
                if (uploadListener != null) {
                    uploadListener.error(file, e, -1, "获取数据失败", null, null);
                }
                removeUpload(file);
            }
        }
    }

    private <T> void requestCallBack(final String body, final File file, final Map<String, List<String>> multimap, boolean async, final EasyUploadListener<T> uploadListener) {
        if (uploadListener == null) {
            removeUpload(file);
            return;
        }
        T t = null;
        Type type = uploadListener.getClass().getGenericSuperclass();
        if (type instanceof ParameterizedType) {
            ParameterizedType parameterizedType = (ParameterizedType) type;
            Type argument = parameterizedType.getActualTypeArguments()[0];

            try {
                if (argument == JSONObject.class) {
                    t = (T) new JSONObject(body);
                } else if (argument == JSONArray.class) {
                    t = (T) new JSONArray(body);
                } else if (argument == JsonObject.class) {
                    t = (T) jsonParser.parse(body).getAsJsonObject();
                } else if (argument == JsonArray.class) {
                    t = (T) jsonParser.parse(body).getAsJsonArray();
                } else if (argument == String.class) {
                    t = (T) body;
                } else {
                    t = gson.fromJson(body, argument);
                }
            } catch (Throwable e) {
                EasyProgressBar.getInstance().closeProgressBar();
                e.printStackTrace();
                maniHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        uploadListener.error(file, new RuntimeException("解析数据失败"), -3, "解析数据失败", body, multimap);
                        removeUpload(file);
                    }
                });
                return;
            }
        } else {
            t = (T) body;
        }

        final T back = t;
        if (async) {
            EasyProgressBar.getInstance().closeProgressBar();
            maniHandler.post(new Runnable() {
                @Override
                public void run() {
                    uploadListener.success(file, back, multimap);
                    removeUpload(file);
                }
            });
        } else {
            uploadListener.success(file, back, multimap);
            removeUpload(file);
        }
    }

    private void removeUpload(File file) {
        uploadSet.remove(file.getPath());
        if (uploadSet.size() == 0) {
            maniHandler.post(new Runnable() {
                @Override
                public void run() {
                    EasyProgressBar.getInstance().closeProgressBar();
                }
            });
        }
    }
}
