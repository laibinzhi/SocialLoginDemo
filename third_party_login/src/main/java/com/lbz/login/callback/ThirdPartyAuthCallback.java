package com.lbz.login.callback;

import com.lbz.login.entities.AuthResult;

public abstract class ThirdPartyAuthCallback extends ThirdPartyCallback {

    public abstract void success(AuthResult authResult);

}
