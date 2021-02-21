package com.tfit.BdBiProcSrvShEduOmc.appmod.ms;

import java.io.IOException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tfit.BdBiProcSrvShEduOmc.config.AppModConfig;
import com.tfit.BdBiProcSrvShEduOmc.config.SpringConfig;
import com.tfit.BdBiProcSrvShEduOmc.dao.domain.TEduBdUserDo;
import com.tfit.BdBiProcSrvShEduOmc.dao.domain.edubd.TEduBdMailSrvDo;
import com.tfit.BdBiProcSrvShEduOmc.dto.IOTHttpRspVO;
import com.tfit.BdBiProcSrvShEduOmc.dto.ms.SendTestMailDTO;
import com.tfit.BdBiProcSrvShEduOmc.service.Db1Service;
import com.tfit.BdBiProcSrvShEduOmc.service.Db2Service;
import com.tfit.BdBiProcSrvShEduOmc.util.SendMailAcceUtils;

//发送测试邮件应用模型
public class SendTestMailAppMod {
	private static final Logger logger = LogManager.getLogger(SendTestMailAppMod.class.getName());
	
	@Autowired
	ObjectMapper objectMapper = new ObjectMapper();
	
	//发送邮件方法
	int sendMailMethod = 1;
	
	//添加账号应用模型函数
  	public IOTHttpRspVO appModFunc(String token, String strBodyCont, Db1Service db1Service, Db2Service db2Service, int[] codes) {
  		IOTHttpRspVO normResp = null;
  		//按参数形式处理
  		if(token != null && db2Service != null) {
  			SendTestMailDTO stmDto = null;
  			String mailCont = null;
  			//将json子串转成对象
  			try {
  				if(strBodyCont != null) {
  					if(!strBodyCont.isEmpty()) {
  						stmDto = objectMapper.readValue(strBodyCont, SendTestMailDTO.class);
  						if(stmDto != null)
  							mailCont = stmDto.getMailCont();
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
  			if(mailCont == null)
				mailCont = "您好，这是一封测试邮件！";
  			if(mailCont != null) {  	    	   
  				//从数据源ds1的数据表t_edu_bd_user中查找用户信息以授权码token
  	  			TEduBdUserDo tebuDo = db2Service.getBdUserInfoByToken(token);
  	  			if(tebuDo.getId() != null) {
  	  				//查询邮件服务记录以用户名
  	  				TEduBdMailSrvDo tebmsDo = db2Service.getMailSrvInfoByUserName(tebuDo.getUserAccount());
  	  				if(tebmsDo != null) {
  	  					boolean flag = false;
  	  					//发送邮件
  	  					if(sendMailMethod == 0)
  	  						flag = SendMailAcceUtils.sendMail(tebmsDo, mailCont, mailCont, true);
  	  					else if(sendMailMethod == 1) {
  	  						String[] amFileNames = new String[2], amOutNames = new String[2], rcvMailUsers = new String[1];
  	  						amFileNames[0] = SpringConfig.repfile_srvdn + "/" + "tomcat.png";
  	  						//amFileNames[0] = "C:/Users/Administrator/Pictures/lADPDgQ9rHx5KvzNAZDNAZA_400_400.jpg";
  	  						amOutNames[0] = "附件1.png";
  	  						amFileNames[1] = SpringConfig.repfile_srvdn + "/" + "index.jsp";
  	  						//amFileNames[1] = "C:/Users/Administrator/Pictures/147D8A98-ECA0-49ec-A19C-0F6CC66D5EE7.png";
  	  						amOutNames[1] = "附件2.jsp";
  	  						rcvMailUsers[0] = tebmsDo.getEmail();
  	  						//rcvMailUsers[1] = "185601074@qq.com";
  	  						//rcvMailUsers[1] = "247147210@qq.com";
  	  						flag = SendMailAcceUtils.sendMail(tebmsDo, mailCont, mailCont, amFileNames, amOutNames, rcvMailUsers, false, 0,"1");
  	  					}
  	  					else
  	  						flag = SendMailAcceUtils.sendMail(tebmsDo, mailCont, mailCont, true);
  	  				    if(flag) {
  	  				    	normResp = AppModConfig.getNormalResp(null);
  	  				    	logger.info("邮件发送成功！");
  	  				    }
  	  				    else {
  	  				    	codes[0] = 2037;
  	  				    	logger.info("邮件发送失败！");
  	  				    }
  	  				}
  	  				else {
  	  					codes[0] = 2013;
  	  					logger.info("查询数据记录失败！");
  	  				}
  	  			}
  	  			else {
  	  				codes[0] = 2013;
  	  				logger.info("查询数据记录失败！");
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
