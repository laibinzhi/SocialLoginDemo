package com.lbz.login.auth;

import android.app.Activity;
import android.content.Intent;

import com.lbz.login.callback.ThirdPartyAuthCallback;
import com.lbz.login.callback.ThirdPartyLoginCallback;
import com.lbz.login.config.ThirdPartyInit;
import com.lbz.login.config.IThirdPartyConfig;
import com.lbz.login.config.ThirdPartyConfigManager;
import com.tencent.mm.opensdk.modelbase.BaseReq;
import com.tencent.mm.opensdk.modelbase.BaseResp;

public class AuthManager implements IThirdPartyConfig {

    private static volatile AuthManager sInstance;

    private ThirdPartyInit.Builder builder;

    private WeChatAuth weChatAuth;
    private WeiBoAuth weiBoAuth;
    private QQAuth qqAuth;

    private AuthManager() {
        builder = ThirdPartyInit.getInstance().getBuilder();
    }

    public static AuthManager getInstance() {
        if (sInstance == null) {
            synchronized (AuthManager.class) {
                if (sInstance == null) {
                    sInstance = new AuthManager();
                }
            }
        }
        if (ThirdPartyConfigManager.iThirdPartyConfig == null) {
            ThirdPartyConfigManager.iThirdPartyConfig = sInstance;
        }
        return sInstance;
    }

    public void auth(Activity activity, int type, ThirdPartyAuthCallback callback) {
        switch (type) {
            case IThirdPartyConfig.TYPE_WX:
                authWeChat(activity, callback);
                break;
            case IThirdPartyConfig.TYPE_WB:
                authWeiBo(activity, callback);
                break;
            case IThirdPartyConfig.TYPE_QQ:
                authQQ(activity, callback);
                break;
        }
    }

    /**
     * 根据type来登录
     */
    public void login(Activity activity, int type, ThirdPartyLoginCallback callback) {
        switch (type) {
            case IThirdPartyConfig.TYPE_WX:
                loginWeChat(activity, callback);
                break;
            case IThirdPartyConfig.TYPE_WB:
                loginWeiBo(activity, callback);
                break;
            case IThirdPartyConfig.TYPE_QQ:
                loginQQ(activity, callback);
                break;
        }
    }

    /**
     * 微信授权
     */
    public void authWeChat(Activity activity, ThirdPartyAuthCallback callback) {
        weChatAuth = new WeChatAuth(activity, builder.getWxAppId(), builder.getWxSecretId(), callback);
        weChatAuth.auth();
    }

    /**
     * 微博授权
     */
    public void authWeiBo(Activity activity, ThirdPartyAuthCallback callback) {
        weiBoAuth = new WeiBoAuth(activity, builder.getWbAppId(), builder.getWbRedirectUrl(), callback);
        weiBoAuth.auth();
    }

    /**
     * QQ授权
     */
    public void authQQ(Activity activity, ThirdPartyAuthCallback callback) {
        qqAuth = new QQAuth(activity, builder.getQqAppId(), callback);
        qqAuth.auth();
    }


    /**
     * 微信登录
     *
     */
    public void loginWeChat(Activity activity, ThirdPartyLoginCallback callback) {
        weChatAuth = new WeChatAuth(activity, builder.getWxAppId(), builder.getWxSecretId(), callback);
        weChatAuth.login();
    }

    /**
     * 微博登录
     */
    public void loginWeiBo(Activity activity, ThirdPartyLoginCallback callback) {
        weiBoAuth = new WeiBoAuth(activity, builder.getWbAppId(), builder.getWbRedirectUrl(), callback);
        weiBoAuth.login();
    }

    /**
     * QQ登录
     *
     */
    public void loginQQ(Activity activity, ThirdPartyLoginCallback callback) {
        qqAuth = new QQAuth(activity, builder.getQqAppId(), callback);
        qqAuth.login();
    }

    @Override
    public void onWXEntryActivityOnReq(BaseReq baseReq) {
        if (weChatAuth != null) {
            weChatAuth.onReq(baseReq);
        }
    }

    @Override
    public void onWXEntryActivityOnResq(BaseResp baseResp) {
        if (weChatAuth != null) {
            weChatAuth.onResp(baseResp);
        }
    }


    /**
     * qq登录微博登录都需要在其当前的activity的onActivityResult中调用该方法
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (qqAuth != null) {
            qqAuth.onActivityResult(requestCode, resultCode, data);
        }
        if (weiBoAuth != null ) {
            weiBoAuth.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    public void onNewIntent(Intent intent) {

    }

}
