package com.tfit.BdBiProcSrvShEduOmc.service.info.manage.impl;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.joda.time.DateTime;
import org.joda.time.Days;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import com.tfit.BdBiProcSrvShEduOmc.config.AppModConfig;
import com.tfit.BdBiProcSrvShEduOmc.config.SpringConfig;
import com.tfit.BdBiProcSrvShEduOmc.constant.GlobalConstant;
import com.tfit.BdBiProcSrvShEduOmc.constant.RedisKeyConstant;
import com.tfit.BdBiProcSrvShEduOmc.constant.RegexExpressConstant;
import com.tfit.BdBiProcSrvShEduOmc.dao.domain.TEduSchoolDo;
import com.tfit.BdBiProcSrvShEduOmc.dao.domain.TProSupplierDo;
import com.tfit.BdBiProcSrvShEduOmc.dao.domain.edu.EduSchool;
import com.tfit.BdBiProcSrvShEduOmc.dao.domain.saas.EduSchoolSupplier;
import com.tfit.BdBiProcSrvShEduOmc.dto.PageInfo;
import com.tfit.BdBiProcSrvShEduOmc.dto.im.KwCommonDets;
import com.tfit.BdBiProcSrvShEduOmc.dto.im.KwCommonRecs;
import com.tfit.BdBiProcSrvShEduOmc.dto.info.manage.RecoveryWasteOilSummarySearchDTO;
import com.tfit.BdBiProcSrvShEduOmc.dto.info.manage.RmcRecoveryWasteOilSearchDTO;
import com.tfit.BdBiProcSrvShEduOmc.dto.info.manage.SchoolRecoveryWasteOilSearchDTO;
import com.tfit.BdBiProcSrvShEduOmc.dto.info.manage.WasteOilTypeCodeSearchDTO;
import com.tfit.BdBiProcSrvShEduOmc.dto.user.UserSetColums;
import com.tfit.BdBiProcSrvShEduOmc.model.enums.OwnerType;
import com.tfit.BdBiProcSrvShEduOmc.model.enums.SchoolStructType;
import com.tfit.BdBiProcSrvShEduOmc.model.enums.WasteOilType;
import com.tfit.BdBiProcSrvShEduOmc.model.export.RmcRecoveryWasteOilDetailsExport;
import com.tfit.BdBiProcSrvShEduOmc.model.export.RmcRecoveryWasteOilSummaryExport;
import com.tfit.BdBiProcSrvShEduOmc.model.export.SchoolRecoveryWasteOilDetailsExport;
import com.tfit.BdBiProcSrvShEduOmc.model.export.SchoolRecoveryWasteOilSummaryExport;
import com.tfit.BdBiProcSrvShEduOmc.model.ro.GroupMealCompanyBasicRO;
import com.tfit.BdBiProcSrvShEduOmc.model.ro.RecoveryWasteOilDetailRO;
import com.tfit.BdBiProcSrvShEduOmc.model.ro.SchoolBasicRO;
import com.tfit.BdBiProcSrvShEduOmc.model.uo.TableUO;
import com.tfit.BdBiProcSrvShEduOmc.model.vo.RecoveryWasteOilSummaryVO;
import com.tfit.BdBiProcSrvShEduOmc.model.vo.RmcRecoveryWasteOilDetailVO;
import com.tfit.BdBiProcSrvShEduOmc.model.vo.SchoolRecoveryWasteOilDetailVO;
import com.tfit.BdBiProcSrvShEduOmc.model.vo.WasteOilTypeCodeVO;
import com.tfit.BdBiProcSrvShEduOmc.model.vo.base.PagedList;
import com.tfit.BdBiProcSrvShEduOmc.service.Db1Service;
import com.tfit.BdBiProcSrvShEduOmc.service.Db2Service;
import com.tfit.BdBiProcSrvShEduOmc.service.DbHiveRecyclerWasteService;
import com.tfit.BdBiProcSrvShEduOmc.service.RedisService;
import com.tfit.BdBiProcSrvShEduOmc.service.SaasService;
import com.tfit.BdBiProcSrvShEduOmc.service.edu.EduSchoolService;
import com.tfit.BdBiProcSrvShEduOmc.service.info.manage.RecoveryWasteOilsService;
import com.tfit.BdBiProcSrvShEduOmc.service.info.manage.SchoolBasicService;
import com.tfit.BdBiProcSrvShEduOmc.service.saas.EduSchoolSupplierService;
import com.tfit.BdBiProcSrvShEduOmc.util.BCDTimeUtil;
import com.tfit.BdBiProcSrvShEduOmc.util.CommonUtil;
import com.tfit.BdBiProcSrvShEduOmc.util.DictConvertUtil;
import com.tfit.BdBiProcSrvShEduOmc.util.ExcelGenerateUtil;
import com.tfit.BdBiProcSrvShEduOmc.util.RedisValueUtil;
import com.tfit.BdBiProcSrvShEduOmc.util.UniqueIdGen;

import lombok.extern.slf4j.Slf4j;

/**
 * @Descritpion：回收废弃油脂服务实现类
 * @author: tianfang_infotech
 * @date: 2019/1/3 11:55
 */
@Slf4j
@Service
public class RecoveryWasteOilsServiceImpl implements RecoveryWasteOilsService {
    private static final Logger logger = LogManager.getLogger(RecoveryWasteOilsServiceImpl.class.getName());

    @Autowired
    private RedisService redisService;

    /**
     * 一天毫秒数(24 * 3600 * 1000);
     */
    private static final int DAY_TOTAL_MILLISECOND = 86400000;
    private static final String SPLIT_SYMBOL = "_";
    /**
     * 回收废弃油脂汇总后缀
     */
    private static final String RECOVERY_WASTE_OIL_SUMMARY_SUFFIX = "_total";
    /**
     * 循环迭代步长
     */
    private static final int ITERATE_STEP_SIZE = 2;
    /**
     * redis 回收废弃油脂详情格式长度
     */
    private static final int REDIS_RECOVERY_WASTE_OIL_DETAIL_RO_FORMAT_LENGTH = 14;
    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
    private static final SimpleDateFormat dateFormat2 = new SimpleDateFormat("yyyy/MM/dd");

    /**
     * 学校基础信息服务
     */
    @Autowired
    private SchoolBasicService schoolBasicService;

    /**
     * 教委系统学校信息服务
     */
    @Autowired
    private EduSchoolService eduSchoolService;

    /**
     * 教委学校供应商信息查询
     */
    @Autowired
    private EduSchoolSupplierService eduSchoolSupplierService;

    @Override
    public List<?> getWasteOilTypeCodes(WasteOilTypeCodeSearchDTO searchDTO) {
        return Arrays.asList(new WasteOilTypeCodeVO(WasteOilType.WASTE_OIL),
                new WasteOilTypeCodeVO(WasteOilType.OILY_WASTE_WATER));
    }

    //
    @Override
    public PagedList<?> getSchoolRecoveryWasteOilSummary(RecoveryWasteOilSummarySearchDTO searchDTO, DbHiveRecyclerWasteService dbHiveRecyclerWasteService) {
        //如无起止时间，取当天时间段
        if (Objects.isNull(searchDTO.getStartSubDate()) || Objects.isNull(searchDTO.getEndSubDate())) {
            Date date = new Date();//创建时间对象
            searchDTO.setStartSubDate(date);//开始时间
            searchDTO.setEndSubDate(date);//结束时间
        }

        /**
         * 设置查询学校回收废弃油脂
         */
        searchDTO.setSearchType(OwnerType.SCHOOL.getCode());

        return new PagedList<>(getRecoveryWasteOilsVOProcess(searchDTO, dbHiveRecyclerWasteService), searchDTO);
    }

    @Override
    public SchoolRecoveryWasteOilSummaryExport exportSchoolRecoveryWasteOilSummary(RecoveryWasteOilSummarySearchDTO searchDTO, DbHiveRecyclerWasteService dbHiveRecyclerWasteService) {
        //如无起止时间，取当天时间段
        if (Objects.isNull(searchDTO.getStartSubDate()) || Objects.isNull(searchDTO.getEndSubDate())) {
            Date date = new Date();
            searchDTO.setStartSubDate(date);
            searchDTO.setEndSubDate(date);
        }

        searchDTO.setPage(1);

        /**
         * 导出全部数据
         */
        searchDTO.setPageSize(Integer.MAX_VALUE);

        /**
         * 设置查询学校回收废弃油脂
         */
        searchDTO.setSearchType(OwnerType.SCHOOL.getCode());

        List<RecoveryWasteOilSummaryVO> result = getRecoveryWasteOilsVOProcess(searchDTO, dbHiveRecyclerWasteService);

        TableUO tableUO = generateRecoveryWasterOilSummaryTableUO(result);

        String repFileName = "/expSchRecWasteOils/" + UniqueIdGen.uuid() + SpringConfig.repFileFormats[SpringConfig.curRepFileFrmtIdx];

        ExcelGenerateUtil.generateSimpleExcelFile(repFileName, tableUO);

        SchoolRecoveryWasteOilSummaryExport export = new SchoolRecoveryWasteOilSummaryExport();

        BeanUtils.copyProperties(searchDTO, export);

        export.setExpFileUrl(SpringConfig.repfile_srvdn + repFileName);

        logger.info("=> 导出文件URL：" + export.getExpFileUrl());

        return export;
    }

    private TableUO generateRecoveryWasterOilSummaryTableUO(List<RecoveryWasteOilSummaryVO> result) {
        int columnCount = 4;
        //增加合计行(result.size() + 1)
        int rowCount = result.size() + 1;

        TableUO tableUO = new TableUO(rowCount, columnCount);

        tableUO.setColumnNames(new String[]{"回收周期", "所在地", "回收次数", "回收数量(桶)"});

        Object[][] data = new Object[rowCount][columnCount];
        for (int i = 0; i < result.size(); i++) {
            data[i][0] = result.get(i).getRecDate();
            data[i][1] = result.get(i).getDistName();
            data[i][2] = result.get(i).getReFeq();
            data[i][3] = result.get(i).getRcNum();
        }

        int lastRowIndex = rowCount - 1;
        int reFeq = 0;
        int reNum = 0;
        RecoveryWasteOilSummaryVO oilsVO;
        Iterator<RecoveryWasteOilSummaryVO> iterator = result.iterator();
        while (iterator.hasNext()) {
            oilsVO = iterator.next();
            if (Objects.isNull(oilsVO)) {
                continue;
            }
            reFeq += oilsVO.getReFeq();
            reNum += oilsVO.getRcNum();
        }
        data[lastRowIndex][0] = "合计";
        data[lastRowIndex][1] = "---";
        data[lastRowIndex][2] = reFeq;
        data[lastRowIndex][3] = reNum;

        tableUO.setDataMatrix(data);

        return tableUO;
    }

    @Override
    public PagedList<?> getSchoolRecoveryWasteOilDetails(SchoolRecoveryWasteOilSearchDTO searchDTO, Db1Service db1Service,
                                                         SaasService saasService,
                                                         DbHiveRecyclerWasteService dbHiveRecyclerWasteService) {
        //如无起止时间，取当天时间段
        if (Objects.isNull(searchDTO.getStartSubDate()) || Objects.isNull(searchDTO.getEndSubDate())) {
            Date date = new Date();
            searchDTO.setStartSubDate(date);
            searchDTO.setEndSubDate(date);
        }


        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        DateTime startDt = BCDTimeUtil.convertDateStrToDate(format.format(searchDTO.getStartSubDate()));
        DateTime currentTime = new DateTime();
        int days = Days.daysBetween(startDt, currentTime).getDays();
        //包含30天之前的数据取hive库，其余取redis
        if (days >= 2) {
            PageInfo pageInfo = new PageInfo();
            List<SchoolRecoveryWasteOilDetailVO> result = getWasteOilList(searchDTO, db1Service, saasService,
                    dbHiveRecyclerWasteService, format, pageInfo);
            return new PagedList<>(result, pageInfo);
        } else {
            return new PagedList<>(getSchoolRecoveryWasteOilDetailsProcess(searchDTO, db1Service), searchDTO);
        }
    }

    private List<SchoolRecoveryWasteOilDetailVO> getWasteOilList(SchoolRecoveryWasteOilSearchDTO searchDTO,
                                                                 Db1Service db1Service, SaasService saasService, DbHiveRecyclerWasteService dbHiveRecyclerWasteService,
                                                                 SimpleDateFormat format, PageInfo pageInfo) {
        List<SchoolRecoveryWasteOilDetailVO> result = new ArrayList<>();
        //所有学校id
        List<TEduSchoolDo> tesDoList = new ArrayList<TEduSchoolDo>();
        tesDoList = db1Service.getTEduSchoolDoListByDs1(null, null, null, 5);
        Map<String, TEduSchoolDo> schMap = tesDoList.stream().collect(Collectors.toMap(TEduSchoolDo::getId, (b) -> b));

        //团餐公司id和团餐公司名称
        Map<String, String> rmcIdToNameMap = new HashMap<>();
        List<TProSupplierDo> tpsDoList = saasService.getIdSupplierIdName();
        if (tpsDoList != null) {
            for (int i = 0; i < tpsDoList.size(); i++) {
                rmcIdToNameMap.put(tpsDoList.get(i).getId(), tpsDoList.get(i).getSupplierName());
            }
        }

        String schoolName = null;
        if (CommonUtil.isNotEmpty(searchDTO.getPpName())) {
            if (schMap.get(searchDTO.getPpName()) != null) {
                schoolName = schMap.get(searchDTO.getPpName()).getSchoolName();
            }
        }

        //如果包含30天之前数据，则去hive库中的数据，否则取redis中的数据
        String startDate = format.format(searchDTO.getStartSubDate());
        String endDate = format.format(searchDTO.getEndSubDate());
        if (startDate == null || startDate.split("-").length < 2) {
            startDate = BCDTimeUtil.convertNormalDate(null);
        }
        if ((endDate == null || endDate.split("-").length < 2) && startDate != null) {
            endDate = startDate;
        } else if (endDate == null || endDate.split("-").length < 2) {
            endDate = BCDTimeUtil.convertNormalDate(null);
        }

        String[] yearMonths = new String[4];
        //根据开始日期、结束日期，获取开始日期和结束日期的年、月
        yearMonths = CommonUtil.getYearMonthByDate(startDate, endDate);
        String startYear = yearMonths[0];
        String startMonth = yearMonths[1];
        String endYear = yearMonths[2];
        String endMonth = yearMonths[3];

        //结束日期+1天，方便查询处理
        String endDateAddOne = CommonUtil.dateAddDay(endDate, 1);
        //获取开始日期、结束日期的年月集合
        List<String> listYearMonth = CommonUtil.getYearMonthList(startYear, startMonth, endYear, endMonth);
        /**
         * 1.从hive库中获取汇总数据
         */
        List<Object> distNamesList = CommonUtil.changeStringToList(searchDTO.getDistNames());
        List<Object> subLevelsList = CommonUtil.changeStringToList(searchDTO.getSubLevels());
        List<Object> compDepsList = CommonUtil.changeStringToList(searchDTO.getCompDeps());
        List<Object> schPropsList = CommonUtil.changeStringToList(searchDTO.getSchProps());
        List<Object> schTypesList = CommonUtil.changeStringToList(searchDTO.getSchTypes());

        List<KwCommonDets> commonDets = new ArrayList<>();

        commonDets = dbHiveRecyclerWasteService.getRecyclerWasteDetsList(listYearMonth, startDate, endDateAddOne, searchDTO.getDistName() == null ? null : searchDTO.getDistName().toString(),
                schoolName, searchDTO.getSchType() == null ? -1 : searchDTO.getSchType(), searchDTO.getRmcName(), null, searchDTO.getRecCompany(), searchDTO.getRecPerson(),
                searchDTO.getSchProp() == null ? -1 : searchDTO.getSchProp(),
                StringUtils.isEmpty(searchDTO.getSubLevel()) ? -1 : Integer.parseInt(searchDTO.getSubLevel()),
                StringUtils.isEmpty(searchDTO.getCompDep()) ? -1 : Integer.parseInt(searchDTO.getCompDep()), searchDTO.getWoType(),
                distNamesList, subLevelsList, compDepsList, schPropsList, schTypesList, 1, 2, searchDTO.getPage() == null ? null : (searchDTO.getPage() - 1) * searchDTO.getPageSize(), searchDTO.getPage() == null ? null : searchDTO.getPage() * searchDTO.getPageSize());
        commonDets.removeAll(Collections.singleton(null));

        Integer pageTotalTemp = dbHiveRecyclerWasteService.getRecyclerWasteDetsCount(listYearMonth, startDate, endDateAddOne,
                searchDTO.getDistName() == null ? null : searchDTO.getDistName().toString(),
                schoolName, searchDTO.getSchType() == null ? -1 : searchDTO.getSchType(), searchDTO.getRmcName(), null, searchDTO.getRecCompany(), searchDTO.getRecPerson(),
                searchDTO.getSchProp() == null ? -1 : searchDTO.getSchProp(),
                StringUtils.isEmpty(searchDTO.getSubLevel()) ? -1 : Integer.parseInt(searchDTO.getSubLevel()),
                StringUtils.isEmpty(searchDTO.getCompDep()) ? -1 : Integer.parseInt(searchDTO.getCompDep()), searchDTO.getWoType(),
                distNamesList, subLevelsList, compDepsList, schPropsList, schTypesList, 1, 2);

        java.text.SimpleDateFormat formatter = new SimpleDateFormat("yyyy/MM/dd");
        String relGenSchName = "";
        for (KwCommonDets kwCommonDets : commonDets) {
            relGenSchName = "";
            SchoolRecoveryWasteOilDetailVO schKwDetObj = new SchoolRecoveryWasteOilDetailVO();
            try {
                schKwDetObj.setRecDate(formatter.parse(kwCommonDets.getRecDate()));
            } catch (ParseException e) {
                e.printStackTrace();
            }
            //区域转换
            schKwDetObj.setDistName(kwCommonDets.getDistName());
            if (kwCommonDets.getDistName() != null && !"".equals(kwCommonDets.getDistName())) {
                if (AppModConfig.distIdToNameMap.get(kwCommonDets.getDistName()) != null) {
                    schKwDetObj.setDistName(AppModConfig.distIdToNameMap.get(kwCommonDets.getDistName()));
                }
            }
            schKwDetObj.setRmcName(kwCommonDets.getRmcName());
            schKwDetObj.setWoType(kwCommonDets.getWoType());
            schKwDetObj.setRecNum(kwCommonDets.getRecNum());
            schKwDetObj.setRecCompany(kwCommonDets.getRecComany());
            schKwDetObj.setRecPerson(kwCommonDets.getRecPerson());
            schKwDetObj.setRecBillNum(kwCommonDets.getRecBillNum());

            schKwDetObj.setPpName(kwCommonDets.getPpName());
            schKwDetObj.setSchGenBraFlag(kwCommonDets.getSchGenBraFlag());
            schKwDetObj.setBraCampusNum(kwCommonDets.getBraCampusNum());
            schKwDetObj.setSubLevel(kwCommonDets.getSubLevel());
            schKwDetObj.setCompDep(kwCommonDets.getCompDep());
            schKwDetObj.setSubDistName(kwCommonDets.getSubDistName());
            schKwDetObj.setSchType(kwCommonDets.getSchType());
            schKwDetObj.setRmcName("-");
            if (CommonUtil.isNotEmpty(kwCommonDets.getSchSupplierId())) {
                if (rmcIdToNameMap.get(kwCommonDets.getSchSupplierId()) != null) {
                    schKwDetObj.setRmcName(rmcIdToNameMap.get(kwCommonDets.getSchSupplierId()));
                }
            }
            schKwDetObj.setSchProp(kwCommonDets.getSchProp());

            //关联学校
            if (CommonUtil.isNotEmpty(schKwDetObj.getRelGenSchName()) && !"-".equals(schKwDetObj.getRelGenSchName())) {
                if (schMap.get(schKwDetObj.getRelGenSchName()) != null) {
                    relGenSchName = schMap.get(schKwDetObj.getRelGenSchName()).getSchoolName();
                }
            }

            if (CommonUtil.isNotEmpty(relGenSchName)) {
                schKwDetObj.setRelGenSchName(relGenSchName);
            } else {
                schKwDetObj.setRelGenSchName("-");
            }


            result.add(schKwDetObj);
        }

        if (pageTotalTemp != null) {
            pageInfo.setPageTotal(pageTotalTemp);
        } else {
            pageInfo.setPageTotal(0);
        }

        pageInfo.setCurPageNum(searchDTO.getPage());
        return result;
    }

    @Override
    public SchoolRecoveryWasteOilDetailsExport exportSchoolRecoveryWasteOilDetails(String token, SchoolRecoveryWasteOilSearchDTO searchDTO,
                                                                                   Db1Service db1Service, SaasService saasService, Db2Service db2Service, DbHiveRecyclerWasteService dbHiveRecyclerWasteService) {
        //如无起止时间，取当天时间段
        if (Objects.isNull(searchDTO.getStartSubDate()) || Objects.isNull(searchDTO.getEndSubDate())) {
            Date date = new Date();
            searchDTO.setStartSubDate(date);
            searchDTO.setEndSubDate(date);
        }

        searchDTO.setPage(1);

        /**
         * 导出全部数据
         */
        searchDTO.setPageSize(Integer.MAX_VALUE);

        /**
         * 设置查询学校回收废弃油脂
         */
        searchDTO.setSearchType(OwnerType.SCHOOL.getCode());
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        DateTime startDt = BCDTimeUtil.convertDateStrToDate(format.format(searchDTO.getStartSubDate()));
        DateTime currentTime = new DateTime();
        int days = Days.daysBetween(startDt, currentTime).getDays();
        //包含30天之前的数据取hive库，其余取redis
        List<SchoolRecoveryWasteOilDetailVO> result = new ArrayList<>();
        if (days >= 2) {
            PageInfo pageInfo = new PageInfo();
            searchDTO.setPage(null);
            searchDTO.setPageSize(null);
            result = getWasteOilList(searchDTO, db1Service, saasService,
                    dbHiveRecyclerWasteService, format, pageInfo);
        } else {
            result = getSchoolRecoveryWasteOilDetailsProcess(searchDTO, db1Service);
        }

        TableUO tableUO = generateSchoolRecoveryWasterOilDetailTableUO(token, result, db2Service);

        String repFileName = "/expSchWasteOilDets/" + UniqueIdGen.uuid() + SpringConfig.repFileFormats[SpringConfig.curRepFileFrmtIdx];

        ExcelGenerateUtil.generateSimpleExcelFile(repFileName, tableUO);

        SchoolRecoveryWasteOilDetailsExport export = new SchoolRecoveryWasteOilDetailsExport();

        BeanUtils.copyProperties(searchDTO, export);

        export.setExpFileUrl(SpringConfig.repfile_srvdn + repFileName);

        logger.info("=> 导出文件URL：" + export.getExpFileUrl());

        return export;
    }

    private TableUO generateSchoolRecoveryWasterOilDetailTableUO(String token, List<SchoolRecoveryWasteOilDetailVO> result, Db2Service db2Service) {

        String[] colNames = new String[]{"序号", "回收日期", "项目点", "总校/分校", "分校数量",
                "关联总校", "所在地", "学制", "办学性质",
                "团餐公司", "种类", "数量（公斤）", "回收单位", "回收人", "回收单据"};
        List<UserSetColums> userSetColumsList = CommonUtil.getUserSetColumList(token, "schWasteOilDets", db2Service);
        //动态列
        if (userSetColumsList != null && userSetColumsList.size() > 0) {
            List<String> colNamesTempList = new ArrayList<String>(); //重新设置列
            for (UserSetColums obj : userSetColumsList) {
                if (obj != null && obj.isChecked()) {
                    colNamesTempList.add(obj.getLabel());
                }
            }
            colNames = colNamesTempList.toArray(new String[colNamesTempList.size()]);
        }

        int columnCount = colNames.length;
        TableUO tableUO = new TableUO(result.size(), columnCount);

        tableUO.setColumnNames(colNames);

        Object[][] data = new Object[result.size()][columnCount];
        int startColumnIdx = 0;
        for (int i = 0; i < result.size(); i++) {
            startColumnIdx = 0;
            if (userSetColumsList != null && userSetColumsList.size() > 0) {
                for (UserSetColums obj : userSetColumsList) {
                    if (obj != null && obj.isChecked()) {
                        if ("sortNo".equals(obj.getKey()))
                            data[i][startColumnIdx++] = i + 1;    //序号
                        if ("recDate".equals(obj.getKey()))
                            data[i][startColumnIdx++] = dateFormat2.format(result.get(i).getRecDate());
                        if ("ppName".equals(obj.getKey()))
                            data[i][startColumnIdx++] = result.get(i).getPpName();
                        if ("schGenBraFlag".equals(obj.getKey()))
                            data[i][startColumnIdx++] = result.get(i).getSchGenBraFlag();
                        if ("braCampusNum".equals(obj.getKey()))
                            data[i][startColumnIdx++] = result.get(i).getBraCampusNum();
                        if ("relGenSchName".equals(obj.getKey()))
                            data[i][startColumnIdx++] = result.get(i).getRelGenSchName();
                        if ("distName".equals(obj.getKey()))
                            data[i][startColumnIdx++] = result.get(i).getDistName();
                        if ("schType".equals(obj.getKey()))
                            data[i][startColumnIdx++] = result.get(i).getSchType();
                        if ("schProp".equals(obj.getKey()))
                            data[i][startColumnIdx++] = result.get(i).getSchProp();
                        if ("rmcName".equals(obj.getKey()))
                            data[i][startColumnIdx++] = result.get(i).getRmcName();
                        if ("woType".equals(obj.getKey()))
                            data[i][startColumnIdx++] = result.get(i).getWoType();
                        if ("recNum".equals(obj.getKey()))
                            data[i][startColumnIdx++] = result.get(i).getRecNum();
                        if ("recCompany".equals(obj.getKey()))
                            data[i][startColumnIdx++] = result.get(i).getRecCompany();
                        if ("recPerson".equals(obj.getKey()))
                            data[i][startColumnIdx++] = result.get(i).getRecPerson();
                        if ("recBillNum".equals(obj.getKey()))
                            data[i][startColumnIdx++] = result.get(i).getRecBillNum();

                    }
                }
            } else {

                //序号
                data[i][0] = i + 1;
                //回收日期
                data[i][1] = dateFormat2.format(result.get(i).getRecDate());
                //项目点
                data[i][2] = result.get(i).getPpName();
                //总校/分校
                data[i][3] = result.get(i).getSchGenBraFlag();
                //分校数量
                data[i][4] = result.get(i).getBraCampusNum();
                //关联总校
                data[i][5] = result.get(i).getRelGenSchName();
	            /*//所属
	            data[i][5] = result.get(i).getSubLevel();
	            //主管部门
	            data[i][6] = result.get(i).getCompDep();
	           //所属区
	            data[i][7] = result.get(i).getSubDistName();*/
                //所在地
                data[i][6] = result.get(i).getDistName();
                //学制
                data[i][7] = result.get(i).getSchType();
                //办学性质
                data[i][8] = result.get(i).getSchProp();
                //团餐公司
                data[i][9] = result.get(i).getRmcName();
                //种类
                data[i][10] = result.get(i).getWoType();
                //数量（公斤）
                data[i][11] = result.get(i).getRecNum();
                //回收单位
                data[i][12] = result.get(i).getRecCompany();
                //回收人
                data[i][13] = result.get(i).getRecPerson();
                //回收单据
                data[i][14] = result.get(i).getRecBillNum();
            }
        }
        tableUO.setDataMatrix(data);
        return tableUO;
    }

    @Override
    public PagedList<?> getRmcRecoveryWasteOilSummary(RecoveryWasteOilSummarySearchDTO searchDTO, DbHiveRecyclerWasteService dbHiveRecyclerWasteService) {
        //如无起止时间，取当天时间段
        if (Objects.isNull(searchDTO.getStartSubDate()) || Objects.isNull(searchDTO.getEndSubDate())) {
            Date date = new Date();
            searchDTO.setStartSubDate(date);
            searchDTO.setEndSubDate(date);
        }

        /**
         * 设置查询团餐公司回收废弃油脂
         */
        searchDTO.setSearchType(OwnerType.RMC.getCode());

        return new PagedList<>(getRecoveryWasteOilsVOProcess(searchDTO, dbHiveRecyclerWasteService), searchDTO);
    }

    @Override
    public RmcRecoveryWasteOilSummaryExport exportRmcRecoveryWasteOilSummary(RecoveryWasteOilSummarySearchDTO searchDTO, DbHiveRecyclerWasteService dbHiveRecyclerWasteService) {
        //如无起止时间，取当天时间段
        if (Objects.isNull(searchDTO.getStartSubDate()) || Objects.isNull(searchDTO.getEndSubDate())) {
            Date date = new Date();
            searchDTO.setStartSubDate(date);
            searchDTO.setEndSubDate(date);
        }

        searchDTO.setPage(1);

        /**
         * 导出全部数据
         */
        searchDTO.setPageSize(Integer.MAX_VALUE);

        /**
         * 设置查询学校回收废弃油脂
         */
        searchDTO.setSearchType(OwnerType.RMC.getCode());

        List<RecoveryWasteOilSummaryVO> result = getRecoveryWasteOilsVOProcess(searchDTO, dbHiveRecyclerWasteService);

        TableUO tableUO = generateRecoveryWasterOilSummaryTableUO(result);

        String repFileName = "/expRmcRecWasteOils/" + UniqueIdGen.uuid() + SpringConfig.repFileFormats[SpringConfig.curRepFileFrmtIdx];

        ExcelGenerateUtil.generateSimpleExcelFile(repFileName, tableUO);

        RmcRecoveryWasteOilSummaryExport export = new RmcRecoveryWasteOilSummaryExport();

        BeanUtils.copyProperties(searchDTO, export);

        export.setExpFileUrl(SpringConfig.repfile_srvdn + repFileName);

        logger.info("=> 导出文件URL：" + export.getExpFileUrl());

        return export;
    }

    @Override
    public PagedList<?> getRmcRecoveryWasteOilDetails(RmcRecoveryWasteOilSearchDTO searchDTO,
                                                      SaasService saasService,
                                                      DbHiveRecyclerWasteService dbHiveRecyclerWasteService) {
        //如无起止时间，取当天时间段
        if (Objects.isNull(searchDTO.getStartSubDate()) || Objects.isNull(searchDTO.getEndSubDate())) {
            Date date = new Date();
            searchDTO.setStartSubDate(date);
            searchDTO.setEndSubDate(date);
        }

        /**
         * 设置查询团餐公司回收废弃油脂
         */
        searchDTO.setSearchType(OwnerType.RMC.getCode());

        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        DateTime startDt = BCDTimeUtil.convertDateStrToDate(format.format(searchDTO.getStartSubDate()));
        DateTime currentTime = new DateTime();
        int days = Days.daysBetween(startDt, currentTime).getDays();
        //包含30天之前的数据取hive库，其余取redis
        if (days >= 2) {
            PageInfo pageInfo = new PageInfo();
            List<RmcRecoveryWasteOilDetailVO> result = getRmcWasteOilList(searchDTO, saasService,
                    dbHiveRecyclerWasteService, format, pageInfo);
            return new PagedList<>(result, pageInfo);
        } else {
            return new PagedList<>(getRmcRecoveryWasteOilDetailsProcess(searchDTO), searchDTO);
        }

    }

    private List<RmcRecoveryWasteOilDetailVO> getRmcWasteOilList(RmcRecoveryWasteOilSearchDTO searchDTO,
                                                                 SaasService saasService, DbHiveRecyclerWasteService dbHiveRecyclerWasteService, SimpleDateFormat format,
                                                                 PageInfo pageInfo) {
        //团餐公司id和团餐公司名称
        Map<String, String> rmcIdToNameMap = new HashMap<>();
        List<TProSupplierDo> tpsDoList = saasService.getIdSupplierIdName();
        if (tpsDoList != null) {
            for (int i = 0; i < tpsDoList.size(); i++) {
                rmcIdToNameMap.put(tpsDoList.get(i).getId(), tpsDoList.get(i).getSupplierName());
            }
        }
        String rmcNameNew = null;
        if (CommonUtil.isNotEmpty(searchDTO.getRmcName())) {
            rmcNameNew = rmcIdToNameMap.get(searchDTO.getRmcName());
        }

        //如果包含30天之前数据，则去hive库中的数据，否则取redis中的数据
        String startDate = format.format(searchDTO.getStartSubDate());
        String endDate = format.format(searchDTO.getEndSubDate());
        if (startDate == null || startDate.split("-").length < 2) {
            startDate = BCDTimeUtil.convertNormalDate(null);
        }
        if ((endDate == null || endDate.split("-").length < 2) && startDate != null) {
            endDate = startDate;
        } else if (endDate == null || endDate.split("-").length < 2) {
            endDate = BCDTimeUtil.convertNormalDate(null);
        }

        String[] yearMonths = new String[4];
        //根据开始日期、结束日期，获取开始日期和结束日期的年、月
        yearMonths = CommonUtil.getYearMonthByDate(startDate, endDate);
        String startYear = yearMonths[0];
        String startMonth = yearMonths[1];
        String endYear = yearMonths[2];
        String endMonth = yearMonths[3];

        //结束日期+1天，方便查询处理
        String endDateAddOne = CommonUtil.dateAddDay(endDate, 1);
        //获取开始日期、结束日期的年月集合
        List<String> listYearMonth = CommonUtil.getYearMonthList(startYear, startMonth, endYear, endMonth);
        /**
         * 1.从hive库中获取汇总数据
         */
        List<Object> distNamesList = CommonUtil.changeStringToList(searchDTO.getDistNames());
        List<KwCommonDets> commonDets = new ArrayList<>();
        commonDets = dbHiveRecyclerWasteService.getRecyclerWasteDetsList(listYearMonth, startDate, endDateAddOne, searchDTO.getDistName() == null ? null : searchDTO.getDistName().toString(),
                null, -1, null, rmcNameNew, searchDTO.getRecCompany(), searchDTO.getRecPerson(), -1, -1, -1, searchDTO.getWoType(),
                distNamesList, null, null, null, null, 2, 2, searchDTO.getPage() == null ? null : (searchDTO.getPage() - 1) * searchDTO.getPageSize(), searchDTO.getPage() == null ? null : searchDTO.getPage() * searchDTO.getPageSize());
        //移除所有未null的值
        commonDets.removeAll(Collections.singleton(null));
        Integer pageTotalTemp = dbHiveRecyclerWasteService.getRecyclerWasteDetsCount(listYearMonth, startDate, endDateAddOne, searchDTO.getDistName() == null ? null : searchDTO.getDistName().toString(),
                null, -1, null, rmcNameNew, searchDTO.getRecCompany(), searchDTO.getRecPerson(), -1, -1, -1, searchDTO.getWoType(),
                distNamesList, null, null, null, null, 2, 2);
        List<RmcRecoveryWasteOilDetailVO> result = new ArrayList<>();
        java.text.SimpleDateFormat formatter = new SimpleDateFormat("yyyy/MM/dd");
        for (KwCommonDets kwCommonDets : commonDets) {
            RmcRecoveryWasteOilDetailVO schKwDetObj = new RmcRecoveryWasteOilDetailVO();
            try {
                schKwDetObj.setRecDate(formatter.parse(kwCommonDets.getRecDate()));
            } catch (ParseException e) {
                e.printStackTrace();
            }
            //区域转换
            schKwDetObj.setDistName(kwCommonDets.getDistName());
            if (kwCommonDets.getDistName() != null && !"".equals(kwCommonDets.getDistName())) {
                if (AppModConfig.distIdToNameMap.get(kwCommonDets.getDistName()) != null) {
                    schKwDetObj.setDistName(AppModConfig.distIdToNameMap.get(kwCommonDets.getDistName()));
                }
            }
            schKwDetObj.setRmcName(kwCommonDets.getRmcName());
            schKwDetObj.setWoType(kwCommonDets.getWoType());
            schKwDetObj.setRecNum(kwCommonDets.getRecNum());
            schKwDetObj.setRecCompany(kwCommonDets.getRecComany());
            schKwDetObj.setRecPerson(kwCommonDets.getRecPerson());
            schKwDetObj.setRecBillNum(kwCommonDets.getRecBillNum());

            result.add(schKwDetObj);
        }

        if (pageTotalTemp != null) {
            pageInfo.setPageTotal(pageTotalTemp);
        } else {
            pageInfo.setPageTotal(0);
        }

        pageInfo.setCurPageNum(searchDTO.getPage());
        return result;
    }

    @Override
    public RmcRecoveryWasteOilDetailsExport exportRmcRecoveryWasteOilDetails(String token, RmcRecoveryWasteOilSearchDTO searchDTO,
                                                                             SaasService saasService, Db2Service db2Service, DbHiveRecyclerWasteService dbHiveRecyclerWasteService) {
        //如无起止时间，取当天时间段
        if (Objects.isNull(searchDTO.getStartSubDate()) || Objects.isNull(searchDTO.getEndSubDate())) {
            Date date = new Date();
            searchDTO.setStartSubDate(date);
            searchDTO.setEndSubDate(date);
        }

        searchDTO.setPage(1);

        /**
         * 导出全部数据
         */
        searchDTO.setPageSize(Integer.MAX_VALUE);

        /**
         * 设置查询学校回收废弃油脂
         */
        searchDTO.setSearchType(OwnerType.RMC.getCode());
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        DateTime startDt = BCDTimeUtil.convertDateStrToDate(format.format(searchDTO.getStartSubDate()));
        DateTime currentTime = new DateTime();
        int days = Days.daysBetween(startDt, currentTime).getDays();
        //包含30天之前的数据取hive库，其余取redis
        List<RmcRecoveryWasteOilDetailVO> result = new ArrayList<>();
        if (days >= 2) {
            PageInfo pageInfo = new PageInfo();
            searchDTO.setPage(null);
            searchDTO.setPageSize(null);
            result = getRmcWasteOilList(searchDTO, saasService,
                    dbHiveRecyclerWasteService, format, pageInfo);
        } else {
            result = getRmcRecoveryWasteOilDetailsProcess(searchDTO);
        }

        TableUO tableUO = generateRmcRecoveryWasterOilDetailTableUO(token, result, db2Service);

        String repFileName = "/expRmcWasteOilDets/" + UniqueIdGen.uuid() + SpringConfig.repFileFormats[SpringConfig.curRepFileFrmtIdx];

        ExcelGenerateUtil.generateSimpleExcelFile(repFileName, tableUO);

        RmcRecoveryWasteOilDetailsExport export = new RmcRecoveryWasteOilDetailsExport();

        BeanUtils.copyProperties(searchDTO, export);

        export.setExpFileUrl(SpringConfig.repfile_srvdn + repFileName);

        logger.info("=> 导出文件URL：" + export.getExpFileUrl());

        return export;
    }

    private TableUO generateRmcRecoveryWasterOilDetailTableUO(String token, List<RmcRecoveryWasteOilDetailVO> result, Db2Service db2Service) {
        String[] colNames = new String[]{"序号", "回收日期", "所在地", "种类", "团餐公司", "数量(公斤)", "回收单位", "回收人", "回收单据"};
        List<UserSetColums> userSetColumsList = CommonUtil.getUserSetColumList(token, "rmcWasteOilDets", db2Service);
        //动态列
        if (userSetColumsList != null && userSetColumsList.size() > 0) {
            List<String> colNamesTempList = new ArrayList<String>(); //重新设置列
            for (UserSetColums obj : userSetColumsList) {
                if (obj != null && obj.isChecked()) {
                    colNamesTempList.add(obj.getLabel());
                }
            }
            colNames = colNamesTempList.toArray(new String[colNamesTempList.size()]);
        }

        int columnCount = colNames.length;
        TableUO tableUO = new TableUO(result.size(), columnCount);
        tableUO.setColumnNames(colNames);

        Object[][] data = new Object[result.size()][columnCount];
        int startColumnIdx = 0;
        for (int i = 0; i < result.size(); i++) {
            startColumnIdx = 0;
            if (userSetColumsList != null && userSetColumsList.size() > 0) {
                for (UserSetColums obj : userSetColumsList) {
                    if (obj != null && obj.isChecked()) {
                        if ("sortNo".equals(obj.getKey()))
                            data[i][startColumnIdx++] = i + 1;    //序号
                        if ("recDate".equals(obj.getKey()))
                            data[i][startColumnIdx++] = dateFormat2.format(result.get(i).getRecDate());
                        if ("distName".equals(obj.getKey()))
                            data[i][startColumnIdx++] = result.get(i).getDistName();
                        if ("woType".equals(obj.getKey()))
                            data[i][startColumnIdx++] = result.get(i).getWoType();
                        if ("rmcName".equals(obj.getKey()))
                            data[i][startColumnIdx++] = result.get(i).getRmcName();
                        if ("recNum".equals(obj.getKey()))
                            data[i][startColumnIdx++] = result.get(i).getRecNum();
                        if ("recCompany".equals(obj.getKey()))
                            data[i][startColumnIdx++] = result.get(i).getRecCompany();
                        if ("recPerson".equals(obj.getKey()))
                            data[i][startColumnIdx++] = result.get(i).getRecPerson();
                        if ("recBillNum".equals(obj.getKey()))
                            data[i][startColumnIdx++] = result.get(i).getRecBillNum();

                    }
                }
            } else {
                //序号
                data[i][0] = i + 1;
                //回收日期
                data[i][1] = dateFormat2.format(result.get(i).getRecDate());
                //区
                data[i][2] = result.get(i).getDistName();
                //种类
                data[i][3] = result.get(i).getWoType();
                //团餐公司
                data[i][4] = result.get(i).getRmcName();
                //数量(桶)
                data[i][5] = result.get(i).getRecNum();
                //回收单位
                data[i][6] = result.get(i).getRecCompany();
                //回收人
                data[i][7] = result.get(i).getRecPerson();
                //回收单据
                data[i][8] = result.get(i).getRecBillNum();
            }
        }
        tableUO.setDataMatrix(data);

        return tableUO;
    }

    private List<RecoveryWasteOilSummaryVO> getRecoveryWasteOilsVOProcess(RecoveryWasteOilSummarySearchDTO searchDTO, DbHiveRecyclerWasteService dbHiveRecyclerWasteService) {
        List<RecoveryWasteOilSummaryVO> aggregationResult = new ArrayList<>();

        List<RecoveryWasteOilSummaryVO> result = getDailyRecoveryWasteOilTotalList(searchDTO, dbHiveRecyclerWasteService);
        //key：区 key：废弃油脂数据
        Map<String, List<RecoveryWasteOilSummaryVO>> groupResult = result.stream().collect(Collectors.groupingBy(RecoveryWasteOilSummaryVO::getDistName));
        //合并数据
        for (String key : groupResult.keySet()) {
            RecoveryWasteOilSummaryVO oilsVO = new RecoveryWasteOilSummaryVO();

            Integer reFeq = 0;
            Float rcNum = Float.valueOf("0");
            Iterator<RecoveryWasteOilSummaryVO> iterator = groupResult.get(key).iterator();
            while (iterator.hasNext()) {
                RecoveryWasteOilSummaryVO item = iterator.next();
                reFeq += item.getReFeq();
                rcNum += item.getRcNum();
            }
            oilsVO.setReFeq(reFeq);
            oilsVO.setRcNum(new BigDecimal(rcNum).setScale(2, BigDecimal.ROUND_HALF_UP).floatValue());

            oilsVO.setDistName(key);
            oilsVO.setRecDate(String.format("%s-%s", dateFormat2.format(searchDTO.getStartSubDate()), dateFormat2.format(searchDTO.getEndSubDate())));

            aggregationResult.add(oilsVO);
        }

        Collections.sort(aggregationResult, (o1, o2) -> {
            //回收频次降序
            int i = o2.getReFeq().compareTo(o1.getReFeq());
            if (i == 0) {
                //回收数量降序
                return o2.getRcNum().compareTo(o1.getRcNum());
            }
            return i;
        });

        return aggregationResult;
    }

    /**
     * 获取学校回收废弃油脂详细列表信息
     *
     * @param searchDTO
     * @return
     */
    private List<SchoolRecoveryWasteOilDetailVO> getSchoolRecoveryWasteOilDetailsProcess(SchoolRecoveryWasteOilSearchDTO searchDTO,
                                                                                         Db1Service db1Service) {

        List<SchoolRecoveryWasteOilDetailVO> result = new ArrayList<>();

        //所有学校
        List<EduSchool> eduSchools = eduSchoolService.getEduSchools();
        //所有学校团餐公司(供应商)
        List<EduSchoolSupplier> eduSchoolSuppliers = eduSchoolSupplierService.getAllSchoolSuppliers();

        String typeRedisKey = resolveRedisKeyForWasteOilDetail(searchDTO.getSearchType());

        Map<String, Date> keyMap = genRedisKey(typeRedisKey, searchDTO.getStartSubDate(), searchDTO.getEndSubDate());

        List<RecoveryWasteOilDetailRO> detailROList = resolveSchoolRecoveryWasteOilDetailROList(keyMap);

        //过滤出学校基础信息查询条件的其他条件的过滤
        List<RecoveryWasteOilDetailRO> filteredDetailROList = filterSchoolRecoveryWasteOilDetailROList(detailROList, eduSchoolSuppliers, searchDTO);

        List<Object> distNamesList = CommonUtil.changeStringToList(searchDTO.getDistNames());
        Map<String, Integer> schIdMap = new HashMap<>();
        List<TEduSchoolDo> tesDoList = new ArrayList<TEduSchoolDo>();
        if (searchDTO.getDistName() != null) {
            tesDoList = db1Service.getTEduSchoolDoListByDs1(searchDTO.getDistName().toString(), null, null, 5);
        } else {
            tesDoList = db1Service.getTEduSchoolDoListByDs1(distNamesList, null, null);
        }
        for (int i = 0; i < tesDoList.size(); i++) {
            schIdMap.put(tesDoList.get(i).getId(), i + 1);
        }

        boolean okFlag;
        Map<String, TEduSchoolDo> schIdSchoolMap = new HashMap<>();
        for (int i = 0; i < tesDoList.size(); i++) {
            schIdSchoolMap.put(tesDoList.get(i).getId(), tesDoList.get(i));
        }
        for (RecoveryWasteOilDetailRO detailRO : filteredDetailROList) {
            TEduSchoolDo schoolDo = schIdSchoolMap.get(detailRO.getSchoolId());

            SchoolBasicRO schoolBasicRO = new SchoolBasicRO();
            BeanUtils.copyProperties(schoolDo, schoolBasicRO);
            schoolBasicRO.setDepartmentMasterId(CommonUtil.isEmpty(schoolDo.getDepartmentMasterId()) ? 0 : Integer.valueOf(schoolDo.getDepartmentMasterId()));
            schoolBasicRO.setLevel((CommonUtil.isEmpty(schoolDo.getLevel()) || !CommonUtil.isInteger(schoolDo.getLevel())) ? 0 : Integer.valueOf(schoolDo.getLevel()));
            schoolBasicRO.setArea((CommonUtil.isEmpty(schoolDo.getArea()) || !CommonUtil.isInteger(schoolDo.getArea())) ? 0 : Integer.valueOf(schoolDo.getArea()));
            if (Objects.isNull(schoolBasicRO)) {
                logger.info("School=> id={} 不存在学校基础信息，匹配失败。", detailRO.getSchoolId());
                continue;
            }

            if (CommonUtil.isEmpty(String.valueOf(detailRO.getArea()))) {
                detailRO.setArea(schoolBasicRO.getArea());
            }

            //过滤学校相关查询条件
            okFlag = filterSchoolBasicRO(schoolBasicRO, searchDTO);

            //不满足过滤条件
            if (!okFlag) {
                continue;
            }

            SchoolRecoveryWasteOilDetailVO detailVO = new SchoolRecoveryWasteOilDetailVO();
            //回收日期
            detailVO.setRecDate(detailRO.getRecDate());
            //回收单位
            detailVO.setRecCompany(detailRO.getReceiverName());
            //回收人
            detailVO.setRecPerson(detailRO.getContact());
            //区域信息
            detailVO.setDistName(DictConvertUtil.mapToDistName(String.valueOf(detailRO.getArea())));
            //回收单据数
            detailVO.setRecBillNum(detailRO.getDocuments());
            //回收数量
            detailVO.setRecNum(detailRO.getNumber());
            //废弃油脂类型
            detailVO.setWoType(DictConvertUtil.mapToWasteOilTypeName(detailRO.getSecondType()));

            //项目点
            detailVO.setPpName(schoolBasicRO.getSchoolName());
            //总校/分校信息
            if (Integer.valueOf(SchoolStructType.BRANCH_SCHOOL.getCode()).equals(schoolBasicRO.getIsBranchSchool())) {
                //分校数量
                long subSchoolCount = eduSchools.stream().filter(s -> null != s.getParentId() && s.getParentId().equals(detailRO.getSchoolId())).count();
                detailVO.setBraCampusNum((int) subSchoolCount);
                detailVO.setSchGenBraFlag(SchoolStructType.BRANCH_SCHOOL.getName());
            } else {
                detailVO.setBraCampusNum(0);
                detailVO.setSchGenBraFlag(SchoolStructType.GENERAL_SCHOOL.getName());
            }

            //关联总校
            detailVO.setRelGenSchName(GlobalConstant.STRING_EMPTY_DISPLAY_DEFAULT);
            if (!StringUtils.isEmpty(schoolBasicRO.getParentId())) {
                //关联总校RO对象
                SchoolBasicRO subSchoolBasicRO = schoolBasicService.getSchoolBasicFromRedis(schoolBasicRO.getParentId());
                if (!Objects.isNull(subSchoolBasicRO) && !StringUtils.isEmpty(subSchoolBasicRO.getSchoolName())) {
                    detailVO.setRelGenSchName(subSchoolBasicRO.getSchoolName());
                }
            }

            //所属，0:其他，1:部属，2:市属，3: 区属
            detailVO.setSubLevel(DictConvertUtil.mapToSubLevelName(schoolBasicRO.getDepartmentMasterId()));
            //主管部门
            detailVO.setCompDep(GlobalConstant.STRING_EMPTY_DISPLAY_DEFAULT);
            String compDep = getCompDep(schoolBasicRO);
            if (!StringUtils.isEmpty(compDep)) {
                detailVO.setCompDep(compDep);
            }

            //所属区域名称

            TEduSchoolDo tesDo = null;
            if (schIdMap.containsKey(detailRO.getSchoolId())) {
                int j = schIdMap.get(detailRO.getSchoolId());
                tesDo = tesDoList.get(j - 1);
            }
            //if(tesDo == null)
            //	continue;

            detailVO.setSubDistName("-");
            if (tesDo != null && tesDo.getSchoolAreaId() != null)
                detailVO.setSubDistName(AppModConfig.distIdToNameMap.get(tesDo.getSchoolAreaId()));

            //学制
            detailVO.setSchType(DictConvertUtil.mapToSchTypeName(schoolBasicRO.getLevel()));
            //办学性质
            detailVO.setSchProp(AppModConfig.getSchProp(schoolBasicRO.getSchoolNature()));
            //团餐公司
            detailVO.setRmcName(GlobalConstant.STRING_EMPTY_DISPLAY_DEFAULT);
            List<String> schoolSupplierNames = eduSchoolSuppliers.stream().filter(s -> s.getSchoolId().equalsIgnoreCase(detailRO.getSchoolId())).map(s -> s.getSupplierName()).collect(Collectors.toList());
            if (!CollectionUtils.isEmpty(schoolSupplierNames)) {
                detailVO.setRmcName(schoolSupplierNames.get(0));
            }

            result.add(detailVO);
        }

        Collections.sort(result, (o1, o2) -> {
            //所在区域升序
            int i = o1.getDistName().compareTo(o2.getDistName());
            if (i == 0) {
                //学校类型升序
                if (CommonUtil.isEmpty(o1.getSchType())) {
                    return -1;
                }
                if (CommonUtil.isEmpty(o1.getSchType())) {
                    return 1;
                }
                return o1.getSchType().compareTo(o2.getSchType());
            }
            return i;
        });

        return result;
    }

    /**
     * 获取学校回收废弃油脂详细列表信息
     *
     * @param searchDTO
     * @return
     */
    private List<RmcRecoveryWasteOilDetailVO> getRmcRecoveryWasteOilDetailsProcess(RmcRecoveryWasteOilSearchDTO searchDTO) {

        List<RmcRecoveryWasteOilDetailVO> result = new ArrayList<>();

        String typeRedisKey = resolveRedisKeyForWasteOilDetail(searchDTO.getSearchType());

        Map<String, Date> keyMap = genRedisKey(typeRedisKey, searchDTO.getStartSubDate(), searchDTO.getEndSubDate());

        List<RecoveryWasteOilDetailRO> detailROList = resolveSchoolRecoveryWasteOilDetailROList(keyMap);

        List<RecoveryWasteOilDetailRO> filteredDetailROList = filterRmcRecoveryWasteOilDetailROList(detailROList, searchDTO);

        //加载所有供应商信息
        Map<String, String> supplierIdNameMap = eduSchoolSupplierService.getAllSupplierBasics()
                .stream().collect(Collectors.toMap(s -> s.getSupplierId(), e -> e.getSupplierName()));

        for (RecoveryWasteOilDetailRO detailRO : filteredDetailROList) {
            String supplierName = supplierIdNameMap.get(detailRO.getSchoolId());
            if (Objects.isNull(supplierName)) {
                logger.info("GroupMealCompany=> id={} 不存在团餐公司基础信息，匹配失败。", detailRO.getSchoolId());
                continue;
            }

            RmcRecoveryWasteOilDetailVO detailVO = new RmcRecoveryWasteOilDetailVO();
            //回收日期
            detailVO.setRecDate(detailRO.getRecDate());
            //回收单位
            detailVO.setRecCompany(detailRO.getReceiverName());
            //回收人
            detailVO.setRecPerson(detailRO.getContact());
            //区域信息
            detailVO.setDistName(DictConvertUtil.mapToDistName(String.valueOf(detailRO.getArea())));
            //回收单据数
            detailVO.setRecBillNum(detailRO.getDocuments());
            //回收数量
            detailVO.setRecNum(detailRO.getNumber());
            //废弃油脂类型
            detailVO.setWoType(DictConvertUtil.mapToWasteOilTypeName(detailRO.getSecondType()));
            //团餐公司
            detailVO.setRmcName(supplierName);

            result.add(detailVO);
        }

        Collections.sort(result, (o1, o2) -> {
            //回收日期降序
            int i = o2.getRecDate().compareTo(o1.getRecDate());
            if (i == 0) {
                //团餐公司名称升序
                return o1.getRmcName().compareTo(o2.getRmcName());
            }
            return i;
        });

        return result;
    }

    /**
     * 检索过滤 Redis 学校回收油脂详情
     *
     * @param detailROList
     * @param searchDTO
     * @return
     */
    private List<RecoveryWasteOilDetailRO> filterSchoolRecoveryWasteOilDetailROList(List<RecoveryWasteOilDetailRO> detailROList,
                                                                                    List<EduSchoolSupplier> eduSchoolSuppliers, SchoolRecoveryWasteOilSearchDTO searchDTO) {

        List<RecoveryWasteOilDetailRO> detailList = new ArrayList<>();

        if (CollectionUtils.isEmpty(detailROList)) {
            return detailList;
        }

        boolean okFlag;
        for (RecoveryWasteOilDetailRO detailRO : detailROList) {
            okFlag = filterSchoolRecoveryWasteOilDetailRO(detailRO, eduSchoolSuppliers, searchDTO);
            if (!okFlag) {
                continue;
            }
            detailList.add(detailRO);
        }

        return detailList;
    }

    /**
     * 检索过滤 Redis 学校回收油脂详情
     *
     * @param detailROList
     * @param searchDTO
     * @return
     */
    private List<RecoveryWasteOilDetailRO> filterRmcRecoveryWasteOilDetailROList(List<RecoveryWasteOilDetailRO> detailROList, RmcRecoveryWasteOilSearchDTO searchDTO) {

        List<RecoveryWasteOilDetailRO> detailList = new ArrayList<>();

        if (CollectionUtils.isEmpty(detailROList)) {
            return detailList;
        }

        boolean okFlag;
        for (RecoveryWasteOilDetailRO detailRO : detailROList) {
            okFlag = filterRmcRecoveryWasteOilDetailRO(detailRO, searchDTO);
            if (!okFlag) {
                continue;
            }
            detailList.add(detailRO);
        }

        return detailList;
    }

    /**
     * Redis学校基础信息过滤器
     *
     * @param schoolBasicRO
     * @param searchDTO
     * @return
     */
    private boolean filterSchoolBasicRO(SchoolBasicRO schoolBasicRO, SchoolRecoveryWasteOilSearchDTO searchDTO) {

        //过滤油脂类型Code
        if (!Objects.isNull(searchDTO.getWoType())) {
            //此模型无需过滤
        }

        //过滤项目点（学校Id）
        if (StringUtils.isNotBlank(searchDTO.getPpName())) {
            if (!searchDTO.getPpName().equalsIgnoreCase(schoolBasicRO.getId())) {
                return false;
            }
        }

        //过滤区Id
        if (!Objects.isNull(searchDTO.getDistName())) {
            if (!searchDTO.getDistName().equals(schoolBasicRO.getArea())) {
                return false;
            }
        }

        //过滤学制Id
        List<Object> schTypeList = CommonUtil.changeStringToList(searchDTO.getSchTypes());
        if (!Objects.isNull(searchDTO.getSchType())) {
            if (!searchDTO.getSchType().equals(schoolBasicRO.getLevel())) {
                return false;
            }
        } else if (schTypeList != null && schTypeList.size() > 0) {
            if (schoolBasicRO.getLevel() == null) {
                return false;
            }
            if (!schTypeList.contains(schoolBasicRO.getLevel().toString())) {
                return false;
            }
        }

        //过滤团餐公司Id
        if (StringUtils.isNotBlank(searchDTO.getRmcName())) {
            //此模型无需过滤
        }

        //回收公司Id
        if (StringUtils.isNotBlank(searchDTO.getRecCompany())) {
            //此模型无需过滤
        }

        //过滤回收人Id
        if (StringUtils.isNotBlank(searchDTO.getRecPerson())) {
            //此模型无需过滤
        }

        List<Object> schPropsList = CommonUtil.changeStringToList(searchDTO.getSchProps());
        //学校性质
        if (searchDTO.getSchProp() != null && searchDTO.getSchProp() != -1) {
            if (StringUtils.isEmpty(schoolBasicRO.getSchoolNature())) {
                return false;
            }
            if (!schoolBasicRO.getSchoolNature().equals(searchDTO.getSchProp().toString())) {
                return false;
            }
        } else if (schPropsList != null && schPropsList.size() > 0) {
            if (StringUtils.isEmpty(schoolBasicRO.getSchoolNature())) {
                return false;
            }
            if (!schPropsList.contains(schoolBasicRO.getSchoolNature())) {
                return false;
            }
        }

        List<Object> subLevelsList = CommonUtil.changeStringToList(searchDTO.getSubLevels());
        List<Object> compDepsList = CommonUtil.changeStringToList(searchDTO.getCompDeps());

        //判断所属
        if (StringUtils.isNotEmpty(searchDTO.getSubLevel()) && !"-1".equals(searchDTO.getSubLevel())) {
            if (schoolBasicRO.getDepartmentMasterId() != Integer.parseInt(searchDTO.getSubLevel()))
                return false;
        } else if (subLevelsList != null && subLevelsList.size() > 0) {
            if (!subLevelsList.contains(String.valueOf(schoolBasicRO.getDepartmentMasterId()))) {
                return false;
            }
        }

        //判断所属部门
        if (StringUtils.isNotEmpty(searchDTO.getCompDep()) && !"-1".equals(searchDTO.getCompDep())) {
            String currDepartmentMasterId = schoolBasicRO.getDepartmentSlaveId();
            //如果是区属查询，需要根据编码（32位字母+数字）转换为对应的数字
            if (schoolBasicRO.getDepartmentMasterId() == 3) {
                String orgName = AppModConfig.compDepIdToNameMap3bd.get(schoolBasicRO.getDepartmentSlaveId());
                if (orgName != null) {
                    currDepartmentMasterId = AppModConfig.compDepNameToIdMap3.get(orgName);
                }
            }
            if (!searchDTO.getCompDep().equals(schoolBasicRO.getDepartmentMasterId() + "_" + currDepartmentMasterId))
                return false;
        } else if (compDepsList != null && compDepsList.size() > 0) {

            String currDepartmentMasterId = schoolBasicRO.getDepartmentSlaveId();
            //如果是区属查询，需要根据编码（32位字母+数字）转换为对应的数字
            if (schoolBasicRO.getDepartmentMasterId() == 3) {
                String orgName = AppModConfig.compDepIdToNameMap3bd.get(schoolBasicRO.getDepartmentSlaveId());
                if (orgName != null) {
                    currDepartmentMasterId = AppModConfig.compDepNameToIdMap3.get(orgName);
                }
            }

            if (!compDepsList.contains(schoolBasicRO.getDepartmentMasterId() + "_" + currDepartmentMasterId)) {
                return false;
            }
        }


        //通过过滤
        return true;
    }

    /**
     * Redis团餐公司基础信息过滤器
     *
     * @param mealCompanyBasicRO
     * @param searchDTO
     * @return
     */
    private boolean filterGroupMealCompanyBasicRO(GroupMealCompanyBasicRO mealCompanyBasicRO, RmcRecoveryWasteOilSearchDTO searchDTO) {

        //过滤油脂类型Code
        if (!Objects.isNull(searchDTO.getWoType())) {
            //此模型无需过滤
        }

        //团餐公司编号
        if (StringUtils.isNotBlank(searchDTO.getRmcName())) {
            if (!searchDTO.getRmcName().equalsIgnoreCase(mealCompanyBasicRO.getId())) {
                return false;
            }
        }

        //过滤区Id
        if (!Objects.isNull(searchDTO.getDistName())) {
            if (!searchDTO.getDistName().equals(mealCompanyBasicRO.getArea())) {
                return false;
            }
        }

        //过滤团餐公司Id
        if (StringUtils.isNotBlank(searchDTO.getRmcName())) {
            //此模型无需过滤
        }

        //回收公司Id
        if (StringUtils.isNotBlank(searchDTO.getRecCompany())) {
            //此模型无需过滤
        }

        //过滤回收人Id
        if (StringUtils.isNotBlank(searchDTO.getRecPerson())) {
            //此模型无需过滤
        }

        //通过过滤
        return true;
    }

    /**
     * 过滤Redis回收废弃油脂详情
     *
     * @param detailVO
     * @param searchDTO
     * @param eduSchoolSuppliers
     * @return
     */
    private boolean filterSchoolRecoveryWasteOilDetailRO(RecoveryWasteOilDetailRO detailVO, List<EduSchoolSupplier> eduSchoolSuppliers,
                                                         SchoolRecoveryWasteOilSearchDTO searchDTO) {

        //过滤油脂类型Code
        if (!Objects.isNull(searchDTO.getWoType())) {
            if (!searchDTO.getWoType().equals(detailVO.getSecondType())) {
                return false;
            }
        }

        //过滤项目点（学校Id/团餐公司Id）
        if (StringUtils.isNotBlank(searchDTO.getPpName())) {
            if (!searchDTO.getPpName().equalsIgnoreCase(detailVO.getSchoolId())) {
                return false;
            }
        }

        //过滤区Id
        if (!Objects.isNull(searchDTO.getDistName())) {
            if (!searchDTO.getDistName().equals(detailVO.getArea())) {
                return false;
            }
        } else if (StringUtils.isNotEmpty(searchDTO.getDistNames())) {
            //区域集合过滤
            List<Object> distNamesList = CommonUtil.changeStringToList(searchDTO.getDistNames());
            if (distNamesList != null && distNamesList.size() > 0) {
                if (!distNamesList.contains(String.valueOf(detailVO.getArea()))) {
                    return false;
                }
            }
        }

        //过滤学制Id
        if (!Objects.isNull(searchDTO.getSchType())) {
            //此模型无需过滤
        }

        //过滤团餐公司Id
        if (StringUtils.isNotBlank(searchDTO.getRmcName())) {
            long count = eduSchoolSuppliers.stream().filter(s -> s.getSchoolId().equalsIgnoreCase(detailVO.getSchoolId())
                    && searchDTO.getRmcName().equalsIgnoreCase(s.getSupplierId())).count();
            if (count == 0) {
                return false;
            }
        }

        //回收公司Id
        if (!filterRecoveryCompanyAndPerson(detailVO, searchDTO.getRecCompany(), searchDTO.getRecPerson())) {
            return false;
        }


        //所属
        //主管部门
        //办学性质

        return true;
    }

    /**
     * 过滤Redis回收废弃油脂详情
     *
     * @param detailVO
     * @param searchDTO
     * @return
     */
    private boolean filterRmcRecoveryWasteOilDetailRO(RecoveryWasteOilDetailRO detailVO, RmcRecoveryWasteOilSearchDTO searchDTO) {

        //过滤油脂类型Code
        if (!Objects.isNull(searchDTO.getWoType())) {
            if (!searchDTO.getWoType().equals(detailVO.getSecondType())) {
                return false;
            }
        }

        //团餐公司编号
        if (StringUtils.isNotBlank(searchDTO.getRmcName())) {
            if (!searchDTO.getRmcName().equalsIgnoreCase(detailVO.getSchoolId())) {
                return false;
            }
        }

        //过滤区Id
        if (!Objects.isNull(searchDTO.getDistName())) {
            if (!searchDTO.getDistName().equals(detailVO.getArea())) {
                return false;
            }
        } else if (StringUtils.isNotEmpty(searchDTO.getDistNames())) {
            //区域集合过滤
            List<Object> distNamesList = CommonUtil.changeStringToList(searchDTO.getDistNames());
            if (distNamesList != null && distNamesList.size() > 0) {
                if (!distNamesList.contains(String.valueOf(detailVO.getArea()))) {
                    return false;
                }
            }
        }

        if (!filterRecoveryCompanyAndPerson(detailVO, searchDTO.getRecCompany(), searchDTO.getRecPerson())) {
            return false;
        }

        return true;
    }

    /**
     * @param detailVO
     * @param recCompany 待搜索的回收公司
     * @param recPerson  待检索的回收人
     * @return
     */
    private boolean filterRecoveryCompanyAndPerson(RecoveryWasteOilDetailRO detailVO, String recCompany, String recPerson) {
        //回收公司Id
        if (StringUtils.isNotBlank(recCompany)) {
            //数据为空
            if (GlobalConstant.REDIS_NULL_VALUE_STRING_DEFAULT.equalsIgnoreCase(detailVO.getReceiverName())
                    || StringUtils.isBlank(detailVO.getReceiverName())) {
                return false;
            }

            //支持回收公司模糊查询
            if (!Objects.isNull(detailVO.getReceiverName()) && detailVO.getReceiverName().indexOf(recCompany) == -1) {
                return false;
            }
        }

        //过滤回收人Id
        if (StringUtils.isNotBlank(recPerson)) {
            //数据为空
            if (GlobalConstant.REDIS_NULL_VALUE_STRING_DEFAULT.equalsIgnoreCase(detailVO.getContact())
                    || StringUtils.isBlank(detailVO.getContact())) {
                return false;
            }

            //支持回收人模糊查询
            if (!Objects.isNull(detailVO.getContact()) && detailVO.getContact().indexOf(recPerson) == -1) {
                return false;
            }
        }

        return true;
    }

    /**
     * 获取主管部门
     *
     * @param schoolBasicRO
     * @return
     */
    private static String getCompDep(SchoolBasicRO schoolBasicRO) {
        String departmentSlaveId = schoolBasicRO.getDepartmentSlaveId();
        if (GlobalConstant.REDIS_NULL_VALUE_STRING_DEFAULT.equalsIgnoreCase(departmentSlaveId)) {
            return null;
        }

        if (Objects.isNull(schoolBasicRO.getDepartmentMasterId())) {
            return "其他";
        }

        //-----
        Integer curCompDep = 0;
        if (schoolBasicRO.getDepartmentMasterId() == 0) {      //其他
            if (StringUtils.isNotEmpty(departmentSlaveId)) {
                curCompDep = Integer.parseInt(departmentSlaveId);
            }
            return AppModConfig.compDepIdToNameMap0.get(String.valueOf(curCompDep));
        } else if (schoolBasicRO.getDepartmentMasterId() == 1) {      //部级
            if (StringUtils.isNotEmpty(departmentSlaveId)) {
                curCompDep = Integer.parseInt(departmentSlaveId);
            }
            return AppModConfig.compDepIdToNameMap1.get(String.valueOf(curCompDep));
        } else if (schoolBasicRO.getDepartmentMasterId() == 2) {      //市级
            if (StringUtils.isNotEmpty(departmentSlaveId)) {
                curCompDep = Integer.parseInt(departmentSlaveId);
            }
            return AppModConfig.compDepIdToNameMap2.get(String.valueOf(curCompDep));
        } else if (schoolBasicRO.getDepartmentMasterId() == 3) {      //区级
            if (StringUtils.isNotEmpty(departmentSlaveId)) {
                String orgName = AppModConfig.compDepIdToNameMap3bd.get(departmentSlaveId);
                if (orgName != null) {
                    curCompDep = Integer.parseInt(AppModConfig.compDepNameToIdMap3.get(orgName));
                }
            }
            return AppModConfig.compDepIdToNameMap3.get(String.valueOf(curCompDep));
        }

        return "其他";
    }

    /**
     * 解析redis废弃油脂详情列表信息
     *
     * @param keyMap
     * @return
     */
    private List<RecoveryWasteOilDetailRO> resolveSchoolRecoveryWasteOilDetailROList(Map<String, Date> keyMap) {
        Map<String, String> fieldMap;
        List<RecoveryWasteOilDetailRO> detailROList = new ArrayList<>();

        for (String key : keyMap.keySet()) {
            fieldMap = readRedis(key);
            if (Objects.isNull(fieldMap)) {
                continue;
            }

            for (String value : fieldMap.values()) {
                RecoveryWasteOilDetailRO detailRO = resolveSchoolRecoveryWasteOilDetailRO(value);
                if (Objects.isNull(detailRO)) {
                    continue;
                }

                detailRO.setRecDate(keyMap.get(key));

                detailROList.add(detailRO);
            }
        }

        return detailROList;
    }

    /**
     * 解析 redis中 回收废弃油脂详情模型
     *
     * @param value
     * @return
     */
    private RecoveryWasteOilDetailRO resolveSchoolRecoveryWasteOilDetailRO(String value) {

        if (value.indexOf(SPLIT_SYMBOL) == -1) {
            logger.info("redis：key={},value={} 值格式错误, 不包含分隔符“{}”, 解析失败。", RedisKeyConstant.SCHOOL_RECOVERY_WASTE_OIL_DETAIL, value, SPLIT_SYMBOL);
            return null;
        }

        int index = -1;
        RecoveryWasteOilDetailRO detailRO = new RecoveryWasteOilDetailRO();

        String[] splitArray = value.split(SPLIT_SYMBOL);

        /**
         * 格式(area_区号_schoolid_学校id_number_回收数量_receivername_回收单位_contact_回收人_documents_回收单据数量_seconttype_油脂类型)
         */
        if (splitArray.length < REDIS_RECOVERY_WASTE_OIL_DETAIL_RO_FORMAT_LENGTH) {
            logger.info("redis：key={},value={} 值格式错误, 数据项少于{}, 解析失败。", RedisKeyConstant.SCHOOL_RECOVERY_WASTE_OIL_DETAIL, value, REDIS_RECOVERY_WASTE_OIL_DETAIL_RO_FORMAT_LENGTH);
            return null;
        }

        index += ITERATE_STEP_SIZE;
        detailRO.setArea(RedisValueUtil.toInteger(splitArray[index]));

        index += ITERATE_STEP_SIZE;
        detailRO.setSchoolId(RedisValueUtil.filterString(splitArray[index]));

        index += ITERATE_STEP_SIZE;
        detailRO.setNumber(String.valueOf(splitArray[index]));

        index += ITERATE_STEP_SIZE;
        detailRO.setReceiverName(RedisValueUtil.filterString(splitArray[index]));

        index += ITERATE_STEP_SIZE;
        detailRO.setContact(RedisValueUtil.filterString(splitArray[index]));

        index += ITERATE_STEP_SIZE;
        detailRO.setDocuments(RedisValueUtil.toInteger(splitArray[index]));

        index += ITERATE_STEP_SIZE;
        detailRO.setSecondType(RedisValueUtil.toInteger(splitArray[index]));

        return detailRO;
    }

    /**
     * 解析废弃油脂汇总 redis Key
     *
     * @param searchType
     * @return
     */
    private String resolveRedisKeyForWasteOilTotal(Integer searchType) {

        //如为空,默认查询学校油脂汇总
        if (Objects.isNull(searchType)) {
            return RedisKeyConstant.SCHOOL_RECOVERY_WASTE_OIL_TOTAL;
        }

        if (OwnerType.SCHOOL.getCode() == searchType.intValue()) {
            return RedisKeyConstant.SCHOOL_RECOVERY_WASTE_OIL_TOTAL;
        }

        if (OwnerType.RMC.getCode() == searchType.intValue()) {
            return RedisKeyConstant.RMC_RECOVERY_WASTE_OIL_TOTAL;
        }

        return RedisKeyConstant.SCHOOL_RECOVERY_WASTE_OIL_TOTAL;
    }

    /**
     * 解析废弃油脂详情 redis Key
     *
     * @param searchType
     * @return
     */
    private String resolveRedisKeyForWasteOilDetail(Integer searchType) {

        //如为空,默认查询学校油脂详情
        if (Objects.isNull(searchType)) {
            return RedisKeyConstant.SCHOOL_RECOVERY_WASTE_OIL_DETAIL;
        }

        if (OwnerType.SCHOOL.getCode() == searchType.intValue()) {
            return RedisKeyConstant.SCHOOL_RECOVERY_WASTE_OIL_DETAIL;
        }

        if (OwnerType.RMC.getCode() == searchType.intValue()) {
            return RedisKeyConstant.RMC_RECOVERY_WASTE_OIL_DETAIL;
        }

        return RedisKeyConstant.SCHOOL_RECOVERY_WASTE_OIL_DETAIL;
    }

    /**
     * 解析出 Redis 每日废弃油脂存储 Key。
     *
     * @param type
     * @param beginDate
     * @param endDate
     * @return
     */
    private Map<String, Date> genRedisKey(String type, Date beginDate, Date endDate) {

        Map<String, Date> keyMap = new HashMap<>();

        for (long i = beginDate.getTime(); i <= endDate.getTime(); i += DAY_TOTAL_MILLISECOND) {
            Date date = new Date(i);
            keyMap.put(String.format("%s_%s", dateFormat.format(date), type), date);
        }

        return keyMap;
    }

    /**
     * 读取redis
     *
     * @param key
     * @return
     */
    private Map<String, String> readRedis(String key) {
        return redisService.getHashByKey(SpringConfig.RedisRunEnvIdx, SpringConfig.RedisDBIdx, key);
    }

    /**
     * 解析每日回收废弃油脂汇总信息
     *
     * @param searchDTO 检索模型
     * @return
     */
    private List<RecoveryWasteOilSummaryVO> getDailyRecoveryWasteOilTotalList(RecoveryWasteOilSummarySearchDTO searchDTO,
                                                                              DbHiveRecyclerWasteService dbHiveRecyclerWasteService) {
        //获取区/县编码
        String distNameCode = resolveDistNameCode(searchDTO.getDistName());
        boolean isFilterCode = !StringUtils.isEmpty(distNameCode);

        //区域集合过滤
        List<Object> distNamesList = CommonUtil.changeStringToList(searchDTO.getDistNames());
        if (distNamesList != null && distNamesList.size() > 0) {
            isFilterCode = true;
        }
        //解析废弃油脂汇总 redis Key
        String typeKey = resolveRedisKeyForWasteOilTotal(searchDTO.getSearchType());
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        DateTime startDt = BCDTimeUtil.convertDateStrToDate(format.format(searchDTO.getStartSubDate()));
        DateTime currentTime = new DateTime();
        int days = Days.daysBetween(startDt, currentTime).getDays();
        List<RecoveryWasteOilSummaryVO> result = new ArrayList<>();
        List<String> distCodeList = new ArrayList<>();
        //遍历distIdToNameMap集合（id和对应的名称）
        for (String key : AppModConfig.distIdToNameMap.keySet()) {
            //过滤区/县信息
            if (distNameCode != null && !key.equalsIgnoreCase(distNameCode)) {
                continue;
            } else if (distNamesList != null && distNamesList.size() > 0) {
                if (!CommonUtil.isInteger(key)) {
                    continue;
                }
                if (!distNamesList.contains(key)) {
                    continue;
                }
            }
            distCodeList.add(key);
        }

        if (days >= 2) {
            //如果包含30天之前数据，则去hive库中的数据，否则取redis中的数据
            String startDate = format.format(searchDTO.getStartSubDate());
            String endDate = format.format(searchDTO.getEndSubDate());
            if (startDate == null || startDate.split("-").length < 2) {
                startDate = BCDTimeUtil.convertNormalDate(null);
            }
            if ((endDate == null || endDate.split("-").length < 2) && startDate != null) {
                endDate = startDate;
            } else if (endDate == null || endDate.split("-").length < 2) {
                endDate = BCDTimeUtil.convertNormalDate(null);
            }

            String[] yearMonths = new String[4];
            //根据开始日期、结束日期，获取开始日期和结束日期的年、月
            yearMonths = CommonUtil.getYearMonthByDate(startDate, endDate);
            String startYear = yearMonths[0];
            String startMonth = yearMonths[1];
            String endYear = yearMonths[2];
            String endMonth = yearMonths[3];

            //结束日期+1天，方便查询处理
            String endDateAddOne = CommonUtil.dateAddDay(endDate, 1);
            //获取开始日期、结束日期的年月集合
            List<String> listYearMonth = CommonUtil.getYearMonthList(startYear, startMonth, endYear, endMonth);

            /**
             * 1.从hive库中获取汇总数据
             */
            List<Object> distIdList = CommonUtil.changeStringToList(searchDTO.getDistNames());
            List<KwCommonRecs> warnCommonLicList = new ArrayList<>();
            warnCommonLicList = dbHiveRecyclerWasteService.getRecyclerWasteList(listYearMonth, startDate, endDateAddOne, searchDTO.getDistName(),
                    distIdList, null, null, -1, -1, null, null, searchDTO.getSearchType(), 2, 0);
            /**
             * 2.合并数据(按区合并)
             */
            RecoveryWasteOilSummaryVO oilsVO = null;
            Map<String, RecoveryWasteOilSummaryVO> warnTotalMap = new HashMap<String, RecoveryWasteOilSummaryVO>();
            for (String distCode : distCodeList) {
                String distName = DictConvertUtil.mapToDistName(RedisValueUtil.filterString(distCode));
                oilsVO = new RecoveryWasteOilSummaryVO();
                oilsVO.setDistName(distName);
                oilsVO.setReFeq(0);
                oilsVO.setRcNum(Float.valueOf("0"));
                result.add(oilsVO);
            }
            for (KwCommonRecs warnCommonLics : warnCommonLicList) {
                String distName = DictConvertUtil.mapToDistName(RedisValueUtil.filterString(warnCommonLics.getDistName()));
                if (StringUtils.isEmpty(distName)) {
                    continue;
                }
                oilsVO = warnTotalMap.get(distName);
                if (oilsVO == null) {
                    oilsVO = new RecoveryWasteOilSummaryVO();
                    oilsVO.setDistName(distName);
                    oilsVO.setReFeq(0);
                    oilsVO.setRcNum(Float.valueOf("0"));
                }
                oilsVO.setReFeq(oilsVO.getReFeq() + warnCommonLics.getRecyclerTotal());
                oilsVO.setRcNum(new BigDecimal(oilsVO.getRcNum() + warnCommonLics.getRecyclerSum()).setScale(2, BigDecimal.ROUND_HALF_UP).floatValue());
                result.add(oilsVO);
            }
        } else {
            Map<String, String> fieldMap;
            Map<String, Date> keyMap = genRedisKey(typeKey, searchDTO.getStartSubDate(), searchDTO.getEndSubDate());

            if (keyMap != null && keyMap.size() > 0) {
                for (String key : keyMap.keySet()) {
                    fieldMap = readRedis(key);

                    for (String distCode : distCodeList) {
                        if (isFilterCode) {
                            //过滤区/县信息
                            if (distNameCode != null && !distCode.equalsIgnoreCase(distNameCode)) {
                                continue;
                            } else if (distNamesList != null && distNamesList.size() > 0) {
                                if (!CommonUtil.isInteger(distCode)) {
                                    continue;
                                }
                                if (!distNamesList.contains(distCode)) {
                                    continue;
                                }
                            }
                        }
                        RecoveryWasteOilSummaryVO oilsVO = generateRecoveryWasteOilSummaryVO(keyMap.get(key), fieldMap, distCode);
                        if (oilsVO == null) {
                            continue;
                        }
                        result.add(oilsVO);
                    }
                }
            }
        }
        if (result == null || result.size() == 0) {
            //如果redis中未查到任何数据，则将各区数据设置为0
            for (String distCode : distCodeList) {
                if (isFilterCode) {
                    //过滤区/县信息
                    if (distNameCode != null && !distCode.equalsIgnoreCase(distNameCode)) {
                        continue;
                    } else if (distNamesList != null && distNamesList.size() > 0) {
                        if (!CommonUtil.isInteger(distCode)) {
                            continue;
                        }
                        if (!distNamesList.contains(distCode)) {
                            continue;
                        }
                    }
                }
                RecoveryWasteOilSummaryVO oilsVO = new RecoveryWasteOilSummaryVO();
                oilsVO.setCompDep("");
                String distName = DictConvertUtil.mapToDistName(RedisValueUtil.filterString(distCode));
                oilsVO.setDistName(distName);
                oilsVO.setRcNum(Float.valueOf("0"));
                oilsVO.setRecDate("");
                oilsVO.setReFeq(0);
                result.add(oilsVO);
            }
        }
        distCodeList.clear();

        return result;
    }

    /**
     * 生成废弃油脂汇总信息
     *
     * @param date
     * @param fieldMap
     * @param distCode
     * @return
     */
    private RecoveryWasteOilSummaryVO generateRecoveryWasteOilSummaryVO(Date date, Map<String, String> fieldMap, String distCode) {

        if (fieldMap == null || fieldMap.size() == 0) {
            return null;
        }

        RecoveryWasteOilSummaryVO oilsVO = new RecoveryWasteOilSummaryVO();

        String distName = DictConvertUtil.mapToDistName(RedisValueUtil.filterString(distCode));
        if (StringUtils.isEmpty(distName)) {
            //忽略解析不到区信息的数据。
            return null;
        } else {
            oilsVO.setDistName(distName);
        }
        if (CommonUtil.isNotEmpty(fieldMap.get(distCode)))
            oilsVO.setRcNum(new BigDecimal(fieldMap.get(distCode)).setScale(2, BigDecimal.ROUND_HALF_UP).floatValue());
        else
            oilsVO.setRcNum(Float.valueOf("0"));
        if (fieldMap.get(distCode + RECOVERY_WASTE_OIL_SUMMARY_SUFFIX) != null)
            oilsVO.setReFeq(RedisValueUtil.toInteger(fieldMap.get(distCode + RECOVERY_WASTE_OIL_SUMMARY_SUFFIX)));
        else
            oilsVO.setReFeq(0);
        oilsVO.setRecDate(dateFormat2.format(date));

        return oilsVO;
    }

    /**
     * 解析出区/县Code信息
     *
     * @param distName 区/县编号
     * @return
     */
    private String resolveDistNameCode(String distName) {

        if (StringUtils.isEmpty(distName)) {
            return null;
        }

        if (Pattern.matches(RegexExpressConstant.REGEX_ALL_NUMBER_FORMAT, distName)) {
            return distName;
        }

        //如“黄浦区”
        return DictConvertUtil.mapToDistId(distName);
    }
}
