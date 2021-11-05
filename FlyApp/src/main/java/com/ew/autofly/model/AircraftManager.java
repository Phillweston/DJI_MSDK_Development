package com.ew.autofly.model;

import com.ew.autofly.application.EWApplication;

import dji.common.product.Model;
import dji.sdk.flightcontroller.FlightController;
import dji.sdk.products.Aircraft;


public class AircraftManager {

    
    public static boolean isAircraftConnected() {
        Aircraft aircraft = getAircraft();
        if (aircraft != null) {
            return aircraft.isConnected();
        }
        return false;
    }

    public static Model getModel() {
        Aircraft aircraft = getAircraft();
        if (aircraft != null) {
            return aircraft.getModel();
        }

        return Model.UNKNOWN_AIRCRAFT;
    }

    /**
     * 是否为RTK飞机
     *
     * @return
     */
    public static boolean isRtkAircraft() {
        Model model = getModel();
        if (model == null) {
            return false;
        }
        return model == Model.PHANTOM_4_RTK || model == Model.MATRICE_210_RTK
                || model == Model.MATRICE_210_RTK_V2;

    }

    /**
     * 是否为双天线RTK飞机
     *
     * @return
     */
    public static boolean isDualAntennaRtkAircraft() {
        Model model = getModel();
        if (model == null) {
            return false;
        }
        return model == Model.MATRICE_210_RTK || model == Model.MATRICE_210_RTK_V2;
    }

    /**
     * 是否为P4R飞机
     *
     * @return
     */
    public static boolean isP4R() {
        Model model = getModel();
        if (model == null) {
            return false;
        }
        return model == Model.PHANTOM_4_RTK;
    }

    /**
     * 是否为M210V2系列
     *
     * @return
     */
    public static boolean isM200V2Series() {
        Model model = AircraftManager.getModel();
        return model == Model.MATRICE_200_V2 || model == Model.MATRICE_210_V2 || model == Model.MATRICE_210_RTK_V2;
    }

    /**
     * 是否为M210V2系列
     *
     * @return
     */
    public static boolean isM200Series() {
        Model model = AircraftManager.getModel();
        return model == Model.MATRICE_210 || model == Model.MATRICE_200 || model == Model.MATRICE_210_RTK;
    }

    /**
     * 只有飞控
     *
     * @return
     */
    public static boolean isOnlyFlightController() {
        Model model = AircraftManager.getModel();
        return model == Model.A3 || model == Model.N3;
    }

    /**
     * 是否支持网络RTK设置
     *
     * @return
     */
    public static boolean isSupportNetWorkRtkSetting() {
        Model model = getModel();
        if (model == null) {
            return false;
        }
        return model == Model.PHANTOM_4_RTK || model == Model.MATRICE_210_RTK_V2;
    }

    /**
     * 是否支持机载AI
     *
     * @return
     */
    public static boolean isSupportAIOnBoard() {
        Model model = getModel();
        if (model == null) {
            return false;
        }
        return model == Model.MATRICE_210 || model == Model.MATRICE_210_V2
                || model == Model.MATRICE_210_RTK || model == Model.MATRICE_210_RTK_V2;
    }

    /**
     * 是否为模拟模式
     *
     * @return
     */
    public static boolean isSimulatorStart() {
        FlightController flightController = getFlightController();
        if (flightController != null && flightController.getSimulator() != null) {
            return flightController.getSimulator().isSimulatorActive();
        }

        return false;
    }

    public static FlightController getFlightController() {
        FlightController flightController = null;
        Aircraft aircraft = getAircraft();
        if (aircraft != null) {
            flightController = aircraft.getFlightController();
        }
        return flightController;
    }

    public static Aircraft getAircraft() {
        return EWApplication.getAircraftInstance();
    }
}
