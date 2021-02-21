package com.tfit.BdBiProcSrvShEduOmc.service.saas.impl;

import com.tfit.BdBiProcSrvShEduOmc.BaseTests;
import com.tfit.BdBiProcSrvShEduOmc.dao.domain.saas.EduSchoolSupplier;
import com.tfit.BdBiProcSrvShEduOmc.model.vo.SupplierBasic;
import com.tfit.BdBiProcSrvShEduOmc.service.saas.EduSchoolSupplierService;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;

import java.util.List;

/**
 * @Descritpion：
 * @author: yanzhao_xu
 * @date: 2019/1/11 10:23
 */
public class EduSchoolSupplierServiceImplTest extends BaseTests {

    @Autowired
    private EduSchoolSupplierService eduSchoolSupplierService;

    @Test
    public void getAllSchoolSuppliers() {
        List<EduSchoolSupplier> list = eduSchoolSupplierService.getAllSchoolSuppliers();

        Assert.assertTrue(!CollectionUtils.isEmpty(list));
    }

    @Test
    public void getSchoolSuppliers() {
        String schoolId = "8165443e-6ae6-4d04-a55e-a201b3399bfd";
        List<EduSchoolSupplier> list = eduSchoolSupplierService.getSchoolSuppliers(schoolId);

        Assert.assertNotNull(list);
    }

    @Test
    public void getAllSupplierBasics() {

        List<SupplierBasic> list = eduSchoolSupplierService.getAllSupplierBasics();

        Assert.assertTrue(!CollectionUtils.isEmpty(list));
    }

    @Test
    public void getSupplierBasicById() {

        String schoolId = "c21a3b39-49cf-4977-bae3-c562a1af1c5c";
        SupplierBasic supplierBasic = eduSchoolSupplierService.getSupplierBasicById(schoolId);

        Assert.assertNotNull(supplierBasic);
    }

}