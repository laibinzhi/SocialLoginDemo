package com.lbz.login.config;

import android.app.Activity;
import android.text.TextUtils;

import com.lbz.login.R;
import com.lbz.login.callback.ThirdPartyCallback;
import com.tencent.tauth.Tencent;

public class QQConfig extends BaseConfig {

    protected Tencent tencent;

    protected QQConfig(Activity activity, String appId, ThirdPartyCallback callback) {
        super(activity, appId, callback);

        if (callback != null) {
            callback.setType(IThirdPartyConfig.TYPE_QQ);
        }

        if (TextUtils.isEmpty(appId)) {
            if (callback != null) {
                callback.fail(IThirdPartyConfig.ErrorCode.ERROR_APPID_EMPTY, getString(R.string.qq_appid_is_empty));
            }
            return;
        }

        tencent = Tencent.createInstance(appId, activity.getApplicationContext());
    }

    protected boolean notInstalled() {
        if (tencent == null) {
            return true;
        }

        if (!tencent.isQQInstalled(activity)) {
            if (thirdPartyCallback != null) {
                thirdPartyCallback.fail(IThirdPartyConfig.ErrorCode.ERROR_NOT_INSTALLED, getString(R.string.uninstall_qq));
            }
            return true;
        }

        return false;
    }
}
