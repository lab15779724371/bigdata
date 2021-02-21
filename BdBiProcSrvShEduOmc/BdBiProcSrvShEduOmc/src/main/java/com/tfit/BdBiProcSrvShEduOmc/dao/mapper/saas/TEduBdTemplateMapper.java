package com.tfit.BdBiProcSrvShEduOmc.dao.mapper.saas;

import java.util.List;

import com.tfit.BdBiProcSrvShEduOmc.obj.base.TEduBdTemplate;

public interface TEduBdTemplateMapper {
    int deleteByPrimaryKey(String id);

    int insert(TEduBdTemplate record);

    int insertSelective(TEduBdTemplate record);

    TEduBdTemplate selectByPrimaryKey(String id);

    int updateByPrimaryKeySelective(TEduBdTemplate record);

    int updateByPrimaryKey(TEduBdTemplate record);
    
    List<TEduBdTemplate> selectAllList();
    
    //获取最大的Id编号
    Integer selectMaxIdList();
    
    //根据模板数据查询模板
    TEduBdTemplate selectByTemplate(TEduBdTemplate key);

}