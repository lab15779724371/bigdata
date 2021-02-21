package com.tfit.BdBiProcSrvShEduOmc.appmod.im;

import java.util.ArrayList;
import java.util.List;

import com.tfit.BdBiProcSrvShEduOmc.config.AppModConfig;
import com.tfit.BdBiProcSrvShEduOmc.dto.im.IsRsCodesDTO;
import com.tfit.BdBiProcSrvShEduOmc.dto.NameCode;
import com.tfit.BdBiProcSrvShEduOmc.service.Db1Service;
import com.tfit.BdBiProcSrvShEduOmc.util.BCDTimeUtil;

//是否留样编码列表应用模型
public class IsRsCodesAppMod {
	//是否为真实数据标识
	private static boolean isRealData = true;
	
	//数组数据初始化
	String[] name_Array = {"未留样", "已留样"};
	String[] code_Array = {"0", "1"};
		
	//模拟数据函数
	private IsRsCodesDTO SimuDataFunc() {
		IsRsCodesDTO ircDto = new IsRsCodesDTO();
		//时戳
		ircDto.setTime(BCDTimeUtil.convertNormalFrom(null));
		//是否留样编码列表模拟数据
		List<NameCode> isRsCodes = new ArrayList<>();
		//赋值
		for (int i = 0; i < name_Array.length; i++) {
			NameCode irc = new NameCode();
			irc.setName(name_Array[i]);
			irc.setCode(code_Array[i]);			
			isRsCodes.add(irc);
		}
		//设置数据
		ircDto.setIsRsCodes(isRsCodes);
		//消息ID
		ircDto.setMsgId(AppModConfig.msgId);
		AppModConfig.msgId++;
		// 消息id小于0判断
		AppModConfig.msgIdLessThan0Judge();
		
		return ircDto;
	}
	
	// 是否留样编码列表模型函数
	public IsRsCodesDTO appModFunc(String distName, String prefCity, String province, Db1Service db1Service) {
		IsRsCodesDTO ircDto = null;
		// 省或直辖市
		if(province == null)
			province = "上海市";
		if(isRealData) {       //真实数据
			ircDto = new IsRsCodesDTO();
			//时戳
			ircDto.setTime(BCDTimeUtil.convertNormalFrom(null));
			//是否留样编码列表模拟数据
			List<NameCode> isRsCodes = new ArrayList<>();
			//赋值
			for (int i = 0; i < name_Array.length; i++) {
				NameCode irc = new NameCode();
				irc.setName(name_Array[i]);
				irc.setCode(code_Array[i]);
				isRsCodes.add(irc);
			}
			//设置数据
			ircDto.setIsRsCodes(isRsCodes);
			//消息ID
			ircDto.setMsgId(AppModConfig.msgId);
			AppModConfig.msgId++;
			// 消息id小于0判断
			AppModConfig.msgIdLessThan0Judge();
		}
		else {    //模拟数据
			//模拟数据函数
			ircDto = SimuDataFunc();
		}		

		return ircDto;
	}
}