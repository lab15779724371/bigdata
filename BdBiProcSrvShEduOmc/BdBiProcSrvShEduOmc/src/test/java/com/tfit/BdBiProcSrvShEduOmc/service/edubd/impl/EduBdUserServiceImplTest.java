package com.tfit.BdBiProcSrvShEduOmc.service.edubd.impl;

import com.tfit.BdBiProcSrvShEduOmc.BaseTests;
import com.tfit.BdBiProcSrvShEduOmc.dao.domain.edubd.EduBdUserDo;
import com.tfit.BdBiProcSrvShEduOmc.model.vo.BasicBdUser;
import com.tfit.BdBiProcSrvShEduOmc.service.edubd.EduBdUserService;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @Descritpion：
 * @author: yanzhao_xu
 * @date: 2019/1/23 11:36
 */
public class EduBdUserServiceImplTest extends BaseTests {

    @Autowired
    private EduBdUserService eduBdUserService;

    @Test
    public void getEduBdUser() {

        String token = "a318becfc2920700cab0c4f7ef99ec7087ab1b30";

        EduBdUserDo eduBdUser = eduBdUserService.getEduBdUser(token);

        Assert.assertNotNull(eduBdUser);
    }

    @Test
    public void getBasicBdUser() {

        String token = "a318becfc2920700cab0c4f7ef99ec7087ab1b30";

        BasicBdUser basicBdUser = eduBdUserService.getBasicBdUser(token);

        Assert.assertNotNull(basicBdUser);
    }
}