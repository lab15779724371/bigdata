package com.tfit.BdBiProcSrvShEduOmc.appmod.cp;

import java.io.IOException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tfit.BdBiProcSrvShEduOmc.config.AppModConfig;
import com.tfit.BdBiProcSrvShEduOmc.dao.domain.edubd.TEduBdComplaintDo;
import com.tfit.BdBiProcSrvShEduOmc.dto.cp.CrOperation;
import com.tfit.BdBiProcSrvShEduOmc.dto.cp.CrOperationBody;
import com.tfit.BdBiProcSrvShEduOmc.dto.cp.CrOperationDTO;
import com.tfit.BdBiProcSrvShEduOmc.service.Db1Service;
import com.tfit.BdBiProcSrvShEduOmc.service.Db2Service;
import com.tfit.BdBiProcSrvShEduOmc.util.BCDTimeUtil;

//投诉举报操作应用模型
public class CrOperationAppMod {
	private static final Logger logger = LogManager.getLogger(CrOperationAppMod.class.getName());
	
	@Autowired
	ObjectMapper objectMapper = new ObjectMapper();	
	
	//投诉举报操作应用模型函数
  	public CrOperationDTO appModFunc(String token, String strBodyCont, Db1Service db1Service, Db2Service db2Service) {
  		CrOperationDTO copDto = null;
  		//按参数形式处理
  		if(token != null && db2Service != null) {
  			CrOperationBody copb = null;
  			try {
  				if(strBodyCont != null)
  					copb = objectMapper.readValue(strBodyCont, CrOperationBody.class);
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
  			if(copb != null && copb.getCrId() != null && copb.getCpStatus() != null) {
  				copDto = new CrOperationDTO();
  				TEduBdComplaintDo tebcpDo = new TEduBdComplaintDo();
  				//投诉举报id
  				tebcpDo.setId(copb.getCrId());
  				//状态，0:待处理，1:已办结
  				tebcpDo.setCpStatus(copb.getCpStatus());
  				//承办人名称
  				tebcpDo.setContractor(copb.getContractor());
  				//办结反馈
  				tebcpDo.setFeedBack(copb.getProcFeedBack());
  				//办结日期，格式：xxxx-xx-xx
  				tebcpDo.setFinishDate(copb.getHandleDate());
  				//最后更新时间
  				tebcpDo.setLastUpdateTime(BCDTimeUtil.convertNormalFrom(null));				
  				//更新投诉举报操作到数据库
  			    db2Service.updateTEduBdComplaintDo(tebcpDo);
  			    //时戳
  			    copDto.setTime(BCDTimeUtil.convertNormalFrom(null));  			    
  			    //获取投诉举报以记录ID
  			    TEduBdComplaintDo tebcpDo2 = db2Service.getTEduBdComplaintDoById(tebcpDo.getId());
  			    //数据
  			    CrOperation crOperation = null; 
  			    if(tebcpDo2 != null) {
  			    	crOperation = new CrOperation();
  			    	crOperation.setCrId(tebcpDo2.getId());
  	  			    crOperation.setCpStatus(tebcpDo2.getCpStatus());		
  	  			    crOperation.setContractor(tebcpDo2.getContractor());
  	  			    crOperation.setProcFeedBack(tebcpDo2.getFeedBack());
  	  			    crOperation.setHandleDate(tebcpDo2.getFinishDate());
  			    }
		    	//设置数据
		    	copDto.setCrOperation(crOperation);
				//消息ID
		    	copDto.setMsgId(AppModConfig.msgId);
				AppModConfig.msgId++;
				// 消息id小于0判断
				AppModConfig.msgIdLessThan0Judge();
  			}
  		}
  		else {
  			logger.info("访问接口参数非法！");
  		}
  		
  		return copDto;
  	}
}