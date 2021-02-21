package com.tfit.BdBiProcSrvShEduOmc.appmod.user;

import java.io.IOException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tfit.BdBiProcSrvShEduOmc.config.AppModConfig;
import com.tfit.BdBiProcSrvShEduOmc.dao.domain.TEduBdUserDo;
import com.tfit.BdBiProcSrvShEduOmc.dto.IOTHttpRspVO;
import com.tfit.BdBiProcSrvShEduOmc.dto.user.AmUserCheckDTO;
import com.tfit.BdBiProcSrvShEduOmc.service.Db2Service;

//用户名检测应用模型
public class AmUserCheckAppMod {
	private static final Logger logger = LogManager.getLogger(AmUserCheckAppMod.class.getName());
	
	@Autowired
	ObjectMapper objectMapper = new ObjectMapper();
	
	// 用户名检测模型函数
	public IOTHttpRspVO appModFunc(String strBodyCont, Db2Service db2Service, int[] codes) {
		IOTHttpRspVO normResp = null;
		if(strBodyCont != null && db2Service != null) {
			AmUserCheckDTO aucDto = null;
			String userName = null;
			try {
				if(strBodyCont != null)
					aucDto = objectMapper.readValue(strBodyCont, AmUserCheckDTO.class);
				if(aucDto != null)
					userName = aucDto.getUserName();
			} catch (JsonParseException e) {
				// TODO 自动生成的 catch 块
				e.printStackTrace();
			} catch (JsonMappingException e) {
				// TODO 自动生成的 catch 块
				e.printStackTrace();
			} catch (IOException e) {
				// TODO 自动生成的 catch 块
				e.printStackTrace();
			}
			if(aucDto != null && userName != null) {
				TEduBdUserDo tebuDo = db2Service.getBdUserInfoByUserName(userName);  			
	  			if(tebuDo.getUserAccount() != null) {      //用户名（账号）已注册
	  				logger.info("用户账号已存在！");
	  				codes[0] = 2005;
	  			}
	  			else {    //用户名（账号）未注册
	  				normResp = AppModConfig.getNormalResp(null);	  				
	  			}
			}
		}
		else {
			logger.info("访问接口参数非法！");
		}						
		
		return normResp;
	}
}