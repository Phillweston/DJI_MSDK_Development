package com.ew.autofly.model.teaching;

import com.ew.autofly.db.entity.FinePatrolWayPointDetail;
import com.ew.autofly.entity.mission.MissionPointType;

import java.util.List;


public interface IWayPointTeachingPresenter {



    /**
     * 是否正在学习航点
     *
     * @return
     */
    boolean isStudying();

    
    boolean startStudy();

    /**
     * 结束学习
     * @param name 任务名称
     */
    boolean endStudy(String name);

    
    void stopStudy();

    /**
     * 记录拍照航点
     * @param pointType 航点类型 {@link com.ew.autofly.entity.mission.MissionPointType}
     * @return
     */
     boolean addStudyDetailWaypoint(MissionPointType pointType);

    
    List<FinePatrolWayPointDetail> loadMissionData(String missionId);

    /**
     * 检查是否存在任务数据
     * @param missionName
     * @return
     */
    boolean checkIfExistMissionData(String missionName);
}
