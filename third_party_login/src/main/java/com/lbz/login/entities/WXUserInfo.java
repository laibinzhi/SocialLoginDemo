package com.lbz.login.entities;

import org.json.JSONException;
import org.json.JSONObject;

public class WXUserInfo extends BaseUserInfo {

    private String city;

    private String country;

    private String province;

    private String unionid;

    public static WXUserInfo parse(String json) throws JSONException {
        JSONObject jsonObject = new JSONObject(json);
        WXUserInfo user = new WXUserInfo();
        user.setNickName(jsonObject.getString("nickname"));
        user.setSex(jsonObject.getInt("sex"));
        user.setHeadImageUrl(jsonObject.getString("headimgurl"));
        user.setHeadImageUrlLarge(jsonObject.getString("headimgurl"));
        user.setProvince(jsonObject.getString("province"));
        user.setCity(jsonObject.getString("city"));
        user.setCountry(jsonObject.getString("country"));
        user.setUnionid(jsonObject.getString("unionid"));
        return user;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public String getUnionid() {
        return unionid;
    }

    public void setUnionid(String unionid) {
        this.unionid = unionid;
    }

    @Override
    public String toString() {
        return "WXUserInfo{" +
                "city='" + city + '\'' +
                ", country='" + country + '\'' +
                ", province='" + province + '\'' +
                ", unionid='" + unionid + '\'' +
                ", authResult=" + authResult +
                ", nickName='" + nickName + '\'' +
                ", sex=" + sex +
                ", headImageUrl='" + headImageUrl + '\'' +
                ", headImageUrlLarge='" + headImageUrlLarge + '\'' +
                '}';
    }
}
