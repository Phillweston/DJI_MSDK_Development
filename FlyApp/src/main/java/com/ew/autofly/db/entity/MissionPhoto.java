package com.ew.autofly.db.entity;

import android.text.TextUtils;

import com.esri.core.geometry.Point;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Property;
import org.greenrobot.greendao.annotation.Transient;

import java.io.Serializable;
import java.util.Date;



@Entity(nameInDb = "MissionPhoto2")
public class MissionPhoto implements Serializable{

    @Transient
    private static final long serialVersionUID = 1L;

    @Id(autoincrement = false)
    @Property(nameInDb = "ID")
    private String id;

    @Property(nameInDb = "MISSIONID")
    private String missionId;

    @Property(nameInDb = "PHOTOINDEX")
    private int photoIndex;

    @Property(nameInDb = "PHOTOPATH")
    private String photoPath;

    @Property(nameInDb = "CREATEDATE")
    private Date createDate;

    @Property(nameInDb = "bigPhotoPath")
    private String bigPhotoPath;

    @Property(nameInDb = "geometrys")
    private String geometrys;

    @Transient
    private Point point;

    @Generated(hash = 1799625435)
    public MissionPhoto(String id, String missionId, int photoIndex,
            String photoPath, Date createDate, String bigPhotoPath, String geometrys) {
        this.id = id;
        this.missionId = missionId;
        this.photoIndex = photoIndex;
        this.photoPath = photoPath;
        this.createDate = createDate;
        this.bigPhotoPath = bigPhotoPath;
        this.geometrys = geometrys;
    }

    @Generated(hash = 854102295)
    public MissionPhoto() {
    }

    public String getGeometrys() {
        return this.geometrys;
    }

    public void setGeometrys(String geometrys) {
        this.geometrys = geometrys;
    }

    public String getBigPhotoPath() {
        return this.bigPhotoPath;
    }

    public void setBigPhotoPath(String bigPhotoPath) {
        this.bigPhotoPath = bigPhotoPath;
    }

    public Date getCreateDate() {
        return this.createDate;
    }

    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }

    public String getPhotoPath() {
        return this.photoPath;
    }

    public void setPhotoPath(String photoPath) {
        this.photoPath = photoPath;
    }

    public int getPhotoIndex() {
        return this.photoIndex;
    }

    public void setPhotoIndex(int photoIndex) {
        this.photoIndex = photoIndex;
    }

    public String getMissionId() {
        return this.missionId;
    }

    public void setMissionId(String missionId) {
        this.missionId = missionId;
    }

    public String getId() {
        return this.id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setPoint(Point point){
        this.point=point;
        geometrys=point.getX()+","+point.getY();
    }



    @Override
    public int hashCode() {
        if (getId() != null && !TextUtils.isEmpty(getId())) {
            return getId().hashCode();
        }else {
            return super.hashCode();
        }
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }

        if (o instanceof MissionPhoto) {
            if (getId() != null && !TextUtils.isEmpty(getId())) {
                return getId().equals(((MissionPhoto) o).getId());
            }else {
                return false;
            }
        }else {
            return false;
        }
    }

}
