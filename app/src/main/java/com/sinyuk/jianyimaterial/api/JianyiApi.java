package com.sinyuk.jianyimaterial.api;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.util.HashMap;

/**
 * Created by Sinyuk on 15.12.28.
 */
public class JianyiApi {
    public static final String JIANYI = "http://wx.i-jianyi.com";
    public static final String BASIC_AUTHOR_ACCOUNT = "1202072324";
    public static final String BASIC_AUTHOR_PASSWORD = "1202072322";


    public static final String GOODS = JIANYI + "/port/goods";
    private static final String NEEDS = JIANYI + "/port/needs";
    private static final String SIGN = JIANYI + "/port/sign";
    private static final String ORDER_DESC = "&order=time_desc";
    private static final String ORDER_ASC = "&order=time_asc";
    private static final String SHOW_USER = JIANYI + "/port/user/show/";
    private static final String IMAGE_UPLOAD = JIANYI + "/port/resource/imgUpload";
    private static final String POST_FEED = JIANYI + "/port/goods/create";
    private static final String SCHOOLS = JIANYI + "/port/school";
    private static final String BANNER = JIANYI + "/port/img/index";

    public static String fetchYihuoProfile(@Nullable int pageIndex) {
        return GOODS + "?title=all" + "&page=" + pageIndex + ORDER_DESC;
    }

    public static String filterYihuoProfile(@Nullable int pageIndex) {
        return GOODS + "?page=" + pageIndex;

    }


    // 大类
    public static String goodsBySort(@Nullable int pageIndex, String parentSort) {
        return GOODS + "?page=" + pageIndex + "&title=" + parentSort;
    }


    public static String shotUrl(@Nullable String uri) {
        return JIANYI + uri;
    }

    public static String yihuoDetails(String id) {
        return GOODS + "/show/" + id;
    }


    public static String goodsByTitle(@NonNull String title, @NonNull int pageIndex) {
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
        return JIANYI + "/port/user/update";
    }

    public static String register() {
        return JIANYI + "/port/sign/register";
    }

    public static String search(int pageIndex, String query) {
        return JIANYI + "/port/goods/search?page=" + pageIndex + "&content=" + query;
    }

    public static String banners() {
        return BANNER;
    }

    public static String goodsByUser(String uid, int pageIndex) {
        return GOODS + "/sellManage?user_id=" + uid + "&page=" + pageIndex;
    }

    public static String goodsBySchool(String schoolIndex, int pageIndex) {
        return GOODS + "?title=all" + "&school=" + schoolIndex + "&page=" + pageIndex;
    }

    public static class ParamsBuilder {
        private HashMap<String, String> mBuilder = new HashMap<>();
        private boolean hasChildSort;

        public ParamsBuilder() {
            mBuilder.put("title", "all");
            hasChildSort = false;
        }

        public ParamsBuilder(String title) {
            mBuilder.put("title", title);
            hasChildSort = true;
        }

        public ParamsBuilder addSchool(int index) {
            addParam("school", String.valueOf(index));
            return this;
        }

        public ParamsBuilder addSort(String sort) {
            if (hasChildSort) { addParam("sort", sort); }
            return this;
        }

        public ParamsBuilder addTimeOrder(boolean isDesc) {
            final String order = isDesc ? "time_desc" : "time_asc";
            mBuilder.remove("order");
            addParam("order", order);
            return this;
        }

        public ParamsBuilder addPriceOrder(boolean isAsc) {
            final String order = isAsc ? "price_asc" : "price_desc";
            mBuilder.remove("order");
            addParam("order", order);
            return this;
        }

        private void addParam(String key, String value) {
            mBuilder.put(key, value);
        }

        public HashMap<String, String> getParams() {
            return mBuilder;
        }

    }
}
