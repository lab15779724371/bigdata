package com.tfit.BdBiProcSrvShEduOmc.appmod.im;

import java.util.ArrayList;
import java.util.List;

import com.tfit.BdBiProcSrvShEduOmc.config.AppModConfig;
import com.tfit.BdBiProcSrvShEduOmc.dto.im.AssignStatusCodesDTO;
import com.tfit.BdBiProcSrvShEduOmc.dto.NameCode;
import com.tfit.BdBiProcSrvShEduOmc.service.Db1Service;
import com.tfit.BdBiProcSrvShEduOmc.util.BCDTimeUtil;

//指派状态编码列表应用模型
public class AssignStatusCodesAppMod {
	// 是否为真实数据标识
	private static boolean isRealData = true;

	// 数组数据初始化
	String[] name_Array = { "未指派", "已指派", "已取消" };
	String[] code_Array = { "0", "1", "2" };

	// 模拟数据函数
	private AssignStatusCodesDTO SimuDataFunc() {
		AssignStatusCodesDTO ascDto = new AssignStatusCodesDTO();
		// 时戳
		ascDto.setTime(BCDTimeUtil.convertNormalFrom(null));
		// 指派状态编码列表模拟数据
		List<NameCode> assignStatusCodes = new ArrayList<>();
		// 赋值
		for (int i = 0; i < name_Array.length; i++) {
			NameCode asc = new NameCode();
			asc.setName(name_Array[i]);
			asc.setCode(code_Array[i]);
			assignStatusCodes.add(asc);
		}
		// 设置数据
		ascDto.setAssignStatusCodes(assignStatusCodes);
		// 消息ID
		ascDto.setMsgId(AppModConfig.msgId);
		AppModConfig.msgId++;
		// 消息id小于0判断
		AppModConfig.msgIdLessThan0Judge();

		return ascDto;
	}

	// 指派状态编码列表模型函数
	public AssignStatusCodesDTO appModFunc(String distName, String prefCity, String province, Db1Service db1Service) {
		AssignStatusCodesDTO ascDto = null;
		// 省或直辖市
		if (province == null)
			province = "上海市";
		if (isRealData) { // 真实数据
			ascDto = new AssignStatusCodesDTO();
			// 时戳
			ascDto.setTime(BCDTimeUtil.convertNormalFrom(null));
			// 指派状态编码列表模拟数据
			List<NameCode> assignStatusCodes = new ArrayList<>();
			// 赋值
			for (int i = 0; i < name_Array.length; i++) {
				NameCode asc = new NameCode();
				asc.setName(name_Array[i]);
				asc.setCode(code_Array[i]);
				assignStatusCodes.add(asc);
			}
			// 设置数据
			ascDto.setAssignStatusCodes(assignStatusCodes);
			// 消息ID
			ascDto.setMsgId(AppModConfig.msgId);
			AppModConfig.msgId++;
			// 消息id小于0判断
			AppModConfig.msgIdLessThan0Judge();
		} else { // 模拟数据
			// 模拟数据函数
			ascDto = SimuDataFunc();
		}

		return ascDto;
	}
}
