package com.tfit.BdBiProcSrvShEduOmc.appmod.user;

import java.io.IOException;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tfit.BdBiProcSrvShEduOmc.config.AppModConfig;
import com.tfit.BdBiProcSrvShEduOmc.dao.domain.TEduBdRoleDo;
import com.tfit.BdBiProcSrvShEduOmc.dto.IOTHttpRspVO;
import com.tfit.BdBiProcSrvShEduOmc.dto.user.AmEditRoleDTO;
import com.tfit.BdBiProcSrvShEduOmc.service.Db2Service;
import com.tfit.BdBiProcSrvShEduOmc.util.BCDTimeUtil;

//编辑角色应用模型
public class AmEditRoleAppMod {
	private static final Logger logger = LogManager.getLogger(AmEditRoleAppMod.class.getName());
	
	@Autowired
	ObjectMapper objectMapper = new ObjectMapper();
	
	//编辑账号应用模型函数
  	public IOTHttpRspVO appModFunc( String token, String strBodyCont, Db2Service db2Service, int[] codes) {
  		IOTHttpRspVO normResp = null;
  		//按参数形式处理
  		if(strBodyCont != null && db2Service != null) {
  			AmEditRoleDTO aerDto = null;
  			String roleId = null;
  			//将json子串转成对象
  			try {
				aerDto = objectMapper.readValue(strBodyCont, AmEditRoleDTO.class);
				if(aerDto != null)
					roleId = aerDto.getRoleId();
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
  			if(aerDto != null && roleId != null) {
  	    	    //判断角色名是否已存在
  				TEduBdRoleDo tebrDo = db2Service.getBdRoleInfoByRoleId(roleId);
  	    	    if(tebrDo.getRoleName() != null && aerDto.getRoleName() != null) {    //存在该角色，则可以更新编辑
  	    	    	//从数据源ds2的数据表t_edu_bd_role中查找所有角色名称
  	  				int roleNameRepeatCount = 0;
  	  			    List<TEduBdRoleDo> tebrDoList = db2Service.getBdRoleInfoByRoleName4(tebrDo.getRoleType(), aerDto.getRoleName());
  	  			    if(tebrDoList != null) {
  	  			    	for(int i = 0; i < tebrDoList.size(); i++) {
  	  			    		if(tebrDoList.get(i).getRoleName().equals(aerDto.getRoleName()) && !tebrDoList.get(i).getId().equals(roleId)) {
  	  			    			roleNameRepeatCount++;
  	  			    		}
  	  			    	}
  	  			    }
  	  			    if(roleNameRepeatCount < 1) {
  	  			    	//角色类型，1:监管部门，2:学校
  	  			    	tebrDo.setRoleType(aerDto.getRoleType());
  	  			    	//角色名称
  	  			    	tebrDo.setRoleName(aerDto.getRoleName());
  	  			    	//角色描述
  	  			    	tebrDo.setDiscrip(aerDto.getRemark());
  	  			    	//最后更新时间
  	  			    	tebrDo.setLastUpdateTime(BCDTimeUtil.convertNormalFrom(null));
  	  			    	//更新角色信息到数据库 
  	  			    	boolean updateRoleFlag = db2Service.UpdateBdRoleInfoByField(tebrDo, "id", roleId);    //更新记录到数据源ds1的数据表t_edu_bd_role中 
  	  			    	if(updateRoleFlag) {
  	  			    		normResp = AppModConfig.getNormalResp(null);
  	  			    		logger.info("角色：" + tebrDo.getRoleName() + "，更新成功！");  	    
  	  			    	}
  	  			    	else {
  	  			    		codes[0] = 2025;
  	  			    		logger.info("数据库操作异常");
  	  			    	}
  	  			    }
  	  			    else {
  	  			    	codes[0] = 2027;
  	  			    	logger.info("角色：" + aerDto.getRoleName() + "已存在" + "，更新失败！");
  	  			    }
  	    	    }
  	    	    else {
  	    	    	codes[0] = 2029;
  	    	    	logger.info("角色：" + aerDto.getRoleName() + "不存在" + "，更新失败！");
  	    	    }
  			}
  			else {
  				codes[0] = 2031;
  				logger.info("访问接口参数格式错误！");
  			}
  		}
  		else {
  			codes[0] = 2017;
  			logger.info("访问接口参数非法！");
  		}
  		
  		return normResp;
  	}
}
