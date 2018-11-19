package com.lbz.login.auth;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.util.Log;

import com.lbz.login.callback.ThirdPartyAuthCallback;
import com.lbz.login.callback.ThirdPartyCallback;
import com.lbz.login.callback.ThirdPartyLoginCallback;
import com.lbz.login.entities.AuthResult;
import com.lbz.login.entities.WBUserInfo;
import com.lbz.login.config.WeiBoConfig;
import com.sina.weibo.sdk.auth.AccessTokenKeeper;
import com.sina.weibo.sdk.auth.Oauth2AccessToken;
import com.sina.weibo.sdk.auth.WbAuthListener;
import com.sina.weibo.sdk.auth.WbConnectErrorMessage;
import com.sina.weibo.sdk.auth.sso.SsoHandler;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import okhttp3.Request;
import okhttp3.Response;

public class WeiBoAuth extends WeiBoConfig implements IAuth, WbAuthListener {

    private static final String BASE_URL = "https://api.weibo.com/2/users/show.json";

    WeiBoAuth(Activity activity, String appId, String redirectUrl, ThirdPartyCallback callback) {
        super(activity, appId, redirectUrl, callback);
    }

    @Override
    public void auth() {
        if (notInstalled()) {
            return;
        }

        ssoHandler = new SsoHandler(activity);
        ssoHandler.authorize(this);
    }

    @Override
    public void login() {
        auth();
    }

    @Override
    public void onSuccess(Oauth2AccessToken oauth2AccessToken) {
        if (oauth2AccessToken.isSessionValid()) {
            AccessTokenKeeper.writeAccessToken(activity, oauth2AccessToken);
            AuthResult authResult = new AuthResult(oauth2AccessToken.getUid(), oauth2AccessToken.getToken());
            if (thirdPartyCallback instanceof ThirdPartyAuthCallback) {
                ((ThirdPartyAuthCallback) thirdPartyCallback).success(authResult);
            } else if (thirdPartyCallback instanceof ThirdPartyLoginCallback) {
                fetchUserInfo(authResult, ((ThirdPartyLoginCallback) thirdPartyCallback));
            }
        } else {
            if (thirdPartyCallback != null) {
                thirdPartyCallback.fail(ErrorCode.ERROR_SDK_PROBLEM, "");
            }
        }
    }

    @SuppressLint("CheckResult")
    private void fetchUserInfo(final AuthResult authResult, final ThirdPartyLoginCallback callback) {
        Observable.create(new ObservableOnSubscribe<WBUserInfo>() {
            @Override
            public void subscribe(ObservableEmitter<WBUserInfo> emitter) throws Exception {
                Request request = new Request.Builder()
                        .url(userInfoUrl(authResult.getCode(), authResult.getOpenId())).build();
                try {
                    Response response = okHttpClient.newCall(request).execute();
                    if (response.isSuccessful()) {
                        WBUserInfo user = WBUserInfo.parse(response.body().string());
                        user.setAuthResult(authResult);
                        emitter.onNext(user);
                    }
                } catch (Exception e) {
                    emitter.onError(e);
                }
            }
        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<WBUserInfo>() {
                    @Override
                    public void accept(WBUserInfo wbUser) throws Exception {
                        callback.success(wbUser);
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        thirdPartyCallback.fail(ErrorCode.ERROR_SDK_PROBLEM, throwable.getMessage());
                    }
                });
    }

    @Override
    public void cancel() {
        if (thirdPartyCallback != null) {
            thirdPartyCallback.cancel();
        }
    }

    @Override
    public void onFailure(WbConnectErrorMessage wbConnectErrorMessage) {
        if (thirdPartyCallback != null) {
            thirdPartyCallback.fail(ErrorCode.ERROR_SDK_PROBLEM, wbConnectErrorMessage.getErrorMessage());
        }
    }

    void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (ssoHandler != null) {
            ssoHandler.authorizeCallBack(requestCode, resultCode, data);
        }
    }

    private String userInfoUrl(String token, String openid) {
        return BASE_URL
                + "?access_token=" + token
                + "&uid=" + openid;
    }
}
