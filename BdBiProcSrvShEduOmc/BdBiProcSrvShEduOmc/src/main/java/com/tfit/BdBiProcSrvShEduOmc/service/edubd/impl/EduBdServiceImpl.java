package com.tfit.BdBiProcSrvShEduOmc.service.edubd.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.tfit.BdBiProcSrvShEduOmc.dao.domain.TEduBdRoleDo;
import com.tfit.BdBiProcSrvShEduOmc.dao.mapper.edubd.TEduBdRoleDoMapper;
import com.tfit.BdBiProcSrvShEduOmc.service.edubd.EduBdService;
@Service
public class EduBdServiceImpl implements EduBdService {
	@Autowired
    private TEduBdRoleDoMapper tebrDoMapper;
	
	//查询角色信息以角色类型和角色名称
    public TEduBdRoleDo getBdRoleInfoByRoleName(int roleType, String roleName) {
    	return tebrDoMapper.getBdRoleInfoByRoleName(roleType, roleName);
    }
}