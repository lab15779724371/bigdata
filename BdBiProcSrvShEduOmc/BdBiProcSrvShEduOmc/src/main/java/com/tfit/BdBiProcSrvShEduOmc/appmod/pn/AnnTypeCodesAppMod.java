package com.tfit.BdBiProcSrvShEduOmc.appmod.pn;

import java.util.ArrayList;
import java.util.List;

import com.tfit.BdBiProcSrvShEduOmc.config.AppModConfig;
import com.tfit.BdBiProcSrvShEduOmc.dto.NameCode;
import com.tfit.BdBiProcSrvShEduOmc.dto.pn.AnnTypeCodesDTO;
import com.tfit.BdBiProcSrvShEduOmc.service.Db1Service;
import com.tfit.BdBiProcSrvShEduOmc.util.BCDTimeUtil;

//通知类型编码列表应用模型
public class AnnTypeCodesAppMod {
	// 是否为真实数据标识
	private static boolean isRealData = true;

	// 数组数据初始化
	String[] name_Array = { "通知公告", "会议通知", "健康宣教" };
	String[] code_Array = { "0", "1", "2" };
	
	// 模拟数据函数
	private AnnTypeCodesDTO SimuDataFunc() {
		AnnTypeCodesDTO atcDto = new AnnTypeCodesDTO();
		// 时戳
		atcDto.setTime(BCDTimeUtil.convertNormalFrom(null));
		// 通知类型编码列表模拟数据
		List<NameCode> annTypeCodes = new ArrayList<>();
		// 赋值
		for (int i = 0; i < name_Array.length; i++) {
			NameCode atc = new NameCode();
			atc.setName(name_Array[i]);
			atc.setCode(code_Array[i]);
			annTypeCodes.add(atc);
		}
		// 设置数据
		atcDto.setAnnTypeCodes(annTypeCodes);
		// 消息ID
		atcDto.setMsgId(AppModConfig.msgId);
		AppModConfig.msgId++;
		// 消息id小于0判断
		AppModConfig.msgIdLessThan0Judge();

		return atcDto;
	}

	// 通知类型编码列表模型函数
	public AnnTypeCodesDTO appModFunc(String distName, String prefCity, String province, Db1Service db1Service) {
		AnnTypeCodesDTO atcDto = null;
		// 省或直辖市
		if (province == null)
			province = "上海市";
		if (isRealData) { // 真实数据
			atcDto = new AnnTypeCodesDTO();
			// 时戳
			atcDto.setTime(BCDTimeUtil.convertNormalFrom(null));
			// 通知类型编码列表模拟数据
			List<NameCode> annTypeCodes = new ArrayList<>();
			// 赋值
			for (int i = 0; i < name_Array.length; i++) {
				NameCode atc = new NameCode();
				atc.setName(name_Array[i]);
				atc.setCode(code_Array[i]);
				annTypeCodes.add(atc);
			}
			// 设置数据
			atcDto.setAnnTypeCodes(annTypeCodes);
			// 消息ID
			atcDto.setMsgId(AppModConfig.msgId);
			AppModConfig.msgId++;
			// 消息id小于0判断
			AppModConfig.msgIdLessThan0Judge();
		} else { // 模拟数据
			// 模拟数据函数
			atcDto = SimuDataFunc();
		}

		return atcDto;
	}
}