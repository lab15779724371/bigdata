package com.tfit.BdBiProcSrvShEduOmc.dao.mapper.edu;

import com.tfit.BdBiProcSrvShEduOmc.dao.domain.edu.EduSchool;

import java.util.List;

/**
 * @Descritpion：学校数据访问类
 * @author: tianfang_infotech
 * @date: 2019/1/9 11:16
 */
public interface EduSchoolExtMapper {

    /**
     * 查询所有学校列表
     * @return
     */
    List<EduSchool> findAllSchools();
}
