package com.tfit.BdBiProcSrvShEduOmc.appmod.iw;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.joda.time.DateTime;
import org.joda.time.Days;
import org.springframework.beans.factory.annotation.Autowired;

import com.tfit.BdBiProcSrvShEduOmc.config.AppModConfig;
import com.tfit.BdBiProcSrvShEduOmc.dto.PageBean;
import com.tfit.BdBiProcSrvShEduOmc.dto.PageInfo;
import com.tfit.BdBiProcSrvShEduOmc.dto.iw.WarnCommon;
import com.tfit.BdBiProcSrvShEduOmc.dto.iw.WarnMaterialOverPro;
import com.tfit.BdBiProcSrvShEduOmc.dto.iw.WarnMaterialOverProDTO;
import com.tfit.BdBiProcSrvShEduOmc.obj.base.DepartmentObj;
import com.tfit.BdBiProcSrvShEduOmc.service.Db1Service;
import com.tfit.BdBiProcSrvShEduOmc.service.Db2Service;
import com.tfit.BdBiProcSrvShEduOmc.service.DbHiveWarnService;
import com.tfit.BdBiProcSrvShEduOmc.service.RedisService;
import com.tfit.BdBiProcSrvShEduOmc.util.BCDTimeUtil;
import com.tfit.BdBiProcSrvShEduOmc.util.CommonUtil;

//证照预警学校证件列表应用模型
public class WarnMaterialOverProAppMod {
	private static final Logger logger = LogManager.getLogger(WarnMaterialOverProAppMod.class.getName());
	
	//Redis服务
	@Autowired
	RedisService redisService = new RedisService();
	
	//是否为真实数据标识
	private static boolean isRealData = true;
	//页号、页大小和总页数
	int curPageNum = 1, pageTotal = 1, pageSize = 20;
	
	/**
	 * 方法类型索引
	 */
	int methodIndex = 1;
	
	//数组数据初始化
	String[] warnPeriod_Array = {"2018/09/03-2018/09/04", "2018/09/03-2018/09/04", "2018/09/03-2018/09/04"};
	String[] distName_Array = {"11", "11", "10"};
	int[] totalWarnNum_Array = {100, 100, 100};
	int[] noProcWarnNum_Array = {80, 80, 80};
	int[] rejectWarnNum_Array = {0, 0, 0};
	int[] auditWarnNum_Array = {10, 10, 10};
	int[] elimWarnNum_Array = {10, 10, 10};
	float[] warnProcRate_Array = {(float) 10.00, (float) 10.00, (float) 10.00};
	
	//模拟数据函数
	private WarnMaterialOverProDTO SimuDataFunc() {
		WarnMaterialOverProDTO wslDto = new WarnMaterialOverProDTO();
		//时戳
		wslDto.setTime(BCDTimeUtil.convertNormalFrom(null));
		//证照预警学校证件列表模拟数据
		List<WarnMaterialOverPro> WarnMaterialOverPro = new ArrayList<>();
		//赋值
		for (int i = 0; i < warnPeriod_Array.length; i++) {
			WarnMaterialOverPro wsl = new WarnMaterialOverPro();
			wsl.setWarnPeriod(warnPeriod_Array[i]);
			wsl.setTotalWarnNum(totalWarnNum_Array[i]);
			wsl.setNoProcWarnNum(noProcWarnNum_Array[i]);
			wsl.setElimWarnNum(elimWarnNum_Array[i]);
			wsl.setWarnProcRate(warnProcRate_Array[i]);
			WarnMaterialOverPro.add(wsl);
		}
		//设置数据
		wslDto.setWarnMaterialOverPro(WarnMaterialOverPro);
		//分页
		PageInfo pageInfo = new PageInfo();
		pageTotal = distName_Array.length;
		pageInfo.setPageTotal(pageTotal);
		pageInfo.setCurPageNum(curPageNum);
		wslDto.setPageInfo(pageInfo);
		//消息ID
		wslDto.setMsgId(AppModConfig.msgId);
		AppModConfig.msgId++;
		// 消息id小于0判断
		AppModConfig.msgIdLessThan0Judge();
		
		return wslDto;
	}
	
	// 证照预警学校证件列表函数
	private WarnMaterialOverProDTO WarnMaterialOverPro(String departmentId, String startDate, 
			String endDate,List<Object> departmentIdsList, List<DepartmentObj> departmentList,Integer target,
			DbHiveWarnService dbHiveWarnService) {
		WarnMaterialOverProDTO wslDto = new WarnMaterialOverProDTO();
		List<WarnMaterialOverPro> WarnMaterialOverPro = new ArrayList<>();
		WarnMaterialOverPro wsl = null;
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
		
		/**
		 * 1.从hive库中获取汇总数据
		 */
		List<WarnCommon> warnCommonList = new ArrayList<>();
		warnCommonList = dbHiveWarnService.getWarnList(3, null, target, listYearMonth, startDate, endDateAddOne,
				null, null, -1, -1, null, null, departmentId, departmentIdsList, 4);
		
		/**
		 * 2.合并数据(按管理部门合并)
		 */
		Map<String, WarnMaterialOverPro> warnTotalMap = new HashMap<String, WarnMaterialOverPro>();
		for(WarnCommon warnCommon : warnCommonList) {
			WarnMaterialOverPro warnCommonLicsTemp =warnTotalMap.get(warnCommon.getDepartmentId());
			if(warnCommonLicsTemp == null) {
				warnCommonLicsTemp = new WarnMaterialOverPro();
				warnCommonLicsTemp.setDepartmentName(warnCommon.getDepartmentId());
			}
			warnCommonLicsTemp.setTotalWarnNum(warnCommonLicsTemp.getTotalWarnNum() + 
					warnCommon.getNoProcWarnNum() + warnCommon.getRejectWarnNum() + warnCommon.getAuditWarnNum() + warnCommon.getElimWarnNum());
			warnCommonLicsTemp.setElimWarnNum(warnCommonLicsTemp.getElimWarnNum() + warnCommon.getElimWarnNum());
			
			warnTotalMap.put(warnCommon.getDepartmentId(), warnCommonLicsTemp);
		}
		
		/**
		 * 3.计算每个区的合计和预警率
		 */
		// 时间段内各区预警统计
		String startDateTemp = startDate.replaceAll("-", "/");
		String endDateTemp = endDate.replaceAll("-", "/");
		float warnProcRate = 0;
		for (DepartmentObj departmentObj : departmentList) {
			warnProcRate = 0;
			wsl = warnTotalMap.get(departmentObj.getDepartmentId());
			if(wsl ==null ) {
				wsl = new WarnMaterialOverPro();
			}
			wsl.setDepartmentId(departmentObj.getDepartmentId());
			wsl.setDepartmentName(departmentObj.getDepartmentName());
			
			wsl.setWarnPeriod(startDateTemp + "-" + endDateTemp);
			//wsl.setTotalWarnNum(wsl.getTotalWarnNum());
			wsl.setNoProcWarnNum(wsl.getTotalWarnNum() - wsl.getElimWarnNum());
			warnProcRate = 0;
			if(wsl.getTotalWarnNum() > 0) {
				warnProcRate = 100 * ((float) wsl.getElimWarnNum() / (float) wsl.getTotalWarnNum());
				BigDecimal bd = new BigDecimal(warnProcRate);
				warnProcRate = bd.setScale(2, BigDecimal.ROUND_HALF_UP).floatValue();
				if (warnProcRate > 100)
					warnProcRate = 100;
			}
			wsl.setWarnProcRate(warnProcRate);
			WarnMaterialOverPro.add(wsl);
		}
		
		// 设置返回数据
		wslDto.setTime(BCDTimeUtil.convertNormalFrom(null));
		// 分页
		if(curPageNum==-1 || pageSize==-1) {
			PageInfo pageInfo = new PageInfo();
			pageInfo.setPageTotal(WarnMaterialOverPro.size());
			pageInfo.setCurPageNum(curPageNum);
			wslDto.setPageInfo(pageInfo);
			// 设置数据
			wslDto.setWarnMaterialOverPro(WarnMaterialOverPro);
		}else {
			PageBean<WarnMaterialOverPro> pageBean = new PageBean<WarnMaterialOverPro>(WarnMaterialOverPro, curPageNum, pageSize);
			PageInfo pageInfo = new PageInfo();
			pageInfo.setPageTotal(pageBean.getTotalCount());
			pageInfo.setCurPageNum(curPageNum);
			wslDto.setPageInfo(pageInfo);
			// 设置数据
			wslDto.setWarnMaterialOverPro(pageBean.getCurPageData());
		}
		// 消息ID
		wslDto.setMsgId(AppModConfig.msgId);
		AppModConfig.msgId++;
		// 消息id小于0判断
		AppModConfig.msgIdLessThan0Judge();

		return wslDto;
	}		
	
	// 证照预警学校证件列表模型函数
	public WarnMaterialOverProDTO appModFunc(String token, String startWarnDate, String endWarnDate, String distName, String prefCity, 
			String province, 
			String departmentIds,
			String page, String pageSize, Db1Service db1Service, Db2Service db2Service,DbHiveWarnService dbHiveWarnService) {
		WarnMaterialOverProDTO wslDto = null;
		if(page != null)
			curPageNum = Integer.parseInt(page);
		if(pageSize != null)
			this.pageSize = Integer.parseInt(pageSize);
		if(isRealData) {       //真实数据
			// 日期
			String[] dates = null;
			if (startWarnDate == null || endWarnDate == null) { // 按照当天日期获取数据
				dates = new String[1];
				dates[0] = BCDTimeUtil.convertNormalDate(null);
			} else { // 按照开始日期和结束日期获取数据
				DateTime startDt = BCDTimeUtil.convertDateStrToDate(startWarnDate);
				DateTime endDt = BCDTimeUtil.convertDateStrToDate(endWarnDate);
				int days = Days.daysBetween(startDt, endDt).getDays() + 1;
				dates = new String[days];
				for (int i = 0; i < days; i++) {
					dates[i] = endDt.minusDays(i).toString("yyyy-MM-dd");
				}
			}
			for (int i = 0; i < dates.length; i++) {
				logger.info("dates[" + i + "] = " + dates[i]);
			}
			// 省或直辖市
			if(province == null)
				province = "上海市";
			// 参数查找标识
			boolean bfind = true;
			String departmentId = null;
			// 按不同参数形式处理
			if (distName != null && prefCity == null && province != null) { // 按区域，省或直辖市处理
				// 存在则获取数据
				if (bfind) {
					if(departmentId == null)
						departmentId = AppModConfig.getUserDataPermDistId(token, db1Service, db2Service);  //获取用户权限区域ID
					// 证照预警学校证件列表函数
					if(methodIndex == 1) {
						List<Object> departmentIdsList=CommonUtil.changeStringToList(departmentIds);
						DepartmentObj departmentObj = new DepartmentObj();
						departmentObj.setDepartmentId(departmentId);
						List<DepartmentObj> deparmentList =  db1Service.getDepartmentObjList(departmentObj,departmentIdsList, -1, -1);	
						Integer target = CommonUtil.getTarget(token, db1Service, db2Service);
						wslDto = WarnMaterialOverPro(departmentId, startWarnDate,endWarnDate,departmentIdsList,deparmentList,target,dbHiveWarnService);
					}
				}
			} else if (distName == null && prefCity == null && province != null) { // 按省或直辖市处理
				if (bfind) {
					if(departmentId == null)
						departmentId = AppModConfig.getUserDataPermDistId(token, db1Service, db2Service);  //获取用户权限区域ID
					// 证照预警学校证件列表函数
					if(methodIndex == 1) {
						List<Object> departmentIdsList=CommonUtil.changeStringToList(departmentIds);
						DepartmentObj departmentObj = new DepartmentObj();
						departmentObj.setDepartmentId(departmentId);
						List<DepartmentObj> deparmentList =  db1Service.getDepartmentObjList(departmentObj,departmentIdsList, -1, -1);	
						Integer target = CommonUtil.getTarget(token, db1Service, db2Service);
						wslDto = WarnMaterialOverPro(departmentId, startWarnDate,endWarnDate,departmentIdsList,deparmentList,target,dbHiveWarnService);
					}
				}
			} else if (distName != null && prefCity != null && province != null) { // 按区域，地级市，省或直辖市处理

			} else if (distName == null && prefCity != null && province != null) { // 地级市，省或直辖市处理

			} else {
				logger.info("访问接口参数非法！");
			}													
		}
		else {    //模拟数据
			//模拟数据函数
			wslDto = SimuDataFunc();
		}		

		return wslDto;
	}
}
