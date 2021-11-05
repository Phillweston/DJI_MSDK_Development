package com.ew.autofly.entity;

import java.util.List;



public class CloudMissionUpload {

    /**
     * id : CCCC
     * name : 广州塔
     * missionType : 1
     * flyAngle : 50
     * imgPath : /image/Desert.jpg
     * altitude : 115
     * speed : 8
     * gimbalAngle : 0
     * resolutionRatio : 2.254
     * sideOverlap : 80
     * routeOverlap : 70
     * area : 12612433.912300117 2648898.9567748588,12612254.153490856 2648986.631004216,12612341.827720216 2649166.3898134753,12612521.586529475 2649078.715584118
     * fixedAltitude : 1
     * status : 1
     * client : WEB
     * missionList : [{"id":"1CX","parentId":"CCCC","name":"白云倾斜1","missionType":1,"workStep":1,"startTime":"2017-10-10 10:10:10","endTime":"2017-10-10 10:10:10","imgPath":"/image/Desert.jpg","altitude":115,"speed":8,"gimbalAngle":0,"resolutionRatio":2.254,"sideOverlap":80,"routeOverlap":70,"fixedAltitude":1,"startPoint":"","homePoint":"","missionPhotoCount":52,"createdTime":"2017-10-10 10:10:10","updatedTime":"2017-10-10 10:10:10"},{"id":"D32","parentId":"CCCC","name":"白云倾斜2","missionType":1,"workStep":2,"startTime":"2017-10-10 10:10:10","endTime":"2017-10-10 10:10:10","imgPath":"/image/Desert.jpg","altitude":115,"speed":8,"gimbalAngle":30,"resolutionRatio":2.254,"sideOverlap":80,"routeOverlap":70,"fixedAltitude":1,"startPoint":"","homePoint":"","missionPhotoCount":52,"createdTime":"2017-10-10 10:10:10","updatedTime":"2017-10-10 10:10:10"},{"id":"DCX","parentId":"CCCC","name":"白云倾斜3","missionType":1,"workStep":3,"startTime":"2017-10-10 10:10:10","endTime":"2017-10-10 10:10:10","imgPath":"/image/Desert.jpg","altitude":115,"speed":8,"gimbalAngle":30,"resolutionRatio":2.254,"sideOverlap":80,"routeOverlap":70,"fixedAltitude":1,"startPoint":"","homePoint":"","missionPhotoCount":52,"createdTime":"2017-10-10 10:10:10","updatedTime":"2017-10-10 10:10:10"},{"id":"V23","parentId":"CCCC","name":"白云倾斜4","missionType":1,"workStep":4,"startTime":"2017-10-10 10:10:10","endTime":"2017-10-10 10:10:10","imgPath":"/image/Desert.jpg","altitude":115,"speed":8,"gimbalAngle":30,"resolutionRatio":2.254,"sideOverlap":80,"routeOverlap":70,"fixedAltitude":1,"startPoint":"","homePoint":"","missionPhotoCount":52,"createdTime":"2017-10-10 10:10:10","updatedTime":"2017-10-10 10:10:10"},{"id":"V2T4","parentId":"CCCC","name":"白云倾斜5","missionType":1,"workStep":5,"startTime":"2017-10-10 10:10:10","endTime":"2017-10-10 10:10:10","imgPath":"/image/Desert.jpg","altitude":115,"speed":8,"gimbalAngle":30,"resolutionRatio":2.254,"sideOverlap":80,"routeOverlap":70,"fixedAltitude":1,"startPoint":"","homePoint":"","missionPhotoCount":52,"createdTime":"2017-10-10 10:10:10","updatedTime":"2017-10-10 10:10:10"}]
     */

    private String id;
    private String name;
    private int missionType;
    private int flyAngle;
    private String imgPath;
    private int altitude;
    private int speed;
    private int gimbalAngle;
    private double resolutionRatio;
    private int sideOverlap;
    private int routeOverlap;
    private String area;
    private int fixedAltitude;
    private int status;
    private String client;
    private List<MissionListBean> missionList;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getMissionType() {
        return missionType;
    }

    public void setMissionType(int missionType) {
        this.missionType = missionType;
    }

    public int getFlyAngle() {
        return flyAngle;
    }

    public void setFlyAngle(int flyAngle) {
        this.flyAngle = flyAngle;
    }

    public String getImgPath() {
        return imgPath;
    }

    public void setImgPath(String imgPath) {
        this.imgPath = imgPath;
    }

    public int getAltitude() {
        return altitude;
    }

    public void setAltitude(int altitude) {
        this.altitude = altitude;
    }

    public int getSpeed() {
        return speed;
    }

    public void setSpeed(int speed) {
        this.speed = speed;
    }

    public int getGimbalAngle() {
        return gimbalAngle;
    }

    public void setGimbalAngle(int gimbalAngle) {
        this.gimbalAngle = gimbalAngle;
    }

    public double getResolutionRatio() {
        return resolutionRatio;
    }

    public void setResolutionRatio(double resolutionRatio) {
        this.resolutionRatio = resolutionRatio;
    }

    public int getSideOverlap() {
        return sideOverlap;
    }

    public void setSideOverlap(int sideOverlap) {
        this.sideOverlap = sideOverlap;
    }

    public int getRouteOverlap() {
        return routeOverlap;
    }

    public void setRouteOverlap(int routeOverlap) {
        this.routeOverlap = routeOverlap;
    }

    public String getArea() {
        return area;
    }

    public void setArea(String area) {
        this.area = area;
    }

    public int getFixedAltitude() {
        return fixedAltitude;
    }

    public void setFixedAltitude(int fixedAltitude) {
        this.fixedAltitude = fixedAltitude;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getClient() {
        return client;
    }

    public void setClient(String client) {
        this.client = client;
    }

    public List<MissionListBean> getMissionList() {
        return missionList;
    }

    public void setMissionList(List<MissionListBean> missionList) {
        this.missionList = missionList;
    }

    public static class MissionListBean {
        /**
         * id : 1CX
         * parentId : CCCC
         * name : 白云倾斜1
         * missionType : 1
         * workStep : 1
         * startTime : 2017-10-10 10:10:10
         * endTime : 2017-10-10 10:10:10
         * imgPath : /image/Desert.jpg
         * altitude : 115
         * speed : 8
         * gimbalAngle : 0
         * resolutionRatio : 2.254
         * sideOverlap : 80
         * routeOverlap : 70
         * fixedAltitude : 1
         * startPoint :
         * homePoint :
         * missionPhotoCount : 52
         * createdTime : 2017-10-10 10:10:10
         * updatedTime : 2017-10-10 10:10:10
         */

        private String id;
        private String parentId;
        private String name;
        private int missionType;
        private int workStep;
        private String startTime;
        private String endTime;
        private String imgPath;
        private int altitude;
        private int speed;
        private int gimbalAngle;
        private double resolutionRatio;
        private int sideOverlap;
        private int routeOverlap;
        private int fixedAltitude;
        private String startPoint;
        private String homePoint;
        private int missionPhotoCount;
        private String createdTime;
        private String updatedTime;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getParentId() {
            return parentId;
        }

        public void setParentId(String parentId) {
            this.parentId = parentId;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public int getMissionType() {
            return missionType;
        }

        public void setMissionType(int missionType) {
            this.missionType = missionType;
        }

        public int getWorkStep() {
            return workStep;
        }

        public void setWorkStep(int workStep) {
            this.workStep = workStep;
        }

        public String getStartTime() {
            return startTime;
        }

        public void setStartTime(String startTime) {
            this.startTime = startTime;
        }

        public String getEndTime() {
            return endTime;
        }

        public void setEndTime(String endTime) {
            this.endTime = endTime;
        }

        public String getImgPath() {
            return imgPath;
        }

        public void setImgPath(String imgPath) {
            this.imgPath = imgPath;
        }

        public int getAltitude() {
            return altitude;
        }

        public void setAltitude(int altitude) {
            this.altitude = altitude;
        }

        public int getSpeed() {
            return speed;
        }

        public void setSpeed(int speed) {
            this.speed = speed;
        }

        public int getGimbalAngle() {
            return gimbalAngle;
        }

        public void setGimbalAngle(int gimbalAngle) {
            this.gimbalAngle = gimbalAngle;
        }

        public double getResolutionRatio() {
            return resolutionRatio;
        }

        public void setResolutionRatio(double resolutionRatio) {
            this.resolutionRatio = resolutionRatio;
        }

        public int getSideOverlap() {
            return sideOverlap;
        }

        public void setSideOverlap(int sideOverlap) {
            this.sideOverlap = sideOverlap;
        }

        public int getRouteOverlap() {
            return routeOverlap;
        }

        public void setRouteOverlap(int routeOverlap) {
            this.routeOverlap = routeOverlap;
        }

        public int getFixedAltitude() {
            return fixedAltitude;
        }

        public void setFixedAltitude(int fixedAltitude) {
            this.fixedAltitude = fixedAltitude;
        }

        public String getStartPoint() {
            return startPoint;
        }

        public void setStartPoint(String startPoint) {
            this.startPoint = startPoint;
        }

        public String getHomePoint() {
            return homePoint;
        }

        public void setHomePoint(String homePoint) {
            this.homePoint = homePoint;
        }

        public int getMissionPhotoCount() {
            return missionPhotoCount;
        }

        public void setMissionPhotoCount(int missionPhotoCount) {
            this.missionPhotoCount = missionPhotoCount;
        }

        public String getCreatedTime() {
            return createdTime;
        }

        public void setCreatedTime(String createdTime) {
            this.createdTime = createdTime;
        }

        public String getUpdatedTime() {
            return updatedTime;
        }

        public void setUpdatedTime(String updatedTime) {
            this.updatedTime = updatedTime;
        }
    }
}
