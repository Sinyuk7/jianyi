package com.sinyuk.jianyimaterial.entity;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Sinyuk on 16.2.9.
 */
public class YihuoProfile implements Parcelable {

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

    public boolean isOnSell = true;

    public boolean isLiked = true;

    public boolean isLiked() {
        return isLiked;
    }

    public void setLiked(boolean liked) {
        isLiked = liked;
    }

    public boolean isOnSell() {
        return isOnSell;
    }

    public void setOnSell(boolean onSell) {
        isOnSell = onSell;
    }


    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getPrice() {
        return price;
    }

    public String getTel() {
        return tel;
    }

    public String getTime() {
        return time;
    }

    public String getPic() {
        return pic;
    }

    public String getUsername() {
        return username;
    }

    public String getHeadImg() {
        return headImg;
    }

    public String getSchoolname() {
        return schoolname;
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

    public void setId(String id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public void setTel(String tel) {
        this.tel = tel;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public void setPic(String pic) {
        this.pic = pic;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setHeadImg(String headImg) {
        this.headImg = headImg;
    }

    public void setSchoolname(String schoolname) {
        this.schoolname = schoolname;
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
        dest.writeByte((byte) (isOnSell ? 1 : 0));
        dest.writeByte((byte) (isLiked ? 1 : 0));
    }

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
        this.isOnSell = in.readByte() != 0;
        this.isLiked = in.readByte() != 0;
    }

    public static final Creator<YihuoProfile> CREATOR = new Creator<YihuoProfile>() {
        public YihuoProfile createFromParcel(Parcel source) {
            return new YihuoProfile(source);
        }

        public YihuoProfile[] newArray(int size) {
            return new YihuoProfile[size];
        }
    };
}
