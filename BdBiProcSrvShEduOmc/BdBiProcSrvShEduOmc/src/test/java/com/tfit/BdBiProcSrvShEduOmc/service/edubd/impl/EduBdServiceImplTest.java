package com.tfit.BdBiProcSrvShEduOmc.service.edubd.impl;

import com.tfit.BdBiProcSrvShEduOmc.BaseTests;
import com.tfit.BdBiProcSrvShEduOmc.dao.domain.TEduBdRoleDo;
import com.tfit.BdBiProcSrvShEduOmc.service.edubd.EduBdService;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.Assert.assertNotNull;

/**
 * @Descritpion：
 * @author: yanzhao_xu
 * @date: 2019/1/11 16:09
 */
public class EduBdServiceImplTest extends BaseTests {

    @Autowired
    private EduBdService eduBdService;

    @Test
    public void getBdRoleInfoByRoleName() {

        TEduBdRoleDo list = eduBdService.getBdRoleInfoByRoleName(1, "11");

        assertNotNull(list);

    }
}