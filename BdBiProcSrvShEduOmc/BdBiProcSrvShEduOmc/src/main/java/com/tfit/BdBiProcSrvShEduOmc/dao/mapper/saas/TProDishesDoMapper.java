package com.tfit.BdBiProcSrvShEduOmc.dao.mapper.saas;

import java.util.List;

import com.tfit.BdBiProcSrvShEduOmc.dao.domain.TProDishesDo;

public interface TProDishesDoMapper {
	//获取所有菜品名称
	List<TProDishesDo> getAllDishNames();
}
