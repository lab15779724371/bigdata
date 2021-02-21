package com.tfit.BdBiProcSrvShEduOmc.dao.mapper.edubd;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.tfit.BdBiProcSrvShEduOmc.dao.domain.edubd.TEduBdFoodSafetyGradeDo;

public interface TEduBdFoodSafetyGradeDoMapper {
	//获取所有食品安全等级记录
	List<TEduBdFoodSafetyGradeDo> getAllTEduBdFoodSafetyGradeDos();
	
	//获取食品安全等级记录以区域名称
	List<TEduBdFoodSafetyGradeDo> getTEduBdFoodSafetyGradeDoByDistName(@Param("distName") String distName);
}