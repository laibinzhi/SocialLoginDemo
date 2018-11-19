package com.lbz.login.config;

import android.app.Activity;
import android.support.annotation.StringRes;

import com.lbz.login.callback.ThirdPartyCallback;

public class BaseConfig {

    protected ThirdPartyCallback thirdPartyCallback;
    protected Activity activity;
    protected String appId;

    protected BaseConfig(Activity activity, String appId, ThirdPartyCallback callback) {
        this.activity = activity;
        this.appId = appId;
        this.thirdPartyCallback = callback;
    }

    protected final String getString(@StringRes int resId) {
        return ThirdPartyInit.getInstance().getContext().getString(resId);
    }
}
