package com.tfit.BdBiProcSrvShEduOmc.appmod.im;

import java.util.ArrayList;
import java.util.List;

import com.tfit.BdBiProcSrvShEduOmc.config.AppModConfig;
import com.tfit.BdBiProcSrvShEduOmc.dto.im.AcceptStatusCodesDTO;
import com.tfit.BdBiProcSrvShEduOmc.dto.NameCode;
import com.tfit.BdBiProcSrvShEduOmc.service.Db1Service;
import com.tfit.BdBiProcSrvShEduOmc.util.BCDTimeUtil;

//验收状态编码列表应用模型
public class AcceptStatusCodesAppMod {
	// 是否为真实数据标识
	private static boolean isRealData = true;

	// 数组数据初始化
	String[] name_Array = { "待验收", "已验收" };
	String[] code_Array = { "0", "1" };
	
	// 模拟数据函数
	private AcceptStatusCodesDTO SimuDataFunc() {
		AcceptStatusCodesDTO acscDto = new AcceptStatusCodesDTO();
		// 时戳
		acscDto.setTime(BCDTimeUtil.convertNormalFrom(null));
		// 验收状态编码列表模拟数据
		List<NameCode> acceptStatusCodes = new ArrayList<>();
		// 赋值
		for (int i = 0; i < name_Array.length; i++) {
			NameCode acsc = new NameCode();
			acsc.setName(name_Array[i]);
			acsc.setCode(code_Array[i]);
			acceptStatusCodes.add(acsc);
		}
		// 设置数据
		acscDto.setAcceptStatusCodes(acceptStatusCodes);
		// 消息ID
		acscDto.setMsgId(AppModConfig.msgId);
		AppModConfig.msgId++;
		// 消息id小于0判断
		AppModConfig.msgIdLessThan0Judge();

		return acscDto;
	}

	// 验收状态编码列表模型函数
	public AcceptStatusCodesDTO appModFunc(String distName, String prefCity, String province, Db1Service db1Service) {
		AcceptStatusCodesDTO acscDto = null;
		// 省或直辖市
		if (province == null)
			province = "上海市";
		if (isRealData) { // 真实数据
			acscDto = new AcceptStatusCodesDTO();
			// 时戳
			acscDto.setTime(BCDTimeUtil.convertNormalFrom(null));
			// 验收状态编码列表模拟数据
			List<NameCode> acceptStatusCodes = new ArrayList<>();
			// 赋值
			for (int i = 0; i < name_Array.length; i++) {
				NameCode acsc = new NameCode();
				acsc.setName(name_Array[i]);
				acsc.setCode(code_Array[i]);
				acceptStatusCodes.add(acsc);
			}
			// 设置数据
			acscDto.setAcceptStatusCodes(acceptStatusCodes);
			// 消息ID
			acscDto.setMsgId(AppModConfig.msgId);
			AppModConfig.msgId++;
			// 消息id小于0判断
			AppModConfig.msgIdLessThan0Judge();
		} else { // 模拟数据
			// 模拟数据函数
			acscDto = SimuDataFunc();
		}

		return acscDto;
	}
}
