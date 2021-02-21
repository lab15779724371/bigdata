package com.tfit.BdBiProcSrvShEduOmc.appmod.rc;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.joda.time.DateTime;
import org.joda.time.Days;

import com.tfit.BdBiProcSrvShEduOmc.client.HdfsRWClient;
import com.tfit.BdBiProcSrvShEduOmc.config.AppModConfig;
import com.tfit.BdBiProcSrvShEduOmc.config.SpringConfig;
import com.tfit.BdBiProcSrvShEduOmc.dto.PageBean;
import com.tfit.BdBiProcSrvShEduOmc.dto.PageInfo;
import com.tfit.BdBiProcSrvShEduOmc.dto.rc.WeeklyOptReps;
import com.tfit.BdBiProcSrvShEduOmc.dto.rc.WeeklyOptRepsDTO;
import com.tfit.BdBiProcSrvShEduOmc.obj.base.DepartmentObj;
import com.tfit.BdBiProcSrvShEduOmc.service.Db1Service;
import com.tfit.BdBiProcSrvShEduOmc.service.Db2Service;
import com.tfit.BdBiProcSrvShEduOmc.util.BCDTimeUtil;
import com.tfit.BdBiProcSrvShEduOmc.util.CommonUtil;
import com.tfit.BdBiProcSrvShEduOmc.util.SortList;

//周运营报告列表应用模型
public class WeeklyOptRepsAppMod {
	private static final Logger logger = LogManager.getLogger(WeeklyOptRepsAppMod.class.getName());
	
	//是否为真实数据标识
	private static boolean isRealData = true;
	//页号、页大小和总页数
	static int curPageNum = 1, pageTotal = 1, pageSize = AppModConfig.maxPageSize;	
	//报表文件资源路径
	String repFileResPath = "/expWeeklyOptReps/";	
	//数组数据初始化
	String[] repId_Array = {"bfe99dcd-ad48-47af-84df-c42c25453637", "e84df234-97bf-4626-a554-aea7f24d8dfd", "6099f13b-991a-4c71-9ec4-8708394d5449", "873138ee-4c0e-400b-949d-a57c94e7cc09", "05cc8b86-9288-401f-b962-50a650bd4d91", "59beea1a-8161-4542-b901-9fb170767f40", "f7d94c6a-6602-4549-ae74-74360526fa9d", "e9a70029-437a-41a0-a425-ce05cd2094d8", "7ec69dae-0585-4db6-aea9-06b4e7d1b871", "442a4f30-c71f-4839-a4b4-27ffefd39b11"};
	String[] repPeriod_Array = {"2019/04/01~2019/04/04", "2019/04/08~2019/04/12", "2019/04/15~2019/04/19", "2019/04/22~2019/04/30", "2019/05/05~2019/05/10", "2019/05/13~2019/05/17", "2019/05/20~2019/05/24", "2019/05/27~2019/05/31", "2019/06/03~2019/06/08", "2019/06/10~2019/06/14"};
	String[] repName_Array = {"2019年食安管理平台使用、验收及证照逾期处理情况（上海市)20190401-0404", "2019年食安管理平台使用、验收及证照逾期处理情况（上海市)20190408-0412", "2019年食安管理平台使用、验收及证照逾期处理情况（上海市)20190415-0419", "2019年食安管理平台使用、验收及证照逾期处理情况（上海市)20190422-0430", "2019年食安管理平台使用、验收及证照逾期处理情况（上海市)20190505-0510", "2019年食安管理平台使用、验收及证照逾期处理情况（上海市)20190513-0517", "2019年食安管理平台使用、验收及证照逾期处理情况（上海市)20190520-0524", "2019年食安管理平台使用、验收及证照逾期处理情况（上海市)20190527-0531", "2019年食安管理平台使用、验收及证照逾期处理情况（上海市)20190603-0609", "2019年食安管理平台使用、验收及证照逾期处理情况（上海市)20190610-0614"};
	String[] repUrl_Array = {"594ee562-03da-42aa-bc59-e88da0b03d16.xlsx", "b50cd49e-20ec-4b02-9c74-d09976152bc4.xlsx", "250c047f-3320-40ae-a25d-f858238c4e71.xlsx", "9fc4602a-52bd-4d0f-999d-73518e7c6f69.xlsx", "868bdbbc-e8f5-4669-8c43-37b183560d44.xlsx", "b753366c-ffa3-4f6f-884d-1c27beedbf33.xlsx", "6336d32e-4f93-494b-8bae-68fc4f0870a0.xlsx", "c29a5347-f4fe-4e21-9b7c-0d5cd3f8dd9b.xlsx", "8403f200-7748-4a9f-93bd-59035f061e39.xlsx", "d0373897-9f5a-49c6-87a7-eccfb1a9853a.xlsx"};
	
	//模拟数据函数
	private WeeklyOptRepsDTO SimuDataFunc(String startDate, String endDate) {
		// 日期
		List<String> searchDates = new ArrayList<>();
		if (startDate == null || endDate == null) { // 按照当天日期获取数据
		} else { // 按照开始日期和结束日期获取数据
			DateTime startDt = BCDTimeUtil.convertDateStrToDate(startDate);
			DateTime endDt = BCDTimeUtil.convertDateStrToDate(endDate);
			int days = Days.daysBetween(startDt, endDt).getDays() + 1;
			for (int i = 0; i < days; i++) {
				searchDates.add(endDt.minusDays(i).toString("yyyy-MM-dd"));
			}
		}
		
		
		WeeklyOptRepsDTO worDto = new WeeklyOptRepsDTO();
		//时戳
		worDto.setTime(BCDTimeUtil.convertNormalFrom(null));
		//周运营报告列表模拟数据
		List<WeeklyOptReps> weeklyOptReps = new ArrayList<>();
		//赋值
		for (int i = 0; i < repId_Array.length; i++) {
			
			if(searchDates !=null && searchDates.size() >0) {
				DateTime startDt = BCDTimeUtil.convertDateStrToDate(repPeriod_Array[i].split("~")[0].replaceAll("/", "-"));
				DateTime endDt = BCDTimeUtil.convertDateStrToDate(repPeriod_Array[i].split("~")[1].replaceAll("/", "-"));
				int days = Days.daysBetween(startDt, endDt).getDays() + 1;
				int index = -1;
				for (int j = 0; j < days; j++) {
					if(searchDates.indexOf(endDt.minusDays(j).toString("yyyy-MM-dd")) > -1) {
						index = searchDates.indexOf(endDt.minusDays(j).toString("yyyy-MM-dd"));
						break;
					}
				}
				if(index <= -1) {
					continue;
				}
			}
			
			WeeklyOptReps wor = new WeeklyOptReps();
			wor.setRepId(repId_Array[i]);
			wor.setRepPeriod(repPeriod_Array[i]);
			wor.setRepName(repName_Array[i]);
			wor.setRepUrl(SpringConfig.repfile_srvdn + repFileResPath + repUrl_Array[i]);
			weeklyOptReps.add(wor);
		}
		//排序
    	SortList<WeeklyOptReps> sortList = new SortList<WeeklyOptReps>();  
    	sortList.Sort(weeklyOptReps, "getRepPeriod", "desc");
		// 分页
		PageBean<WeeklyOptReps> pageBean = new PageBean<WeeklyOptReps>(weeklyOptReps, curPageNum, pageSize);
		PageInfo pageInfo = new PageInfo();
		pageInfo.setPageTotal(pageBean.getTotalCount());
		pageInfo.setCurPageNum(curPageNum);
		worDto.setPageInfo(pageInfo);
		// 设置数据
		worDto.setWeeklyOptReps(pageBean.getCurPageData());
		//消息ID
		worDto.setMsgId(AppModConfig.msgId);
		AppModConfig.msgId++;
		// 消息id小于0判断
		AppModConfig.msgIdLessThan0Judge();
		
		return worDto;
	}
	
	//模拟数据函数
	private static WeeklyOptRepsDTO getReportList(String distId,String startDate, String endDate,String year,String month,String departmentId,Db1Service db1Service) {
		// 日期
		List<String> searchDates = new ArrayList<>();
		if (CommonUtil.isEmpty(startDate) || CommonUtil.isEmpty(endDate)) { // 按照当天日期获取数据
		} else { // 按照开始日期和结束日期获取数据
			DateTime startDt = BCDTimeUtil.convertDateStrToDate(startDate);
			DateTime endDt = BCDTimeUtil.convertDateStrToDate(endDate);
			int days = Days.daysBetween(startDt, endDt).getDays() + 1;
			for (int i = 0; i < days; i++) {
				searchDates.add(endDt.minusDays(i).toString("yyyy-MM-dd"));
			}
		}
		
		List<DepartmentObj> deparmentList =  db1Service.getDepartmentObjList(new DepartmentObj(),null, -1, -1);	
		Map<String,DepartmentObj> deparmentMap = deparmentList.stream().collect(Collectors.toMap(DepartmentObj::getDepartmentName,(b)->b));
		
		WeeklyOptRepsDTO worDto = new WeeklyOptRepsDTO();
		//时戳
		worDto.setTime(BCDTimeUtil.convertNormalFrom(null));
		//周运营报告列表模拟数据
		List<WeeklyOptReps> weeklyOptReps = new ArrayList<>();
		/**
		 * 1.获取动态生成报表
		 *   文件名称规则：2019年食安管理平台使用、验收及证照逾期处理情况_上海市_20190401_20190404_20190917100101
		 *   //2019/06/10~2019/06/14 2019年食安管理平台使用、验收及证照逾期处理情况（上海市)20190610-0614 2019/06/14 10:01:01
		 */
		String srcPathFileName = "/edu_week_report/shanghai";
		List<String> dataList=  HdfsRWClient.GetHdfsFileList(srcPathFileName);
		if(dataList != null && dataList.size() >0) {
			for(String data : dataList) {
				String newData = data;
				//去除多余的_
				if(data.indexOf("__") >= 0 ) {
					newData = data.replaceAll("__", "_");
				}
				
				String [] datas = newData.split("_");
				if(datas.length < 5) {
					continue;
				}
				//权限过滤
				if(distId != null) {
					if(deparmentMap.get(datas[1])==null) {
						continue;
					}
					if( !distId.equals(deparmentMap.get(datas[1]).getDepartmentId())) {
						continue;
					}
				}else {
					/*if(!"上海市".equals(datas[1])) {
						continue;
					}*/
				}
				
				//日期过滤
				DateTime startDt = BCDTimeUtil.convertDateStrToDate(datas[2],"yyyyMMdd");
				DateTime endDt = BCDTimeUtil.convertDateStrToDate(datas[3],"yyyyMMdd");
				DateTime creatDate = BCDTimeUtil.convertDateStrToDate(datas[4].substring(0,datas[4].indexOf(".")<0?datas[4].length():datas[4].indexOf(".")),"yyyyMMddHHmmss");
				if(searchDates !=null && searchDates.size() >0) {
					
					int days = Days.daysBetween(startDt, endDt).getDays() + 1;
					int index = -1;
					for (int j = 0; j < days; j++) {
						if(searchDates.indexOf(endDt.minusDays(j).toString("yyyy-MM-dd")) > -1) {
							index = searchDates.indexOf(endDt.minusDays(j).toString("yyyy-MM-dd"));
							break;
						}
					}
					if(index <= -1) {
						continue;
					}
				}
				//年过滤
				if(CommonUtil.isNotEmpty(year) && !year.equals(startDt.toString("yyyy"))) {
					continue;
				}
				//月过滤
				if(CommonUtil.isNotEmpty(month) && !month.equals(startDt.toString("M"))) {
					continue;
				}
				//报告分类
				if(CommonUtil.isNotEmpty(departmentId)) {
					if(!"上海市".equals(datas[1]) && deparmentMap.get(datas[1])==null) {
						continue;
					}
					if("-1".equals(departmentId)) {
						if(!"上海市".equals(datas[1])) {
							continue;
						}
					}else if( !departmentId.equals(deparmentMap.get(datas[1])==null?"":deparmentMap.get(datas[1]).getDepartmentId())) {
						continue;
					}
				}
				WeeklyOptReps wor = new WeeklyOptReps();
				wor.setRepId(data);
				wor.setRepPeriod(startDt.toString("yyyy/MM/dd") + "~" +  endDt.toString("yyyy/MM/dd"));
				
				
				//2020年食安管理平台使用、验收及证照逾期处理情况(上海市)20200316-0322
				//wor.setRepName(datas[0]+"("+datas[1]+")"+datas[2]+"-"+datas[3].substring(4,datas[3].length()));
				
				//2019年11月04日-10日周操作数据报告（上海市）
				wor.setRepName(startDt.toString("yyyy年MM月dd日") + "-" +  endDt.toString("dd日")+"周操作数据报告"+"("+datas[1]+")");
				wor.setRepUrl("");
				wor.setCreatTime(creatDate.toString("yyyy/MM/dd HH:mm:ss"));
				
				wor.setYear(startDt.toString("yyyy年"));
				wor.setMonth(startDt.toString("M月份"));
				wor.setDepartmentName(datas[1]);
				weeklyOptReps.add(wor);
			}
		}
		
		//排序
    	SortList<WeeklyOptReps> sortList = new SortList<WeeklyOptReps>();  
    	sortList.Sort(weeklyOptReps, "getRepPeriod", "desc");
		// 分页
		PageBean<WeeklyOptReps> pageBean = new PageBean<WeeklyOptReps>(weeklyOptReps, curPageNum, pageSize);
		PageInfo pageInfo = new PageInfo();
		pageInfo.setPageTotal(pageBean.getTotalCount());
		pageInfo.setCurPageNum(curPageNum);
		worDto.setPageInfo(pageInfo);
		// 设置数据
		worDto.setWeeklyOptReps(pageBean.getCurPageData());
		//消息ID
		worDto.setMsgId(AppModConfig.msgId);
		AppModConfig.msgId++;
		// 消息id小于0判断
		AppModConfig.msgIdLessThan0Judge();
		
		return worDto;
	}
	
	// 周运营报告列表模型函数
	public WeeklyOptRepsDTO appModFunc(String token, String startDate, String endDate, 
			String year,String month,String departmentId,
			String page, String pageSize, Db1Service db1Service, Db2Service db2Service) {
		WeeklyOptRepsDTO worDto = null;
		if(page != null)
			curPageNum = Integer.parseInt(page);
		if(pageSize != null)
			this.pageSize = Integer.parseInt(pageSize);
		if(isRealData) {       //真实数据
			String distId = AppModConfig.getUserDataPermDistId(token, db1Service, db2Service);  //获取用户权限区域ID
			worDto = getReportList(distId,startDate, endDate,year,month,departmentId,db1Service);
		}
		else {    //模拟数据
			//模拟数据函数
			worDto = SimuDataFunc(startDate,endDate);
		}		

		return worDto;
	}
}