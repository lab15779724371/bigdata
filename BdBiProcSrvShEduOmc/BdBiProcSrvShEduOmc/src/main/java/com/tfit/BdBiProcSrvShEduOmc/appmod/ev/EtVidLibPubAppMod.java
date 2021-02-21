package com.tfit.BdBiProcSrvShEduOmc.appmod.ev;

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
import com.tfit.BdBiProcSrvShEduOmc.dao.domain.edubd.TEduBdEtvidLibDo;
import com.tfit.BdBiProcSrvShEduOmc.dto.ev.EtVidLibPub;
import com.tfit.BdBiProcSrvShEduOmc.dto.ev.EtVidLibPubBody;
import com.tfit.BdBiProcSrvShEduOmc.dto.ev.EtVidLibPubDTO;
import com.tfit.BdBiProcSrvShEduOmc.service.Db2Service;
import com.tfit.BdBiProcSrvShEduOmc.util.BCDTimeUtil;
import com.tfit.BdBiProcSrvShEduOmc.util.UniqueIdGen;

//教育培训视频发布应用模型
public class EtVidLibPubAppMod {
	private static final Logger logger = LogManager.getLogger(EtVidLibPubAppMod.class.getName());
	
	@Autowired
	ObjectMapper objectMapper = new ObjectMapper();	
	
	//教育培训视频发布应用模型函数
  	public EtVidLibPubDTO appModFunc(String token, String strBodyCont, Db2Service db2Service) {
  		EtVidLibPubDTO evlpDto = null;
  		//按参数形式处理
  		if(token != null && db2Service != null) {
  			EtVidLibPubBody evlpb = null;
  			try {
  				if(strBodyCont != null)
  					evlpb = objectMapper.readValue(strBodyCont, EtVidLibPubBody.class);
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
  			if(evlpb != null) {
  				evlpDto = new EtVidLibPubDTO();
  				TEduBdEtvidLibDo tebelDo = new TEduBdEtvidLibDo();
  				//从数据源ds2的数据表t_edu_bd_user中查找用户信息以授权码token
  			    TEduBdUserDo tebuDo = db2Service.getBdUserInfoByToken(token);
  				//视频id
  				tebelDo.setId(UniqueIdGen.uuidInterSeg());
  				//创建 时间
  				tebelDo.setCreateTime(BCDTimeUtil.convertNormalFrom(null));
  				//视频名称
  				tebelDo.setVidName(evlpb.getVidName());
  				//副标题	
  				tebelDo.setSubTitle(evlpb.getSubTitle());
  				//视频分类，0:系统操作，1:食品安全，2:政策法规	
  				tebelDo.setVidCategory(evlpb.getVidCategory());
  				//缩略图图片URL
  				if(evlpb.getThumbUrl() != null) {
  					String curThumbUrl = evlpb.getThumbUrl().replaceAll(SpringConfig.video_srvdn, "");
  					tebelDo.setThumbUrl(curThumbUrl);
  				}
  				//视频URL
  				if(evlpb.getVidUrl() != null) {
  					String curVidUrl = evlpb.getVidUrl().replaceAll(SpringConfig.video_srvdn, "");
  					tebelDo.setVidUrl(curVidUrl);
  				}
  				//视频描述内容	
  				tebelDo.setVidDescrCont(evlpb.getVidDescrCont().getBytes());
  				//用户名
  				tebelDo.setUserName(tebuDo.getUserAccount());
  				//审核状态
  				tebelDo.setAuditStatus(0);
  				//最后更新时间
  				tebelDo.setLastUpdateTime(BCDTimeUtil.convertNormalFrom(null));  				
  				//记录有效性
  				tebelDo.setStat(1);
  				//保存教育培训视频发布到数据库
  			    db2Service.insertTEduBdEtvidLibDo(tebelDo);  			   
  			    //时戳
  			    evlpDto.setTime(BCDTimeUtil.convertNormalFrom(null));
  			    //获取教育视频以记录ID
  			    TEduBdEtvidLibDo tebelDo2 = db2Service.getTEduBdEtvidLibDoById(tebelDo.getId());
  			    //数据
  			    EtVidLibPub etVidLibPub = null; 
  			    if(tebelDo2 != null) {
  			    	etVidLibPub = new EtVidLibPub();
  	  			    etVidLibPub.setVidId(tebelDo2.getId());
  	  			    etVidLibPub.setVidName(tebelDo2.getVidName());
  	  			    etVidLibPub.setSubtitle(tebelDo2.getSubTitle());
  	  			    etVidLibPub.setVidCategory(tebelDo2.getVidCategory());
  	  			    etVidLibPub.setThumbUrl(SpringConfig.video_srvdn + tebelDo2.getThumbUrl());
  	  			    etVidLibPub.setVidUrl(SpringConfig.video_srvdn + tebelDo2.getVidUrl());
  	  			    etVidLibPub.setVidDescrCont(new String(tebelDo2.getVidDescrCont(), 0, tebelDo2.getVidDescrCont().length));
  			    }
		    	//设置数据
		    	evlpDto.setEtVidLibPub(etVidLibPub);
				//消息ID
		    	evlpDto.setMsgId(AppModConfig.msgId);
				AppModConfig.msgId++;
				// 消息id小于0判断
				AppModConfig.msgIdLessThan0Judge();
  			}
  		}
  		else {
  			logger.info("访问接口参数非法！");
  		}
  		
  		return evlpDto;
  	}
}