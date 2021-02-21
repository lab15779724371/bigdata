package com.tfit.BdBiProcSrvShEduOmc.service.edu.impl;

import com.tfit.BdBiProcSrvShEduOmc.BaseTests;
import com.tfit.BdBiProcSrvShEduOmc.dao.domain.edu.EduSchool;
import com.tfit.BdBiProcSrvShEduOmc.service.edu.EduSchoolService;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;

import java.util.List;

public class EduSchoolServiceImplTest extends BaseTests {

    @Autowired
    private EduSchoolService eduSchoolService;

    @Test
    public void getEduSchools() {

        List<EduSchool> list = eduSchoolService.getEduSchools();

        Assert.assertTrue(!CollectionUtils.isEmpty(list));
    }

}