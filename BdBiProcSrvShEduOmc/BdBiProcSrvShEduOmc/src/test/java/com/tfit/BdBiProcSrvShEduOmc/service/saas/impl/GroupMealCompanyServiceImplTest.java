package com.tfit.BdBiProcSrvShEduOmc.service.saas.impl;

import com.tfit.BdBiProcSrvShEduOmc.BaseTests;
import com.tfit.BdBiProcSrvShEduOmc.dao.domain.SupplierIdDo;
import com.tfit.BdBiProcSrvShEduOmc.service.SaasService;
import com.tfit.BdBiProcSrvShEduOmc.service.impl.SaasServiceImpl;
import com.tfit.BdBiProcSrvShEduOmc.service.saas.GroupMealCompanyService;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;

import java.util.List;

/**
 * @Descritpion：
 * @author: yanzhao_xu
 * @date: 2019/1/9 18:49
 */
public class GroupMealCompanyServiceImplTest extends BaseTests {

    @Autowired
    private GroupMealCompanyService groupMealCompanyService;

    @Autowired
    private SaasService saasService;

    @Test
    public void getAllSupplierIds() {

        List<SupplierIdDo> list = groupMealCompanyService.getAllSupplierIds();

        Assert.assertTrue(!CollectionUtils.isEmpty(list));
    }

    @Test
    public void getAllSupplierIds1() {

        List<SupplierIdDo> list = saasService.getAllSupplierId();

        Assert.assertTrue(!CollectionUtils.isEmpty(list));
    }
}