package com.sinyuk.jianyimaterial.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Sinyuk on 16.2.9.
 */
public class YihuoProfile implements Parcelable {

    public static final String TAG = "YihuoProfile";
    public static final String LOAD_REQUEST = "load_" + TAG;
    public static final String REFRESH_REQUEST = "refresh_" + TAG;
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

    public static String getTAG() {
        return TAG;
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


    public YihuoProfile(String id, String name, String schoolname, String price, String tel, String time, String pic, String username, String headImg) {
        this.id = id;
        this.name = name;
        this.schoolname = schoolname;
        this.price = price;
        this.tel = tel;
        this.time = time;
        this.pic = pic;
        this.username = username;
        this.headImg = headImg;
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
    }

    public static final Creator<YihuoProfile> CREATOR = new Creator<YihuoProfile>() {
        public YihuoProfile createFromParcel(Parcel source) {
            return new YihuoProfile(source);
        }

        public YihuoProfile[] newArray(int size) {
            return new YihuoProfile[size];
        }
    };

    @Override
    public String toString() {
        return "YihuoProfile{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", price='" + price + '\'' +
                ", tel='" + tel + '\'' +
                ", time='" + time + '\'' +
                ", pic='" + pic + '\'' +
                ", username='" + username + '\'' +
                ", headImg='" + headImg + '\'' +
                ", schoolname='" + schoolname + '\'' +
                '}';
    }
}
