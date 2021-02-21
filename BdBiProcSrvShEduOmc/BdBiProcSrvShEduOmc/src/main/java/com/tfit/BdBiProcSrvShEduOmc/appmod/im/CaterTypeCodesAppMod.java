package com.tfit.BdBiProcSrvShEduOmc.appmod.im;

import java.util.ArrayList;
import java.util.List;

import com.tfit.BdBiProcSrvShEduOmc.config.AppModConfig;
import com.tfit.BdBiProcSrvShEduOmc.dao.domain.TEduCaterTypeDo;
import com.tfit.BdBiProcSrvShEduOmc.dto.NameCode;
import com.tfit.BdBiProcSrvShEduOmc.dto.im.CaterTypeCodesDTO;
import com.tfit.BdBiProcSrvShEduOmc.service.Db1Service;
import com.tfit.BdBiProcSrvShEduOmc.service.SaasService;
import com.tfit.BdBiProcSrvShEduOmc.util.BCDTimeUtil;

//餐别编码列表应用模型
public class CaterTypeCodesAppMod {
	//是否为真实数据标识
	private static boolean isRealData = true;
	
	//数组数据初始化
	String[] name_Array = {"早餐", "午餐", "晚餐", "午点", "早点"};
	String[] code_Array = {"0", "1", "2", "3", "4"};
		
	//模拟数据函数
	private CaterTypeCodesDTO SimuDataFunc() {
		CaterTypeCodesDTO ctcDto = new CaterTypeCodesDTO();
		//时戳
		ctcDto.setTime(BCDTimeUtil.convertNormalFrom(null));
		//餐别编码列表模拟数据
		List<NameCode> caterTypeCodes = new ArrayList<>();
		//赋值
		for (int i = 0; i < name_Array.length; i++) {
			NameCode ctc = new NameCode();
			ctc.setName(name_Array[i]);
			ctc.setCode(code_Array[i]);			
			caterTypeCodes.add(ctc);
		}
		//设置数据
		ctcDto.setCaterTypeCodes(caterTypeCodes);
		//消息ID
		ctcDto.setMsgId(AppModConfig.msgId);
		AppModConfig.msgId++;
		// 消息id小于0判断
		AppModConfig.msgIdLessThan0Judge();
		
		return ctcDto;
	}
	
	// 餐别编码列表模型函数
	public CaterTypeCodesDTO appModFunc(String distName, String prefCity, String province, Db1Service db1Service, SaasService saasService) {
		CaterTypeCodesDTO ctcDto = null;
		// 省或直辖市
		if(province == null)
			province = "上海市";
		if(isRealData) {       //真实数据
			//获取所有餐别类型名称
			List<TEduCaterTypeDo> tectDoList = saasService.getAllCaterTypeNames();
			if(tectDoList != null) {
				ctcDto = new CaterTypeCodesDTO();
				//时戳
				ctcDto.setTime(BCDTimeUtil.convertNormalFrom(null));
				//餐别编码列表模拟数据
				List<NameCode> caterTypeCodes = new ArrayList<>();
				//赋值
				for (int i = 0; i < tectDoList.size(); i++) {
					NameCode ctc = new NameCode();
					ctc.setName(tectDoList.get(i).getCaterTypeName());
					ctc.setCode(tectDoList.get(i).getCaterTypeName());
					caterTypeCodes.add(ctc);
				}
				//设置数据
				ctcDto.setCaterTypeCodes(caterTypeCodes);
				//消息ID
				ctcDto.setMsgId(AppModConfig.msgId);
				AppModConfig.msgId++;
				// 消息id小于0判断
				AppModConfig.msgIdLessThan0Judge();
			}
		}
		else {    //模拟数据
			//模拟数据函数
			ctcDto = SimuDataFunc();
		}		

		return ctcDto;
	}
}
