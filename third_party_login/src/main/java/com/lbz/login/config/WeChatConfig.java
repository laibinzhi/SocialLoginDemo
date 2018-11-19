package com.lbz.login.config;

import android.app.Activity;
import android.text.TextUtils;

import com.lbz.login.R;
import com.lbz.login.callback.ThirdPartyCallback;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;

public class WeChatConfig extends BaseConfig {

    protected IWXAPI iwxapi;
    protected String secretId;

    protected WeChatConfig(Activity activity, String appId, String secretId, ThirdPartyCallback callback) {
        super(activity, appId, callback);
        this.secretId = secretId;

        if (callback != null) {
            callback.setType(IThirdPartyConfig.TYPE_WX);
        }

        if (TextUtils.isEmpty(appId) || TextUtils.isEmpty(secretId)) {
            if (callback != null) {
                callback.fail(IThirdPartyConfig.ErrorCode.ERROR_APPID_EMPTY, getString(R.string.wx_appid_or_secretId_is_empty));
            }
            return;
        }

        iwxapi = WXAPIFactory.createWXAPI(activity, appId, true);
        iwxapi.registerApp(appId);
    }

    protected boolean notInstalled() {
        if (iwxapi == null) {
            return true;
        }

        if (!iwxapi.isWXAppInstalled()) {
            if (thirdPartyCallback != null) {
                thirdPartyCallback.fail(IThirdPartyConfig.ErrorCode.ERROR_NOT_INSTALLED, getString(R.string.uninstall_wx));
            }
            return true;
        }

        return false;
    }
}
