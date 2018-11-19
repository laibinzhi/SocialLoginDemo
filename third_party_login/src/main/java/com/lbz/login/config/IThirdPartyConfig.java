package com.lbz.login.config;

import android.content.Intent;

import com.tencent.mm.opensdk.modelbase.BaseReq;
import com.tencent.mm.opensdk.modelbase.BaseResp;

public interface IThirdPartyConfig {

    int TYPE_WX = 1;
    int TYPE_QQ = 2;
    int TYPE_WB = 3;

    void onWXEntryActivityOnReq(BaseReq baseReq);

    void onWXEntryActivityOnResq(BaseResp baseResp);

    void onActivityResult(int requestCode, int resultCode, Intent data);

    void onNewIntent(Intent intent);

    interface ErrorCode {

        int ERROR_SDK_PROBLEM = -1;

        //第三方平台的appid缺失
        int ERROR_APPID_EMPTY = 1;

        //未安装
        int ERROR_NOT_INSTALLED = 2;

    }
}
