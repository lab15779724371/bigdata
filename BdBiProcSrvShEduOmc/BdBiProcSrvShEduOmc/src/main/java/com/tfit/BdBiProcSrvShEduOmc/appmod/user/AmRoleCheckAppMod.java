package com.tfit.BdBiProcSrvShEduOmc.appmod.user;

import java.io.IOException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tfit.BdBiProcSrvShEduOmc.config.AppModConfig;
import com.tfit.BdBiProcSrvShEduOmc.dao.domain.TEduBdRoleDo;
import com.tfit.BdBiProcSrvShEduOmc.dto.IOTHttpRspVO;
import com.tfit.BdBiProcSrvShEduOmc.dto.user.AmRoleCheckDTO;
import com.tfit.BdBiProcSrvShEduOmc.service.Db2Service;
import com.tfit.BdBiProcSrvShEduOmc.service.edubd.EduBdService;

//角色名检测应用模型
public class AmRoleCheckAppMod {
	private static final Logger logger = LogManager.getLogger(AmRoleCheckAppMod.class.getName());
	
	@Autowired
	ObjectMapper objectMapper = new ObjectMapper();
	
	// 角色名检测模型函数
	public IOTHttpRspVO appModFunc(String strBodyCont, Db2Service db2Service, EduBdService eduBdService, int[] codes) {
		IOTHttpRspVO normResp = null;
		if(strBodyCont != null && db2Service != null && eduBdService != null) {
			AmRoleCheckDTO arcDto = null;
			String roleName = null;
			Integer roleType = null;
			try {
				if(strBodyCont != null)
					arcDto = objectMapper.readValue(strBodyCont, AmRoleCheckDTO.class);
				if(arcDto != null) {
					roleName = arcDto.getRoleName();
					roleType = arcDto.getRoleType();
				}
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
			if(arcDto != null && roleName != null && roleType != null) {
				logger.info("roleType = " + roleType + ", roleName = " + roleName);
				TEduBdRoleDo tebrDo = eduBdService.getBdRoleInfoByRoleName(roleType, roleName);	
	  			if(tebrDo != null) {      //角色名（账号）已注册
	  				logger.info("角色已存在！");
	  				codes[0] = 2027;
	  			}
	  			else {    //角色名（账号）未注册
	  				normResp = AppModConfig.getNormalResp(null);	  				
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
