package com.tfit.BdBiProcSrvShEduOmc.service.edu.impl;

import com.tfit.BdBiProcSrvShEduOmc.dao.domain.edu.EduSchool;
import com.tfit.BdBiProcSrvShEduOmc.dao.mapper.edu.EduSchoolExtMapper;
import com.tfit.BdBiProcSrvShEduOmc.service.edu.EduSchoolService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @Descritpion：教委学校服务实现类
 * @author: tianfang_infotech
 * @date: 2019/1/9 11:45
 */
@Service
public class EduSchoolServiceImpl implements EduSchoolService {

    @Autowired
    private EduSchoolExtMapper eduSchoolExtMapper;

    @Override
    public List<EduSchool> getEduSchools() {
        return eduSchoolExtMapper.findAllSchools();
    }
}
