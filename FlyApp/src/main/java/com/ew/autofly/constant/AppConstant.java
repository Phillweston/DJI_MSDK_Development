package com.ew.autofly.constant;

import android.os.Environment;

import com.ew.autofly.BuildConfig;
import com.ew.autofly.utils.coordinate.LocationCoordinateUtils;
import com.flycloud.autofly.map.MapCoordinateSystem;

import java.io.File;
import java.util.Objects;

public class AppConstant {

  
    public static final int DESIGN_RESOLUTION_WIDTH = 720;

    public static final String DATABASE_NAME = "Geometrys.sdb";
    public static final String DATABASE_GREENDAO_NAME = "Geometrys_Greendao.db";

    public static final String BASE_URL = "http://192.168.1.106/";
    public static final String DOWNLOADTRA_BASEURL = "http://192.168.1.106:8080/tra/";
    public static final String CHECK_UPDATE_URL = BASE_URL + "update.aspx";
    public static final String CHECK_REGISTER_URL = BASE_URL + "checkreg.aspx";

    public static final boolean DEBUG = false;
  

  
    public static final String APP_STORAGE_PATH = BuildConfig.APP_STORAGE_PATH;
    public static final String DEFAULT_SIMULATE_LAT = "26.141162";
    public static final String DEFAULT_SIMULATE_LON = "114.292579";
    public final static int DEFAULT_MAP_SCALE = 10000;
    public final static int DEFAULT_MAP_MAX_SCALE = 800;

  
    public static final int MIN_ALTITUDE = 20;
  
    public static final int MAX_ALTITUDE = 500;

  
    public static final int MAX_ACTION_TIMEOUT = 15;

    public static final int TOWER_MIN_ALTITUDE = -200;
    public static final int TOWER_MAX_ALTITUDE = 500;

    public static final String ROOT_PATH = Environment
            .getExternalStorageDirectory().getAbsolutePath() + File.separator + APP_STORAGE_PATH;
    public static final String LOG_PATH = ROOT_PATH + "/Log";
    public static final String PROP_PATH = ROOT_PATH + "/Prop";

    public static int ScreenWidth = 0;
    public static int ScreenHeight = 0;

    public static final float MAX_FLY_SPEED = 15.0f;

    public static final String BING_MAP_KEY = "AiB-iskkAqseWHXlUCGRGI0xfxvTE6cbMGqTbPrg7huAqxxYy7qWYBBHhDZxZidI";
    public static final String ARCGIS_CLIENT_ID = "o4AGICRmrKDsNHRY";

    public static final int REQUEST_CODE_SAVE_POINT = 0x00001;
    public static final int REQUEST_CODE_SAVE_POLYLINE = 0x00002;
    public static final int REQUEST_CODE_SAVE_POLYGON = 0x00003;

    public static final int REQUEST_CODE_CHOICE_POINT_SYMBOL = 0x00004;
    public static final int REQUEST_CODE_LAYER_MANAGER = 0x00005;
    public static final int REQUEST_CODE_TAKE_PHOTO = 0x00006;
    public static final String ACTION_RESUME_FLY = "action_resume_fly";

    public static final String DIR_MAP = "Maps";
    public static final String DIR_BASE_MAP = "BaseMap";
    public static final String DIR_LOG = "Log";
    public static final String DIR_EXP = "Exports";
    public static final String DIR_GEOMETRYS = "Geometrys";
    public static final String DIR_GEOMETRY_IMPORT = "GeometrysImport";
    public static final String DIR_KML = DIR_GEOMETRY_IMPORT + File.separator + "Kml";
    public static final String DIR_EXCEL = DIR_GEOMETRY_IMPORT + File.separator + "Excel";
    public static final String DIR_TOWER_KML = DIR_GEOMETRY_IMPORT + File.separator +
            "Tower" + File.separator + "Kml";
    public static final String DIR_TOWER_EXCEL = DIR_GEOMETRY_IMPORT + File.separator +
            "Tower" + File.separator + "Excel";
    public static final String DIR_GPS_KML_IMPORT = "GpsKmlImport";
    public static final String DIR_PHOTOS = "Photos";
    public static final String DIR_RECORD = "Records";
    public static final String DIR_SYMBOLS = "Symbols";
    public static final String DIR_SCREENSHOT = "ScreenShot";
    public static final String DIR_CACHE = "Cache";
    public static final String DIR_FTP_CONFIG = "FtpConfig";
    public static final String DIR_MISSION_PHOTO = "MissionPhotos";
    public static final String DIR_DRONE_PHOTO = "DronePhotos";
    public static final String DIR_MAP_CACHE = DIR_CACHE + File.separator + "Map";
    public static final String DIR_GOOGLE_CACHE = DIR_MAP_CACHE + File.separator + "GoogleCache";
    public static final String DIR_GAODE_CACHE = DIR_MAP_CACHE + File.separator + "GaodeCache";
    public static final String DIR_OPENCYCLE_CACHE = DIR_MAP_CACHE + File.separator + "OpenCycleCache";
    public static final String DIR_FTP = "Ftp";
    public static final String DIR_EXPORT = "Export";


    public static final String DIR_LASER_DATA = "LaserData";
    public static final String DIR_LASER_DATA_CACHE = "LaserData" + File.separator + "Cache";
    public static final String DIR_LASER_DATA_PHOTO = "LaserData" + File.separator + "Photo";
    public static final String DIR_LASER_DATA_TEMPLATE = "LaserData" + File.separator + "Template";
    public static final String DIR_LASER_DATA_EXPORT = "LaserData" + File.separator + "Export";
    public static final String DIR_EXPORT_FLIGHTRECORD = DIR_EXPORT + File.separator + "FlightRecord";
    public static final String DIR_EXPORT_IMAGERECORD_FINEPATROL
            = DIR_EXPORT + File.separator + "ImageRecord" + File.separator + "FinePatrol";
    public static final String DIR_FTP_EXPORT = DIR_FTP
            + File.separator + DIR_EXPORT + File.separator;
    public static final String DIR_FTP_EXPORT_TOWER_KML = DIR_FTP_EXPORT + "Tower" + File.separator + "Kml";
    public static final String DIR_FTP_EXPORT_TOWER_EXCEL = DIR_FTP_EXPORT + "Tower" + File.separator + "Excel";
    public static final String DIR_FTP_EXPORT_KML = DIR_FTP_EXPORT + "Kml";
    public static final String DIR_FTP_EXPORT_EXCEL = DIR_FTP_EXPORT + "Excel";
    
    public static final String[] DIRECTORIES = new String[]{
            DIR_MAP, DIR_BASE_MAP, DIR_EXP, DIR_GEOMETRYS, DIR_GEOMETRY_IMPORT,
            DIR_KML, DIR_EXCEL, DIR_TOWER_KML, DIR_TOWER_EXCEL, DIR_GPS_KML_IMPORT,
            DIR_PHOTOS, DIR_RECORD, DIR_SYMBOLS, DIR_SCREENSHOT, DIR_CACHE,
            DIR_MISSION_PHOTO, DIR_DRONE_PHOTO, DIR_GOOGLE_CACHE, DIR_GAODE_CACHE, DIR_OPENCYCLE_CACHE,
            DIR_FTP_EXPORT_TOWER_KML, DIR_FTP_EXPORT_TOWER_EXCEL, DIR_FTP_EXPORT_KML, DIR_FTP_EXPORT_EXCEL,
            DIR_EXPORT, DIR_EXPORT_IMAGERECORD_FINEPATROL, DIR_EXPORT_FLIGHTRECORD,
            DIR_LASER_DATA, DIR_LASER_DATA_CACHE, DIR_LASER_DATA_EXPORT, DIR_LASER_DATA_TEMPLATE};

    public static class DIR_MISSION {

        public static final String ROOT = "Mission";
        public static final String DATA = "Data";
        public static final String SCREENSHOT = "ScreenShot";
        public static final String RIVER_PATROL = "RiverPatrol";
        public static final String RIVER_PATROL_DATA = ROOT + File.separator + RIVER_PATROL + File.separator + DATA;
        public static final String RIVER_PATROL_SCREENSHOT = ROOT + File.separator + RIVER_PATROL + File.separator + SCREENSHOT;

        public static final String SUBSTATION_PATROL = "SubstationPatrol";
        public static final String SUBSTATION_PATROL_DATA = ROOT + File.separator + SUBSTATION_PATROL + File.separator + DATA;
        public static final String SUBSTATION_PATROL_SCENE = ROOT + File.separator + SUBSTATION_PATROL + File.separator + "Scene";
        public static final String SUBSTATION_PATROL_SCREENSHOT = ROOT + File.separator + SUBSTATION_PATROL + File.separator + SCREENSHOT;


        public static final String PATROL_JOB_RAW = AppConstant.ROOT_PATH + File.separator + ROOT
                + File.separator + "PatrolJob" + File.separator + "raw";
        public static final String PATROL_JOB_UZIP = AppConstant.ROOT_PATH + File.separator + ROOT
                + File.separator + "PatrolJob" + File.separator + "uzip";
        public static final String PATROL_JOB_IMAGE = AppConstant.ROOT_PATH + File.separator + ROOT
                + File.separator + "PatrolJob" + File.separator + "image";

        /**
         * 获取巡检任务文件下载的源地址
         *
         * @param jobId
         * @return
         */
        public static String getPatrolJobRawPath(String jobId) {
            return AppConstant.DIR_MISSION.PATROL_JOB_RAW + File.separator + jobId;
        }

        /**
         * 文件解压地址
         *
         * @return
         */
        public static String getPatrolJobUZipPath(String jobId) {
            return AppConstant.DIR_MISSION.PATROL_JOB_UZIP + File.separator + jobId;

        }

        public static final String[] DIRECTORIES = new String[]{RIVER_PATROL_DATA, RIVER_PATROL_SCREENSHOT,
                SUBSTATION_PATROL, SUBSTATION_PATROL_DATA, SUBSTATION_PATROL_SCREENSHOT, SUBSTATION_PATROL_SCENE};
    }


    public static final double BING_INIT_LON = 113.292575;
    public static final double BING_INIT_LAT = 23.14116389;
  
  
    public static final String ECHO_POINT = "ECHO_POINT";
    public static final String ECHO_POLYLINE = "ECHO_POLYLINE";
    public static final String ECHO_POLYGON = "ECHO_POLYGON";
    public static final String ECHO_KMLS = "ECHO_KMLS";
    public static final String ECHO_ANNO = "ECHO_ANNO";

  
    public static final String EDIT_TAG = "EDIT_TAG";
    public static final String EDIT_TYPE = "EDIT_TYPE";
    public static final String EDIT_GID = "EDIT_GID";
    public static final String EDIT_UID = "EDIT_UID";
    public static final String EDIT_NAME = "EDIT_NAME";
    public static final String EDIT_GEOMETRY = "EDIT_GEOMETRY";
    public static final String EDIT_SYMBOL_POINT = "EDIT_SYMBOL_POINT";
    public static final String EDIT_SYMBOL_LINE_WIDTH = "EDIT_SYMBOL_LINE_WIDTH";
    public static final String EDIT_SYMBOL_LINE_COLOR = "EDIT_SYMBOL_LINE_COLOR";
    public static final String EDIT_SYMBOL_POLYGON_FILL_COLOR = "EDIT_SYMBOL_POLYGON_FILL_COLOR";
    public static final String EDIT_IS_BING_SHOW = "EDIT_IS_BING_SHOW";

  
    public static final String PREF_MAP_TYPE = "pref_1";
    public static final String PREF_DOWNLOAD_IMAGE = "pref_2";
    public static final String PREF_UPLOAD_IMAGE = "pref_3";
    public static final String PREF_MODE = "pref_4";
    public static final String PREF_SIMULATOR = "pref_5";
    public static final String PREF_SIMULATE_LATITUDE = "pref_6";
    public static final String PREF_SIMULATE_LONGITUDE = "pref_7";
    public static final String PREF_AIRCRAFT_MODEL = "pref_8";
    public static final String PREF_EXPIRE_DATE = "pref_9";
    public static final String PREF_CAMERA_NAME = "pref_10";
    public static final String PREF_MODE_CODE_FROM_SERVER = "pref_11";
    public static final String PREF_GOHOME_HEIGHT = "pref_12";
    public static final String PREF_MAX_FLY_HEIGHT = "pref_13";
    public static final String PREF_PSDK_ENABLE = "pref_14";

  
    public static final String BROADCAST_DJI_PRODUCT_CONNECTION_CHANGE = "gdas_gig_autofly_connection_change";

  
    public static final String BROADCAST_MODE_CHANGE = "BROADCAST_MODE_CHANGE";

    public static final String BROADCAST_STITCH_MONITOR = "BROADCAST_STITCH_MONITOR";

    public static final String BROADCAST_SEQUOIA_MONITOR = "BROADCAST_SEQUOIA_MONITOR";


    public static final String BROADCAST_CUSTOM_BUTTON_CLICKED = "BROADCAST_CUSTOM_BUTTON_CLICKED";


    public static final String BROADCAST_INTENT_CUSTOM_BUTTON_TAG = "BROADCAST_INTENT_CUSTOM_BUTTON_TAG";

    public static final String BROADCAST_UPLOAD_MISSION = "BROADCAST_UPLOAD_MISSION";
    public static final String BROADCAST_UPLOAD_MISSION_ERROR = "BROADCAST_UPLOAD_MISSION_ERROR";
    public static final String BROADCAST_UPLOAD_MISSION_PROGRESS = "BROADCAST_UPLOAD_MISSION_PROGRESS";

  
    public static final String BOX_IP = "192.168.191.1";

  
    public static final String IMU_STATE = "IMU_STATE";

  
    public static final String BAIDU_KEY = "LKmIRUAGMc0REcOoNgxn4cG2";

  
    public static final String BAIDU_CODE = "08:49:0D:23:26:BD:73:F7:A4:9B:3E:32:07:2E:0C:CD:FC:2F:EE:F1;baidumapsdk.demo";

  
    public static final String BAIDU_URL = "http://api.map.baidu.com/geocoder/v2/";

  
    public static final String DEM_LIST = "DEM_LIST";

  
    public static final String STORAGE_LOCATION = "storage_location";

  
    public static final String WX_APP_ID = "wx9b44962f3b4bedcd";

  
    public enum FlightAction {
        StartTask,
        PauseTask,
        ResumeTask,
        GoHomeTask,
        Unknown
    }

    public enum OperateAction {
        UploadingWayPoint,
        SecurityCheck,
        ExecuteTask,
        FinishTask,
        GoHome,
        Unknown //默认状态，当切到非F档时，转换为Unknown
    }

  
    public enum CoordinateType {
        GPS84,
        GCJ02,
        BJ54,
        MERCATOR
    }

    public enum MapType {
        Google,
        Gaode
    }

    public enum OperationMode {
        LinePatrolVideo,//线状调查,拍摄视频
        LinePatrolPhoto,//线状调查,定时拍照
        PositiveImage,//正摄影像
        TiltImage,//倾斜影像
        AutoFullImage, //自动360全景
        ManualFullImage, //手动360全景
    }

    public enum RiverMissionMode {
        RiverPatrolStudy,
        RiverPatrolMode,
        RiverPatrolManual
    }

    public static final int RETURN_MODE_STRAIGHT = 0;
    public static final int RETURN_MODE_ORIGIN = 1;
    public static final int MIDWAY_RECODE_VIDEO = 0;
    public static final int START_RECODE_VIDEO = 1;
    public static final int ACTION_MODE_VIDEO = 0;
    public static final int ACTION_MODE_PHOTO = 1;
    public static final int FINE_PATROL_MODE_STUDY = 0;
    public static final int FINE_PATROL_MODE_PATROL = 1;
    public static final int FINE_PATROL_MODE_AUTO = 0;
    public static final int FINE_PATROL_MODE_MANUAL = 1;
    public static final int RIVER_PATROL_MODE_STUDY = 0;
    public static final int RIVER_PATROL_MODE_PATROL = 1;
    public static final int RIVER_PATROL_MODE_MANUAL = 2;

    public static final String CHANNEL_NAME_AUTO_FLY = "autofly";
    public static final String CHANNEL_NAME_EASY_FLY = "easyfly";
    public static final String CHANNEL_NAME_DING_XIN = "dingxin";

    
    @Deprecated
    public static MapCoordinateSystem mapCoordinateType = LocationCoordinateUtils.MAP_COORDINATE_SYSTEM;

    
    @Deprecated
    public static final MapCoordinateSystem WGS84_MAP_TYPE = MapCoordinateSystem.WGS84;


    public static final int TURN_MODE_RIGHT = 0x00001;
    public static final int TURN_MODE_LEFT = 0x00002;
}