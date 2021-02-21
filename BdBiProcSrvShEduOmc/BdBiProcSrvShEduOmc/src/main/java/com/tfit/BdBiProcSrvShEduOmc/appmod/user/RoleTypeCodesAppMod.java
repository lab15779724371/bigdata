package com.tfit.BdBiProcSrvShEduOmc.appmod.user;

import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.tfit.BdBiProcSrvShEduOmc.config.AppModConfig;
import com.tfit.BdBiProcSrvShEduOmc.dto.NameCode;
import com.tfit.BdBiProcSrvShEduOmc.dto.user.RoleTypeCodesDTO;
import com.tfit.BdBiProcSrvShEduOmc.service.Db2Service;
import com.tfit.BdBiProcSrvShEduOmc.util.BCDTimeUtil;

//角色类型编码列表应用模型
public class RoleTypeCodesAppMod {
	private static final Logger logger = LogManager.getLogger(RoleTypeCodesAppMod.class.getName());
	
	//是否为真实数据标识
	private static boolean isRealData = true;
	
	//数组数据初始化
	String[] name_Array = {"监管部门", "学校"};
	String[] code_Array = {"1", "2"};
	
	//模拟数据函数
	private RoleTypeCodesDTO SimuDataFunc() {
		RoleTypeCodesDTO rncDto = new RoleTypeCodesDTO();
		//时戳
		rncDto.setTime(BCDTimeUtil.convertNormalFrom(null));
		//角色类型编码列表模拟数据
		List<NameCode> roleTypeCodes = new ArrayList<>();
		//赋值
		for (int i = 0; i < name_Array.length; i++) {
			NameCode rnc = new NameCode();
			rnc.setName(name_Array[i]);
			rnc.setCode(code_Array[i]);
			roleTypeCodes.add(rnc);
		}
		//设置数据
		rncDto.setRoleTypeCodes(roleTypeCodes);
		//消息ID
		rncDto.setMsgId(AppModConfig.msgId);
		AppModConfig.msgId++;
		// 消息id小于0判断
		AppModConfig.msgIdLessThan0Judge();
		
		return rncDto;
	}
	
	// 角色类型编码列表模型函数
	public RoleTypeCodesDTO appModFunc(Db2Service db2Service, int[] codes) {
		RoleTypeCodesDTO rncDto = null;
		if(isRealData) {       //真实数据
			//按参数形式处理
	  		if(db2Service != null) {
	  			rncDto = new RoleTypeCodesDTO();
				//时戳
				rncDto.setTime(BCDTimeUtil.convertNormalFrom(null));
				//是否留样编码列表模拟数据
				List<NameCode> roleTypeCodes = new ArrayList<>();
				//赋值
				for (int i = 0; i < name_Array.length; i++) {
					NameCode rnc = new NameCode();
					rnc.setName(name_Array[i]);
					rnc.setCode(code_Array[i]);
					roleTypeCodes.add(rnc);
				}
				//设置数据
				rncDto.setRoleTypeCodes(roleTypeCodes);
				//消息ID
				rncDto.setMsgId(AppModConfig.msgId);
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
			rncDto = SimuDataFunc();
		}		

		return rncDto;
	}
}
