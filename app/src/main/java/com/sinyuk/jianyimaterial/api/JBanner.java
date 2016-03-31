package com.sinyuk.jianyimaterial.api;

import java.util.List;

/**
 * Created by Sinyuk on 16.3.31.
 */
public class JBanner {


    /**
     * status : 1
     * data : [{"id":"1","src":"/img/ios/1.jpg","type":"1","del":"0","link":"www.baidu.com"},{"id":"2","src":"/img/ios/2.jpg","type":"1","del":"0","link":"www.baiud.com"},{"id":"3","src":"/img/ios/3.jpg","type":"1","del":"0","link":"www.baidu.com"},{"id":"4","src":"/img/ios/4.jpg","type":"2","del":"0","link":"www.baidu.com"},{"id":"5","src":"/img/ios/4.jpg","type":"2","del":"0","link":"www.baidu.com"},{"id":"6","src":"/img/ios/4.jpg","type":"2","del":"0","link":"www.baidu.com"},{"id":"7","src":"/img/ios/4.jpg","type":"2","del":"0","link":"www.baiud.com"}]
     * code : 2001
     */

    private int status;
    private int code;
    /**
     * id : 1
     * src : /img/ios/1.jpg
     * type : 1
     * del : 0
     * link : www.baidu.com
     */

    private List<Data> data;

    public int getStatus() { return status;}

    public void setStatus(int status) { this.status = status;}

    public int getCode() { return code;}

    public void setCode(int code) { this.code = code;}

    public List<Data> getData() { return data;}

    public void setData(List<Data> data) { this.data = data;}

    public static class Data {
        private String id;
        private String src;
        private String type;
        private String del;
        private String link;

        public String getId() { return id;}

        public void setId(String id) { this.id = id;}

        public String getSrc() { return src;}

        public void setSrc(String src) { this.src = src;}

        public String getType() { return type;}

        public void setType(String type) { this.type = type;}

        public String getDel() { return del;}

        public void setDel(String del) { this.del = del;}

        public String getLink() { return link;}

        public void setLink(String link) { this.link = link;}
    }
}
