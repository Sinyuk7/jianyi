package com.sinyuk.jianyimaterial.api;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

/**
 * Created by Sinyuk on 15.12.28.
 */
public class JianyiApi {
    public static final String JIANYI = "http://wx.i-jianyi.com";
    public static final String BASIC_AUTHOR_ACCOUNT = "1202072324";
    public static final String BASIC_AUTHOR_PASSWORD = "1202072322";
    private static final String GOODS = JIANYI + "/port/goods";
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

    public static String filterYihuoProfile(@Nullable int pageIndex, String url) {
        return url + "&page=" + pageIndex;

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
        return JIANYI + uri;
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

    public static class YihuoProfileBuilder {
        private StringBuilder mStringBuilder;
        private boolean hasSort = true;

        public YihuoProfileBuilder() {
            mStringBuilder = new StringBuilder(GOODS + "?title=all");
            hasSort = false;
        }

        public YihuoProfileBuilder(String title) {
            mStringBuilder = new StringBuilder(GOODS + "?title=" + title);
        }

        public YihuoProfileBuilder addSchool(int index) {
            addParam("&school=" + index);
            return this;
        }

        public YihuoProfileBuilder addSort(String sort) {
            if (hasSort) { addParam("&sort=" + sort); }
            return this;
        }

        public YihuoProfileBuilder addTimeOrder(boolean isDesc) {
            final String order = isDesc ? "time_desc" : "time_asc";
            addParam("&order=" + order);
            return this;
        }

        public YihuoProfileBuilder addPriceOrder(boolean isAsc) {
            final String order = isAsc ? "price_asc" : "price_desc";
            addParam("&order=" + order);
            return this;
        }

        public YihuoProfileBuilder addPageSize(int size) {
            addParam("&page_size=" + size);
            return this;
        }

        public YihuoProfileBuilder addPageIndex(int index) {
            addParam("&page=" + index);
            return this;
        }


        private void addParam(String s) {
            mStringBuilder.append(s);
        }

        public String getUrl() {
            return mStringBuilder.toString();
        }

    }
}
