package com.tfit.BdBiProcSrvShEduOmc.dao.mapper.edu;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.tfit.BdBiProcSrvShEduOmc.dao.domain.SchIdNameDo;

@Mapper
public interface TEduSchoolDoMapper {
	//从数据源ds1的数据表t_edu_school中查找所有id以区域ID（空时在查询所有）
    List<SchIdNameDo> getSchIdListByDs1();
    
	//从数据源ds1的数据表t_edu_school中查找所有id以区域ID（空时在查询所有）
    List<SchIdNameDo> getSchIdListByDs1DistId(@Param(value = "distId")String distId);
}
