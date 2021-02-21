package com.tfit.BdBiProcSrvShEduOmc.dao.mapper.edubd;

import org.apache.ibatis.annotations.Param;

import com.tfit.BdBiProcSrvShEduOmc.dao.domain.edubd.TEduBdExamPaperSubjectDo;

public interface TEduBdExamPaperSubjectDoMapper {
	//获取所有试卷大题型主题以试卷ID和试题类型
	TEduBdExamPaperSubjectDo getTEduBdExamPaperSubjectDoByEpIdQuestionType(@Param("epId") String epId, @Param("questionType") int questionType);
}