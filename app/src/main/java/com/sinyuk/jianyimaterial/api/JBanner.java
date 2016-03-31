package com.sinyuk.jianyimaterial.api;

/**
 * Created by Sinyuk on 16.3.31.
 */
public class JBanner {
    /**
     * status : 1
     * data : {"id":"3","src":"/img/ios/3.jpg","type":"1","del":"0","link":"www.baidu.com"}
     * code : 2001
     */

    private int status;
    /**
     * id : 3
     * src : /img/ios/3.jpg
     * type : 1
     * del : 0
     * link : www.baidu.com
     */

    private Data data;
    private int code;

    public int getStatus() { return status;}

    public void setStatus(int status) { this.status = status;}

    public Data getData() { return data;}

    public void setData(Data data) { this.data = data;}

    public int getCode() { return code;}

    public void setCode(int code) { this.code = code;}

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
