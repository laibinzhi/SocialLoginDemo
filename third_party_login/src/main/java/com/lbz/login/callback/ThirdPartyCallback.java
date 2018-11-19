package com.lbz.login.callback;


public abstract class ThirdPartyCallback {

    private int type;

    public void setType(int type) {
        this.type = type;
    }

    public int getType() {
        return type;
    }

    public abstract void fail(int errorCode, String defaultMsg);

    public abstract void cancel();

}
