package com.tfit.BdBiProcSrvShEduOmc.appmod.user;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.tfit.BdBiProcSrvShEduOmc.config.AppModConfig;
import com.tfit.BdBiProcSrvShEduOmc.config.SpringConfig;
import com.tfit.BdBiProcSrvShEduOmc.dao.domain.TEduBdRoleDo;
import com.tfit.BdBiProcSrvShEduOmc.dao.domain.TEduBdUserDo;
import com.tfit.BdBiProcSrvShEduOmc.dto.user.AmUserInfo;
import com.tfit.BdBiProcSrvShEduOmc.dto.user.AmUserInfoDTO;
import com.tfit.BdBiProcSrvShEduOmc.service.Db2Service;
import com.tfit.BdBiProcSrvShEduOmc.util.BCDTimeUtil;

//个人资料应用模型
public class AmUserInfoAppMod {
	private static final Logger logger = LogManager.getLogger(AmUserInfoAppMod.class.getName());
	
	//个人资料应用模型函数
  	public AmUserInfoDTO appModFunc(String token, Db2Service db2Service) {
  		AmUserInfoDTO auiDto = null;
  		//按参数形式处理
  		if(token != null && db2Service != null) {
  			//从数据源ds1的数据表t_edu_bd_user中查找用户信息以授权码token
  			TEduBdUserDo tebuDo = db2Service.getBdUserInfoByToken(token);
  			if(tebuDo.getId() != null) {
  				//从数据源ds1的数据表t_edu_bd_role中查找角色信息以id
  			    TEduBdRoleDo tebrDo = db2Service.getBdRoleInfoByRoleId(tebuDo.getRoleId());
  			    if(tebrDo != null) {
  			    	auiDto = new AmUserInfoDTO();
  			    	//时戳
  			    	auiDto.setTime(BCDTimeUtil.convertNormalFrom(null));
  			    	//数据
  			    	AmUserInfo amUserInfo = new AmUserInfo();
  			    	amUserInfo.setUserName(tebuDo.getUserAccount());                                            //用户名
  			    	amUserInfo.setOrgName(tebuDo.getOrgName());                                                 //单位名称
  			    	amUserInfo.setFullName(tebuDo.getName());                                                   //姓名
  			    	amUserInfo.setRoleName(tebrDo.getRoleName());                                               //角色名称
  			    	amUserInfo.setMobPhone(tebuDo.getMobilePhone());                                            //手机
  			    	amUserInfo.setEmail(tebuDo.getEmail());                                                     //电子邮箱
  			    	amUserInfo.setFixPhone(tebuDo.getFixPhone());                                               //固定电话
  			    	amUserInfo.setFax(tebuDo.getFax());                                                         //传真
  			    	if(tebuDo.getUserPicUrl() != null)
  			    		amUserInfo.setHpPicUrl(SpringConfig.video_srvdn + tebuDo.getUserPicUrl());              //头像图片URL
  			    	//设置数据
  			    	auiDto.setAmUserInfo(amUserInfo);
  					//消息ID
  			    	auiDto.setMsgId(AppModConfig.msgId);
  					AppModConfig.msgId++;
  					// 消息id小于0判断
  					AppModConfig.msgIdLessThan0Judge();
  			    }
  			}
  		}
  		else {
  			logger.info("访问接口参数非法！");
  		}
  		
  		return auiDto;
  	}
}
