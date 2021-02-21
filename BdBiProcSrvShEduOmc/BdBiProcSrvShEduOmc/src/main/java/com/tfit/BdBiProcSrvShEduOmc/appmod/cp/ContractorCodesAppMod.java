package com.tfit.BdBiProcSrvShEduOmc.appmod.cp;

import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.tfit.BdBiProcSrvShEduOmc.config.AppModConfig;
import com.tfit.BdBiProcSrvShEduOmc.dto.NameCode;
import com.tfit.BdBiProcSrvShEduOmc.dto.cp.ContractorCodesDTO;
import com.tfit.BdBiProcSrvShEduOmc.service.Db1Service;
import com.tfit.BdBiProcSrvShEduOmc.util.BCDTimeUtil;

//承办人编码列表应用模型
public class ContractorCodesAppMod {
	private static final Logger logger = LogManager.getLogger(ContractorCodesAppMod.class.getName());
	
	//是否为真实数据标识
	private static boolean isRealData = true;
	
	//数组数据初始化
	String[] name_Array = {"徐汇区教育局", "黄浦区教育局", "静安区教育局", "长宁区教育局", "普陀区教育局", "虹口区教育局", "杨浦区教育局", "闵行区教育局", "嘉定区教育局", "宝山区教育局", "浦东新区教育局", "松江区教育局", "金山区教育局", "青浦区教育局", "奉贤区教育局", "崇明区教育局"};
	String[] code_Array = {"11", "1", "10", "12", "13", "14", "15", "16", "2", "3", "4", "5", "6", "7", "8", "9"};		
	
	//模拟数据函数
	private ContractorCodesDTO SimuDataFunc() {
		ContractorCodesDTO ctsDto = new ContractorCodesDTO();
		//时戳
		ctsDto.setTime(BCDTimeUtil.convertNormalFrom(null));
		//承办人编码列表模拟数据
		List<NameCode> contractors = new ArrayList<>();
		//赋值
		for (int i = 0; i < name_Array.length; i++) {
			NameCode cts = new NameCode();
			cts.setName(name_Array[i]);
			cts.setCode(code_Array[i]);
			contractors.add(cts);
		}
		//设置数据
		ctsDto.setContractorCodes(contractors);
		//消息ID
		ctsDto.setMsgId(AppModConfig.msgId);
		AppModConfig.msgId++;
		// 消息id小于0判断
		AppModConfig.msgIdLessThan0Judge();
		
		return ctsDto;
	}
	
	// 承办人编码列表模型函数
	public ContractorCodesDTO appModFunc(String distName, String prefCity, String province, Db1Service db1Service) {
		ContractorCodesDTO ctsDto = null;
		if(isRealData) {       //真实数据
			// 省或直辖市
			if(province == null)
				province = "上海市";
			ctsDto = new ContractorCodesDTO();
			//时戳
			ctsDto.setTime(BCDTimeUtil.convertNormalFrom(null));
			//承办人编码列表模拟数据
			List<NameCode> contractors = new ArrayList<>();
			//赋值
			for (String key : AppModConfig.compDepNameToIdMap3bd.keySet()) {
				NameCode cts = new NameCode();
				if(!key.equals("其他")) {
					cts.setName(key);
					cts.setCode(key);
					contractors.add(cts);
				}
			}
			for(String key : AppModConfig.compDepNameToIdMap2.keySet()) {
				NameCode cts = new NameCode();
				if(key.equals("市教委")) {
					cts.setName(key);
					cts.setCode(key);
					contractors.add(cts);
				}
			}
			//设置数据
			ctsDto.setContractorCodes(contractors);
			//消息ID
			ctsDto.setMsgId(AppModConfig.msgId);
			AppModConfig.msgId++;
			// 消息id小于0判断
			AppModConfig.msgIdLessThan0Judge();
		}
		else {    //模拟数据
			//模拟数据函数
			ctsDto = SimuDataFunc();
		}		

		return ctsDto;
	}
}
