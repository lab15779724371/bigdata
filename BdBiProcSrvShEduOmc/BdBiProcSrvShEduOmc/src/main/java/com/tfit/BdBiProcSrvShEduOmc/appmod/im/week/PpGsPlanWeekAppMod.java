package com.tfit.BdBiProcSrvShEduOmc.appmod.im.week;

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
import com.tfit.BdBiProcSrvShEduOmc.dto.PageInfo;
import com.tfit.BdBiProcSrvShEduOmc.dto.im.week.AppTEduLedgerMasterTotalWObj;
import com.tfit.BdBiProcSrvShEduOmc.dto.im.week.PpGsPlanWeekDto;
import com.tfit.BdBiProcSrvShEduOmc.dto.im.week.PpGsPlanWeekOutDto;
import com.tfit.BdBiProcSrvShEduOmc.service.Db1Service;
import com.tfit.BdBiProcSrvShEduOmc.service.Db2Service;
import com.tfit.BdBiProcSrvShEduOmc.service.DbHiveGsService;
import com.tfit.BdBiProcSrvShEduOmc.service.RedisService;
import com.tfit.BdBiProcSrvShEduOmc.util.BCDTimeUtil;
import com.tfit.BdBiProcSrvShEduOmc.util.CommonUtil;

//配货周报表
public class PpGsPlanWeekAppMod {
	private static final Logger logger = LogManager.getLogger(PpGsPlanWeekAppMod.class.getName());
	
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
	PpGsPlanWeekOutDto ppGsPlanWeekFromHive(String[] dates, 
			AppTEduLedgerMasterTotalWObj inputObj,
			DbHiveGsService dbHiveGsService) {
		
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
			
		List<AppTEduLedgerMasterTotalWObj> appTEduPlatoonTotalWList = dbHiveGsService.getAppTEduLedgerMasterTotalWObjList(listYearMonth, startDate, endDateAddOne, 
				inputObj, statPage, endPage);
		appTEduPlatoonTotalWList.removeAll(Collections.singleton(null));
		
		Integer pageTotalTemp = dbHiveGsService.getAppTEduLedgerMasterTotalWObjListCount(listYearMonth, startDate, endDateAddOne, inputObj);
		
		if(pageTotalTemp!=null) {
			pageTotal = pageTotalTemp;
		}
		
		List<PpGsPlanWeekDto> ppGsPlanWeekDto = new ArrayList<>();
		for(AppTEduLedgerMasterTotalWObj obj : appTEduPlatoonTotalWList) {
			PpGsPlanWeekDto pdd = new PpGsPlanWeekDto();
			try {
				BeanUtils.copyProperties(pdd, obj);
				
				pdd.setActionDatePeriod((obj.getStartActionDate()==null?"":obj.getStartActionDate().replaceAll("-", "/")) 
						+ "-" + (obj.getEndActionDate()==null?"":obj.getEndActionDate().replaceAll("-", "/")));
				
				//学制
				pdd.setLevelId(obj.getLevelName());
				if(CommonUtil.isNotEmpty(obj.getLevelName())) {
					pdd.setLevelName(AppModConfig.schTypeIdToNameMap.get(Integer.valueOf(obj.getLevelName())));
				}else {
					pdd.setLevelName("");
				}
				//学校性质
				pdd.setSchoolNatureId(obj.getSchoolNatureName());
				pdd.setSchoolNatureName("-");
				if(CommonUtil.isNotEmpty(obj.getSchoolNatureName()) && !"-1".equals(obj.getSchoolNatureName())) {
					pdd.setSchoolNatureName(AppModConfig.schPropIdToNameMap.get(Integer.valueOf(obj.getSchoolNatureName())));
				}else {
					pdd.setSchoolNatureName("-");
				}
				
				//所属
				pdd.setDepartmentMasterName("-");
				if(CommonUtil.isNotEmpty(obj.getDepartmentMasterId()) && !"-1".equals(obj.getDepartmentMasterId())) {
					pdd.setDepartmentMasterName(AppModConfig.subLevelIdToNameMap.get(Integer.valueOf(obj.getDepartmentMasterId())));
				}
				//主管部门
				pdd.setDepartmentSlaveId(obj.getDepartmentSlaveIdName());
				pdd.setDepartmentSlaveIdName("-");
				if(CommonUtil.isNotEmpty(obj.getDepartmentMasterId())&& !"-1".equals(obj.getDepartmentMasterId())
						&& !"-1".equals(obj.getDepartmentSlaveIdName())) {
					if("0".equals(obj.getDepartmentMasterId())) {
						pdd.setDepartmentSlaveIdName(AppModConfig.compDepIdToNameMap0.get(obj.getDepartmentSlaveIdName()));
					}else if ("1".equals(obj.getDepartmentMasterId())) {
						pdd.setDepartmentSlaveIdName(AppModConfig.compDepIdToNameMap1.get(obj.getDepartmentSlaveIdName()));
					}else if ("2".equals(obj.getDepartmentMasterId())) {
						pdd.setDepartmentSlaveIdName(AppModConfig.compDepIdToNameMap2.get(obj.getDepartmentSlaveIdName()));
					}else if ("3".equals(obj.getDepartmentMasterId())) {
						pdd.setDepartmentSlaveIdName(AppModConfig.compDepIdToNameMap3.get(obj.getDepartmentSlaveIdName()));
					}
				}
				
				//所属区域名称
				pdd.setSchoolAreaName("-");
				if(CommonUtil.isNotEmpty(obj.getSchoolAreaId()) && !"-1".equals(obj.getSchoolAreaId())&&
						AppModConfig.distIdToNameMap.get(obj.getSchoolAreaId()) !=null ) {
					pdd.setSchoolAreaName(AppModConfig.distIdToNameMap.get(obj.getSchoolAreaId()));
				}
				
				//食品经营许可证主体
				pdd.setLicenseMainTypeName("-");
				if(CommonUtil.isNotEmpty(obj.getLicenseMainType()) && CommonUtil.isInteger(obj.getLicenseMainType())) {
					pdd.setLicenseMainTypeName(AppModConfig.fblMbIdToNameMap.get(Integer.parseInt(obj.getLicenseMainType())));
				}
				
				//供餐模式
				pdd.setLicenseMainChildName("-");
				if(obj.getLicenseMainChild() != null) {
					String licenseMainType = obj.getLicenseMainType();
					Integer licenseMainChild = obj.getLicenseMainChild();
					//新的经营模式判断
			  		if(licenseMainType != null) {
			  			if(licenseMainType.equals("0")) {    //学校
			  				if(licenseMainChild != null) {
			  					if(licenseMainChild == 0)
			  						pdd.setLicenseMainChildName("自营");
			  					else if(licenseMainChild == 1)
			  						pdd.setLicenseMainChildName("自营");
			  				}
			  				pdd.setLicenseMainChildName("自营");
			  			}
			  			else if(licenseMainType.equals("1")) {    //外包
			  				if(licenseMainChild != null) {
			  					if(licenseMainChild == 0)
			  						pdd.setLicenseMainChildName("托管");
			  					else if(licenseMainChild == 1)
			  						pdd.setLicenseMainChildName("外送");
			  				}
			  			}
			  		}
				}
				
				//总分校标识
				pdd.setIsBranchSchoolName("-");
				if(pdd.getIsBranchSchool() > -1) {
					if(pdd.getIsBranchSchool() == 1) { //分校
						pdd.setIsBranchSchoolName("分校");
					}
					else {   //总校
						pdd.setIsBranchSchoolName("总校");
					}
				}
				
				ppGsPlanWeekDto.add(pdd);
				
			} catch (IllegalAccessException e) {
				e.printStackTrace();
				logger.info(e.getMessage());
			} catch (InvocationTargetException e) {
				e.printStackTrace();
				logger.info(e.getMessage());
			}
		}
		
		PpGsPlanWeekOutDto pddDto = new PpGsPlanWeekOutDto();
		// 设置返回数据
		pddDto.setTime(BCDTimeUtil.convertNormalFrom(null));
		// 分页
		PageInfo pageInfo = new PageInfo();
		pageInfo.setPageTotal(pageTotal);
		pageInfo.setCurPageNum(curPageNum);
		pddDto.setPageInfo(pageInfo);
		//设置数据
		pddDto.setPpGsPlanWeekList(ppGsPlanWeekDto);
		//消息ID
		pddDto.setMsgId(AppModConfig.msgId);
		AppModConfig.msgId++;
		// 消息id小于0判断
		AppModConfig.msgIdLessThan0Judge();
		
		return pddDto;
	}	

	//项目点排菜详情列表模型函数
	public PpGsPlanWeekOutDto appModFunc(String token,  
			AppTEduLedgerMasterTotalWObj inputObj,
			Db1Service db1Service, 
			Db2Service db2Service,
			DbHiveGsService dbHiveGsService) {
		
		if(inputObj == null) {
			inputObj = new AppTEduLedgerMasterTotalWObj();
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
			if (inputObj.getStartActionDate() == null) { // 按照当天日期获取数据
				dates = new String[1];
				dates[0] = BCDTimeUtil.convertNormalDate(null);
				inputObj.setStartActionDate(BCDTimeUtil.convertNormalDate(null));
			} else { // 按照开始日期和结束日期获取数据
				DateTime startDt = BCDTimeUtil.convertDateStrToDate(inputObj.getStartActionDate());
				DateTime endDt = BCDTimeUtil.convertDateStrToDate(inputObj.getEndActionDate());
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
					/*DateTime startDt = BCDTimeUtil.convertDateStrToDate(dates[dates.length-1]);
					DateTime currentTime = new DateTime();
					int days = Days.daysBetween(startDt, currentTime).getDays();*/
					//if(days >= 2) {
						return ppGsPlanWeekFromHive(dates, inputObj, dbHiveGsService);
					//}
				}
			} else if (inputObj.getArea() == null && inputObj.getPrefCity() == null && inputObj.getProvince() != null) { // 按省或直辖市处理
				if (bfind) {
					if(departmentId == null)
						departmentId = AppModConfig.getUserDataPermDistId(token, db1Service, db2Service);  //获取用户权限区域ID
					if(CommonUtil.isNotEmpty(departmentId)) {
						inputObj.setDepartmentId(departmentId);
					}
					
					//DateTime startDt = BCDTimeUtil.convertDateStrToDate(dates[dates.length-1]);
					//DateTime currentTime = new DateTime();
					//int days = Days.daysBetween(startDt, currentTime).getDays();
					//if(days >= 2) {
						return ppGsPlanWeekFromHive(dates, inputObj, dbHiveGsService);
					//}
				}
			} else if (inputObj.getArea() != null && inputObj.getPrefCity() != null && inputObj.getProvince() != null) { // 按区域，地级市，省或直辖市处理

			} else if (inputObj.getArea() == null && inputObj.getPrefCity() != null && inputObj.getProvince() != null) { // 地级市，省或直辖市处理

			} else {
				logger.info("访问接口参数非法！");
			}			
		} else { // 模拟数据
			//模拟数据函数
		}

		List<PpGsPlanWeekDto> dishList = new ArrayList<>();
		PpGsPlanWeekOutDto pddDto = new PpGsPlanWeekOutDto();
		// 设置返回数据
		pddDto.setTime(BCDTimeUtil.convertNormalFrom(null));
		// 分页
		PageInfo pageInfo = new PageInfo();
		pageInfo.setPageTotal(pageTotal);
		pageInfo.setCurPageNum(curPageNum);
		pddDto.setPageInfo(pageInfo);
		//设置数据
		pddDto.setPpGsPlanWeekList(dishList);
		//消息ID
		pddDto.setMsgId(AppModConfig.msgId);
		AppModConfig.msgId++;
		// 消息id小于0判断
		AppModConfig.msgIdLessThan0Judge();
		return pddDto;
	}



}