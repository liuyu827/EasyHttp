package com.lz.easyhttp.request;

import android.content.Context;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.lz.easyhttp.tools.MD5Tool;

import org.json.JSONObject;

import java.io.File;
import java.util.Map;

/**
 * 网络操作类
 */
public class EasyBuilder {

    protected Context context;
    /**
     * 请求地址
     */
    protected String requestUrl;

    protected String method;

    protected boolean async = true;

    /**
     * 表单请求参数
     */
    protected Map<String, String> parameters;

    /**
     * JSON请求参数
     */
    protected JsonObject requestJsonObject;

    /**
     * JSON请求参数
     */
    protected JSONObject requestJSONObject;


    /**
     * 上传文件
     */
    protected File uploadFile;

    /**
     * header
     */
    protected Map<String, String> headerMap;

    /**
     * 是否开启转轮
     */
    protected boolean isShowBar = false;

    /**
     * 转轮提示文字
     */
    protected String barMessage;

    /**
     * 时候可以打点请求
     */
    protected boolean canCancel = false;

    /**
     * 点击返回是否可以退出当前界面
     */
    protected boolean canFinish = false;

    /**
     * 超时时间 毫秒
     */
    protected int timeOut = 10000;

    /**
     * 是否先获取本地数据
     */
    protected boolean localFirst = false;

    protected String downloadPath;


    protected EasyBuilder(Context context, String url) {
        this.context = context;
        this.requestUrl = url;
    }

    protected String jsonString() {
        if (requestJsonObject != null) {
            return requestJsonObject.toString();
        } else if (requestJSONObject != null) {
            return requestJSONObject.toString();
        }
        return null;
    }


    public EasyBuilder setHeader(Map<String, String> headerMap) {
        this.headerMap = headerMap;
        return this;
    }

    public EasyBuilder setTimeout(int timeOut) {
        this.timeOut = timeOut;
        return this;
    }

    public EasyBuilder barMessage(String msg) {
        if (msg != null && msg.length() > 0) {
            this.isShowBar = true;
        }
        this.barMessage = msg;
        return this;
    }

    public EasyBuilder barCanCancel() {
        this.isShowBar = true;
        this.canCancel = true;
        return this;
    }

    public EasyBuilder barCanFinish() {
        this.isShowBar = true;
        this.canFinish = true;
        return this;
    }

    public EasyBuilder localFirst() {
        this.localFirst = true;
        return this;
    }

    public EasyFuture asGet(Map<String, String> getObject) {
        this.method = "GET";
        this.requestUrl = EasyURLEncoder.appendUrl(this.requestUrl, getObject);
        return new EasyFuture(this);
    }


    public EasyFuture asHead(Map<String, String> headObject) {
        this.method = "HEAD";
        this.requestUrl = EasyURLEncoder.appendUrl(this.requestUrl, headObject);
        return new EasyFuture(this);
    }


    public EasyFuture asPut(Map<String, String> putObject) {
        this.method = "PUT";
        this.parameters = putObject;
        return new EasyFuture(this);
    }

    public EasyFuture asPatch(Map<String, String> patchObject) {
        this.method = "PATCH";
        this.parameters = patchObject;
        return new EasyFuture(this);
    }


    public EasyFuture asDelete(Map<String, String> deleteObject) {
        this.method = "DELETE";
        this.parameters = deleteObject;
        return new EasyFuture(this);
    }

    public EasyFuture asPostParameters(Map<String, String> parameters) {
        this.method = "POST";
        this.parameters = parameters;
        return new EasyFuture(this);
    }

    public EasyFuture asPostJson(JsonObject requestObject) {
        this.method = "POST";
        this.requestJsonObject = requestObject;
        return new EasyFuture(this);
    }

    public EasyFuture asPostJson(JSONObject requestObject) {
        this.method = "POST";
        this.requestJSONObject = requestObject;
        return new EasyFuture(this);
    }


    public EasyFuture asUploadFile(File file) {
        this.method = "POST";
        this.uploadFile = file;
        return new EasyFuture(this);
    }

    public EasyDownLoadFuture asDownload(String path) {
        this.downloadPath = path;
        return new EasyDownLoadFuture(this);
    }

    protected String toMd5() {
        return MD5Tool.getMD5String(requestUrl + parameters + requestJsonObject + requestJSONObject + headerMap);
    }

    @Override
    public String toString() {
        return "\nurl: " + requestUrl +
                "\nmethod: " + method +
                (requestJsonObject != null ? "\njsonObject: " + requestJsonObject.toString() : "") +
                (headerMap != null ? "\nheader: " + headerMap.toString() : "") +
                (parameters != null ? "\nmap: " + parameters.toString() : "") +
                "\ntimeOut: " + timeOut + "毫秒" +
                "\nlocalFirst: " + localFirst +
                "\n\n";
    }
}
