package com.ew.autofly.entity;

import java.util.List;



public class CloudMissionList {

    private double altitude;
    private String area;
    private double centerLat;
    private double centerLng;
    private String client;
    private CreateUserBean createUser;
    private String createdTime;
    private int droneModelId;
    private boolean fixedAltitude;
    private int flyAngle;
    private int groupId;
    private HandleUserBean handleUser;
    private String id;
    private String imgPath;
    private boolean isBigMission;
    private int missionType;
    private String name;
    private boolean publishing;
    private double resolutionRatio;
    private int routeOverlap;
    private int sideOverlap;
    private int buffer;
    private int baseLineHeight;
    private double speed;
    private int status;
    private String updatedTime;
    private List<?> missionBatchList;
    private List<MissionListBean> missionList;

    public double getAltitude() {
        return altitude;
    }

    public void setAltitude(double altitude) {
        this.altitude = altitude;
    }

    public String getArea() {
        return area;
    }

    public void setArea(String area) {
        this.area = area;
    }

    public double getCenterLat() {
        return centerLat;
    }

    public void setCenterLat(double centerLat) {
        this.centerLat = centerLat;
    }

    public double getCenterLng() {
        return centerLng;
    }

    public void setCenterLng(double centerLng) {
        this.centerLng = centerLng;
    }

    public String getClient() {
        return client;
    }

    public void setClient(String client) {
        this.client = client;
    }

    public CreateUserBean getCreateUser() {
        return createUser;
    }

    public void setCreateUser(CreateUserBean createUser) {
        this.createUser = createUser;
    }

    public String getCreatedTime() {
        return createdTime;
    }

    public void setCreatedTime(String createdTime) {
        this.createdTime = createdTime;
    }

    public int getDroneModelId() {
        return droneModelId;
    }

    public void setDroneModelId(int droneModelId) {
        this.droneModelId = droneModelId;
    }

    public boolean isFixedAltitude() {
        return fixedAltitude;
    }

    public void setFixedAltitude(boolean fixedAltitude) {
        this.fixedAltitude = fixedAltitude;
    }

    public int getFlyAngle() {
        return flyAngle;
    }

    public void setFlyAngle(int flyAngle) {
        this.flyAngle = flyAngle;
    }

    public int getGroupId() {
        return groupId;
    }

    public void setGroupId(int groupId) {
        this.groupId = groupId;
    }

    public HandleUserBean getHandleUser() {
        return handleUser;
    }

    public void setHandleUser(HandleUserBean handleUser) {
        this.handleUser = handleUser;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getImgPath() {
        return imgPath;
    }

    public void setImgPath(String imgPath) {
        this.imgPath = imgPath;
    }

    public boolean isIsBigMission() {
        return isBigMission;
    }

    public void setIsBigMission(boolean isBigMission) {
        this.isBigMission = isBigMission;
    }

    public int getMissionType() {
        return missionType;
    }

    public void setMissionType(int missionType) {
        this.missionType = missionType;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isPublishing() {
        return publishing;
    }

    public void setPublishing(boolean publishing) {
        this.publishing = publishing;
    }

    public double getResolutionRatio() {
        return resolutionRatio;
    }

    public void setResolutionRatio(double resolutionRatio) {
        this.resolutionRatio = resolutionRatio;
    }

    public int getRouteOverlap() {
        return routeOverlap;
    }

    public void setRouteOverlap(int routeOverlap) {
        this.routeOverlap = routeOverlap;
    }

    public int getSideOverlap() {
        return sideOverlap;
    }

    public void setSideOverlap(int sideOverlap) {
        this.sideOverlap = sideOverlap;
    }

    public int getBuffer() {
        return buffer;
    }

    public void setBuffer(int buffer) {
        this.buffer = buffer;
    }

    public int getBaseLineHeight() {
        return baseLineHeight;
    }

    public void setBaseLineHeight(int baseLineHeight) {
        this.baseLineHeight = baseLineHeight;
    }

    public double getSpeed() {
        return speed;
    }

    public void setSpeed(double speed) {
        this.speed = speed;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getUpdatedTime() {
        return updatedTime;
    }

    public void setUpdatedTime(String updatedTime) {
        this.updatedTime = updatedTime;
    }

    public List<?> getMissionBatchList() {
        return missionBatchList;
    }

    public void setMissionBatchList(List<?> missionBatchList) {
        this.missionBatchList = missionBatchList;
    }

    public List<MissionListBean> getMissionList() {
        return missionList;
    }

    public void setMissionList(List<MissionListBean> missionList) {
        this.missionList = missionList;
    }

    public static class CreateUserBean {
        /**
         * age : 26
         * email : 13250775336@qq.com
         * id : 30
         * nickname : 永辉3
         * phone : 13250775336
         * sex : 1
         */

        private int age;
        private String email;
        private int id;
        private String nickname;
        private String phone;
        private int sex;

        public int getAge() {
            return age;
        }

        public void setAge(int age) {
            this.age = age;
        }

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getNickname() {
            return nickname;
        }

        public void setNickname(String nickname) {
            this.nickname = nickname;
        }

        public String getPhone() {
            return phone;
        }

        public void setPhone(String phone) {
            this.phone = phone;
        }

        public int getSex() {
            return sex;
        }

        public void setSex(int sex) {
            this.sex = sex;
        }
    }

    public static class HandleUserBean {
        /**
         * age : 26
         * email : 13250775336@qq.com
         * id : 30
         * nickname : 永辉3
         * phone : 13250775336
         * sex : 1
         */

        private int age;
        private String email;
        private int id;
        private String nickname;
        private String phone;
        private int sex;

        public int getAge() {
            return age;
        }

        public void setAge(int age) {
            this.age = age;
        }

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getNickname() {
            return nickname;
        }

        public void setNickname(String nickname) {
            this.nickname = nickname;
        }

        public String getPhone() {
            return phone;
        }

        public void setPhone(String phone) {
            this.phone = phone;
        }

        public int getSex() {
            return sex;
        }

        public void setSex(int sex) {
            this.sex = sex;
        }
    }

    public static class MissionListBean {

        private double altitude;
        private String createdTime;
        private String endTime;
        private int gimbalAngle;
        private String homePoint;
        private String id;
        private String imgPath;
        private int missionPhotoCount;
        private String name;
        private String parentId;
        private String parentName;
        private double resolutionRatio;
        private double routeOverlap;
        private double sideOverlap;
        private double speed;
        private String startPoint;
        private String startTime;
        private String updatedTime;
        private int workStep;

        public double getAltitude() {
            return altitude;
        }

        public void setAltitude(double altitude) {
            this.altitude = altitude;
        }

        public String getCreatedTime() {
            return createdTime;
        }

        public void setCreatedTime(String createdTime) {
            this.createdTime = createdTime;
        }

        public String getEndTime() {
            return endTime;
        }

        public void setEndTime(String endTime) {
            this.endTime = endTime;
        }

        public int getGimbalAngle() {
            return gimbalAngle;
        }

        public void setGimbalAngle(int gimbalAngle) {
            this.gimbalAngle = gimbalAngle;
        }

        public String getHomePoint() {
            return homePoint;
        }

        public void setHomePoint(String homePoint) {
            this.homePoint = homePoint;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getImgPath() {
            return imgPath;
        }

        public void setImgPath(String imgPath) {
            this.imgPath = imgPath;
        }

        public int getMissionPhotoCount() {
            return missionPhotoCount;
        }

        public void setMissionPhotoCount(int missionPhotoCount) {
            this.missionPhotoCount = missionPhotoCount;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getParentId() {
            return parentId;
        }

        public void setParentId(String parentId) {
            this.parentId = parentId;
        }

        public String getParentName() {
            return parentName;
        }

        public void setParentName(String parentName) {
            this.parentName = parentName;
        }

        public double getResolutionRatio() {
            return resolutionRatio;
        }

        public void setResolutionRatio(double resolutionRatio) {
            this.resolutionRatio = resolutionRatio;
        }

        public double getRouteOverlap() {
            return routeOverlap;
        }

        public void setRouteOverlap(double routeOverlap) {
            this.routeOverlap = routeOverlap;
        }

        public double getSideOverlap() {
            return sideOverlap;
        }

        public void setSideOverlap(double sideOverlap) {
            this.sideOverlap = sideOverlap;
        }

        public double getSpeed() {
            return speed;
        }

        public void setSpeed(double speed) {
            this.speed = speed;
        }

        public String getStartPoint() {
            return startPoint;
        }

        public void setStartPoint(String startPoint) {
            this.startPoint = startPoint;
        }

        public String getStartTime() {
            return startTime;
        }

        public void setStartTime(String startTime) {
            this.startTime = startTime;
        }

        public String getUpdatedTime() {
            return updatedTime;
        }

        public void setUpdatedTime(String updatedTime) {
            this.updatedTime = updatedTime;
        }

        public int getWorkStep() {
            return workStep;
        }

        public void setWorkStep(int workStep) {
            this.workStep = workStep;
        }


    }
}