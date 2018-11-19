package com.lbz.login.config;

import android.content.Context;
import android.text.TextUtils;

public class ThirdPartyInit {

    private static volatile ThirdPartyInit sInstance;

    private Builder builder;

    private Context context;

    private ThirdPartyInit(Context context, Builder builder) {
        this.context = context;
        this.builder = builder;
    }

    public Context getContext() {
        return context;
    }

    public Builder getBuilder() {
        return builder;
    }


    public static void init(Context context, ThirdPartyInit.Builder builder) {
        if (sInstance == null) {
            synchronized (ThirdPartyInit.class) {
                if (sInstance == null) {
                    sInstance = new ThirdPartyInit(context, builder);
                }
            }
        }
    }

    public static ThirdPartyInit getInstance() {
        return sInstance;
    }

    public static final class Builder {

        private String qqAppId;

        private String wxAppId;
        private String wxSecretId;

        private String wbAppId;
        private String wbRedirectUrl;

        public String getQqAppId() {
            return qqAppId;
        }

        public Builder setQqAppId(String qqAppId) {
            this.qqAppId = qqAppId;
            return this;
        }

        public String getWxAppId() {
            return wxAppId;
        }

        public Builder setWxAppId(String wxAppId,String wxSecretId) {
            this.wxAppId = wxAppId;
            this.wxSecretId = wxSecretId;
            return this;
        }

        public String getWxSecretId() {
            return wxSecretId;
        }

        public String getWbAppId() {
            return wbAppId;
        }

        public Builder setWbAppId(String wbAppId, String wbRedirectUrl) {
            this.wbAppId = wbAppId;
            this.wbRedirectUrl = wbRedirectUrl;
            return this;
        }

        public String getWbRedirectUrl() {
            return wbRedirectUrl;
        }

        public Builder build() {
            return this;
        }
    }

}
