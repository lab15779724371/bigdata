package com.tfit.BdBiProcSrvShEduOmc.appmod.bd;

import java.util.ArrayList;
import java.util.List;

import com.tfit.BdBiProcSrvShEduOmc.config.AppModConfig;
import com.tfit.BdBiProcSrvShEduOmc.dto.bd.SubLevelCodesDTO;
import com.tfit.BdBiProcSrvShEduOmc.dto.NameCode;
import com.tfit.BdBiProcSrvShEduOmc.service.Db1Service;
import com.tfit.BdBiProcSrvShEduOmc.util.BCDTimeUtil;
import com.tfit.BdBiProcSrvShEduOmc.util.SortList;

//所属属别编码列表应用模型
public class SubLevelCodesAppMod {
	//是否为真实数据标识
	private static boolean isRealData = true;
	
	//数组数据初始化
	String[] name_Array = {"其他", "部属", "市属", "区属"};
	String[] code_Array = {"0", "1", "2", "3"};
		
	//模拟数据函数
	private SubLevelCodesDTO SimuDataFunc() {
		SubLevelCodesDTO slcDto = new SubLevelCodesDTO();
		//时戳
		slcDto.setTime(BCDTimeUtil.convertNormalFrom(null));
		//所属属别编码列表模拟数据
		List<NameCode> subLevelCodes = new ArrayList<>();
		//赋值
		for (int i = 0; i < name_Array.length; i++) {
			NameCode slc = new NameCode();
			slc.setName(name_Array[i]);
			slc.setCode(code_Array[i]);			
			subLevelCodes.add(slc);
		}
		//设置数据
		slcDto.setSubLevelCodes(subLevelCodes);
		//消息ID
		slcDto.setMsgId(AppModConfig.msgId);
		AppModConfig.msgId++;
		// 消息id小于0判断
		AppModConfig.msgIdLessThan0Judge();
		
		return slcDto;
	}
	
	// 所属属别编码列表模型函数
	public SubLevelCodesDTO appModFunc(String distName, String prefCity, String province, Db1Service db1Service) {
		SubLevelCodesDTO slcDto = null;
		// 省或直辖市
		if(province == null)
			province = "上海市";
		if(isRealData) {       //真实数据
			slcDto = new SubLevelCodesDTO();
			//时戳
			slcDto.setTime(BCDTimeUtil.convertNormalFrom(null));
			//所属属别编码列表模拟数据
			List<NameCode> subLevelCodes = new ArrayList<>();
			//赋值
			for (Integer I : AppModConfig.subLevelIdToNameMap.keySet()) {
				NameCode slc = new NameCode();
				slc.setName(AppModConfig.subLevelIdToNameMap.get(I));
				slc.setCode(String.valueOf(I));
				subLevelCodes.add(slc);
			}
	    	//排序
	    	SortList<NameCode> sortList = new SortList<NameCode>();  
	    	sortList.Sort(subLevelCodes, "getCode", "desc");
			//设置数据
			slcDto.setSubLevelCodes(subLevelCodes);
			//消息ID
			slcDto.setMsgId(AppModConfig.msgId);
			AppModConfig.msgId++;
			// 消息id小于0判断
			AppModConfig.msgIdLessThan0Judge();
		}
		else {    //模拟数据
			//模拟数据函数
			slcDto = SimuDataFunc();
		}		

		return slcDto;
	}
}
