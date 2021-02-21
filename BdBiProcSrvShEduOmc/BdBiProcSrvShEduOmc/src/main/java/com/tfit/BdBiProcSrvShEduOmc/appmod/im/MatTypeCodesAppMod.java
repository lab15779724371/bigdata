package com.tfit.BdBiProcSrvShEduOmc.appmod.im;

import java.util.ArrayList;
import java.util.List;

import com.tfit.BdBiProcSrvShEduOmc.config.AppModConfig;
import com.tfit.BdBiProcSrvShEduOmc.dto.NameCode;
import com.tfit.BdBiProcSrvShEduOmc.dto.im.MatTypeCodesDTO;
import com.tfit.BdBiProcSrvShEduOmc.service.Db1Service;
import com.tfit.BdBiProcSrvShEduOmc.util.BCDTimeUtil;

//用料类别编码列表应用模型
public class MatTypeCodesAppMod {
	//是否为真实数据标识
	private static boolean isRealData = true;
	
	//数组数据初始化
	String[] name_Array = {"原料", "成品菜"};
	String[] code_Array = {"0", "1"};
		
	//模拟数据函数
	private MatTypeCodesDTO SimuDataFunc() {
		MatTypeCodesDTO mtcDto = new MatTypeCodesDTO();
		//时戳
		mtcDto.setTime(BCDTimeUtil.convertNormalFrom(null));
		//用料类别编码列表模拟数据
		List<NameCode> matTypeCodes = new ArrayList<>();
		//赋值
		for (int i = 0; i < name_Array.length; i++) {
			NameCode mtc = new NameCode();
			mtc.setName(name_Array[i]);
			mtc.setCode(code_Array[i]);			
			matTypeCodes.add(mtc);
		}
		//设置数据
		mtcDto.setMatTypeCodes(matTypeCodes);
		//消息ID
		mtcDto.setMsgId(AppModConfig.msgId);
		AppModConfig.msgId++;
		// 消息id小于0判断
		AppModConfig.msgIdLessThan0Judge();
		
		return mtcDto;
	}
	
	// 用料类别编码列表模型函数
	public MatTypeCodesDTO appModFunc(String distName, String prefCity, String province, Db1Service db1Service) {
		MatTypeCodesDTO mtcDto = null;
		// 省或直辖市
		if(province == null)
			province = "上海市";
		if(isRealData) {       //真实数据
			mtcDto = new MatTypeCodesDTO();
			//时戳
			mtcDto.setTime(BCDTimeUtil.convertNormalFrom(null));
			//用料类别编码列表模拟数据
			List<NameCode> matTypeCodes = new ArrayList<>();
			//赋值
			for (int i = 0; i < name_Array.length; i++) {
				NameCode mtc = new NameCode();
				mtc.setName(name_Array[i]);
				mtc.setCode(code_Array[i]);
				matTypeCodes.add(mtc);
			}
			//设置数据
			mtcDto.setMatTypeCodes(matTypeCodes);
			//消息ID
			mtcDto.setMsgId(AppModConfig.msgId);
			AppModConfig.msgId++;
			// 消息id小于0判断
			AppModConfig.msgIdLessThan0Judge();
		}
		else {    //模拟数据
			//模拟数据函数
			mtcDto = SimuDataFunc();
		}		

		return mtcDto;
	}
}
