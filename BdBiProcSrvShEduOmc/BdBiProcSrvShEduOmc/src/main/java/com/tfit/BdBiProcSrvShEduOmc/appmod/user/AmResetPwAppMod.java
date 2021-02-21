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
import com.tfit.BdBiProcSrvShEduOmc.dto.user.AmResetPwDTO;
import com.tfit.BdBiProcSrvShEduOmc.service.Db2Service;
import com.tfit.BdBiProcSrvShEduOmc.util.BCDTimeUtil;

//修改密码应用模型
public class AmResetPwAppMod {
	private static final Logger logger = LogManager.getLogger(AmResetPwAppMod.class.getName());
	
	@Autowired
	ObjectMapper objectMapper = new ObjectMapper();
	
	//修改密码应用模型函数
  	public IOTHttpRspVO appModFunc(String token, String strBodyCont, Db2Service db2Service, int[] codes) {
  		IOTHttpRspVO normResp = null;
		//按参数形式处理
  		if(token != null && strBodyCont != null && db2Service != null) {
  			TEduBdUserDo tebuDo = null;
  			String userName = null;  			
  			AmResetPwDTO arpDto = null;
  			try {
  				if(strBodyCont != null)
  					arpDto = objectMapper.readValue(strBodyCont, AmResetPwDTO.class);
  				if(arpDto != null)
  					userName = arpDto.getUserName();
  				if(userName != null) {
  					tebuDo = db2Service.getBdUserInfoByUserName(userName);
  				}  	  					
  				else {
  					tebuDo = db2Service.getBdUserInfoByToken(token);
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
  			if(arpDto != null && tebuDo != null) {
  				if(arpDto.getNewPassword() != null) {
  					if(arpDto.getOldPassword().equals(tebuDo.getPassword())) {
  						TEduBdUserDo curTebuDo = new TEduBdUserDo();
  						curTebuDo.setPassword(arpDto.getNewPassword());
  						if(arpDto.getSafeGrade() != null)
  							curTebuDo.setSafeGrade(arpDto.getSafeGrade());
  						curTebuDo.setLastUpdateTime(BCDTimeUtil.convertNormalFrom(null));
  						boolean flag = false;
  						if(userName != null)  {
  							//更新记录到数据源ds2的数据表t_edu_bd_user中以输入字段
  							flag = db2Service.UpdateBdUserInfoByField(curTebuDo, "user_account", userName);
  						}
  						else {
  							//更新记录到数据源ds1的数据表t_edu_bd_user中
  	  						flag = db2Service.UpdateBdUserInfo(curTebuDo, token,true);
  						}  						
  						if(flag) {
  							normResp = AppModConfig.getNormalResp(null);
  							logger.info("密码修改成功！");
  						}
  					}
  					else {
  						logger.info("原密码错误！");
  						codes[0] = 2007;
  					}
  				}
  				else {
  					logger.info("新密码不能为空！");
  					codes[0] = 2009;
  				}
  			}
  			else {
  				logger.info("Json格式数据解析失败！");
  				codes[0] = 2011;
  			}
  		}
  		else {
  			logger.info("访问接口参数非法！");
  		}
		
		return normResp;
  	}
}
