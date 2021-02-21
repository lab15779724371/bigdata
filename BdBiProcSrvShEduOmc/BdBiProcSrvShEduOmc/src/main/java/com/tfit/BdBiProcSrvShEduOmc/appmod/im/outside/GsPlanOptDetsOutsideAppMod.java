package com.tfit.BdBiProcSrvShEduOmc.appmod.im.outside;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.joda.time.DateTime;
import org.joda.time.Days;
import org.springframework.beans.factory.annotation.Autowired;

import com.tfit.BdBiProcSrvShEduOmc.config.AppModConfig;
import com.tfit.BdBiProcSrvShEduOmc.config.SpringConfig;
import com.tfit.BdBiProcSrvShEduOmc.dto.im.GsPlanOptCommonDets;
import com.tfit.BdBiProcSrvShEduOmc.dto.im.outside.GsPlanOptDetsOutside;
import com.tfit.BdBiProcSrvShEduOmc.dto.im.outside.GsPlanOptDetsOutsideDTO;
import com.tfit.BdBiProcSrvShEduOmc.service.Db1Service;
import com.tfit.BdBiProcSrvShEduOmc.service.Db2Service;
import com.tfit.BdBiProcSrvShEduOmc.service.DbHiveGsService;
import com.tfit.BdBiProcSrvShEduOmc.service.RedisService;
import com.tfit.BdBiProcSrvShEduOmc.service.SaasService;
import com.tfit.BdBiProcSrvShEduOmc.util.BCDTimeUtil;
import com.tfit.BdBiProcSrvShEduOmc.util.CommonUtil;
import com.tfit.BdBiProcSrvShEduOmc.util.SortList;

//配货计划操作详情列表应用模型
public class GsPlanOptDetsOutsideAppMod {
	private static final Logger logger = LogManager.getLogger(GsPlanOptDetsOutsideAppMod.class.getName());

	//Redis服务
	@Autowired
	RedisService redisService = new RedisService();

	//三级排序条件
	final String[] methods = {"getDistName", "getSchType", "getDistrBatNumber"};
	final String[] sorts = {"asc", "asc", "asc"};
	final String[] dataTypes = {"String", "String", "String"};

	//是否为真实数据标识
	private static boolean isRealData = true;

	/**
	 * 方法类型索引
	 */
	int methodIndex = 2;

	//数组数据初始化
	String[] distrDate_Array = {"2018/08/08", "2018/08/08"};
	String[] distrBatNumber_Array = {"2018080802055", "2018080802055"};
	String[] ppName_Array = {"上海市天山中学", "上海市天山中学"};
	String[] schType_Array = {"初级中学", "初级中学"};
	String[] distName_Array = {"长宁区", "长宁区"};
	String[] rmcName_Array = {"上海绿捷", "上海绿捷"};
	String[] dispType_Array = {"原料", "原料"};
	String[] dispMode_Array = {"统配", "统配"};
	int[] assignStatus_Array = {1, 1};
	int[] dispStatus_Array = {0, 0};
	int[] acceptStatus_Array = {0, 0};
	int[] sendFlag_Array = {0, 0};

	//模拟数据函数
	private GsPlanOptDetsOutsideDTO SimuDataFunc() {
		GsPlanOptDetsOutsideDTO gpodDto = new GsPlanOptDetsOutsideDTO();
		//设置返回数据
		gpodDto.setTime(BCDTimeUtil.convertNormalFrom(null));
		//列表元素设置
		List<GsPlanOptDetsOutside> matConfirmDets = new ArrayList<>();
		//赋值
		for (int i = 0; i < distrDate_Array.length; i++) {
			GsPlanOptDetsOutside gpod = new GsPlanOptDetsOutside();
			/*gpod.setDistrDate(distrDate_Array[i]);
			gpod.setDistrBatNumber(distrBatNumber_Array[i]);
			gpod.setPpName(ppName_Array[i]);
			gpod.setSchType(schType_Array[i]);
			gpod.setDistName(distName_Array[i]);
			gpod.setRmcName(rmcName_Array[i]);
			gpod.setDispType(dispType_Array[i]);
			gpod.setDispMode(dispMode_Array[i]);
			gpod.setAssignStatus(assignStatus_Array[i]);
			gpod.setDispStatus(dispStatus_Array[i]);
			gpod.setAcceptStatus(acceptStatus_Array[i]);
			gpod.setSendFlag(sendFlag_Array[i]);*/
			matConfirmDets.add(gpod);
		}
		//设置数据
		gpodDto.setGsPlanOptDets(matConfirmDets);
		//消息ID
		gpodDto.setMsgId(AppModConfig.msgId);
		AppModConfig.msgId++;
		// 消息id小于0判断
		AppModConfig.msgIdLessThan0Judge();

		return gpodDto;
	}

	// 配货计划操作详情列表函数
	GsPlanOptDetsOutsideDTO gsPlanOptDetsFromHive(String[] dates,
			List<String> distrBatNumberList,
			DbHiveGsService dbHiveGsService
			) {
		GsPlanOptDetsOutsideDTO gpodDto = new GsPlanOptDetsOutsideDTO();
		List<GsPlanOptDetsOutside> gsPlanOptDets = new ArrayList<>();

		// 时间段内各区餐厨垃圾学校回收总数
		String startDate = dates[dates.length - 1];
		String endDate = dates[0];
		if(startDate==null || startDate.split("-").length < 2) {
    		startDate = BCDTimeUtil.convertNormalDate(null);
    	}
    	if((endDate==null || endDate.split("-").length < 2)&& startDate!=null) {
    		endDate = startDate;
    	}else if (endDate==null || endDate.split("-").length < 2) {
    		endDate = BCDTimeUtil.convertNormalDate(null);
    	}

    	String [] yearMonths = new String [4];
    	//根据开始日期、结束日期，获取开始日期和结束日期的年、月
    	yearMonths = CommonUtil.getYearMonthByDate(startDate, endDate);
    	String startYear = yearMonths[0];
    	String startMonth = yearMonths[1];
    	String endYear = yearMonths[2];
    	String endMonth = yearMonths[3];

		//结束日期+1天，方便查询处理
		String endDateAddOne = CommonUtil.dateAddDay(endDate, 1);
		//获取开始日期、结束日期的年月集合
		List<String> listYearMonth = CommonUtil.getYearMonthList(startYear, startMonth, endYear, endMonth);

        List<GsPlanOptCommonDets> dishList = new ArrayList<>();

        dishList = dbHiveGsService.getGsDetsList(listYearMonth, startDate, endDateAddOne, null,
                -1, -1, -1, null, -1, -1, null, null,
                null, null, -1, -1, null,
                null, null, null, null,
                null, -1, -1, -1,
                null, null, -1, -1,
                null, null, null,
                distrBatNumberList,
                2, null, null);
        dishList.removeAll(Collections.singleton(null));
        for (GsPlanOptCommonDets commonDets : dishList) {
            GsPlanOptDetsOutside pdd = new GsPlanOptDetsOutside();
            try {
                BeanUtils.copyProperties(pdd, commonDets);
                //配货单操作状态
                pdd.setPlaStatus(commonDets.getDisDealStatus());

                gsPlanOptDets.add(pdd);

            } catch (IllegalAccessException e) {
                e.printStackTrace();
                logger.info(e.getMessage());
            } catch (InvocationTargetException e) {
                e.printStackTrace();
                logger.info(e.getMessage());
            }
        }

        //时戳
        gpodDto.setTime(BCDTimeUtil.convertNormalFrom(null));
        //设置数据
        gpodDto.setGsPlanOptDets(gsPlanOptDets);
        //消息ID
        gpodDto.setMsgId(AppModConfig.msgId);
        AppModConfig.msgId++;
        // 消息id小于0判断
        AppModConfig.msgIdLessThan0Judge();

        return gpodDto;
    }


    // 配货计划操作详情列表函数
    GsPlanOptDetsOutsideDTO gsPlanOptDets(String[] dates, List<String> distrBatNumberList) {
        GsPlanOptDetsOutsideDTO gpodDto = new GsPlanOptDetsOutsideDTO();
        //配货状态列表
        List<GsPlanOptDetsOutside> gsPlanOptDets = new ArrayList<>();
        Map<String, String> distributionDetailMap = new HashMap<>();
        int k, dateCount = dates.length;
        String key = null;
        String[] keyVals = null;

        // 时间段内各区配货计划详情
        for (k = 0; k < dateCount; k++) {
            key = dates[k] + "_Distribution-Detail";
            //Feild:(id_主键id_type_配送类型（1 原料，2 成品菜）_schoolid_学校id_area_区号_sourceid_团餐公司id_batchno_发货批次号_delivery_统配/直配)
            //Value:配送状态_deliveryDate_验收上报时间_disstatus_配送规则_purchaseDate_进货时间_deliveryReDate_验收时间（手动）
            distributionDetailMap = redisService.getHashByKey(SpringConfig.RedisConnPool.REDISCLUSTER1.value, SpringConfig.RedisDBIdx, key);
            if (distributionDetailMap != null) {
                for (String curKey : distributionDetailMap.keySet()) {
                    keyVals = distributionDetailMap.get(curKey).split("_");
                    // 配货计划列表
                    String[] curKeys = curKey.split("_");
                    if (curKeys.length >= 14) {
                        GsPlanOptDetsOutside gpod = new GsPlanOptDetsOutside();

                        //如果value值为空或者value第一个值不是数字，则不做统计
                        if (keyVals.length < 1) {
                            continue;
                        }

                        //配货日期
                        gpod.setDistrDate(dates[k].replaceAll("-", "/"));

                        //配货批次号
                        gpod.setDistrBatNumber(curKeys[11]);
                        //操作状态
                        if (keyVals.length >= 5 && CommonUtil.isNotEmpty(keyVals[4])) {
                            gpod.setPlaStatus(keyVals[4]);
                        } else {
                            gpod.setPlaStatus("");
                        }

                        //条件判断
                        if (distrBatNumberList != null && distrBatNumberList.size() > 0) {
                            if (!distrBatNumberList.contains(String.valueOf(gpod.getDistrBatNumber()))) {
                                continue;
                            }
                        }
                        gsPlanOptDets.add(gpod);
                    } else
                        logger.info("配货计划：" + curKey + "，格式错误！");
                }
            }
        }
        //排序
        SortList<GsPlanOptDetsOutside> sortList = new SortList<GsPlanOptDetsOutside>();
        sortList.Sort3Level(gsPlanOptDets, methods, sorts, dataTypes);
        //时戳
        gpodDto.setTime(BCDTimeUtil.convertNormalFrom(null));
        //设置数据
        gpodDto.setGsPlanOptDets(gsPlanOptDets);
        //消息ID
        gpodDto.setMsgId(AppModConfig.msgId);
        AppModConfig.msgId++;
        // 消息id小于0判断
        AppModConfig.msgIdLessThan0Judge();

        return gpodDto;
    }

    //配货计划操作详情列表模型函数
    public GsPlanOptDetsOutsideDTO appModFunc(String token, String startDate, String endDate,
                                              List<String> distrBatNumberList,
                                              Db1Service db1Service, Db2Service db2Service, SaasService saasService,
                                              DbHiveGsService dbHiveGsService) {
        GsPlanOptDetsOutsideDTO gpodDto = null;
        if (isRealData) { // 真实数据
            // 日期
            String[] dates = null;
            if (startDate == null || endDate == null) { // 按照当天日期获取数据
                dates = new String[1];
                dates[0] = BCDTimeUtil.convertNormalDate(null);
            } else { // 按照开始日期和结束日期获取数据
                DateTime startDt = BCDTimeUtil.convertDateStrToDate(startDate);
                DateTime endDt = BCDTimeUtil.convertDateStrToDate(endDate);
                // 两个日期之前的整天数
                int days = Days.daysBetween(startDt, endDt).getDays() + 1;
                dates = new String[days];
                for (int i = 0; i < days; i++) {
                    dates[i] = endDt.minusDays(i).toString("yyyy-MM-dd");
                    logger.info("dates[" + i + "] = " + dates[i]);
                }
            }

            DateTime startDt = BCDTimeUtil.convertDateStrToDate(dates[dates.length - 1]);
            DateTime currentTime = new DateTime();
            int days = Days.daysBetween(startDt, currentTime).getDays();
            if (days >= 2) {
                gpodDto = gsPlanOptDetsFromHive(dates, distrBatNumberList,
                        dbHiveGsService);
            } else {
                gpodDto = gsPlanOptDets(dates, distrBatNumberList);
            }
        } else { // 模拟数据
            //模拟数据函数
            gpodDto = SimuDataFunc();
        }

		return gpodDto;
	}
}