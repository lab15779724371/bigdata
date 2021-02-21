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
import com.tfit.BdBiProcSrvShEduOmc.dao.domain.edubd.TEduBdEtvidLibDo;
import com.tfit.BdBiProcSrvShEduOmc.dto.ev.EtVidLibEdit;
import com.tfit.BdBiProcSrvShEduOmc.dto.ev.EtVidLibEditBody;
import com.tfit.BdBiProcSrvShEduOmc.dto.ev.EtVidLibEditDTO;
import com.tfit.BdBiProcSrvShEduOmc.service.Db2Service;
import com.tfit.BdBiProcSrvShEduOmc.util.BCDTimeUtil;

//教育培训视频编辑应用模型
public class EtVidLibEditAppMod {
	private static final Logger logger = LogManager.getLogger(EtVidLibEditAppMod.class.getName());
	
	@Autowired
	ObjectMapper objectMapper = new ObjectMapper();	
	
	//教育培训视频编辑应用模型函数
  	public EtVidLibEditDTO appModFunc(String token, String strBodyCont, Db2Service db2Service) {
  		EtVidLibEditDTO evleDto = null;
  		//按参数形式处理
  		if(token != null && db2Service != null) {
  			EtVidLibEditBody evleb = null;
  			try {
  				if(strBodyCont != null)
  					evleb = objectMapper.readValue(strBodyCont, EtVidLibEditBody.class);
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
  			if(evleb != null && evleb.getVidId() != null) {
  				evleDto = new EtVidLibEditDTO();
  				TEduBdEtvidLibDo tebelDo = new TEduBdEtvidLibDo();
  				//视频ID
  				tebelDo.setId(evleb.getVidId());
  				//视频名称
  				if(evleb.getVidName() !=  null)
  					tebelDo.setVidName(evleb.getVidName());
  				//副标题	
  				if(evleb.getSubTitle() != null)
  					tebelDo.setSubTitle(evleb.getSubTitle());
  				//视频分类，0:系统操作，1:食品安全，2:政策法规	
  				if(evleb.getVidCategory() != null)
  					tebelDo.setVidCategory(evleb.getVidCategory());
  				//缩略图图片URL
  				if(evleb.getThumbUrl() != null) {
  					String curThumbUrl = evleb.getThumbUrl().replaceAll(SpringConfig.video_srvdn, "");
  					tebelDo.setThumbUrl(curThumbUrl);
  				}
  				//视频URL
  				if(evleb.getVidUrl() != null) {
  					String curVidUrl = evleb.getVidUrl().replaceAll(SpringConfig.video_srvdn, "");
  					tebelDo.setVidUrl(curVidUrl);
  				}
  				//视频描述内容	
  				if(evleb.getVidDescrCont() != null)
  					tebelDo.setVidDescrCont(evleb.getVidDescrCont().getBytes());
  				//最后更新时间
  				tebelDo.setLastUpdateTime(BCDTimeUtil.convertNormalFrom(null));  				
  				//更新教育培训视频编辑到数据库
  			    db2Service.updateTEduBdEtvidLibDo(tebelDo);
  			    //时戳
  			    evleDto.setTime(BCDTimeUtil.convertNormalFrom(null));
  			    //获取教育视频以记录ID
  			    TEduBdEtvidLibDo tebelDo2 = db2Service.getTEduBdEtvidLibDoById(tebelDo.getId());
  			    //数据
  			    EtVidLibEdit etVidLibEdit = null; 
  			    if(tebelDo2 != null) {
  			    	etVidLibEdit = new EtVidLibEdit();
  	  			    etVidLibEdit.setVidId(tebelDo2.getId());
  	  			    etVidLibEdit.setCreateTime(tebelDo2.getCreateTime());
  	  			    etVidLibEdit.setVidName(tebelDo2.getVidName());
  	  			    etVidLibEdit.setSubTitle(tebelDo2.getSubTitle());
  	  			    etVidLibEdit.setVidCategory(tebelDo2.getVidCategory());
  	  			    etVidLibEdit.setThumbUrl(SpringConfig.video_srvdn + tebelDo2.getThumbUrl());
  	  			    etVidLibEdit.setVidUrl(SpringConfig.video_srvdn + tebelDo2.getVidUrl());
  	  			    etVidLibEdit.setVidDescrCont(new String(tebelDo2.getVidDescrCont(), 0, tebelDo2.getVidDescrCont().length));
  	  			    etVidLibEdit.setLastUpdateTime(tebelDo2.getLastUpdateTime());
  			    }
		    	//设置数据
		    	evleDto.setEtVidLibEdit(etVidLibEdit);
				//消息ID
		    	evleDto.setMsgId(AppModConfig.msgId);
				AppModConfig.msgId++;
				// 消息id小于0判断
				AppModConfig.msgIdLessThan0Judge();
  			}
  		}
  		else {
  			logger.info("访问接口参数非法！");
  		}
  		
  		return evleDto;
  	}
}
