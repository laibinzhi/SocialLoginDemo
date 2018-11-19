package com.lbz.login.auth;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.util.Log;

import com.lbz.login.callback.ThirdPartyAuthCallback;
import com.lbz.login.callback.ThirdPartyCallback;
import com.lbz.login.callback.ThirdPartyLoginCallback;
import com.lbz.login.entities.AuthResult;
import com.lbz.login.entities.QQUserInfo;
import com.lbz.login.config.QQConfig;
import com.tencent.tauth.IUiListener;
import com.tencent.tauth.Tencent;
import com.tencent.tauth.UiError;

import org.json.JSONObject;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import okhttp3.Request;
import okhttp3.Response;

public class QQAuth extends QQConfig implements IAuth, IUiListener {

    private static final String BASE_URL = "https://graph.qq.com/user/get_user_info";

    QQAuth(Activity activity, String appId, ThirdPartyCallback callback) {
        super(activity, appId, callback);
    }

    @Override
    public void auth() {
        if (notInstalled()) {
            return;
        }

        if (!tencent.isSessionValid()) {
            tencent.login(activity, "all", this);
        } else {
            if (thirdPartyCallback instanceof ThirdPartyAuthCallback) {
                AuthResult authResult = new AuthResult(tencent.getOpenId(), tencent.getAccessToken());
                ((ThirdPartyAuthCallback) thirdPartyCallback).success(authResult);
            }
        }
    }

    @Override
    public void login() {
        auth();
    }

    @Override
    public void onComplete(Object obj) {
        try {
            JSONObject jsonObject = new JSONObject(obj.toString());
            String token = jsonObject.getString("access_token");
            String openid = jsonObject.getString("openid");
            AuthResult authResult = new AuthResult(openid, token);
            if (thirdPartyCallback instanceof ThirdPartyAuthCallback) {
                ((ThirdPartyAuthCallback) thirdPartyCallback).success(authResult);
            } else if (thirdPartyCallback instanceof ThirdPartyLoginCallback) {
                fetchUserInfo(authResult, ((ThirdPartyLoginCallback) thirdPartyCallback));
            }
        } catch (Exception e) {
            thirdPartyCallback.fail(ErrorCode.ERROR_SDK_PROBLEM, e.getMessage());
        }
    }

    @SuppressLint("CheckResult")
    private void fetchUserInfo(final AuthResult authResult, final ThirdPartyLoginCallback callback) {
        Observable.create(new ObservableOnSubscribe<QQUserInfo>() {
            @Override
            public void subscribe(ObservableEmitter<QQUserInfo> emitter) throws Exception {
                Request request = new Request.Builder()
                        .url(userInfoUrl(authResult.getCode(), authResult.getOpenId())).build();
                try {
                    Response response = okHttpClient.newCall(request).execute();
                    if (response.isSuccessful()) {
                        QQUserInfo user = QQUserInfo.parse(response.body().string());
                        user.setAuthResult(authResult);
                        emitter.onNext(user);
                    }
                } catch (Exception e) {
                    emitter.onError(e);
                }
            }
        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<QQUserInfo>() {
                    @Override
                    public void accept(QQUserInfo qqUser) throws Exception {
                        callback.success(qqUser);
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        thirdPartyCallback.fail(ErrorCode.ERROR_SDK_PROBLEM, throwable.getMessage());
                    }
                });
    }

    @Override
    public void onError(UiError uiError) {
        if (thirdPartyCallback != null) {
            thirdPartyCallback.fail(ErrorCode.ERROR_SDK_PROBLEM, uiError.errorMessage);
        }
    }

    @Override
    public void onCancel() {
        if (thirdPartyCallback != null) {
            thirdPartyCallback.cancel();
        }
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Tencent.onActivityResultData(requestCode, resultCode, data, this);
    }

    private String userInfoUrl(String token, String openid) {
        return BASE_URL
                + "?access_token="
                + token
                + "&oauth_consumer_key="
                + appId
                + "&openid="
                + openid;
    }
}

