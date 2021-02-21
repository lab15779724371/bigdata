package com.tfit.BdBiProcSrvShEduOmc.appmod.user;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.tfit.BdBiProcSrvShEduOmc.config.AppModConfig;
import com.tfit.BdBiProcSrvShEduOmc.dao.domain.TEduBdUserDo;
import com.tfit.BdBiProcSrvShEduOmc.dao.domain.edubd.EduBdInterfaceColumnsDo;
import com.tfit.BdBiProcSrvShEduOmc.dto.user.AddUserInterfaceColums;
import com.tfit.BdBiProcSrvShEduOmc.dto.user.AddUserInterfaceColumsDTO;
import com.tfit.BdBiProcSrvShEduOmc.service.Db2Service;
import com.tfit.BdBiProcSrvShEduOmc.util.BCDTimeUtil;

//保存个人资料应用模型
public class GetUserInterfaceColumsAppMod {
	private static final Logger logger = LogManager.getLogger(GetUserInterfaceColumsAppMod.class.getName());
	
	//保存个人资料应用模型函数
  	public AddUserInterfaceColumsDTO appModFunc(String token,String interfaceName, Db2Service db2Service) {
  		
  		AddUserInterfaceColumsDTO asuiDto = null;
  		//按参数形式处理
  		if(token != null && db2Service != null) {
  			
  			TEduBdUserDo loginUser =  AppModConfig.getUserByToken(token, db2Service);
  			if(loginUser==null) {
  				return null;
  			}
  			
			//更新记录到数据源ds1的数据表t_edu_bd_user中
  			EduBdInterfaceColumnsDo interfaceColums = db2Service.getByInterfaceName(loginUser.getId(),interfaceName);
			asuiDto = new AddUserInterfaceColumsDTO();
			//时戳
			asuiDto.setTime(BCDTimeUtil.convertNormalFrom(null));
	    	//数据
			AddUserInterfaceColums userColumsOut =null;
			if(interfaceColums!=null) {
				userColumsOut = new AddUserInterfaceColums();
				userColumsOut.setColumns(interfaceColums.getColumns());
				userColumsOut.setInterfaceName(interfaceColums.getInterfaceName());
			}
	    	//设置数据
	    	asuiDto.setUserColums(userColumsOut);
			//消息ID
	    	asuiDto.setMsgId(AppModConfig.msgId);
			AppModConfig.msgId++;
			// 消息id小于0判断
			AppModConfig.msgIdLessThan0Judge();
  		}
  		else {
  			logger.info("访问接口参数非法！");
  		}
  		
  		return asuiDto;
  	}
}
