package com.tfit.BdBiProcSrvShEduOmc.appmod.iw;

import java.util.ArrayList;
import java.util.List;

import com.tfit.BdBiProcSrvShEduOmc.config.AppModConfig;
import com.tfit.BdBiProcSrvShEduOmc.dto.iw.LicTypeCodesDTO;
import com.tfit.BdBiProcSrvShEduOmc.dto.NameCode;
import com.tfit.BdBiProcSrvShEduOmc.service.Db1Service;
import com.tfit.BdBiProcSrvShEduOmc.util.BCDTimeUtil;

//证件类型编码列表应用模型
public class LicTypeCodesAppMod {
	// 是否为真实数据标识
	private static boolean isRealData = true;

	// 数组数据初始化
	String[] name_Array = { "食品经营许可证", "营业执照", "健康证", "餐饮服务许可证", "A1证", "A2证", "B证", "C证" };
	String[] code_Array = { "0", "1", "2", "3", "4", "5", "6", "7" };
	
	// 模拟数据函数
	private LicTypeCodesDTO SimuDataFunc() {
		LicTypeCodesDTO ltcDto = new LicTypeCodesDTO();
		// 时戳
		ltcDto.setTime(BCDTimeUtil.convertNormalFrom(null));
		// 证件类型编码列表模拟数据
		List<NameCode> dispTypeCodes = new ArrayList<>();
		// 赋值
		for (int i = 0; i < name_Array.length; i++) {
			NameCode ltc = new NameCode();
			ltc.setName(name_Array[i]);
			ltc.setCode(code_Array[i]);
			dispTypeCodes.add(ltc);
		}
		// 设置数据
		ltcDto.setLicTypeCodes(dispTypeCodes);
		// 消息ID
		ltcDto.setMsgId(AppModConfig.msgId);
		AppModConfig.msgId++;
		// 消息id小于0判断
		AppModConfig.msgIdLessThan0Judge();

		return ltcDto;
	}

	// 证件类型编码列表模型函数
	public LicTypeCodesDTO appModFunc(String distName, String prefCity, String province, Db1Service db1Service) {
		LicTypeCodesDTO ltcDto = null;
		// 省或直辖市
		if (province == null)
			province = "上海市";
		if (isRealData) { // 真实数据
			ltcDto = new LicTypeCodesDTO();
			// 时戳
			ltcDto.setTime(BCDTimeUtil.convertNormalFrom(null));
			// 证件类型编码列表模拟数据
			List<NameCode> dispTypeCodes = new ArrayList<>();
			// 赋值
			for (int i = 0; i < name_Array.length; i++) {
				NameCode ltc = new NameCode();
				ltc.setName(name_Array[i]);
				ltc.setCode(code_Array[i]);
				dispTypeCodes.add(ltc);
			}
			// 设置数据
			ltcDto.setLicTypeCodes(dispTypeCodes);
			// 消息ID
			ltcDto.setMsgId(AppModConfig.msgId);
			AppModConfig.msgId++;
			// 消息id小于0判断
			AppModConfig.msgIdLessThan0Judge();
		} else { // 模拟数据
			// 模拟数据函数
			ltcDto = SimuDataFunc();
		}

		return ltcDto;
	}
}
