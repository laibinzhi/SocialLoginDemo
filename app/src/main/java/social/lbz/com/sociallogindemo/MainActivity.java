package social.lbz.com.sociallogindemo;

import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.lbz.login.auth.AuthManager;
import com.lbz.login.callback.ThirdPartyAuthCallback;
import com.lbz.login.entities.AuthResult;
import com.lbz.login.entities.BaseUserInfo;
import com.lbz.login.config.ThirdPartyConfigManager;
import com.lbz.login.callback.ThirdPartyLoginCallback;
public class MainActivity extends AppCompatActivity implements View.OnClickListener{
    private TextView mUserInfoTv;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        findViewById(R.id.btn_qq).setOnClickListener(this);
        findViewById(R.id.btn_wx).setOnClickListener(this);
        findViewById(R.id.btn_wb).setOnClickListener(this);
        findViewById(R.id.btn_qq_auth).setOnClickListener(this);
        findViewById(R.id.btn_wx_auth).setOnClickListener(this);
        findViewById(R.id.btn_wb_auth).setOnClickListener(this);
        mUserInfoTv = findViewById(R.id.tv_user);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_qq:
                AuthManager.getInstance().loginQQ(this, mSocialLoginCallback);
                break;
            case R.id.btn_wx:

                AuthManager.getInstance().loginWeChat(this, mSocialLoginCallback);
                break;
            case R.id.btn_wb:
                AuthManager.getInstance().loginWeiBo(this, mSocialLoginCallback);

                break;
            case R.id.btn_qq_auth:
                AuthManager.getInstance().authQQ(this, mSocialAuthCallback);

                break;
            case R.id.btn_wx_auth:
                AuthManager.getInstance().authWeChat(this, mSocialAuthCallback);

                break;
            case R.id.btn_wb_auth:
                AuthManager.getInstance().authWeiBo(this, mSocialAuthCallback);

                break;
        }
    }

    private ThirdPartyLoginCallback mSocialLoginCallback = new ThirdPartyLoginCallback() {
        @Override
        public void success(BaseUserInfo baseUser) {
            mUserInfoTv.setText(baseUser.toString());
        }

        @Override
        public void fail(int errorCode, String defaultMsg) {
            String msg = "fail:" + getType() + " errCode:" + errorCode + " defaultMsg:" + defaultMsg;
            toast(msg);
        }

        @Override
        public void cancel() {
            String msg = "cancel:" + getType();
            toast(msg);
        }
    };

    private ThirdPartyAuthCallback mSocialAuthCallback = new ThirdPartyAuthCallback() {
        @Override
        public void success(AuthResult authResult) {
            String msg = "success:" + getType() + " code:" + authResult.getCode();
            toast(msg);
            mUserInfoTv.setText("token="+authResult.getCode());
        }

        @Override
        public void fail(int errorCode, String defaultMsg) {
            String msg = "fail:" + getType() + " errCode:" + errorCode + " defaultMsg:" + defaultMsg;
            toast(msg);
        }

        @Override
        public void cancel() {
            String msg = "cancel:" + getType();
            toast(msg);
        }
    };


    private void toast(String msg) {
        Toast.makeText(MainActivity.this, msg, Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        ThirdPartyConfigManager.onActivityResult(requestCode, resultCode, data);
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        ThirdPartyConfigManager.onNewIntent(intent);
        super.onNewIntent(intent);
    }
}
