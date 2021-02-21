package com.tfit.BdBiProcSrvShEduOmc.appmod.bd;

import java.util.ArrayList;
import java.util.List;

import com.tfit.BdBiProcSrvShEduOmc.config.AppModConfig;
import com.tfit.BdBiProcSrvShEduOmc.dto.bd.SemSetCodesDTO;
import com.tfit.BdBiProcSrvShEduOmc.dto.NameCode;
import com.tfit.BdBiProcSrvShEduOmc.service.Db1Service;
import com.tfit.BdBiProcSrvShEduOmc.util.BCDTimeUtil;

//学期设置编码列表应用模型
public class SemSetCodesAppMod {
	//是否为真实数据标识
	private static boolean isRealData = true;
	
	//数组数据初始化
	String[] name_Array = {"未设置", "已设置"};
	String[] code_Array = {"0", "1"};
		
	//模拟数据函数
	private SemSetCodesDTO SimuDataFunc() {
		SemSetCodesDTO sscDto = new SemSetCodesDTO();
		//时戳
		sscDto.setTime(BCDTimeUtil.convertNormalFrom(null));
		//学期设置编码列表模拟数据
		List<NameCode> semSetCodes = new ArrayList<>();
		//赋值
		for (int i = 0; i < name_Array.length; i++) {
			NameCode ssc = new NameCode();
			ssc.setName(name_Array[i]);
			ssc.setCode(code_Array[i]);			
			semSetCodes.add(ssc);
		}
		//设置数据
		sscDto.setSemSetCodes(semSetCodes);
		//消息ID
		sscDto.setMsgId(AppModConfig.msgId);
		AppModConfig.msgId++;
		// 消息id小于0判断
		AppModConfig.msgIdLessThan0Judge();
		
		return sscDto;
	}
	
	// 学期设置编码列表模型函数
	public SemSetCodesDTO appModFunc(String distName, String prefCity, String province, Db1Service db1Service) {
		SemSetCodesDTO sscDto = null;
		// 省或直辖市
		if(province == null)
			province = "上海市";
		if(isRealData) {       //真实数据
			sscDto = new SemSetCodesDTO();
			//时戳
			sscDto.setTime(BCDTimeUtil.convertNormalFrom(null));
			//学期设置编码列表模拟数据
			List<NameCode> semSetCodes = new ArrayList<>();
			//赋值
			for (int i = 0; i < name_Array.length; i++) {
				NameCode ssc = new NameCode();
				ssc.setName(name_Array[i]);
				ssc.setCode(code_Array[i]);
				semSetCodes.add(ssc);
			}
			//设置数据
			sscDto.setSemSetCodes(semSetCodes);
			//消息ID
			sscDto.setMsgId(AppModConfig.msgId);
			AppModConfig.msgId++;
			// 消息id小于0判断
			AppModConfig.msgIdLessThan0Judge();
		}
		else {    //模拟数据
			//模拟数据函数
			sscDto = SimuDataFunc();
		}		

		return sscDto;
	}
}
