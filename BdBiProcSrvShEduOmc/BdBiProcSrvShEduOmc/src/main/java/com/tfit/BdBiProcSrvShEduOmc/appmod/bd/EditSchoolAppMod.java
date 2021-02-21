package com.tfit.BdBiProcSrvShEduOmc.appmod.bd;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tfit.BdBiProcSrvShEduOmc.config.AppModConfig;
import com.tfit.BdBiProcSrvShEduOmc.dao.domain.TEduBdUserPermDo;
import com.tfit.BdBiProcSrvShEduOmc.dto.IOTHttpRspVO;
import com.tfit.BdBiProcSrvShEduOmc.dto.bd.BdSchList;
import com.tfit.BdBiProcSrvShEduOmc.service.Db1Service;
import com.tfit.BdBiProcSrvShEduOmc.service.Db2Service;

//编辑账号应用模型
public class EditSchoolAppMod {
	private static final Logger logger = LogManager.getLogger(EditSchoolAppMod.class.getName());
	
	@Autowired
	ObjectMapper objectMapper = new ObjectMapper();
	
	//编辑账号应用模型函数
  	public IOTHttpRspVO appModFunc( String token, BdSchList bdSchList, Db1Service db1Service, Db2Service db2Service, int[] codes) {
  		IOTHttpRspVO normResp = null;
  		//按参数形式处理
  		if(bdSchList != null && db2Service != null) {
  			String userName = null;
	    	//更新角色信息到数据库 
	    	boolean updateRoleFlag = false;
	    	if(bdSchList.getId() != null)
	    		updateRoleFlag = db1Service.updateSchoolDepartMent(bdSchList.getId(), bdSchList.getDepartmentId());    //更新记录到数据源ds1的数据表t_edu_bd_role中 
	    	if(updateRoleFlag) {
	    		normResp = AppModConfig.getNormalResp(null);
	    		logger.info("学校：" + userName + "，更新成功！");
	    	}
	    	
	    	//删除原有权限
	    	TEduBdUserPermDo tebrpDo = new TEduBdUserPermDo();
	    	tebrpDo.setStat(0);
  		}
  		else {
  			logger.info("访问接口参数非法！");
  		}
  		
  		return normResp;
  	}
}
