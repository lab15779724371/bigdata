package com.tfit.BdBiProcSrvShEduOmc.dao.mapper.saas;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.tfit.BdBiProcSrvShEduOmc.dao.domain.TProRecyclerWasteDo;

@Mapper
public interface TProRecyclerWasteDoMapper {
	//获取回收人名称
	List<TProRecyclerWasteDo> getAllRecPersonName();
}
