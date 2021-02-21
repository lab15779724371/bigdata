package com.tfit.BdBiProcSrvShEduOmc.appmod.cp;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.joda.time.DateTime;
import org.joda.time.Days;

import com.tfit.BdBiProcSrvShEduOmc.config.AppModConfig;
import com.tfit.BdBiProcSrvShEduOmc.dao.domain.TEduSchoolDo;
import com.tfit.BdBiProcSrvShEduOmc.dao.domain.edu.TEduDistrictDo;
import com.tfit.BdBiProcSrvShEduOmc.dao.domain.edubd.TEduBdComplaintDo;
import com.tfit.BdBiProcSrvShEduOmc.dto.PageBean;
import com.tfit.BdBiProcSrvShEduOmc.dto.PageInfo;
import com.tfit.BdBiProcSrvShEduOmc.dto.cp.CrList;
import com.tfit.BdBiProcSrvShEduOmc.dto.cp.CrListDTO;
import com.tfit.BdBiProcSrvShEduOmc.service.Db1Service;
import com.tfit.BdBiProcSrvShEduOmc.service.Db2Service;
import com.tfit.BdBiProcSrvShEduOmc.util.BCDTimeUtil;
import com.tfit.BdBiProcSrvShEduOmc.util.SortList;

//投诉举报列表应用模型
public class CrListAppMod {
	private static final Logger logger = LogManager.getLogger(CrListAppMod.class.getName());
	
	//是否为真实数据标识
	private static boolean isRealData = true;
	//页号、页大小和总页数
	int curPageNum = 1, pageTotal = 1, pageSize = 20;
	
	//数组数据初始化
	String[] subDate_Array = {"2018-08-15", "2018-08-14"};
	int[] crNum_Array = {20, 15};
	int[] noProcCrNum_Array = {15, 5};
    int[] assignCrNum_Array = {4, 8};
	int[] procCrNum_Array = {1, 2};
	float[] procCrRate_Array = {(float) 5.00, (float) 13.33};
	
	//模拟数据函数
	private CrListDTO SimuDataFunc() {
		CrListDTO crlDto = new CrListDTO();
		//时戳
		crlDto.setTime(BCDTimeUtil.convertNormalFrom(null));
		//投诉举报列表模拟数据
		List<CrList> crList = new ArrayList<>();
		//赋值
		for (int i = 0; i < subDate_Array.length; i++) {
			CrList crl = new CrList();
			crl.setSubDate(subDate_Array[i]);
			crl.setCrNum(crNum_Array[i]);
			crl.setNoProcCrNum(noProcCrNum_Array[i]);
			crl.setAssignCrNum(assignCrNum_Array[i]);
			crl.setProcCrNum(procCrNum_Array[i]);
			crl.setProcCrRate(procCrRate_Array[i]);
			crList.add(crl);
		}
		//设置数据
		crlDto.setCrList(crList);
		//分页
		PageInfo pageInfo = new PageInfo();
		pageTotal = subDate_Array.length;
		pageInfo.setPageTotal(pageTotal);
		pageInfo.setCurPageNum(curPageNum);
		crlDto.setPageInfo(pageInfo);
		//消息ID
		crlDto.setMsgId(AppModConfig.msgId);
		AppModConfig.msgId++;
		// 消息id小于0判断
		AppModConfig.msgIdLessThan0Judge();
		
		return crlDto;
	}
	
	// 投诉举报列表函数
	private CrListDTO crList(String distIdorSCName, String[] dates, Db1Service db1Service, Db2Service db2Service) {
		CrListDTO crlDto = new CrListDTO();
		//时戳
		crlDto.setTime(BCDTimeUtil.convertNormalFrom(null));
		//投诉举报列表
		List<CrList> crList = new ArrayList<>();
		//获取投诉举报以日期段，日期格式：xxxx-xx-xx
	    List<TEduBdComplaintDo> tebcpDoList = null;
	    if(dates != null) {     //查询指定日期段投诉举报
	    	tebcpDoList = db2Service.getTEduBdComplaintDosBySubDate(dates[dates.length-1], dates[0]);
	    }
	    else {     //查询所有投诉举报
	    	tebcpDoList = db2Service.getAllTEduBdComplaintDos();
	    }
		//赋值
	    Map<String, String> subDateToTotalMap = new HashMap<>(), subDateToNoProcMap = new HashMap<>(), subDateToAssignMap = new HashMap<>(), subDateToFinishMap = new HashMap<>();
		if(tebcpDoList != null) {
			for (int i = 0; i < tebcpDoList.size(); i++) {
				TEduBdComplaintDo tebcp = tebcpDoList.get(i);
				if(tebcp.getSubDate() != null) {
					//从数据源ds1的数据表t_edu_school中查找学校信息以学校id
  	  			    TEduSchoolDo tesDo = db1Service.getTEduSchoolDoBySchId(tebcp.getSchoolId(), 3);
					//区域筛选
					if(distIdorSCName != null && tesDo != null) {
						if(tesDo.getArea() == null) {
							continue;
						}
						if(!tesDo.getArea().equalsIgnoreCase(distIdorSCName))
							continue ;
					}
					//投诉举报数量
					if(subDateToTotalMap.containsKey(tebcp.getSubDate())) {
						int curVal = Integer.parseInt(subDateToTotalMap.get(tebcp.getSubDate()));
						curVal++;
						subDateToTotalMap.put(tebcp.getSubDate(), String.valueOf(curVal));
					}
					else {
						subDateToTotalMap.put(tebcp.getSubDate(), "1");
					}
					//待处理数量
					if(tebcp.getCpStatus() == 0) {
						if(subDateToNoProcMap.containsKey(tebcp.getSubDate())) {
							int curVal = Integer.parseInt(subDateToNoProcMap.get(tebcp.getSubDate()));
							curVal++;
							subDateToNoProcMap.put(tebcp.getSubDate(), String.valueOf(curVal));
						}
						else {
							subDateToNoProcMap.put(tebcp.getSubDate(), "1");
						}
					}
					else if(tebcp.getCpStatus() == 1) {  //已指派数量
						if(subDateToAssignMap.containsKey(tebcp.getSubDate())) {
							int curVal = Integer.parseInt(subDateToAssignMap.get(tebcp.getSubDate()));
							curVal++;
							subDateToAssignMap.put(tebcp.getSubDate(), String.valueOf(curVal));
						}
						else {
							subDateToAssignMap.put(tebcp.getSubDate(), "1");
						}
					}
					else if(tebcp.getCpStatus() == 2) {  //已办结数量
						if(subDateToFinishMap.containsKey(tebcp.getSubDate())) {
							int curVal = Integer.parseInt(subDateToFinishMap.get(tebcp.getSubDate()));
							curVal++;
							subDateToFinishMap.put(tebcp.getSubDate(), String.valueOf(curVal));
						}
						else {
							subDateToFinishMap.put(tebcp.getSubDate(), "1");
						}
					}
				}
			}
		}
		for(String key : subDateToTotalMap.keySet()) {
			CrList crl = new CrList();
			//提交日期
			crl.setSubDate(key);
			//总数量
			int crNum = Integer.parseInt(subDateToTotalMap.get(key));
			crl.setCrNum(crNum);
			//待处理数量
			if(subDateToNoProcMap.containsKey(key)) {
				crl.setNoProcCrNum(Integer.parseInt(subDateToNoProcMap.get(key)));
			}
			else {
				crl.setNoProcCrNum(0);
			}
			//已指派数量
			if(subDateToAssignMap.containsKey(key)) {
				crl.setAssignCrNum(Integer.parseInt(subDateToAssignMap.get(key)));
			}
			else {
				crl.setAssignCrNum(0);
			}
			//已办结数量
			int procCrNum = 0;
			if(subDateToFinishMap.containsKey(key)) {
				procCrNum = Integer.parseInt(subDateToFinishMap.get(key));
			}
			crl.setProcCrNum(procCrNum);
			//办结率
			float procCrRate = 100 * ((float) procCrNum / (float) crNum);
			BigDecimal bd = new BigDecimal(procCrRate);
			procCrRate = bd.setScale(2, BigDecimal.ROUND_HALF_UP).floatValue();
			crl.setProcCrRate(procCrRate);
			crList.add(crl);
		}
		//排序
    	SortList<CrList> sortList = new SortList<CrList>();  
    	sortList.Sort(crList, "getSubDate", "desc");
		// 分页
    	PageBean<CrList> pageBean = new PageBean<CrList>(crList, curPageNum, pageSize);
    	PageInfo pageInfo = new PageInfo();
    	pageInfo.setPageTotal(pageBean.getTotalCount());
    	pageInfo.setCurPageNum(curPageNum);
    	crlDto.setPageInfo(pageInfo);
    	// 设置数据
    	crlDto.setCrList(pageBean.getCurPageData());
		//消息ID
		crlDto.setMsgId(AppModConfig.msgId);
		AppModConfig.msgId++;
		// 消息id小于0判断
		AppModConfig.msgIdLessThan0Judge();

		return crlDto;
	}
	
	// 投诉举报列表模型函数
	public CrListDTO appModFunc(String token, String startSubDate, String endSubDate, String distName, String prefCity, String province, String page, String pageSize, Db1Service db1Service, Db2Service db2Service) {
		CrListDTO crlDto = null;
		this.curPageNum = Integer.parseInt(page);
		this.pageSize = Integer.parseInt(pageSize);
		if(isRealData) {       //真实数据
			// 日期
			String[] dates = null;
			if (startSubDate != null && endSubDate != null) { // 按照当天日期获取数据
				DateTime startDt = BCDTimeUtil.convertDateStrToDate(startSubDate);
				DateTime endDt = BCDTimeUtil.convertDateStrToDate(endSubDate);
				int days = Days.daysBetween(startDt, endDt).getDays() + 1;
				dates = new String[days];
				for (int i = 0; i < days; i++) {
					dates[i] = endDt.minusDays(i).toString("yyyy-MM-dd");
				}
			}
			if(dates != null) {
				for (int i = 0; i < dates.length; i++) {
					logger.info("dates[" + i + "] = " + dates[i]);
				}
			}
			// 省或直辖市
			if(province == null)
				province = "上海市";
			// 参数查找标识
			boolean bfind = false;
			String distIdorSCName = null;
			// 按不同参数形式处理
			if (distName != null && prefCity == null && province != null) { // 按区域，省或直辖市处理
				List<TEduDistrictDo> tedList = db1Service.getListByDs1IdName();
				if(tedList != null) {
					// 查找是否存在该区域和省市
					for (int i = 0; i < tedList.size(); i++) {
						TEduDistrictDo curTdd = tedList.get(i);
						if (curTdd.getId().compareTo(distName) == 0) {
							bfind = true;
							distIdorSCName = curTdd.getId();
							break;
						}
					}
				}
				// 存在则获取数据
				if (bfind) {
					if(distIdorSCName == null)
						distIdorSCName = AppModConfig.getUserDataPermDistId(token, db1Service, db2Service);  //获取用户权限区域ID
					// 投诉举报列表函数
					crlDto = crList(distIdorSCName, dates, db1Service, db2Service);
				}
			} else if (distName == null && prefCity == null && province != null) { // 按省或直辖市处理
				List<TEduDistrictDo> tedList = null;
				if (province.compareTo("上海市") == 0) {
					tedList = db1Service.getListByDs1IdName();
					if(tedList != null)
						bfind = true;
					distIdorSCName = null;
				}
				if (bfind) {
					if(distIdorSCName == null)
						distIdorSCName = AppModConfig.getUserDataPermDistId(token, db1Service, db2Service);  //获取用户权限区域ID
					// 投诉举报列表函数
					crlDto = crList(distIdorSCName, dates, db1Service, db2Service);
				}
			} else if (distName != null && prefCity != null && province != null) { // 按区域，地级市，省或直辖市处理

			} else if (distName == null && prefCity != null && province != null) { // 地级市，省或直辖市处理

			} else {
				logger.info("访问接口参数非法！");
			}
		}
		else {    //模拟数据
			//模拟数据函数
			crlDto = SimuDataFunc();
		}		

		return crlDto;
	}
}
