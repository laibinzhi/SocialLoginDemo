package social.lbz.com.sociallogindemo;

import android.app.Application;

import com.lbz.login.config.ThirdPartyInit;

public class MyApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        initThirdPartyLogin();
    }

    private void initThirdPartyLogin() {

        ThirdPartyInit.Builder builder = new ThirdPartyInit.Builder()
                .setQqAppId(Constants.QQ_APP_ID)
                .setWxAppId(Constants.WECHAT_APP_ID, Constants.WECHAT_SECRETID)
                .setWbAppId(Constants.WEIBO_APP_ID, Constants.WEIBO_REDIRECTURL)
                .build();
        ThirdPartyInit.init(getApplicationContext(), builder);

    }


}
