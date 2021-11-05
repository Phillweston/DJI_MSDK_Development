package com.ew.autofly.entity.airroute;

import com.ew.autofly.db.entity.FinePatrolWayPointDetail;

import java.util.List;

/**
 * 多任务(兼容单任务)
 *
 * @description ：
 */
public class MultiAirRoute extends AirRoute {

    private List<List<FinePatrolWayPointDetail>> missions;

    public List<List<FinePatrolWayPointDetail>> getMissions() {
        return missions;
    }

    public void setMissions(List<List<FinePatrolWayPointDetail>> missions) {
        this.missions = missions;
    }

    @Override
    public List<FinePatrolWayPointDetail> getWaypointList() {
        List<FinePatrolWayPointDetail> wayPointDetails = null;
        if (missions != null && !missions.isEmpty()) {
            wayPointDetails = missions.get(0);
        }
        return wayPointDetails;
    }
}
