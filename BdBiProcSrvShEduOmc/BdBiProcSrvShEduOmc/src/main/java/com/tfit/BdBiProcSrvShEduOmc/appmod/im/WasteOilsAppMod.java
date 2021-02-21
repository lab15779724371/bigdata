package com.tfit.BdBiProcSrvShEduOmc.appmod.im;

import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.tfit.BdBiProcSrvShEduOmc.config.AppModConfig;
import com.tfit.BdBiProcSrvShEduOmc.dto.PageInfo;
import com.tfit.BdBiProcSrvShEduOmc.dto.im.WasteOils;
import com.tfit.BdBiProcSrvShEduOmc.dto.im.WasteOilsDTO;
import com.tfit.BdBiProcSrvShEduOmc.service.Db1Service;
import com.tfit.BdBiProcSrvShEduOmc.util.BCDTimeUtil;

//废弃油脂列表应用模型
public class WasteOilsAppMod {
	private static final Logger logger = LogManager.getLogger(WasteOilsAppMod.class.getName());
	
	//是否为真实数据标识
	private static boolean isRealData = false;
	//页号、页大小和总页数
	int curPageNum = 1, pageTotal = 1, pageSize = 20;
	
	//数组数据初始化		
	String[] recDate_Array = {"2018/09/03-2018/09/04", "2018/09/03-2018/09/04", "2018/09/03-2018/09/04"};
	String[] distName_Array = {"11", "1", "10"};
	int[] rmcNum_Array = {268, 151, 243};
	String[] rcNum_Array = {"206 公斤", "130 公斤", "215 公斤"};
	
	//模拟数据函数
	private WasteOilsDTO SimuDataFunc() {
		WasteOilsDTO wasDto = new WasteOilsDTO();
		//时戳
		wasDto.setTime(BCDTimeUtil.convertNormalFrom(null));
		//废弃油脂列表模拟数据
		List<WasteOils> wasteOils = new ArrayList<>();
		//赋值
		for (int i = 0; i < recDate_Array.length; i++) {
			WasteOils was = new WasteOils();
			was.setRecDate(recDate_Array[i]);
			was.setDistName(distName_Array[i]);
			was.setRmcNum(rmcNum_Array[i]);
			was.setRcNum(rcNum_Array[i]);
			wasteOils.add(was);
		}
		//设置数据
		wasDto.setWasteOils(wasteOils);
		//分页
		PageInfo pageInfo = new PageInfo();
		pageTotal = recDate_Array.length;
		pageInfo.setPageTotal(pageTotal);
		pageInfo.setCurPageNum(curPageNum);
		wasDto.setPageInfo(pageInfo);
		//消息ID
		wasDto.setMsgId(AppModConfig.msgId);
		AppModConfig.msgId++;
		// 消息id小于0判断
		AppModConfig.msgIdLessThan0Judge();
		
		return wasDto;
	}
	
	// 废弃油脂列表模型函数
	public WasteOilsDTO appModFunc(String recStartDate, String recEndDate, String distName, String prefCity, String province, String page, String pageSize, Db1Service db1Service) {
		WasteOilsDTO wasDto = null;
		if(isRealData) {       //真实数据
			
		}
		else {    //模拟数据
			//模拟数据函数
			wasDto = SimuDataFunc();
		}		

		return wasDto;
	}
}
