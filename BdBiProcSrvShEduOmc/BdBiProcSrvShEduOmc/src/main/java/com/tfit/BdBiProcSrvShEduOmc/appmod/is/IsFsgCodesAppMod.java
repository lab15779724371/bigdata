package com.tfit.BdBiProcSrvShEduOmc.appmod.is;

import java.util.ArrayList;
import java.util.List;

import com.tfit.BdBiProcSrvShEduOmc.config.AppModConfig;
import com.tfit.BdBiProcSrvShEduOmc.dto.NameCode;
import com.tfit.BdBiProcSrvShEduOmc.dto.is.IsFsgCodesDTO;
import com.tfit.BdBiProcSrvShEduOmc.service.Db1Service;
import com.tfit.BdBiProcSrvShEduOmc.util.BCDTimeUtil;

//食品安全等级编码列表应用模型
public class IsFsgCodesAppMod {
	//是否为真实数据标识
	private static boolean isRealData = true;
	
	//数组数据初始化
	String[] name_Array = {"良好", "一般", "较差", "待评价"};
	String[] code_Array = {"0", "1", "2", "11"};
		
	//模拟数据函数
	private IsFsgCodesDTO SimuDataFunc() {
		IsFsgCodesDTO ifcDto = new IsFsgCodesDTO();
		//时戳
		ifcDto.setTime(BCDTimeUtil.convertNormalFrom(null));
		//食品安全等级编码列表模拟数据
		List<NameCode> isFsgCodes = new ArrayList<>();
		//赋值
		for (int i = 0; i < name_Array.length; i++) {
			NameCode ifc = new NameCode();
			ifc.setName(name_Array[i]);
			ifc.setCode(code_Array[i]);			
			isFsgCodes.add(ifc);
		}
		//设置数据
		ifcDto.setIsFsgCodes(isFsgCodes);
		//消息ID
		ifcDto.setMsgId(AppModConfig.msgId);
		AppModConfig.msgId++;
		// 消息id小于0判断
		AppModConfig.msgIdLessThan0Judge();
		
		return ifcDto;
	}
	
	// 食品安全等级编码列表模型函数
	public IsFsgCodesDTO appModFunc(String distName, String prefCity, String province, Db1Service db1Service) {
		IsFsgCodesDTO ifcDto = null;
		// 省或直辖市
		if(province == null)
			province = "上海市";
		if(isRealData) {       //真实数据
			ifcDto = new IsFsgCodesDTO();
			//时戳
			ifcDto.setTime(BCDTimeUtil.convertNormalFrom(null));
			//食品安全等级编码列表模拟数据
			List<NameCode> isFsgCodes = new ArrayList<>();
			//赋值
			for (int i = 0; i < name_Array.length; i++) {
				NameCode ifc = new NameCode();
				ifc.setName(name_Array[i]);
				ifc.setCode(code_Array[i]);
				isFsgCodes.add(ifc);
			}
			//设置数据
			ifcDto.setIsFsgCodes(isFsgCodes);
			//消息ID
			ifcDto.setMsgId(AppModConfig.msgId);
			AppModConfig.msgId++;
			// 消息id小于0判断
			AppModConfig.msgIdLessThan0Judge();
		}
		else {    //模拟数据
			//模拟数据函数
			ifcDto = SimuDataFunc();
		}		

		return ifcDto;
	}
}
