package com.tfit.BdBiProcSrvShEduOmc.appmod.ev;

import java.io.IOException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tfit.BdBiProcSrvShEduOmc.config.AppModConfig;
import com.tfit.BdBiProcSrvShEduOmc.dao.domain.edubd.TEduBdEtvidLibDo;
import com.tfit.BdBiProcSrvShEduOmc.dto.IOTHttpRspVO;
import com.tfit.BdBiProcSrvShEduOmc.dto.ev.EtVidLibDelDTO;
import com.tfit.BdBiProcSrvShEduOmc.service.Db2Service;

//教育培训视频删除应用模型
public class EtVidLibDelAppMod {
	private static final Logger logger = LogManager.getLogger(EtVidLibDelAppMod.class.getName());
	
	@Autowired
	ObjectMapper objectMapper = new ObjectMapper();
	
	//教育培训视频删除应用模型函数
  	public IOTHttpRspVO appModFunc(String strBodyCont, Db2Service db2Service, int[] codes) {
  		IOTHttpRspVO normResp = null;
		//按参数形式处理
  		if(strBodyCont != null && db2Service != null) {
  			EtVidLibDelDTO evldDto = null;
  			try {
  				if(strBodyCont != null)
  					evldDto = objectMapper.readValue(strBodyCont, EtVidLibDelDTO.class);
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
  			if(evldDto != null && evldDto.getVidId() != null) {
  				//获取教育视频以记录ID
  			    TEduBdEtvidLibDo tebelDo = db2Service.getTEduBdEtvidLibDoById(evldDto.getVidId());
  				if(tebelDo != null) {
  					//删除教育视频记录以记录ID
  					db2Service.deleteTEduBdEtvidLibDoById(evldDto.getVidId());
  					normResp = AppModConfig.getNormalResp(null);
  					logger.info("教育培训视频删除成功！");
  				}
  			}
  			else {
  				logger.info("Json格式数据解析失败！");
  				codes[0] = 2011;
  			}  			
  	   }	
  	   else {
  			logger.info("访问接口参数非法！");
  			codes[0] = 2017;
  		}
		
		return normResp;
  	}
}
