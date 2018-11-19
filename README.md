# 简介
封装QQ,WeChat,WeiBo第三方登录的一个库（不包含分享）
# 快速集成
1. 引入库
- 方法一，下载整个[项目](https://github.com/laibinzhi/SocialLoginDemo),把moudle模块***third_party_login***整个文件夹复制到你项目根目录，在app的build.gradle中的***dependencies***添加如下代码

```
    implementation project(':third_party_login')

```

然后在settings.gradle中加入

```
    include ':app', ':third_party_login'

```
然后rebuild一下就ok了。

- 方法二，通过dependencies方式：

首先在app根目录的build.gradle中添加

```
allprojects {
    repositories {
        google()
        jcenter()
        maven { url "https://dl.bintray.com/thelasterstar/maven/" }
        maven {
            url 'https://dl.bintray.com/laibinzhi/maven/'
        }
    }
}
```

然后再项目的build.gradle中的dependencies添加


```
 implementation 'com.lbz.login:third_party_login:0.1.1'
```

rebuild一下就ok了



2. 微信授权登录需要一个微信回调类*WXEntryActivity*，注意这个类的位置是在程序包名下的wxapi包下。例如我程序的包名是*social.lbz.com.sociallogindemo*，那么这个类就在*social.lbz.com.sociallogindemo.wxapi*下。直接复制下面即可。回调事件onReq()和onResp()交给ThirdPartyConfigManager处理。


```
package social.lbz.com.sociallogindemo.wxapi;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.lbz.login.config.ThirdPartyInit;
import com.lbz.login.config.ThirdPartyConfigManager;
import com.tencent.mm.opensdk.modelbase.BaseReq;
import com.tencent.mm.opensdk.modelbase.BaseResp;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.IWXAPIEventHandler;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;

public class WXEntryActivity extends Activity implements IWXAPIEventHandler {

    private IWXAPI mApi;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mApi = WXAPIFactory.createWXAPI(this, ThirdPartyInit.getInstance().getBuilder().getWxAppId(), true);
        mApi.registerApp(ThirdPartyInit.getInstance().getBuilder().getWxAppId());

        mApi.handleIntent(getIntent(), this);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        mApi.handleIntent(getIntent(), this);
    }

    @Override
    public void onReq(BaseReq baseReq) {
        ThirdPartyConfigManager.onWXEntryActivityOnReq(baseReq);
    }

    @Override
    public void onResp(BaseResp baseResp) {
        if (null == baseResp) {
            return;
        }
        ThirdPartyConfigManager.onWXEntryActivityOnResq(baseResp);
        finish();
    }
}
```


3. 在Application子类OnCreate()方法中初始化**ThirdPartyInit**，需要提供三大开放平台的应用id等信息。最后别忘记在manifest文件中注册这个Application。

```
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
```

4. 在manifest文件application标签内的加入以下

```
        <activity
            android:name="com.tencent.tauth.AuthActivity"
            android:launchMode="singleTask"
            android:noHistory="true">
            <intent-filter>
                <action android:name="android.intent.action.VIEW"/>

                <category android:name="android.intent.category.DEFAULT"/>
                <category android:name="android.intent.category.BROWSABLE"/>

                <data android:scheme="tencentxxxxxxx"/>
            </intent-filter>
        </activity>
        <activity
            android:name="com.tencent.connect.common.AssistActivity"
            android:configChanges="orientation|keyboardHidden|screenSize"
            android:theme="@android:style/Theme.Translucent.NoTitleBar"/>
    
        <!-- 微信 -->
        <activity
            android:name=".wxapi.WXEntryActivity"
            android:configChanges="keyboardHidden|orientation|screenSize"
            android:exported="true"
            android:launchMode="singleTask"
            android:theme="@android:style/Theme.Translucent.NoTitleBar"/>
```

5. 如何调用授权？
- 第一步，创建一个回调类继承于ThirdPartyAuthCallback,例如

```
 private ThirdPartyAuthCallback mSocialAuthCallback = new ThirdPartyAuthCallback() {
        @Override
        public void success(AuthResult authResult) {
            mUserInfoTv.setText("token=" + authResult.getCode());
        }

        @Override
        public void fail(int errorCode, String defaultMsg) {
         
            Log.e("MainActivity","fail:" + getType() + " errCode:" + errorCode + " defaultMsg:" + defaultMsg);
        }

        @Override
        public void cancel() {
            Log.e("MainActivity","cancel:" + getType());
        }
    };
```

- 第二步

AuthManager.getInstance().authQQ(this, mSocialAuthCallback);

 AuthManager.getInstance().authWeChat(this, mSocialAuthCallback);
 
  AuthManager.getInstance().authWeiBo(this, mSocialAuthCallback);
  
  然后就可以在回调方法**success**中获取token。
  

6. 如何获取三大平台用户信息？
 
- 第一步，创建一个回调类继承于ThirdPartyLoginCallback,例如

```
  private ThirdPartyLoginCallback mSocialLoginCallback = new ThirdPartyLoginCallback() {
        @Override
        public void success(BaseUserInfo baseUser) {
            mUserInfoTv.setText("user="+baseUser.toString());
        }

        @Override
        public void fail(int errorCode, String defaultMsg) {
            Log.e("MainActivity","fail:" + getType() + " errCode:" + errorCode + " defaultMsg:" + defaultMsg);
        }

        @Override
        public void cancel() {
            Log.e("MainActivity","cancel:" + getType());
        }
    };
```

- 第二步

AuthManager.getInstance().loginQQ(this, mSocialLoginCallback);

 AuthManager.getInstance().loginWeChat(this, mSocialLoginCallback);
 
  AuthManager.getInstance().loginWeiBo(this, mSocialLoginCallback);
  
  然后就可以在回调方法**success**中获取用户信息。
  
  
# 项目地址
[https://github.com/laibinzhi/SocialLoginDemo](https://github.com/laibinzhi/SocialLoginDemo)


# 项目截图
![image](http://pd4brty72.bkt.clouddn.com/gif5%E6%96%B0%E6%96%87%E4%BB%B6%20%286%29.gif)

