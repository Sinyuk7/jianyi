package com.sinyuk.jianyimaterial.api;

import java.util.List;

/**
 * Created by Sinyuk on 16.2.27.
 */
public class JSchool {

    /**
     * status : 1
     * data : [{"id":"1","name":"浙江传媒学院-下沙校区","coord":null,"x":"30.3206550215","y":"120.3440017540"},{"id":"2","name":"浙江传媒学院-桐乡校区","coord":null,"x":"30.6484198712","y":"120.5270543382"},{"id":"3","name":"杭州电子科技大学","coord":null,"x":"30.3138824212","y":"120.3427393986"},{"id":"4","name":"浙江理工大学","coord":null,"x":"30.3134798374","y":"120.3529457707"},{"id":"5","name":"杭州师范大学","coord":null,"x":"30.3162067748","y":"120.3947977073"},{"id":"6","name":"浙江工商大学","coord":null,"x":"30.3090475843","y":"120.3889428071"},{"id":"7","name":"浙江财经学院","coord":null,"x":"30.3189197748","y":"120.3940337073"},{"id":"8","name":"浙江金融学院","coord":null,"x":"30.3210530825","y":"120.3836180665"},{"id":"9","name":"浙江育英学院","coord":null,"x":"30.3054224466","y":"120.3525416216"},{"id":"10","name":"浙江经济职业技术学院","coord":null,"x":"30.3082547779","y":"120.3790198281"},{"id":"11","name":"浙江经贸职业技术学院","coord":null,"x":"30.3164199539","y":"120.3824280287"},{"id":"12","name":"杭州职业技术学院","coord":null,"x":"30.3202564822","y":"120.3520403536"},{"id":"13","name":"浙江警官职业学院","coord":null,"x":"30.3129648317","y":"120.3599973135"},{"id":"14","name":"中国计量大学","coord":null,"x":"30.3205759524","y":"120.3616563868"},{"id":"15","name":"浙江水利水电学院","coord":null,"x":"30.3130929285","y":"120.3736933563"},{"id":"16","name":"原湛江师范学院","coord":null,"x":"21.2687450621","y":"110.3435690659"},{"id":"17","name":"广东海洋大学寸金学院","coord":null,"x":"21.2853141739","y":"110.3359277861"},{"id":"18","name":"广东海洋大学","coord":null,"x":"21.1505658344","y":"110.3015007144"}]
     * code : 2001
     */

    public int status;
    public int code;

    public List<Data> getData() {
        return data;
    }

    public int getStatus() {
        return status;
    }

    public int getCode() {
        return code;
    }

    /**
     * id : 1
     * name : 浙江传媒学院-下沙校区
     * coord : null
     * x : 30.3206550215
     * y : 120.3440017540
     */

    public List<Data> data;

    public static class Data {
        public String id;
        public String name;
        public Object coord;
        public String x;
        public String y;

        public String getId() {
            return id;
        }

        public String getName() {
            return name;
        }

        public Object getCoord() {
            return coord;
        }

        public String getX() {
            return x;
        }

        public String getY() {
            return y;
        }
    }
}
