package com.sinyuk.jianyimaterial.entity;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Sinyuk on 16.2.14.
 */
public class Needs implements Parcelable {
    private static final String TAG = "Needs";
    public static final String LOAD_REQUEST = "load_" + TAG;
    public static final String REFRESH_REQUEST = "refresh_" + TAG;
    public String id;
    public String detail;
    public String price;
    public String tel;
    public String time;
    public String username;
    public String headimg;
    public String schoolname;

    public String getId() {
        return id;
    }

    public String getDetail() {
        return detail;
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

    public String getUsername() {
        return username;
    }

    public String getHeadimg() {
        return headimg;
    }

    public String getSchoolname() {
        return schoolname;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.id);
        dest.writeString(this.detail);
        dest.writeString(this.price);
        dest.writeString(this.tel);
        dest.writeString(this.time);
        dest.writeString(this.username);
        dest.writeString(this.headimg);
        dest.writeString(this.schoolname);
    }

    public Needs() {
    }

    protected Needs(Parcel in) {
        this.id = in.readString();
        this.detail = in.readString();
        this.price = in.readString();
        this.tel = in.readString();
        this.time = in.readString();
        this.username = in.readString();
        this.headimg = in.readString();
        this.schoolname = in.readString();
    }

    public static final Parcelable.Creator<Needs> CREATOR = new Parcelable.Creator<Needs>() {
        public Needs createFromParcel(Parcel source) {
            return new Needs(source);
        }

        public Needs[] newArray(int size) {
            return new Needs[size];
        }
    };

    @Override
    public String toString() {
        return "Needs{" +
                "id='" + id + '\'' +
                ", detail='" + detail + '\'' +
                ", price='" + price + '\'' +
                ", tel='" + tel + '\'' +
                ", time='" + time + '\'' +
                ", username='" + username + '\'' +
                ", headimg='" + headimg + '\'' +
                ", schoolname='" + schoolname + '\'' +
                '}';
    }
//
//    @Subscribe(threadMode = Thre)
//    public void onNeedsRefresh()
}
