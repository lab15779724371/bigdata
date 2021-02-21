package com.tfit.BdBiProcSrvShEduOmc.appmod.user;

import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.tfit.BdBiProcSrvShEduOmc.config.AppModConfig;
import com.tfit.BdBiProcSrvShEduOmc.dao.domain.TEduBdRoleDo;
import com.tfit.BdBiProcSrvShEduOmc.dto.NameCode;
import com.tfit.BdBiProcSrvShEduOmc.dto.user.RoleNameCodesDTO;
import com.tfit.BdBiProcSrvShEduOmc.service.Db2Service;
import com.tfit.BdBiProcSrvShEduOmc.util.BCDTimeUtil;

//角色名称编码列表应用模型
public class RoleNameCodesAppMod {
	private static final Logger logger = LogManager.getLogger(RoleNameCodesAppMod.class.getName());
	
	//是否为真实数据标识
	private static boolean isRealData = true;
	
	//数组数据初始化
	String[] name_Array = {"局长", "副局长", "校长", "副校长", "科员"};
	String[] code_Array = {"局长", "副局长", "校长", "副校长", "科员"};
	
	//模拟数据函数
	private RoleNameCodesDTO SimuDataFunc() {
		RoleNameCodesDTO rncDto = new RoleNameCodesDTO();
		//时戳
		rncDto.setTime(BCDTimeUtil.convertNormalFrom(null));
		//角色名称编码列表模拟数据
		List<NameCode> roleNameCodes = new ArrayList<>();
		//赋值
		for (int i = 0; i < name_Array.length; i++) {
			NameCode rnc = new NameCode();
			rnc.setName(name_Array[i]);
			rnc.setCode(code_Array[i]);
			roleNameCodes.add(rnc);
		}
		//设置数据
		rncDto.setRoleNameCodes(roleNameCodes);
		//消息ID
		rncDto.setMsgId(AppModConfig.msgId);
		AppModConfig.msgId++;
		// 消息id小于0判断
		AppModConfig.msgIdLessThan0Judge();
		
		return rncDto;
	}
	
	// 角色名称编码列表模型函数
	public RoleNameCodesDTO appModFunc(String roleType, Db2Service db2Service, int[] codes) {
		RoleNameCodesDTO rncDto = null;
		if(isRealData) {       //真实数据
			//按参数形式处理
	  		if(db2Service != null) {
	  			List<TEduBdRoleDo> tebrDoList = null;
	  			if(roleType == null) {
	  				//从数据源ds2的数据表t_edu_bd_role中查找所有角色名称
	  				tebrDoList = db2Service.getBdRoleInfoAllRoleNames();
	  			}
	  			else {
	  				//从数据源ds2的数据表t_edu_bd_role中查找角色名称以角色类型
	  				tebrDoList = db2Service.getBdRoleInfoRoleNamesByRoleType(Integer.parseInt(roleType));
	  			}
	  			if(tebrDoList != null) {
	  				rncDto = new RoleNameCodesDTO();
	  				//时戳
	  				rncDto.setTime(BCDTimeUtil.convertNormalFrom(null));
	  				//角色名称数据
	  				List<NameCode> roleNameCodes = new ArrayList<>();
	  				//赋值
	  				for(int i = 0; i < tebrDoList.size(); i++) {
	  					NameCode rnc = new NameCode();
	  					if(tebrDoList.get(i).getRoleName() != null) {
	  						if(!tebrDoList.get(i).getRoleName().isEmpty()) {
	  							rnc.setCode(tebrDoList.get(i).getRoleName());
	  							rnc.setName(tebrDoList.get(i).getRoleName());
	  							roleNameCodes.add(rnc);
	  						}
	  					}
	  				}
	  				//设置数据
	  				rncDto.setRoleNameCodes(roleNameCodes);
	  				//消息ID
	  				rncDto.setMsgId(AppModConfig.msgId);
	  				AppModConfig.msgId++;
	  				// 消息id小于0判断
	  				AppModConfig.msgIdLessThan0Judge();	  				
	  			}
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
