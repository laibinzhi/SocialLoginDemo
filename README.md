# ���
��װQQ,WeChat,WeiBo��������¼��һ���⣨����������
# ���ټ���
1. �����
- ����һ����������[��Ŀ](https://github.com/laibinzhi/SocialLoginDemo),��moudleģ��***third_party_login***�����ļ��и��Ƶ�����Ŀ��Ŀ¼����app��build.gradle�е�***dependencies***������´���

```
    implementation project(':third_party_login')

```

Ȼ����settings.gradle�м���

```
    include ':app', ':third_party_login'

```
Ȼ��rebuildһ�¾�ok�ˡ�

- ��������ͨ��dependencies��ʽ��

������app��Ŀ¼��build.gradle�����

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

Ȼ������Ŀ��build.gradle�е�dependencies���


```
 implementation 'com.lbz.login:third_party_login:0.1.1'
```

rebuildһ�¾�ok��



2. ΢����Ȩ��¼��Ҫһ��΢�Żص���*WXEntryActivity*��ע��������λ�����ڳ�������µ�wxapi���¡������ҳ���İ�����*social.lbz.com.sociallogindemo*����ô��������*social.lbz.com.sociallogindemo.wxapi*�¡�ֱ�Ӹ������漴�ɡ��ص��¼�onReq()��onResp()����ThirdPartyConfigManager����


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


3. ��Application����OnCreate()�����г�ʼ��**ThirdPartyInit**����Ҫ�ṩ���󿪷�ƽ̨��Ӧ��id����Ϣ������������manifest�ļ���ע�����Application��

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

4. ��manifest�ļ�application��ǩ�ڵļ�������

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
    
        <!-- ΢�� -->
        <activity
            android:name=".wxapi.WXEntryActivity"
            android:configChanges="keyboardHidden|orientation|screenSize"
            android:exported="true"
            android:launchMode="singleTask"
            android:theme="@android:style/Theme.Translucent.NoTitleBar"/>
```

5. ��ε�����Ȩ��
- ��һ��������һ���ص���̳���ThirdPartyAuthCallback,����

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

- �ڶ���

AuthManager.getInstance().authQQ(this, mSocialAuthCallback);

 AuthManager.getInstance().authWeChat(this, mSocialAuthCallback);
 
  AuthManager.getInstance().authWeiBo(this, mSocialAuthCallback);
  
  Ȼ��Ϳ����ڻص�����**success**�л�ȡtoken��
  

6. ��λ�ȡ����ƽ̨�û���Ϣ��
 
- ��һ��������һ���ص���̳���ThirdPartyLoginCallback,����

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

- �ڶ���

AuthManager.getInstance().loginQQ(this, mSocialLoginCallback);

 AuthManager.getInstance().loginWeChat(this, mSocialLoginCallback);
 
  AuthManager.getInstance().loginWeiBo(this, mSocialLoginCallback);
  
  Ȼ��Ϳ����ڻص�����**success**�л�ȡ�û���Ϣ��
  
  
# ��Ŀ��ַ
[https://github.com/laibinzhi/SocialLoginDemo](https://github.com/laibinzhi/SocialLoginDemo)


# ��Ŀ��ͼ
![image](http://pd4brty72.bkt.clouddn.com/gif5%E6%96%B0%E6%96%87%E4%BB%B6%20%286%29.gif)

