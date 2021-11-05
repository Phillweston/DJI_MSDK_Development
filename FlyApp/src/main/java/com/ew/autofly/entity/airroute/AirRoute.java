package com.ew.autofly.entity.airroute;

import com.ew.autofly.db.entity.FinePatrolWayPointDetail;

import java.io.Serializable;
import java.util.List;



public class AirRoute extends BaseAirRoute {

    private List<FinePatrolWayPointDetail> waypointList;

    public List<FinePatrolWayPointDetail> getWaypointList() {
        return waypointList;
    }

    public void setWaypointList(List<FinePatrolWayPointDetail> waypointList) {
        this.waypointList = waypointList;
    }
}
