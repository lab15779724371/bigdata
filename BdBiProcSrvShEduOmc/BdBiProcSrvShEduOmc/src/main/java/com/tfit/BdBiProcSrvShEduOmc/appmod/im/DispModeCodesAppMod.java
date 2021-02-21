package com.tfit.BdBiProcSrvShEduOmc.appmod.im;

import java.util.ArrayList;
import java.util.List;

import com.tfit.BdBiProcSrvShEduOmc.config.AppModConfig;
import com.tfit.BdBiProcSrvShEduOmc.dto.im.DispModeCodesDTO;
import com.tfit.BdBiProcSrvShEduOmc.dto.NameCode;
import com.tfit.BdBiProcSrvShEduOmc.service.Db1Service;
import com.tfit.BdBiProcSrvShEduOmc.util.BCDTimeUtil;

//配送方式编码列表应用模型
public class DispModeCodesAppMod {
	// 是否为真实数据标识
	private static boolean isRealData = true;

	// 数组数据初始化
	String[] name_Array = { "统配", "直配" };
	String[] code_Array = { "0", "1" };
	
	// 模拟数据函数
	private DispModeCodesDTO SimuDataFunc() {
		DispModeCodesDTO dmcDto = new DispModeCodesDTO();
		// 时戳
		dmcDto.setTime(BCDTimeUtil.convertNormalFrom(null));
		// 配送方式编码列表模拟数据
		List<NameCode> dispModeCodes = new ArrayList<>();
		// 赋值
		for (int i = 0; i < name_Array.length; i++) {
			NameCode dmc = new NameCode();
			dmc.setName(name_Array[i]);
			dmc.setCode(code_Array[i]);
			dispModeCodes.add(dmc);
		}
		// 设置数据
		dmcDto.setDispModeCodes(dispModeCodes);
		// 消息ID
		dmcDto.setMsgId(AppModConfig.msgId);
		AppModConfig.msgId++;
		// 消息id小于0判断
		AppModConfig.msgIdLessThan0Judge();

		return dmcDto;
	}

	// 配送方式编码列表模型函数
	public DispModeCodesDTO appModFunc(String distName, String prefCity, String province, Db1Service db1Service) {
		DispModeCodesDTO dmcDto = null;
		// 省或直辖市
		if (province == null)
			province = "上海市";
		if (isRealData) { // 真实数据
			dmcDto = new DispModeCodesDTO();
			// 时戳
			dmcDto.setTime(BCDTimeUtil.convertNormalFrom(null));
			// 配送方式编码列表模拟数据
			List<NameCode> dispModeCodes = new ArrayList<>();
			// 赋值
			for (int i = 0; i < name_Array.length; i++) {
				NameCode dmc = new NameCode();
				dmc.setName(name_Array[i]);
				dmc.setCode(code_Array[i]);
				dispModeCodes.add(dmc);
			}
			// 设置数据
			dmcDto.setDispModeCodes(dispModeCodes);
			// 消息ID
			dmcDto.setMsgId(AppModConfig.msgId);
			AppModConfig.msgId++;
			// 消息id小于0判断
			AppModConfig.msgIdLessThan0Judge();
		} else { // 模拟数据
			// 模拟数据函数
			dmcDto = SimuDataFunc();
		}

		return dmcDto;
	}
}
