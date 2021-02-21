package com.tfit.BdBiProcSrvShEduOmc.service.saas.impl;

import com.tfit.BdBiProcSrvShEduOmc.dao.domain.saas.ProRecyclerSupplier;
import com.tfit.BdBiProcSrvShEduOmc.dao.mapper.saas.ProRecyclerSupplierMapper;
import com.tfit.BdBiProcSrvShEduOmc.service.saas.ProRecyclerSupplierService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @Descritpion：回收单位管理服务
 * @author: tianfang_infotech
 * @date: 2019/1/10 19:24
 */
@Service
public class ProRecyclerSupplierServiceImpl implements ProRecyclerSupplierService {

    @Autowired
    private ProRecyclerSupplierMapper proRecyclerSupplierMapper;

    @Override
    public List<ProRecyclerSupplier> getProRecyclerSuppliersBySchoolId(String sourceId) {
        return proRecyclerSupplierMapper.findProRecyclerSuppliersBySchoolId(sourceId);
    }

    @Override
    public List<ProRecyclerSupplier> getAllProRecyclerSuppliers() {
        return proRecyclerSupplierMapper.findAllProRecyclerSuppliers();
    }
}
