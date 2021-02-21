package com.tfit.BdBiProcSrvShEduOmc.appmod.user;

import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.tfit.BdBiProcSrvShEduOmc.config.AppModConfig;
import com.tfit.BdBiProcSrvShEduOmc.dto.NameCode;
import com.tfit.BdBiProcSrvShEduOmc.dto.user.AccountTypeCodesDTO;
import com.tfit.BdBiProcSrvShEduOmc.service.Db1Service;
import com.tfit.BdBiProcSrvShEduOmc.util.BCDTimeUtil;

//账号类型编码列表应用模型
public class AccountTypeCodesAppMod {
	private static final Logger logger = LogManager.getLogger(AccountTypeCodesAppMod.class.getName());
	
	//是否为真实数据标识
	private static boolean isRealData = false;
	
	//数组数据初始化
	String[] name_Array = {"普通账号", "管理员账号"};
	String[] code_Array = {"0", "1"};
	
	//模拟数据函数
	private AccountTypeCodesDTO SimuDataFunc() {
		AccountTypeCodesDTO utcDto = new AccountTypeCodesDTO();
		//时戳
		utcDto.setTime(BCDTimeUtil.convertNormalFrom(null));
		//账号类型编码列表模拟数据
		List<NameCode> dishNameCodes = new ArrayList<>();
		//赋值
		for (int i = 0; i < name_Array.length; i++) {
			NameCode utc = new NameCode();
			utc.setName(name_Array[i]);
			utc.setCode(code_Array[i]);
			dishNameCodes.add(utc);
		}
		//设置数据
		utcDto.setAccountTypeCodes(dishNameCodes);
		//消息ID
		utcDto.setMsgId(AppModConfig.msgId);
		AppModConfig.msgId++;
		// 消息id小于0判断
		AppModConfig.msgIdLessThan0Judge();
		
		return utcDto;
	}
	
	// 账号类型编码列表模型函数
	public AccountTypeCodesDTO appModFunc(Db1Service db1Service, int[] codes) {
		AccountTypeCodesDTO utcDto = null;
		if(isRealData) {       //真实数据
			
		}
		else {    //模拟数据
			//模拟数据函数
			utcDto = SimuDataFunc();
		}		

		return utcDto;
	}
}
