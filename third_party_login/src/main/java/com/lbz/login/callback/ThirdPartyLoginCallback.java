package com.lbz.login.callback;

import com.lbz.login.entities.BaseUserInfo;

public abstract class ThirdPartyLoginCallback extends ThirdPartyCallback{

    public abstract void success(BaseUserInfo user);

}
