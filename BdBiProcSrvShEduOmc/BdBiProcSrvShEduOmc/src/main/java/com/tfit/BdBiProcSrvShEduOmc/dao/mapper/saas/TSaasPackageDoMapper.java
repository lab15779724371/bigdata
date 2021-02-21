package com.tfit.BdBiProcSrvShEduOmc.dao.mapper.saas;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.tfit.BdBiProcSrvShEduOmc.dao.domain.TSaasPackageDo;

@Mapper
public interface TSaasPackageDoMapper {
	//获取所有菜单组名称
	List<TSaasPackageDo> getAllMenuGroupName();
}
