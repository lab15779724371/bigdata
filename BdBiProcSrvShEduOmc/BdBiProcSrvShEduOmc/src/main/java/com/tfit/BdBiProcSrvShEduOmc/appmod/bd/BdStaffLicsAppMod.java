package com.tfit.BdBiProcSrvShEduOmc.appmod.bd;

import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.tfit.BdBiProcSrvShEduOmc.config.AppModConfig;
import com.tfit.BdBiProcSrvShEduOmc.dto.PageInfo;
import com.tfit.BdBiProcSrvShEduOmc.dto.bd.BdStaffLics;
import com.tfit.BdBiProcSrvShEduOmc.dto.bd.BdStaffLicsDTO;
import com.tfit.BdBiProcSrvShEduOmc.service.Db1Service;
import com.tfit.BdBiProcSrvShEduOmc.util.BCDTimeUtil;

//基础数据员工证照列表应用模型
public class BdStaffLicsAppMod {
	private static final Logger logger = LogManager.getLogger(BdStaffLicsAppMod.class.getName());
	
	//是否为真实数据标识
	private static boolean isRealData = false;
	//页号、页大小和总页数
	int curPageNum = 1, pageTotal = 1, pageSize = 20;
	
	//数组数据初始化	
	String[] rmcName_Array = {"上海绿捷", "上海绿捷"};
	String[] staffName_Array = {"张三", "李四"};
	String[] ppName_Array = {"上海市天山中学", "上海市徐汇区康宁外国语中学"};
	int[] licType_Array = {2, 2};
	String[] licNo_Array = {"JY33701050000548", "JY33701050000548"};
	String[] licStartDate_Array = {"2015/12/29", "2015/12/29"};
	String[] licExpireDate_Array = {"2020/12/28", "2020/12/28"};
	int[] licStatus_Array = {1, 1};
	
	//模拟数据函数
	private BdStaffLicsDTO SimuDataFunc() {
		BdStaffLicsDTO bstlDto = new BdStaffLicsDTO();
		//时戳
		bstlDto.setTime(BCDTimeUtil.convertNormalFrom(null));
		//基础数据员工证照列表模拟数据
		List<BdStaffLics> bdDishList = new ArrayList<>();
		//赋值
		for (int i = 0; i < rmcName_Array.length; i++) {
			BdStaffLics bstl = new BdStaffLics();
			bstl.setRmcName(rmcName_Array[i]);
			bstl.setStaffName(staffName_Array[i]);
			bstl.setPpName(ppName_Array[i]);
			bstl.setLicType(licType_Array[i]);
			bstl.setLicNo(licNo_Array[i]);
			bstl.setLicStartDate(licStartDate_Array[i]);
			bstl.setLicExpireDate(licExpireDate_Array[i]);
			bstl.setLicStatus(licStatus_Array[i]);
			bdDishList.add(bstl);
		}
		//设置数据
		bstlDto.setBdStaffLics(bdDishList);
		//分页
		PageInfo pageInfo = new PageInfo();
		pageTotal = rmcName_Array.length;
		pageInfo.setPageTotal(pageTotal);
		pageInfo.setCurPageNum(curPageNum);
		bstlDto.setPageInfo(pageInfo);
		//消息ID
		bstlDto.setMsgId(AppModConfig.msgId);
		AppModConfig.msgId++;
		// 消息id小于0判断
		AppModConfig.msgIdLessThan0Judge();
		
		return bstlDto;
	}
	
	// 基础数据员工证照列表模型函数
	public BdStaffLicsDTO appModFunc(String rmcName, String staffName, String ppName, String licType, String licNo, String licStartDate, String licExpireDate, String licStatus, String distName, String prefCity, String province, String page, String pageSize, Db1Service db1Service) {
		BdStaffLicsDTO bstlDto = null;
		if(isRealData) {       //真实数据
			
		}
		else {    //模拟数据
			//模拟数据函数
			bstlDto = SimuDataFunc();
		}		

		return bstlDto;
	}
}
