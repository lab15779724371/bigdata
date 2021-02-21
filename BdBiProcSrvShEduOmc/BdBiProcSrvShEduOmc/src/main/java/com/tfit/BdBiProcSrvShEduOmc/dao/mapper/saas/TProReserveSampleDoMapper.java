package com.tfit.BdBiProcSrvShEduOmc.dao.mapper.saas;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.tfit.BdBiProcSrvShEduOmc.dao.domain.TProReserveSampleDo;

@Mapper
public interface TProReserveSampleDoMapper {
	//获取留样单位
	List<TProReserveSampleDo> getAllRsUnits();
}
