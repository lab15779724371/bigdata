package com.tfit.BdBiProcSrvShEduOmc.dao.mapper.edubd;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.tfit.BdBiProcSrvShEduOmc.dao.domain.edubd.TEduBdEtvidLibDo;

public interface TEduBdEtvidLibDoMapper {
	//插入教育视频记录
	int insertTEduBdEtvidLibDo(TEduBdEtvidLibDo tebelDo);
	
	//获取教育视频以记录ID
	TEduBdEtvidLibDo getTEduBdEtvidLibDoById(@Param("id") String id);
	
	//获取所有教育视频
	List<TEduBdEtvidLibDo> getAllTEduBdEtvidLibDos();
	
	//获取教育视频以开始和结束时间
	List<TEduBdEtvidLibDo> getTEduBdEtvidLibDosByCreateTime(@Param("startTime") String startTime, @Param("endTime") String endTime);
	
	//更新视频名称
	int updateVidNameByTEduBdEtvidLibDo(TEduBdEtvidLibDo tebelDo);
	
	//副标题	
	int updateSubTitleByTEduBdEtvidLibDo(TEduBdEtvidLibDo tebelDo);
	
	//视频分类，0:系统操作，1:食品安全，2:政策法规	
	int updateVidCategoryByTEduBdEtvidLibDo(TEduBdEtvidLibDo tebelDo);

	//缩略图图片URL
	int updateThumbUrlByTEduBdEtvidLibDo(TEduBdEtvidLibDo tebelDo);
	
	//视频URL
	int updateVidUrlByTEduBdEtvidLibDo(TEduBdEtvidLibDo tebelDo);
	
	//视频描述内容	
	int updateVidDescrContByTEduBdEtvidLibDo(TEduBdEtvidLibDo tebelDo);
	
	//最后更新时间
	int updateLastUpdateTimeByTEduBdEtvidLibDo(TEduBdEtvidLibDo tebelDo);
	
	//删除教育视频记录以记录ID
    int deleteTEduBdEtvidLibDoById(@Param("id") String id);
}