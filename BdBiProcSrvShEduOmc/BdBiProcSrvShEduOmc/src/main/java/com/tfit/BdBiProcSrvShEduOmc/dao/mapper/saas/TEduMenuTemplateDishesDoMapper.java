package com.tfit.BdBiProcSrvShEduOmc.dao.mapper.saas;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.tfit.BdBiProcSrvShEduOmc.dao.domain.TEduMenuTemplateDishesDo;

@Mapper
public interface TEduMenuTemplateDishesDoMapper {
	//获取所有菜品类别
	List<TEduMenuTemplateDishesDo> getAllDishTypes();
}