package com.tfit.BdBiProcSrvShEduOmc.appmod.pn;

import java.io.IOException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tfit.BdBiProcSrvShEduOmc.appmod.user.AmUserCheckAppMod;
import com.tfit.BdBiProcSrvShEduOmc.config.AppModConfig;
import com.tfit.BdBiProcSrvShEduOmc.dao.domain.TEduBdUserDo;
import com.tfit.BdBiProcSrvShEduOmc.dao.domain.edubd.TEduBdNoticeStatusDo;
import com.tfit.BdBiProcSrvShEduOmc.dto.IOTHttpRspVO;
import com.tfit.BdBiProcSrvShEduOmc.dto.pn.RbAnnReadBody;
import com.tfit.BdBiProcSrvShEduOmc.service.Db2Service;

//通知阅读应用模型
public class RbAnnReadAppMod {
	private static final Logger logger = LogManager.getLogger(AmUserCheckAppMod.class.getName());
	
	@Autowired
	ObjectMapper objectMapper = new ObjectMapper();
	
	// 用户名检测模型函数
	public IOTHttpRspVO appModFunc(String token, String strBodyCont, Db2Service db2Service, int[] codes) {
		IOTHttpRspVO normResp = null;
		if(strBodyCont != null && db2Service != null) {
			RbAnnReadBody rarb = null;
			String bulletinId = null, userName = null;
			try {
				if(strBodyCont != null)
					rarb = objectMapper.readValue(strBodyCont, RbAnnReadBody.class);
				if(rarb != null) {
					bulletinId = rarb.getBulletinId();
					userName = rarb.getUserName();
				}
				if(userName == null) {
					//从数据源ds2的数据表t_edu_bd_user中查找用户信息以授权码token
				    TEduBdUserDo tebuDo = db2Service.getBdUserInfoByToken(token);
				    if(tebuDo.getUserAccount() != null) {
				    	userName = tebuDo.getUserAccount();
				    }
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
			if(bulletinId != null && userName != null) {
				//查询消息通知状态记录列表以通知id和接收用户名
			    TEduBdNoticeStatusDo tebnsDo = db2Service.getMsgNoticeStatusBybIdRcvUserName(bulletinId, userName);
			    if(tebnsDo != null) {
			    	int curReadCount = 0;
			    	if(tebnsDo.getReadCount() != null)
			    		curReadCount = tebnsDo.getReadCount() + 1;
			    	else
			    		curReadCount++;
			    	//更新阅读次数
			    	db2Service.updateReadCountInMsgNotice(bulletinId, userName, curReadCount);
			    	normResp = AppModConfig.getNormalResp(null);
			    }
			    else {
			    	codes[0] = 2013;
			    	logger.info("查询数据记录失败");
			    }
			}
		}
		else {
			logger.info("访问接口参数非法！");
		}						
		
		return normResp;
	}
}
