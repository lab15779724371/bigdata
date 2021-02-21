package com.tfit.BdBiProcSrvShEduOmc.dao.mapper.edubd;

import org.apache.ibatis.annotations.Param;

import com.tfit.BdBiProcSrvShEduOmc.dao.domain.TEduBdRoleDo;

//从数据源ds2的数据表t_edu_bd_role
public interface TEduBdRoleDoMapper {
	//查询角色信息以角色类型和角色名称
    TEduBdRoleDo getBdRoleInfoByRoleName(@Param(value = "roleType")int roleType, @Param(value = "roleName")String roleName);
}
