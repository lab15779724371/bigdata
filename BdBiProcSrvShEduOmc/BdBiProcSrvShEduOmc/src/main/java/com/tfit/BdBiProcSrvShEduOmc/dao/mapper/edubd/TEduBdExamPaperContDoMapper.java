package com.tfit.BdBiProcSrvShEduOmc.dao.mapper.edubd;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.tfit.BdBiProcSrvShEduOmc.dao.domain.edubd.TEduBdExamPaperContDo;

public interface TEduBdExamPaperContDoMapper {
	//获取所有试卷内容以试卷ID
	List<TEduBdExamPaperContDo> getTEduBdExamPaperContDosByEpId(@Param("epId") String epId);
}