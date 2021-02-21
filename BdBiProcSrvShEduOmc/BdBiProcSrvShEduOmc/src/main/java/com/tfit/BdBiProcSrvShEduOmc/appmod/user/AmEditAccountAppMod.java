package com.tfit.BdBiProcSrvShEduOmc.appmod.user;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tfit.BdBiProcSrvShEduOmc.config.AppModConfig;
import com.tfit.BdBiProcSrvShEduOmc.dao.domain.TEduBdRoleDo;
import com.tfit.BdBiProcSrvShEduOmc.dao.domain.TEduBdUserDo;
import com.tfit.BdBiProcSrvShEduOmc.dao.domain.TEduBdUserPermDo;
import com.tfit.BdBiProcSrvShEduOmc.dao.domain.TEduSchoolDo;
import com.tfit.BdBiProcSrvShEduOmc.dto.IOTHttpRspVO;
import com.tfit.BdBiProcSrvShEduOmc.dto.user.AmEditAccountDTO;
import com.tfit.BdBiProcSrvShEduOmc.service.Db1Service;
import com.tfit.BdBiProcSrvShEduOmc.service.Db2Service;
import com.tfit.BdBiProcSrvShEduOmc.util.BCDTimeUtil;

//编辑账号应用模型
public class AmEditAccountAppMod {
	private static final Logger logger = LogManager.getLogger(AmEditAccountAppMod.class.getName());
	
	@Autowired
	ObjectMapper objectMapper = new ObjectMapper();
	
	//编辑账号应用模型函数
  	public IOTHttpRspVO appModFunc( String token, String strBodyCont, Db1Service db1Service, Db2Service db2Service, int[] codes) {
  		IOTHttpRspVO normResp = null;
  		//按参数形式处理
  		if(strBodyCont != null && db2Service != null) {
  			
  			AmEditAccountDTO aeaDto = null;
  			String userName = null;
  			List<TEduBdUserPermDo> tebrpDoList = null;
  			//将json子串转成对象
  			try {
				aeaDto = objectMapper.readValue(strBodyCont, AmEditAccountDTO.class);
				if(aeaDto != null)
					userName = aeaDto.getUserName();
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
  			
  			if(aeaDto != null && userName != null) {
  	    	    //判断用户名是否已存在
  				TEduBdUserDo tebuDo = db2Service.getBdUserInfoByUserName(userName);
  	    	    if(tebuDo.getUserAccount() != null) {    //存在该用户，则可以更新编辑
  	    	    	//用户信息
  	    	    	TEduBdUserDo editTebuDo = new TEduBdUserDo();
  	    	    	//主键id
  	    	    	editTebuDo.setId(tebuDo.getId());
  	    	    	//是否为管理员，0:否，1:是
  	    	    	editTebuDo.setIsAdmin(aeaDto.getIsAdmin());          
  	    	    	//密码
  	    	    	editTebuDo.setPassword(null);  //aeaDto.getPassword());
  	    	    	//姓名
  	    	    	editTebuDo.setName(aeaDto.getFullName());    
  	    	    	//固定电话
  	    	    	editTebuDo.setFixPhone(aeaDto.getFixPhone());
  	    	    	//手机号码
  	    	    	editTebuDo.setMobilePhone(aeaDto.getMobPhone());
  	    	    	//邮箱
  	    	    	editTebuDo.setEmail(aeaDto.getEmail());
  	    	    	//更新时间
  	    	    	editTebuDo.setLastUpdateTime(BCDTimeUtil.convertNormalFrom(null));
  	    	    	//用户单位ID
  	    	    	if(aeaDto.getRoleType() != null) {
  	    	    		if(aeaDto.getRoleType().intValue() == 1) {      //监管部门
  	    	    			tebuDo.setOrgId(aeaDto.getUserOrgId());
  	    	    			tebuDo.setOrgName(aeaDto.getUserOrgId());
  	    	    		}
  	    	    		else if(aeaDto.getRoleType().intValue() == 2) {   //学校
  	    	    			//所有学校id
  	    	    			List<TEduSchoolDo> tesDoList = db1Service.getTEduSchoolDoListByDs1(null,1,1, 1);
  	    	    			if(tesDoList != null) {
  	    	    				for (int i = 0; i < tesDoList.size(); i++) {
  	    	    					if(aeaDto.getUserOrgId().equals(tesDoList.get(i).getId())) {
  	    	    						tebuDo.setOrgId(aeaDto.getUserOrgId());
  	    	  	    	    			tebuDo.setOrgName(tesDoList.get(i).getSchoolName());
  	    	    						break;
  	    	    					}
  	    	    				}		
  	    	  				}
  	    	    		}
  	    	    	}
  	    	    	//传真
  	    	    	editTebuDo.setFax(aeaDto.getFax());
  	    	    	
  	    	    	//角色信息
  	    	    	TEduBdRoleDo tebrDo = new TEduBdRoleDo();
  	    	    	//角色类型，1:监管部门，2:学校
  	    	    	tebrDo.setRoleType(aeaDto.getRoleType());
  	    	    	//角色名称
  	    	    	tebrDo.setRoleName(aeaDto.getRoleName());   
  	    	    	//最后更新时间
  	    	    	tebrDo.setLastUpdateTime(BCDTimeUtil.convertNormalFrom(null));
  	    	    	//查找当前角色信息
  	    	    	TEduBdRoleDo curTebrDo = db2Service.getBdRoleInfoByRoleName(aeaDto.getRoleName());
  	    	    	//更新角色信息到数据库 
  	    	    	boolean updateRoleFlag = false;
  	    	    	if(curTebrDo.getId() != null)
  	    	    		updateRoleFlag = db2Service.UpdateBdRoleInfoByField(tebrDo, "id", curTebrDo.getId());    //更新记录到数据源ds1的数据表t_edu_bd_role中 
  	    	    	if(updateRoleFlag) {
  	    	    		editTebuDo.setRoleId(curTebrDo.getId());  	    	    		
  	    	    	}
  	    	    	//更新用户信息到数据库
  	    	    	boolean updateUserFlag = db2Service.UpdateBdUserInfoByField(editTebuDo, "user_account", tebuDo.getUserAccount());
  	    	    	if(updateUserFlag) {
  	    	    		normResp = AppModConfig.getNormalResp(null);
  	    	    		logger.info("账号：" + userName + "，更新成功！");
  	    	    	}
  	    	    	
  	    	    	//删除原有权限
  	    	    	TEduBdUserPermDo tebrpDo = new TEduBdUserPermDo();
  	    	    	tebrpDo.setStat(0);
  	    	    	//删除数据源ds2的数据表t_edu_bd_user_perm中记录以用户ID
  	    	    	boolean deleteUserPermFlag = db2Service.DeleteBdUserPermInfoByUserId(tebuDo.getId());
  	    	    	if(deleteUserPermFlag) {
  	    	    		//数据权限
  	    	    		tebrpDoList = new ArrayList<>();
  	    	    		if(aeaDto.getAmDataPerm() != null && tebuDo.getRoleId() != null) {
  	    	    			List<String> schIdList = aeaDto.getAmDataPerm();
  	    	    			for(int i = 0; i < schIdList.size(); i++) {
  	    	    				tebrpDo = new TEduBdUserPermDo();
  	    	    				tebrpDo.setUserId(tebuDo.getId());
  	    	    				tebrpDo.setPermId(schIdList.get(i));
  	    	    				tebrpDo.setPermType(1);
  	    	    				tebrpDo.setCreateTime(BCDTimeUtil.convertNormalFrom(null));
  	    	    				tebrpDo.setUpdateTime(BCDTimeUtil.convertNormalFrom(null));
  	    	    				tebrpDo.setStat(1);
  	    	    				tebrpDoList.add(tebrpDo);
  	    	    			}
  	    	    		}  	    	    	
  	    	    		//菜单权限
  	    	    		if(aeaDto.getAmL1MenuPerm() != null && tebuDo.getRoleId() != null) {
  	    	    			List<String> l1MenuList = aeaDto.getAmL1MenuPerm();
  	    	    			for(int i = 0; i < l1MenuList.size(); i++) {
  	    	    				tebrpDo = new TEduBdUserPermDo();
  	    	    				tebrpDo.setUserId(tebuDo.getId());
  	    	    				tebrpDo.setPermId(l1MenuList.get(i));
  	    	    				tebrpDo.setPermType(2);
  	    	    				tebrpDo.setCreateTime(BCDTimeUtil.convertNormalFrom(null));
  	    	    				tebrpDo.setUpdateTime(BCDTimeUtil.convertNormalFrom(null));
  	    	    				tebrpDo.setStat(1);
  	    	    				tebrpDoList.add(tebrpDo);
  	    	    			}
  	    	    		}
  	    	    		if(aeaDto.getAmL2MenuPerm() != null && tebuDo.getRoleId() != null) {
  	    	    			List<String> l2MenuList = aeaDto.getAmL2MenuPerm();
  	    	    			for(int i = 0; i < l2MenuList.size(); i++) {
  	    	    				tebrpDo = new TEduBdUserPermDo();
  	    	    				tebrpDo.setUserId(tebuDo.getId());
  	    	    				tebrpDo.setPermId(l2MenuList.get(i));
  	    	    				tebrpDo.setPermType(2);
  	    	    				tebrpDo.setCreateTime(BCDTimeUtil.convertNormalFrom(null));
  	    	    				tebrpDo.setUpdateTime(BCDTimeUtil.convertNormalFrom(null));
  	    	    				tebrpDo.setStat(1);
  	    	    				tebrpDoList.add(tebrpDo);
  	    	    			}
  	    	    		}
  	    	    		if(aeaDto.getAmL3MenuPerm() != null && tebuDo.getRoleId() != null) {
  	    	    			List<String> l3MenuList = aeaDto.getAmL3MenuPerm();
  	    	    			for(int i = 0; i < l3MenuList.size(); i++) {
  	    	    				tebrpDo = new TEduBdUserPermDo();
  	    	    				tebrpDo.setUserId(tebuDo.getId());
  	    	    				tebrpDo.setPermId(l3MenuList.get(i));
  	    	    				tebrpDo.setPermType(2);
  	    	    				tebrpDo.setCreateTime(BCDTimeUtil.convertNormalFrom(null));
  	    	    				tebrpDo.setUpdateTime(BCDTimeUtil.convertNormalFrom(null));
  	    	    				tebrpDo.setStat(1);
  	    	    				tebrpDoList.add(tebrpDo);
  	    	    			}
  	    	    		}
  	    	    		//插入记录到数据源ds1的数据表t_edu_bd_user_perm中
  	    	    		db2Service.InsertBdUserPermInfo(tebrpDoList);
  	    	    	}
  	    	    }
  	    	    else {
  	    	    	codes[0] = 2015;
  	    	    	logger.info("用户账号：" + userName + "不存在" + "，更新失败！");
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
