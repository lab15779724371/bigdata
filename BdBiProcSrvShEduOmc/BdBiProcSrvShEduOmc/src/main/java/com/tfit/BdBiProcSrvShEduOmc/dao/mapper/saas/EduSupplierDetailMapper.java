package com.tfit.BdBiProcSrvShEduOmc.dao.mapper.saas;


import java.util.List;

import com.tfit.BdBiProcSrvShEduOmc.obj.base.EduSupplierDetail;

public interface EduSupplierDetailMapper {

    List<EduSupplierDetail> getSupplierList(EduSupplierDetail example);
}