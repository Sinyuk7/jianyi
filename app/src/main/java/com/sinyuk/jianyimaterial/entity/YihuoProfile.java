package com.sinyuk.jianyimaterial.entity;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Sinyuk on 16.2.9.
 */
public class YihuoProfile implements Parcelable {

    public static final Creator<YihuoProfile> CREATOR = new Creator<YihuoProfile>() {
        public YihuoProfile createFromParcel(Parcel source) {
            return new YihuoProfile(source);
        }

        public YihuoProfile[] newArray(int size) {
            return new YihuoProfile[size];
        }
    };
    public String id;
    // 商品名
    public String name;
    public String price;
    // 电话
    public String tel;
    public String time;
    // 照片封面 1 张
    public String pic;
    public String username;
    // 用户头像
    public String headImg;
    public String schoolname;
    public String uid;
    @SerializedName("del")
    public String isOnShelf;
    public boolean isLiked = true;

    public YihuoProfile() {
    }

    protected YihuoProfile(Parcel in) {
        this.id = in.readString();
        this.name = in.readString();
        this.price = in.readString();
        this.tel = in.readString();
        this.time = in.readString();
        this.pic = in.readString();
        this.username = in.readString();
        this.headImg = in.readString();
        this.schoolname = in.readString();
        this.uid = in.readString();
        this.isOnShelf = in.readString();
        this.isLiked = in.readByte() != 0;
    }

    public boolean isLiked() {
        return isLiked;
    }

    public void setLiked(boolean liked) {
        isLiked = liked;
    }

    public boolean isOnShelf() {
        return isOnShelf.equals("0");
    }

    public void setOnShelf(boolean isOnShelf) {
        final String value = isOnShelf ? "0" : "1";
        this.isOnShelf = value;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getTel() {
        return tel;
    }

    public void setTel(String tel) {
        this.tel = tel;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getPic() {
        return pic;
    }

    public void setPic(String pic) {
        this.pic = pic;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getHeadImg() {
        return headImg;
    }

    public void setHeadImg(String headImg) {
        this.headImg = headImg;
    }

    public String getSchoolname() {
        return schoolname;
    }

    public void setSchoolname(String schoolname) {
        this.schoolname = schoolname;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.id);
        dest.writeString(this.name);
        dest.writeString(this.price);
        dest.writeString(this.tel);
        dest.writeString(this.time);
        dest.writeString(this.pic);
        dest.writeString(this.username);
        dest.writeString(this.headImg);
        dest.writeString(this.schoolname);
        dest.writeString(this.uid);
        dest.writeString(this.isOnShelf);
        dest.writeByte((byte) (isLiked ? 1 : 0));
    }
}
