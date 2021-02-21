package com.tfit.BdBiProcSrvShEduOmc.config;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Workbook;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tfit.BdBiProcSrvShEduOmc.client.HdfsRWClient;
import com.tfit.BdBiProcSrvShEduOmc.dao.domain.TEduBdRoleDo;
import com.tfit.BdBiProcSrvShEduOmc.dao.domain.TEduBdUserDo;
import com.tfit.BdBiProcSrvShEduOmc.dao.domain.TEduBdUserPermDo;
import com.tfit.BdBiProcSrvShEduOmc.dto.IOTHttpRspVO;
import com.tfit.BdBiProcSrvShEduOmc.dto.IOTRspType;
import com.tfit.BdBiProcSrvShEduOmc.dto.RedisKeyValue;
import com.tfit.BdBiProcSrvShEduOmc.dto.RedisKeyValueDTO;
import com.tfit.BdBiProcSrvShEduOmc.dto.user.UserDataPermInfoDTO;
import com.tfit.BdBiProcSrvShEduOmc.service.Db1Service;
import com.tfit.BdBiProcSrvShEduOmc.service.Db2Service;
import com.tfit.BdBiProcSrvShEduOmc.util.BCDTimeUtil;
import com.tfit.BdBiProcSrvShEduOmc.util.FileWRCommSys;
import com.tfit.BdBiProcSrvShEduOmc.util.FtpUtil;
import com.tfit.BdBiProcSrvShEduOmc.util.JsonFormatTool;

public class AppModConfig {
    private static final Logger logger = LogManager.getLogger(AppModConfig.class.getName());

    //消息id
    public static long msgId = 1;

    //最大页大小
    public static int maxPageSize = 64 * 1024;

    @Autowired
    public static ObjectMapper objectMapper = new ObjectMapper();

    //不供餐原因类型（包含其他）
    @SuppressWarnings("serial")
    public static final List<String> noDishReasonType = new ArrayList<String>() {{
        add("春/秋游");
        add("节假日");
        add("运动会");
        add("未排课");
        add("恶劣天气");
        add("其他");
    }};

    //不供餐原因类型（不包含其他）
    @SuppressWarnings("serial")
    public static final List<String> noDishReasonTypeTwo = new ArrayList<String>() {{
        add("春/秋游");
        add("节假日");
        add("运动会");
        add("未排课");
        add("恶劣天气");
    }};

    //初始化学校类型名称与ID映射Map
    @SuppressWarnings("serial")
    public static final Map<String, Integer> schTypeNameToIdMap = new HashMap<String, Integer>() {{
        put("托儿所", 0);
        put("托幼园", 1);
        put("托幼小", 2);
        put("幼儿园", 3);
        put("幼小", 4);
        put("幼小初", 5);
        put("幼小初高", 6);
        put("小学", 7);
        put("初级中学", 8);
        put("高级中学", 9);
        put("完全中学", 10);
        put("九年一贯制学校", 11);
        put("十二年一贯制学校", 12);
        put("职业初中", 13);
        put("中等职业学校", 14);
        put("工读学校", 15);
        put("特殊教育学校", 16);
        put("其他", 17);
    }};
    @SuppressWarnings("serial")
    public static final Map<Integer, String> schTypeIdToNameMap = new LinkedHashMap<Integer, String>() {{
        put(0, "托儿所");
        put(1, "托幼园");
        put(2, "托幼小");
        put(3, "幼儿园");
        put(4, "幼小");
        put(5, "幼小初");
        put(6, "幼小初高");
        put(7, "小学");
        put(8, "初级中学");
        put(9, "高级中学");
        put(10, "完全中学");
        put(11, "九年一贯制学校");
        put(12, "十二年一贯制学校");
        put(13, "职业初中");
        put(14, "中等职业学校");
        put(15, "工读学校");
        put(16, "特殊教育学校");
        put(17, "其他");
    }};

    @SuppressWarnings("serial")
    public static final Map<String, String> schTypeNameToParentTypeNameMap = new HashMap<String, String>() {{
        put("托儿所", "托幼");
        put("托幼园", "托幼");
        put("托幼小", "托幼");
        put("幼儿园", "托幼");
        put("幼小", "托幼");
        put("幼小初", "托幼");
        put("幼小初高", "托幼");
        put("小学", "中小学");
        put("初级中学", "中小学");
        put("高级中学", "中小学");
        put("完全中学", "中小学");
        put("九年一贯制学校", "中小学");
        put("十二年一贯制学校", "中小学");
        put("职业初中", "中小学");
        put("中等职业学校", "中小学");
        put("工读学校", "其他");
        put("特殊教育学校", "其他");
        put("其他", "其他");
    }};

    //初始化经营模式名称与ID映射Map
    @SuppressWarnings("serial")
    public static final Map<String, Integer> optModeNameToIdMap = new HashMap<String, Integer>() {{
        put("自营", 0);
        //put("自营", 1);
        put("托管", 2);
        put("外送", 3);
    }};
    @SuppressWarnings("serial")
    public static final Map<Integer, String> optModeIdToNameMap = new HashMap<Integer, String>() {{
        put(0, "自营");
        //put(1, "自营");
        put(2, "托管");
        put(3, "外送");
    }};

    //初始化是否供餐名称与ID映射Map
    @SuppressWarnings("serial")
    public static final Map<String, Integer> isMealNameToIdMap = new HashMap<String, Integer>() {{
        put("否", 0);
        put("是", 1);
    }};
    @SuppressWarnings("serial")
    public static final Map<Integer, String> isMealIdToNameMap = new HashMap<Integer, String>() {{
        put(0, "否");
        put(1, "是");
    }};

    //初始化是否排菜名称与ID映射Map
    @SuppressWarnings("serial")
    public static final Map<String, Integer> isDishNameToIdMap = new HashMap<String, Integer>() {{
        put("未排菜", 0);
        put("已排菜", 1);
    }};
    @SuppressWarnings("serial")
    public static final Map<Integer, String> isDishIdToNameMap = new HashMap<Integer, String>() {{
        put(0, "未排菜");
        put(1, "已排菜");
    }};

    //初始化区域名称与ID映射Map
    @SuppressWarnings("serial")
    public static final Map<String, String> distNameToIdMap = new HashMap<String, String>() {{
        put("黄浦区", "1");
        put("静安区", "10");
        put("徐汇区", "11");
        put("长宁区", "12");
        put("普陀区", "13");
        put("虹口区", "14");
        put("杨浦区", "15");
        put("闵行区", "16");
        put("嘉定区", "2");
        put("宝山区", "3");
        put("浦东新区", "4");
        put("松江区", "5");
        put("金山区", "6");
        put("青浦区", "7");
        put("奉贤区", "8");
        put("崇明区", "9");
    }};
    @SuppressWarnings("serial")
    public static final Map<String, String> distIdToNameMap = new HashMap<String, String>() {{
        put("1", "黄浦区");
        put("10", "静安区");
        put("11", "徐汇区");
        put("12", "长宁区");
        put("13", "普陀区");
        put("14", "虹口区");
        put("15", "杨浦区");
        put("16", "闵行区");
        put("2", "嘉定区");
        put("3", "宝山区");
        put("4", "浦东新区");
        put("5", "松江区");
        put("6", "金山区");
        put("7", "青浦区");
        put("8", "奉贤区");
        put("9", "崇明区");
    }};

    //初始化区域名称与ID映射Map
    @SuppressWarnings("serial")
    public static final Map<String, Integer> distNameToIdMap2 = new HashMap<String, Integer>() {{
        put("浦东新区", 0);
        put("黄浦区", 1);
        put("静安区", 2);
        put("徐汇区", 3);
        put("长宁区", 4);
        put("普陀区", 5);
        put("虹口区", 6);
        put("杨浦区", 7);
        put("宝山区", 8);
        put("闵行区", 9);
        put("嘉定区", 10);
        put("金山区", 11);
        put("松江区", 12);
        put("青浦区", 13);
        put("奉贤区", 14);
        put("崇明区", 15);
    }};
    @SuppressWarnings("serial")
    public static final Map<Integer, String> distIdToNameMap2 = new HashMap<Integer, String>() {{
        put(0, "浦东新区");
        put(1, "黄浦区");
        put(2, "静安区");
        put(3, "徐汇区");
        put(4, "长宁区");
        put(5, "普陀区");
        put(6, "虹口区");
        put(7, "杨浦区");
        put(8, "宝山区");
        put(9, "闵行区");
        put(10, "嘉定区");
        put(11, "金山区");
        put(12, "松江区");
        put(13, "青浦区");
        put(14, "奉贤区");
        put(15, "崇明区");
    }};

    //初始化配送类型名称与ID映射Map
    @SuppressWarnings("serial")
    public static final Map<String, Integer> dispTypeNameToIdMap = new HashMap<String, Integer>() {{
        put("原料", 0);
        put("成品菜", 1);
    }};
    @SuppressWarnings("serial")
    public static final Map<Integer, String> dispTypeIdToNameMap = new HashMap<Integer, String>() {{
        put(0, "原料");
        put(1, "成品菜");
    }};

    //初始化配送类型名称与ID映射Map(用与综合分析菜品供应明细)
    @SuppressWarnings("serial")
    public static final Map<String, Integer> dispTypeNameToIdForDsihMap = new HashMap<String, Integer>() {{
        put("原料", 1);
        put("成品菜", 2);
    }};
    @SuppressWarnings("serial")
    public static final Map<Integer, String> dispTypeIdToNameForDsihMap = new HashMap<Integer, String>() {{
        put(1, "原料");
        put(2, "成品菜");
    }};

    //初始化配送方式名称与ID映射Map
    @SuppressWarnings("serial")
    public static final Map<String, Integer> dispModeNameToIdMap = new HashMap<String, Integer>() {{
        put("统配", 0);
        put("直配", 1);
    }};
    @SuppressWarnings("serial")
    public static final Map<Integer, String> dispModeIdToNameMap = new HashMap<Integer, String>() {{
        put(0, "统配");
        put(1, "直配");
    }};

    //初始化学校性质名称与ID映射Map
    @SuppressWarnings("serial")
    public static final Map<String, Integer> schPropNameToIdMap = new HashMap<String, Integer>() {{
        put("公办", 0);
        put("民办", 1);
        put("外籍人员子女学校", 2);
        put("其他", 3);
    }};
    @SuppressWarnings("serial")
    public static final Map<Integer, String> schPropIdToNameMap = new HashMap<Integer, String>() {{
        put(0, "公办");
        put(1, "民办");
        put(2, "外籍人员子女学校");
        put(3, "其他");
    }};

    //初始化指派状态名称与ID映射Map
    @SuppressWarnings("serial")
    public static final Map<String, Integer> assignStatusNameToIdMap = new HashMap<String, Integer>() {{
        put("未指派", 0);
        put("已指派", 1);
        put("已取消", 2);
    }};
    @SuppressWarnings("serial")
    public static final Map<Integer, String> assignStatusIdToNameMap = new HashMap<Integer, String>() {{
        put(0, "未指派");
        put(1, "已指派");
        put(2, "已取消");
    }};

    //初始化配送状态名称与ID映射Map
    @SuppressWarnings("serial")
    public static final Map<String, Integer> dispStatusNameToIdMap = new HashMap<String, Integer>() {{
        put("未派送", 0);
        put("配送中", 1);
        put("已配送", 2);
    }};
    @SuppressWarnings("serial")
    public static final Map<Integer, String> dispStatusIdToNameMap = new HashMap<Integer, String>() {{
        put(0, "未派送");
        put(1, "配送中");
        put(2, "已配送");
    }};

    //初始化验收状态名称与ID映射Map
    @SuppressWarnings("serial")
    public static final Map<String, Integer> acceptStatusNameToIdMap = new HashMap<String, Integer>() {{
        put("待验收", 0);
        put("已验收", 1);
    }};
    @SuppressWarnings("serial")
    public static final Map<Integer, String> acceptStatusIdToNameMap = new HashMap<Integer, String>() {{
        put(0, "待验收");
        put(1, "已验收");
    }};

    //初始化发送状态名称与ID映射Map
    @SuppressWarnings("serial")
    public static final Map<String, Integer> sendFlagNameToIdMap = new HashMap<String, Integer>() {{
        put("未发送", 0);
        put("已发送", 1);
    }};
    @SuppressWarnings("serial")
    public static final Map<Integer, String> sendFlagIdToNameMap = new HashMap<Integer, String>() {{
        put(0, "未发送");
        put(1, "已发送");
    }};

    //初始化是否确认名称与ID映射Map
    @SuppressWarnings("serial")
    public static final Map<String, Integer> confirmFlagNameToIdMap = new HashMap<String, Integer>() {{
        put("未确认", 0);
        put("已确认", 1);
    }};
    @SuppressWarnings("serial")
    public static final Map<Integer, String> confirmFlagIdToNameMap = new HashMap<Integer, String>() {{
        put(0, "未确认");
        put(1, "已确认");
    }};

    //初始化餐别名称与ID映射Map
    @SuppressWarnings("serial")
    public static final Map<String, Integer> caterTypeNameToIdMap = new HashMap<String, Integer>() {{
        put("早餐", 0);
        put("午餐", 1);
        put("晚餐", 2);
        put("午点", 3);
        put("早点", 4);
    }};
    @SuppressWarnings("serial")
    public static final Map<Integer, String> caterTypeIdToNameMap = new HashMap<Integer, String>() {{
        put(0, "早餐");
        put(1, "午餐");
        put(2, "晚餐");
        put(3, "午点");
        put(4, "早点");
    }};

    //初始化原料类别名称与ID映射Map
    @SuppressWarnings("serial")
    public static final Map<String, Integer> matCategoryNameToIdMap = new HashMap<String, Integer>() {{
        put("主料", 0);
        put("辅料", 1);
    }};
    @SuppressWarnings("serial")
    public static final Map<Integer, String> matCategoryIdToNameMap = new HashMap<Integer, String>() {{
        put(0, "主料");
        put(1, "辅料");
    }};

    //初始化证件类型名称与ID映射Map
    @SuppressWarnings("serial")
    public static final Map<String, Integer> licTypeNameToIdMap = new HashMap<String, Integer>() {{
        put("食品经营许可证", 0);
        put("营业执照", 1);
        put("健康证", 2);
        put("餐饮服务许可证", 3);
        put("A1证", 4);
        put("A2证", 5);
        put("B证", 6);
        put("C证", 7);
    }};
    @SuppressWarnings("serial")
    public static final Map<Integer, String> licTypeIdToNameMap = new HashMap<Integer, String>() {{
        put(0, "食品经营许可证");
        put(1, "营业执照");
        put(2, "健康证");
        put(3, "餐饮服务许可证");
        put(4, "A1证");
        put(5, "A2证");
        put(6, "B证");
        put(7, "C证");
    }};

    //初始化证件预警审核状态名称与ID映射Map
    @SuppressWarnings("serial")
    public static final Map<String, Integer> licAudStatusNameToIdMap = new HashMap<String, Integer>() {{
        put("未处理", 0);
        put("审核中", 1);
        put("已消除", 2);
        put("已驳回", 3);
    }};
    @SuppressWarnings("serial")
    public static final Map<Integer, String> licAudStatusIdToNameMap = new HashMap<Integer, String>() {{
        put(0, "未处理");
        put(1, "审核中");
        put(2, "已消除");
        put(3, "已驳回");
    }};

    //初始化证件状况名称与ID映射Map
    @SuppressWarnings("serial")
    public static final Map<String, Integer> licStatusNameToIdMap = new HashMap<String, Integer>() {{
        put("逾期", 0);
        put("到期", 1);
    }};
    @SuppressWarnings("serial")
    public static final Map<Integer, String> licStatusIdToNameMap = new HashMap<Integer, String>() {{
        put(0, "逾期");
        put(1, "到期");
    }};

    //初始化总分校标识名称与ID映射Map
    @SuppressWarnings("serial")
    public static final Map<String, Integer> genBraSchNameToIdMap = new HashMap<String, Integer>() {{
        put("-", 0);
        put("总校", 1);
        put("分校", 2);
    }};
    @SuppressWarnings("serial")
    public static final Map<Integer, String> genBraSchIdToNameMap = new HashMap<Integer, String>() {{
        put(0, "-");
        put(1, "总校");
        put(2, "分校");
    }};

    //初始化所属名称与ID映射Map
    @SuppressWarnings("serial")
    public static final Map<String, Integer> subLevelNameToIdMap = new HashMap<String, Integer>() {{
        put("其他", 0);
        put("部属", 1);
        put("市属", 2);
        put("区属", 3);
    }};
    @SuppressWarnings("serial")
    public static final Map<Integer, String> subLevelIdToNameMap = new HashMap<Integer, String>() {{
        put(0, "其他");
        put(1, "部属");
        put(2, "市属");
        put(3, "区属");
    }};

    //初始化其他主管部门0名称与ID映射Map
    @SuppressWarnings("serial")
    public static final Map<String, String> compDepNameToIdMap0 = new HashMap<String, String>() {{
        put("其他", "0");
    }};
    @SuppressWarnings("serial")
    public static final Map<String, String> compDepIdToNameMap0 = new HashMap<String, String>() {{
        put("0", "其他");
    }};

    //初始化部属主管部门1名称与ID映射Map
    @SuppressWarnings("serial")
    public static final Map<String, String> compDepNameToIdMap1 = new HashMap<String, String>() {{
        put("其他", "0");
        put("教育部", "1");
    }};
    @SuppressWarnings("serial")
    public static final Map<String, String> compDepIdToNameMap1 = new HashMap<String, String>() {{
        put("0", "其他");
        put("1", "教育部");
    }};

    //初始化市属主管部门2名称与ID映射Map
    @SuppressWarnings("serial")
    public static final Map<String, String> compDepNameToIdMap2 = new HashMap<String, String>() {{
        put("其他", "0");
        put("市水务局（海洋局）", "1");
        put("市农委", "2");
        put("市交通委", "3");
        put("市科委", "4");
        put("市商务委", "5");
        put("市经信委", "6");
        put("市教委", "7");
    }};
    @SuppressWarnings("serial")
    public static final Map<String, String> compDepIdToNameMap2 = new HashMap<String, String>() {{
        put("0", "其他");
        put("1", "市水务局（海洋局）");
        put("2", "市农委");
        put("3", "市交通委");
        put("4", "市科委");
        put("5", "市商务委");
        put("6", "市经信委");
        put("7", "市教委");
    }};

    //初始化区属主管部门3名称与ID映射Map（内部数据库映射）
    @SuppressWarnings("serial")
    public static Map<String, String> compDepNameToIdMap3bd = new HashMap<String, String>() {{
        put("黄浦区教育局", "e6ee4acf-2c5b-11e6-b1e8-005056a5ed30");
        put("静安区教育局", "e6ee4bd5-2c5b-11e6-b1e8-005056a5ed30");
        put("徐汇区教育局", "e6ee4c4f-2c5b-11e6-b1e8-005056a5ed30");
        put("长宁区教育局", "e6ee4cb2-2c5b-11e6-b1e8-005056a5ed30");
        put("普陀区教育局", "e6ee4d17-2c5b-11e6-b1e8-005056a5ed30");
        put("虹口区教育局", "e6ee4d78-2c5b-11e6-b1e8-005056a5ed30");
        put("杨浦区教育局", "e6ee4dd1-2c5b-11e6-b1e8-005056a5ed30");
        put("闵行区教育局", "e6ee4e3f-2c5b-11e6-b1e8-005056a5ed30");
        put("嘉定区教育局", "e6ee4e97-2c5b-11e6-b1e8-005056a5ed30");
        put("宝山区教育局", "e6ee4eec-2c5b-11e6-b1e8-005056a5ed30");
        put("浦东新区教育局", "e6ee4f43-2c5b-11e6-b1e8-005056a5ed30");
        put("松江区教育局", "e6ee4fa4-2c5b-11e6-b1e8-005056a5ed30");
        put("金山区教育局", "e6ee4ffa-2c5b-11e6-b1e8-005056a5ed30");
        put("青浦区教育局", "e6ee5054-2c5b-11e6-b1e8-005056a5ed30");
        put("奉贤区教育局", "e6ee50ac-2c5b-11e6-b1e8-005056a5ed30");
        put("崇明区教育局", "e6ee5101-2c5b-11e6-b1e8-005056a5ed30");
        put("其他", "0");
    }};
    @SuppressWarnings("serial")
    public static Map<String, String> compDepIdToNameMap3bd = new HashMap<String, String>() {{
        put("e6ee4acf-2c5b-11e6-b1e8-005056a5ed30", "黄浦区教育局");
        put("e6ee4bd5-2c5b-11e6-b1e8-005056a5ed30", "静安区教育局");
        put("e6ee4c4f-2c5b-11e6-b1e8-005056a5ed30", "徐汇区教育局");
        put("e6ee4cb2-2c5b-11e6-b1e8-005056a5ed30", "长宁区教育局");
        put("e6ee4d17-2c5b-11e6-b1e8-005056a5ed30", "普陀区教育局");
        put("e6ee4d78-2c5b-11e6-b1e8-005056a5ed30", "虹口区教育局");
        put("e6ee4dd1-2c5b-11e6-b1e8-005056a5ed30", "杨浦区教育局");
        put("e6ee4e3f-2c5b-11e6-b1e8-005056a5ed30", "闵行区教育局");
        put("e6ee4e97-2c5b-11e6-b1e8-005056a5ed30", "嘉定区教育局");
        put("e6ee4eec-2c5b-11e6-b1e8-005056a5ed30", "宝山区教育局");
        put("e6ee4f43-2c5b-11e6-b1e8-005056a5ed30", "浦东新区教育局");
        put("e6ee4fa4-2c5b-11e6-b1e8-005056a5ed30", "松江区教育局");
        put("e6ee4ffa-2c5b-11e6-b1e8-005056a5ed30", "金山区教育局");
        put("e6ee5054-2c5b-11e6-b1e8-005056a5ed30", "青浦区教育局");
        put("e6ee50ac-2c5b-11e6-b1e8-005056a5ed30", "奉贤区教育局");
        put("e6ee5101-2c5b-11e6-b1e8-005056a5ed30", "崇明区教育局");
        put("0", "其他");
    }};

    //初始化区属主管部门3名称与ID映射Map
    @SuppressWarnings("serial")
    public static Map<String, String> compDepNameToIdMap3 = new HashMap<String, String>() {{
        put("黄浦区教育局", "1");
        put("静安区教育局", "10");
        put("徐汇区教育局", "11");
        put("长宁区教育局", "12");
        put("普陀区教育局", "13");
        put("虹口区教育局", "14");
        put("杨浦区教育局", "15");
        put("闵行区教育局", "16");
        put("嘉定区教育局", "2");
        put("宝山区教育局", "3");
        put("浦东新区教育局", "4");
        put("松江区教育局", "5");
        put("金山区教育局", "6");
        put("青浦区教育局", "7");
        put("奉贤区教育局", "8");
        put("崇明区教育局", "9");
        put("其他", "0");
    }};
    @SuppressWarnings("serial")
    public static Map<String, String> compDepIdToNameMap3 = new HashMap<String, String>() {{
        put("1", "黄浦区教育局");
        put("10", "静安区教育局");
        put("11", "徐汇区教育局");
        put("12", "长宁区教育局");
        put("13", "普陀区教育局");
        put("14", "虹口区教育局");
        put("15", "杨浦区教育局");
        put("16", "闵行区教育局");
        put("2", "嘉定区教育局");
        put("3", "宝山区教育局");
        put("4", "浦东新区教育局");
        put("5", "松江区教育局");
        put("6", "金山区教育局");
        put("7", "青浦区教育局");
        put("8", "奉贤区教育局");
        put("9", "崇明区教育局");
        put("0", "其他");
    }};

    //初始化证件主体名称与ID映射Map
    @SuppressWarnings("serial")
    public static final Map<String, Integer> fblMbNameToIdMap = new HashMap<String, Integer>() {{
        put("自营", 0);
        put("外包", 1);
    }};
    @SuppressWarnings("serial")
    public static final Map<Integer, String> fblMbIdToNameMap = new HashMap<Integer, String>() {{
        put(0, "自营");
        put(1, "外包");
    }};

    //初始化学期设置名称与ID映射Map
    @SuppressWarnings("serial")
    public static final Map<String, Integer> semSetNameToIdMap = new HashMap<String, Integer>() {{
        put("未设置", 0);
        put("已设置", 1);
    }};
    @SuppressWarnings("serial")
    public static final Map<Integer, String> semSetIdToNameMap = new HashMap<Integer, String>() {{
        put(0, "未设置");
        put(1, "已设置");
    }};

    //初始化账号类型名称与ID映射Map
    @SuppressWarnings("serial")
    public static final Map<String, Integer> accountTypeNameToIdMap = new HashMap<String, Integer>() {{
        put("普通账号", 0);
        put("管理员账号", 1);
    }};
    @SuppressWarnings("serial")
    public static final Map<Integer, String> accountTypeIdToNameMap = new HashMap<Integer, String>() {{
        put(0, "普通账号");
        put(1, "管理员账号");
    }};

    //初始化用户状态名称与ID映射Map
    @SuppressWarnings("serial")
    public static final Map<String, Integer> userStatusNameToIdMap = new HashMap<String, Integer>() {{
        put("禁用", 0);
        put("启用", 1);
    }};
    @SuppressWarnings("serial")
    public static final Map<Integer, String> userStatusIdToNameMap = new HashMap<Integer, String>() {{
        put(0, "禁用");
        put(1, "启用");
    }};

    //初始化角色类型名称与ID映射Map
    @SuppressWarnings("serial")
    public static final Map<String, Integer> roleTypeNameToIdMap = new HashMap<String, Integer>() {{
        put("监管部门", 1);
        put("学校", 2);
    }};
    @SuppressWarnings("serial")
    public static final Map<Integer, String> roleTypeIdToNameMap = new HashMap<Integer, String>() {{
        put(1, "监管部门");
        put(2, "学校");
    }};

    //初始化供应商类型名称与ID映射Map
    @SuppressWarnings("serial")
    public static final Map<String, Integer> supplierTypeNameToIdMap = new HashMap<String, Integer>() {{
        put("-", 0);
        put("生产类", 1);
        put("经销类", 2);
    }};
    @SuppressWarnings("serial")
    public static final Map<Integer, String> supplierTypeIdToNameMap = new HashMap<Integer, String>() {{
        put(0, "-");
        put(1, "生产类");
        put(2, "经销类");
    }};

    //初始化食堂安全等级名称与ID映射Map
    @SuppressWarnings("serial")
    public static final Map<String, Integer> cantSaftGradNameToIdMap = new HashMap<String, Integer>() {{
        put("良好", 0);
        put("一般", 1);
        put("较差", 2);
        put("待评价", 11);
    }};
    @SuppressWarnings("serial")
    public static final Map<Integer, String> cantSaftGradIdToNameMap = new HashMap<Integer, String>() {{
        put(0, "良好");
        put(1, "一般");
        put(2, "较差");
        put(11, "待评价");
    }};

    //初始化投诉举报处理状态名称与ID映射Map
    @SuppressWarnings("serial")
    public static final Map<String, Integer> crProcStatusNameToIdMap = new HashMap<String, Integer>() {{
        put("待处理", 0);
        put("已指派", 1);
        put("已办结", 2);
    }};
    @SuppressWarnings("serial")
    public static final Map<Integer, String> crProcStatusIdToNameMap = new HashMap<Integer, String>() {{
        put(0, "待处理");
        put(1, "已指派");
        put(2, "已办结");
    }};

    //初始化选择题候选答案序号名称与ID映射Map
    @SuppressWarnings("serial")
    public static final Map<String, Integer> choiceCandAnsSnNameToIdMap = new HashMap<String, Integer>() {{
        put("A", 0);
        put("B", 1);
        put("C", 2);
        put("D", 3);
        put("E", 4);
        put("F", 5);
        put("G", 6);
        put("H", 7);
        put("I", 8);
    }};
    @SuppressWarnings("serial")
    public static final Map<Integer, String> choiceCandAnsSnIdToNameMap = new HashMap<Integer, String>() {{
        put(0, "A");
        put(1, "B");
        put(2, "C");
        put(3, "D");
        put(4, "E");
        put(5, "F");
        put(6, "G");
        put(7, "H");
        put(8, "I");
    }};

    //消息id小于0判断
    public static void msgIdLessThan0Judge() {
        if (msgId < 0)
            msgId = msgId & 0x7fffffffffffffffL;
        if (msgId == 0)
            msgId = 1;
    }

    //选择排序（由大到小）
    public static void selectSort(int[] a, int[] idxes) {
        int len = a.length;
        for (int i = 0; i < len; i++) { //循环次数
            int value = a[i];
            int position = i;
            for (int j = i + 1; j < len; j++) {  //找到最小的值和位置
                if (a[j] > value) {
                    value = a[j];
                    position = j;
                }
            }
            a[position] = a[i];   //进行交换
            a[i] = value;
            int idx = idxes[position];
            idxes[position] = idxes[i];
            idxes[i] = idx;
        }
    }

    //选择排序（由大到小）
    public static void selectSort(float[] a, int[] idxes) {
        int len = a.length;
        for (int i = 0; i < len; i++) { //循环次数
            float value = a[i];
            int position = i;
            for (int j = i + 1; j < len; j++) {  //找到最小的值和位置
                if (a[j] > value) {
                    value = a[j];
                    position = j;
                }
            }
            a[position] = a[i];   //进行交换
            a[i] = value;
            int idx = idxes[position];
            idxes[position] = idxes[i];
            idxes[i] = idx;
        }
    }

    //获取正常响应
    public static IOTHttpRspVO getNormalResp(Object data) {
        IOTHttpRspVO iotRspVO = new IOTHttpRspVO();
        //设置消息ID
        iotRspVO.setMsgId(AppModConfig.msgId);
        AppModConfig.msgId++;
        //设置网关状态
        iotRspVO.setStatus(200);
        //设置结果对象
        iotRspVO.getResult().setCode(IOTRspType.Success.getCode());
        iotRspVO.getResult().setMsg(IOTRspType.Success.getMsg());
        //设置结果对象中数据对象
        if (data != null)
            iotRspVO.getResult().setDataMap(data);

        return iotRspVO;
    }

    //获取正常响应
    public static IOTHttpRspVO getNormalResp(Object data, Integer msgCode, String msg) {
        IOTHttpRspVO iotRspVO = new IOTHttpRspVO();
        //设置消息ID
        iotRspVO.setMsgId(AppModConfig.msgId);
        AppModConfig.msgId++;
        //设置网关状态
        iotRspVO.setStatus(200);
        //设置结果对象
        iotRspVO.getResult().setCode(msgCode);
        iotRspVO.getResult().setMsg(msg);
        //设置结果对象中数据对象
        iotRspVO.getResult().setDataMap(data);

        return iotRspVO;
    }

    //获取异常响应
    public static IOTHttpRspVO getExcepResp() {
        IOTHttpRspVO iotRspVO = new IOTHttpRspVO();
        //设置消息ID
        iotRspVO.setMsgId(AppModConfig.msgId);
        AppModConfig.msgId++;
        //消息id小于0判断
        AppModConfig.msgIdLessThan0Judge();
        //设置结果对象
        iotRspVO.getResult().setCode(IOTRspType.System_ERR.getCode());
        iotRspVO.getResult().setMsg(IOTRspType.System_ERR.getMsg());

        return iotRspVO;
    }

    // 获取异常响应
    public static IOTHttpRspVO getExcepResp(int code) {
        IOTHttpRspVO iotRspVO = new IOTHttpRspVO();
        // 设置消息ID
        iotRspVO.setMsgId(AppModConfig.msgId);
        AppModConfig.msgId++;
        // 消息id小于0判断
        AppModConfig.msgIdLessThan0Judge();
        // 设置结果对象
        if (code == 2001) {
            iotRspVO.getResult().setCode(IOTRspType.System_ERR.getCode());
            iotRspVO.getResult().setMsg(IOTRspType.System_ERR.getMsg());
        } else if (code == 2003) {
            iotRspVO.getResult().setCode(IOTRspType.AUTHCODE_CHKERR.getCode());
            iotRspVO.getResult().setMsg(IOTRspType.AUTHCODE_CHKERR.getMsg());
        } else if (code == 2005) {
            iotRspVO.getResult().setCode(IOTRspType.Account_Exist.getCode());
            iotRspVO.getResult().setMsg(IOTRspType.Account_Exist.getMsg());
        } else if (code == 2007) {
            iotRspVO.getResult().setCode(IOTRspType.OldPassword_Err.getCode());
            iotRspVO.getResult().setMsg(IOTRspType.OldPassword_Err.getMsg());
        } else if (code == 2009) {
            iotRspVO.getResult().setCode(IOTRspType.NewConfPassword_Err.getCode());
            iotRspVO.getResult().setMsg(IOTRspType.NewConfPassword_Err.getMsg());
        } else if (code == 2011) {
            iotRspVO.getResult().setCode(IOTRspType.JsonFormParse_Err.getCode());
            iotRspVO.getResult().setMsg(IOTRspType.JsonFormParse_Err.getMsg());
        } else if (code == 2013) {
            iotRspVO.getResult().setCode(IOTRspType.QueryDataRecord_Err.getCode());
            iotRspVO.getResult().setMsg(IOTRspType.QueryDataRecord_Err.getMsg());
        } else if (code == 2015) {
            iotRspVO.getResult().setCode(IOTRspType.Account_NonExist.getCode());
            iotRspVO.getResult().setMsg(IOTRspType.Account_NonExist.getMsg());
        } else if (code == 2017) {
            iotRspVO.getResult().setCode(IOTRspType.Param_VisitIllegal.getCode());
            iotRspVO.getResult().setMsg(IOTRspType.Param_VisitIllegal.getMsg());
        } else if (code == 2019) {
            iotRspVO.getResult().setCode(IOTRspType.UserEnable_Err.getCode());
            iotRspVO.getResult().setMsg(IOTRspType.UserEnable_Err.getMsg());
        } else if (code == 2021) {
            iotRspVO.getResult().setCode(IOTRspType.HTTP_REQUEST_EXCEPTION.getCode());
            iotRspVO.getResult().setMsg(IOTRspType.HTTP_REQUEST_EXCEPTION.getMsg());
        } else if (code == 2023) {
            iotRspVO.getResult().setCode(IOTRspType.UNKNOW_EXCEPTION.getCode());
            iotRspVO.getResult().setMsg(IOTRspType.UNKNOW_EXCEPTION.getMsg());
        } else if (code == 2025) {
            iotRspVO.getResult().setCode(IOTRspType.DATABASE_OPS_EXCEPTION.getCode());
            iotRspVO.getResult().setMsg(IOTRspType.DATABASE_OPS_EXCEPTION.getMsg());
        } else if (code == 2027) {
            iotRspVO.getResult().setCode(IOTRspType.RoleName_Exist.getCode());
            iotRspVO.getResult().setMsg(IOTRspType.RoleName_Exist.getMsg());
        } else if (code == 2029) {
            iotRspVO.getResult().setCode(IOTRspType.RoleName_NoExist.getCode());
            iotRspVO.getResult().setMsg(IOTRspType.RoleName_NoExist.getMsg());
        } else if (code == 2031) {
            iotRspVO.getResult().setCode(IOTRspType.Param_VisitFrmErr.getCode());
            iotRspVO.getResult().setMsg(IOTRspType.Param_VisitFrmErr.getMsg());
        } else if (code == 2033) {
            iotRspVO.getResult().setCode(IOTRspType.AdminAccount_NonExist.getCode());
            iotRspVO.getResult().setMsg(IOTRspType.AdminAccount_NonExist.getMsg());
        } else if (code == 2035) {
            iotRspVO.getResult().setCode(IOTRspType.Account_Forbid.getCode());
            iotRspVO.getResult().setMsg(IOTRspType.Account_Forbid.getMsg());
        } else if (code == 2037) {
            iotRspVO.getResult().setCode(IOTRspType.Mail_Send_Fail.getCode());
            iotRspVO.getResult().setMsg(IOTRspType.Mail_Send_Fail.getMsg());
        } else {
            iotRspVO.getResult().setCode(IOTRspType.System_ERR.getCode());
            iotRspVO.getResult().setMsg(IOTRspType.System_ERR.getMsg());
        }

        return iotRspVO;
    }

    //验证授权码
    public static boolean verifyAuthCode(String token, Db1Service db1Service) {
        boolean flag = false;
        String dbToken = db1Service.getAuthCodeByCurAuthCode(token);
        if (dbToken.equals(token)) {
            if (dbToken.length() == 40)
                flag = true;
        }

        return flag;
    }

    //验证授权码2
    public static boolean verifyAuthCode2(String token, Db2Service db2Service, int[] codes) {
        boolean flag = false;
        codes[0] = 2003;
        String dbToken = db2Service.getAuthCodeByCurAuthCode2(token);
        if (dbToken != null) {
            if (dbToken.equals(token)) {
                if (dbToken.length() == 40) {
                    flag = true;
                    codes[0] = 0;
                }
            }
        }

        return flag;
    }

    //学校类型解析获取
    public static String getSchType(String level, Integer level2) {
        String schTypeName = "其他";
        int curLevel2 = 0;
        //依据新字段学制判断
        if (level2 != null) {
            curLevel2 = level2.intValue();
        } else {    //新学制字段为空时，依据旧学制判断
            curLevel2 = -1;
            level = level.trim();
            int len = level.length();
            int[] flIdxs = new int[10];
            for (int i = 0; i < flIdxs.length; i++) {
                String strIdx = String.valueOf(i);
                flIdxs[i] = level.indexOf(strIdx);
            }
            if (len == 1 && flIdxs[7] != -1)
                curLevel2 = 0;
            else if (len == 3 && flIdxs[0] != -1 && flIdxs[7] != -1)
                curLevel2 = 1;
            else if (len == 5 && flIdxs[0] != -1 && flIdxs[1] != -1 && flIdxs[7] != -1)
                curLevel2 = 2;
            else if (len == 1 && flIdxs[0] != -1)
                curLevel2 = 3;
            else if (len == 3 && flIdxs[0] != -1 && flIdxs[1] != -1)
                curLevel2 = 4;
            else if (len == 5 && flIdxs[0] != -1 && flIdxs[1] != -1 && flIdxs[2] != -1)
                curLevel2 = 5;
            else if (len == 7 && flIdxs[0] != -1 && flIdxs[1] != -1 && flIdxs[2] != -1 && flIdxs[3] != -1)
                curLevel2 = 6;
            else if (len == 1 && flIdxs[1] != -1)
                curLevel2 = 7;
            else if (len == 1 && flIdxs[2] != -1)
                curLevel2 = 8;
            else if (len == 1 && flIdxs[3] != -1)
                curLevel2 = 9;
            else if (len == 3 && flIdxs[2] != -1 && flIdxs[3] != -1)
                curLevel2 = 10;
            else if (len == 3 && flIdxs[1] != -1 && flIdxs[2] != -1)
                curLevel2 = 11;
            else if (len == 5 && flIdxs[1] != -1 && flIdxs[2] != -1 && flIdxs[3] != -1)
                curLevel2 = 12;
            else if (len == 1 && flIdxs[6] != -1)
                curLevel2 = 13;
            else if (len == 3 && ((flIdxs[3] != -1 && flIdxs[6] != -1) || (flIdxs[0] != -1 && flIdxs[6] != -1) || (flIdxs[6] != -1 && flIdxs[8] != -1)))
                curLevel2 = 14;
            else if (len == 1 && flIdxs[9] != -1)
                curLevel2 = 17;
        }
        //输出学校类型名称
        if (curLevel2 == 0)
            schTypeName = "托儿所";
        else if (curLevel2 == 1)
            schTypeName = "托幼园";
        else if (curLevel2 == 2)
            schTypeName = "托幼小";
        else if (curLevel2 == 3)
            schTypeName = "幼儿园";
        else if (curLevel2 == 4)
            schTypeName = "幼小";
        else if (curLevel2 == 5)
            schTypeName = "幼小初";
        else if (curLevel2 == 6)
            schTypeName = "幼小初高";
        else if (curLevel2 == 7)
            schTypeName = "小学";
        else if (curLevel2 == 8)
            schTypeName = "初级中学";
        else if (curLevel2 == 9)
            schTypeName = "高级中学";
        else if (curLevel2 == 10)
            schTypeName = "完全中学";
        else if (curLevel2 == 11)
            schTypeName = "九年一贯制学校";
        else if (curLevel2 == 12)
            schTypeName = "十二年一贯制学校";
        else if (curLevel2 == 13)
            schTypeName = "职业初中";
        else if (curLevel2 == 14)
            schTypeName = "中等职业学校";
        else if (curLevel2 == 15)
            schTypeName = "工读学校";
        else if (curLevel2 == 16)
            schTypeName = "特殊教育学校";
        else if (curLevel2 == 17)
            schTypeName = "其他";

        return schTypeName;
    }


    //学校类型编号解析获取
    public static Integer getSchTypeId(String level, Integer level2) {
        Integer curLevel2 = null;
        //依据新字段学制判断
        if (level2 != null) {
            curLevel2 = level2.intValue();
        } else {    //新学制字段为空时，依据旧学制判断
            curLevel2 = null;
            level = level.trim();
            int len = level.length();
            int[] flIdxs = new int[10];
            for (int i = 0; i < flIdxs.length; i++) {
                String strIdx = String.valueOf(i);
                flIdxs[i] = level.indexOf(strIdx);
            }
            if (len == 1 && flIdxs[7] != -1)
                curLevel2 = 0;
            else if (len == 3 && flIdxs[0] != -1 && flIdxs[7] != -1)
                curLevel2 = 1;
            else if (len == 5 && flIdxs[0] != -1 && flIdxs[1] != -1 && flIdxs[7] != -1)
                curLevel2 = 2;
            else if (len == 1 && flIdxs[0] != -1)
                curLevel2 = 3;
            else if (len == 3 && flIdxs[0] != -1 && flIdxs[1] != -1)
                curLevel2 = 4;
            else if (len == 5 && flIdxs[0] != -1 && flIdxs[1] != -1 && flIdxs[2] != -1)
                curLevel2 = 5;
            else if (len == 7 && flIdxs[0] != -1 && flIdxs[1] != -1 && flIdxs[2] != -1 && flIdxs[3] != -1)
                curLevel2 = 6;
            else if (len == 1 && flIdxs[1] != -1)
                curLevel2 = 7;
            else if (len == 1 && flIdxs[2] != -1)
                curLevel2 = 8;
            else if (len == 1 && flIdxs[3] != -1)
                curLevel2 = 9;
            else if (len == 3 && flIdxs[2] != -1 && flIdxs[3] != -1)
                curLevel2 = 10;
            else if (len == 3 && flIdxs[1] != -1 && flIdxs[2] != -1)
                curLevel2 = 11;
            else if (len == 5 && flIdxs[1] != -1 && flIdxs[2] != -1 && flIdxs[3] != -1)
                curLevel2 = 12;
            else if (len == 1 && flIdxs[6] != -1)
                curLevel2 = 13;
            else if (len == 3 && ((flIdxs[3] != -1 && flIdxs[6] != -1) || (flIdxs[0] != -1 && flIdxs[6] != -1) || (flIdxs[6] != -1 && flIdxs[8] != -1)))
                curLevel2 = 14;
            else if (len == 1 && flIdxs[9] != -1)
                curLevel2 = 17;
        }
        return curLevel2;
    }

    //输出学校性质
    public static String getSchProp(String schoolNature) {
        String schProp = "其他";
        if (schoolNature != null) {
            int[] flIdxs = new int[5];
            for (int i = 0; i < flIdxs.length; i++) {
                String strIdx = String.valueOf(i);
                flIdxs[i] = schoolNature.indexOf(strIdx);
            }
            if (flIdxs[0] != -1 || flIdxs[1] != -1)
                schProp = "公办";
            else if (flIdxs[2] != -1 || flIdxs[3] != -1)
                schProp = "民办";
            //兼容学校性质判断
            if (schoolNature.equals("0"))
                schProp = "公办";
            else if (schoolNature.equals("2"))
                schProp = "民办";
            else if (schoolNature.equals("3"))
                schProp = "外籍人员子女学校";
        }

        return schProp;
    }

    //输出经营模式属性
    public static String getOptModeName(short canteenMode, String ledgerType, String licenseMainType, Short licenseMainChild) {
        String optModeName = "-";
        if (canteenMode == 0) {      //自营
            optModeName = "自营";
        } else if (canteenMode == 1) {   //外包
            if (ledgerType.indexOf("0") != -1)
                optModeName = "外送";
            else if (ledgerType.indexOf("1") != -1)
                optModeName = "托管";
            else
                optModeName = "托管";
        }
        //新的经营模式判断
        if (licenseMainType != null) {
            if (licenseMainType.equals("0")) {    //学校
                if (licenseMainChild != null) {
                    if (licenseMainChild == 0)
                        optModeName = "自营";
                    else if (licenseMainChild == 1)
                        optModeName = "自营";
                }
                optModeName = "自营";
            } else if (licenseMainType.equals("1")) {    //外包
                if (licenseMainChild != null) {
                    if (licenseMainChild == 0)
                        optModeName = "托管";
                    else if (licenseMainChild == 1)
                        optModeName = "外送";
                }
            } else {
                optModeName = "";
            }
        } else {
            optModeName = "";
        }

        return optModeName;
    }

    //判断是否为整数
    //@param str 传入的字符串
    //@return 是整数返回true,否则返回false
    public static boolean isInteger(String str) {
        if (str.isEmpty())
            return false;
        Pattern pattern = Pattern.compile("^[-\\+]?[\\d]*$");
        return pattern.matcher(str).matches();
    }

    //获取变量名称及对应索引值
    public static int getVarValIndex(String[] varVals, String varName) {
        int i, index = -1;
        for (i = 0; i < varVals.length; i++) {
            if (varVals[i].equalsIgnoreCase(varName) && i < varVals.length - 1) {
                index = i + 1;
                break;
            }
        }
        return index;
    }

    //获取用户权限
    public static Map<String, Integer> getUserDataPerm(String token, Db1Service db1Service, Db2Service db2Service) {
        Map<String, Integer> schIdToFlagMap = null;
        //从数据源ds2的数据表t_edu_supervise_user中查找授权码以当前授权码
        TEduBdUserDo tebuDo = db2Service.getBdUserInfoByCurAuthCode(token);
        if (tebuDo.getId() != null) {
            if (tebuDo.getParentId() != null) {
                schIdToFlagMap = new HashMap<>();
                //获取数据权限
                List<TEduBdUserPermDo> tebupDoList = db2Service.getAllBdUserPermInfo(tebuDo.getId(), 1);    //从数据源ds2的数据表t_edu_bd_user_perm中查找所有用户权限信息
                if (tebupDoList != null) {
                    for (int i = 0; i < tebupDoList.size(); i++) {
                        schIdToFlagMap.put(tebupDoList.get(i).getPermId(), 1);
                    }
                }
            }
        }

        return schIdToFlagMap;
    }

    //获取用户权限区域ID
    public static String getUserDataPermDistId(String token, Db1Service db1Service, Db2Service db2Service) {
        String distId = null;
        //从数据源ds2的数据表t_edu_supervise_user中查找授权码以当前授权码
        TEduBdUserDo tebuDo = db2Service.getBdUserInfoByCurAuthCode(token);
        if (tebuDo.getId() != null) {
            if (tebuDo.getOrgName() != null) {
                String curOrgName = tebuDo.getOrgName();
                if (curOrgName.equals("黄浦区教育局")) {
                    distId = "1";         //黄浦区
                } else if (curOrgName.equals("静安区教育局")) {
                    distId = "10";         //静安区
                } else if (curOrgName.equals("徐汇区教育局")) {
                    distId = "11";         //徐汇区
                } else if (curOrgName.equals("长宁区教育局")) {
                    distId = "12";         //长宁区
                } else if (curOrgName.equals("普陀区教育局")) {
                    distId = "13";         //普陀区
                } else if (curOrgName.equals("虹口区教育局")) {
                    distId = "14";         //虹口区
                } else if (curOrgName.equals("杨浦区教育局")) {
                    distId = "15";         //杨浦区
                } else if (curOrgName.equals("闵行区教育局")) {
                    distId = "16";         //闵行区
                } else if (curOrgName.equals("嘉定区教育局")) {
                    distId = "2";         //嘉定区
                } else if (curOrgName.equals("宝山区教育局")) {
                    distId = "3";         //宝山区
                } else if (curOrgName.equals("浦东新区教育局")) {
                    distId = "4";         //浦东新区
                } else if (curOrgName.equals("松江区教育局")) {
                    distId = "5";         //松江区
                } else if (curOrgName.equals("金山区教育局")) {
                    distId = "6";         //金山区
                } else if (curOrgName.equals("青浦区教育局")) {
                    distId = "7";         //青浦区
                } else if (curOrgName.equals("奉贤区教育局")) {
                    distId = "8";         //奉贤区
                } else if (curOrgName.equals("崇明区教育局")) {
                    distId = "9";         //崇明区
                }
            }
        }

        return distId;
    }

    //获取登录用户信息
    public static TEduBdUserDo getUserByToken(String token, Db2Service db2Service) {
        //从数据源ds2的数据表t_edu_supervise_user中查找授权码以当前授权码
        TEduBdUserDo tebuDo = db2Service.getBdUserInfoByCurAuthCode(token);
        return tebuDo;
    }

    //获取用户数据权限信息
    public static UserDataPermInfoDTO getUserDataPermInfo(String token, Db1Service db1Service, Db2Service db2Service) {
        UserDataPermInfoDTO udpiDto = new UserDataPermInfoDTO();
        udpiDto.setSubLevelId(-1);
        udpiDto.setCompDepId(-1);
        //是否启用按所属、主管部门提取数据使能
        boolean bIsUseSubLevelCompDepFlag = false;
        //获取用户信息
        TEduBdUserDo tebuDo = db2Service.getBdUserInfoByToken(token);
        if (tebuDo.getId() != null) {
            //用户名称
            udpiDto.setUserName(tebuDo.getUserAccount());
            //所在区
            String distId = null, curOrgName = tebuDo.getOrgName();
            if (curOrgName.equals("黄浦区教育局")) {
                distId = "1";         //黄浦区
            } else if (curOrgName.equals("静安区教育局")) {
                distId = "10";         //静安区
            } else if (curOrgName.equals("徐汇区教育局")) {
                distId = "11";         //徐汇区
            } else if (curOrgName.equals("长宁区教育局")) {
                distId = "12";         //长宁区
            } else if (curOrgName.equals("普陀区教育局")) {
                distId = "13";         //普陀区
            } else if (curOrgName.equals("虹口区教育局")) {
                distId = "14";         //虹口区
            } else if (curOrgName.equals("杨浦区教育局")) {
                distId = "15";         //杨浦区
            } else if (curOrgName.equals("闵行区教育局")) {
                distId = "16";         //闵行区
            } else if (curOrgName.equals("嘉定区教育局")) {
                distId = "2";         //嘉定区
            } else if (curOrgName.equals("宝山区教育局")) {
                distId = "3";         //宝山区
            } else if (curOrgName.equals("浦东新区教育局")) {
                distId = "4";         //浦东新区
            } else if (curOrgName.equals("松江区教育局")) {
                distId = "5";         //松江区
            } else if (curOrgName.equals("金山区教育局")) {
                distId = "6";         //金山区
            } else if (curOrgName.equals("青浦区教育局")) {
                distId = "7";         //青浦区
            } else if (curOrgName.equals("奉贤区教育局")) {
                distId = "8";         //奉贤区
            } else if (curOrgName.equals("崇明区教育局")) {
                distId = "9";         //崇明区
            }
            udpiDto.setDistId(distId);
            //组织ID和组织名称
            udpiDto.setOrgId(tebuDo.getOrgId());
            udpiDto.setOrgName(tebuDo.getOrgName());
            //获取角色信息
            TEduBdRoleDo tebrDo = db2Service.getBdRoleInfoByRoleId(tebuDo.getRoleId());
            udpiDto.setRoleType(tebrDo.getRoleType());
            if (tebrDo.getRoleType() != null && bIsUseSubLevelCompDepFlag) {
                if (tebrDo.getRoleType().intValue() == 1) {
                    if (curOrgName.equals("黄浦区教育局") || curOrgName.equals("静安区教育局") || curOrgName.equals("徐汇区教育局") || curOrgName.equals("长宁区教育局") || curOrgName.equals("普陀区教育局") ||
                            curOrgName.equals("虹口区教育局") || curOrgName.equals("杨浦区教育局") || curOrgName.equals("闵行区教育局") || curOrgName.equals("嘉定区教育局") || curOrgName.equals("宝山区教育局") ||
                            curOrgName.equals("浦东新区教育局") || curOrgName.equals("松江区教育局") || curOrgName.equals("金山区教育局") || curOrgName.equals("青浦区教育局") || curOrgName.equals("奉贤区教育局") ||
                            curOrgName.equals("崇明区教育局")) {
                        udpiDto.setSubLevel("区属");
                        //所属ID和主管部门ID
                        udpiDto.setSubLevelId(3);
                        String compDevId = compDepNameToIdMap3.get(curOrgName);
                        if (compDevId != null)
                            udpiDto.setCompDepId(Integer.parseInt(compDevId));
                    } else if (curOrgName.equals("市水务局（海洋局）") || curOrgName.equals("市农委") || curOrgName.equals("市交通委") || curOrgName.equals("市科委") || curOrgName.equals("市商务委") || curOrgName.equals("市经信委")) {   // || curOrgName.equals("市教委")
                        udpiDto.setSubLevel("市属");
                        //所属ID和主管部门ID
                        udpiDto.setSubLevelId(2);
                        String compDevId = compDepNameToIdMap2.get(curOrgName);
                        if (compDevId != null)
                            udpiDto.setCompDepId(Integer.parseInt(compDevId));
                    } else if (curOrgName.equals("教育部")) {
                        udpiDto.setSubLevel("部属");
                        //所属ID和主管部门ID
                        udpiDto.setSubLevelId(1);
                        String compDevId = compDepNameToIdMap1.get(curOrgName);
                        if (compDevId != null)
                            udpiDto.setCompDepId(Integer.parseInt(compDevId));
                    }
                }
            }
        }

        return udpiDto;
    }

    //获取head数据
    public static String GetHeadJsonReq(HttpServletRequest request, String headName) {
        String hnVal = null, strJson = "{", curSubJson;
        boolean isHeadJson = false;
        //通过getHeaderNames获得所有头名字的Enumeration集合
        Enumeration<String> headNames = request.getHeaderNames();
        while (headNames.hasMoreElements()) {
            String curHeadName = headNames.nextElement();
            if (headName != null) {
                if (curHeadName.equalsIgnoreCase(headName)) {
                    hnVal = request.getHeader(headName);
                    break;
                }
            } else {
                if (isHeadJson)
                    curSubJson = ",\"" + headName + "\":" + "\"" + request.getHeader(headName) + "\"";
                else {
                    curSubJson = "\"" + headName + "\":" + "\"" + request.getHeader(headName) + "\"";
                    isHeadJson = true;
                }
                strJson += curSubJson;
            }
        }
        if (headName != null)
            logger.info(headName + ": " + hnVal);
        else {
            hnVal = strJson;
            logger.info(strJson);
        }

        return hnVal;
    }

    //获取hdfs文件数据key
    public static Map<String, String> getHdfsDataKey(String date, String key) {
        Map<String, String> hdfsMap = null;
        //Redis离线文件目录
        String redisOfflineDir = "/redishistory",
                localRedisPathName = "/opt/data/BdBiProcSrvShEduOmc",
                fileName = key + ".json",
                strJson = null;
        String localRedisFileName = localRedisPathName + "/" + fileName;
        File file = new File(localRedisFileName);
        if (!file.exists()) {
            //下载到本地，再读取
            HdfsRWClient.hdfsFileDownload(redisOfflineDir + "/" + date + "/" + fileName, localRedisPathName, null);
        }
        if (file.exists()) {
            // 读取二进制文件
            try {
                byte[] fileCont = FileWRCommSys.ReadBinaryFile(localRedisFileName);
                if (fileCont != null) {
                    strJson = new String(fileCont, 0, fileCont.length);
                    if (!strJson.isEmpty()) {
                        RedisKeyValueDTO rkvDto = AppModConfig.objectMapper.readValue(strJson, RedisKeyValueDTO.class);
                        if (rkvDto != null) {
                            List<RedisKeyValue> rkvList = rkvDto.getRedisKeyValue();
                            if (rkvList != null) {
                                hdfsMap = new HashMap<>();
                                for (int i = 0; i < rkvList.size(); i++) {
                                    hdfsMap.put(rkvList.get(i).getKey(), rkvList.get(i).getValue());
                                }
                            }
                        }
                    }
                }
                file.delete();
            } catch (IOException e) {
                // TODO 自动生成的 catch 块
                e.printStackTrace();
            }
        }

        return hdfsMap;
    }

    //获取hdfs文件数据key
    public static String getHdfsDataKeyField(String date, String key, String field) {
        //Redis离线文件目录
        String redisOfflineDir = "/redishistory", localRedisPathName = "/opt/data/BdBiProcSrvShEduOmc", fileName = key + ".json", strJson = null;
        String localRedisFileName = localRedisPathName + "/" + fileName;
        File file = new File(localRedisFileName);
        if (!file.exists()) {
            //下载到本地，再读取
            HdfsRWClient.hdfsFileDownload(redisOfflineDir + "/" + date + "/" + fileName, localRedisPathName, null);
        }
        if (file.exists()) {
            // 读取二进制文件
            try {
                byte[] fileCont = FileWRCommSys.ReadBinaryFile(localRedisFileName);
                if (fileCont != null) {
                    strJson = new String(fileCont, 0, fileCont.length);
                    if (!strJson.isEmpty()) {
                        RedisKeyValueDTO rkvDto = AppModConfig.objectMapper.readValue(strJson, RedisKeyValueDTO.class);
                        if (rkvDto != null) {
                            List<RedisKeyValue> rkvList = rkvDto.getRedisKeyValue();
                            if (rkvList != null) {
                                for (int i = 0; i < rkvList.size(); i++) {
                                    if (field.equals(rkvList.get(i).getKey())) {
                                        return rkvList.get(i).getValue();
                                    }
                                }
                            }
                        }
                    }
                }
                file.delete();
            } catch (IOException e) {
                // TODO 自动生成的 catch 块
                e.printStackTrace();
            }
        }

        return null;
    }

    //获取hdfs文件数据key
    public static Map<String, String> getHdfsHashKey(String key) {
        String date = null;
        Map<String, String> hdfsMap = null;
        if (key == null)
            return hdfsMap;
        String[] keys = key.split("_");
        boolean extractDateFlag = false;
        if (keys.length > 1) {
            extractDateFlag = BCDTimeUtil.isValidDate(keys[0], "yyyy-MM-dd");
            if (extractDateFlag) {
                date = keys[0];
                //计算当天前N天（如40天）日期
                String curDate = BCDTimeUtil.convertNormalDate(null);
                DateTime curDt = BCDTimeUtil.convertDateStrToDate(curDate);
                String preDate = curDt.minusDays(SpringConfig.redis_clearagodays + 2).toString("yyyy-MM-dd");
                if (date.compareTo(preDate) >= 0)
                    extractDateFlag = false;
                logger.info("今天日期：" + curDate + "，N（如N=40）天前日期：" + preDate + "，当前运算日期：" + date + "，是否适合离线提取：" + extractDateFlag);
            }
        }
        if (extractDateFlag) {
            //Redis离线文件目录
            String redisOfflineDir = "/redishistory", localRedisPathName = "/opt/data/BdBiProcSrvShEduOmc", fileName = key + ".json", strJson = null;
            String localRedisFileName = localRedisPathName + "/" + fileName;
            File file = new File(localRedisFileName);
            if (!file.exists()) {
                //下载到本地，再读取
                HdfsRWClient.hdfsFileDownload(redisOfflineDir + "/" + date + "/" + fileName, localRedisPathName, null);
            } else if (file.length() == 0) {
                file.delete();
                //下载到本地，再读取
                HdfsRWClient.hdfsFileDownload(redisOfflineDir + "/" + date + "/" + fileName, localRedisPathName, null);
            }
            if (file.exists()) {
                // 读取二进制文件
                try {
                    byte[] fileCont = FileWRCommSys.ReadBinaryFile(localRedisFileName);
                    strJson = new String(fileCont, 0, fileCont.length);
                    RedisKeyValueDTO rkvDto = AppModConfig.objectMapper.readValue(strJson, RedisKeyValueDTO.class);
                    if (rkvDto != null) {
                        List<RedisKeyValue> rkvList = rkvDto.getRedisKeyValue();
                        if (rkvList != null) {
                            hdfsMap = new HashMap<>();
                            for (int i = 0; i < rkvList.size(); i++) {
                                hdfsMap.put(rkvList.get(i).getKey(), rkvList.get(i).getValue());
                            }
                        }
                    }
                    //file.delete();
                } catch (IOException e) {
                    // TODO 自动生成的 catch 块
                    e.printStackTrace();
                }
            }
        }

        return hdfsMap;
    }

    //获取excell单元风格
    public static CellStyle getExcellCellStyle(Workbook wb) {
        if (wb == null)
            return null;
        CellStyle style = wb.createCellStyle();                  // 样式对象
        // 设置单元格的背景颜色为淡蓝色
        style.setFillForegroundColor(HSSFColor.PALE_BLUE.index);
        style.setVerticalAlignment(CellStyle.VERTICAL_CENTER);   // 垂直
        style.setAlignment(CellStyle.ALIGN_CENTER);              // 水平
        style.setWrapText(true);                                 // 指定当单元格内容显示不下时自动换行
        Font font = wb.createFont();
        font.setBoldweight(Font.BOLDWEIGHT_BOLD);
        style.setFont(font);

        return style;
    }

    //获取excell单元风格(加粗字体+水平居左)
    public static CellStyle getExcellCellStyleAlignLeft(Workbook wb) {
        if (wb == null)
            return null;
        CellStyle style = wb.createCellStyle();                  // 样式对象
        // 设置单元格的背景颜色为淡蓝色
        style.setFillForegroundColor(HSSFColor.PALE_BLUE.index);
        style.setVerticalAlignment(CellStyle.ALIGN_LEFT);      // 垂直
        style.setAlignment(CellStyle.ALIGN_LEFT);              // 水平
        style.setWrapText(true);                                 // 指定当单元格内容显示不下时自动换行
        Font font = wb.createFont();
        font.setBoldweight(Font.BOLDWEIGHT_BOLD);
        style.setFont(font);
        return style;
    }

    //获取excell单元风格(加边框)
    public static CellStyle getExcellCellStyleBorder(Workbook wb) {
        if (wb == null)
            return null;
        CellStyle style = wb.createCellStyle();                  // 样式对象
        style.setBorderBottom(CellStyle.BORDER_THIN);
        style.setBorderLeft(CellStyle.BORDER_THIN);
        style.setBorderRight(CellStyle.BORDER_THIN);
        style.setBorderTop(CellStyle.BORDER_THIN);


        return style;
    }

    //获取excell单元风格
    public static CellStyle getExcellCellStyleTwo(Workbook wb) {
        if (wb == null)
            return null;
        CellStyle style = wb.createCellStyle();                  // 样式对象
        // 设置单元格的背景颜色为淡蓝色
        style.setFillForegroundColor(HSSFColor.PALE_BLUE.index);
        style.setVerticalAlignment(CellStyle.VERTICAL_CENTER);   // 垂直
        style.setAlignment(CellStyle.ALIGN_CENTER);              // 水平
        style.setWrapText(true);                                 // 指定当单元格内容显示不下时自动换行
        Font font = wb.createFont();
        font.setBoldweight(Font.BOLDWEIGHT_BOLD);
        style.setFont(font);
        style.setBorderBottom(CellStyle.BORDER_THIN);
        style.setBorderLeft(CellStyle.BORDER_THIN);
        style.setBorderRight(CellStyle.BORDER_THIN);
        style.setBorderTop(CellStyle.BORDER_THIN);

        return style;
    }

    //获取excell单元风格
    public static CellStyle getExcellCellStyle(Workbook wb, String fontName, int fontHeight) {
        if (wb == null)
            return null;
        CellStyle style = wb.createCellStyle();                  // 样式对象
        // 设置单元格的背景颜色为淡蓝色
        style.setFillForegroundColor(HSSFColor.PALE_BLUE.index);
        style.setVerticalAlignment(CellStyle.VERTICAL_CENTER);   // 垂直
        style.setAlignment(CellStyle.ALIGN_CENTER);              // 水平
        style.setWrapText(true);                                 // 指定当单元格内容显示不下时自动换行
        Font font = wb.createFont();
        font.setBoldweight(Font.BOLDWEIGHT_BOLD);
        if (fontName != null)
            font.setFontName(fontName);
        else
            font.setFontName("宋体");
        if (fontHeight > 0)
            font.setFontHeight((short) fontHeight);
        else
            font.setFontHeight((short) 280);
        style.setFont(font);

        return style;
    }

    //写二进制文件
    public static void WriteBinaryFile(byte[] FileCont, String strFileInfo, String strFileName) {
        String fileName = strFileName, strNmJson = "";
        try {
            if (FileCont != null) {
                System.out.println("文件长度: " + FileCont.length + " 字节");
                //保存日志文件
                DataOutputStream out = new DataOutputStream(new BufferedOutputStream(new FileOutputStream(fileName)));
                out.write(FileCont);
                out.close();

                logger.info("*************ftp开始");
                FtpUtil.ftpPicServer(strFileName, FileCont);
                logger.info("*************ftp结束");
            }
            if (strFileInfo == null || strFileInfo.isEmpty())
                return;
            //保存日志文件信息
            int idx = fileName.lastIndexOf(".");
            String strLogFileInfoName = "";
            if (idx != -1)
                strLogFileInfoName = fileName.substring(0, idx) + ".json";
            else
                strLogFileInfoName = fileName + ".json";
            DataOutputStream outfileinfo = new DataOutputStream(new BufferedOutputStream(new FileOutputStream(strLogFileInfoName)));
            strNmJson = JsonFormatTool.formatJson(strFileInfo);
            outfileinfo.write(strNmJson.getBytes());
            outfileinfo.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //移动文件到其他目录
    public static void moveFileToOtherFolder(String pathFileName, String dstPath) {
        String startPath = pathFileName;
        String endPath = dstPath;
        try {
            File startFile = new File(startPath);
            File tmpFile = new File(endPath);//获取文件夹路径
            if (!tmpFile.exists()) {//判断文件夹是否创建，没有创建则创建新文件夹
                tmpFile.mkdirs();
            }
            System.out.println(endPath + startFile.getName());
            if (startFile.renameTo(new File(endPath + startFile.getName()))) {
                System.out.println("File is moved successful!");
                logger.info("文件移动成功！文件名：《{}》 目标路径：{}", pathFileName, endPath);
            } else {
                System.out.println("File is failed to move!");
                logger.info("文件移动失败！文件名：《{}》 起始路径：{}", pathFileName, startPath);
            }
        } catch (Exception e) {
            logger.info("文件移动异常！文件名：《{}》 起始路径：{}", pathFileName, startPath);
        }
    }

    //获取body数据
    public static String GetBodyJsonReq(HttpServletRequest request, boolean isUtf8Code) {
        BufferedReader br;
        StringBuilder sb = null;
        String reqBody = null;
        try {
            br = new BufferedReader(new InputStreamReader(request.getInputStream()));
            String line = null;
            sb = new StringBuilder();
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }
            if (isUtf8Code) {
                reqBody = URLDecoder.decode(sb.toString(), "UTF-8");
            } else
                reqBody = sb.toString();
            logger.info("接收Body内容：" + reqBody);
            return reqBody;
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return null;
        }
    }

    @SuppressWarnings("serial")
    public static final Map<Integer, String> warnLevelIdToNameMap = new LinkedHashMap<Integer, String>() {{
        put(1, "提示");
        put(2, "提醒");
        put(3, "预警");
        put(4, "督办");
        put(5, "追责");
    }};

    @SuppressWarnings("serial")
    public static final Map<Integer, String> warnLevelIdToNameHiveMap = new LinkedHashMap<Integer, String>() {{
        put(1, "提示");
        put(2, "提醒");
        put(3, "预警");
        put(4, "警告");
        put(5, "追责");
    }};


    ////操作状态 1 表示规范录入 2 表示补录 3 表示逾期补录 4 表示无数据
    @SuppressWarnings("serial")
    public static final Map<String, String> plaStatusIdToNameMap = new LinkedHashMap<String, String>() {{
        put("1", "规范录入");
        put("2", "补录");
        put("3", "逾期补录");
        put("4", "无数据");
        put("5", "不供餐");
    }};


}
