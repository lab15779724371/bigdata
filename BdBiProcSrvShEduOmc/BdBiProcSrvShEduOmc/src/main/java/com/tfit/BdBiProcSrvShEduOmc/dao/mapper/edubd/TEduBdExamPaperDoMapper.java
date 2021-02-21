package com.tfit.BdBiProcSrvShEduOmc.dao.mapper.edubd;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.tfit.BdBiProcSrvShEduOmc.dao.domain.edubd.TEduBdExamPaperDo;

public interface TEduBdExamPaperDoMapper {
	//获取所有试卷
	List<TEduBdExamPaperDo> getAllTEduBdExamPaperDo();
	
	//获取所有试卷信息以试卷ID
	TEduBdExamPaperDo getTEduBdExamPaperDoById(@Param("id") String id);
}