package com.tfit.BdBiProcSrvShEduOmc.service;

import java.util.List;

import com.tfit.BdBiProcSrvShEduOmc.dao.domain.edu.TEduDistrictDo;

public interface Custom1Service {
	//从数据源ds1的数据表t_edu_district中查找id和区域名称
    List<TEduDistrictDo> getListByDs1IdName();
}
