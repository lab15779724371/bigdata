package com.tfit.BdBiProcSrvShEduOmc.appmod.pn;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

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
import com.tfit.BdBiProcSrvShEduOmc.dao.domain.edubd.TEduBdMsgNoticeDo;
import com.tfit.BdBiProcSrvShEduOmc.dao.domain.edubd.TEduBdNoticeStatusDo;
import com.tfit.BdBiProcSrvShEduOmc.dto.pn.RbAnnounce;
import com.tfit.BdBiProcSrvShEduOmc.dto.pn.RbAnnounceBody;
import com.tfit.BdBiProcSrvShEduOmc.dto.pn.RbAnnounceDTO;
import com.tfit.BdBiProcSrvShEduOmc.dto.pn.RbUlAttachment;
import com.tfit.BdBiProcSrvShEduOmc.service.Db2Service;
import com.tfit.BdBiProcSrvShEduOmc.util.BCDTimeUtil;
import com.tfit.BdBiProcSrvShEduOmc.util.SendMailAcceUtils;
import com.tfit.BdBiProcSrvShEduOmc.util.UniqueIdGen;

//通知发布应用模型
public class RbAnnounceAppMod {
	private static final Logger logger = LogManager.getLogger(RbAnnounceAppMod.class.getName());
	
	@Autowired
	ObjectMapper objectMapper = new ObjectMapper();	
	
	//通知发布应用模型函数
  	public RbAnnounceDTO appModFunc(String token, String strBodyCont, Db2Service db2Service) {
  		RbAnnounceDTO racDto = null;
  		//按参数形式处理
  		if(token != null && db2Service != null) {
  			RbAnnounceBody rab = null;
  			try {
  				if(strBodyCont != null)
  					rab = objectMapper.readValue(strBodyCont, RbAnnounceBody.class);
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
  			if(rab != null) {
  				racDto = new RbAnnounceDTO();
  				TEduBdMsgNoticeDo tebmnDo = new TEduBdMsgNoticeDo();
  				String[] receivers = null;
  				List<String> userReceivers = new ArrayList<>(), mailReceivers = new ArrayList<>();
  				String senderMail = null;
  				//通知id
  				tebmnDo.setId(UniqueIdGen.uuidInterSeg());
  				//创建 时间
  				tebmnDo.setCreateTime(BCDTimeUtil.convertNormalFrom(null));
  				//接收人
  				if(rab.getReceiver() != null) {
  					tebmnDo.setReceiver(rab.getReceiver());
  					receivers = rab.getReceiver().split(",");
  					for(int i = 0; i < receivers.length; i++) {
  						int idx = receivers[i].indexOf("@");
  						if(idx == -1) {     //用户名接收人
  							userReceivers.add(receivers[i]);
  							//查找该用户邮件以该用户名
  						    TEduBdUserDo tebuDo = db2Service.getBdUserInfoByUserName(receivers[i]);
  						    if(tebuDo != null) {
  						    	if(tebuDo.getEmail() != null)
  						    		mailReceivers.add(tebuDo.getEmail());
  						    }
  						}
  						else {      //邮件接收人
  							mailReceivers.add(receivers[i]);
  						}
  					}
  				}
  				//通知标题
  				if(rab.getTitle() != null)
  					tebmnDo.setTitle(rab.getTitle());
  				//通知类型
  				tebmnDo.setAnnounceType(0);
  				if(rab.getAnnounceType() != null) {
  					tebmnDo.setAnnounceType(rab.getAnnounceType());
  				}
  				//通知内容
  				if(rab.getAnnCont() != null) {
  					tebmnDo.setAnnCont(rab.getAnnCont().getBytes());
  				}
  				//附件信息
  				if(rab.getAmInfos() != null) {
  					List<RbUlAttachment> amInfos = rab.getAmInfos();
  					if(amInfos.size() > 0) {
  						String amInfo = "";
  						for(int i = 0; i < amInfos.size(); i++) {
  							String amRes = " ";
  							int idx = -1;
  							logger.info("当前附件URL：" + amInfos.get(i).getAmUrl() + "，文件服务域名：" + SpringConfig.repfile_srvdn);
  							if((idx = amInfos.get(i).getAmUrl().indexOf(SpringConfig.repfile_srvdn)) != -1) {
  								amRes = amInfos.get(i).getAmUrl().substring(idx+SpringConfig.repfile_srvdn.length(), amInfos.get(i).getAmUrl().length());
  							}
  							if(i < amInfos.size() - 1) {
  								amInfo += amInfos.get(i).getAmName();
  								amInfo += ",";
  								amInfo += amRes;
  								amInfo += ",";
  							}
  							else {
  								amInfo += amInfos.get(i).getAmName();
  								amInfo += ",";
  								amInfo += amRes;
  							}
  						}
  						tebmnDo.setAmInfo(amInfo);
  						tebmnDo.setAmFlag(1);
  					}
  					else {
  						tebmnDo.setAmFlag(0);
  					}
  				}
  				else {
  					tebmnDo.setAmFlag(0);
  				}
  				//记录有效性
  				tebmnDo.setStat(1);
  				//从数据源ds2的数据表t_edu_bd_user中查找授权码以当前授权码
  			    TEduBdUserDo tebuDo = db2Service.getBdUserInfoByCurAuthCode(token);
  			    senderMail = tebuDo.getEmail();
  				//用户信息
  			    tebmnDo.setUserName(tebuDo.getUserAccount());
  			    //更新时间
  			    tebmnDo.setLastUpdateTime(BCDTimeUtil.convertNormalFrom(null));
  				//保存发布通知到数据库
  			    db2Service.insertMsgNotice(tebmnDo);
  			    //保存发布通知状态数据到数据库
  			    if(receivers != null) {
  			    	List<TEduBdNoticeStatusDo> tebnsDoList = new ArrayList<>();
  			    	//所属用户消息通知状态记录
  			    	TEduBdNoticeStatusDo tebnsDo = new TEduBdNoticeStatusDo();
  			    	tebnsDo.setCreateTime(BCDTimeUtil.convertNormalFrom(null));
  			    	tebnsDo.setBulletinId(tebmnDo.getId());
  			    	tebnsDo.setOwnerUserName(tebmnDo.getUserName());
  			    	tebnsDo.setRcvUserName(tebmnDo.getUserName());
  			    	tebnsDo.setReadCount(0);
  			    	tebnsDo.setSignFlag(0);
  			    	tebnsDo.setLastUpdateTime(BCDTimeUtil.convertNormalFrom(null));
  			    	tebnsDo.setStat(1);
  			    	tebnsDoList.add(tebnsDo);
  			    	//所属用户、接收用户消息通知状态记录
  			    	int userReceiversLen = userReceivers.size();
  			    	for(int i = 0; i < userReceiversLen; i++) {
  			    		tebnsDo = new TEduBdNoticeStatusDo();
  			    		tebnsDo.setCreateTime(BCDTimeUtil.convertNormalFrom(null));
  			    		tebnsDo.setBulletinId(tebmnDo.getId());
  			    		tebnsDo.setOwnerUserName(tebmnDo.getUserName());
  			    		tebnsDo.setRcvUserName(userReceivers.get(i));
  			    		tebnsDo.setReadCount(0);
  			    		tebnsDo.setLastUpdateTime(BCDTimeUtil.convertNormalFrom(null));
  			    		tebnsDo.setStat(1);
  			    		tebnsDoList.add(tebnsDo);
  			    	}
  			    	//插入用户消息通知状态记录
  			    	for(int i = 0; i < tebnsDoList.size(); i++) {
  			    		db2Service.insertMsgNoticeStatus(tebnsDoList.get(i));
  			    	}
  			    }
  			    //发送邮件
  			    if(rab.getIsSynMailNotice() != null && senderMail != null && mailReceivers.size() > 0) {
  			    	if(rab.getIsSynMailNotice() == 1) {     //同步发送邮件
  			    		//查询邮件服务记录以用户名
  	  	  				TEduBdMailSrvDo tebmsDo = db2Service.getMailSrvInfoByUserName(tebuDo.getUserAccount());
  	  	  				int amCount = 0, mailReceiverCount = mailReceivers.size();
  	  	  				if(rab.getAmInfos() != null)
  	  	  				{
  	  	  					if(rab.getAmInfos().size() > 0)
  	  	  						amCount = rab.getAmInfos().size();
  	  	  				}
  	  	  				String[] amFileNames = null, amOutNames = null, rcvMailUsers = new String[mailReceiverCount];
  	  	  				if(amCount > 0) {
  	  	  					amFileNames = new String[amCount];
  	  	  					amOutNames = new String[amCount];
  	  	  					for(int i = 0; i < amCount; i++) {
  	  	  						String amUrl = rab.getAmInfos().get(i).getAmUrl();
  	  	  						if(amUrl.indexOf(SpringConfig.repfile_srvdn) != -1) {
  	  	  							//String amFileName = SpringConfig.tomcatSrvDirs[1] + amUrl.substring(SpringConfig.repfile_srvdn.length());
  	  	  							String amFileName = amUrl;
  	  	  					        amFileNames[i] = amFileName;
  	  	  					        logger.info("amFileNames[" + i + "] = " + amFileNames[i]);
  	  	  					        amOutNames[i] = rab.getAmInfos().get(i).getAmName();
  	  	  						}
  	  	  					}
  	  	  				}
  	  	  				for(int i = 0; i < mailReceiverCount; i++) {
  	  	  					rcvMailUsers[i] = mailReceivers.get(i);
  	  	  				}
  	  	  				String mailTitle = null, mailCont = null;
  	  	  				mailTitle = rab.getTitle();
  	  	  				mailCont = rab.getAnnCont();
  	  	  				if(tebmsDo != null)
  	  	  					SendMailAcceUtils.sendMail(tebmsDo, mailTitle, mailCont, amFileNames, amOutNames, rcvMailUsers, false, 1,"1");
  			    	}
  			    }
  			    
  			    //时戳
  			    racDto.setTime(BCDTimeUtil.convertNormalFrom(null));
  			    //数据
  			    RbAnnounce rbAnnounce = new RbAnnounce();
  			    rbAnnounce.setBulletinId(tebmnDo.getId());
  			    rbAnnounce.setTitle(tebmnDo.getTitle());
  			    rbAnnounce.setAnnounceType(tebmnDo.getAnnounceType());
  			    rbAnnounce.setPublishDate(tebmnDo.getCreateTime());
		    	//设置数据
		    	racDto.setRbAnnounce(rbAnnounce);
				//消息ID
		    	racDto.setMsgId(AppModConfig.msgId);
				AppModConfig.msgId++;
				// 消息id小于0判断
				AppModConfig.msgIdLessThan0Judge();
  			}
  		}
  		else {
  			logger.info("访问接口参数非法！");
  		}
  		
  		return racDto;
  	}
}