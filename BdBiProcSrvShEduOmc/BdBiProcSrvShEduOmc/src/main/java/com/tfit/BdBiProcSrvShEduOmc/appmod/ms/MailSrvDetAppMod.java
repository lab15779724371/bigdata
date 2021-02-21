package com.tfit.BdBiProcSrvShEduOmc.appmod.ms;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.tfit.BdBiProcSrvShEduOmc.config.AppModConfig;
import com.tfit.BdBiProcSrvShEduOmc.dao.domain.TEduBdUserDo;
import com.tfit.BdBiProcSrvShEduOmc.dao.domain.edubd.TEduBdMailSrvDo;
import com.tfit.BdBiProcSrvShEduOmc.dto.ms.MailSrvDet;
import com.tfit.BdBiProcSrvShEduOmc.dto.ms.MailSrvDetDTO;
import com.tfit.BdBiProcSrvShEduOmc.service.Db2Service;
import com.tfit.BdBiProcSrvShEduOmc.util.BCDTimeUtil;

//邮件服务器详情应用模型
public class MailSrvDetAppMod {
	private static final Logger logger = LogManager.getLogger(MailSrvDetAppMod.class.getName());
	
	//邮件服务器详情应用模型函数
  	public MailSrvDetDTO appModFunc(String token, Db2Service db2Service) {
  		MailSrvDetDTO msdDto = null;
  		//按参数形式处理
  		if(token != null && db2Service != null) {
  			//从数据源ds1的数据表t_edu_bd_user中查找用户信息以授权码token
  			TEduBdUserDo tebuDo = db2Service.getBdUserInfoByToken(token);
  			if(tebuDo.getId() != null) {
  				String userName = tebuDo.getUserAccount();
  				//查询邮件服务记录以用户名
  				TEduBdMailSrvDo tebmsDo = db2Service.getMailSrvInfoByUserName(userName);
				msdDto = new MailSrvDetDTO();
				//时戳
				msdDto.setTime(BCDTimeUtil.convertNormalFrom(null));
				//数据
				MailSrvDet mailSrvDet = new MailSrvDet();
				mailSrvDet.setUserName(userName);                                                               //用户名
				mailSrvDet.setMailUserName(tebuDo.getEmail());                                                  //邮件用户名
				if(tebmsDo != null) {
					mailSrvDet.setMailUserName(tebmsDo.getEmail());                                             //邮件用户名
					mailSrvDet.setPassword(tebmsDo.getPassword());                                              //邮件密码
					mailSrvDet.setRcvMailServer(tebmsDo.getRcvServer());                                        //收件服务器
					mailSrvDet.setRcvMailPort(tebmsDo.getRcvSrvPort());                                         //收件端口
					mailSrvDet.setRcvMailPortNo(tebmsDo.getRcvSrvPortNo());                                     //收件端口号
					mailSrvDet.setSendMailServer(tebmsDo.getSendServer());                                      //发件服务器
					mailSrvDet.setSendMailPort(tebmsDo.getSendSrvPort());                                       //发件端口
					mailSrvDet.setSendMailPortNo(tebmsDo.getSendSrvPortNo());                                   //发件端口号
				}
				//设置数据
				msdDto.setMailSrvDet(mailSrvDet);
				//消息ID
				msdDto.setMsgId(AppModConfig.msgId);
				AppModConfig.msgId++;
				// 消息id小于0判断
				AppModConfig.msgIdLessThan0Judge();
  			}
  		}
  		else {
  			logger.info("访问接口参数非法！");
  		}
  		
  		return msdDto;
  	}
}