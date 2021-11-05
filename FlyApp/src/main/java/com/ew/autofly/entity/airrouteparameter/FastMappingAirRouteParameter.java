package com.ew.autofly.entity.airrouteparameter;

import com.ew.autofly.entity.AirRouteParameter;

public class FastMappingAirRouteParameter extends AirRouteParameter {

  
    private boolean isTerrainFollow;

    public FastMappingAirRouteParameter(String cameraName) {
        super(cameraName);
    }

    public boolean isTerrainFollow() {
        return isTerrainFollow;
    }

    public void setTerrainFollow(boolean terrainFollow) {
        isTerrainFollow = terrainFollow;
    }
}
