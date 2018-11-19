package com.lbz.login.entities;

public class BaseUserInfo {

    protected AuthResult authResult;

    protected String nickName;

    protected int sex;

    protected String headImageUrl;

    protected String headImageUrlLarge;

    public AuthResult getAuthResult() {
        return authResult;
    }

    public void setAuthResult(AuthResult authResult) {
        this.authResult = authResult;
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public int getSex() {
        return sex;
    }

    public void setSex(int sex) {
        this.sex = sex;
    }

    public String getHeadImageUrl() {
        return headImageUrl;
    }

    public void setHeadImageUrl(String headImageUrl) {
        this.headImageUrl = headImageUrl;
    }

    public String getHeadImageUrlLarge() {
        return headImageUrlLarge;
    }

    public void setHeadImageUrlLarge(String headImageUrlLarge) {
        this.headImageUrlLarge = headImageUrlLarge;
    }

    @Override
    public String toString() {
        return "BaseUserInfo{" +
                "authResult=" + authResult +
                ", nickName='" + nickName + '\'' +
                ", sex=" + sex +
                ", headImageUrl='" + headImageUrl + '\'' +
                ", headImageUrlLarge='" + headImageUrlLarge + '\'' +
                '}';
    }


}
