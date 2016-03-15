package com.sinyuk.jianyimaterial.api;

import java.util.List;

/**
 * Created by Sinyuk on 16.2.8.
 */
public class Show {

    /**
     * status : 1
     * data : {"id":"2803","name":"可复美面膜","detail":"祛痘","title":"个人护理","price":"83.0","tel":"13588004015","sort":"美妆护肤","del":"0","top":null,"time":"2016-01-06 21:26:44","uid":"889","way":null,"reason":null,"viewcount":"1","x":"30.32253","y":"120.3417","oldprice":null,"pic":"/uploads/goods/poster_568d161524e27.jpg","pics":[{"id":"2741","pic":"/uploads/goods/poster_568d161524e27.jpg","gid":"2803","del":"0"},{"id":"2742","pic":"/uploads/goods/poster_568d161546fcd.jpg","gid":"2803","del":"0"}]}
     * code : 2001
     */

    public int status;
    /**
     * id : 2803
     * name : 可复美面膜
     * detail : 祛痘
     * title : 个人护理
     * price : 83.0
     * tel : 13588004015
     * sort : 美妆护肤
     * del : 0
     * top : null
     * time : 2016-01-06 21:26:44
     * uid : 889
     * way : null
     * reason : null
     * viewcount : 1
     * x : 30.32253
     * y : 120.3417
     * oldprice : null
     * pic : /uploads/goods/poster_568d161524e27.jpg
     * pics : [{"id":"2741","pic":"/uploads/goods/poster_568d161524e27.jpg","gid":"2803","del":"0"},{"id":"2742","pic":"/uploads/goods/poster_568d161546fcd.jpg","gid":"2803","del":"0"}]
     */

    public Data data;
    public int code;

    public int getStatus() {
        return status;
    }

    public Data getData() {
        return data;
    }

    public int getCode() {
        return code;
    }

    public static class Data {
        public String id;
        public String name;
        public String detail;
        public String title;
        public String price;
        public String tel;
        public String sort;
        public String del;
        public String top;
        public String time;
        public String uid;
        public String way;
        public String reason;
        public String viewcount;
        public String x;
        public String y;
        public String oldprice;
        public String pic;
        /**
         * id : 2741
         * pic : /uploads/goods/poster_568d161524e27.jpg
         * gid : 2803
         * del : 0
         */

        public List<Pics> pics;

        public static class Pics {
            public String id;
            public String pic;
            public String gid;
            public String del;
        }
    }

    @Override
    public String toString() {
        return "Show{" +
                "status=" + status +
                ", data=" + data +
                ", code=" + code +
                '}';
    }
}
