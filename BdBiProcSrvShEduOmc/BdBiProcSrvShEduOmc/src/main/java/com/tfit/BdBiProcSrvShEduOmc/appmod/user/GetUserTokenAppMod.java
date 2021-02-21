package com.tfit.BdBiProcSrvShEduOmc.appmod.user;

import java.util.HashMap;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.tfit.BdBiProcSrvShEduOmc.config.AppModConfig;
import com.tfit.BdBiProcSrvShEduOmc.dao.domain.TEduBdUserDo;
import com.tfit.BdBiProcSrvShEduOmc.dto.IOTHttpRspVO;
import com.tfit.BdBiProcSrvShEduOmc.dto.IOTRspType;
import com.tfit.BdBiProcSrvShEduOmc.service.Db1Service;
import com.tfit.BdBiProcSrvShEduOmc.service.Db2Service;

//用户登录应用模型
public class GetUserTokenAppMod {
	private static final Logger logger = LogManager.getLogger(GetUserTokenAppMod.class.getName());
	//登录方式（方案2）
	private IOTHttpRspVO loginMethod1(String userName,Db1Service db1Service, Db2Service db2Service, int[] codes) {
		IOTHttpRspVO normResp = null;
		//按参数形式处理
  		if(userName != null && db2Service != null) {
  			String token = null;
  			TEduBdUserDo tebuDo = db2Service.getBdUserInfoByUserName(userName); 
  			if(tebuDo==null || tebuDo.getUserAccount() ==null || "".equals(tebuDo.getUserAccount())) {
  				Map<String,String> data = new HashMap<String,String>();
				data.put("token", null);      //返回数据对象相关值
				data.put("distId", null);//区域ID，为null表示拥有所有权限
				normResp = AppModConfig.getNormalResp(data,IOTRspType.USERACCOUNT_NOTEXIST_ERR.getCode(),IOTRspType.USERACCOUNT_NOTEXIST_ERR.getMsg());
				logger.info("账号：" + userName + "不存在！");
				return normResp;
  			}
  			if(tebuDo != null) {
  				token = tebuDo.getToken();
  					
  			}
  			if(token != null) {
  				if(tebuDo.getForbid() == 1) {    //账户启用状态
						Map<String,String> data = new HashMap<String,String>();
						data.put("token", token);      //返回数据对象相关值
						String distIdorSCName = AppModConfig.getUserDataPermDistId(token, db1Service, db2Service);  //获取用户权限区域ID
						data.put("token", token);      //返回数据对象相关值
						data.put("distId", distIdorSCName);
						normResp = AppModConfig.getNormalResp(data);
  					}
  					else {
  						codes[0] = 2003;
  					}
			}
			else {    //账户禁用状态
				codes[0] = 2035;
				logger.info("账号：" + userName + "，被禁用！");
			}
		}
		
		return normResp;
	}
	
	//用户登录应用模型函数
  	public IOTHttpRspVO appModFunc(String userName,Db1Service db1Service, Db2Service db2Service, int[] codes) {
  		IOTHttpRspVO normResp = null;
  			normResp = loginMethod1(userName, db1Service, db2Service, codes);
  		
  		return normResp;
  	}
}
