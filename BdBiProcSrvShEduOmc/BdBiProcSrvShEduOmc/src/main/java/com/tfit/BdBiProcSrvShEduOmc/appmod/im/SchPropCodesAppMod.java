package com.tfit.BdBiProcSrvShEduOmc.appmod.im;

import java.util.ArrayList;
import java.util.List;

import com.tfit.BdBiProcSrvShEduOmc.config.AppModConfig;
import com.tfit.BdBiProcSrvShEduOmc.dto.im.SchPropCodesDTO;
import com.tfit.BdBiProcSrvShEduOmc.dto.NameCode;
import com.tfit.BdBiProcSrvShEduOmc.service.Db1Service;
import com.tfit.BdBiProcSrvShEduOmc.util.BCDTimeUtil;

//学校性质编码列表应用模型
public class SchPropCodesAppMod {
	//是否为真实数据标识
	private static boolean isRealData = true;
	
	//数组数据初始化
	String[] name_Array = {"公办", "民办", "其他"};
	String[] code_Array = {"0", "1", "2"};
		
	//模拟数据函数
	private SchPropCodesDTO SimuDataFunc() {
		SchPropCodesDTO spcDto = new SchPropCodesDTO();
		//时戳
		spcDto.setTime(BCDTimeUtil.convertNormalFrom(null));
		//学校性质编码列表模拟数据
		List<NameCode> schPropCodes = new ArrayList<>();
		//赋值
		for (int i = 0; i < name_Array.length; i++) {
			NameCode spc = new NameCode();
			spc.setName(name_Array[i]);
			spc.setCode(code_Array[i]);			
			schPropCodes.add(spc);
		}
		//设置数据
		spcDto.setSchPropCodes(schPropCodes);
		//消息ID
		spcDto.setMsgId(AppModConfig.msgId);
		AppModConfig.msgId++;
		// 消息id小于0判断
		AppModConfig.msgIdLessThan0Judge();
		
		return spcDto;
	}
	
	// 学校性质编码列表模型函数
	public SchPropCodesDTO appModFunc(String distName, String prefCity, String province, Db1Service db1Service) {
		SchPropCodesDTO spcDto = null;
		// 省或直辖市
		if(province == null)
			province = "上海市";
		if(isRealData) {       //真实数据
			spcDto = new SchPropCodesDTO();
			//时戳
			spcDto.setTime(BCDTimeUtil.convertNormalFrom(null));
			//学校性质编码列表模拟数据
			List<NameCode> schPropCodes = new ArrayList<>();
			//赋值
			for(Integer i : AppModConfig.schPropIdToNameMap.keySet()) {
				NameCode spc = new NameCode();
				spc.setName(AppModConfig.schPropIdToNameMap.get(i));
				spc.setCode(String.valueOf(i));
				schPropCodes.add(spc);
			}
			//设置数据
			spcDto.setSchPropCodes(schPropCodes);
			//消息ID
			spcDto.setMsgId(AppModConfig.msgId);
			AppModConfig.msgId++;
			// 消息id小于0判断
			AppModConfig.msgIdLessThan0Judge();
		}
		else {    //模拟数据
			//模拟数据函数
			spcDto = SimuDataFunc();
		}		

		return spcDto;
	}
}
