package com.tfit.BdBiProcSrvShEduOmc.dao.mapper.saas;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.tfit.BdBiProcSrvShEduOmc.dao.domain.TProRecyclerSupplierDo;

@Mapper
public interface TProRecyclerSupplierDoMapper {
	//获取回收单位ID和名称
	List<TProRecyclerSupplierDo> getAllRecyclerIdName();
	
	//获取回收人名称
	List<TProRecyclerSupplierDo> getAllRecPersonName();
}
