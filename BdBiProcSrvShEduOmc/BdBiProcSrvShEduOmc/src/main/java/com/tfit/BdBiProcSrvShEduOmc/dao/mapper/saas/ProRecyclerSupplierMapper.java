package com.tfit.BdBiProcSrvShEduOmc.dao.mapper.saas;

import com.tfit.BdBiProcSrvShEduOmc.dao.domain.saas.ProRecyclerSupplier;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @Descritpion：回收单位管理
 * @author: tianfang_infotech
 * @date: 2019/1/10 19:02
 */
public interface ProRecyclerSupplierMapper {

    /**
     * 查询所有有效的回收公司
     * @return
     */
    List<ProRecyclerSupplier> findAllProRecyclerSuppliers();

    /**
     * 查询回收公司
     * @param schoolId
     * @return
     */
    List<ProRecyclerSupplier> findProRecyclerSuppliersBySchoolId(@Param("schoolId") String schoolId);
}
