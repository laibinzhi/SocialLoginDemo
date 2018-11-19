
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
