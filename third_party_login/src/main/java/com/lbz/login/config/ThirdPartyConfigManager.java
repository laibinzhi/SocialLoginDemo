package com.lbz.login.config;

import android.content.Intent;

import com.tencent.mm.opensdk.modelbase.BaseReq;
import com.tencent.mm.opensdk.modelbase.BaseResp;

public class ThirdPartyConfigManager {

    public static IThirdPartyConfig iThirdPartyConfig;

    public static void onWXEntryActivityOnReq(BaseReq baseReq) {
        if (iThirdPartyConfig != null) {
            iThirdPartyConfig.onWXEntryActivityOnReq(baseReq);
        }
    }

    public static void onWXEntryActivityOnResq(BaseResp baseResp) {
        if (iThirdPartyConfig != null) {
            iThirdPartyConfig.onWXEntryActivityOnResq(baseResp);
        }
    }

    public static void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (iThirdPartyConfig != null) {
            iThirdPartyConfig.onActivityResult(requestCode, resultCode, data);
        }
    }

    public static void onNewIntent(Intent intent) {
        if (iThirdPartyConfig != null) {
            iThirdPartyConfig.onNewIntent(intent);
        }
    }

}
