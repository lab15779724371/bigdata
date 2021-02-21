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
import com.tfit.BdBiProcSrvShEduOmc.dto.user.AmDelRoleDTO;
import com.tfit.BdBiProcSrvShEduOmc.service.Db2Service;

//删除角色应用模型
public class AmDelRoleAppMod {
	private static final Logger logger = LogManager.getLogger(AmDelRoleAppMod.class.getName());
	
	@Autowired
	ObjectMapper objectMapper = new ObjectMapper();
	
	//账号删除应用模型函数
  	public IOTHttpRspVO appModFunc(String strBodyCont, Db2Service db2Service, int[] codes) {
  		IOTHttpRspVO normResp = null;
		//按参数形式处理
  		if(strBodyCont != null && db2Service != null) {
  			AmDelRoleDTO aueDto = null;
  			String roleId = null;
  			try {
  				if(strBodyCont != null)
  					aueDto = objectMapper.readValue(strBodyCont, AmDelRoleDTO.class);
  				if(aueDto != null)
  					roleId = aueDto.getRoleId();
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
  			if(aueDto != null && roleId != null) {
  				//查询角色名
  				TEduBdRoleDo tebrDo = db2Service.getBdRoleInfoByRoleId(roleId);
  				if(tebrDo.getRoleName() != null) {
  				    //删除数据源ds2的数据表t_edu_bd_role中记录以角色名
  				    boolean flag = db2Service.DeleteBdRoleInfoByRoleName(tebrDo.getRoleName());
  					if(flag) {
  					    normResp = AppModConfig.getNormalResp(null);
  					    logger.info("角色删除成功！");
  					}
  				}
  				else {
  					normResp = AppModConfig.getNormalResp(null);
  					logger.info("角色已删除！");
  				}
  			}
  			else {
  				logger.info("Json格式数据解析失败！");
  				codes[0] = 2011;
  			}  			
  	   }	
  	   else {
  			logger.info("访问接口参数非法！");
  			codes[0] = 2017;
  		}
		
		return normResp;
  	}
}