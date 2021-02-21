package com.tfit.BdBiProcSrvShEduOmc.service.saas.impl;

import com.tfit.BdBiProcSrvShEduOmc.dao.domain.SupplierIdDo;
import com.tfit.BdBiProcSrvShEduOmc.dao.mapper.saas.SupplierIdDoExtMapper;
import com.tfit.BdBiProcSrvShEduOmc.service.saas.GroupMealCompanyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @Descritpion：团餐公司服务实现类
 * @author: tianfang_infotech
 * @date: 2019/1/9 18:24
 */
@Service
public class GroupMealCompanyServiceImpl implements GroupMealCompanyService {

    @Autowired
    private SupplierIdDoExtMapper supplierIdDoExtMapper;

    @Override
    public List<SupplierIdDo> getAllSupplierIds() {
        return supplierIdDoExtMapper.getAllSupplierId();
    }
}
