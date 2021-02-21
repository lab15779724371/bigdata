package com.tfit.BdBiProcSrvShEduOmc.appmod.user;

import java.io.IOException;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tfit.BdBiProcSrvShEduOmc.config.AppModConfig;
import com.tfit.BdBiProcSrvShEduOmc.dao.domain.TEduBdUserDo;
import com.tfit.BdBiProcSrvShEduOmc.dto.IOTHttpRspVO;
import com.tfit.BdBiProcSrvShEduOmc.dto.user.AmAdminUserCheckDTO;
import com.tfit.BdBiProcSrvShEduOmc.service.Db2Service;

//管理员管理员用户名检测应用模型
public class AmAdminUserCheckAppMod {
	private static final Logger logger = LogManager.getLogger(AmAdminUserCheckAppMod.class.getName());
	
	@Autowired
	ObjectMapper objectMapper = new ObjectMapper();
	
	// 管理员用户名检测模型函数
	public IOTHttpRspVO appModFunc(String token,String strBodyCont, Db2Service db2Service, int[] codes) {
		IOTHttpRspVO normResp = null;
		if(strBodyCont != null && db2Service != null) {
			AmAdminUserCheckDTO aaucDto = null;
			try {
				if(strBodyCont != null)
					aaucDto = objectMapper.readValue(strBodyCont, AmAdminUserCheckDTO.class);
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
			if(aaucDto != null) {
				
				//获取父账户信息以token
	    	    TEduBdUserDo parTebuDo = db2Service.getBdUserInfoByToken(token);
	    	    	
				boolean isFindAdmin = false;
				//从数据源ds2的数据表t_edu_bd_user中查找用户信息以单位ID
				List<TEduBdUserDo> tebuDoList = db2Service.getBdUserInfoByUserOrg(aaucDto.getOrgId(),parTebuDo.getUserType());			
	  			if(tebuDoList != null) {      //
	  				for(int i = 0; i < tebuDoList.size(); i++) {
	  					if(tebuDoList.get(i).getIsAdmin() == 1) {
	  						isFindAdmin = true;
	  						break;
	  					}
	  				}
	  			}
	  			if(isFindAdmin) {  //有管理员账号
	  				normResp = AppModConfig.getNormalResp(null);
	  				logger.info("管理员账号已存在！");
	  			}
	  			else {    //没有管理员账号
	  				logger.info("管理员账号不存在！");
	  				codes[0] = 2033;
	  			}
			}
		}
		else {
			codes[0] = 2017;
			logger.info("访问接口参数非法！");
		}						
		
		return normResp;
	}
}
