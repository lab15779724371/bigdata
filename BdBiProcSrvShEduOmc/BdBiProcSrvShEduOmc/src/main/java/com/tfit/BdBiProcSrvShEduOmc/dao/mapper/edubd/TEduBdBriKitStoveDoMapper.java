package com.tfit.BdBiProcSrvShEduOmc.dao.mapper.edubd;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.tfit.BdBiProcSrvShEduOmc.dao.domain.edubd.TEduBdBriKitStoveDo;

public interface TEduBdBriKitStoveDoMapper {
	//查询学校视频监控记录信息以学校id
    List<TEduBdBriKitStoveDo> getSchVidSurvInfosBySchId(@Param("schoolId") String schoolId);
    
    //查询所有学校视频监控记录信息
    List<TEduBdBriKitStoveDo> getAllSchVidSurvInfos();
    
    //查询学校视频监控记录信息以区域id
    List<TEduBdBriKitStoveDo> getSchVidSurvInfosByDistId(@Param("regionId") String regionId);
}