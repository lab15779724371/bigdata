package com.tfit.BdBiProcSrvShEduOmc.appmod.user;

import java.io.IOException;

import org.apache.commons.codec.binary.Base64;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tfit.BdBiProcSrvShEduOmc.config.AppModConfig;
import com.tfit.BdBiProcSrvShEduOmc.config.SpringConfig;
import com.tfit.BdBiProcSrvShEduOmc.dao.domain.TEduBdUserDo;
import com.tfit.BdBiProcSrvShEduOmc.dto.user.AmSaveUserInfo;
import com.tfit.BdBiProcSrvShEduOmc.dto.user.AmSaveUserInfoBody;
import com.tfit.BdBiProcSrvShEduOmc.dto.user.AmSaveUserInfoDTO;
import com.tfit.BdBiProcSrvShEduOmc.dto.user.AmUserInfoDTO;
import com.tfit.BdBiProcSrvShEduOmc.service.Db2Service;
import com.tfit.BdBiProcSrvShEduOmc.util.BCDTimeUtil;
import com.tfit.BdBiProcSrvShEduOmc.util.FileWRCommSys;
import com.tfit.BdBiProcSrvShEduOmc.util.UniqueIdGen;

//保存个人资料应用模型
public class AmSaveUserInfoAppMod {
	private static final Logger logger = LogManager.getLogger(AmSaveUserInfoAppMod.class.getName());
	
	@Autowired
	ObjectMapper objectMapper = new ObjectMapper();
	
	//资源路径
	String fileResPath = "/amSaveUserInfo/";
	
	//个人资料应用模型
	AmUserInfoAppMod auiAppMod = new AmUserInfoAppMod();
	
	//保存个人资料应用模型函数
  	public AmSaveUserInfoDTO appModFunc(String token, String strBodyCont, Db2Service db2Service) {
  		AmSaveUserInfoDTO asuiDto = null;
  		//按参数形式处理
  		if(token != null && db2Service != null) {
  			AmSaveUserInfoBody asuib = null;
  			try {
  				if(strBodyCont != null)
  					asuib = objectMapper.readValue(strBodyCont, AmSaveUserInfoBody.class);
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
  			if(asuib != null) {
  				String userPicUrlRes = null;
  				if(asuib.getHpPicFormat() != null && asuib.getHpPicData() != null) {
  					String[] hpPicDatas = asuib.getHpPicData().split(";");
  					if(hpPicDatas.length > 1) {
  						byte[] FileCont = Base64.decodeBase64(hpPicDatas[1].substring(7));
  						if(FileCont != null) {
  							userPicUrlRes = fileResPath + UniqueIdGen.uuid() + "_hpimg." + asuib.getHpPicFormat();
  							String fileName = SpringConfig.tomcatSrvDirs[0] + userPicUrlRes;
  							FileWRCommSys.WriteBinaryFile(FileCont, null, fileName);
  						}
  					}
  				}
  				TEduBdUserDo tebuDo = new TEduBdUserDo();
  				if(asuib.getFullName() != null)
  					tebuDo.setName(asuib.getFullName());
  				if(asuib.getMobPhone() != null)
  					tebuDo.setMobilePhone(asuib.getMobPhone());
  				if(asuib.getEmail() != null) {
  					tebuDo.setEmail(asuib.getEmail());
  				}
  				if(asuib.getFixPhone() != null) {
  					tebuDo.setFixPhone(asuib.getFixPhone());
  				}
  				if(asuib.getFax() != null) {
  					tebuDo.setFax(asuib.getFax());
  				}
  				if(userPicUrlRes != null) {
  					tebuDo.setUserPicUrl(userPicUrlRes);
  				}
  				tebuDo.setLastUpdateTime(BCDTimeUtil.convertNormalFrom(null));
  				//更新记录到数据源ds1的数据表t_edu_bd_user中
  				boolean flag = db2Service.UpdateBdUserInfo(tebuDo, token,false);
  				if(flag) {
  					AmUserInfoDTO auiDto = auiAppMod.appModFunc(token, db2Service);
  					if(auiDto !=  null) {
  						asuiDto = new AmSaveUserInfoDTO();
  						//时戳
  						asuiDto.setTime(BCDTimeUtil.convertNormalFrom(null));
  	  			    	//数据
  						AmSaveUserInfo amSaveUserInfo = new AmSaveUserInfo();
  	  			    	amSaveUserInfo.setUserName(auiDto.getAmUserInfo().getUserName());                                      //用户名
  	  			    	amSaveUserInfo.setOrgName(auiDto.getAmUserInfo().getOrgName());                                        //单位名称
  	  			    	amSaveUserInfo.setFullName(auiDto.getAmUserInfo().getFullName());                                      //姓名
  	  			    	amSaveUserInfo.setRoleName(auiDto.getAmUserInfo().getRoleName());                                      //角色名称
  	  			    	amSaveUserInfo.setMobPhone(auiDto.getAmUserInfo().getMobPhone());                                      //手机
  	  			    	amSaveUserInfo.setEmail(auiDto.getAmUserInfo().getEmail());                                            //电子邮箱
  	  			        amSaveUserInfo.setFixPhone(auiDto.getAmUserInfo().getFixPhone());                                      //固定电话
  	  			        amSaveUserInfo.setFax(auiDto.getAmUserInfo().getFax());                                                //传真
		    		  	if(auiDto.getAmUserInfo().getHpPicUrl() != null)
  	  			    		amSaveUserInfo.setHpPicUrl(auiDto.getAmUserInfo().getHpPicUrl());                                  //头像图片URL
  	  			    	//设置数据
  	  			    	asuiDto.setAmSaveUserInfo(amSaveUserInfo);
  	  					//消息ID
  	  			    	asuiDto.setMsgId(AppModConfig.msgId);
  	  					AppModConfig.msgId++;
  	  					// 消息id小于0判断
  	  					AppModConfig.msgIdLessThan0Judge();
  					}
  				}
  			}
  		}
  		else {
  			logger.info("访问接口参数非法！");
  		}
  		
  		return asuiDto;
  	}
}
