package com.tfit.BdBiProcSrvShEduOmc.dao.mapper.edubd;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.tfit.BdBiProcSrvShEduOmc.dao.domain.edubd.TEduBdComplaintDo;

public interface TEduBdComplaintDoMapper {
	//插入投诉举报记录
	int insertTEduBdComplaintDo(TEduBdComplaintDo tebcpDo);
	
	//获取投诉举报以记录ID
	TEduBdComplaintDo getTEduBdComplaintDoById(@Param("id") String id);
	
	//更新投诉举报处理状态
	int updateCpStatusByTEduBdComplaintDo(TEduBdComplaintDo tebcpDo);
	
	//更新承办人名称
	int updateContractorByTEduBdComplaintDo(TEduBdComplaintDo tebcpDo);
	
	//更新办结反馈
	int updateFeedBackByTEduBdComplaintDo(TEduBdComplaintDo tebcpDo);
	
	//更新办结日期，格式：xxxx-xx-xx
	int updateFinishDateByTEduBdComplaintDo(TEduBdComplaintDo tebcpDo);
	
	//更新投诉举报最近更新时间
	int updateLastUpdateTimeByTEduBdComplaintDo(TEduBdComplaintDo tebcpDo);
	
	//获取投诉举报以日期段，日期格式：xxxx-xx-xx
	List<TEduBdComplaintDo> getTEduBdComplaintDosBySubDate(@Param("startDate") String startDate, @Param("endDate") String endDate);
	
	//获取所有投诉举报
	List<TEduBdComplaintDo> getAllTEduBdComplaintDos();
}