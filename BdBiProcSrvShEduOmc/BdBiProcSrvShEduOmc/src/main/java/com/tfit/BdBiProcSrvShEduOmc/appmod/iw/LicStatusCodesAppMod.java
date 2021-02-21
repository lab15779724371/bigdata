package com.tfit.BdBiProcSrvShEduOmc.appmod.iw;

import java.util.ArrayList;
import java.util.List;

import com.tfit.BdBiProcSrvShEduOmc.config.AppModConfig;
import com.tfit.BdBiProcSrvShEduOmc.dto.NameCode;
import com.tfit.BdBiProcSrvShEduOmc.dto.iw.LicStatusCodesDTO;
import com.tfit.BdBiProcSrvShEduOmc.service.Db1Service;
import com.tfit.BdBiProcSrvShEduOmc.util.BCDTimeUtil;

//证件状况编码列表应用模型
public class LicStatusCodesAppMod {
	// 是否为真实数据标识
	private static boolean isRealData = true;

	// 数组数据初始化
	String[] name_Array = { "逾期", "到期" };
	String[] code_Array = { "0", "1" };
	
	// 模拟数据函数
	private LicStatusCodesDTO SimuDataFunc() {
		LicStatusCodesDTO lascDto = new LicStatusCodesDTO();
		// 时戳
		lascDto.setTime(BCDTimeUtil.convertNormalFrom(null));
		// 证件状况编码列表模拟数据
		List<NameCode> licStatusCodes = new ArrayList<>();
		// 赋值
		for (int i = 0; i < name_Array.length; i++) {
			NameCode lasc = new NameCode();
			lasc.setName(name_Array[i]);
			lasc.setCode(code_Array[i]);
			licStatusCodes.add(lasc);
		}
		// 设置数据
		lascDto.setLicStatusCodes(licStatusCodes);
		// 消息ID
		lascDto.setMsgId(AppModConfig.msgId);
		AppModConfig.msgId++;
		// 消息id小于0判断
		AppModConfig.msgIdLessThan0Judge();

		return lascDto;
	}

	// 证件状况编码列表模型函数
	public LicStatusCodesDTO appModFunc(String distName, String prefCity, String province, Db1Service db1Service) {
		LicStatusCodesDTO lascDto = null;
		// 省或直辖市
		if (province == null)
			province = "上海市";
		if (isRealData) { // 真实数据
			lascDto = new LicStatusCodesDTO();
			// 时戳
			lascDto.setTime(BCDTimeUtil.convertNormalFrom(null));
			// 证件状况编码列表模拟数据
			List<NameCode> licStatusCodes = new ArrayList<>();
			// 赋值
			for (int i = 0; i < name_Array.length; i++) {
				NameCode lasc = new NameCode();
				lasc.setName(name_Array[i]);
				lasc.setCode(code_Array[i]);
				licStatusCodes.add(lasc);
			}
			// 设置数据
			lascDto.setLicStatusCodes(licStatusCodes);
			// 消息ID
			lascDto.setMsgId(AppModConfig.msgId);
			AppModConfig.msgId++;
			// 消息id小于0判断
			AppModConfig.msgIdLessThan0Judge();
		} else { // 模拟数据
			// 模拟数据函数
			lascDto = SimuDataFunc();
		}

		return lascDto;
	}
}
