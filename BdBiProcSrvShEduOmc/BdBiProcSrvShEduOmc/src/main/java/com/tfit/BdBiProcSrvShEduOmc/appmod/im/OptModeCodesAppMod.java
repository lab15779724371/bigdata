package com.tfit.BdBiProcSrvShEduOmc.appmod.im;

import java.util.ArrayList;
import java.util.List;

import com.tfit.BdBiProcSrvShEduOmc.config.AppModConfig;
import com.tfit.BdBiProcSrvShEduOmc.dto.im.OptModeCodesDTO;
import com.tfit.BdBiProcSrvShEduOmc.dto.NameCode;
import com.tfit.BdBiProcSrvShEduOmc.service.Db1Service;
import com.tfit.BdBiProcSrvShEduOmc.util.BCDTimeUtil;

//经营模式编码列表应用模型
public class OptModeCodesAppMod {
	// 数组数据初始化
	String[] name_Array = { "自营", "外包-外包", "外包-外送" };
	String[] code_Array = { "0", "1", "2" };

	// 经营模式编码列表模型函数
	public OptModeCodesDTO appModFunc(String distName, String prefCity, String province, Db1Service db1Service) {
		OptModeCodesDTO omcDto = null;
		// 省或直辖市
		if (province == null)
			province = "上海市";
		omcDto = new OptModeCodesDTO();
		// 时戳
		omcDto.setTime(BCDTimeUtil.convertNormalFrom(null));
		// 经营模式编码列表模拟数据
		List<NameCode> isMealCodes = new ArrayList<>();
		// 赋值
		for (Integer i : AppModConfig.optModeIdToNameMap.keySet()) {
			NameCode omc = new NameCode();
			omc.setName(AppModConfig.optModeIdToNameMap.get(i));
			omc.setCode(String.valueOf(i));
			isMealCodes.add(omc);
		}
		// 设置数据
		omcDto.setOptModeCodes(isMealCodes);
		// 消息ID
		omcDto.setMsgId(AppModConfig.msgId);
		AppModConfig.msgId++;
		// 消息id小于0判断
		AppModConfig.msgIdLessThan0Judge();

		return omcDto;
	}
}
