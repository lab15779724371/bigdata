package com.tfit.BdBiProcSrvShEduOmc.service.saas;

import com.tfit.BdBiProcSrvShEduOmc.dao.domain.saas.EduSchoolSupplier;
import com.tfit.BdBiProcSrvShEduOmc.model.vo.SupplierBasic;

import java.util.List;

/**
 * @Descritpion：学校供应商服务
 * @author: tianfang_infotech
 * @date: 2019/1/10 13:34
 */
public interface EduSchoolSupplierService {

    /**
     * 查询所有学校可用团餐公司信息（如需要，可启用缓存）
     * @return
     */
    List<EduSchoolSupplier> getAllSchoolSuppliers();

    /**
     * 查询学校可用团餐公司信息
     * @param schoolId
     * @return
     */
    List<EduSchoolSupplier> getSchoolSuppliers(String schoolId);

    /**
     * 查询供应商基本信息
     * @param supplierId
     * @return
     */
    SupplierBasic getSupplierBasicById(String supplierId);

    /**
     * 查询所有供应商基本信息（如需要，可启用缓存）
     * @return
     */
    List<SupplierBasic> getAllSupplierBasics();
}
