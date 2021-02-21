package com.tfit.BdBiProcSrvShEduOmc.appmod.user;

import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.tfit.BdBiProcSrvShEduOmc.config.AppModConfig;
import com.tfit.BdBiProcSrvShEduOmc.dto.NameCode;
import com.tfit.BdBiProcSrvShEduOmc.dto.user.UserSrcCodesDTO;
import com.tfit.BdBiProcSrvShEduOmc.service.Db1Service;
import com.tfit.BdBiProcSrvShEduOmc.util.BCDTimeUtil;

//账号来源编码列表应用模型
public class UserSrcCodesAppMod {
	private static final Logger logger = LogManager.getLogger(UserSrcCodesAppMod.class.getName());
	
	//是否为真实数据标识
	private static boolean isRealData = false;
	
	//数组数据初始化
	String[] name_Array = {"阳光午餐", "大数据平台"};
	String[] code_Array = {"0", "1"};
	
	//模拟数据函数
	private UserSrcCodesDTO SimuDataFunc() {
		UserSrcCodesDTO uscDto = new UserSrcCodesDTO();
		//时戳
		uscDto.setTime(BCDTimeUtil.convertNormalFrom(null));
		//账号来源编码列表模拟数据
		List<NameCode> userSrcCodes = new ArrayList<>();
		//赋值
		for (int i = 0; i < name_Array.length; i++) {
			NameCode usc = new NameCode();
			usc.setName(name_Array[i]);
			usc.setCode(code_Array[i]);
			userSrcCodes.add(usc);
		}
		//设置数据
		uscDto.setUserSrcCodes(userSrcCodes);
		//消息ID
		uscDto.setMsgId(AppModConfig.msgId);
		AppModConfig.msgId++;
		// 消息id小于0判断
		AppModConfig.msgIdLessThan0Judge();
		
		return uscDto;
	}
	
	// 账号来源编码列表模型函数
	public UserSrcCodesDTO appModFunc(Db1Service db1Service, int[] codes) {
		UserSrcCodesDTO uscDto = null;
		if(isRealData) {       //真实数据
			//按参数形式处理
	  		if(db1Service != null) {
	  			uscDto = new UserSrcCodesDTO();
				//时戳
				uscDto.setTime(BCDTimeUtil.convertNormalFrom(null));
				//是否留样编码列表模拟数据
				List<NameCode> userSrcCodes = new ArrayList<>();
				//赋值
				for (int i = 0; i < name_Array.length; i++) {
					NameCode usc = new NameCode();
					usc.setName(name_Array[i]);
					usc.setCode(code_Array[i]);
					userSrcCodes.add(usc);
				}
				//设置数据
				uscDto.setUserSrcCodes(userSrcCodes);
				//消息ID
				uscDto.setMsgId(AppModConfig.msgId);
				AppModConfig.msgId++;
				// 消息id小于0判断
				AppModConfig.msgIdLessThan0Judge();
	  		}
	  		else {
	  			logger.info("访问接口参数非法！");
	  		}
		}
		else {    //模拟数据
			//模拟数据函数
			uscDto = SimuDataFunc();
		}		

		return uscDto;
	}
}
