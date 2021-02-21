package com.tfit.BdBiProcSrvShEduOmc.dao.mapper.edubd;

import org.apache.ibatis.annotations.Param;

import com.tfit.BdBiProcSrvShEduOmc.dao.domain.edubd.TEduBdQuestionBodyDo;

public interface TEduBdQuestionBodyDoMapper {
	//获取试题以试题ID
	TEduBdQuestionBodyDo getTEduBdQuestionBodyDoById(@Param("id") String id);
}