package com.tfit.BdBiProcSrvShEduOmc.appmod.im;

import java.util.ArrayList;
import java.util.List;

import com.tfit.BdBiProcSrvShEduOmc.config.AppModConfig;
import com.tfit.BdBiProcSrvShEduOmc.dto.NameCode;
import com.tfit.BdBiProcSrvShEduOmc.dto.im.DishExeListDTO;
import com.tfit.BdBiProcSrvShEduOmc.service.Db1Service;
import com.tfit.BdBiProcSrvShEduOmc.util.BCDTimeUtil;

//排菜执行编码列表应用模型
public class DishExeListAppMod {
	//是否为真实数据标识
	private static boolean isRealData = true;
	
	//数组数据初始化
	String[] name_Array = {"未排菜", "已排菜"};
	String[] code_Array = {"0", "1"};
		
	//模拟数据函数
	private DishExeListDTO SimuDataFunc() {
		DishExeListDTO delDto = new DishExeListDTO();
		//时戳
		delDto.setTime(BCDTimeUtil.convertNormalFrom(null));
		//排菜执行编码列表模拟数据
		List<NameCode> dishExeList = new ArrayList<>();
		//赋值
		for (int i = 0; i < name_Array.length; i++) {
			NameCode del = new NameCode();
			del.setName(name_Array[i]);
			del.setCode(code_Array[i]);			
			dishExeList.add(del);
		}
		//设置数据
		delDto.setDishExeList(dishExeList);
		//消息ID
		delDto.setMsgId(AppModConfig.msgId);
		AppModConfig.msgId++;
		// 消息id小于0判断
		AppModConfig.msgIdLessThan0Judge();
		
		return delDto;
	}
	
	// 排菜执行编码列表模型函数
	public DishExeListDTO appModFunc(String distName, String prefCity, String province, Db1Service db1Service) {
		DishExeListDTO delDto = null;
		// 省或直辖市
		if(province == null)
			province = "上海市";
		if(isRealData) {       //真实数据
			delDto = new DishExeListDTO();
			//时戳
			delDto.setTime(BCDTimeUtil.convertNormalFrom(null));
			//排菜执行编码列表模拟数据
			List<NameCode> dishExeList = new ArrayList<>();
			//赋值
			for (int i = 0; i < name_Array.length; i++) {
				NameCode del = new NameCode();
				del.setName(name_Array[i]);
				del.setCode(code_Array[i]);
				dishExeList.add(del);
			}
			//设置数据
			delDto.setDishExeList(dishExeList);
			//消息ID
			delDto.setMsgId(AppModConfig.msgId);
			AppModConfig.msgId++;
			// 消息id小于0判断
			AppModConfig.msgIdLessThan0Judge();
		}
		else {    //模拟数据
			//模拟数据函数
			delDto = SimuDataFunc();
		}		

		return delDto;
	}
}
