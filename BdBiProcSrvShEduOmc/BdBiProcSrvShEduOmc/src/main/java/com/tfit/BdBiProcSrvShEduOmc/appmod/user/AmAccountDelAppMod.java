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
import com.tfit.BdBiProcSrvShEduOmc.dto.user.AmAccountDelDTO;
import com.tfit.BdBiProcSrvShEduOmc.service.Db2Service;

//账号删除应用模型
public class AmAccountDelAppMod {
	private static final Logger logger = LogManager.getLogger(AmAccountDelAppMod.class.getName());
	
	@Autowired
	ObjectMapper objectMapper = new ObjectMapper();
	
	//账号删除应用模型函数
  	public IOTHttpRspVO appModFunc(String strBodyCont, Db2Service db2Service, int[] codes) {
  		IOTHttpRspVO normResp = null;
		//按参数形式处理
  		if(strBodyCont != null && db2Service != null) {
  			AmAccountDelDTO aueDto = null;
  			String userName = null;
  			try {
  				if(strBodyCont != null)
  					aueDto = objectMapper.readValue(strBodyCont, AmAccountDelDTO.class);
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
  				//查询用户名
  				TEduBdUserDo tebuDo = db2Service.getBdUserInfoByUserName(userName);
  				if(tebuDo.getUserAccount() != null) {
  					//删除数据源ds2的数据表t_edu_bd_user中记录以用户名
  					boolean flag = db2Service.DeleteBdUserInfoByUserName(userName);
  					if(flag) {
  						//删除数据源ds2的数据表t_edu_bd_user_perm中记录以用户名
  					    db2Service.DeleteBdUserPermInfoByUserId(tebuDo.getId());
  					    normResp = AppModConfig.getNormalResp(null);
  					    logger.info("账号删除成功！");
  					}
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
