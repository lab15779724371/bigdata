package com.tfit.BdBiProcSrvShEduOmc.appmod.ms;

import java.io.IOException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tfit.BdBiProcSrvShEduOmc.config.AppModConfig;
import com.tfit.BdBiProcSrvShEduOmc.dao.domain.TEduBdUserDo;
import com.tfit.BdBiProcSrvShEduOmc.dao.domain.edubd.TEduBdMailSrvDo;
import com.tfit.BdBiProcSrvShEduOmc.dto.ms.SaveMailSrvInfo;
import com.tfit.BdBiProcSrvShEduOmc.dto.ms.SaveMailSrvInfoDTO;
import com.tfit.BdBiProcSrvShEduOmc.service.Db2Service;
import com.tfit.BdBiProcSrvShEduOmc.util.BCDTimeUtil;
import com.tfit.BdBiProcSrvShEduOmc.util.UniqueIdGen;

//保存邮件服务器设置应用模型
public class SaveMailSrvInfoAppMod {
	private static final Logger logger = LogManager.getLogger(SaveMailSrvInfoAppMod.class.getName());
	
	@Autowired
	ObjectMapper objectMapper = new ObjectMapper();
	
	//保存邮件服务器设置应用模型函数
  	public SaveMailSrvInfoDTO appModFunc(String token, String strBodyCont, Db2Service db2Service) {
  		SaveMailSrvInfoDTO smsiDto = null;
  		//按参数形式处理
  		if(token != null && db2Service != null) {
  			SaveMailSrvInfo smsi = null;
  			try {
  				if(strBodyCont != null)
  					smsi = objectMapper.readValue(strBodyCont, SaveMailSrvInfo.class);
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
  			if(smsi != null) {
  				boolean flag = false;
  				String userName = null;
  				//从数据源ds2的数据表t_edu_supervise_user中查找授权码以当前授权码
  			    TEduBdUserDo tebuDo = db2Service.getBdUserInfoByCurAuthCode(token);
  			    if(tebuDo.getUserAccount() != null) {
  			    	userName = tebuDo.getUserAccount();
  			    	//查询邮件服务记录以用户名
  			    	TEduBdMailSrvDo tebmsDo = db2Service.getMailSrvInfoByUserName(userName);
  			    	if(tebmsDo != null) {     //更新记录字段
  			    		tebmsDo.setUserName(userName);                                  //用户名
  			    		tebmsDo.setEmail(smsi.getMailUserName());                       //邮件用户名（包含@后面内容）
  			    		tebmsDo.setPassword(smsi.getPassword());                        //邮件用户密码
  			    		tebmsDo.setRcvServer(smsi.getRcvMailServer());                  //接收服务器
  			    		tebmsDo.setRcvSrvPort(smsi.getRcvMailPort());                   //接收端口
  			    		tebmsDo.setRcvSrvPortNo(smsi.getRcvMailPortNo());               //接收端口号
  			    		tebmsDo.setSendServer(smsi.getSendMailServer());                //发送服务器
  			    		tebmsDo.setSendSrvPort(smsi.getSendMailPort());                 //发送服务端口
  			    		tebmsDo.setSendSrvPortNo(smsi.getSendMailPortNo());             //发送服务端口号
  			    		//更新邮件服务记录
  			    		flag = db2Service.updateMailSrv(tebmsDo);
  			    	}
  			    	else {     //添加记录
  			    		tebmsDo = new TEduBdMailSrvDo();
  			    		tebmsDo.setId(UniqueIdGen.uuidInterSeg());
  			    		tebmsDo.setUserName(userName);                                  //用户名
  			    		tebmsDo.setEmail(smsi.getMailUserName());                       //邮件用户名（包含@后面内容）
  			    		tebmsDo.setPassword(smsi.getPassword());                        //邮件用户密码
  			    		tebmsDo.setRcvServer(smsi.getRcvMailServer());                  //接收服务器
  			    		tebmsDo.setRcvSrvPort(smsi.getRcvMailPort());                   //接收端口
  			    		tebmsDo.setRcvSrvPortNo(smsi.getRcvMailPortNo());               //接收端口号
  			    		tebmsDo.setSendServer(smsi.getSendMailServer());                //发送服务器
  			    		tebmsDo.setSendSrvPort(smsi.getSendMailPort());                 //发送服务端口
  			    		tebmsDo.setSendSrvPortNo(smsi.getSendMailPortNo());             //发送服务端口号
  			    		tebmsDo.setStat(1);                                             //0:无效，1:有效
  			    		//插入邮件服务记录
  			    		db2Service.insertMailSrv(tebmsDo);
  			    		flag = true;
  			    	}
  			    }
  			    if(flag) {
  			    	//更新记录到数据源ds2的数据表t_edu_bd_user中
  			    	TEduBdUserDo tebuDo2 = new TEduBdUserDo();
  			    	tebuDo2.setEmail(smsi.getMailUserName());
  			    	db2Service.UpdateBdUserInfo(tebuDo2, token,false);
  			    	smsiDto = new SaveMailSrvInfoDTO();
  			    	if(userName != null) {
  			    		//查询邮件服务记录以用户名
  			    		TEduBdMailSrvDo tebmsDo = db2Service.getMailSrvInfoByUserName(userName);
  			    		if(tebmsDo != null) {
  			    			//时戳
  			    			smsiDto.setTime(BCDTimeUtil.convertNormalFrom(null));
  			    			//设置数据
  			    			SaveMailSrvInfo saveMailSrvInfo = new SaveMailSrvInfo();
  			    			saveMailSrvInfo.setUserName(userName);
  			    			saveMailSrvInfo.setMailUserName(tebmsDo.getEmail());
  			    			saveMailSrvInfo.setPassword(tebmsDo.getPassword());
  			    			saveMailSrvInfo.setRcvMailServer(tebmsDo.getRcvServer());
  			    			saveMailSrvInfo.setRcvMailPort(tebmsDo.getRcvSrvPort());
  			    			saveMailSrvInfo.setRcvMailPortNo(tebmsDo.getRcvSrvPortNo());
  			    			saveMailSrvInfo.setSendMailServer(tebmsDo.getSendServer());
  			    			saveMailSrvInfo.setSendMailPort(tebmsDo.getSendSrvPort());
  			    			saveMailSrvInfo.setSendMailPortNo(tebmsDo.getSendSrvPortNo());
  			    			smsiDto.setSaveMailSrvInfo(saveMailSrvInfo);
  			    			//消息ID
  			    			smsiDto.setMsgId(AppModConfig.msgId);
  			    			AppModConfig.msgId++;
  			    			// 消息id小于0判断
  			    			AppModConfig.msgIdLessThan0Judge();
  			    		}
  			    	}  	  				
  			    }
  			}
  		}
  		else {
  			logger.info("访问接口参数非法！");
  		}
  		
  		return smsiDto;
  	}
}
