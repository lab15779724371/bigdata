package com.tfit.BdBiProcSrvShEduOmc.appmod.im;

import java.util.ArrayList;
import java.util.List;

import com.tfit.BdBiProcSrvShEduOmc.config.AppModConfig;
import com.tfit.BdBiProcSrvShEduOmc.dto.NameCode;
import com.tfit.BdBiProcSrvShEduOmc.dto.im.SendStatusCodesDTO;
import com.tfit.BdBiProcSrvShEduOmc.service.Db1Service;
import com.tfit.BdBiProcSrvShEduOmc.util.BCDTimeUtil;

//发送状态编码列表应用模型
public class SendStatusCodesAppMod {
	// 数组数据初始化
	String[] name_Array = { "未发送", "已发送" };
	String[] code_Array = { "0", "1" };

	// 发送状态编码列表模型函数
	public SendStatusCodesDTO appModFunc(String distName, String prefCity, String province, Db1Service db1Service) {
		SendStatusCodesDTO omcDto = null;
		// 省或直辖市
		if (province == null)
			province = "上海市";
		omcDto = new SendStatusCodesDTO();
		// 时戳
		omcDto.setTime(BCDTimeUtil.convertNormalFrom(null));
		// 发送状态编码列表模拟数据
		List<NameCode> isMealCodes = new ArrayList<>();
		// 赋值
		for (int i = 0; i < name_Array.length; i++) {
			NameCode omc = new NameCode();
			omc.setName(name_Array[i]);
			omc.setCode(code_Array[i]);
			isMealCodes.add(omc);
		}
		// 设置数据
		omcDto.setSendStatusCodes(isMealCodes);
		// 消息ID
		omcDto.setMsgId(AppModConfig.msgId);
		AppModConfig.msgId++;
		// 消息id小于0判断
		AppModConfig.msgIdLessThan0Judge();

		return omcDto;
	}
}
