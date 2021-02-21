package com.tfit.BdBiProcSrvShEduOmc.appmod.user;

import java.io.IOException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tfit.BdBiProcSrvShEduOmc.config.AppModConfig;
import com.tfit.BdBiProcSrvShEduOmc.dao.domain.TEduBdRoleDo;
import com.tfit.BdBiProcSrvShEduOmc.dao.domain.TEduBdUserDo;
import com.tfit.BdBiProcSrvShEduOmc.dto.IOTHttpRspVO;
import com.tfit.BdBiProcSrvShEduOmc.dto.user.AmAddRoleDTO;
import com.tfit.BdBiProcSrvShEduOmc.service.Db2Service;
import com.tfit.BdBiProcSrvShEduOmc.util.BCDTimeUtil;
import com.tfit.BdBiProcSrvShEduOmc.util.UniqueIdGen;

//添加角色应用模型
public class AmAddRoleAppMod {
	private static final Logger logger = LogManager.getLogger(AmAddRoleAppMod.class.getName());
	
	@Autowired
	ObjectMapper objectMapper = new ObjectMapper();
	
	//添加账号应用模型函数
  	public IOTHttpRspVO appModFunc(String token, String strBodyCont, Db2Service db2Service, int[] codes) {
  		IOTHttpRspVO normResp = null;
  		//按参数形式处理
  		if(token != null && strBodyCont != null && db2Service != null) {
  			AmAddRoleDTO aarDto = null;
  			Integer roleType = null;
  			String roleName = null;
  			//将json子串转成对象
  			try {
				aarDto = objectMapper.readValue(strBodyCont, AmAddRoleDTO.class);
				if(aarDto != null) {
					roleName = aarDto.getRoleName();
					roleType = aarDto.getRoleType();
				}
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
  			if(aarDto != null && roleType != null && roleName != null) {
  	    	    //判断角色名是否已存在
  				TEduBdRoleDo tebrDo = db2Service.getBdRoleInfoByRoleName3(roleType, roleName);
  	    	    if(tebrDo.getId() == null && tebrDo.getRoleName() == null) {    //不存在该角色，则可以添加
  	    	    	//获取当前账户信息以token
  	    	    	TEduBdUserDo curTebuDo = db2Service.getBdUserInfoByToken(token);
  	    	    	//角色信息
  	    	    	tebrDo.setId(UniqueIdGen.uuidInterSeg());
  	    	    	//角色类型，1:监管部门，2:学校
  	    	    	tebrDo.setRoleType(aarDto.getRoleType());
  	    	    	//角色名称
  	    	    	tebrDo.setRoleName(aarDto.getRoleName());   
  	    	    	//创建时间
  	    	    	tebrDo.setCreateTime(BCDTimeUtil.convertNormalFrom(null));
  	    	    	//创建人
  	    	    	tebrDo.setCreator(curTebuDo.getUserAccount());
  	    	    	//最后更新时间
  	    	    	tebrDo.setLastUpdateTime(BCDTimeUtil.convertNormalFrom(null));
  	    	    	//创建者
  	    	    	if(curTebuDo != null) {
  	    	    		tebrDo.setCreator(curTebuDo.getUserAccount());
  	    	    	}
  	    	    	//描述
  	    	    	tebrDo.setDiscrip(aarDto.getRemark());
  	    	    	//0:无效，1:有效
  	    	    	tebrDo.setStat(1);
  	    	    	//保存角色信息到数据库 
  	    	    	boolean saveRoleFlag = db2Service.InsertBdRoleInfo(tebrDo);    //插入记录到数据源ds1的数据表t_edu_bd_role中 
  	    	    	if(saveRoleFlag) {
  	    	    		normResp = AppModConfig.getNormalResp(null);
  	    	    		logger.info("角色：" + roleName + "，添加成功！");  	    	    		
  	    	    	}
  	    	    }
  	    	    else {
  	    	    	codes[0] = 2027;
  	    	    	logger.info("角色：" + roleName + "已存在" + "，添加失败！");
  	    	    }
  			}
  			else
  				logger.info("访问接口参数格式错误！");
  		}
  		else {
  			logger.info("访问接口参数非法！");
  		}
  		
  		return normResp;
  	}
}
