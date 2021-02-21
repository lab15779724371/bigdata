package com.tfit.BdBiProcSrvShEduOmc.service.edu;

import com.tfit.BdBiProcSrvShEduOmc.dao.domain.edu.EduSchool;

import java.util.List;

/**
 * @Descritpion：教委学校信息服务
 * @author: tianfang_infotech
 * @date: 2019/1/9 11:43
 */
public interface EduSchoolService {

    /**
     * 查找学校信息（如需要，可启用缓存）
     * @return
     */
    List<EduSchool> getEduSchools();
}
