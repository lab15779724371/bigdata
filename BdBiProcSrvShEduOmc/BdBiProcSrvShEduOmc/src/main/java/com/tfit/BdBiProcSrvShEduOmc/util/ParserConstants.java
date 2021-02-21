package com.tfit.BdBiProcSrvShEduOmc.util;

/**
 * Redis中定义的存储结构Key
 * 目前有String和Hash两种类型
 */
public class ParserConstants {

    public static final int MINUTES_HEARTBEAT_EXPIRE = 7;//消息过期的时间间隔:七分钟
    public static final int SECONDS_HEARTBEAT_EXPIRE = 300;//心跳五分钟过期
    public static final int SECONDS_FLYBEACON_HEARTBEAT_EXPIRE = 28800;//FlyBeacon心跳默认8小时一个周期
    public static final int SECONDS_MSG_EXPIRE = 86400;//下发消息一天过期

    public static final String MSG_GLOBAL_KEY = "MsgGlobalKey";//非Lora特定要求0-255的msgID,都可以用这个获取

    public static final String DATA_POINT_DEFINITION = "datapoint_definition:%s";//存放所有PK的标准数据点定义

    public static final String DID_ZIPVERSION_HASH = "did_zip";
    public static final String TASK_SN_BY_MAC = "taskSN:%s";//taskSN 每个mac一个key,0-255,超过255重置为0,Lora的要求
    public static final String MSG_INFO_STRING = "msgID:%s";//msgID 查找消息的控制信息
    public static final String UUID_DEV_HASH = "uuid_dev";//uuid 查找设备信息
    public static final String LORA_UUID_HASH = "lora_uuid";//uuid 查找设备信息
    public static final String MAC_LORA_HASH = "mac_lora";//mac 查找设备lora信息
    public static final String DID_UUID_HASH = "did_uuid:%s";//Did 和UUID的映射
    public static final String TASK_COUNT = "task_count:%s,%s";//task(mac,taskSN)的下发次数,三次为限,下发到第四次时即更新状态为超时
    public static final String SADM_SCREEN_RESPONSE = "sadm_screen_res:%s,%s";//(mac,msgID)广告机截屏抓取的返回结果

    public static final String MAC_LIVE_STRING = "MAC_live:%s";//探测iot设备是否建立 如beacon

    public static final String BEACON_HEARTBEAT_HASH = "BeaHeartBTab"; //beacon 心跳数据
    public static final String BEACON_DEV_DATA_HASH = "BeaDevDataTab";//beacon 数据上报
    public static final String BEACON_STATUS_DATA_HASH = "BeaDevStatusTab";

    public static final String BOX_HEARTBEAT_HASH = "BoxHeartBTab"; //box 心跳数据
    public static final String BOX_DEV_DATA_HASH = "BoxDevDataTab";//box 数据上报
    public static final String BOX_STATUS_DATA_HASH = "BoxDevStatusTab";

    public static final String AP_HEARTBEAT_HASH = "APHeartBTab"; //AP 心跳数据
    public static final String AP_DEV_DATA_HASH = "APDevDataTab";//AP 数据上报

    public static final String PROBE_HEARTBEAT_HASH = "ProbeHeartBTab"; //probe 心跳数据
    public static final String PROBE_STATUS_DATA_HASH = "ProbeStatusTab";

    public static final String ELECTAG_HEARTBEAT_HASH = "ElecTagHeartBTab"; //电子标签 心跳数据
    public static final String ELECTAG_DEV_DATA_HASH = "ElecTagDevDataTab";//电子标签 数据上报
    public static final String ELECTAG_STATUS_DATA_HASH = "ElecTagStatusTab";//ElecTag设备状态数据

    public static final String ADMACH_HEARTBEAT_HASH = "AdMachHeartBTab"; //广告机 心跳数据
    public static final String ADMACH_DEV_DATA_HASH = "AdMachPlayStatusTab";//AdMach设备播放状态数据
    public static final String ADMACH_STATUS_DATA_HASH = "AdMachStatusTab";//AdMach设备状态数据

    public static final String SADM_HEARTBEAT_HASH = "SadmHeartBTab"; //智能广告机 心跳数据
    public static final String SADM_DEV_DATA_HASH = "SadmDevInfoTab";
    public static final String SADM_STATUS_DATA_HASH = "SadmStatusTab";
    public static final String SADM_PROBE_MAPPING_HASH = "SadmProbeMappingTab";
    public static final String SADM_PROBE_DATA_HASH = "SadmProbeDataTab";

    public static final String PFIM_HEARTBEAT_HASH = "PfimHeartBTab"; //TODO 客流一体机 心跳数据
    public static final String PFIM_DEV_DATA_HASH = "PfimDevInfoTab";
    public static final String PFIM_STATUS_DATA_HASH = "PfimStatusTab";
    public static final String PFIM_PROBE_MAPPING_HASH = "PfimProbeMappingTab";
    public static final String PFIM_PROBE_DATA_HASH = "PfimProbeDataTab";

    public static final String CAMERA_HEARTBEAT_HASH = "CamHeartBTab"; //Camera 心跳数据
    public static final String CAMERA_STATUS_DATA_HASH = "CamStatusTab";
    public static final String CAMERA_DEV_DATA_HASH = "CamInfoDataTab";

    public static final String PRINTER_HEARTBEAT_HASH = "PrtHeartBTab";
    public static final String PRINTER_STATUS_DATA_HASH = "PrtStatusTab";
    public static final String PRINTER_DEV_DATA_HASH = "PrtInfoDataTab";

    public static final String POS_HEARTBEAT_HASH = "PosHeartBTab";
    public static final String POS_DEV_DATA_HASH = "PosInfoDataTab";
    public static final String POS_STATUS_DATA_HASH = "PosStatusTab";

    public static final String BGW_HEARTBEAT_HASH = "BgwHeartBTab";
    public static final String BGW_DEV_DATA_HASH = "BgwDevDataTab";
    public static final String BGW_STATUS_DATA_HASH = "BgwDevStatusTab";

    public static final String AUDIO_HEARTBEAT_HASH = "AudioHeartBTab";
    public static final String AUDIO_DEV_DATA_HASH = "AudioDevDataTab";
    public static final String AUDIO_STATUS_DATA_HASH = "AudioDevStatusTab";

    public static final String CHANNEL_PICTURE_UPDATE = "DevPicUpdate"; //设备接收图片更新接口定义
    public static final String CHANNEL_PLAY_RENEW = "DevPlayRenew"; //设备接收播放更新接口定义

    public static final String CHANNEL_SET_DEV_TIME = "SetDevDateTime"; //设备接收设置日期时间接口定义
    public static final String CHANNEL_GET_DEV_STATUS = "GetDevStatus"; //设备接收状态查询接口定义
    public static final String CHANNEL_SET_DEV_STATUS = "SetDevStatus"; //设备接收状态控制接口定义
    public static final String CHANNEL_QUERY_VERSION = "QueryFwVer"; //设备接收固件版本查询接口定义
    public static final String CHANNEL_SCREENSHOT_GRAB = "DevScrshotGrab"; //设备接收截屏抓取接口定义
    
    public static final String CHANNEL_CONFIG_WHITELIST = "NetworkConfig"; //设备接收白名单配置接口定义

}
