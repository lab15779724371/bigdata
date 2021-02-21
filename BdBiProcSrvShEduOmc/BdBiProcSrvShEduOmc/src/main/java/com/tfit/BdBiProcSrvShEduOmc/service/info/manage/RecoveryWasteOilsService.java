package com.tfit.BdBiProcSrvShEduOmc.service.info.manage;

import java.util.List;

import com.tfit.BdBiProcSrvShEduOmc.dto.info.manage.RecoveryWasteOilSummarySearchDTO;
import com.tfit.BdBiProcSrvShEduOmc.dto.info.manage.RmcRecoveryWasteOilSearchDTO;
import com.tfit.BdBiProcSrvShEduOmc.dto.info.manage.SchoolRecoveryWasteOilSearchDTO;
import com.tfit.BdBiProcSrvShEduOmc.dto.info.manage.WasteOilTypeCodeSearchDTO;
import com.tfit.BdBiProcSrvShEduOmc.model.export.RmcRecoveryWasteOilDetailsExport;
import com.tfit.BdBiProcSrvShEduOmc.model.export.RmcRecoveryWasteOilSummaryExport;
import com.tfit.BdBiProcSrvShEduOmc.model.export.SchoolRecoveryWasteOilDetailsExport;
import com.tfit.BdBiProcSrvShEduOmc.model.export.SchoolRecoveryWasteOilSummaryExport;
import com.tfit.BdBiProcSrvShEduOmc.model.vo.base.PagedList;
import com.tfit.BdBiProcSrvShEduOmc.service.Db1Service;
import com.tfit.BdBiProcSrvShEduOmc.service.Db2Service;
import com.tfit.BdBiProcSrvShEduOmc.service.DbHiveRecyclerWasteService;
import com.tfit.BdBiProcSrvShEduOmc.service.SaasService;

/**
 * @Descritpion：回收废弃油脂服务
 * @author: tianfang_infotech
 * @date: 2019/1/3 11:53
 */
public interface RecoveryWasteOilsService {

    /**
     * 获取废弃油脂类型编码列表
     * @param searchDTO
     * @return
     */
    List<?> getWasteOilTypeCodes(WasteOilTypeCodeSearchDTO searchDTO);

    /**
     * 每个区所有学校回收废弃油脂汇总列表
     * @param searchDTO
     * @return
     */
    PagedList<?> getSchoolRecoveryWasteOilSummary(RecoveryWasteOilSummarySearchDTO searchDTO,DbHiveRecyclerWasteService dbHiveRecyclerWasteService);

    /**
     * 导出每个区所有学校回收废弃油脂汇总列表
     * @param searchDTO
     * @return
     */
    SchoolRecoveryWasteOilSummaryExport exportSchoolRecoveryWasteOilSummary(RecoveryWasteOilSummarySearchDTO searchDTO,DbHiveRecyclerWasteService dbHiveRecyclerWasteService);

    /**
     * 获取学校废弃油脂详情列表
     * @param searchDTO
     * @return
     */
    PagedList<?> getSchoolRecoveryWasteOilDetails(SchoolRecoveryWasteOilSearchDTO searchDTO,Db1Service db1Service,SaasService saasService,
    		DbHiveRecyclerWasteService dbHiveRecyclerWasteService);

    /**
     * 导出学校废弃油脂详情列表
     * @param searchDTO
     * @return
     */
    SchoolRecoveryWasteOilDetailsExport exportSchoolRecoveryWasteOilDetails(String token,SchoolRecoveryWasteOilSearchDTO searchDTO,Db1Service db1Service
    		,SaasService saasService,Db2Service db2Service,DbHiveRecyclerWasteService dbHiveRecyclerWasteService);

    /**
     * 每个区所有团餐公司回收废弃油脂汇总列表
     * @param searchDTO
     * @return
     */
    PagedList<?> getRmcRecoveryWasteOilSummary(RecoveryWasteOilSummarySearchDTO searchDTO,DbHiveRecyclerWasteService dbHiveRecyclerWasteService);

    /**
     * 导出每个区所有团餐公司回收废弃油脂汇总列表
     * @param searchDTO
     * @return
     */
    RmcRecoveryWasteOilSummaryExport exportRmcRecoveryWasteOilSummary(RecoveryWasteOilSummarySearchDTO searchDTO,DbHiveRecyclerWasteService dbHiveRecyclerWasteService);

    /**
     * 获取团餐公司废弃油脂详情列表
     * @param searchDTO
     * @return
     */
    PagedList<?> getRmcRecoveryWasteOilDetails(RmcRecoveryWasteOilSearchDTO searchDTO,SaasService saasService,DbHiveRecyclerWasteService dbHiveRecyclerWasteService);

    /**
     * 导出团餐公司废弃油脂详情列表
     * @param searchDTO
     * @return
     */
    RmcRecoveryWasteOilDetailsExport exportRmcRecoveryWasteOilDetails(String token,RmcRecoveryWasteOilSearchDTO searchDTO,SaasService saasService,Db2Service db2Service, DbHiveRecyclerWasteService dbHiveRecyclerWasteService);
}
