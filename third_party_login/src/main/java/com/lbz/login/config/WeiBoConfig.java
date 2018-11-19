package com.lbz.login.config;

import android.app.Activity;
import android.text.TextUtils;

import com.lbz.login.R;
import com.lbz.login.callback.ThirdPartyCallback;
import com.sina.weibo.sdk.WbSdk;
import com.sina.weibo.sdk.auth.AuthInfo;
import com.sina.weibo.sdk.auth.sso.SsoHandler;
import com.sina.weibo.sdk.share.WbShareHandler;

public class WeiBoConfig extends BaseConfig {

    private static final String SCOPE = "email";

    protected SsoHandler ssoHandler;
    protected WbShareHandler shareHandler;
    protected String redirectUrl;

    protected WeiBoConfig(Activity activity, String appId, String redirectUrl, ThirdPartyCallback callback) {
        super(activity, appId, callback);
        this.redirectUrl = redirectUrl;

        if (callback != null) {
            callback.setType(IThirdPartyConfig.TYPE_WB);
        }

        if (TextUtils.isEmpty(appId) || TextUtils.isEmpty(redirectUrl)) {
            if (callback != null) {
                callback.fail(IThirdPartyConfig.ErrorCode.ERROR_APPID_EMPTY, getString(R.string.wb_appid_or_redirectUrl_is_empty));
            }
            return;
        }

        WbSdk.install(activity.getApplicationContext(), new AuthInfo(activity.getApplicationContext(), appId, redirectUrl, SCOPE));
    }

    protected boolean notInstalled() {
        try {
            WbSdk.checkInit();
        } catch (Exception e) {
            return true;
        }

        return false;
    }
}
