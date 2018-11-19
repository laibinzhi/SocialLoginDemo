package com.lbz.login.auth;

import android.annotation.SuppressLint;
import android.app.Activity;


import com.lbz.login.callback.ThirdPartyAuthCallback;
import com.lbz.login.callback.ThirdPartyCallback;
import com.lbz.login.callback.ThirdPartyLoginCallback;
import com.lbz.login.entities.AuthResult;
import com.lbz.login.entities.WXUserInfo;
import com.lbz.login.config.WeChatConfig;
import com.tencent.mm.opensdk.constants.ConstantsAPI;
import com.tencent.mm.opensdk.modelbase.BaseReq;
import com.tencent.mm.opensdk.modelbase.BaseResp;
import com.tencent.mm.opensdk.modelmsg.SendAuth;
import com.tencent.mm.opensdk.openapi.IWXAPIEventHandler;

import org.json.JSONObject;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import okhttp3.Request;
import okhttp3.Response;

public class WeChatAuth extends WeChatConfig implements IAuth, IWXAPIEventHandler {

    private static final String BASE_URL = "https://api.weixin.qq.com/sns/";

    WeChatAuth(Activity activity, String appId, String secretId, ThirdPartyCallback callback) {
        super(activity, appId, secretId, callback);
    }

    @Override
    public void auth() {
        if (notInstalled()) {
            return;
        }

        SendAuth.Req req = new SendAuth.Req();
        req.scope = "snsapi_userinfo";
        req.state = String.valueOf(System.currentTimeMillis());
        iwxapi.sendReq(req);
    }

    @Override
    public void login() {
        auth();
    }

    @Override
    public void onReq(BaseReq baseReq) {

    }

    @SuppressLint("CheckResult")
    @Override
    public void onResp(BaseResp baseResp) {
        if (baseResp.getType() == ConstantsAPI.COMMAND_SENDAUTH) {
            //授权
            if (baseResp.errCode == BaseResp.ErrCode.ERR_OK) {
                //授权码
                String code = ((SendAuth.Resp) baseResp).code;
                getAccessToken(code).subscribe(new Consumer<AuthResult>() {
                    @Override
                    public void accept(AuthResult authResult) throws Exception {
                        if (thirdPartyCallback instanceof ThirdPartyAuthCallback) {
                            ((ThirdPartyAuthCallback) thirdPartyCallback).success(authResult);
                        } else if (thirdPartyCallback instanceof ThirdPartyLoginCallback) {
                            fetchUserInfo(authResult, ((ThirdPartyLoginCallback) thirdPartyCallback));
                        }
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        if (thirdPartyCallback != null) {
                            thirdPartyCallback.fail(ErrorCode.ERROR_SDK_PROBLEM, throwable.getMessage());
                        }
                    }
                });
            } else {
                if (thirdPartyCallback != null) {
                    thirdPartyCallback.cancel();
                }
            }
        }
    }

    @SuppressLint("CheckResult")
    private void fetchUserInfo(final AuthResult authResult, final ThirdPartyLoginCallback callback) {
        Observable.create(new ObservableOnSubscribe<WXUserInfo>() {
            @Override
            public void subscribe(ObservableEmitter<WXUserInfo> emitter) throws Exception {
                Request request = new Request.Builder()
                        .url(userInfoUrl(authResult.getCode(), authResult.getOpenId())).build();
                try {
                    Response response = okHttpClient.newCall(request).execute();
                    if (response.isSuccessful()) {
                        WXUserInfo user = WXUserInfo.parse(response.body().string());
                        user.setAuthResult(authResult);
                        emitter.onNext(user);
                    }
                } catch (Exception e) {
                    emitter.onError(e);
                }
            }
        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<WXUserInfo>() {
                    @Override
                    public void accept(WXUserInfo wxUser) throws Exception {
                        callback.success(wxUser);
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        thirdPartyCallback.fail(ErrorCode.ERROR_SDK_PROBLEM, throwable.getMessage());
                    }
                });
    }

    private Observable<AuthResult> getAccessToken(final String code) {
        return Observable.create(new ObservableOnSubscribe<AuthResult>() {
            @Override
            public void subscribe(ObservableEmitter<AuthResult> emitter) throws Exception {
                Request request = new Request.Builder()
                        .url(wxTokenUrl(code)).build();
                try {
                    Response response = okHttpClient.newCall(request).execute();
                    if (response.isSuccessful()) {
                        JSONObject jsonObject = new JSONObject(response.body().string());
                        String accessToken = jsonObject.getString("access_token");
                        String openid = jsonObject.getString("openid");
                        emitter.onNext(new AuthResult(openid, accessToken));
                    }
                } catch (Exception e) {
                    emitter.onError(e);
                }
            }
        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread());
    }

    private String wxTokenUrl(String code) {
        return BASE_URL
                + "oauth2/access_token?appid="
                + appId
                + "&secret="
                + secretId
                + "&code="
                + code
                + "&grant_type=authorization_code";
    }

    private String userInfoUrl(String token, String openid) {
        return BASE_URL
                + "userinfo?access_token="
                + token
                + "&openid="
                + openid;
    }
}
