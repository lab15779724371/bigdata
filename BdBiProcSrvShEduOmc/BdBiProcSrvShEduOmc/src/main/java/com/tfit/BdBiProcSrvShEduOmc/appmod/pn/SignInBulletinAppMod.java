package com.tfit.BdBiProcSrvShEduOmc.appmod.pn;

import java.io.IOException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tfit.BdBiProcSrvShEduOmc.config.AppModConfig;
import com.tfit.BdBiProcSrvShEduOmc.dao.domain.edubd.TEduBdNoticeStatusDo;
import com.tfit.BdBiProcSrvShEduOmc.dto.pn.SignInBulletin;
import com.tfit.BdBiProcSrvShEduOmc.dto.pn.SignInBulletinBody;
import com.tfit.BdBiProcSrvShEduOmc.dto.pn.SignInBulletinDTO;
import com.tfit.BdBiProcSrvShEduOmc.service.Db2Service;
import com.tfit.BdBiProcSrvShEduOmc.util.BCDTimeUtil;

//签到通知应用模型
public class SignInBulletinAppMod {
	private static final Logger logger = LogManager.getLogger(SignInBulletinAppMod.class.getName());
	
	@Autowired
	ObjectMapper objectMapper = new ObjectMapper();	
	
	//签到通知应用模型函数
  	public SignInBulletinDTO appModFunc(String token, String strBodyCont, Db2Service db2Service) {
  		SignInBulletinDTO sibDto = null;
  		//按参数形式处理
  		if(token != null && db2Service != null) {
  			SignInBulletinBody sibb = null;
  			try {
  				if(strBodyCont != null)
  					sibb = objectMapper.readValue(strBodyCont, SignInBulletinBody.class);
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
  			if(sibb != null) {
  				sibDto = new SignInBulletinDTO();
  				//通知ID、发布用户、接收用户
  				if(sibb.getBulletinId() != null && sibb.getSendUserName() != null && sibb.getRcvUserName() != null) {
  				    TEduBdNoticeStatusDo tebnsDo = new TEduBdNoticeStatusDo();
  				    tebnsDo.setBulletinId(sibb.getBulletinId());
  				    tebnsDo.setOwnerUserName(sibb.getSendUserName());
  				    tebnsDo.setRcvUserName(sibb.getRcvUserName());
  				    tebnsDo.setSignFlag(1);
  					//更新签到标识
  				    db2Service.updateSignFlagByTEduBdNoticeStatusDo(tebnsDo);  					
  				}
  			    //时戳
  			    sibDto.setTime(BCDTimeUtil.convertNormalFrom(null));
  			    //数据
  			    SignInBulletin signInBulletin = new SignInBulletin();
  			    signInBulletin.setBulletinId(sibb.getBulletinId());
  			    signInBulletin.setSendUserName(sibb.getSendUserName());
  			    signInBulletin.setRcvUserName(sibb.getRcvUserName());
  			    signInBulletin.setSignFlag(1);
		    	//设置数据
		    	sibDto.setSignInBulletin(signInBulletin);
				//消息ID
		    	sibDto.setMsgId(AppModConfig.msgId);
				AppModConfig.msgId++;
				// 消息id小于0判断
				AppModConfig.msgIdLessThan0Judge();
  			}
  		}
  		else {
  			logger.info("访问接口参数非法！");
  		}
  		
  		return sibDto;
  	}
}