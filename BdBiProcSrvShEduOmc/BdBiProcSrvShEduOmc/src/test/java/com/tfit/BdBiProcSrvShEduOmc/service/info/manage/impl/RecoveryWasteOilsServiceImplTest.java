package com.tfit.BdBiProcSrvShEduOmc.service.info.manage.impl;

import com.tfit.BdBiProcSrvShEduOmc.BaseTests;
import com.tfit.BdBiProcSrvShEduOmc.dto.info.manage.SchoolRecoveryWasteOilSearchDTO;
import com.tfit.BdBiProcSrvShEduOmc.dto.info.manage.RecoveryWasteOilSummarySearchDTO;
import com.tfit.BdBiProcSrvShEduOmc.dto.info.manage.WasteOilTypeCodeSearchDTO;
import com.tfit.BdBiProcSrvShEduOmc.model.vo.base.PagedList;
import com.tfit.BdBiProcSrvShEduOmc.service.Db1Service;
import com.tfit.BdBiProcSrvShEduOmc.service.DbHiveRecyclerWasteService;
import com.tfit.BdBiProcSrvShEduOmc.service.SaasService;
import com.tfit.BdBiProcSrvShEduOmc.service.info.manage.RecoveryWasteOilsService;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static org.junit.Assert.assertNotNull;

/**
 * @Descritpion：
 * @author: yanzhao_xu
 * @date: 2019/1/10 20:19
 */
public class RecoveryWasteOilsServiceImplTest extends BaseTests {

    @Autowired
    private RecoveryWasteOilsService recoveryWasteOilsService;
    
    @Autowired
    private Db1Service db1Service;
    
    @Autowired
    private SaasService saasService;
    
    @Autowired
    private DbHiveRecyclerWasteService dbHiveRecyclerWasteService;

    //@Test
    public void getSchoolRecoveryWasteOilTotalsForDistName() {

        RecoveryWasteOilSummarySearchDTO searchDTO = new RecoveryWasteOilSummarySearchDTO();
        searchDTO.setDistName("12");

        PagedList list = recoveryWasteOilsService.getSchoolRecoveryWasteOilSummary(searchDTO,dbHiveRecyclerWasteService);

        assertNotNull(list);
    }

    @Test
    public void getWasteOilTypeCodes() {

        WasteOilTypeCodeSearchDTO searchDTO = new WasteOilTypeCodeSearchDTO();

        List list = recoveryWasteOilsService.getWasteOilTypeCodes(searchDTO);

        assertNotNull(list);
    }

    //@Test
    public void getSchoolRecoveryWasteOilDetails() {

        SchoolRecoveryWasteOilSearchDTO searchDTO = new SchoolRecoveryWasteOilSearchDTO();

        PagedList list = recoveryWasteOilsService.getSchoolRecoveryWasteOilDetails(searchDTO,db1Service,saasService,dbHiveRecyclerWasteService);

        assertNotNull(list);
    }
}