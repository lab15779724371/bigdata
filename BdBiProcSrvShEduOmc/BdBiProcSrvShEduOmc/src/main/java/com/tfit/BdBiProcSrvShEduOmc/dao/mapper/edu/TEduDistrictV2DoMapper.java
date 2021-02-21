package com.tfit.BdBiProcSrvShEduOmc.dao.mapper.edu;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.tfit.BdBiProcSrvShEduOmc.dao.domain.edu.TEduDistrictDo;

@Mapper
public interface TEduDistrictV2DoMapper {
	//从数据源ds1的数据表t_edu_district中查找id和区域名称
    List<TEduDistrictDo> getDistIdNameListByDs1();
}