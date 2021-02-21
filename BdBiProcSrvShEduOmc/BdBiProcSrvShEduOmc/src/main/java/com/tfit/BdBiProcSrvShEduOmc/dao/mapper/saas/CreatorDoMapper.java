package com.tfit.BdBiProcSrvShEduOmc.dao.mapper.saas;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.tfit.BdBiProcSrvShEduOmc.dao.domain.CreatorDo;

@Mapper
public interface CreatorDoMapper {
	List<CreatorDo> getCreatorBySupplierId(String strSupplierId);
}
