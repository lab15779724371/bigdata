package com.tfit.BdBiProcSrvShEduOmc.appmod.im;

import java.util.ArrayList;
import java.util.List;

import com.tfit.BdBiProcSrvShEduOmc.config.AppModConfig;
import com.tfit.BdBiProcSrvShEduOmc.dto.im.DispStatusCodesDTO;
import com.tfit.BdBiProcSrvShEduOmc.dto.NameCode;
import com.tfit.BdBiProcSrvShEduOmc.service.Db1Service;
import com.tfit.BdBiProcSrvShEduOmc.util.BCDTimeUtil;

//配送状态编码列表应用模型
public class DispStatusCodesAppMod {
	// 是否为真实数据标识
	private static boolean isRealData = true;

	// 数组数据初始化
	String[] name_Array = { "未派送", "配送中", "已配送" };
	String[] code_Array = { "0", "1", "2" };
	
	// 模拟数据函数
	private DispStatusCodesDTO SimuDataFunc() {
		DispStatusCodesDTO dscDto = new DispStatusCodesDTO();
		// 时戳
		dscDto.setTime(BCDTimeUtil.convertNormalFrom(null));
		// 配送状态编码列表模拟数据
		List<NameCode> dispStatusCodes = new ArrayList<>();
		// 赋值
		for (int i = 0; i < name_Array.length; i++) {
			NameCode dsc = new NameCode();
			dsc.setName(name_Array[i]);
			dsc.setCode(code_Array[i]);
			dispStatusCodes.add(dsc);
		}
		// 设置数据
		dscDto.setDispStatusCodes(dispStatusCodes);
		// 消息ID
		dscDto.setMsgId(AppModConfig.msgId);
		AppModConfig.msgId++;
		// 消息id小于0判断
		AppModConfig.msgIdLessThan0Judge();

		return dscDto;
	}

	// 配送状态编码列表模型函数
	public DispStatusCodesDTO appModFunc(String distName, String prefCity, String province, Db1Service db1Service) {
		DispStatusCodesDTO dscDto = null;
		// 省或直辖市
		if (province == null)
			province = "上海市";
		if (isRealData) { // 真实数据
			dscDto = new DispStatusCodesDTO();
			// 时戳
			dscDto.setTime(BCDTimeUtil.convertNormalFrom(null));
			// 配送状态编码列表模拟数据
			List<NameCode> dispStatusCodes = new ArrayList<>();
			// 赋值
			for (int i = 0; i < name_Array.length; i++) {
				NameCode dsc = new NameCode();
				dsc.setName(name_Array[i]);
				dsc.setCode(code_Array[i]);
				dispStatusCodes.add(dsc);
			}
			// 设置数据
			dscDto.setDispStatusCodes(dispStatusCodes);
			// 消息ID
			dscDto.setMsgId(AppModConfig.msgId);
			AppModConfig.msgId++;
			// 消息id小于0判断
			AppModConfig.msgIdLessThan0Judge();
		} else { // 模拟数据
			// 模拟数据函数
			dscDto = SimuDataFunc();
		}

		return dscDto;
	}
}
