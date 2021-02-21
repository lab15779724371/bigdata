package com.tfit.BdBiProcSrvShEduOmc.appmod.im;

import java.util.ArrayList;
import java.util.List;

import com.tfit.BdBiProcSrvShEduOmc.config.AppModConfig;
import com.tfit.BdBiProcSrvShEduOmc.dto.im.DispTypeCodesDTO;
import com.tfit.BdBiProcSrvShEduOmc.dto.NameCode;
import com.tfit.BdBiProcSrvShEduOmc.service.Db1Service;
import com.tfit.BdBiProcSrvShEduOmc.util.BCDTimeUtil;

//配送类型编码列表应用模型
public class DispTypeCodesAppMod {
	// 是否为真实数据标识
	private static boolean isRealData = true;

	// 数组数据初始化
	String[] name_Array = { "原料", "成品菜" };
	String[] code_Array = { "0", "1" };
	
	// 模拟数据函数
	private DispTypeCodesDTO SimuDataFunc() {
		DispTypeCodesDTO dtcDto = new DispTypeCodesDTO();
		// 时戳
		dtcDto.setTime(BCDTimeUtil.convertNormalFrom(null));
		// 配送类型编码列表模拟数据
		List<NameCode> dispTypeCodes = new ArrayList<>();
		// 赋值
		for (int i = 0; i < name_Array.length; i++) {
			NameCode dtc = new NameCode();
			dtc.setName(name_Array[i]);
			dtc.setCode(code_Array[i]);
			dispTypeCodes.add(dtc);
		}
		// 设置数据
		dtcDto.setDispTypeCodes(dispTypeCodes);
		// 消息ID
		dtcDto.setMsgId(AppModConfig.msgId);
		AppModConfig.msgId++;
		// 消息id小于0判断
		AppModConfig.msgIdLessThan0Judge();

		return dtcDto;
	}

	// 配送类型编码列表模型函数
	public DispTypeCodesDTO appModFunc(String distName, String prefCity, String province, Db1Service db1Service) {
		DispTypeCodesDTO dtcDto = null;
		// 省或直辖市
		if (province == null)
			province = "上海市";
		if (isRealData) { // 真实数据
			dtcDto = new DispTypeCodesDTO();
			// 时戳
			dtcDto.setTime(BCDTimeUtil.convertNormalFrom(null));
			// 配送类型编码列表模拟数据
			List<NameCode> dispTypeCodes = new ArrayList<>();
			// 赋值
			for (int i = 0; i < name_Array.length; i++) {
				NameCode dtc = new NameCode();
				dtc.setName(name_Array[i]);
				dtc.setCode(code_Array[i]);
				dispTypeCodes.add(dtc);
			}
			// 设置数据
			dtcDto.setDispTypeCodes(dispTypeCodes);
			// 消息ID
			dtcDto.setMsgId(AppModConfig.msgId);
			AppModConfig.msgId++;
			// 消息id小于0判断
			AppModConfig.msgIdLessThan0Judge();
		} else { // 模拟数据
			// 模拟数据函数
			dtcDto = SimuDataFunc();
		}

		return dtcDto;
	}
}
