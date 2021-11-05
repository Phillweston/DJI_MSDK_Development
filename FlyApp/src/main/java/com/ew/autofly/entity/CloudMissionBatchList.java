package com.ew.autofly.entity;

import java.util.List;



public class CloudMissionBatchList {

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

        private double altitude;
        private double centerLat;
        private double centerLng;
        private String client;
        private CreateUserBean createUser;
        private String createdTime;
        private int droneModelId;
        private int flyAngle;
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
        private String status;
        private int buffer;

        public double getAltitude() {
            return altitude;
        }

        public void setAltitude(double altitude) {
            this.altitude = altitude;
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

        public int getFlyAngle() {
            return flyAngle;
        }

        public void setFlyAngle(int flyAngle) {
            this.flyAngle = flyAngle;
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

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }

        public int getBuffer() {
            return buffer;
        }

        public void setBuffer(int buffer) {
            this.buffer = buffer;
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
    }
}