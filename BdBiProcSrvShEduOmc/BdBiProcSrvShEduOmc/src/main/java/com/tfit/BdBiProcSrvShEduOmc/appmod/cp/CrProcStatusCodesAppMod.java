package com.tfit.BdBiProcSrvShEduOmc.appmod.cp;

import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.tfit.BdBiProcSrvShEduOmc.config.AppModConfig;
import com.tfit.BdBiProcSrvShEduOmc.dto.NameCode;
import com.tfit.BdBiProcSrvShEduOmc.dto.cp.CrProcStatusCodesDTO;
import com.tfit.BdBiProcSrvShEduOmc.service.Db1Service;
import com.tfit.BdBiProcSrvShEduOmc.util.BCDTimeUtil;

//投诉举报处理状态编码列表应用模型
public class CrProcStatusCodesAppMod {
	private static final Logger logger = LogManager.getLogger(CrProcStatusCodesAppMod.class.getName());
	
	//是否为真实数据标识
	private static boolean isRealData = false;
	
	//数组数据初始化
	String[] name_Array = {"待处理", "已指派", "已办结"};
	String[] code_Array = {"0", "1", "2"};	
	
	//模拟数据函数
	private CrProcStatusCodesDTO SimuDataFunc() {
		CrProcStatusCodesDTO cpsDto = new CrProcStatusCodesDTO();
		//时戳
		cpsDto.setTime(BCDTimeUtil.convertNormalFrom(null));
		//投诉举报处理状态编码列表模拟数据
		List<NameCode> crProcStatusCodes = new ArrayList<>();
		//赋值
		for (int i = 0; i < name_Array.length; i++) {
			NameCode cps = new NameCode();
			cps.setName(name_Array[i]);
			cps.setCode(code_Array[i]);
			crProcStatusCodes.add(cps);
		}
		//设置数据
		cpsDto.setCrProcStatusCodes(crProcStatusCodes);
		//消息ID
		cpsDto.setMsgId(AppModConfig.msgId);
		AppModConfig.msgId++;
		// 消息id小于0判断
		AppModConfig.msgIdLessThan0Judge();
		
		return cpsDto;
	}
	
	// 投诉举报处理状态编码列表模型函数
	public CrProcStatusCodesDTO appModFunc(String distName, String prefCity, String province, Db1Service db1Service) {
		CrProcStatusCodesDTO cpsDto = null;
		if(isRealData) {       //真实数据
			// 省或直辖市
			if(province == null)
				province = "上海市";
			cpsDto = new CrProcStatusCodesDTO();
			//时戳
			cpsDto.setTime(BCDTimeUtil.convertNormalFrom(null));
			//承办人编码列表模拟数据
			List<NameCode> crProcStatusCodes = new ArrayList<>();
			//赋值
			for (String key : AppModConfig.crProcStatusNameToIdMap.keySet()) {
				NameCode cps = new NameCode();
				cps.setName(key);
				cps.setCode(String.valueOf(AppModConfig.crProcStatusNameToIdMap.get(key)));
				crProcStatusCodes.add(cps);
			}
			//设置数据
			cpsDto.setCrProcStatusCodes(crProcStatusCodes);
			//消息ID
			cpsDto.setMsgId(AppModConfig.msgId);
			AppModConfig.msgId++;
			// 消息id小于0判断
			AppModConfig.msgIdLessThan0Judge();
		}
		else {    //模拟数据
			//模拟数据函数
			cpsDto = SimuDataFunc();
		}		

		return cpsDto;
	}
}