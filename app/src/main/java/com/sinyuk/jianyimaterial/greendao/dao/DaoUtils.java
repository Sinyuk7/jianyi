package com.sinyuk.jianyimaterial.greendao.dao;

/**
 * Created by Sinyuk on 16.2.17.
 */
public class DaoUtils {
    private static UserService userService;
    private static YihuoDetailsService yihuoDetailsService;
    private static SchoolService schoolService;

    private static UserDao getUserDao() {
        return DaoCore.getDaoSession().getUserDao();
    }

    private static SchoolDao getSchoolDao() {
        return DaoCore.getDaoSession().getSchoolDao();
    }

    private static YihuoDetailsDao getYihuoLikeDao() {
        return DaoCore.getDaoSession().getYihuoDetailsDao();
    }

    public static UserService getUserService() {
        if (userService == null) {
            userService = new UserService(getUserDao());
        }
        return userService;
    }

    public static YihuoDetailsService getYihuoDetailsService() {
        if (yihuoDetailsService == null) {
            yihuoDetailsService = new YihuoDetailsService(getYihuoLikeDao());
        }
        return yihuoDetailsService;
    }

    public static SchoolService getSchoolService() {
        if (schoolService == null) {
            schoolService = new SchoolService(getSchoolDao());
        }
        return schoolService;
    }
}
