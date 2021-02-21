package com.tfit.BdBiProcSrvShEduOmc.dao.mapper.edubd;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.tfit.BdBiProcSrvShEduOmc.dao.domain.edubd.TEduBdQuestionCandAnsDo;

public interface TEduBdQuestionCandAnsDoMapper {
	//获取候选答案以试题ID
	List<TEduBdQuestionCandAnsDo> getTEduBdQuestionCandAnsDoByQuestionId(@Param("questionId") String questionId);
}