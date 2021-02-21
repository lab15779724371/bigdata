package com.tfit.BdBiProcSrvShEduOmc.service.saas;

import com.tfit.BdBiProcSrvShEduOmc.dao.domain.saas.ProRecyclerSupplier;

import java.util.List;

/**
 * @Descritpion：
 * @author: tianfang_infotech
 * @date: 2019/1/10 19:24
 */
public interface ProRecyclerSupplierService {

    /**
     * 查询学校或团餐公司回收单位
     * @param sourceId
     * @return
     */
    List<ProRecyclerSupplier> getProRecyclerSuppliersBySchoolId(String sourceId);

    /**
     * 查询所有回收单位
     * @return
     */
    List<ProRecyclerSupplier> getAllProRecyclerSuppliers();
}
