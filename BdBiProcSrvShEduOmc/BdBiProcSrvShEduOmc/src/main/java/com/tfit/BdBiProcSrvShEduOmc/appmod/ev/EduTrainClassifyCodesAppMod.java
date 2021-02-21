package com.tfit.BdBiProcSrvShEduOmc.appmod.ev;

import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.tfit.BdBiProcSrvShEduOmc.config.AppModConfig;
import com.tfit.BdBiProcSrvShEduOmc.dto.NameCode;
import com.tfit.BdBiProcSrvShEduOmc.dto.ev.EduTrainClassifyCodesDTO;
import com.tfit.BdBiProcSrvShEduOmc.service.Db1Service;
import com.tfit.BdBiProcSrvShEduOmc.util.BCDTimeUtil;

//教育培训分类编码列表应用模型
public class EduTrainClassifyCodesAppMod {
	private static final Logger logger = LogManager.getLogger(EduTrainClassifyCodesAppMod.class.getName());
	
	//是否为真实数据标识
	private static boolean isRealData = false;
	
	//数组数据初始化
	String[] name_Array = {"系统操作", "食品安全", "政策法规"};
	String[] code_Array = {"0", "1", "2"};
	
	//模拟数据函数
	private EduTrainClassifyCodesDTO SimuDataFunc() {
		EduTrainClassifyCodesDTO ctsDto = new EduTrainClassifyCodesDTO();
		//时戳
		ctsDto.setTime(BCDTimeUtil.convertNormalFrom(null));
		//教育培训分类编码列表模拟数据
		List<NameCode> contractors = new ArrayList<>();
		//赋值
		for (int i = 0; i < name_Array.length; i++) {
			NameCode cts = new NameCode();
			cts.setName(name_Array[i]);
			cts.setCode(code_Array[i]);
			contractors.add(cts);
		}
		//设置数据
		ctsDto.setEduTrainClassifyCodes(contractors);
		//消息ID
		ctsDto.setMsgId(AppModConfig.msgId);
		AppModConfig.msgId++;
		// 消息id小于0判断
		AppModConfig.msgIdLessThan0Judge();
		
		return ctsDto;
	}
	
	// 教育培训分类编码列表模型函数
	public EduTrainClassifyCodesDTO appModFunc(String distName, String prefCity, String province, Db1Service db1Service) {
		EduTrainClassifyCodesDTO ctsDto = null;
		if(isRealData) {       //真实数据
			
		}
		else {    //模拟数据
			//模拟数据函数
			ctsDto = SimuDataFunc();
		}		

		return ctsDto;
	}
}
