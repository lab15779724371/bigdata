package com.tfit.BdBiProcSrvShEduOmc.dao.mapper.saas;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.tfit.BdBiProcSrvShEduOmc.dao.domain.TBaseMaterialTypeDo;

@Mapper
public interface TBaseMaterialTypeDoMapper {
	// 获取所有物料分类
	List<TBaseMaterialTypeDo> getAllMatClassifyIdName();
}