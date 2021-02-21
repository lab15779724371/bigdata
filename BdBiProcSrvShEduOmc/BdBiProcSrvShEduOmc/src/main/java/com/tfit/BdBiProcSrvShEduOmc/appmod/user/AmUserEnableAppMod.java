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
import com.tfit.BdBiProcSrvShEduOmc.dto.user.AmUserEnableDTO;
import com.tfit.BdBiProcSrvShEduOmc.service.Db2Service;
import com.tfit.BdBiProcSrvShEduOmc.util.BCDTimeUtil;

//账号启禁用应用模型
public class AmUserEnableAppMod {
	private static final Logger logger = LogManager.getLogger(AmUserEnableAppMod.class.getName());
	
	@Autowired
	ObjectMapper objectMapper = new ObjectMapper();
	
	//账号启禁用应用模型函数
  	public IOTHttpRspVO appModFunc(String strBodyCont, Db2Service db2Service, int[] codes) {
  		IOTHttpRspVO normResp = null;
		//按参数形式处理
  		if(strBodyCont != null && db2Service != null) {
  			AmUserEnableDTO aueDto = null;
  			String userName = null;
  			try {
  				if(strBodyCont != null)
  					aueDto = objectMapper.readValue(strBodyCont, AmUserEnableDTO.class);
  				if(aueDto != null)
  					userName = aueDto.getUserName();
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
  			if(aueDto != null && userName != null) {
  				if(aueDto.getUserEnable() != null) {
  					TEduBdUserDo curTebuDo = new TEduBdUserDo();
  					curTebuDo.setForbid(aueDto.getUserEnable());
  					curTebuDo.setLastUpdateTime(BCDTimeUtil.convertNormalFrom(null));
  					//更新记录到数据源ds2的数据表t_edu_bd_user中以输入字段
  					boolean flag = db2Service.UpdateBdUserInfoByField(curTebuDo, "user_account", userName);
  					if(flag) {
  						normResp = AppModConfig.getNormalResp(null);
  						if(aueDto.getUserEnable() == 1) {
  							logger.info("账号启用成功！");
  						}
  						else {
  							logger.info("账号禁用成功！");
  						}
  					}
  				}
  				else {
  					logger.info("启禁用参数错误！");
  					codes[0] = 2019;
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
