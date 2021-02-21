package com.tfit.BdBiProcSrvShEduOmc.appmod.user;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tfit.BdBiProcSrvShEduOmc.config.AppModConfig;
import com.tfit.BdBiProcSrvShEduOmc.dao.domain.TEduBdRoleDo;
import com.tfit.BdBiProcSrvShEduOmc.dao.domain.TEduBdUserPermDo;
import com.tfit.BdBiProcSrvShEduOmc.dao.domain.TEduSchoolDo;
import com.tfit.BdBiProcSrvShEduOmc.dao.domain.TEduBdUserDo;
import com.tfit.BdBiProcSrvShEduOmc.dto.IOTHttpRspVO;
import com.tfit.BdBiProcSrvShEduOmc.dto.user.AmAddAccountDTO;
import com.tfit.BdBiProcSrvShEduOmc.obj.base.DepartmentObj;
import com.tfit.BdBiProcSrvShEduOmc.service.Db1Service;
import com.tfit.BdBiProcSrvShEduOmc.service.Db2Service;
import com.tfit.BdBiProcSrvShEduOmc.util.BCDTimeUtil;
import com.tfit.BdBiProcSrvShEduOmc.util.UniqueIdGen;

//添加账号应用模型
public class AmAddAccountAppMod {
	private static final Logger logger = LogManager.getLogger(AmAddAccountAppMod.class.getName());
	
	@Autowired
	ObjectMapper objectMapper = new ObjectMapper();
	
	//报表文件资源路径
	String repFileResPath = "/userPermInfo/";
	
	//添加账号应用模型函数
  	public IOTHttpRspVO appModFunc(String token, String strBodyCont, Db1Service db1Service, Db2Service db2Service, int[] codes) {
  		IOTHttpRspVO normResp = null;
  		//按参数形式处理
  		if(token != null && strBodyCont != null && db2Service != null) {
  			
  			AmAddAccountDTO aaaDto = null;
  			String userName = null;
  			List<TEduBdUserPermDo> tebrpDoList = null;
  			//将json子串转成对象
  			try {
				aaaDto = objectMapper.readValue(strBodyCont, AmAddAccountDTO.class);
				if(aaaDto != null)
  					userName = aaaDto.getUserName();
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
  			if(aaaDto != null && userName != null) {
  	    	    //判断用户名是否已存在
  				TEduBdUserDo tebuDo = db2Service.getBdUserInfoByUserName(userName);
  	    	    if(tebuDo.getId() == null && tebuDo.getUserAccount() == null) {    //不存在该用户，则可以添加
  	    	    	//获取父账户信息以token
  	    	    	TEduBdUserDo parTebuDo = db2Service.getBdUserInfoByToken(token);
  	    	    	//用户信息
  	    	    	//主键id
  	    	    	tebuDo.setId(UniqueIdGen.uuidInterSeg());
  	    	    	//是否为管理员，0:否，1:是
  	    	    	tebuDo.setIsAdmin(aaaDto.getIsAdmin());          
  	    	    	//用户名
  	    	    	tebuDo.setUserAccount(userName);
  	    	    	//密码
  	    	    	tebuDo.setPassword(aaaDto.getPassword());
  	    	    	//姓名
  	    	    	tebuDo.setName(aaaDto.getFullName());    
  	    	    	//固定电话
  	    	    	tebuDo.setFixPhone(aaaDto.getFixPhone());
  	    	    	//手机号码
  	    	    	tebuDo.setMobilePhone(aaaDto.getMobPhone());
  	    	    	//邮箱
  	    	    	tebuDo.setEmail(aaaDto.getEmail());
  	    	    	//账户父ID，空表示超级管理员账户，拥有最高权限
  	    	    	tebuDo.setParentId(parTebuDo.getId());
  	    	    	//最后登录时间
  	    	    	tebuDo.setLastLoginTime(null);
  	    	    	//创建者
  	    	    	tebuDo.setCreator(parTebuDo.getUserAccount());
  	    	    	//创建时间
  	    	    	tebuDo.setCreateTime(BCDTimeUtil.convertNormalFrom(null));
  	    	    	//更新人
  	    	    	tebuDo.setUpdater(parTebuDo.getUserAccount());
  	    	    	//更新时间
  	    	    	tebuDo.setLastUpdateTime(BCDTimeUtil.convertNormalFrom(null));
  	    	    	//是否禁用0禁用 1启用
  	    	    	tebuDo.setForbid(1);
  	    	    	//用户授权码
  	    	    	tebuDo.setToken(null);
  	    	    	//是否有效 1 有效 0 无效
  	    	    	tebuDo.setStat(1);
  	    	    	//备注
  	    	    	tebuDo.setRemarks(null);
  	    	    	
  	    	    	tebuDo.setUserType(parTebuDo.getUserType());
  	    	    	
  	    	    	//用户单位ID
  	    	    	if(aaaDto.getRoleType() != null) {
  	    	    		if(aaaDto.getRoleType().intValue() == 1) {      //监管部门
  	    	    			tebuDo.setOrgId(aaaDto.getUserOrgId());
  	    	    			tebuDo.setOrgName(aaaDto.getUserOrgId());
  	    	    		}
  	    	    		else if(aaaDto.getRoleType().intValue() == 2) {   //学校
  	    	    			//所有学校id
  	    	    			List<TEduSchoolDo> tesDoList = db1Service.getTEduSchoolDoListByDs1(null,1,1, 1);
  	    	    			if(tesDoList != null) {
  	    	    				for (int i = 0; i < tesDoList.size(); i++) {
  	    	    					if(aaaDto.getUserOrgId().equals(tesDoList.get(i).getId())) {
  	    	    						tebuDo.setOrgId(aaaDto.getUserOrgId());
  	    	  	    	    			tebuDo.setOrgName(tesDoList.get(i).getSchoolName());
  	    	    						break;
  	    	    					}
  	    	    				}		
  	    	  				}
  	    	    		}
  	    	    	}
  	    	    	//传真
  	    	    	tebuDo.setFax(aaaDto.getFax());
  	    	    		
  	    	        //从数据源ds2的数据表t_edu_bd_role中查找角色信息以角色名称
  	    	    	if(aaaDto.getRoleName() != null) {
  	    	    		TEduBdRoleDo tebrDo = db2Service.getBdRoleInfoByRoleName(aaaDto.getRoleName());
  	    	    		if(tebrDo.getRoleName() != null) {
  	    	    			//角色id
  	    	    			tebuDo.setRoleId(tebrDo.getId());  	    	    			
  	    	    		}
  	    	    	}
  	    	    	//保存用户信息到数据库
  	    			boolean saveUserFlag = db2Service.InsertBdUserInfo(tebuDo);
  	    			if(saveUserFlag) {
  	    				normResp = AppModConfig.getNormalResp(null);
  	    				logger.info("账号：" + userName + "，添加成功！");
  	    			}  	    	    	
  	    	    	//数据权限
  	    	    	tebrpDoList = new ArrayList<>();
  	    	    	TEduBdUserPermDo tebrpDo = null;
  	    	    	if(aaaDto.getAmDataPerm() != null && tebuDo.getRoleId() != null) {
  	    	    		List<String> schIdList = aaaDto.getAmDataPerm();
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
  	    	    	if(aaaDto.getAmL1MenuPerm() != null && tebuDo.getRoleId() != null) {
  	    	    		List<String> l1MenuList = aaaDto.getAmL1MenuPerm();
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
  	    	    	if(aaaDto.getAmL2MenuPerm() != null && tebuDo.getRoleId() != null) {
  	    	    		List<String> l2MenuList = aaaDto.getAmL2MenuPerm();
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
  	    	    	if(aaaDto.getAmL3MenuPerm() != null && tebuDo.getRoleId() != null) {
  	    	    		List<String> l3MenuList = aaaDto.getAmL3MenuPerm();
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
  	    	    	//写入权限数据到用户权限文件中
  	    	    }
  	    	    else {
  	    	    	codes[0] = 2005;
  	    	    	logger.info("用户账号：" + userName + "已存在" + "，添加失败！");
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
