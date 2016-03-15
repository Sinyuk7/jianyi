package com.sinyuk.jianyimaterial.api;

/**
 * Created by Sinyuk on 16.2.15.
 */
public class JUser {

    /**
     * status : 1
     * data : {"id":"37","status":null,"name":"乔昔之","lastlogin":null,"lastip":null,"email":null,"openid":"o_PSAs1pp5Iu2HqKFFiySJoZ5o1Q","sex":"2","role_id":"3","realname":null,"province":"福建","city":"厦门","country":"中国","heading":"http://wx.qlogo.cn/mmopen/SIR4tXYdtCzMW3Luh0OvdX0UiaQKI464cVnALVCDvyhdQibibicMt595UKWlzqBn5LXbHIbAEGTynO0SqQW9icOw0w7byAZiavZmc9/0","language":"zh_CN","Gamount":"753","tel":"15757161389","self_words":"哇哈哈哈","edu_id":null,"idcard":null,"self_introduction":"好卖家","school":"1","last_x":"30.32571","last_y":"120.3389","current_school":"1"}
     * code : 2001
     */

    public int status;

    @Override
    public String toString() {
        return "JUser{" +
                "status=" + status +
                ", data=" + data +
                ", code=" + code +
                '}';
    }

    /**
     * id : 37
     * status : null
     * name : 乔昔之
     * lastlogin : null
     * lastip : null
     * email : null
     * openid : o_PSAs1pp5Iu2HqKFFiySJoZ5o1Q
     * sex : 2
     * role_id : 3
     * realname : null
     * province : 福建
     * city : 厦门
     * country : 中国
     * heading : http://wx.qlogo.cn/mmopen/SIR4tXYdtCzMW3Luh0OvdX0UiaQKI464cVnALVCDvyhdQibibicMt595UKWlzqBn5LXbHIbAEGTynO0SqQW9icOw0w7byAZiavZmc9/0
     * language : zh_CN
     * Gamount : 753
     * tel : 15757161389
     * self_words : 哇哈哈哈
     * edu_id : null
     * idcard : null
     * self_introduction : 好卖家
     * school : 1
     * last_x : 30.32571
     * last_y : 120.3389
     * current_school : 1
     */

    public Data data;
    public int code;

    public static class Data {
        public String id;
        public String status;
        public String name;
        public String lastlogin;
        public String lastip;
        public String email;
        public String openid;
        public String sex;
        public String role_id;
        public String realname;
        public String province;
        public String city;
        public String country;
        public String heading;
        public String language;
        public String Gamount;
        public String tel;
        public String self_words;
        public String edu_id;
        public String idcard;
        public String self_introduction;
        public String school;
        public String last_x;
        public String last_y;
        public String current_school;
    }

    public int getStatus() {
        return status;
    }

    public Data getData() {
        return data;
    }

    public int getCode() {
        return code;
    }
}
