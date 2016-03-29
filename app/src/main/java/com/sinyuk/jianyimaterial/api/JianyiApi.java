package com.sinyuk.jianyimaterial.api;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

/**
 * Created by Sinyuk on 15.12.28.
 */
public class JianyiApi {
    public static final String JIANYI = "http://wx.i-jianyi.com";
    private static final String GOODS = "http://wx.i-jianyi.com/port/goods";
    private static final String NEEDS = "http://wx.i-jianyi.com/port/needs";
    private static final String SIGN = "http://wx.i-jianyi.com/port/sign";
    private static final String ORDER_DESC = "&order=time_desc";
    private static final String ORDER_ASC = "&order=time_asc";
    private static final String SHOW_USER = "http://wx.i-jianyi.com/port/user/show/";
    private static final String IMAGE_UPLOAD = "http://wx.i-jianyi.com/port/resource/imgUpload";
    private static final String POST_FEED = "http://wx.i-jianyi.com/port/goods/create";
    private static final String SCHOOLS = "http://wx.i-jianyi.com/port/school";

    public static String yihuoAll(@Nullable int pageIndex) {
        return GOODS +
                "?title=all&sort=all" + "&page=" + pageIndex + ORDER_DESC;

    }

    public static String yihuoAll(@Nullable int pageIndex, boolean reverse) {
        if (reverse)
            return GOODS +
                    "?title=all&sort=all" + "&page=" + pageIndex + ORDER_ASC;
        return yihuoAll(pageIndex);
    }

    // 大类
    public static String yihuoBySort(@Nullable int pageIndex, String parentSort) {
        return "http://wx.i-jianyi.com/port/goods/index" + "?page=" + pageIndex + "&title=" + parentSort;
    }


    public static String yihuoBySort(@Nullable int pageIndex, String parentSort, String subSort) {
        return "http://wx.i-jianyi.com/port/goods/index" +
                "?sort=" + subSort + "&page=" + pageIndex + "&title=" + parentSort;
    }


    public static String shotUrl(@Nullable String uri) {
        return "http://wx.i-jianyi.com" + uri;
    }

    public static String yihuoDetails(String id) {
        return GOODS + "/show/" + id;
    }


    public static String yihuoByTabs(@NonNull String title, @NonNull int pageIndex) {
        return GOODS + ORDER_DESC + "?sort=all" + "&page=" + pageIndex + "&title=" + title;
    }

    public static String needs(@NonNull int pageIndex) {
        return NEEDS + "&page=" + pageIndex;
    }

    public static String login() {
        return SIGN + "/index";
    }


    public static String postNeeds() {
        return NEEDS + "/create";
    }

    public static String userById(String uId) {
        return SHOW_USER + uId;
    }

    public static String uploadImage() {

        return IMAGE_UPLOAD;
    }

    public static String postFeed() {
        return POST_FEED;
    }

    public static String schools() {
        return SCHOOLS;
    }

    public static String updateUser() {
        return "http://wx.i-jianyi.com/port/user/update";
    }

    public static String register() {
        return "http://wx.i-jianyi.com/port/sign/register";
    }

    public static String search(int pageIndex, String query) {
        return "http://wx.i-jianyi.com/port/goods/search?page=" + pageIndex + "&content=" + query;
    }
}
