package com.lbz.login.auth;


import com.lbz.login.config.IThirdPartyConfig;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;

public interface IAuth {

    //创建OkHttpClient来请求各个平台的用户信息
    OkHttpClient okHttpClient = new OkHttpClient.Builder().retryOnConnectionFailure(true)
            .connectTimeout(60, TimeUnit.SECONDS)
            .readTimeout(60, TimeUnit.SECONDS).build();

    void auth();

    void login();

    interface ErrorCode extends IThirdPartyConfig.ErrorCode {


    }

}
