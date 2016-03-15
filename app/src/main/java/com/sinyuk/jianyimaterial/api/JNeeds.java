package com.sinyuk.jianyimaterial.api;

import java.util.List;

/**
 * Created by Sinyuk on 16.2.14.
 */
public class JNeeds {

    /**
     * status : 1
     * data : {"first":1,"before":1,"items":[{"id":"79","detail":"Send needs for test.","price":"12","tel":"18768102901","time":"2015-12-16 14:26:44","username":"EvenLam","headimg":null,"schoolname":null},{"id":"78","detail":"Send needs for test!","price":"11","tel":"18768102901","time":"2015-12-16 14:26:13","username":"EvenLam","headimg":null,"schoolname":null},{"id":"56","detail":"我是测试","price":"12","tel":"15757161281","time":"2015-11-30 15:00:52","username":"kel2","headimg":null,"schoolname":"浙江传媒学院-桐乡校区"},{"id":"55","detail":"我是测试","price":"12","tel":"15757161281","time":"2015-11-30 14:55:47","username":"kel2","headimg":null,"schoolname":"浙江传媒学院-桐乡校区"},{"id":"54","detail":"我是测试","price":"12","tel":"15757161281","time":"2015-11-30 14:54:36","username":"kel2","headimg":null,"schoolname":"浙江传媒学院-桐乡校区"},{"id":"53","detail":"啊阿拉拉","price":"12","tel":"15757161281","time":"2015-11-30 14:51:48","username":"kel2","headimg":null,"schoolname":"浙江传媒学院-桐乡校区"},{"id":"48","detail":"垃圾铲！ 大四学姐求购！","price":"5","tel":"15757161389","time":"2015-11-12 22:11:39","username":"乔昔之","headimg":"http://wx.qlogo.cn/mmopen/SIR4tXYdtCzMW3Luh0OvdX0UiaQKI464cVnALVCDvyhdQibibicMt595UKWlzqBn5LXbHIbAEGTynO0SqQW9icOw0w7byAZiavZmc9/0","schoolname":"浙江传媒学院-下沙校区"},{"id":"47","detail":"重新选择的机会","price":"998","tel":"561808","time":"2015-09-18 21:37:45","username":"暖洋洋","headimg":"http://wx.qlogo.cn/mmopen/ajNVdqHZLLARTctscCicdTNTf7OFdl1q5fUS8zTpRUNEnPzaZiaEtgaoanKKzvA2qwOYq3oPSSX4qvrwr9TuthEg/0","schoolname":"浙江传媒学院-下沙校区"}],"next":3,"last":4,"current":2,"total_pages":4,"total_items":26,"limit":8}
     * code : 2001
     */

    public int status;
    /**
     * first : 1
     * before : 1
     * items : [{"id":"79","detail":"Send needs for test.","price":"12","tel":"18768102901","time":"2015-12-16 14:26:44","username":"EvenLam","headimg":null,"schoolname":null},{"id":"78","detail":"Send needs for test!","price":"11","tel":"18768102901","time":"2015-12-16 14:26:13","username":"EvenLam","headimg":null,"schoolname":null},{"id":"56","detail":"我是测试","price":"12","tel":"15757161281","time":"2015-11-30 15:00:52","username":"kel2","headimg":null,"schoolname":"浙江传媒学院-桐乡校区"},{"id":"55","detail":"我是测试","price":"12","tel":"15757161281","time":"2015-11-30 14:55:47","username":"kel2","headimg":null,"schoolname":"浙江传媒学院-桐乡校区"},{"id":"54","detail":"我是测试","price":"12","tel":"15757161281","time":"2015-11-30 14:54:36","username":"kel2","headimg":null,"schoolname":"浙江传媒学院-桐乡校区"},{"id":"53","detail":"啊阿拉拉","price":"12","tel":"15757161281","time":"2015-11-30 14:51:48","username":"kel2","headimg":null,"schoolname":"浙江传媒学院-桐乡校区"},{"id":"48","detail":"垃圾铲！ 大四学姐求购！","price":"5","tel":"15757161389","time":"2015-11-12 22:11:39","username":"乔昔之","headimg":"http://wx.qlogo.cn/mmopen/SIR4tXYdtCzMW3Luh0OvdX0UiaQKI464cVnALVCDvyhdQibibicMt595UKWlzqBn5LXbHIbAEGTynO0SqQW9icOw0w7byAZiavZmc9/0","schoolname":"浙江传媒学院-下沙校区"},{"id":"47","detail":"重新选择的机会","price":"998","tel":"561808","time":"2015-09-18 21:37:45","username":"暖洋洋","headimg":"http://wx.qlogo.cn/mmopen/ajNVdqHZLLARTctscCicdTNTf7OFdl1q5fUS8zTpRUNEnPzaZiaEtgaoanKKzvA2qwOYq3oPSSX4qvrwr9TuthEg/0","schoolname":"浙江传媒学院-下沙校区"}]
     * next : 3
     * last : 4
     * current : 2
     * total_pages : 4
     * total_items : 26
     * limit : 8
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
        public int first;
        public int before;
        public int next;
        public int last;
        public int current;
        public int total_pages;
        public int total_items;
        public int limit;

        public List<Items> getItems() {
            return items;
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

        public int getTotal_pages() {
            return total_pages;
        }

        public int getTotal_items() {
            return total_items;
        }

        public int getLimit() {
            return limit;
        }

        /**
         * id : 79
         * detail : Send needs for test.
         * price : 12
         * tel : 18768102901
         * time : 2015-12-16 14:26:44
         * username : EvenLam
         * headimg : null
         * schoolname : null
         */

        public List<Items> items;

        public static class Items {
            public String id;
            public String detail;
            public String price;
            public String tel;
            public String time;
            public String username;
            public String headimg;
            public String schoolname;
        }
    }


}
