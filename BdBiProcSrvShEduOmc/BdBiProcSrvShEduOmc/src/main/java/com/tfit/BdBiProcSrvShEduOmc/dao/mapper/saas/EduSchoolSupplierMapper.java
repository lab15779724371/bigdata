package com.tfit.BdBiProcSrvShEduOmc.dao.mapper.saas;

import com.tfit.BdBiProcSrvShEduOmc.dao.domain.saas.EduSchoolSupplier;
import com.tfit.BdBiProcSrvShEduOmc.model.vo.SupplierBasic;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @Descritpion：学校供应商Mapper
 * @author: tianfang_infotech
 * @date: 2019/1/10 11:56
 */
public interface EduSchoolSupplierMapper {

    /**
     * 查询某学校供应商信息
     * @param schoolId
     * @return
     */
    List<EduSchoolSupplier> findSuppliersBySchoolId(@Param("schoolId") String schoolId);

    /**
     * 查询所有学校供应商信息
     * @return
     */
    List<EduSchoolSupplier> findAllSuppliers();

    /**
     * 查询供应商基本信息
     * @param supplierId
     * @return
     */
    SupplierBasic findSupplierBasicById(@Param("supplierId") String supplierId);

    /**
     * 查询供应商基本信息
     * @return
     */
    List<SupplierBasic> findAllSupplierBasics();
}
