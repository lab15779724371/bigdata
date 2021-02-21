package com.tfit.BdBiProcSrvShEduOmc.appmod.im;

import java.util.ArrayList;
import java.util.List;

import com.tfit.BdBiProcSrvShEduOmc.config.AppModConfig;
import com.tfit.BdBiProcSrvShEduOmc.dto.NameCode;
import com.tfit.BdBiProcSrvShEduOmc.dto.im.SchTypeCodesDTO;
import com.tfit.BdBiProcSrvShEduOmc.service.Db1Service;
import com.tfit.BdBiProcSrvShEduOmc.util.BCDTimeUtil;

//学校类型（学制）编码列表应用模型
public class SchTypeCodesAppMod {
	// 数组数据初始化
	String[] name_Array = { "托儿所", "托幼园", "托幼小", "幼儿园", "幼小", "幼小初", "幼小初高", "小学", "初级中学", "高级中学", "完全中学", "九年一贯制学校", "十二年一贯制学校", "职业初中", "中等职业学校", "工读学校", "特殊教育学校", "其他" };
	String[] code_Array = { "0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12", "13", "14", "15", "16", "17" };

	// 学校类型（学制）编码列表模型函数
	public SchTypeCodesDTO appModFunc(String distName, String prefCity, String province, Db1Service db1Service) {
		SchTypeCodesDTO stcDto = null;
		// 省或直辖市
		if (province == null)
			province = "上海市";
		stcDto = new SchTypeCodesDTO();
		// 时戳
		stcDto.setTime(BCDTimeUtil.convertNormalFrom(null));
		// 学校类型（学制）编码列表模拟数据
		List<NameCode> schTypeCodes = new ArrayList<>();
		// 赋值
		for (int i = 0; i < name_Array.length; i++) {
			NameCode stc = new NameCode();
			stc.setName(name_Array[i]);
			stc.setCode(code_Array[i]);
			schTypeCodes.add(stc);
		}
		// 设置数据
		stcDto.setSchTypeCodes(schTypeCodes);
		// 消息ID
		stcDto.setMsgId(AppModConfig.msgId);
		AppModConfig.msgId++;
		// 消息id小于0判断
		AppModConfig.msgIdLessThan0Judge();
		
		return stcDto;
	}
}
