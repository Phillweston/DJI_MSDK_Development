package com.ew.autofly.entity;

import java.io.Serializable;
import java.util.List;



public class KmlInfo implements Serializable{

    /**
     * nowPage : 1
     * rows : [{"createdTime":"2018-04-28 12:38:22","id":64,"lastestUsedTime":"2018-04-28 12:38:22","name":"kml1","path":"http://www.iflyer360.com/cloudManager/staticData/kml/c42bf0b0-9c8a-4e2a-b27a-825db26e3bd2.kml","uploadUserId":30},{"createdTime":"2018-05-02 09:57:06","id":65,"lastestUsedTime":"2018-05-02 09:57:06","name":"kml2","path":"http://www.iflyer360.com/cloudManager/staticData/kml/9878e30c-30c8-4ce6-a997-63134116f2fa.kml","uploadUserId":30}]
     * total : 2
     * totalPage : 1
     */

    private int nowPage;
    private int total;
    private int totalPage;
    private List<RowsBean> rows;

    public int getNowPage() {
        return nowPage;
    }

    public void setNowPage(int nowPage) {
        this.nowPage = nowPage;
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public int getTotalPage() {
        return totalPage;
    }

    public void setTotalPage(int totalPage) {
        this.totalPage = totalPage;
    }

    public List<RowsBean> getRows() {
        return rows;
    }

    public void setRows(List<RowsBean> rows) {
        this.rows = rows;
    }

    public static class RowsBean {
        /**
         * createdTime : 2018-04-28 12:38:22
         * id : 64
         * lastestUsedTime : 2018-04-28 12:38:22
         * name : kml1
         * path : http://www.iflyer360.com/cloudManager/staticData/kml/c42bf0b0-9c8a-4e2a-b27a-825db26e3bd2.kml
         * uploadUserId : 30
         */

        private String createdTime;
        private int id;
        private String lastestUsedTime;
        private String name;
        private String path;
        private int uploadUserId;

        public String getCreatedTime() {
            return createdTime;
        }

        public void setCreatedTime(String createdTime) {
            this.createdTime = createdTime;
        }

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getLastestUsedTime() {
            return lastestUsedTime;
        }

        public void setLastestUsedTime(String lastestUsedTime) {
            this.lastestUsedTime = lastestUsedTime;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getPath() {
            return path;
        }

        public void setPath(String path) {
            this.path = path;
        }

        public int getUploadUserId() {
            return uploadUserId;
        }

        public void setUploadUserId(int uploadUserId) {
            this.uploadUserId = uploadUserId;
        }
    }
}
