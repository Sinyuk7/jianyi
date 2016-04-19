package com.sinyuk.jianyimaterial.api;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by Sinyuk on 16.2.14.
 */
public class JNeeds {

    /**
     * status : 1
     * data : {"first":1,"before":1,"needList":[{"id":"48","detail":"垃圾铲！ 大四学姐求购！","price":"5","tel":"15757161389","time":"2015-11-12 22:11:39","username":"乔昔之","headimg":"http://wx.qlogo.cn/mmopen/35AP2EiaInkyNRCZVRib9HxRG6GqmgdFlQosibicy021VWfBhtXhD2C0sziaWRGicsojPuqycFmBBIzEQoRIb4ib1PLGpddPx3QLvIz/0","schoolname":"浙江传媒学院-下沙校区"},{"id":"43","detail":"浙传里的氧适堡半学年的健身卡","price":"200","tel":"17826835489","time":null,"username":"BU","headimg":"http://wx.qlogo.cn/mmopen/SIR4tXYdtCzMW3Luh0OvdbtywiaYib5ROAMc9yK9Rrb7ib0uOUibRZSjd1UAnGuaGw9PiaH51WRWkQ6owqO2rlP9iaW1SxBtBKY414/0","schoolname":"浙江传媒学院-下沙校区"},{"id":"42","detail":"求购女士自行车，普通的就行","price":"50","tel":"15858109121（679121）","time":"2015-09-16 16:34:48","username":"Sara","headimg":"http://wx.qlogo.cn/mmopen/vsACyXq1DBg5r9FFRsP0tic2p3icxaCsQ4HaXyNm2rjKspwCfkEPEC9kZheXsXIibKmwMX7d25NsMJ48FibGhq5KjYOrpAkicUO2N/0","schoolname":"浙江传媒学院-下沙校区"},{"id":"34","detail":"测试","price":"700","tel":"15757161281","time":"2015-09-16 16:34:27","username":null,"headimg":null,"schoolname":null},{"id":"33","detail":"失败了","price":"1","tel":"15757161281","time":"2015-09-16 16:34:24","username":null,"headimg":null,"schoolname":null},{"id":"32","detail":"求购ipone","price":"1500","tel":"15757161389","time":"2015-09-16 16:34:21","username":"乔昔之","headimg":"http://wx.qlogo.cn/mmopen/35AP2EiaInkyNRCZVRib9HxRG6GqmgdFlQosibicy021VWfBhtXhD2C0sziaWRGicsojPuqycFmBBIzEQoRIb4ib1PLGpddPx3QLvIz/0","schoolname":"浙江传媒学院-下沙校区"},{"id":"31","detail":"树洞测试","price":"18","tel":"15757161281","time":"2015-09-16 16:34:18","username":null,"headimg":null,"schoolname":null}],"next":2,"last":2,"current":2,"total_pages":2,"total_items":15,"limit":8}
     * code : 2001
     */

    @SerializedName("status")
    public int status;
    /**
     * first : 1
     * before : 1
     * needList : [{"id":"48","detail":"垃圾铲！ 大四学姐求购！","price":"5","tel":"15757161389","time":"2015-11-12 22:11:39","username":"乔昔之","headimg":"http://wx.qlogo.cn/mmopen/35AP2EiaInkyNRCZVRib9HxRG6GqmgdFlQosibicy021VWfBhtXhD2C0sziaWRGicsojPuqycFmBBIzEQoRIb4ib1PLGpddPx3QLvIz/0","schoolname":"浙江传媒学院-下沙校区"},{"id":"43","detail":"浙传里的氧适堡半学年的健身卡","price":"200","tel":"17826835489","time":null,"username":"BU","headimg":"http://wx.qlogo.cn/mmopen/SIR4tXYdtCzMW3Luh0OvdbtywiaYib5ROAMc9yK9Rrb7ib0uOUibRZSjd1UAnGuaGw9PiaH51WRWkQ6owqO2rlP9iaW1SxBtBKY414/0","schoolname":"浙江传媒学院-下沙校区"},{"id":"42","detail":"求购女士自行车，普通的就行","price":"50","tel":"15858109121（679121）","time":"2015-09-16 16:34:48","username":"Sara","headimg":"http://wx.qlogo.cn/mmopen/vsACyXq1DBg5r9FFRsP0tic2p3icxaCsQ4HaXyNm2rjKspwCfkEPEC9kZheXsXIibKmwMX7d25NsMJ48FibGhq5KjYOrpAkicUO2N/0","schoolname":"浙江传媒学院-下沙校区"},{"id":"34","detail":"测试","price":"700","tel":"15757161281","time":"2015-09-16 16:34:27","username":null,"headimg":null,"schoolname":null},{"id":"33","detail":"失败了","price":"1","tel":"15757161281","time":"2015-09-16 16:34:24","username":null,"headimg":null,"schoolname":null},{"id":"32","detail":"求购ipone","price":"1500","tel":"15757161389","time":"2015-09-16 16:34:21","username":"乔昔之","headimg":"http://wx.qlogo.cn/mmopen/35AP2EiaInkyNRCZVRib9HxRG6GqmgdFlQosibicy021VWfBhtXhD2C0sziaWRGicsojPuqycFmBBIzEQoRIb4ib1PLGpddPx3QLvIz/0","schoolname":"浙江传媒学院-下沙校区"},{"id":"31","detail":"树洞测试","price":"18","tel":"15757161281","time":"2015-09-16 16:34:18","username":null,"headimg":null,"schoolname":null}]
     * next : 2
     * last : 2
     * current : 2
     * total_pages : 2
     * total_items : 15
     * limit : 8
     */

    @SerializedName("data")
    public Data data;
    @SerializedName("code")
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
        @SerializedName("first")
        public int first;
        @SerializedName("before")
        public int before;
        @SerializedName("next")
        public int next;
        @SerializedName("last")
        public int last;
        @SerializedName("current")
        public int current;
        @SerializedName("total_pages")
        public int totalPages;
        @SerializedName("total_items")
        public int totalItems;
        @SerializedName("limit")
        public int limit;
        /**
         * id : 48
         * detail : 垃圾铲！ 大四学姐求购！
         * price : 5
         * tel : 15757161389
         * time : 2015-11-12 22:11:39
         * username : 乔昔之
         * headimg : http://wx.qlogo.cn/mmopen/35AP2EiaInkyNRCZVRib9HxRG6GqmgdFlQosibicy021VWfBhtXhD2C0sziaWRGicsojPuqycFmBBIzEQoRIb4ib1PLGpddPx3QLvIz/0
         * schoolname : 浙江传媒学院-下沙校区
         */

        @SerializedName("items")
        public List<Need> needList;

        public static class Need {
            @SerializedName("id")
            public String id;
            @SerializedName("detail")
            public String detail;
            @SerializedName("price")
            public String price;
            @SerializedName("tel")
            public String tel;
            @SerializedName("time")
            public String time;
            @SerializedName("username")
            public String username;
            @SerializedName("headimg")
            public String avatarUrl;
            @SerializedName("schoolname")
            public String schoolName;

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

            public String getAvatarUrl() {
                return avatarUrl;
            }

            public String getSchoolName() {
                return schoolName;
            }
        }

        public int getFirst() {
            return first;
        }

        public int getBefore() {
            return before;
        }

        public int getNext() {
            return next;
        }

        public int getLast() {
            return last;
        }

        public int getCurrent() {
            return current;
        }

        public int getTotalPages() {
            return totalPages;
        }

        public int getTotalItems() {
            return totalItems;
        }

        public int getLimit() {
            return limit;
        }

        public List<Need> getNeedList() {
            return needList;
        }
    }
}
