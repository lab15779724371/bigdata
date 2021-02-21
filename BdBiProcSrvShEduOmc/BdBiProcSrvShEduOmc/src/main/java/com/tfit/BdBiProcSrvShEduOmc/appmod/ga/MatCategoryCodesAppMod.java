package com.tfit.BdBiProcSrvShEduOmc.appmod.ga;

import java.util.ArrayList;
import java.util.List;

import com.tfit.BdBiProcSrvShEduOmc.config.AppModConfig;
import com.tfit.BdBiProcSrvShEduOmc.dto.ga.MatCategoryCodesDTO;
import com.tfit.BdBiProcSrvShEduOmc.dto.NameCode;
import com.tfit.BdBiProcSrvShEduOmc.service.Db1Service;
import com.tfit.BdBiProcSrvShEduOmc.util.BCDTimeUtil;

//原料类别编码列表应用模型
public class MatCategoryCodesAppMod {
	// 数组数据初始化
	String[] name_Array = { "主料", "辅料" };
	String[] code_Array = { "0", "1" };

	// 原料类别编码列表模型函数
	public MatCategoryCodesDTO appModFunc(String distName, String prefCity, String province, Db1Service db1Service) {
		MatCategoryCodesDTO imcDto = null;
		// 省或直辖市
		if (province == null)
			province = "上海市";
		imcDto = new MatCategoryCodesDTO();
		// 时戳
		imcDto.setTime(BCDTimeUtil.convertNormalFrom(null));
		// 原料类别编码列表模拟数据
		List<NameCode> isMealCodes = new ArrayList<>();
		// 赋值
		for (int i = 0; i < name_Array.length; i++) {
			NameCode imc = new NameCode();
			imc.setName(name_Array[i]);
			imc.setCode(code_Array[i]);
			isMealCodes.add(imc);
		}
		// 设置数据
		imcDto.setMatCategoryCodes(isMealCodes);
		// 消息ID
		imcDto.setMsgId(AppModConfig.msgId);
		AppModConfig.msgId++;
		// 消息id小于0判断
		AppModConfig.msgIdLessThan0Judge();

		return imcDto;
	}
}
