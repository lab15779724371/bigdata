package com.tfit.BdBiProcSrvShEduOmc.appmod.rc;

import java.lang.reflect.InvocationTargetException;
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
import com.tfit.BdBiProcSrvShEduOmc.dto.rc.DishWarnSumListRepsDto;
import com.tfit.BdBiProcSrvShEduOmc.dto.rc.DishWarnSumListRepsSumOutDto;
import com.tfit.BdBiProcSrvShEduOmc.obj.warn.AppTEduNoLedgerCollectD;
import com.tfit.BdBiProcSrvShEduOmc.service.Db1Service;
import com.tfit.BdBiProcSrvShEduOmc.service.Db2Service;
import com.tfit.BdBiProcSrvShEduOmc.service.DbHiveWarnService;
import com.tfit.BdBiProcSrvShEduOmc.service.RedisService;
import com.tfit.BdBiProcSrvShEduOmc.util.BCDTimeUtil;
import com.tfit.BdBiProcSrvShEduOmc.util.CommonUtil;

//排菜周报表
public class AcceptWarnSumListRepsSumAppMod {
	private static final Logger logger = LogManager.getLogger(AcceptWarnSumListRepsSumAppMod.class.getName());
	
	//Redis服务
	@Autowired
	RedisService redisService = new RedisService();
	
	//三级排序条件
	final String[] methods = {"getDistName", "getSchType", "getPpName"};
	final String[] sorts = {"asc", "asc", "asc"};
	final String[] dataTypes = {"String", "String", "String"};
	
	//是否为真实数据标识
	private static boolean isRealData = true;
	//页号、页大小和总页数
	int curPageNum = 1, pageTotal = 1, pageSize = 20, actPageSize = 0, attrCount = 9;
	
	/**
	 * 方法类型索引
	 */
	int methodIndex = 1;
	
	//数组数据初始化
	String[] dishDate_Array = {"2018/08/08", "2018/08/08"};
	String[] ppName_Array = {"上海市天山中学", "上海市天山中学"};	
	String[] schGenBraFlag_Array = {"总校", "总校"};
	int[] braCampusNum_Array = {1, 1};
	String[] relGenSchName_Array = {"-", "-"};
	String[] subLevel_Array = {"区属", "区属"};
	String[] compDep_Array = {"长宁区教育局", "长宁区教育局"};
	String[] subDistName_Array = {"-", "-"};
	String[] fblMb_Array = {"学校", "学校"};
	String[] distName_Array = {"长宁区", "长宁区"};
	String[] schType_Array = {"初级中学", "初级中学"};
	String[] schProp_Array = {"公办", "公办"};
	int[] mealFlag_Array = {1, 1};
	String[] optMode_Array = {"外包-外包", "外包-外送"};
	String[] rmcName_Array = {"上海绿捷", "上海神龙"};
	int[] dishFlag_Array = {1, 1};
	
	//团餐公司id到团餐公司名称
	Map<String, String> RmcIdToNameMap = new HashMap<>();
	
	// 项目点排菜详情列表函数（方案2）
	DishWarnSumListRepsSumOutDto ppDishWeekFromHive(String[] dates, 
			AppTEduNoLedgerCollectD inputObj,
			DbHiveWarnService dbHiveWarnService) {
		
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

		Integer statPage = (curPageNum-1)*pageSize;
		Integer endPage = curPageNum*pageSize;
		if(curPageNum == -1 ||pageSize == -1) {
			statPage = -1;
			endPage = -1;
		}
		
		AppTEduNoLedgerCollectD appTEduNoPlatoonCollectDList = dbHiveWarnService.getAppTEduNoLedgerCollectDListSum(listYearMonth, startDate, endDateAddOne, 
				inputObj);
		
		DishWarnSumListRepsDto pdd = new DishWarnSumListRepsDto();
		try {
			BeanUtils.copyProperties(pdd, appTEduNoPlatoonCollectDList);
			
		} catch (IllegalAccessException e) {
			e.printStackTrace();
			logger.info(e.getMessage());
		} catch (InvocationTargetException e) {
			e.printStackTrace();
			logger.info(e.getMessage());
		}
		
		DishWarnSumListRepsSumOutDto pddDto = new DishWarnSumListRepsSumOutDto();
		// 设置返回数据
		pddDto.setTime(BCDTimeUtil.convertNormalFrom(null));
		//设置数据
		pddDto.setData(pdd);
		//消息ID
		pddDto.setMsgId(AppModConfig.msgId);
		AppModConfig.msgId++;
		// 消息id小于0判断
		AppModConfig.msgIdLessThan0Judge();
		
		return pddDto;
	}	

	//项目点排菜详情列表模型函数
	public DishWarnSumListRepsSumOutDto appModFunc(String token,  
			AppTEduNoLedgerCollectD inputObj,
			Db1Service db1Service, 
			Db2Service db2Service,
			DbHiveWarnService dbHiveWarnService) {
		
		if(inputObj == null) {
			inputObj = new AppTEduNoLedgerCollectD();
			//return new ApiResponse<>(IOTRspType.Param_VisitFrmErr, IOTRspType.Param_VisitFrmErr.getMsg()); 
		}
		
		if(inputObj.getPage() !=null ) {
			this.curPageNum = Integer.parseInt(inputObj.getPage());
		}
		if(inputObj.getPageSize() !=null ) {
			this.pageSize = Integer.parseInt(inputObj.getPageSize());
		}
		if (isRealData) { // 真实数据
			// 日期
			String[] dates = null;
			if (inputObj.getWarnStartDate() == null) { // 按照当天日期获取数据
				dates = new String[1];
				dates[0] = BCDTimeUtil.convertNormalDate(null);
				inputObj.setWarnStartDate(BCDTimeUtil.convertNormalDate(null));
			} else { // 按照开始日期和结束日期获取数据
				DateTime startDt = BCDTimeUtil.convertDateStrToDate(inputObj.getWarnStartDate());
				DateTime endDt = BCDTimeUtil.convertDateStrToDate(inputObj.getWarnEndDate());
				int days = Days.daysBetween(startDt, endDt).getDays() + 1;
				dates = new String[days];
				for (int i = 0; i < days; i++) {
					dates[i] = endDt.minusDays(i).toString("yyyy-MM-dd");
					logger.info("dates[" + i + "] = " + dates[i]);
				}
			}
			// 省或直辖市
			if(inputObj.getProvince() == null)
				inputObj.setProvince("上海市");
			// 参数查找标识
			boolean bfind = true;
			String departmentId = null;
			// 按不同参数形式处理
			if (inputObj.getArea() != null && inputObj.getPrefCity() == null && inputObj.getProvince() != null) {    // 按区域，省或直辖市处理
				// 存在则获取数据
				if (bfind) {
					if(departmentId == null)
						departmentId = AppModConfig.getUserDataPermDistId(token, db1Service, db2Service);  //获取用户权限区域ID
					if(CommonUtil.isNotEmpty(departmentId)) {
						inputObj.setDepartmentId(departmentId);
					}
					// 项目点排菜详情列表函数
					return ppDishWeekFromHive(dates, inputObj, dbHiveWarnService);
				}
			} else if (inputObj.getArea() == null && inputObj.getPrefCity() == null && inputObj.getProvince() != null) { // 按省或直辖市处理
				if (bfind) {
					if(departmentId == null)
						departmentId = AppModConfig.getUserDataPermDistId(token, db1Service, db2Service);  //获取用户权限区域ID
					if(CommonUtil.isNotEmpty(departmentId)) {
						inputObj.setDepartmentId(departmentId);
					}
					
					return ppDishWeekFromHive(dates, inputObj, dbHiveWarnService);
				}
			} else if (inputObj.getArea() != null && inputObj.getPrefCity() != null && inputObj.getProvince() != null) { // 按区域，地级市，省或直辖市处理

			} else if (inputObj.getArea() == null && inputObj.getPrefCity() != null && inputObj.getProvince() != null) { // 地级市，省或直辖市处理

			} else {
				logger.info("访问接口参数非法！");
			}			
		} else { // 模拟数据
			//模拟数据函数
		}

		DishWarnSumListRepsDto data = new DishWarnSumListRepsDto();
		DishWarnSumListRepsSumOutDto pddDto = new DishWarnSumListRepsSumOutDto();
		// 设置返回数据
		pddDto.setTime(BCDTimeUtil.convertNormalFrom(null));
		//设置数据
		pddDto.setData(data);
		//消息ID
		pddDto.setMsgId(AppModConfig.msgId);
		AppModConfig.msgId++;
		// 消息id小于0判断
		AppModConfig.msgIdLessThan0Judge();
		return pddDto;
	}



}