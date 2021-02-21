package com.tfit.BdBiProcSrvShEduOmc.appmod.im;

import java.util.ArrayList;
import java.util.List;

import com.tfit.BdBiProcSrvShEduOmc.config.AppModConfig;
import com.tfit.BdBiProcSrvShEduOmc.dto.NameCode;
import com.tfit.BdBiProcSrvShEduOmc.dto.im.MatConfirmListDTO;
import com.tfit.BdBiProcSrvShEduOmc.service.Db1Service;
import com.tfit.BdBiProcSrvShEduOmc.util.BCDTimeUtil;

//用料计划确认编码列表应用模型
public class MatConfirmListAppMod {
	//是否为真实数据标识
	private static boolean isRealData = true;
	
	//数组数据初始化
	String[] name_Array = {"未确认", "已确认"};
	String[] code_Array = {"0", "1"};
	
	//模拟数据函数
	private MatConfirmListDTO SimuDataFunc() {
		MatConfirmListDTO mclDto = new MatConfirmListDTO();
		//时戳
		mclDto.setTime(BCDTimeUtil.convertNormalFrom(null));
		//用料计划确认编码列表模拟数据
		List<NameCode> matConfirmList = new ArrayList<>();
		//赋值
		for (int i = 0; i < name_Array.length; i++) {
			NameCode mcl = new NameCode();
			mcl.setName(name_Array[i]);
			mcl.setCode(code_Array[i]);			
			matConfirmList.add(mcl);
		}
		//设置数据
		mclDto.setMatConfirmList(matConfirmList);
		//消息ID
		mclDto.setMsgId(AppModConfig.msgId);
		AppModConfig.msgId++;
		// 消息id小于0判断
		AppModConfig.msgIdLessThan0Judge();
		
		return mclDto;
	}
	
	// 用料计划确认编码列表模型函数
	public MatConfirmListDTO appModFunc(String distName, String prefCity, String province, Db1Service db1Service) {
		MatConfirmListDTO mclDto = null;
		if(isRealData) {       //真实数据
			mclDto = new MatConfirmListDTO();
			//时戳
			mclDto.setTime(BCDTimeUtil.convertNormalFrom(null));
			//用料计划确认编码列表模拟数据
			List<NameCode> matConfirmList = new ArrayList<>();
			//赋值
			for (int i = 0; i < name_Array.length; i++) {
				NameCode mcl = new NameCode();
				mcl.setName(name_Array[i]);
				mcl.setCode(code_Array[i]);			
				matConfirmList.add(mcl);
			}
			//设置数据
			mclDto.setMatConfirmList(matConfirmList);
			//消息ID
			mclDto.setMsgId(AppModConfig.msgId);
			AppModConfig.msgId++;
			// 消息id小于0判断
			AppModConfig.msgIdLessThan0Judge();
		}
		else {    //模拟数据
			//模拟数据函数
			mclDto = SimuDataFunc();
		}		

		return mclDto;
	}
}
