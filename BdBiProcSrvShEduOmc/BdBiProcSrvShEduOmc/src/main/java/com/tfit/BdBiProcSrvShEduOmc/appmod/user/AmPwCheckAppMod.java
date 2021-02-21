package com.tfit.BdBiProcSrvShEduOmc.appmod.user;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.tfit.BdBiProcSrvShEduOmc.config.AppModConfig;
import com.tfit.BdBiProcSrvShEduOmc.dao.domain.TEduBdUserDo;
import com.tfit.BdBiProcSrvShEduOmc.dto.IOTHttpRspVO;
import com.tfit.BdBiProcSrvShEduOmc.service.Db2Service;

//密码验证应用模型
public class AmPwCheckAppMod {
	private static final Logger logger = LogManager.getLogger(AmPwCheckAppMod.class.getName());
	
	//密码验证应用模型函数
  	public IOTHttpRspVO appModFunc(String token, String inPassword, Db2Service db2Service, int[] codes) {
  		IOTHttpRspVO normResp = null;
		//按参数形式处理
  		if(token != null && inPassword != null && db2Service != null) {
  			TEduBdUserDo tebuDo = db2Service.getBdUserInfoByToken(token);  			
  			if(tebuDo != null) {
  				if(tebuDo.getPassword().equals(inPassword)) {
  					normResp = AppModConfig.getNormalResp(null);
  					logger.info("密码验证通过！");  		
  				}
  				else {
  					logger.info("原密码错误！");
  					codes[0] = 2007;
  				}
  			}
  		}
  		else {
  			logger.info("访问接口参数非法！");
  		}
		
		return normResp;
  	}
}