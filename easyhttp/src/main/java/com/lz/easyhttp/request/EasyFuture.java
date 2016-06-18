package com.lz.easyhttp.request;

/**
 * 回调类
 */
public class EasyFuture {
    EasyBuilder builder;

    protected EasyFuture(EasyBuilder builder) {
        this.builder = builder;
    }

    public <T> void executeAsync(EasyLoadingListener<T> ionLoadingCallback){
        this.builder.async = true;
        EasyRequest.getInstence().request(builder, ionLoadingCallback);
    }

    public <T> void executeSync(EasyLoadingListener<T> ionLoadingCallback){
        this.builder.async = false;
        EasyRequest.getInstence().request(builder, ionLoadingCallback);
    }
}
