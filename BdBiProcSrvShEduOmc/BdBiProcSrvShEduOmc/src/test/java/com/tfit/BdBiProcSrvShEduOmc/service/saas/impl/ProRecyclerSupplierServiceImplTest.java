package com.tfit.BdBiProcSrvShEduOmc.service.saas.impl;

import com.tfit.BdBiProcSrvShEduOmc.BaseTests;
import com.tfit.BdBiProcSrvShEduOmc.dao.domain.saas.ProRecyclerSupplier;
import com.tfit.BdBiProcSrvShEduOmc.service.saas.ProRecyclerSupplierService;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;

import java.util.List;

import static org.junit.Assert.*;

/**
 * @Descritpion：
 * @author: yanzhao_xu
 * @date: 2019/1/10 20:08
 */
public class ProRecyclerSupplierServiceImplTest extends BaseTests {

    @Autowired
    private ProRecyclerSupplierService proRecyclerSupplierService;

    @Test
    public void getProRecyclerSuppliersBySchoolId() {
        String schoolId = "8165443e-6ae6-4d04-a55e-a201b3399bfd";
        List<ProRecyclerSupplier> list = proRecyclerSupplierService.getProRecyclerSuppliersBySchoolId(schoolId);

        assertNotNull(list);
    }

    @Test
    public void getAllProRecyclerSuppliers() {

        List<ProRecyclerSupplier> list = proRecyclerSupplierService.getAllProRecyclerSuppliers();

        assertTrue(!CollectionUtils.isEmpty(list));
    }
}