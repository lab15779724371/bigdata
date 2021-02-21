package com.tfit.BdBiProcSrvShEduOmc.appmod.cp;

import java.io.IOException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tfit.BdBiProcSrvShEduOmc.config.AppModConfig;
import com.tfit.BdBiProcSrvShEduOmc.dao.domain.TEduBdUserDo;
import com.tfit.BdBiProcSrvShEduOmc.dao.domain.TEduSchoolDo;
import com.tfit.BdBiProcSrvShEduOmc.dao.domain.edubd.TEduBdComplaintDo;
import com.tfit.BdBiProcSrvShEduOmc.dto.cp.CrPublish;
import com.tfit.BdBiProcSrvShEduOmc.dto.cp.CrPublishBody;
import com.tfit.BdBiProcSrvShEduOmc.dto.cp.CrPublishDTO;
import com.tfit.BdBiProcSrvShEduOmc.service.Db1Service;
import com.tfit.BdBiProcSrvShEduOmc.service.Db2Service;
import com.tfit.BdBiProcSrvShEduOmc.util.BCDTimeUtil;
import com.tfit.BdBiProcSrvShEduOmc.util.UniqueIdGen;

//投诉举报发布应用模型
public class CrPublishAppMod {
	private static final Logger logger = LogManager.getLogger(CrPublishAppMod.class.getName());
	
	@Autowired
	ObjectMapper objectMapper = new ObjectMapper();	
	
	//投诉举报发布应用模型函数
  	public CrPublishDTO appModFunc(String token, String strBodyCont, Db1Service db1Service, Db2Service db2Service) {
  		CrPublishDTO cplpDto = null;
  		//按参数形式处理
  		if(token != null && db2Service != null) {
  			CrPublishBody cplpb = null;
  			try {
  				if(strBodyCont != null)
  					cplpb = objectMapper.readValue(strBodyCont, CrPublishBody.class);
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
  			if(cplpb != null) {
  				cplpDto = new CrPublishDTO();
  				TEduBdComplaintDo tebcpDo = new TEduBdComplaintDo();
  				//从数据源ds2的数据表t_edu_bd_user中查找授权码以当前授权码
  	  			TEduBdUserDo tebuDo = db2Service.getBdUserInfoByCurAuthCode(token);
  				//投诉举报id
  				tebcpDo.setId(UniqueIdGen.uuidInterSeg());
  				//创建时间，格式：xxxx-xx-xx xx:xx:xx
  				tebcpDo.setCreateTime(BCDTimeUtil.convertNormalFrom(null));
  				//提交日期	
  				tebcpDo.setSubDate(cplpb.getSubDate());
  				//学校ID
  				tebcpDo.setSchoolId(cplpb.getPpName());
  				//投诉主题
  				tebcpDo.setTitle(cplpb.getTitle());
  				//投诉举报内容
  				tebcpDo.setContent(cplpb.getCpCont().getBytes());
  				//投诉人姓名
  				tebcpDo.setCptName(cplpb.getCptName());
  				//联系电话
  				tebcpDo.setContactNo(cplpb.getContactNo());
  				//承办人
  				tebcpDo.setContractor(cplpb.getContractor());
  				//状态，0:待处理，1:已办结
  				tebcpDo.setCpStatus(0);
  				//办结反馈
  				tebcpDo.setFeedBack(null);
  				//办结日期，格式：xxxx-xx-xx xx:xx:xx
  				tebcpDo.setFinishDate(null);
  				//用户名	
  				tebcpDo.setUserName(tebuDo.getUserAccount());
  				//最后更新时间
  				tebcpDo.setLastUpdateTime(BCDTimeUtil.convertNormalFrom(null));  				
  				//记录有效性
  				tebcpDo.setStat(1);  				
  				//保存投诉举报发布到数据库
  			    db2Service.insertTEduBdComplaintDo(tebcpDo);  			   
  			    //时戳
  			    cplpDto.setTime(BCDTimeUtil.convertNormalFrom(null));  			    
  			    //获取投诉举报以记录ID
  			    TEduBdComplaintDo tebcpDo2 = db2Service.getTEduBdComplaintDoById(tebcpDo.getId());
  			    //数据
  			    CrPublish crPublish = null; 
  			    if(tebcpDo2 != null) {
  			    	crPublish = new CrPublish();
  			    	crPublish.setCrId(tebcpDo2.getId());
  	  			    crPublish.setSubDate(tebcpDo2.getSubDate());
  	  			    //从数据源ds1的数据表t_edu_school中查找学校信息以学校id
  	  			    TEduSchoolDo tesDo = db1Service.getTEduSchoolDoBySchId(tebcpDo2.getSchoolId(), 3);
  	  			    crPublish.setPpName(tesDo.getSchoolName());
  	  			    crPublish.setTitle(tebcpDo2.getTitle());
  	  			    crPublish.setCpCont(new String(tebcpDo2.getContent(), 0, tebcpDo2.getContent().length));
  	  			    crPublish.setCptName(tebcpDo2.getCptName());
  	  			    crPublish.setContactNo(tebcpDo2.getContactNo());
  	  			    crPublish.setContractor(tebcpDo2.getContractor());
  	  			    crPublish.setCpStatus(tebcpDo2.getCpStatus());
  	  			    crPublish.setFinalFeedBack(tebcpDo2.getFeedBack());
  	  			    crPublish.setFinishDate(tebcpDo2.getFinishDate());  	  			    
  			    }
		    	//设置数据
		    	cplpDto.setCrPublish(crPublish);
				//消息ID
		    	cplpDto.setMsgId(AppModConfig.msgId);
				AppModConfig.msgId++;
				// 消息id小于0判断
				AppModConfig.msgIdLessThan0Judge();
  			}
  		}
  		else {
  			logger.info("访问接口参数非法！");
  		}
  		
  		return cplpDto;
  	}
}