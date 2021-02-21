package com.tfit.BdBiProcSrvShEduOmc.controller;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tfit.BdBiProcSrvShEduOmc.annotation.AccessLog;
import com.tfit.BdBiProcSrvShEduOmc.appmod.im.AcceptStatusCodesAppMod;
import com.tfit.BdBiProcSrvShEduOmc.appmod.im.AssignStatusCodesAppMod;
import com.tfit.BdBiProcSrvShEduOmc.appmod.im.CaterTypeCodesAppMod;
import com.tfit.BdBiProcSrvShEduOmc.appmod.im.DishExeListAppMod;
import com.tfit.BdBiProcSrvShEduOmc.appmod.im.DishRetSamplesAppMod;
import com.tfit.BdBiProcSrvShEduOmc.appmod.im.DishRsDetsAppMod;
import com.tfit.BdBiProcSrvShEduOmc.appmod.im.DispModeCodesAppMod;
import com.tfit.BdBiProcSrvShEduOmc.appmod.im.DispStatusCodesAppMod;
import com.tfit.BdBiProcSrvShEduOmc.appmod.im.DispTypeCodesAppMod;
import com.tfit.BdBiProcSrvShEduOmc.appmod.im.DistListAppMod;
import com.tfit.BdBiProcSrvShEduOmc.appmod.im.ExpDishRetSamplesAppMod;
import com.tfit.BdBiProcSrvShEduOmc.appmod.im.ExpDishRsDetsAppMod;
import com.tfit.BdBiProcSrvShEduOmc.appmod.im.ExpGsPlanOptDetsAppMod;
import com.tfit.BdBiProcSrvShEduOmc.appmod.im.ExpGsPlanOptsAppMod;
import com.tfit.BdBiProcSrvShEduOmc.appmod.im.ExpKwRmcRecsAppMod;
import com.tfit.BdBiProcSrvShEduOmc.appmod.im.ExpKwSchRecsAppMod;
import com.tfit.BdBiProcSrvShEduOmc.appmod.im.ExpMatConfirmDetsAppMod;
import com.tfit.BdBiProcSrvShEduOmc.appmod.im.ExpMatConfirmsAppMod;
import com.tfit.BdBiProcSrvShEduOmc.appmod.im.ExpPpDishDetsAppMod;
import com.tfit.BdBiProcSrvShEduOmc.appmod.im.ExpPpDishListAppMod;
import com.tfit.BdBiProcSrvShEduOmc.appmod.im.ExpPpGsPlanOptsAppMod;
import com.tfit.BdBiProcSrvShEduOmc.appmod.im.ExpPpRetSamplesAppMod;
import com.tfit.BdBiProcSrvShEduOmc.appmod.im.ExpRmcKwDetsAppMod;
import com.tfit.BdBiProcSrvShEduOmc.appmod.im.ExpSchKwDetsAppMod;
import com.tfit.BdBiProcSrvShEduOmc.appmod.im.ExpSumDataDetsAppMod;
import com.tfit.BdBiProcSrvShEduOmc.appmod.im.GsPlanOptDetsAppMod;
import com.tfit.BdBiProcSrvShEduOmc.appmod.im.GsPlanOptsAppMod;
import com.tfit.BdBiProcSrvShEduOmc.appmod.im.IsMealCodesAppMod;
import com.tfit.BdBiProcSrvShEduOmc.appmod.im.IsRsCodesAppMod;
import com.tfit.BdBiProcSrvShEduOmc.appmod.im.KwRmcRecsAppMod;
import com.tfit.BdBiProcSrvShEduOmc.appmod.im.KwSchRecsAppMod;
import com.tfit.BdBiProcSrvShEduOmc.appmod.im.MatConfirmDetsAppMod;
import com.tfit.BdBiProcSrvShEduOmc.appmod.im.MatConfirmListAppMod;
import com.tfit.BdBiProcSrvShEduOmc.appmod.im.MatConfirmsAppMod;
import com.tfit.BdBiProcSrvShEduOmc.appmod.im.MatTypeCodesAppMod;
import com.tfit.BdBiProcSrvShEduOmc.appmod.im.MenuNameCodesAppMod;
import com.tfit.BdBiProcSrvShEduOmc.appmod.im.OptModeCodesAppMod;
import com.tfit.BdBiProcSrvShEduOmc.appmod.im.PpDishDetsAppMod;
import com.tfit.BdBiProcSrvShEduOmc.appmod.im.PpDishListAppMod;
import com.tfit.BdBiProcSrvShEduOmc.appmod.im.PpGsPlanOptsAppMod;
import com.tfit.BdBiProcSrvShEduOmc.appmod.im.PpNameCodesAppMod;
import com.tfit.BdBiProcSrvShEduOmc.appmod.im.PpRetSamplesAppMod;
import com.tfit.BdBiProcSrvShEduOmc.appmod.im.RecPersonCodesAppMod;
import com.tfit.BdBiProcSrvShEduOmc.appmod.im.RecUnitCodesAppMod;
import com.tfit.BdBiProcSrvShEduOmc.appmod.im.RmcKwDetsAppMod;
import com.tfit.BdBiProcSrvShEduOmc.appmod.im.RmcListAppMod;
import com.tfit.BdBiProcSrvShEduOmc.appmod.im.RsUnitCodesAppMod;
import com.tfit.BdBiProcSrvShEduOmc.appmod.im.SchKwDetsAppMod;
import com.tfit.BdBiProcSrvShEduOmc.appmod.im.SchPropCodesAppMod;
import com.tfit.BdBiProcSrvShEduOmc.appmod.im.SchSelModeCodesAppMod;
import com.tfit.BdBiProcSrvShEduOmc.appmod.im.SchTypeCodesAppMod;
import com.tfit.BdBiProcSrvShEduOmc.appmod.im.SendStatusCodesAppMod;
import com.tfit.BdBiProcSrvShEduOmc.appmod.im.SumDataDetsAppMod;
import com.tfit.BdBiProcSrvShEduOmc.appmod.im.WasteOilDetsAppMod;
import com.tfit.BdBiProcSrvShEduOmc.appmod.im.WasteOilsAppMod;
import com.tfit.BdBiProcSrvShEduOmc.appmod.im.week.ExpPpDishWeekAppMod;
import com.tfit.BdBiProcSrvShEduOmc.appmod.im.week.ExpPpGsPlanWeekAppMod;
import com.tfit.BdBiProcSrvShEduOmc.appmod.im.week.ExpPpMatConfirmWeekAppMod;
import com.tfit.BdBiProcSrvShEduOmc.appmod.im.week.ExpPpRetSamplesWeekAppMod;
import com.tfit.BdBiProcSrvShEduOmc.appmod.im.week.PpDishWeekAppMod;
import com.tfit.BdBiProcSrvShEduOmc.appmod.im.week.PpGsPlanWeekAppMod;
import com.tfit.BdBiProcSrvShEduOmc.appmod.im.week.PpMatConfirmWeekAppMod;
import com.tfit.BdBiProcSrvShEduOmc.appmod.im.week.PpRetSamplesWeekAppMod;
import com.tfit.BdBiProcSrvShEduOmc.common.ApiResponse;
import com.tfit.BdBiProcSrvShEduOmc.config.AppModConfig;
import com.tfit.BdBiProcSrvShEduOmc.context.UserContextHandler;
import com.tfit.BdBiProcSrvShEduOmc.dto.IOTHttpRspVO;
import com.tfit.BdBiProcSrvShEduOmc.dto.im.AcceptStatusCodesDTO;
import com.tfit.BdBiProcSrvShEduOmc.dto.im.AssignStatusCodesDTO;
import com.tfit.BdBiProcSrvShEduOmc.dto.im.CaterTypeCodesDTO;
import com.tfit.BdBiProcSrvShEduOmc.dto.im.DishExeListDTO;
import com.tfit.BdBiProcSrvShEduOmc.dto.im.DishRetSamplesDTO;
import com.tfit.BdBiProcSrvShEduOmc.dto.im.DishRsDetsDTO;
import com.tfit.BdBiProcSrvShEduOmc.dto.im.DispModeCodesDTO;
import com.tfit.BdBiProcSrvShEduOmc.dto.im.DispStatusCodesDTO;
import com.tfit.BdBiProcSrvShEduOmc.dto.im.DispTypeCodesDTO;
import com.tfit.BdBiProcSrvShEduOmc.dto.im.DistListDTO;
import com.tfit.BdBiProcSrvShEduOmc.dto.im.ExpCommonDTO;
import com.tfit.BdBiProcSrvShEduOmc.dto.im.ExpDishRetSamplesDTO;
import com.tfit.BdBiProcSrvShEduOmc.dto.im.ExpDishRsDetsDTO;
import com.tfit.BdBiProcSrvShEduOmc.dto.im.ExpGsPlanOptDetsDTO;
import com.tfit.BdBiProcSrvShEduOmc.dto.im.ExpGsPlanOptsDTO;
import com.tfit.BdBiProcSrvShEduOmc.dto.im.ExpKwRmcRecsDTO;
import com.tfit.BdBiProcSrvShEduOmc.dto.im.ExpKwSchRecsDTO;
import com.tfit.BdBiProcSrvShEduOmc.dto.im.ExpMatConfirmDetsDTO;
import com.tfit.BdBiProcSrvShEduOmc.dto.im.ExpMatConfirmsDTO;
import com.tfit.BdBiProcSrvShEduOmc.dto.im.ExpPpDishDetsDTO;
import com.tfit.BdBiProcSrvShEduOmc.dto.im.ExpPpDishListDTO;
import com.tfit.BdBiProcSrvShEduOmc.dto.im.ExpPpGsPlanOptsDTO;
import com.tfit.BdBiProcSrvShEduOmc.dto.im.ExpPpRetSamplesDTO;
import com.tfit.BdBiProcSrvShEduOmc.dto.im.ExpRmcKwDetsDTO;
import com.tfit.BdBiProcSrvShEduOmc.dto.im.ExpSchKwDetsDTO;
import com.tfit.BdBiProcSrvShEduOmc.dto.im.ExpSumDataDetsDTO;
import com.tfit.BdBiProcSrvShEduOmc.dto.im.GsPlanOptDetsDTO;
import com.tfit.BdBiProcSrvShEduOmc.dto.im.GsPlanOptsDTO;
import com.tfit.BdBiProcSrvShEduOmc.dto.im.IsMealCodesDTO;
import com.tfit.BdBiProcSrvShEduOmc.dto.im.IsRsCodesDTO;
import com.tfit.BdBiProcSrvShEduOmc.dto.im.KwRmcRecsDTO;
import com.tfit.BdBiProcSrvShEduOmc.dto.im.KwSchRecsDTO;
import com.tfit.BdBiProcSrvShEduOmc.dto.im.MatConfirmDetsDTO;
import com.tfit.BdBiProcSrvShEduOmc.dto.im.MatConfirmListDTO;
import com.tfit.BdBiProcSrvShEduOmc.dto.im.MatConfirmsDTO;
import com.tfit.BdBiProcSrvShEduOmc.dto.im.MatTypeCodesDTO;
import com.tfit.BdBiProcSrvShEduOmc.dto.im.MenuNameCodesDTO;
import com.tfit.BdBiProcSrvShEduOmc.dto.im.OptModeCodesDTO;
import com.tfit.BdBiProcSrvShEduOmc.dto.im.PpDishDetsDTO;
import com.tfit.BdBiProcSrvShEduOmc.dto.im.PpDishListDTO;
import com.tfit.BdBiProcSrvShEduOmc.dto.im.PpGsPlanOptsDTO;
import com.tfit.BdBiProcSrvShEduOmc.dto.im.PpNameCodesDTO;
import com.tfit.BdBiProcSrvShEduOmc.dto.im.PpRetSamplesDTO;
import com.tfit.BdBiProcSrvShEduOmc.dto.im.RecPersonCodesDTO;
import com.tfit.BdBiProcSrvShEduOmc.dto.im.RecUnitCodesDTO;
import com.tfit.BdBiProcSrvShEduOmc.dto.im.RmcKwDetsDTO;
import com.tfit.BdBiProcSrvShEduOmc.dto.im.RmcListDTO;
import com.tfit.BdBiProcSrvShEduOmc.dto.im.RsUnitCodesDTO;
import com.tfit.BdBiProcSrvShEduOmc.dto.im.SchKwDetsDTO;
import com.tfit.BdBiProcSrvShEduOmc.dto.im.SchPropCodesDTO;
import com.tfit.BdBiProcSrvShEduOmc.dto.im.SchSelModeCodesDTO;
import com.tfit.BdBiProcSrvShEduOmc.dto.im.SchTypeCodesDTO;
import com.tfit.BdBiProcSrvShEduOmc.dto.im.SendStatusCodesDTO;
import com.tfit.BdBiProcSrvShEduOmc.dto.im.SumDataDetsDTO;
import com.tfit.BdBiProcSrvShEduOmc.dto.im.WasteOilDetsDTO;
import com.tfit.BdBiProcSrvShEduOmc.dto.im.WasteOilsDTO;
import com.tfit.BdBiProcSrvShEduOmc.dto.im.week.AppTEduLedgerMasterTotalWObj;
import com.tfit.BdBiProcSrvShEduOmc.dto.im.week.AppTEduMaterialTotalWObj;
import com.tfit.BdBiProcSrvShEduOmc.dto.im.week.AppTEduPlatoonTotalWObj;
import com.tfit.BdBiProcSrvShEduOmc.dto.im.week.AppTEduReserveTotalW;
import com.tfit.BdBiProcSrvShEduOmc.dto.im.week.PpDishWeekOutDto;
import com.tfit.BdBiProcSrvShEduOmc.dto.im.week.PpGsPlanWeekOutDto;
import com.tfit.BdBiProcSrvShEduOmc.dto.im.week.PpMatConfirmWeekOutDto;
import com.tfit.BdBiProcSrvShEduOmc.dto.im.week.PpRetSamplesWeekOutDto;
import com.tfit.BdBiProcSrvShEduOmc.dto.info.manage.RecoveryWasteOilSummarySearchDTO;
import com.tfit.BdBiProcSrvShEduOmc.dto.info.manage.RmcRecoveryWasteOilSearchDTO;
import com.tfit.BdBiProcSrvShEduOmc.dto.info.manage.SchoolRecoveryWasteOilSearchDTO;
import com.tfit.BdBiProcSrvShEduOmc.dto.info.manage.WasteOilTypeCodeSearchDTO;
import com.tfit.BdBiProcSrvShEduOmc.model.vo.base.ApiResult;
import com.tfit.BdBiProcSrvShEduOmc.service.Db1Service;
import com.tfit.BdBiProcSrvShEduOmc.service.Db2Service;
import com.tfit.BdBiProcSrvShEduOmc.service.DbHiveDishService;
import com.tfit.BdBiProcSrvShEduOmc.service.DbHiveGsService;
import com.tfit.BdBiProcSrvShEduOmc.service.DbHiveMatService;
import com.tfit.BdBiProcSrvShEduOmc.service.DbHiveRecyclerWasteService;
import com.tfit.BdBiProcSrvShEduOmc.service.DbHiveRetSamplesService;
import com.tfit.BdBiProcSrvShEduOmc.service.DbHiveWarnService;
import com.tfit.BdBiProcSrvShEduOmc.service.SaasService;
import com.tfit.BdBiProcSrvShEduOmc.service.edu.EduSchoolService;
import com.tfit.BdBiProcSrvShEduOmc.service.info.manage.RecoveryWasteOilsService;
import com.tfit.BdBiProcSrvShEduOmc.util.ApiResultUtil;
import com.tfit.BdBiProcSrvShEduOmc.util.StringUtil;

/**
 * @Descritpion：业务数据-信息管理
 * @author: tianfang_infotech
 * @date: 2019/1/2 14:20
 */
@RestController
@RequestMapping(value = "/biOptAnl")
public class InfoManageController {

    private static final Logger logger = LogManager.getLogger(HomeController.class.getName());

    /**
     * mysql主数据库服务
     */
    @Autowired
    SaasService saasService;

    /**
     * mysql数据库服务1
     */
    @Autowired
    Db1Service db1Service;

    /**
     * mysql数据库服务2
     */
    @Autowired
    Db2Service db2Service;


    /**
     * hive数据库服务
     */
    @Autowired
    DbHiveWarnService dbHiveWarnService;

    /**
     * hive数据库服务
     */
    @Autowired
    DbHiveRecyclerWasteService dbHiveRecyclerWasteService;

    /**
     * hive数据库服务
     */
    @Autowired
    DbHiveDishService dbHiveDishService;

    /**
     * hive数据库服务
     */
    @Autowired
    DbHiveMatService dbHiveMatService;

    /**
     * hive数据库服务
     */
    @Autowired
    DbHiveGsService dbHiveGsService;

    /**
     * hive数据库服务
     */
    @Autowired
    DbHiveRetSamplesService dbHiveRetSamplesService;

    /**
     * 教委系统学校信息服务
     */
    @Autowired
    private EduSchoolService eduSchoolService;

    @Autowired
    ObjectMapper objectMapper;

    //学校筛选模式编码列表应用模型
    SchSelModeCodesAppMod ssmcAppMod = new SchSelModeCodesAppMod();

    //汇总数据详情列表应用模型
    SumDataDetsAppMod sddAppMod = new SumDataDetsAppMod();

    //导出汇总数据详情列表应用模型
    ExpSumDataDetsAppMod esddAppMod = new ExpSumDataDetsAppMod();

    /**
     * 回收废弃油脂服务
     */
    @Autowired
    private RecoveryWasteOilsService recoveryWasteOilsService;

    /**
     * 项目点排菜列表应用模型
     */
    PpDishListAppMod pdlAppMod = new PpDishListAppMod();

    /**
     * 导出项目点排菜列表应用模型
     */
    ExpPpDishListAppMod epdlAppMod = new ExpPpDishListAppMod();

    /**
     * 项目点排菜详情列表应用模型
     */
    PpDishDetsAppMod pddAppMod = new PpDishDetsAppMod();

    /**
     * 导出项目点排菜详情列表应用模型
     */
    ExpPpDishDetsAppMod epddAppMod = new ExpPpDishDetsAppMod();

    /**
     * 用料确认列表应用模型
     */
    MatConfirmsAppMod mcsAppMod = new MatConfirmsAppMod();

    /**
     * 导出用料确认列表应用模型
     */
    ExpMatConfirmsAppMod emcAppMod = new ExpMatConfirmsAppMod();

    /**
     * 用料确认详情列表应用模型
     */
    MatConfirmDetsAppMod mcdAppMod = new MatConfirmDetsAppMod();

    /**
     * 导出用料确认详情列表应用模型
     */
    ExpMatConfirmDetsAppMod emcdAppMod = new ExpMatConfirmDetsAppMod();

    /**
     * 配货计划操作列表应用模型
     */
    GsPlanOptsAppMod gpoAppMod = new GsPlanOptsAppMod();

    /**
     * 导出配货计划操作列表应用模型
     */
    ExpGsPlanOptsAppMod egpoAppMod = new ExpGsPlanOptsAppMod();

    /**
     * 导出配货计划操作列表应用模型
     */
    AssignStatusCodesAppMod ascAppMod = new AssignStatusCodesAppMod();

    /**
     * 配送状态编码列表应用模型
     */
    DispStatusCodesAppMod dscAppMod = new DispStatusCodesAppMod();

    /**
     * 验收状态编码列表应用模型
     */
    AcceptStatusCodesAppMod acscAppMod = new AcceptStatusCodesAppMod();

    /**
     * 配送类型编码列表应用模型
     */
    DispTypeCodesAppMod dtcAppMod = new DispTypeCodesAppMod();

    /**
     * 配送方式编码列表应用模型
     */
    DispModeCodesAppMod dmcAppMod = new DispModeCodesAppMod();

    /**
     * 配货计划操作详情列表应用模型
     */
    GsPlanOptDetsAppMod gpodAppMod = new GsPlanOptDetsAppMod();

    /**
     * 导出配货计划操作详情列表应用模型
     */
    ExpGsPlanOptDetsAppMod egpodAppMod = new ExpGsPlanOptDetsAppMod();

    /**
     * 菜品留样列表应用模型
     */
    DishRetSamplesAppMod drsAppMod = new DishRetSamplesAppMod();

    /**
     * 导出菜品留样列表应用模型
     */
    ExpDishRetSamplesAppMod edrsAppMod = new ExpDishRetSamplesAppMod();

    /**
     * 是否留样编码列表应用模型
     */
    IsRsCodesAppMod ircAppMod = new IsRsCodesAppMod();

    /**
     * 餐别编码列表应用模型
     */
    CaterTypeCodesAppMod ctcAppMod = new CaterTypeCodesAppMod();

    /**
     * 菜单名称编码列表应用模型
     */
    MenuNameCodesAppMod mncAppMod = new MenuNameCodesAppMod();

    /**
     * 留样单位编码列表应用模型
     */
    RsUnitCodesAppMod rucAppMod = new RsUnitCodesAppMod();

    /**
     * 菜品留样详情列表应用模型
     */
    DishRsDetsAppMod drdAppMod = new DishRsDetsAppMod();

    /**
     * 导出菜品留样详情列表应用模型
     */
    ExpDishRsDetsAppMod edrdAppMod = new ExpDishRsDetsAppMod();

    /**
     * 餐厨垃圾学校回收列表应用模型
     */
    KwSchRecsAppMod ksrAppMod = new KwSchRecsAppMod();

    /**
     * 导出餐厨垃圾学校回收列表应用模型
     */
    ExpKwSchRecsAppMod eksrAppMod = new ExpKwSchRecsAppMod();

    /**
     * 回收单位编码列表应用模型
     */
    RecUnitCodesAppMod reucAppMod = new RecUnitCodesAppMod();

    /**
     * 回收人编码列表应用模型
     */
    RecPersonCodesAppMod rpcAppMod = new RecPersonCodesAppMod();

    /**
     * 学校餐厨垃圾详情列表应用模型
     */
    SchKwDetsAppMod skdAppMod = new SchKwDetsAppMod();

    /**
     * 导出学校餐厨垃圾详情列表应用模型
     */
    ExpSchKwDetsAppMod eskdAppMod = new ExpSchKwDetsAppMod();

    /**
     * 餐厨垃圾团餐公司回收列表应用模型
     */
    KwRmcRecsAppMod krrAppMod = new KwRmcRecsAppMod();

    /**
     * 导出餐厨垃圾团餐公司回收列表应用模型
     */
    ExpKwRmcRecsAppMod ekrrAppMod = new ExpKwRmcRecsAppMod();

    /**
     * 团餐公司餐厨垃圾详情列表应用模型
     */
    RmcKwDetsAppMod rkdAppMod = new RmcKwDetsAppMod();

    /**
     * 导出团餐公司餐厨垃圾详情列表应用模型
     */
    ExpRmcKwDetsAppMod erkdAppMod = new ExpRmcKwDetsAppMod();

    /**
     * 废弃油脂列表应用模型
     */
    WasteOilsAppMod wasAppMod = new WasteOilsAppMod();

    /**
     * 废弃油脂详情列表应用模型
     */
    WasteOilDetsAppMod wodAppMod = new WasteOilDetsAppMod();

    /**
     * 区域列表应用模型
     */
    DistListAppMod dilAppMod = new DistListAppMod();

    /**
     * 团餐公司列表应用模型
     */
    RmcListAppMod rmlAppMod = new RmcListAppMod();

    /**
     * 项目点编码列表应用模型
     */
    PpNameCodesAppMod pncAppMod = new PpNameCodesAppMod();

    /**
     * 学校性质编码列表应用模型
     */
    SchPropCodesAppMod spcAppMod = new SchPropCodesAppMod();

    /**
     * 用料类别编码列表应用模型
     */
    MatTypeCodesAppMod mtcAppMod = new MatTypeCodesAppMod();

    /**
     * 排菜执行编码列表应用模型
     */
    DishExeListAppMod delAppMod = new DishExeListAppMod();

    /**
     * 学校类型（学制）编码列表应用模型
     */
    SchTypeCodesAppMod stcAppMod = new SchTypeCodesAppMod();

    /**
     * 是否供餐编码列表应用模型
     */
    IsMealCodesAppMod imcAppMod = new IsMealCodesAppMod();

    /**
     * 经营模式编码列表应用模型
     */
    OptModeCodesAppMod omcAppMod = new OptModeCodesAppMod();

    /**
     * 发送状态编码列表应用模型
     */
    SendStatusCodesAppMod sscAppMod = new SendStatusCodesAppMod();

    /**
     * 用料计划确认编码列表应用模型
     */
    MatConfirmListAppMod mclAppMod = new MatConfirmListAppMod();

    /**
     * 3.2.41.	项目点配货计划操作列表应用模型
     */
    PpGsPlanOptsAppMod ppGsPlanOptsAppMod = new PpGsPlanOptsAppMod();

    /**
     * 3.2.53.	项目点留样列表应用模型
     */
    PpRetSamplesAppMod ppRetSamplesAppMod = new PpRetSamplesAppMod();

    /**
     * 3.2.42.	导出项目点配货计划操作列表应用模型
     */
    ExpPpGsPlanOptsAppMod expPpGsPlanOptsAppMod = new ExpPpGsPlanOptsAppMod();

    /**
     * 3.2.54.	导出项目点留样列表
     */
    ExpPpRetSamplesAppMod expPpRetSamplesAppMod = new ExpPpRetSamplesAppMod();

    /**
     * 排菜周报表
     */
    PpDishWeekAppMod ppDishWeekAppMod = new PpDishWeekAppMod();
    ExpPpDishWeekAppMod expPpDishWeekAppMod = new ExpPpDishWeekAppMod();

    PpGsPlanWeekAppMod ppGsPlanWeekAppMod = new PpGsPlanWeekAppMod();
    PpMatConfirmWeekAppMod ppMatConfirmWeekAppMod = new PpMatConfirmWeekAppMod();

    PpRetSamplesWeekAppMod ppRetSamplesWeekAppMod = new PpRetSamplesWeekAppMod();

    //学校筛选模式编码列表
    @RequestMapping(value = "/v1/schSelModeCodes", method = RequestMethod.GET)
    public String v1_schSelModeCodes(HttpServletRequest request) {
        //初始化响应数据
        String strResp = null;
        SchSelModeCodesDTO ssmcDto = null;
        boolean isAuth = false;
        int code = 0;
        int[] codes = new int[1];
        //授权码
        String token = AppModConfig.GetHeadJsonReq(request, "Authorization");
        //区域名称
        String distName = request.getParameter("distName");
        if (distName == null) {
            distName = request.getParameter("distname");
        }
        //地级城市
        String prefCity = request.getParameter("prefCity");
        if (prefCity == null) {
            prefCity = request.getParameter("prefcity");
        }
        //省或直辖市
        String province = request.getParameter("province");

        //省或直辖市
        String mode = request.getParameter("mode");
        //页号
        String page = request.getParameter("page");
        if (page == null) {
            page = "1";
        }
        //分页大小
        String pageSize = request.getParameter("pageSize");
        if (pageSize == null) {
            pageSize = String.valueOf(AppModConfig.maxPageSize);
        }
        logger.info("输入参数：" + "token = " + token + ", distName = " + distName + ", prefCity = " + prefCity
                + ", province = " + province + ", page = " + page + ", pageSize = " + pageSize);
        //验证授权
        isAuth = AppModConfig.verifyAuthCode2(token, db2Service, codes);
        //学校筛选模式编码列表应用模型函数
        if (isAuth)
            ssmcDto = ssmcAppMod.appModFunc(token, distName, prefCity, province, mode, db1Service, db2Service);
        else
            logger.info("授权码：" + token + "，验证失败！");
        //设置响应数据
        if (ssmcDto != null) {
            try {
                strResp = objectMapper.writeValueAsString(ssmcDto);
                logger.info(strResp);
            } catch (JsonProcessingException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        } else {
            //获取异常响应
            code = codes[0];
            logger.info("错误码：code = " + code);
            IOTHttpRspVO excepResp = AppModConfig.getExcepResp(code);
            try {
                strResp = objectMapper.writeValueAsString(excepResp);
                logger.info(strResp);
            } catch (JsonProcessingException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }

        return strResp;
    }

    //汇总数据详情列表
    @RequestMapping(value = "/v1/sumDataDets", method = RequestMethod.GET)
    public String v1_sumDataDets(HttpServletRequest request) {
        //初始化响应数据
        String strResp = null;
        SumDataDetsDTO sddDto = null;
        boolean isAuth = false;
        int code = 0;
        int[] codes = new int[1];
        //授权码
        String token = AppModConfig.GetHeadJsonReq(request, "Authorization");
        //开始日期，默认为当天日期，格式：xxxx-xx-xx
        String startDate = request.getParameter("startSubDate");
        //结束日期，默认为当天日期，格式：xxxx-xx-xx
        String endDate = request.getParameter("endSubDate");
        //学校筛选方式，0:按主管部门，1:按所在地
        String schSelMode = request.getParameter("schSelMode");
        //所属，0:其他，1:部属，2:市属，3: 区属，按主管部门有效
        String subLevel = request.getParameter("subLevel");
        //主管部门，按主管部门有效
        String compDep = request.getParameter("compDep");
        //所属区域名称，按主管部门有效
        String subDistName = request.getParameter("subDistName");
        //区域名称
        String distName = request.getParameter("distName");
        if (distName == null) {
            distName = request.getParameter("distname");
        }
        //地级城市
        String prefCity = request.getParameter("prefCity");
        if (prefCity == null) {
            prefCity = request.getParameter("prefcity");
        }
        //省或直辖市
        String province = request.getParameter("province");
        //页号
        String page = request.getParameter("page");
        if (page == null) {
            page = "1";
        }
        //分页大小
        String pageSize = request.getParameter("pageSize");
        if (pageSize == null) {
            pageSize = "20";
        }

        //所属，0:其他，1:部属，2:市属，3: 区属，按主管部门有效(多个[1,3])
        String subLevels = request.getParameter("subLevels");
        //主管部门，按主管部门有效(多个[1_1,3_1])
        String compDeps = request.getParameter("compDeps");
        //区域名称(多个[1,3])
        String distNames = request.getParameter("distNames");

        //管理部门集合(多个[1,3])
        String departmentIds = request.getParameter("departmentIds");
        //管理部门
        String departmentId = request.getParameter("departmentId");

        logger.info("输入参数：" + "token = " + token + ", startDate = " + startDate + ", endDate = " + endDate
                + ", schSelMode = " + schSelMode + ", subLevel = " + subLevel + ", compDep = " + compDep
                + ", subDistName = " + subDistName + ", distName = " + distName + ", prefCity = " + prefCity
                + ", province = " + province + ", page = " + page + ", pageSize = " + pageSize
                + ", subLevels = " + subLevels + ", compDeps = " + compDeps + ", distNames = " + distNames);
        //验证授权
        isAuth = AppModConfig.verifyAuthCode2(token, db2Service, codes);
        //汇总数据详情列表应用模型函数
        if (isAuth)
            sddDto = sddAppMod.appModFunc(token, startDate, endDate, schSelMode, subLevel, compDep, subDistName,
                    distName, departmentId, prefCity, province,
                    subLevels, compDeps, distNames, departmentIds,
                    page, pageSize, db1Service, db2Service, dbHiveWarnService,
                    dbHiveRecyclerWasteService, dbHiveDishService, dbHiveGsService);
        else
            logger.info("授权码：" + token + "，验证失败！");
        //设置响应数据
        if (sddDto != null) {
            try {
                strResp = objectMapper.writeValueAsString(sddDto);
                logger.info(strResp);
            } catch (JsonProcessingException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        } else {
            //获取异常响应
            code = codes[0];
            logger.info("错误码：code = " + code);
            IOTHttpRspVO excepResp = AppModConfig.getExcepResp(code);
            try {
                strResp = objectMapper.writeValueAsString(excepResp);
                logger.info(strResp);
            } catch (JsonProcessingException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }

        return strResp;
    }

    //导出汇总数据详情列表
    @RequestMapping(value = "/v1/expSumDataDets", method = RequestMethod.GET)
    public String v1_expSumDataDets(HttpServletRequest request, HttpServletResponse response) {
        //初始化响应数据
        String strResp = null;
        ExpSumDataDetsDTO esddDto = null;
        boolean isAuth = false;
        int code = 0;
        int[] codes = new int[1];
        //授权码
        String token = AppModConfig.GetHeadJsonReq(request, "Authorization");
        //开始日期
        String startDate = request.getParameter("startSubDate");
        //结束日期
        String endDate = request.getParameter("endSubDate");
        //学校筛选方式，0:按主管部门，1:按所在地
        String schSelMode = request.getParameter("schSelMode");
        //所属，0:其他，1:部属，2:市属，3: 区属，按主管部门有效
        String subLevel = request.getParameter("subLevel");
        //主管部门，按主管部门有效
        String compDep = request.getParameter("compDep");
        //所属区域名称，按主管部门有效
        String subDistName = request.getParameter("subDistName");
        //区域名称
        String distName = request.getParameter("distName");
        if (distName == null) {
            distName = request.getParameter("distname");
        }
        //地级城市
        String prefCity = request.getParameter("prefCity");
        if (prefCity == null) {
            prefCity = request.getParameter("prefcity");
        }
        //省或直辖市
        String province = request.getParameter("province");

        //所属，0:其他，1:部属，2:市属，3: 区属，按主管部门有效(多个[1,3])
        String subLevels = request.getParameter("subLevels");
        //主管部门，按主管部门有效(多个[1_1,3_1])
        String compDeps = request.getParameter("compDeps");
        //区域名称(多个[1,3])
        String distNames = request.getParameter("distNames");

        //管理部门集合(多个[1,3])
        String departmentIds = request.getParameter("departmentIds");
        //管理部门
        String departmentId = request.getParameter("departmentId");

        logger.info("输入参数：" + "token = " + token + ", startDate = " + startDate
                + ", endDate = " + endDate + ", distName = " + distName
                + ", prefCity = " + prefCity + ", province = " + province);
        //验证授权
        isAuth = AppModConfig.verifyAuthCode2(token, db2Service, codes);
        //导出汇总数据详情列表应用模型函数
        if (isAuth)
            esddDto = esddAppMod.appModFunc(token, startDate, endDate, schSelMode, subLevel,
                    compDep, subDistName, distName, departmentId, prefCity, province,
                    subLevels, compDeps, distNames, departmentIds,
                    db1Service, db2Service, dbHiveWarnService, dbHiveRecyclerWasteService,
                    dbHiveDishService, dbHiveGsService);
        else
            logger.info("授权码：" + token + "，验证失败！");
        //设置响应数据
        if (esddDto != null) {
            try {
                strResp = objectMapper.writeValueAsString(esddDto);
                logger.info(strResp);
            } catch (JsonProcessingException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        } else {
            //获取异常响应
            code = codes[0];
            logger.info("错误码：code = " + code);
            IOTHttpRspVO excepResp = AppModConfig.getExcepResp(code);
            try {
                strResp = objectMapper.writeValueAsString(excepResp);
                logger.info(strResp);
            } catch (JsonProcessingException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }

        return strResp;
    }

    /**
     * 3.2.1 - 项目点排菜列表
     *
     * @param request
     * @return
     */
    @RequestMapping(value = "/v1/ppDishList", method = RequestMethod.GET)
    public String v1_ppDishList(HttpServletRequest request) {
        //初始化响应数据
        String strResp = null;
        PpDishListDTO pdlDto = null;
        boolean isAuth = false;
        int code = 0;
        int[] codes = new int[1];
        //授权码
        String token = AppModConfig.GetHeadJsonReq(request, "Authorization");
        //开始日期
        String startDate = request.getParameter("startSubDate");
        //结束日期
        String endDate = request.getParameter("endSubDate");
        //学校筛选方式，0:按主管部门，1:按所在地
        String schSelMode = request.getParameter("schSelMode");
        //所属，0:其他，1:部属，2:市属，3: 区属，按主管部门有效
        String subLevel = request.getParameter("subLevel");
        //主管部门，按主管部门有效
        String compDep = request.getParameter("compDep");
        //所属区域名称，按主管部门有效
        String subDistName = request.getParameter("subDistName");
        //区域名称
        String distName = request.getParameter("distName");
        if (distName == null) {
            distName = request.getParameter("distname");
        }
        //地级城市
        String prefCity = request.getParameter("prefCity");
        if (prefCity == null) {
            prefCity = request.getParameter("prefcity");
        }
        //省或直辖市
        String province = request.getParameter("province");
        //页号
        String page = request.getParameter("page");
        if (page == null) {
            page = "1";
        }
        //分页大小
        String pageSize = request.getParameter("pageSize");
        if (pageSize == null) {
            pageSize = "20";
        }

        //所属，0:其他，1:部属，2:市属，3: 区属，按主管部门有效(多个[1,3])
        String subLevels = request.getParameter("subLevels");
        //主管部门，按主管部门有效(多个[1_1,3_1])
        String compDeps = request.getParameter("compDeps");
        //区域名称(多个[1,3])
        String distNames = request.getParameter("distNames");

        //管理部门集合(多个[1,3])
        String departmentIds = request.getParameter("departmentIds");
        //管理部门
        String departmentId = request.getParameter("departmentId");

        logger.info("输入参数：" + "token = " + token + ", startDate = " + startDate + ", endDate = " + endDate
                + ", schSelMode = " + schSelMode + ", subLevel = " + subLevel + ", compDep = " + compDep
                + ", subDistName = " + subDistName + ", distName = " + distName + ", prefCity = " + prefCity
                + ", province = " + province + ", page = " + page + ", pageSize = " + pageSize
                + ", subLevels = " + subLevels + ", compDeps = " + compDeps + ", distNames = " + distNames);
        //验证授权
        isAuth = AppModConfig.verifyAuthCode2(token, db2Service, codes);
        //项目点排菜列表应用模型函数
        if (isAuth)
            pdlDto = pdlAppMod.appModFunc(token, startDate, endDate, schSelMode, subLevel, compDep,
                    subDistName, distName, departmentId, prefCity, province, subLevels, compDeps, distNames, departmentIds,
                    page, pageSize, db1Service, db2Service, dbHiveDishService);
        else
            logger.info("授权码：" + token + "，验证失败！");
        //设置响应数据
        if (pdlDto != null) {
            try {
                strResp = objectMapper.writeValueAsString(pdlDto);
                logger.info(strResp);
            } catch (JsonProcessingException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        } else {
            //获取异常响应
            code = codes[0];
            logger.info("错误码：code = " + code);
            IOTHttpRspVO excepResp = AppModConfig.getExcepResp(code);
            try {
                strResp = objectMapper.writeValueAsString(excepResp);
                logger.info(strResp);
            } catch (JsonProcessingException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }

        return strResp;
    }

    /**
     * 3.2.2 - 导出项目点排菜列表
     *
     * @param request
     * @param response
     * @return
     */
    @RequestMapping(value = "/v1/expPpDishList", method = RequestMethod.GET)
    public String v1_expPpDishList(HttpServletRequest request, HttpServletResponse response) {
        //初始化响应数据
        String strResp = null;
        ExpPpDishListDTO epdlDto = null;
        boolean isAuth = false;
        int code = 0;
        int[] codes = new int[1];
        //授权码
        String token = AppModConfig.GetHeadJsonReq(request, "Authorization");
        //开始日期
        String startDate = request.getParameter("startSubDate");
        //结束日期
        String endDate = request.getParameter("endSubDate");
        //学校筛选方式，0:按主管部门，1:按所在地
        String schSelMode = request.getParameter("schSelMode");
        //所属，0:其他，1:部属，2:市属，3: 区属，按主管部门有效
        String subLevel = request.getParameter("subLevel");
        //主管部门，按主管部门有效
        String compDep = request.getParameter("compDep");
        //所属区域名称，按主管部门有效
        String subDistName = request.getParameter("subDistName");
        //区域名称
        String distName = request.getParameter("distName");
        if (distName == null) {
            distName = request.getParameter("distname");
        }
        //地级城市
        String prefCity = request.getParameter("prefCity");
        if (prefCity == null) {
            prefCity = request.getParameter("prefcity");
        }

        //所属，0:其他，1:部属，2:市属，3: 区属，按主管部门有效(多个[1,3])
        String subLevels = request.getParameter("subLevels");
        //主管部门，按主管部门有效(多个[1_1,3_1])
        String compDeps = request.getParameter("compDeps");
        //区域名称(多个[1,3])
        String distNames = request.getParameter("distNames");

        //管理部门集合(多个[1,3])
        String departmentIds = request.getParameter("departmentIds");
        //管理部门
        String departmentId = request.getParameter("departmentId");

        //省或直辖市
        String province = request.getParameter("province");
        logger.info("输入参数：" + "token = " + token + ", startDate = " + startDate + ", endDate = " + endDate
                + ", schSelMode = " + schSelMode + ", subLevel = " + subLevel
                + ", compDep = " + compDep + ", subDistName = " + subDistName
                + ", distName = " + distName + ", prefCity = " + prefCity
                + ", province = " + province
                + ", subLevels = " + subLevels + ", compDeps = " + compDeps + ", distNames = " + distNames);
        //验证授权
        isAuth = AppModConfig.verifyAuthCode2(token, db2Service, codes);
        //导出项目点排菜列表应用模型函数
        if (isAuth)
            epdlDto = epdlAppMod.appModFunc(token, startDate, endDate, schSelMode, subLevel,
                    compDep, subDistName, distName, departmentId, prefCity, province, subLevels, compDeps, distNames, departmentIds,
                    db1Service, db2Service, dbHiveDishService);
        else
            logger.info("授权码：" + token + "，验证失败！");
        //设置响应数据
        if (epdlDto != null) {
            try {
                strResp = objectMapper.writeValueAsString(epdlDto);
                logger.info(strResp);
            } catch (JsonProcessingException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        } else {
            //获取异常响应
            code = codes[0];
            logger.info("错误码：code = " + code);
            IOTHttpRspVO excepResp = AppModConfig.getExcepResp(code);
            try {
                strResp = objectMapper.writeValueAsString(excepResp);
                logger.info(strResp);
            } catch (JsonProcessingException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }

        return strResp;
    }

    /**
     * 3.2.3 - 区域列表
     *
     * @param request
     * @return
     */
    @RequestMapping(value = "/v1/distList", method = RequestMethod.GET)
    public String v1_distList(HttpServletRequest request) {
        //初始化响应数据
        String strResp = null;
        DistListDTO dilDto = null;
        boolean isAuth = false;
        int code = 0;
        int[] codes = new int[1];
        //授权码
        String token = AppModConfig.GetHeadJsonReq(request, "Authorization");
        //地级城市
        String prefCity = request.getParameter("prefCity");
        if (prefCity == null) {
            prefCity = request.getParameter("prefcity");
        }
        //省或直辖市
        String province = request.getParameter("province");
        logger.info("输入参数：" + "token = " + token + ", prefCity = " + prefCity + ", province = " + province);
        //验证授权
        isAuth = AppModConfig.verifyAuthCode2(token, db2Service, codes);
        //区域列表应用模型函数
        if (isAuth)
            dilDto = dilAppMod.appModFunc(token, prefCity, province, db1Service, db2Service);
        else
            logger.info("授权码：" + token + "，验证失败！");
        //设置响应数据
        if (dilDto != null) {
            try {
                strResp = objectMapper.writeValueAsString(dilDto);
                logger.info(strResp);
            } catch (JsonProcessingException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        } else {
            //获取异常响应
            code = codes[0];
            logger.info("错误码：code = " + code);
            IOTHttpRspVO excepResp = AppModConfig.getExcepResp(code);
            try {
                strResp = objectMapper.writeValueAsString(excepResp);
                logger.info(strResp);
            } catch (JsonProcessingException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }

        return strResp;
    }

    /**
     * 3.2.4 - 项目点排菜详情列表
     *
     * @param request
     * @return
     */
    @RequestMapping(value = "/v1/ppDishDets", method = RequestMethod.GET)
    public String v1_ppDishDets(HttpServletRequest request) {
        //初始化响应数据
        String strResp = null;
        PpDishDetsDTO pddDto = null;
        boolean isAuth = false;
        int code = 0;
        int[] codes = new int[1];
        //授权码
        String token = AppModConfig.GetHeadJsonReq(request, "Authorization");
        //开始日期
        String startDate = request.getParameter("startSubDate");
        //结束日期
        String endDate = request.getParameter("endSubDate");
        //项目点名称
        String ppName = request.getParameter("ppName");
        //区域名称
        String distName = request.getParameter("distName");
        if (distName == null) {
            distName = request.getParameter("distname");
        }
        //地级城市
        String prefCity = request.getParameter("prefCity");
        if (prefCity == null) {
            prefCity = request.getParameter("prefcity");
        }
        //省或直辖市
        String province = request.getParameter("province");
        //所属，0:其他，1:部属，2:市属，3: 区属
        String subLevel = request.getParameter("subLevel");
        //主管部门，0:市教委，1:商委，2:教育部
        String compDep = request.getParameter("compDep");
        //总分校标识，0:无，1:总校，2:分校
        String schGenBraFlag = request.getParameter("schGenBraFlag");
        //所属区域名称
        String subDistName = request.getParameter("subDistName");
        //证件主体，0:学校，1:外包
        String fblMb = request.getParameter("fblMb");
        //学校性质，0:公办，1:民办，2:其他
        String schProp = request.getParameter("schProp");
        //是否排菜，0:未排菜，1:已排菜
        String dishFlag = request.getParameter("dishFlag");
        //团餐公司名称
        String rmcName = request.getParameter("rmcName");
        //学校类型（学制）
        String schType = request.getParameter("schType");
        //是否供餐，0:否，1:是
        String mealFlag = request.getParameter("mealFlag");
        //经营模式
        String optMode = request.getParameter("optMode");
        //发送状态，0:未发送，1:已发送
        String sendFlag = request.getParameter("sendFlag");
        //页号
        String page = request.getParameter("page");
        if (page == null) {
            page = "1";
        }
        //分页大小
        String pageSize = request.getParameter("pageSize");
        if (pageSize == null) {
            pageSize = "20";
        }

        //区域名称 格式：[“1”,”2”……]
        String distNames = request.getParameter("distNames");
        //所属 格式：[“1”,”2”……]
        String subLevels = request.getParameter("subLevels");
        //主管部门 格式：[“1”,”2”……]
        String compDeps = request.getParameter("compDeps");
        //学校性质 格式：[“1”,”2”……]
        String schProps = request.getParameter("schProps");
        //学校类型 格式：[“1”,”2”……]
        String schTypes = request.getParameter("schTypes");
        //经营模式 格式：[“1”,”2”……]
        String optModes = request.getParameter("optModes");
        //所属区(sub) 格式：[“1”,”2”……]
        String subDistNames = request.getParameter("subDistNames");

        String departmentId = request.getParameter("departmentId");
        String departmentIds = request.getParameter("departmentIds");

        String plastatus = request.getParameter("plastatus");
        String reason = request.getParameter("reason");

        logger.info("输入参数：" + "token = " + token + ", startDate = " + startDate
                + ", endDate = " + endDate + ", ppName = " + ppName + ", distName = " + distName
                + ", prefCity = " + prefCity + ", province = " + province
                + ", subLevel = " + subLevel + ", compDep = " + compDep
                + ", schGenBraFlag = " + schGenBraFlag + ", subDistName = " + subDistName
                + ", fblMb = " + fblMb + ", schProp = " + schProp + ", dishFlag = " + dishFlag
                + ", rmcName = " + rmcName + ", schType = " + schType + ", mealFlag = " + mealFlag
                + ", optMode = " + optMode + ", sendFlag = " + sendFlag
                + ", page = " + page + ", pageSize = " + pageSize
                + ", distNames = " + distNames + ", subLevels = " + subLevels
                + ", compDeps = " + compDeps + ", schProps = " + schProps
                + ", schTypes = " + schTypes + ", optModes = " + optModes
                + ", subDistNames = " + subDistNames);
        //验证授权
        isAuth = AppModConfig.verifyAuthCode2(token, db2Service, codes);
        //项目点排菜详情列表应用模型函数
        if (isAuth)
            pddDto = pddAppMod.appModFunc(token, startDate, endDate, ppName, distName, prefCity,
                    province, subLevel, compDep, schGenBraFlag, subDistName, fblMb, schProp, dishFlag,
                    rmcName, schType, mealFlag, optMode, sendFlag,
                    distNames, subLevels, compDeps, schProps, schTypes, optModes, subDistNames,
                    departmentId, departmentIds, plastatus, reason, null,
                    page, pageSize,
                    db1Service, db2Service, saasService, dbHiveDishService);
        else
            logger.info("授权码：" + token + "，验证失败！");
        //设置响应数据
        if (pddDto != null) {
            try {
                strResp = objectMapper.writeValueAsString(pddDto);
                logger.info(strResp);
            } catch (JsonProcessingException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        } else {
            //获取异常响应
            code = codes[0];
            logger.info("错误码：code = " + code);
            IOTHttpRspVO excepResp = AppModConfig.getExcepResp(code);
            try {
                strResp = objectMapper.writeValueAsString(excepResp);
                logger.info(strResp);
            } catch (JsonProcessingException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }

        return strResp;
    }

    /**
     * 3.2.5 - 导出项目点排菜详情列表
     *
     * @param request
     * @return
     */
    @RequestMapping(value = "/v1/expPpDishDets", method = RequestMethod.GET)
    public String v1_expPpDishDets(HttpServletRequest request) {
        //初始化响应数据
        String strResp = null;
        ExpPpDishDetsDTO epddDto = null;
        boolean isAuth = false;
        int code = 0;
        int[] codes = new int[1];
        //授权码
        String token = AppModConfig.GetHeadJsonReq(request, "Authorization");
        //开始日期，默认为当天日期，格式：xxxx-xx-xx
        String startDate = request.getParameter("startSubDate");
        //结束日期，默认为当天日期，格式：xxxx-xx-xx
        String endDate = request.getParameter("endSubDate");
        //项目点名称
        String ppName = request.getParameter("ppName");
        //区域名称
        String distName = request.getParameter("distName");
        if (distName == null) {
            distName = request.getParameter("distname");
        }
        //地级城市
        String prefCity = request.getParameter("prefCity");
        if (prefCity == null) {
            prefCity = request.getParameter("prefcity");
        }
        //省或直辖市
        String province = request.getParameter("province");
        //所属，0:其他，1:部属，2:市属，3: 区属
        String subLevel = request.getParameter("subLevel");
        //主管部门，0:市教委，1:商委，2:教育部
        String compDep = request.getParameter("compDep");
        //总分校标识，0:无，1:总校，2:分校
        String schGenBraFlag = request.getParameter("schGenBraFlag");
        //所属区域名称
        String subDistName = request.getParameter("subDistName");
        //证件主体，0:学校，1:外包
        String fblMb = request.getParameter("fblMb");
        //学校性质，0:公办，1:民办，2:其他
        String schProp = request.getParameter("schProp");
        //是否排菜，0:未排菜，1:已排菜
        String dishFlag = request.getParameter("dishFlag");
        //团餐公司名称或ID
        String rmcName = request.getParameter("rmcName");
        //学校类型（学制），0:托儿所，1:托幼园，2:托幼小，3:幼儿园，4:幼小，5:幼小初，6:幼小初高，7:小学，8:初级中学，9:高级中学，10:完全中学，11:九年一贯制学校，12:十二年一贯制学校，13:职业初中，14:中等职业学校，15:工读学校，16:特殊教育学校，17:其他
        String schType = request.getParameter("schType");
        //是否供餐，0:否，1:是
        String mealFlag = request.getParameter("mealFlag");
        //经营模式，0:自营，1:外包-现场加工，2:外包-快餐配送
        String optMode = request.getParameter("optMode");
        //发送状态，0:未发送，1:已发送
        String sendFlag = request.getParameter("sendFlag");

        //区域名称 格式：[“1”,”2”……]
        String distNames = request.getParameter("distNames");
        //所属 格式：[“1”,”2”……]
        String subLevels = request.getParameter("subLevels");
        //主管部门 格式：[“1”,”2”……]
        String compDeps = request.getParameter("compDeps");
        //学校性质 格式：[“1”,”2”……]
        String schProps = request.getParameter("schProps");
        //学校类型 格式：[“1”,”2”……]
        String schTypes = request.getParameter("schTypes");
        //经营模式 格式：[“1”,”2”……]
        String optModes = request.getParameter("optModes");
        //所属区(sub) 格式：[“1”,”2”……]
        String subDistNames = request.getParameter("subDistNames");

        String departmentId = request.getParameter("departmentId");
        String departmentIds = request.getParameter("departmentIds");

        String plastatus = request.getParameter("plastatus");
        String reason = request.getParameter("reason");

        logger.info("输入参数：" + "token = " + token + ", startDate = " + startDate
                + ", endDate = " + endDate + ", ppName = " + ppName
                + ", distName = " + distName + ", prefCity = " + prefCity
                + ", province = " + province + ", subLevel = " + subLevel + ", compDep = " + compDep
                + ", schGenBraFlag = " + schGenBraFlag + ", subDistName = " + subDistName
                + ", fblMb = " + fblMb + ", schProp = " + schProp + ", dishFlag = " + dishFlag
                + ", rmcName = " + rmcName + ", schType = " + schType
                + ", mealFlag = " + mealFlag + ", optMode = " + optMode
                + ", sendFlag = " + sendFlag
                + ", distNames = " + distNames + ", subLevels = " + subLevels
                + ", compDeps = " + compDeps + ", schProps = " + schProps
                + ", schTypes = " + schTypes + ", optModes = " + optModes
                + ", subDistNames = " + subDistNames);
        //验证授权
        isAuth = AppModConfig.verifyAuthCode2(token, db2Service, codes);
        //导出项目点排菜详情列表应用模型函数
        if (isAuth)
            epddDto = epddAppMod.appModFunc(token, startDate, endDate, ppName,
                    distName, prefCity, province, subLevel, compDep, schGenBraFlag,
                    subDistName, fblMb, schProp, dishFlag, rmcName, schType, mealFlag, optMode, sendFlag,
                    distNames, subLevels, compDeps, schProps, schTypes, optModes, subDistNames,
                    departmentId, departmentIds, plastatus, reason,
                    db1Service, db2Service, saasService, dbHiveDishService);
        else
            logger.info("授权码：" + token + "，验证失败！");
        //设置响应数据
        if (epddDto != null) {
            try {
                strResp = objectMapper.writeValueAsString(epddDto);
                logger.info(strResp);
            } catch (JsonProcessingException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        } else {
            //获取异常响应
            code = codes[0];
            logger.info("错误码：code = " + code);
            IOTHttpRspVO excepResp = AppModConfig.getExcepResp(code);
            try {
                strResp = objectMapper.writeValueAsString(excepResp);
                logger.info(strResp);
            } catch (JsonProcessingException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }

        return strResp;
    }

    /**
     * 3.2.6 - 项目点编码列表
     *
     * @param request
     * @return
     */
    @RequestMapping(value = "/v1/ppNameCodes", method = RequestMethod.GET)
    public String v1_ppNameCodes(HttpServletRequest request) {
        //初始化响应数据
        String strResp = null;
        PpNameCodesDTO pncDto = null;
        boolean isAuth = false;
        int code = 0;
        int[] codes = new int[1];
        //授权码
        String token = AppModConfig.GetHeadJsonReq(request, "Authorization");
        //区域名称
        String distName = request.getParameter("distName");
        if (distName == null) {
            distName = request.getParameter("distname");
        }
        //地级城市
        String prefCity = request.getParameter("prefCity");
        if (prefCity == null) {
            prefCity = request.getParameter("prefcity");
        }
        //省或直辖市
        String province = request.getParameter("province");
        //页号
        String page = request.getParameter("page");
        if (page == null) {
            page = "1";
        }
        //分页大小
        String pageSize = request.getParameter("pageSize");
        if (pageSize == null) {
            pageSize = String.valueOf(AppModConfig.maxPageSize);
        }
        logger.info("输入参数：" + "token = " + token + ", distName = " + distName + ", prefCity = " + prefCity + ", province = " + province + ", page = " + page + ", pageSize = " + pageSize);
        //验证授权
        isAuth = AppModConfig.verifyAuthCode2(token, db2Service, codes);
        //项目点编码列表应用模型函数
        if (isAuth)
            pncDto = pncAppMod.appModFunc(token, distName, prefCity, province, page, pageSize, db1Service, db2Service, saasService);
        else
            logger.info("授权码：" + token + "，验证失败！");
        //设置响应数据
        if (pncDto != null) {
            try {
                strResp = objectMapper.writeValueAsString(pncDto);
                logger.info(strResp);
            } catch (JsonProcessingException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        } else {
            //获取异常响应
            code = codes[0];
            logger.info("错误码：code = " + code);
            IOTHttpRspVO excepResp = AppModConfig.getExcepResp(code);
            try {
                strResp = objectMapper.writeValueAsString(excepResp);
                logger.info(strResp);
            } catch (JsonProcessingException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }

        return strResp;
    }

    /**
     * 3.2.7 - 团餐公司列表
     *
     * @param request
     * @return
     */
    @RequestMapping(value = "/v1/rmcList", method = RequestMethod.GET)
    public String v1_rmcList(HttpServletRequest request) {
        //初始化响应数据
        String strResp = null;
        RmcListDTO rmlDto = null;
        boolean isAuth = false;
        int code = 0;
        int[] codes = new int[1];
        //授权码
        String token = AppModConfig.GetHeadJsonReq(request, "Authorization");
        //区域名称
        String distName = request.getParameter("distName");
        if (distName == null) {
            distName = request.getParameter("distname");
        }
        //地级城市
        String prefCity = request.getParameter("prefCity");
        if (prefCity == null) {
            prefCity = request.getParameter("prefcity");
        }
        //省或直辖市
        String province = request.getParameter("province");
        //页号
        String page = request.getParameter("page");
        if (page == null) {
            page = "1";
        }
        //分页大小
        String pageSize = request.getParameter("pageSize");
        if (pageSize == null) {
            pageSize = String.valueOf(AppModConfig.maxPageSize);
        }
        logger.info("输入参数：" + "token = " + token + ", distName = " + distName + ", prefCity = " + prefCity + ", province = " + province + ", page = " + page + ", pageSize = " + pageSize);
        //验证授权
        isAuth = AppModConfig.verifyAuthCode2(token, db2Service, codes);
        //团餐公司列表应用模型函数
        if (isAuth)
            rmlDto = rmlAppMod.appModFunc(token, distName, prefCity, province, page, pageSize, db1Service, db2Service, saasService);
        else
            logger.info("授权码：" + token + "，验证失败！");
        //设置响应数据
        if (rmlDto != null) {
            try {
                strResp = objectMapper.writeValueAsString(rmlDto);
                logger.info(strResp);
            } catch (JsonProcessingException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        } else {
            //获取异常响应
            code = codes[0];
            logger.info("错误码：code = " + code);
            IOTHttpRspVO excepResp = AppModConfig.getExcepResp(code);
            try {
                strResp = objectMapper.writeValueAsString(excepResp);
                logger.info(strResp);
            } catch (JsonProcessingException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }

        return strResp;
    }

    /**
     * 3.2.8 - 学校类型（学制）编码列表
     *
     * @param request
     * @return
     */
    @RequestMapping(value = "/v1/schTypeCodes", method = RequestMethod.GET)
    public String v1_schTypeCodes(HttpServletRequest request) {
        //初始化响应数据
        String strResp = null;
        SchTypeCodesDTO stcDto = null;
        boolean isAuth = false;
        int code = 0;
        int[] codes = new int[1];
        //授权码
        String token = AppModConfig.GetHeadJsonReq(request, "Authorization");
        //区域名称
        String distName = request.getParameter("distName");
        if (distName == null) {
            distName = request.getParameter("distname");
        }
        //地级城市
        String prefCity = request.getParameter("prefCity");
        if (prefCity == null) {
            prefCity = request.getParameter("prefcity");
        }
        //省或直辖市
        String province = request.getParameter("province");
        logger.info("输入参数：" + "token = " + token + ", distName = " + distName + ", prefCity = " + prefCity + ", province = " + province);
        //验证授权
        isAuth = AppModConfig.verifyAuthCode2(token, db2Service, codes);
        //学校类型（学制）编码列表应用模型函数
        if (isAuth)
            stcDto = stcAppMod.appModFunc(distName, prefCity, province, db1Service);
        else
            logger.info("授权码：" + token + "，验证失败！");
        //设置响应数据
        if (stcDto != null) {
            try {
                strResp = objectMapper.writeValueAsString(stcDto);
                logger.info(strResp);
            } catch (JsonProcessingException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        } else {
            //获取异常响应
            code = codes[0];
            logger.info("错误码：code = " + code);
            IOTHttpRspVO excepResp = AppModConfig.getExcepResp(code);
            try {
                strResp = objectMapper.writeValueAsString(excepResp);
                logger.info(strResp);
            } catch (JsonProcessingException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }

        return strResp;
    }

    /**
     * 3.2.9 - 学校性质编码列表
     *
     * @param request
     * @return
     */
    @RequestMapping(value = "/v1/schPropCodes", method = RequestMethod.GET)
    public String v1_schPropCodes(HttpServletRequest request) {
        //初始化响应数据
        String strResp = null;
        SchPropCodesDTO spcDto = null;
        boolean isAuth = false;
        int code = 0;
        int[] codes = new int[1];
        //授权码
        String token = AppModConfig.GetHeadJsonReq(request, "Authorization");
        //区域名称
        String distName = request.getParameter("distName");
        if (distName == null) {
            distName = request.getParameter("distname");
        }
        //地级城市
        String prefCity = request.getParameter("prefCity");
        if (prefCity == null) {
            prefCity = request.getParameter("prefcity");
        }
        //省或直辖市
        String province = request.getParameter("province");
        logger.info("输入参数：" + "token = " + token + ", distName = " + distName + ", prefCity = " + prefCity + ", province = " + province);
        //验证授权
        isAuth = AppModConfig.verifyAuthCode2(token, db2Service, codes);
        //学校性质编码列表应用模型函数
        if (isAuth)
            spcDto = spcAppMod.appModFunc(distName, prefCity, province, db1Service);
        else
            logger.info("授权码：" + token + "，验证失败！");
        //设置响应数据
        if (spcDto != null) {
            try {
                strResp = objectMapper.writeValueAsString(spcDto);
                logger.info(strResp);
            } catch (JsonProcessingException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        } else {
            //获取异常响应
            code = codes[0];
            logger.info("错误码：code = " + code);
            IOTHttpRspVO excepResp = AppModConfig.getExcepResp(code);
            try {
                strResp = objectMapper.writeValueAsString(excepResp);
                logger.info(strResp);
            } catch (JsonProcessingException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }

        return strResp;
    }

    /**
     * 3.2.10 - 用料类别编码列表
     *
     * @param request
     * @return
     */
    @RequestMapping(value = "/v1/matTypeCodes", method = RequestMethod.GET)
    public String v1_matTypeCodes(HttpServletRequest request) {
        //初始化响应数据
        String strResp = null;
        MatTypeCodesDTO mtcDto = null;
        boolean isAuth = false;
        int code = 0;
        int[] codes = new int[1];
        //授权码
        String token = AppModConfig.GetHeadJsonReq(request, "Authorization");
        //区域名称
        String distName = request.getParameter("distName");
        if (distName == null) {
            distName = request.getParameter("distname");
        }
        //地级城市
        String prefCity = request.getParameter("prefCity");
        if (prefCity == null) {
            prefCity = request.getParameter("prefcity");
        }
        //省或直辖市
        String province = request.getParameter("province");
        logger.info("输入参数：" + "token = " + token + ", distName = " + distName + ", prefCity = " + prefCity + ", province = " + province);
        //验证授权
        isAuth = AppModConfig.verifyAuthCode2(token, db2Service, codes);
        //用料类别编码列表应用模型函数
        if (isAuth)
            mtcDto = mtcAppMod.appModFunc(distName, prefCity, province, db1Service);
        else
            logger.info("授权码：" + token + "，验证失败！");
        //设置响应数据
        if (mtcDto != null) {
            try {
                strResp = objectMapper.writeValueAsString(mtcDto);
                logger.info(strResp);
            } catch (JsonProcessingException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        } else {
            //获取异常响应
            code = codes[0];
            logger.info("错误码：code = " + code);
            IOTHttpRspVO excepResp = AppModConfig.getExcepResp(code);
            try {
                strResp = objectMapper.writeValueAsString(excepResp);
                logger.info(strResp);
            } catch (JsonProcessingException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }

        return strResp;
    }

    /**
     * 3.2.11 - 是否供餐编码列表
     *
     * @param request
     * @return
     */
    @RequestMapping(value = "/v1/isMealCodes", method = RequestMethod.GET)
    public String v1_isMealCodes(HttpServletRequest request) {
        //初始化响应数据
        String strResp = null;
        IsMealCodesDTO imcDto = null;
        boolean isAuth = false;
        int code = 0;
        int[] codes = new int[1];
        //授权码
        String token = AppModConfig.GetHeadJsonReq(request, "Authorization");
        //区域名称
        String distName = request.getParameter("distName");
        if (distName == null)
            distName = request.getParameter("distname");
        //地级城市
        String prefCity = request.getParameter("prefCity");
        if (prefCity == null)
            prefCity = request.getParameter("prefcity");
        //省或直辖市
        String province = request.getParameter("province");
        logger.info("输入参数：" + "token = " + token + ", distName = " + distName + ", prefCity = " + prefCity + ", province = " + province);
        //验证授权
        isAuth = AppModConfig.verifyAuthCode2(token, db2Service, codes);
        //是否供餐编码列表应用模型函数
        if (isAuth)
            imcDto = imcAppMod.appModFunc(distName, prefCity, province, db1Service);
        else
            logger.info("授权码：" + token + "，验证失败！");
        //设置响应数据
        if (imcDto != null) {
            try {
                strResp = objectMapper.writeValueAsString(imcDto);
                logger.info(strResp);
            } catch (JsonProcessingException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        } else {
            //获取异常响应
            code = codes[0];
            logger.info("错误码：code = " + code);
            IOTHttpRspVO excepResp = AppModConfig.getExcepResp(code);
            try {
                strResp = objectMapper.writeValueAsString(excepResp);
                logger.info(strResp);
            } catch (JsonProcessingException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }

        return strResp;
    }

    /**
     * 3.2.12 - 经营模式编码列表
     *
     * @param request
     * @return
     */
    @RequestMapping(value = "/v1/optModeCodes", method = RequestMethod.GET)
    public String v1_optModeCodes(HttpServletRequest request) {
        //初始化响应数据
        String strResp = null;
        OptModeCodesDTO omcDto = null;
        boolean isAuth = false;
        int code = 0;
        int[] codes = new int[1];
        //授权码
        String token = AppModConfig.GetHeadJsonReq(request, "Authorization");
        //区域名称
        String distName = request.getParameter("distName");
        if (distName == null)
            distName = request.getParameter("distname");
        //地级城市
        String prefCity = request.getParameter("prefCity");
        if (prefCity == null)
            prefCity = request.getParameter("prefcity");
        //省或直辖市
        String province = request.getParameter("province");
        logger.info("输入参数：" + "token = " + token + ", distName = " + distName + ", prefCity = " + prefCity + ", province = " + province);
        //验证授权
        isAuth = AppModConfig.verifyAuthCode2(token, db2Service, codes);
        //经营模式编码列表应用模型函数
        if (isAuth)
            omcDto = omcAppMod.appModFunc(distName, prefCity, province, db1Service);
        else
            logger.info("授权码：" + token + "，验证失败！");
        //设置响应数据
        if (omcDto != null) {
            try {
                strResp = objectMapper.writeValueAsString(omcDto);
                logger.info(strResp);
            } catch (JsonProcessingException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        } else {
            //获取异常响应
            code = codes[0];
            logger.info("错误码：code = " + code);
            IOTHttpRspVO excepResp = AppModConfig.getExcepResp(code);
            try {
                strResp = objectMapper.writeValueAsString(excepResp);
                logger.info(strResp);
            } catch (JsonProcessingException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }

        return strResp;
    }

    /**
     * 3.2.13 - 发送状态编码列表
     *
     * @param request
     * @return
     */
    @RequestMapping(value = "/v1/sendStatusCodes", method = RequestMethod.GET)
    public String v1_sendStatusCodes(HttpServletRequest request) {
        //初始化响应数据
        String strResp = null;
        SendStatusCodesDTO sscDto = null;
        boolean isAuth = false;
        int code = 0;
        int[] codes = new int[1];
        //授权码
        String token = AppModConfig.GetHeadJsonReq(request, "Authorization");
        //区域名称
        String distName = request.getParameter("distName");
        if (distName == null) {
            distName = request.getParameter("distname");
        }
        //地级城市
        String prefCity = request.getParameter("prefCity");
        if (prefCity == null) {
            prefCity = request.getParameter("prefcity");
        }
        //省或直辖市
        String province = request.getParameter("province");
        logger.info("输入参数：" + "token = " + token + ", distName = " + distName + ", prefCity = " + prefCity + ", province = " + province);
        //验证授权
        isAuth = AppModConfig.verifyAuthCode2(token, db2Service, codes);
        //发送状态编码列表应用模型函数
        if (isAuth)
            sscDto = sscAppMod.appModFunc(distName, prefCity, province, db1Service);
        else
            logger.info("授权码：" + token + "，验证失败！");
        //设置响应数据
        if (sscDto != null) {
            try {
                strResp = objectMapper.writeValueAsString(sscDto);
                logger.info(strResp);
            } catch (JsonProcessingException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        } else {
            //获取异常响应
            code = codes[0];
            logger.info("错误码：code = " + code);
            IOTHttpRspVO excepResp = AppModConfig.getExcepResp(code);
            try {
                strResp = objectMapper.writeValueAsString(excepResp);
                logger.info(strResp);
            } catch (JsonProcessingException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }

        return strResp;
    }

    /**
     * 3.2.14 - 排菜执行编码列表
     *
     * @param request
     * @return
     */
    @RequestMapping(value = "/v1/dishExeList", method = RequestMethod.GET)
    public String v1_dishExeList(HttpServletRequest request) {
        //初始化响应数据
        String strResp = null;
        DishExeListDTO delDto = null;
        boolean isAuth = false;
        int code = 0;
        int[] codes = new int[1];
        //授权码
        String token = AppModConfig.GetHeadJsonReq(request, "Authorization");
        //区域名称
        String distName = request.getParameter("distName");
        if (distName == null) {
            distName = request.getParameter("distname");
        }
        //地级城市
        String prefCity = request.getParameter("prefCity");
        if (prefCity == null) {
            prefCity = request.getParameter("prefcity");
        }
        //省或直辖市
        String province = request.getParameter("province");
        logger.info("输入参数：" + "token = " + token + ", distName = " + distName + ", prefCity = " + prefCity + ", province = " + province);
        //验证授权
        isAuth = AppModConfig.verifyAuthCode2(token, db2Service, codes);
        //排菜执行编码列表应用模型函数
        if (isAuth)
            delDto = delAppMod.appModFunc(distName, prefCity, province, db1Service);
        else
            logger.info("授权码：" + token + "，验证失败！");
        //设置响应数据
        if (delDto != null) {
            try {
                strResp = objectMapper.writeValueAsString(delDto);
                logger.info(strResp);
            } catch (JsonProcessingException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        } else {
            //获取异常响应
            code = codes[0];
            logger.info("错误码：code = " + code);
            IOTHttpRspVO excepResp = AppModConfig.getExcepResp(code);
            try {
                strResp = objectMapper.writeValueAsString(excepResp);
                logger.info(strResp);
            } catch (JsonProcessingException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }

        return strResp;
    }

    /**
     * 3.2.15 - 用料确认列表
     *
     * @param request
     * @return
     */
    @RequestMapping(value = "/v1/matConfirms", method = RequestMethod.GET)
    public String v1_matConfirms(HttpServletRequest request) {
        //初始化响应数据
        String strResp = null;
        MatConfirmsDTO mcsDto = null;
        boolean isAuth = false;
        int code = 0;
        int[] codes = new int[1];
        //授权码
        String token = AppModConfig.GetHeadJsonReq(request, "Authorization");
        //开始日期
        String startDate = request.getParameter("startSubDate");
        //结束日期
        String endDate = request.getParameter("endSubDate");
        //学校筛选方式，0:按主管部门，1:按所在地
        String schSelMode = request.getParameter("schSelMode");
        //所属，0:其他，1:部属，2:市属，3: 区属，按主管部门有效
        String subLevel = request.getParameter("subLevel");
        //主管部门，按主管部门有效
        String compDep = request.getParameter("compDep");
        //所属区域名称，按主管部门有效
        String subDistName = request.getParameter("subDistName");
        //区域名称
        String distName = request.getParameter("distName");
        if (distName == null) {
            distName = request.getParameter("distname");
        }
        //地级城市
        String prefCity = request.getParameter("prefCity");
        if (prefCity == null) {
            prefCity = request.getParameter("prefcity");
        }
        //省或直辖市
        String province = request.getParameter("province");
        //页号
        String page = request.getParameter("page");
        if (page == null) {
            page = "1";
        }
        //分页大小
        String pageSize = request.getParameter("pageSize");
        if (pageSize == null) {
            pageSize = "20";
        }

        //所属，0:其他，1:部属，2:市属，3: 区属，按主管部门有效(多个[1,3])
        String subLevels = request.getParameter("subLevels");
        //主管部门，按主管部门有效(多个[1_1,3_1])
        String compDeps = request.getParameter("compDeps");
        //区域名称(多个[1,3])
        String distNames = request.getParameter("distNames");

        //区域名称(多个[1,3])
        String departmentIds = request.getParameter("departmentIds");

        logger.info("输入参数：" + "token = " + token + ", startDate = " + startDate
                + ", endDate = " + endDate + ", schSelMode = " + schSelMode
                + ", subLevel = " + subLevel + ", compDep = " + compDep
                + ", subDistName = " + subDistName + ", distName = " + distName
                + ", prefCity = " + prefCity + ", province = " + province
                + ", page = " + page + ", pageSize = " + pageSize
                + ", subLevels = " + subLevels + ", compDeps = " + compDeps + ", distNames = " + distNames);
        //验证授权
        isAuth = AppModConfig.verifyAuthCode2(token, db2Service, codes);
        //用料确认列表应用模型函数
        if (isAuth)
            mcsDto = mcsAppMod.appModFunc(token, startDate, endDate, schSelMode, subLevel, compDep,
                    subDistName, distName, prefCity, province,
                    subLevels, compDeps, distNames, departmentIds,
                    page, pageSize, db1Service, db2Service, dbHiveMatService);
        else
            logger.info("授权码：" + token + "，验证失败！");
        //设置响应数据
        if (mcsDto != null) {
            try {
                strResp = objectMapper.writeValueAsString(mcsDto);
                logger.info(strResp);
            } catch (JsonProcessingException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        } else {
            //获取异常响应
            code = codes[0];
            logger.info("错误码：code = " + code);
            IOTHttpRspVO excepResp = AppModConfig.getExcepResp(code);
            try {
                strResp = objectMapper.writeValueAsString(excepResp);
                logger.info(strResp);
            } catch (JsonProcessingException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }

        return strResp;
    }

    /**
     * 3.2.16 - 导出用料确认列表
     *
     * @param request
     * @param response
     * @return
     */
    @RequestMapping(value = "/v1/expMatConfirms", method = RequestMethod.GET)
    public String v1_expMatConfirms(HttpServletRequest request, HttpServletResponse response) {
        //初始化响应数据
        String strResp = null;
        ExpMatConfirmsDTO emcDto = null;
        boolean isAuth = false;
        int code = 0;
        int[] codes = new int[1];
        //授权码
        String token = AppModConfig.GetHeadJsonReq(request, "Authorization");
        //开始日期
        String startDate = request.getParameter("startSubDate");
        //结束日期
        String endDate = request.getParameter("endSubDate");
        //学校筛选方式，0:按主管部门，1:按所在地
        String schSelMode = request.getParameter("schSelMode");
        //所属，0:其他，1:部属，2:市属，3: 区属，按主管部门有效
        String subLevel = request.getParameter("subLevel");
        //主管部门，按主管部门有效
        String compDep = request.getParameter("compDep");
        //所属区域名称，按主管部门有效
        String subDistName = request.getParameter("subDistName");
        //区域名称
        String distName = request.getParameter("distName");
        if (distName == null) {
            distName = request.getParameter("distname");
        }
        //地级城市
        String prefCity = request.getParameter("prefCity");
        if (prefCity == null) {
            prefCity = request.getParameter("prefcity");
        }
        //省或直辖市
        String province = request.getParameter("province");

        //所属，0:其他，1:部属，2:市属，3: 区属，按主管部门有效(多个[1,3])
        String subLevels = request.getParameter("subLevels");
        //主管部门，按主管部门有效(多个[1_1,3_1])
        String compDeps = request.getParameter("compDeps");
        //区域名称(多个[1,3])
        String distNames = request.getParameter("distNames");

        //区域名称(多个[1,3])
        String departmentIds = request.getParameter("departmentIds");

        logger.info("输入参数：" + "token = " + token + ", startDate = " + startDate
                + ", endDate = " + endDate + ", schSelMode = " + schSelMode
                + ", subLevel = " + subLevel + ", compDep = " + compDep
                + ", subDistName = " + subDistName + ", distName = " + distName
                + ", prefCity = " + prefCity + ", province = " + province
                + ", subLevels = " + subLevels + ", compDeps = " + compDeps + ", distNames = " + distNames);
        //验证授权
        isAuth = AppModConfig.verifyAuthCode2(token, db2Service, codes);
        //导出用料确认列表应用模型函数
        if (isAuth)
            emcDto = emcAppMod.appModFunc(token, startDate, endDate, schSelMode, subLevel, compDep,
                    subDistName, distName, prefCity, province,
                    subLevels, compDeps, distNames, departmentIds,
                    db1Service, db2Service, dbHiveMatService);
        else
            logger.info("授权码：" + token + "，验证失败！");
        //设置响应数据
        if (emcDto != null) {
            try {
                strResp = objectMapper.writeValueAsString(emcDto);
                logger.info(strResp);
            } catch (JsonProcessingException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        } else {
            //获取异常响应
            code = codes[0];
            logger.info("错误码：code = " + code);
            IOTHttpRspVO excepResp = AppModConfig.getExcepResp(code);
            try {
                strResp = objectMapper.writeValueAsString(excepResp);
                logger.info(strResp);
            } catch (JsonProcessingException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }

        return strResp;
    }

    /**
     * 3.2.17 - 用料确认详情列表
     *
     * @param request
     * @return
     */
    @RequestMapping(value = "/v1/matConfirmDets", method = RequestMethod.GET)
    public String v1_matConfirmDets(HttpServletRequest request) {
        //初始化响应数据
        String strResp = null;
        MatConfirmDetsDTO mcdDto = null;
        boolean isAuth = false;
        int code = 0;
        int[] codes = new int[1];
        //授权码
        String token = AppModConfig.GetHeadJsonReq(request, "Authorization");
        //开始日期
        String startDate = request.getParameter("startSubDate");
        //结束日期
        String endDate = request.getParameter("endSubDate");
        //项目点名称
        String ppName = request.getParameter("ppName");
        //团餐公司名称
        String rmcName = request.getParameter("rmcName");
        //区域名称
        String distName = request.getParameter("distName");
        if (distName == null) {
            distName = request.getParameter("distname");
        }
        //地级城市
        String prefCity = request.getParameter("prefCity");
        if (prefCity == null) {
            prefCity = request.getParameter("prefcity");
        }
        //省或直辖市
        String province = request.getParameter("province");
        //所属，0:其他，1:部属，2:市属，3: 区属
        String subLevel = request.getParameter("subLevel");
        //主管部门，0:市教委，1:商委，2:教育部
        String compDep = request.getParameter("compDep");
        //总分校标识，0:无，1:总校，2:分校
        String schGenBraFlag = request.getParameter("schGenBraFlag");
        //所属区域名称
        String subDistName = request.getParameter("subDistName");
        //证件主体，0:学校，1:外包
        String fblMb = request.getParameter("fblMb");
        //是否确认， 0:未确认，1:已确认
        String confirmFlag = request.getParameter("confirmFlag");
        //学校类型（学制），0:托儿所，1:托幼园，2:托幼小，3:幼儿园，4:幼小，5:幼小初，6:幼小初高，7:小学，8:初级中学，9:高级中学，10:完全中学，11:九年一贯制学校，12:十二年一贯制学校，13:职业初中，14:中等职业学校，15:工读学校，16:特殊教育学校，17:其他
        String schType = request.getParameter("schType");
        //学校性质，0:公办，1:民办，2:其他
        String schProp = request.getParameter("schProp");
        //经营模式，0:自营，1:外包-现场加工，2:外包-快餐配送
        String optMode = request.getParameter("optMode");
        //用料类别，0:原料，1:成品菜
        String matType = request.getParameter("matType");
        //页号
        String page = request.getParameter("page");
        if (page == null) {
            page = "1";
        }
        //分页大小
        String pageSize = request.getParameter("pageSize");
        if (pageSize == null) {
            pageSize = "20";
        }

        //区域名称 格式：[“1”,”2”……]
        String distNames = request.getParameter("distNames");
        //所属 格式：[“1”,”2”……]
        String subLevels = request.getParameter("subLevels");
        //主管部门 格式：[“1”,”2”……]
        String compDeps = request.getParameter("compDeps");
        //学校性质 格式：[“1”,”2”……]
        String schProps = request.getParameter("schProps");
        //学校类型 格式：[“1”,”2”……]
        String schTypes = request.getParameter("schTypes");
        //经营模式 格式：[“1”,”2”……]
        String optModes = request.getParameter("optModes");
        //所属区(sub) 格式：[“1”,”2”……]
        String subDistNames = request.getParameter("subDistNames");

        String departmentId = request.getParameter("departmentId");
        //所属区(sub) 格式：[“1”,”2”……]
        String departmentIds = request.getParameter("departmentIds");

        logger.info("输入参数：" + "token = " + token + ", startDate = " + startDate
                + ", endDate = " + endDate + ", ppName = " + ppName
                + ", rmcName = " + rmcName + ", distName = " + distName
                + ", prefCity = " + prefCity + ", province = " + province
                + ", subLevel = " + subLevel + ", compDep = " + compDep
                + ", schGenBraFlag = " + schGenBraFlag + ", subDistName = " + subDistName
                + ", fblMb = " + fblMb + ", confirmFlag = " + confirmFlag
                + ", schType = " + schType + ", schProp = " + schProp
                + ", optMode = " + optMode + ", matType = " + matType
                + ", page = " + page + ", pageSize = " + pageSize
                + ", distNames = " + distNames + ", subLevels = " + subLevels
                + ", compDeps = " + compDeps + ", schProps = " + schProps
                + ", schTypes = " + schTypes + ", optModes = " + optModes
                + ", subDistNames = " + subDistNames);
        //验证授权
        isAuth = AppModConfig.verifyAuthCode2(token, db2Service, codes);
        //用料确认详情列表应用模型函数
        if (isAuth)
            mcdDto = mcdAppMod.appModFunc(token, startDate, endDate, ppName, rmcName,
                    distName, prefCity, province, subLevel, compDep, schGenBraFlag,
                    subDistName, fblMb, confirmFlag, schType, schProp, optMode, matType,
                    distNames, subLevels, compDeps, schProps, schTypes, optModes, subDistNames,
                    departmentId, departmentIds,
                    page, pageSize, db1Service, db2Service, saasService, dbHiveMatService);
        else
            logger.info("授权码：" + token + "，验证失败！");
        //设置响应数据
        if (mcdDto != null) {
            try {
                strResp = objectMapper.writeValueAsString(mcdDto);
                logger.info(strResp);
            } catch (JsonProcessingException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        } else {
            //获取异常响应
            code = codes[0];
            logger.info("错误码：code = " + code);
            IOTHttpRspVO excepResp = AppModConfig.getExcepResp(code);
            try {
                strResp = objectMapper.writeValueAsString(excepResp);
                logger.info(strResp);
            } catch (JsonProcessingException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }

        return strResp;
    }

    /**
     * 3.2.18 - 导出用料确认详情列表
     *
     * @param request
     * @return
     */
    @RequestMapping(value = "/v1/expMatConfirmDets", method = RequestMethod.GET)
    public String v1_expMatConfirmDets(HttpServletRequest request) {
        //初始化响应数据
        String strResp = null;
        ExpMatConfirmDetsDTO emcdDto = null;
        boolean isAuth = false;
        int code = 0;
        int[] codes = new int[1];
        //授权码
        String token = AppModConfig.GetHeadJsonReq(request, "Authorization");
        //开始日期，默认为当天日期，格式：xxxx-xx-xx
        String startDate = request.getParameter("startSubDate");
        //结束日期，默认为当天日期，格式：xxxx-xx-xx
        String endDate = request.getParameter("endSubDate");
        //项目点名称
        String ppName = request.getParameter("ppName");
        //团餐公司名称
        String rmcName = request.getParameter("rmcName");
        //区域名称
        String distName = request.getParameter("distName");
        if (distName == null) {
            distName = request.getParameter("distname");
        }
        //地级城市
        String prefCity = request.getParameter("prefCity");
        if (prefCity == null) {
            prefCity = request.getParameter("prefcity");
        }
        //省或直辖市
        String province = request.getParameter("province");
        //所属，0:其他，1:部属，2:市属，3: 区属
        String subLevel = request.getParameter("subLevel");
        //主管部门，0:市教委，1:商委，2:教育部
        String compDep = request.getParameter("compDep");
        //总分校标识，0:无，1:总校，2:分校
        String schGenBraFlag = request.getParameter("schGenBraFlag");
        //所属区域名称
        String subDistName = request.getParameter("subDistName");
        //证件主体，0:学校，1:外包
        String fblMb = request.getParameter("fblMb");
        //是否确认， 0:未确认，1:已确认
        String confirmFlag = request.getParameter("confirmFlag");
        //学校类型（学制），0:托儿所，1:托幼园，2:托幼小，3:幼儿园，4:幼小，5:幼小初，6:幼小初高，7:小学，8:初级中学，9:高级中学，10:完全中学，11:九年一贯制学校，12:十二年一贯制学校，13:职业初中，14:中等职业学校，15:工读学校，16:特殊教育学校，17:其他
        String schType = request.getParameter("schType");
        //学校性质，0:公办，1:民办，2:其他
        String schProp = request.getParameter("schProp");
        //经营模式，0:自营，1:外包-现场加工，2:外包-快餐配送
        String optMode = request.getParameter("optMode");
        //发送状态，0:未发送，1:已发送
        String sendFlag = request.getParameter("sendFlag");

        //区域名称 格式：[“1”,”2”……]
        String distNames = request.getParameter("distNames");
        //所属 格式：[“1”,”2”……]
        String subLevels = request.getParameter("subLevels");
        //主管部门 格式：[“1”,”2”……]
        String compDeps = request.getParameter("compDeps");
        //学校性质 格式：[“1”,”2”……]
        String schProps = request.getParameter("schProps");
        //学校类型 格式：[“1”,”2”……]
        String schTypes = request.getParameter("schTypes");
        //经营模式 格式：[“1”,”2”……]
        String optModes = request.getParameter("optModes");
        //所属区(sub) 格式：[“1”,”2”……]
        String subDistNames = request.getParameter("subDistNames");


        String departmentId = request.getParameter("departmentId");
        //所属区(sub) 格式：[“1”,”2”……]
        String departmentIds = request.getParameter("departmentIds");

        logger.info("输入参数：" + "token = " + token + ", startDate = " + startDate
                + ", endDate = " + endDate + ", ppName = " + ppName + ", rmcName = " + rmcName
                + ", distName = " + distName + ", prefCity = " + prefCity + ", province = " + province
                + ", subLevel = " + subLevel + ", compDep = " + compDep + ", schGenBraFlag = " + schGenBraFlag
                + ", subDistName = " + subDistName + ", fblMb = " + fblMb
                + ", confirmFlag = " + confirmFlag + ", schType = " + schType
                + ", schProp = " + schProp + ", optMode = " + optMode
                + ", schType = " + schType + ", sendFlag = " + sendFlag
                + ", distNames = " + distNames + ", subLevels = " + subLevels
                + ", compDeps = " + compDeps + ", schProps = " + schProps
                + ", schTypes = " + schTypes + ", optModes = " + optModes
                + ", subDistNames = " + subDistNames);
        //验证授权
        isAuth = AppModConfig.verifyAuthCode2(token, db2Service, codes);
        //导出用料确认详情列表应用模型函数
        if (isAuth)
            emcdDto = emcdAppMod.appModFunc(token, startDate, endDate, ppName, rmcName,
                    distName, prefCity, province, subLevel, compDep, schGenBraFlag,
                    subDistName, fblMb, confirmFlag, schType, schProp, optMode, sendFlag,
                    distNames, subLevels, compDeps, schProps, schTypes, optModes, subDistNames,
                    departmentId, departmentIds,
                    db1Service, db2Service, saasService, dbHiveMatService);
        else
            logger.info("授权码：" + token + "，验证失败！");
        //设置响应数据
        if (emcdDto != null) {
            try {
                strResp = objectMapper.writeValueAsString(emcdDto);
                logger.info(strResp);
            } catch (JsonProcessingException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        } else {
            //获取异常响应
            code = codes[0];
            logger.info("错误码：code = " + code);
            IOTHttpRspVO excepResp = AppModConfig.getExcepResp(code);
            try {
                strResp = objectMapper.writeValueAsString(excepResp);
                logger.info(strResp);
            } catch (JsonProcessingException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }

        return strResp;
    }

    /**
     * 3.2.19 - 用料计划确认编码列表
     *
     * @param request
     * @return
     */
    @RequestMapping(value = "/v1/matConfirmList", method = RequestMethod.GET)
    public String v1_matConfirmList(HttpServletRequest request) {
        //初始化响应数据
        String strResp = null;
        MatConfirmListDTO mclDto = null;
        boolean isAuth = false;
        int code = 0;
        int[] codes = new int[1];
        //授权码
        String token = AppModConfig.GetHeadJsonReq(request, "Authorization");
        //区域名称
        String distName = request.getParameter("distName");
        if (distName == null) {
            distName = request.getParameter("distname");
        }
        //地级城市
        String prefCity = request.getParameter("prefCity");
        if (prefCity == null) {
            prefCity = request.getParameter("prefcity");
        }
        //省或直辖市
        String province = request.getParameter("province");
        logger.info("输入参数：" + "token = " + token + ", distName = " + distName + ", prefCity = " + prefCity + ", province = " + province);
        //验证授权
        isAuth = AppModConfig.verifyAuthCode2(token, db2Service, codes);
        //用料计划确认编码列表应用模型函数
        if (isAuth)
            mclDto = mclAppMod.appModFunc(distName, prefCity, province, db1Service);
        else
            logger.info("授权码：" + token + "，验证失败！");
        //设置响应数据
        if (mclDto != null) {
            try {
                strResp = objectMapper.writeValueAsString(mclDto);
                logger.info(strResp);
            } catch (JsonProcessingException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        } else {
            //获取异常响应
            code = codes[0];
            logger.info("错误码：code = " + code);
            IOTHttpRspVO excepResp = AppModConfig.getExcepResp(code);
            try {
                strResp = objectMapper.writeValueAsString(excepResp);
                logger.info(strResp);
            } catch (JsonProcessingException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }

        return strResp;
    }

    /**
     * 3.2.20 - 配货计划操作列表
     *
     * @param request
     * @return
     */
    @RequestMapping(value = "/v1/gsPlanOpts", method = RequestMethod.GET)
    public String v1_gsPlanOpts(HttpServletRequest request) {
        //初始化响应数据
        String strResp = null;
        GsPlanOptsDTO gpoDto = null;
        boolean isAuth = false;
        int code = 0;
        int[] codes = new int[1];
        //授权码
        String token = AppModConfig.GetHeadJsonReq(request, "Authorization");
        //开始日期
        String startDate = request.getParameter("startSubDate");
        //结束日期
        String endDate = request.getParameter("endSubDate");
        //学校筛选方式，0:按主管部门，1:按所在地
        String schSelMode = request.getParameter("schSelMode");
        //所属，0:其他，1:部属，2:市属，3: 区属，按主管部门有效
        String subLevel = request.getParameter("subLevel");
        //主管部门，按主管部门有效
        String compDep = request.getParameter("compDep");
        //所属区域名称，按主管部门有效
        String subDistName = request.getParameter("subDistName");
        //区域名称
        String distName = request.getParameter("distName");
        if (distName == null) {
            distName = request.getParameter("distname");
        }
        //地级城市
        String prefCity = request.getParameter("prefCity");
        if (prefCity == null) {
            prefCity = request.getParameter("prefcity");
        }
        //省或直辖市
        String province = request.getParameter("province");
        //页号
        String page = request.getParameter("page");
        if (page == null) {
            page = "1";
        }
        //分页大小
        String pageSize = request.getParameter("pageSize");
        if (pageSize == null) {
            pageSize = "20";
        }

        //所属，0:其他，1:部属，2:市属，3: 区属，按主管部门有效(多个[1,3])
        String subLevels = request.getParameter("subLevels");
        //主管部门，按主管部门有效(多个[1_1,3_1])
        String compDeps = request.getParameter("compDeps");
        //区域名称(多个[1,3])
        String distNames = request.getParameter("distNames");

        //区域名称(多个[1,3])
        String departmentIds = request.getParameter("departmentIds");

        logger.info("输入参数：" + "token = " + token + ", startDate = " + startDate + ", endDate = " + endDate
                + ", schSelMode = " + schSelMode + ", subLevel = " + subLevel + ", compDep = " + compDep
                + ", subDistName = " + subDistName + ", distName = " + distName + ", prefCity = " + prefCity
                + ", province = " + province + ", page = " + page + ", pageSize = " + pageSize +
                ", subLevels = " + subLevels + ", compDeps = " + compDeps + ", distNames = " + distNames);
        //验证授权
        isAuth = AppModConfig.verifyAuthCode2(token, db2Service, codes);
        //配货计划操作列表应用模型函数
        if (isAuth)
            gpoDto = gpoAppMod.appModFunc(token, startDate, endDate, schSelMode, subLevel,
                    compDep, subDistName, distName, prefCity, province, subLevels, compDeps, distNames,
                    departmentIds,
                    page, pageSize, db1Service, db2Service, dbHiveGsService);
        else
            logger.info("授权码：" + token + "，验证失败！");
        //设置响应数据
        if (gpoDto != null) {
            try {
                strResp = objectMapper.writeValueAsString(gpoDto);
                logger.info(strResp);
            } catch (JsonProcessingException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        } else {
            //获取异常响应
            code = codes[0];
            logger.info("错误码：code = " + code);
            IOTHttpRspVO excepResp = AppModConfig.getExcepResp(code);
            try {
                strResp = objectMapper.writeValueAsString(excepResp);
                logger.info(strResp);
            } catch (JsonProcessingException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }

        return strResp;
    }

    /**
     * 3.2.21 - 导出配货计划操作列表
     *
     * @param request
     * @return
     */
    @RequestMapping(value = "/v1/expGsPlanOpts", method = RequestMethod.GET)
    public String v1_expGsPlanOpts(HttpServletRequest request) {
        //初始化响应数据
        String strResp = null;
        ExpGsPlanOptsDTO egpoDto = null;
        boolean isAuth = false;
        int code = 0;
        int[] codes = new int[1];
        //授权码
        String token = AppModConfig.GetHeadJsonReq(request, "Authorization");
        //开始日期
        String startDate = request.getParameter("startSubDate");
        //结束日期
        String endDate = request.getParameter("endSubDate");
        //学校筛选方式，0:按主管部门，1:按所在地
        String schSelMode = request.getParameter("schSelMode");
        //所属，0:其他，1:部属，2:市属，3: 区属，按主管部门有效
        String subLevel = request.getParameter("subLevel");
        //主管部门，按主管部门有效
        String compDep = request.getParameter("compDep");
        //所属区域名称，按主管部门有效
        String subDistName = request.getParameter("subDistName");
        //区域名称
        String distName = request.getParameter("distName");
        if (distName == null) {
            distName = request.getParameter("distname");
        }
        //地级城市
        String prefCity = request.getParameter("prefCity");
        if (prefCity == null) {
            prefCity = request.getParameter("prefcity");
        }
        //省或直辖市
        String province = request.getParameter("province");

        //所属，0:其他，1:部属，2:市属，3: 区属，按主管部门有效(多个[1,3])
        String subLevels = request.getParameter("subLevels");
        //主管部门，按主管部门有效(多个[1_1,3_1])
        String compDeps = request.getParameter("compDeps");
        //区域名称(多个[1,3])
        String distNames = request.getParameter("distNames");

        //区域名称(多个[1,3])
        String departmentIds = request.getParameter("departmentIds");

        logger.info("输入参数：" + "token = " + token + ", startDate = " + startDate + ", endDate = " + endDate
                + ", schSelMode = " + schSelMode + ", subLevel = " + subLevel + ", compDep = " + compDep
                + ", subDistName = " + subDistName + ", distName = " + distName
                + ", prefCity = " + prefCity + ", province = " + province +
                ", subLevels = " + subLevels + ", compDeps = " + compDeps + ", distNames = " + distNames);
        //验证授权
        isAuth = AppModConfig.verifyAuthCode2(token, db2Service, codes);
        //导出配货计划操作列表应用模型函数
        if (isAuth)
            egpoDto = egpoAppMod.appModFunc(token, startDate, endDate, schSelMode, subLevel, compDep,
                    subDistName, distName, prefCity, province, subLevels, compDeps, distNames, departmentIds,
                    db1Service, db2Service, dbHiveGsService);
        else
            logger.info("授权码：" + token + "，验证失败！");
        //设置响应数据
        if (egpoDto != null) {
            try {
                strResp = objectMapper.writeValueAsString(egpoDto);
                logger.info(strResp);
            } catch (JsonProcessingException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        } else {
            //获取异常响应
            code = codes[0];
            logger.info("错误码：code = " + code);
            IOTHttpRspVO excepResp = AppModConfig.getExcepResp(code);
            try {
                strResp = objectMapper.writeValueAsString(excepResp);
                logger.info(strResp);
            } catch (JsonProcessingException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }

        return strResp;
    }

    /**
     * 3.2.22 - 指派状态编码列表
     *
     * @param request
     * @return
     */
    @RequestMapping(value = "/v1/assignStatusCodes", method = RequestMethod.GET)
    public String v1_assignStatusCodes(HttpServletRequest request) {
        //初始化响应数据
        String strResp = null;
        AssignStatusCodesDTO ascDto = null;
        boolean isAuth = false;
        int code = 0;
        int[] codes = new int[1];
        //授权码
        String token = AppModConfig.GetHeadJsonReq(request, "Authorization");
        //区域名称
        String distName = request.getParameter("distName");
        if (distName == null) {
            distName = request.getParameter("distname");
        }
        //地级城市
        String prefCity = request.getParameter("prefCity");
        if (prefCity == null) {
            prefCity = request.getParameter("prefcity");
        }
        //省或直辖市
        String province = request.getParameter("province");
        logger.info("输入参数：" + "token = " + token + ", distName = " + distName + ", prefCity = " + prefCity + ", province = " + province);
        //验证授权
        isAuth = AppModConfig.verifyAuthCode2(token, db2Service, codes);
        //指派状态编码列表应用模型函数
        if (isAuth)
            ascDto = ascAppMod.appModFunc(distName, prefCity, province, db1Service);
        else
            logger.info("授权码：" + token + "，验证失败！");
        //设置响应数据
        if (ascDto != null) {
            try {
                strResp = objectMapper.writeValueAsString(ascDto);
                logger.info(strResp);
            } catch (JsonProcessingException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        } else {
            //获取异常响应
            code = codes[0];
            logger.info("错误码：code = " + code);
            IOTHttpRspVO excepResp = AppModConfig.getExcepResp(code);
            try {
                strResp = objectMapper.writeValueAsString(excepResp);
                logger.info(strResp);
            } catch (JsonProcessingException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }

        return strResp;
    }

    /**
     * 3.2.23 - 配送状态编码列表
     *
     * @param request
     * @return
     */
    @RequestMapping(value = "/v1/dispStatusCodes", method = RequestMethod.GET)
    public String v1_dispStatusCodes(HttpServletRequest request) {
        //初始化响应数据
        String strResp = null;
        DispStatusCodesDTO dscDto = null;
        boolean isAuth = false;
        int code = 0;
        int[] codes = new int[1];
        //授权码
        String token = AppModConfig.GetHeadJsonReq(request, "Authorization");
        //区域名称
        String distName = request.getParameter("distName");
        if (distName == null) {
            distName = request.getParameter("distname");
        }
        //地级城市
        String prefCity = request.getParameter("prefCity");
        if (prefCity == null) {
            prefCity = request.getParameter("prefcity");
        }
        //省或直辖市
        String province = request.getParameter("province");
        logger.info("输入参数：" + "token = " + token + ", distName = " + distName + ", prefCity = " + prefCity + ", province = " + province);
        //验证授权
        isAuth = AppModConfig.verifyAuthCode2(token, db2Service, codes);
        //配送状态编码列表应用模型函数
        if (isAuth)
            dscDto = dscAppMod.appModFunc(distName, prefCity, province, db1Service);
        else
            logger.info("授权码：" + token + "，验证失败！");
        //设置响应数据
        if (dscDto != null) {
            try {
                strResp = objectMapper.writeValueAsString(dscDto);
                logger.info(strResp);
            } catch (JsonProcessingException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        } else {
            //获取异常响应
            code = codes[0];
            logger.info("错误码：code = " + code);
            IOTHttpRspVO excepResp = AppModConfig.getExcepResp(code);
            try {
                strResp = objectMapper.writeValueAsString(excepResp);
                logger.info(strResp);
            } catch (JsonProcessingException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }

        return strResp;
    }

    /**
     * 3.2.24 - 验收状态编码列表
     *
     * @param request
     * @return
     */
    @RequestMapping(value = "/v1/acceptStatusCodes", method = RequestMethod.GET)
    public String v1_acceptStatusCodes(HttpServletRequest request) {
        //初始化响应数据
        String strResp = null;
        AcceptStatusCodesDTO ascDto = null;
        boolean isAuth = false;
        int code = 0;
        int[] codes = new int[1];
        //授权码
        String token = AppModConfig.GetHeadJsonReq(request, "Authorization");
        //区域名称
        String distName = request.getParameter("distName");
        if (distName == null) {
            distName = request.getParameter("distname");
        }
        //地级城市
        String prefCity = request.getParameter("prefCity");
        if (prefCity == null) {
            prefCity = request.getParameter("prefcity");
        }
        //省或直辖市
        String province = request.getParameter("province");
        logger.info("输入参数：" + "token = " + token + ", distName = " + distName + ", prefCity = " + prefCity + ", province = " + province);
        //验证授权
        isAuth = AppModConfig.verifyAuthCode2(token, db2Service, codes);
        //验收状态编码列表应用模型函数
        if (isAuth)
            ascDto = acscAppMod.appModFunc(distName, prefCity, province, db1Service);
        else
            logger.info("授权码：" + token + "，验证失败！");
        //设置响应数据
        if (ascDto != null) {
            try {
                strResp = objectMapper.writeValueAsString(ascDto);
                logger.info(strResp);
            } catch (JsonProcessingException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        } else {
            //获取异常响应
            code = codes[0];
            logger.info("错误码：code = " + code);
            IOTHttpRspVO excepResp = AppModConfig.getExcepResp(code);
            try {
                strResp = objectMapper.writeValueAsString(excepResp);
                logger.info(strResp);
            } catch (JsonProcessingException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }

        return strResp;
    }

    /**
     * 3.2.25 - 配送类型编码列表
     *
     * @param request
     * @return
     */
    @RequestMapping(value = "/v1/dispTypeCodes", method = RequestMethod.GET)
    public String v1_dispTypeCodes(HttpServletRequest request) {
        //初始化响应数据
        String strResp = null;
        DispTypeCodesDTO dtcDto = null;
        boolean isAuth = false;
        int code = 0;
        int[] codes = new int[1];
        //授权码
        String token = AppModConfig.GetHeadJsonReq(request, "Authorization");
        //区域名称
        String distName = request.getParameter("distName");
        if (distName == null) {
            distName = request.getParameter("distname");
        }
        //地级城市
        String prefCity = request.getParameter("prefCity");
        if (prefCity == null) {
            prefCity = request.getParameter("prefcity");
        }
        //省或直辖市
        String province = request.getParameter("province");
        logger.info("输入参数：" + "token = " + token + ", distName = " + distName + ", prefCity = " + prefCity + ", province = " + province);
        //验证授权
        isAuth = AppModConfig.verifyAuthCode2(token, db2Service, codes);
        //配送类型编码列表应用模型函数
        if (isAuth)
            dtcDto = dtcAppMod.appModFunc(distName, prefCity, province, db1Service);
        else
            logger.info("授权码：" + token + "，验证失败！");
        //设置响应数据
        if (dtcDto != null) {
            try {
                strResp = objectMapper.writeValueAsString(dtcDto);
                logger.info(strResp);
            } catch (JsonProcessingException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        } else {
            //获取异常响应
            code = codes[0];
            logger.info("错误码：code = " + code);
            IOTHttpRspVO excepResp = AppModConfig.getExcepResp(code);
            try {
                strResp = objectMapper.writeValueAsString(excepResp);
                logger.info(strResp);
            } catch (JsonProcessingException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }

        return strResp;
    }

    /**
     * 3.2.26 - 配送方式编码列表
     *
     * @param request
     * @return
     */
    @RequestMapping(value = "/v1/dispModeCodes", method = RequestMethod.GET)
    public String v1_dispModeCodes(HttpServletRequest request) {
        //初始化响应数据
        String strResp = null;
        DispModeCodesDTO dmcDto = null;
        boolean isAuth = false;
        int code = 0;
        int[] codes = new int[1];
        //授权码
        String token = AppModConfig.GetHeadJsonReq(request, "Authorization");
        //区域名称
        String distName = request.getParameter("distName");
        if (distName == null) {
            distName = request.getParameter("distname");
        }
        //地级城市
        String prefCity = request.getParameter("prefCity");
        if (prefCity == null) {
            prefCity = request.getParameter("prefcity");
        }
        //省或直辖市
        String province = request.getParameter("province");
        logger.info("输入参数：" + "token = " + token + ", distName = " + distName + ", prefCity = " + prefCity + ", province = " + province);
        //验证授权
        isAuth = AppModConfig.verifyAuthCode2(token, db2Service, codes);
        //配送方式编码列表应用模型函数
        if (isAuth)
            dmcDto = dmcAppMod.appModFunc(distName, prefCity, province, db1Service);
        else
            logger.info("授权码：" + token + "，验证失败！");
        //设置响应数据
        if (dmcDto != null) {
            try {
                strResp = objectMapper.writeValueAsString(dmcDto);
                logger.info(strResp);
            } catch (JsonProcessingException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        } else {
            //获取异常响应
            code = codes[0];
            logger.info("错误码：code = " + code);
            IOTHttpRspVO excepResp = AppModConfig.getExcepResp(code);
            try {
                strResp = objectMapper.writeValueAsString(excepResp);
                logger.info(strResp);
            } catch (JsonProcessingException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }

        return strResp;
    }

    /**
     * 3.2.27 - 配货计划操作详情列表
     *
     * @param request
     * @return
     */
    @RequestMapping(value = "/v1/gsPlanOptDets", method = RequestMethod.GET)
    public String v1_gsPlanOptDets(HttpServletRequest request) {
        //初始化响应数据
        String strResp = null;
        GsPlanOptDetsDTO gpodDto = null;
        boolean isAuth = false;
        int code = 0;
        int[] codes = new int[1];
        //授权码
        String token = AppModConfig.GetHeadJsonReq(request, "Authorization");
        //开始日期
        String startDate = request.getParameter("startSubDate");
        //结束日期
        String endDate = request.getParameter("endSubDate");
        //项目点名称
        String ppName = request.getParameter("ppName");
        //区域名称
        String distName = request.getParameter("distName");
        if (distName == null) {
            distName = request.getParameter("distname");
        }
        //地级城市
        String prefCity = request.getParameter("prefCity");
        if (prefCity == null) {
            prefCity = request.getParameter("prefcity");
        }
        //省或直辖市
        String province = request.getParameter("province");
        //所属，0:其他，1:部属，2:市属，3: 区属
        String subLevel = request.getParameter("subLevel");
        //主管部门，0:市教委，1:商委，2:教育部
        String compDep = request.getParameter("compDep");
        //总分校标识，0:无，1:总校，2:分校
        String schGenBraFlag = request.getParameter("schGenBraFlag");
        //所属区域名称
        String subDistName = request.getParameter("subDistName");
        //证件主体，0:学校，1:外包
        String fblMb = request.getParameter("fblMb");
        //学校性质，0:公办，1:民办，2:其他
        String schProp = request.getParameter("schProp");
        //团餐公司名称
        String rmcName = request.getParameter("rmcName");
        //指派状态，0:未指派，1：已指派，2:已取消
        String assignStatus = request.getParameter("assignStatus");
        //配送状态，0:未派送，1:配送中，2:已配送
        String dispStatus = request.getParameter("dispStatus");
        //验收状态，0:未验收，1:已验收
        String acceptStatus = request.getParameter("acceptStatus");
        //配货批次号
        String distrBatNumber = request.getParameter("distrBatNumber");
        //学校类型（学制），0:托儿所，1:托幼园，2:托幼小，3:幼儿园，4:幼小，5:幼小初，6:幼小初高，7:小学，8:初级中学，9:高级中学，10:完全中学，
        //11:九年一贯制学校，12:十二年一贯制学校，13:职业初中，14:中等职业学校，15:工读学校，16:特殊教育学校，17:其他
        String schType = request.getParameter("schType");
        //配送类型，0:原料，1:成品菜
        String dispType = request.getParameter("dispType");
        //配送方式，0:统配，1:直配
        String dispMode = request.getParameter("dispMode");
        //发送状态，0:未发送，1:已发送
        String sendFlag = request.getParameter("sendFlag");
        //页号
        String page = request.getParameter("page");
        if (page == null) {
            page = "1";
        }
        //分页大小
        String pageSize = request.getParameter("pageSize");
        if (pageSize == null) {
            pageSize = "20";
        }

        //区域名称 格式：[“1”,”2”……]
        String distNames = request.getParameter("distNames");
        //所属 格式：[“1”,”2”……]
        String subLevels = request.getParameter("subLevels");
        //主管部门 格式：[“1”,”2”……]
        String compDeps = request.getParameter("compDeps");
        //学校性质 格式：[“1”,”2”……]
        String schProps = request.getParameter("schProps");
        //餐别 格式：[“1”,”2”……]
        //学校类型 格式：[“1”,”2”……]
        String schTypes = request.getParameter("schTypes");
        //菜单名称 格式：[“1”,”2”……]
        String assignStatuss = request.getParameter("assignStatuss");
        //经营模式 格式：[“1”,”2”……]
        String dispStatuss = request.getParameter("dispStatuss");

        String departmentId = request.getParameter("departmentId");
        String departmentIds = request.getParameter("departmentIds");

        String plastatus = request.getParameter("plastatus");

        logger.info("输入参数：" + "token = " + token + ", startDate = " + startDate + ", endDate = " + endDate
                + ", ppName = " + ppName + ", distName = " + distName + ", prefCity = " + prefCity
                + ", province = " + province + ", subLevel = " + subLevel + ", compDep = " + compDep
                + ", schGenBraFlag = " + schGenBraFlag + ", subDistName = " + subDistName
                + ", fblMb = " + fblMb + ", schProp = " + schProp
                + ", rmcName = " + rmcName + ", assignStatus = " + assignStatus + ", dispStatus = " + dispStatus
                + ", acceptStatus = " + acceptStatus + ", distrBatNumber = " + distrBatNumber + ", schType = " + schType
                + ", dispType = " + dispType + ", dispMode = " + dispMode + ", rmcName = " + sendFlag
                + ", page = " + page + ", pageSize = " + pageSize
                + ", distNames = " + distNames + ", subLevels = " + subLevels
                + ", compDeps = " + compDeps + ", schProps = " + schProps
                + ", schTypes = " + schTypes + ", assignStatuss = " + assignStatuss
                + ", dispStatuss = " + dispStatuss);
        //验证授权
        isAuth = AppModConfig.verifyAuthCode2(token, db2Service, codes);
        //配货计划操作详情列表应用模型函数
        if (isAuth)
            gpodDto = gpodAppMod.appModFunc(token, startDate, endDate, ppName, distName, prefCity,
                    province, subLevel, compDep, schGenBraFlag, subDistName, fblMb, schProp, rmcName,
                    assignStatus, dispStatus, acceptStatus, distrBatNumber, schType, dispType,
                    dispMode, sendFlag,
                    distNames, subLevels, compDeps, schProps,
                    schTypes, dispStatuss, assignStatuss,
                    departmentId, departmentIds, plastatus, null,
                    page, pageSize,
                    db1Service, db2Service, saasService, dbHiveGsService);
        else
            logger.info("授权码：" + token + "，验证失败！");
        //设置响应数据
        if (gpodDto != null) {
            try {
                strResp = objectMapper.writeValueAsString(gpodDto);
                logger.info(strResp);
            } catch (JsonProcessingException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        } else {
            //获取异常响应
            code = codes[0];
            logger.info("错误码：code = " + code);
            IOTHttpRspVO excepResp = AppModConfig.getExcepResp(code);
            try {
                strResp = objectMapper.writeValueAsString(excepResp);
                logger.info(strResp);
            } catch (JsonProcessingException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }

        return strResp;
    }

    /**
     * 3.2.28 - 导出配货计划操作详情列表
     *
     * @param request
     * @return
     */
    @RequestMapping(value = "/v1/expGsPlanOptDets", method = RequestMethod.GET)
    public String v1_expGsPlanOptDets(HttpServletRequest request) {
        //初始化响应数据
        String strResp = null;
        ExpGsPlanOptDetsDTO egpodDto = null;
        boolean isAuth = false;
        int code = 0;
        int[] codes = new int[1];
        //授权码
        String token = AppModConfig.GetHeadJsonReq(request, "Authorization");
        //开始日期，默认为当天日期，格式：xxxx-xx-xx
        String startDate = request.getParameter("startSubDate");
        //结束日期，默认为当天日期，格式：xxxx-xx-xx
        String endDate = request.getParameter("endSubDate");
        //项目点名称
        String ppName = request.getParameter("ppName");
        //区域名称
        String distName = request.getParameter("distName");
        if (distName == null) {
            distName = request.getParameter("distname");
        }
        //地级城市
        String prefCity = request.getParameter("prefCity");
        if (prefCity == null) {
            prefCity = request.getParameter("prefcity");
        }
        //省或直辖市
        String province = request.getParameter("province");
        //所属，0:其他，1:部属，2:市属，3: 区属
        String subLevel = request.getParameter("subLevel");
        //主管部门，0:市教委，1:商委，2:教育部
        String compDep = request.getParameter("compDep");
        //总分校标识，0:无，1:总校，2:分校
        String schGenBraFlag = request.getParameter("schGenBraFlag");
        //所属区域名称
        String subDistName = request.getParameter("subDistName");
        //证件主体，0:学校，1:外包
        String fblMb = request.getParameter("fblMb");
        //学校性质，0:公办，1:民办，2:其他
        String schProp = request.getParameter("schProp");
        //团餐公司名称
        String rmcName = request.getParameter("rmcName");
        //指派状态，0:未指派，1：已指派，2:已取消
        String assignStatus = request.getParameter("assignStatus");
        //配送状态，0:未派送，1:配送中，2: 已配送
        String dispStatus = request.getParameter("dispStatus");
        //验收状态，0:待验收，1:已验收
        String acceptStatus = request.getParameter("acceptStatus");
        //配货批次号
        String distrBatNumber = request.getParameter("distrBatNumber");
        //学校类型（学制），0:托儿所，1:托幼园，2:托幼小，3:幼儿园，4:幼小，5:幼小初，6:幼小初高，7:小学，8:初级中学，9:高级中学，10:完全中学，11:九年一贯制学校，12:十二年一贯制学校，13:职业初中，14:中等职业学校，15:工读学校，16:特殊教育学校，17:其他
        String schType = request.getParameter("schType");
        //配送类型，0:原料，1:成品菜
        String dispType = request.getParameter("dispType");
        //配送方式，0:统配，1:直配
        String dispMode = request.getParameter("dispMode");
        //发送状态，0:未发送，1:已发送
        String sendFlag = request.getParameter("sendFlag");

        //区域名称 格式：[“1”,”2”……]
        String distNames = request.getParameter("distNames");
        //所属 格式：[“1”,”2”……]
        String subLevels = request.getParameter("subLevels");
        //主管部门 格式：[“1”,”2”……]
        String compDeps = request.getParameter("compDeps");
        //学校性质 格式：[“1”,”2”……]
        String schProps = request.getParameter("schProps");
        //餐别 格式：[“1”,”2”……]
        //学校类型 格式：[“1”,”2”……]
        String schTypes = request.getParameter("schTypes");
        //菜单名称 格式：[“1”,”2”……]
        String assignStatuss = request.getParameter("assignStatuss");
        //经营模式 格式：[“1”,”2”……]
        String dispStatuss = request.getParameter("dispStatuss");

        String departmentId = request.getParameter("departmentId");
        String departmentIds = request.getParameter("departmentIds");

        String plastatus = request.getParameter("plastatus");

        logger.info("输入参数：" + "token = " + token + ", startDate = " + startDate
                + ", endDate = " + endDate + ", ppName = " + ppName
                + ", distName = " + distName + ", prefCity = " + prefCity
                + ", province = " + province + ", subLevel = " + subLevel
                + ", compDep = " + compDep + ", schGenBraFlag = " + schGenBraFlag
                + ", subDistName = " + subDistName + ", fblMb = " + fblMb
                + ", schProp = " + schProp
                + ", rmcName = " + rmcName + ", assignStatus = " + assignStatus
                + ", dispStatus = " + dispStatus + ", acceptStatus = " + acceptStatus
                + ", distrBatNumber = " + distrBatNumber + ", schType = " + schType
                + ", dispType = " + dispType + ", dispMode = " + dispMode
                + ", rmcName = " + sendFlag
                + ", distNames = " + distNames + ", subLevels = " + subLevels
                + ", compDeps = " + compDeps + ", schProps = " + schProps
                + ", schTypes = " + schTypes + ", assignStatuss = " + assignStatuss
                + ", dispStatuss = " + dispStatuss);
        //验证授权
        isAuth = AppModConfig.verifyAuthCode2(token, db2Service, codes);
        //导出配货计划操作详情列表应用模型函数
        if (isAuth)
            egpodDto = egpodAppMod.appModFunc(token, startDate, endDate, ppName, distName,
                    prefCity, province, subLevel, compDep, schGenBraFlag, subDistName, fblMb,
                    schProp, rmcName, assignStatus, dispStatus, acceptStatus, distrBatNumber,
                    schType, dispType, dispMode, sendFlag,
                    distNames, subLevels, compDeps, schProps,
                    schTypes, dispStatuss, assignStatuss,
                    departmentId, departmentIds, plastatus,
                    db1Service, db2Service, saasService, dbHiveGsService);
        else
            logger.info("授权码：" + token + "，验证失败！");
        //设置响应数据
        if (egpodDto != null) {
            try {
                strResp = objectMapper.writeValueAsString(egpodDto);
                logger.info(strResp);
            } catch (JsonProcessingException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        } else {
            //获取异常响应
            code = codes[0];
            logger.info("错误码：code = " + code);
            IOTHttpRspVO excepResp = AppModConfig.getExcepResp(code);
            try {
                strResp = objectMapper.writeValueAsString(excepResp);
                logger.info(strResp);
            } catch (JsonProcessingException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }

        return strResp;
    }

    /**
     * 3.2.29 - 菜品留样列表
     *
     * @param request
     * @return
     */
    @RequestMapping(value = "/v1/dishRetSamples", method = RequestMethod.GET)
    public String v1_dishRetSamples(HttpServletRequest request) {
        //初始化响应数据
        String strResp = null;
        DishRetSamplesDTO drsDto = null;
        boolean isAuth = false;
        int code = 0;
        int[] codes = new int[1];
        //授权码
        String token = AppModConfig.GetHeadJsonReq(request, "Authorization");
        //就餐开始日期
        String repastStartDate = request.getParameter("startSubDate");
        //就餐结束日期
        String repasEndDate = request.getParameter("endSubDate");
        //学校筛选方式，0:按主管部门，1:按所在地
        String schSelMode = request.getParameter("schSelMode");
        //所属，0:其他，1:部属，2:市属，3: 区属，按主管部门有效
        String subLevel = request.getParameter("subLevel");
        //主管部门，按主管部门有效
        String compDep = request.getParameter("compDep");
        //所属区域名称，按主管部门有效
        String subDistName = request.getParameter("subDistName");
        //区域名称
        String distName = request.getParameter("distName");
        if (distName == null) {
            distName = request.getParameter("distname");
        }
        //地级城市
        String prefCity = request.getParameter("prefCity");
        if (prefCity == null) {
            prefCity = request.getParameter("prefcity");
        }
        //省或直辖市
        String province = request.getParameter("province");
        //页号
        String page = request.getParameter("page");
        if (page == null) {
            page = "1";
        }
        //分页大小
        String pageSize = request.getParameter("pageSize");
        if (pageSize == null) {
            pageSize = "20";
        }

        //所属，0:其他，1:部属，2:市属，3: 区属，按主管部门有效(多个[1,3])
        String subLevels = request.getParameter("subLevels");
        //主管部门，按主管部门有效(多个[1_1,3_1])
        String compDeps = request.getParameter("compDeps");
        //区域名称(多个[1,3])
        String distNames = request.getParameter("distNames");

        String departmentIds = request.getParameter("departmentIds");

        logger.info("输入参数：" + "token = " + token + ", repastStartDate = " + repastStartDate + ", repasEndDate = " + repasEndDate +
                ", schSelMode = " + schSelMode + ", subLevel = " + subLevel + ", compDep = " + compDep +
                ", subDistName = " + subDistName + ", distName = " + distName + ", prefCity = " + prefCity +
                ", province = " + province + ", page = " + page + ", pageSize = " + pageSize +
                ", subLevels = " + subLevels + ", compDeps = " + compDeps + ", distNames = " + distNames);
        //验证授权
        isAuth = AppModConfig.verifyAuthCode2(token, db2Service, codes);
        //菜品留样列表应用模型函数
        if (isAuth)
            drsDto = drsAppMod.appModFunc(token, repastStartDate, repasEndDate, schSelMode, subLevel, compDep,
                    subDistName, distName, prefCity, province,
                    subLevels, compDeps, distNames, departmentIds,
                    page, pageSize, db1Service, db2Service);
        else
            logger.info("授权码：" + token + "，验证失败！");
        //设置响应数据
        if (drsDto != null) {
            try {
                strResp = objectMapper.writeValueAsString(drsDto);
                logger.info(strResp);
            } catch (JsonProcessingException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        } else {
            //获取异常响应
            code = codes[0];
            logger.info("错误码：code = " + code);
            IOTHttpRspVO excepResp = AppModConfig.getExcepResp(code);
            try {
                strResp = objectMapper.writeValueAsString(excepResp);
                logger.info(strResp);
            } catch (JsonProcessingException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }

        return strResp;
    }

    /**
     * 3.2.30 - 导出菜品留样列表
     *
     * @param request
     * @return
     */
    @RequestMapping(value = "/v1/expDishRetSamples", method = RequestMethod.GET)
    public String v1_expDishRetSamples(HttpServletRequest request) {
        //初始化响应数据
        String strResp = null;
        ExpDishRetSamplesDTO edrsDto = null;
        boolean isAuth = false;
        int code = 0;
        int[] codes = new int[1];
        //授权码
        String token = AppModConfig.GetHeadJsonReq(request, "Authorization");
        //就餐开始日期
        String repastStartDate = request.getParameter("startSubDate");
        //就餐结束日期
        String repasEndDate = request.getParameter("endSubDate");
        //学校筛选方式，0:按主管部门，1:按所在地
        String schSelMode = request.getParameter("schSelMode");
        //所属，0:其他，1:部属，2:市属，3: 区属，按主管部门有效
        String subLevel = request.getParameter("subLevel");
        //主管部门，按主管部门有效
        String compDep = request.getParameter("compDep");
        //所属区域名称，按主管部门有效
        String subDistName = request.getParameter("subDistName");
        //区域名称
        String distName = request.getParameter("distName");
        if (distName == null) {
            distName = request.getParameter("distname");
        }
        //地级城市
        String prefCity = request.getParameter("prefCity");
        if (prefCity == null) {
            prefCity = request.getParameter("prefcity");
        }
        //省或直辖市
        String province = request.getParameter("province");

        //所属，0:其他，1:部属，2:市属，3: 区属，按主管部门有效(多个[1,3])
        String subLevels = request.getParameter("subLevels");
        //主管部门，按主管部门有效(多个[1_1,3_1])
        String compDeps = request.getParameter("compDeps");
        //区域名称(多个[1,3])
        String distNames = request.getParameter("distNames");
        String departmentIds = request.getParameter("departmentIds");

        logger.info("输入参数：" + "token = " + token + ", repastStartDate = " + repastStartDate + ", repasEndDate = " + repasEndDate +
                ", schSelMode = " + schSelMode + ", subLevel = " + subLevel + ", compDep = " + compDep +
                ", subDistName = " + subDistName + ", distName = " + distName + ", prefCity = " + prefCity +
                ", province = " + province +
                ", subLevels = " + subLevels + ", compDeps = " + compDeps + ", distNames = " + distNames);

        //验证授权
        isAuth = AppModConfig.verifyAuthCode2(token, db2Service, codes);
        //导出菜品留样列表应用模型函数
        if (isAuth)
            edrsDto = edrsAppMod.appModFunc(token, repastStartDate, repasEndDate, schSelMode, subLevel, compDep,
                    subDistName, distName, prefCity, province, subLevels, compDeps, distNames, departmentIds,
                    db1Service, db2Service);
        else
            logger.info("授权码：" + token + "，验证失败！");
        //设置响应数据
        if (edrsDto != null) {
            try {
                strResp = objectMapper.writeValueAsString(edrsDto);
                logger.info(strResp);
            } catch (JsonProcessingException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        } else {
            //获取异常响应
            code = codes[0];
            logger.info("错误码：code = " + code);
            IOTHttpRspVO excepResp = AppModConfig.getExcepResp(code);
            try {
                strResp = objectMapper.writeValueAsString(excepResp);
                logger.info(strResp);
            } catch (JsonProcessingException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }

        return strResp;
    }

    /**
     * 3.2.31 - 是否留样编码列表
     *
     * @param request
     * @return
     */
    @RequestMapping(value = "/v1/isRsCodes", method = RequestMethod.GET)
    public String v1_isRsCodes(HttpServletRequest request) {
        //初始化响应数据
        String strResp = null;
        IsRsCodesDTO ircDto = null;
        boolean isAuth = false;
        int code = 0;
        int[] codes = new int[1];
        //授权码
        String token = AppModConfig.GetHeadJsonReq(request, "Authorization");
        //区域名称
        String distName = request.getParameter("distName");
        if (distName == null) {
            distName = request.getParameter("distname");
        }
        //地级城市
        String prefCity = request.getParameter("prefCity");
        if (prefCity == null) {
            prefCity = request.getParameter("prefcity");
        }
        //省或直辖市
        String province = request.getParameter("province");
        logger.info("输入参数：" + "token = " + token + ", distName = " + distName + ", prefCity = " + prefCity + ", province = " + province);
        //验证授权
        isAuth = AppModConfig.verifyAuthCode2(token, db2Service, codes);
        //是否留样编码列表应用模型函数
        if (isAuth)
            ircDto = ircAppMod.appModFunc(distName, prefCity, province, db1Service);
        else
            logger.info("授权码：" + token + "，验证失败！");
        //设置响应数据
        if (ircDto != null) {
            try {
                strResp = objectMapper.writeValueAsString(ircDto);
                logger.info(strResp);
            } catch (JsonProcessingException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        } else {
            //获取异常响应
            code = codes[0];
            logger.info("错误码：code = " + code);
            IOTHttpRspVO excepResp = AppModConfig.getExcepResp(code);
            try {
                strResp = objectMapper.writeValueAsString(excepResp);
                logger.info(strResp);
            } catch (JsonProcessingException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }

        return strResp;
    }

    /**
     * 3.2.32 - 餐别编码列表
     *
     * @param request
     * @return
     */
    @RequestMapping(value = "/v1/caterTypeCodes", method = RequestMethod.GET)
    public String v1_caterTypeCodes(HttpServletRequest request) {
        //初始化响应数据
        String strResp = null;
        CaterTypeCodesDTO ctcDto = null;
        boolean isAuth = false;
        int code = 0;
        int[] codes = new int[1];
        //授权码
        String token = AppModConfig.GetHeadJsonReq(request, "Authorization");
        //区域名称
        String distName = request.getParameter("distName");
        if (distName == null) {
            distName = request.getParameter("distname");
        }
        //地级城市
        String prefCity = request.getParameter("prefCity");
        if (prefCity == null) {
            prefCity = request.getParameter("prefcity");
        }
        //省或直辖市
        String province = request.getParameter("province");
        logger.info("输入参数：" + "token = " + token + ", distName = " + distName + ", prefCity = " + prefCity + ", province = " + province);
        //验证授权
        isAuth = AppModConfig.verifyAuthCode2(token, db2Service, codes);
        //餐别编码列表应用模型函数
        if (isAuth)
            ctcDto = ctcAppMod.appModFunc(distName, prefCity, province, db1Service, saasService);
        else
            logger.info("授权码：" + token + "，验证失败！");
        //设置响应数据
        if (ctcDto != null) {
            try {
                strResp = objectMapper.writeValueAsString(ctcDto);
                logger.info(strResp);
            } catch (JsonProcessingException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        } else {
            //获取异常响应
            code = codes[0];
            logger.info("错误码：code = " + code);
            IOTHttpRspVO excepResp = AppModConfig.getExcepResp(code);
            try {
                strResp = objectMapper.writeValueAsString(excepResp);
                logger.info(strResp);
            } catch (JsonProcessingException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }

        return strResp;
    }

    /**
     * 3.2.33 - 菜单名称编码列表
     *
     * @param request
     * @return
     */
    @RequestMapping(value = "/v1/menuNameCodes", method = RequestMethod.GET)
    public String v1_menuNameCodes(HttpServletRequest request) {
        //初始化响应数据
        String strResp = null;
        MenuNameCodesDTO mnDto = null;
        boolean isAuth = false;
        int code = 0;
        int[] codes = new int[1];
        //授权码
        String token = AppModConfig.GetHeadJsonReq(request, "Authorization");
        //区域名称
        String distName = request.getParameter("distName");
        if (distName == null) {
            distName = request.getParameter("distname");
        }
        //地级城市
        String prefCity = request.getParameter("prefCity");
        if (prefCity == null) {
            prefCity = request.getParameter("prefcity");
        }
        //省或直辖市
        String province = request.getParameter("province");
        //页号
        String page = request.getParameter("page");
        if (page == null) {
            page = "1";
        }
        //分页大小
        String pageSize = request.getParameter("pageSize");
        if (pageSize == null) {
            pageSize = String.valueOf(AppModConfig.maxPageSize);
        }
        logger.info("输入参数：" + "token = " + token + ", distName = " + distName + ", prefCity = " + prefCity + ", province = " + province + ", page = " + page + ", pageSize = " + pageSize);
        //验证授权
        isAuth = AppModConfig.verifyAuthCode2(token, db2Service, codes);
        //菜单名称编码列表应用模型函数
        if (isAuth)
            mnDto = mncAppMod.appModFunc(distName, prefCity, province, page, pageSize, db1Service, saasService);
        else
            logger.info("授权码：" + token + "，验证失败！");
        //设置响应数据
        if (mnDto != null) {
            try {
                strResp = objectMapper.writeValueAsString(mnDto);
                logger.info(strResp);
            } catch (JsonProcessingException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        } else {
            //获取异常响应
            code = codes[0];
            logger.info("错误码：code = " + code);
            IOTHttpRspVO excepResp = AppModConfig.getExcepResp(code);
            try {
                strResp = objectMapper.writeValueAsString(excepResp);
                logger.info(strResp);
            } catch (JsonProcessingException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }

        return strResp;
    }

    /**
     * 3.2.34 - 留样单位编码列表
     *
     * @param request
     * @return
     */
    @RequestMapping(value = "/v1/rsUnitCodes", method = RequestMethod.GET)
    public String v1_rsUnitCodes(HttpServletRequest request) {
        //初始化响应数据
        String strResp = null;
        RsUnitCodesDTO rucDto = null;
        boolean isAuth = false;
        int code = 0;
        int[] codes = new int[1];
        //授权码
        String token = AppModConfig.GetHeadJsonReq(request, "Authorization");
        //区域名称
        String distName = request.getParameter("distName");
        if (distName == null) {
            distName = request.getParameter("distname");
        }
        //地级城市
        String prefCity = request.getParameter("prefCity");
        if (prefCity == null) {
            prefCity = request.getParameter("prefcity");
        }
        //省或直辖市
        String province = request.getParameter("province");
        //页号
        String page = request.getParameter("page");
        if (page == null) {
            page = "1";
        }
        //分页大小
        String pageSize = request.getParameter("pageSize");
        if (pageSize == null) {
            pageSize = String.valueOf(AppModConfig.maxPageSize);
        }
        logger.info("输入参数：" + "token = " + token + ", distName = " + distName + ", prefCity = " + prefCity + ", province = " + province + ", page = " + page + ", pageSize = " + pageSize);
        //验证授权
        isAuth = AppModConfig.verifyAuthCode2(token, db2Service, codes);
        //留样单位编码列表应用模型函数
        if (isAuth)
            rucDto = rucAppMod.appModFunc(distName, prefCity, province, page, pageSize, db1Service, saasService);
        else
            logger.info("授权码：" + token + "，验证失败！");
        //设置响应数据
        if (rucDto != null) {
            try {
                strResp = objectMapper.writeValueAsString(rucDto);
                logger.info(strResp);
            } catch (JsonProcessingException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        } else {
            //获取异常响应
            code = codes[0];
            logger.info("错误码：code = " + code);
            IOTHttpRspVO excepResp = AppModConfig.getExcepResp(code);
            try {
                strResp = objectMapper.writeValueAsString(excepResp);
                logger.info(strResp);
            } catch (JsonProcessingException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }

        return strResp;
    }

    /**
     * 3.2.35 - 菜品留样详情列表
     *
     * @param request
     * @return
     */
    @RequestMapping(value = "/v1/dishRsDets", method = RequestMethod.GET)
    public String v1_dishRsDets(HttpServletRequest request) {
        //初始化响应数据
        String strResp = null;
        DishRsDetsDTO drdDto = null;
        boolean isAuth = false;
        int code = 0;
        int[] codes = new int[1];
        //授权码
        String token = AppModConfig.GetHeadJsonReq(request, "Authorization");
        //就餐开始日期
        String repastStartDate = request.getParameter("startSubDate");
        //就餐结束日期
        String repasEndDate = request.getParameter("endSubDate");
        //区域名称
        String distName = request.getParameter("distName");
        if (distName == null) {
            distName = request.getParameter("distname");
        }
        //地级城市
        String prefCity = request.getParameter("prefCity");
        if (prefCity == null) {
            prefCity = request.getParameter("prefcity");
        }
        //省或直辖市
        String province = request.getParameter("province");
        //所属，0:其他，1:部属，2:市属，3: 区属
        String subLevel = request.getParameter("subLevel");
        //主管部门，0:市教委，1:商委，2:教育部
        String compDep = request.getParameter("compDep");
        //总分校标识，0:无，1:总校，2:分校
        String schGenBraFlag = request.getParameter("schGenBraFlag");
        //所属区域名称
        String subDistName = request.getParameter("subDistName");
        //证件主体，0:学校，1:外包
        String fblMb = request.getParameter("fblMb");
        //学校性质，0:公办，1:民办，2:其他
        String schProp = request.getParameter("schProp");
        //项目点名称
        String ppName = request.getParameter("ppName");
        //团餐公司名称
        String rmcName = request.getParameter("rmcName");
        //是否留样标识，0:未留样，1:已留样
        String rsFlag = request.getParameter("rsFlag");
        //餐别，0:早餐，1:午餐，2:晚餐，3:午点，4:早点
        String caterType = request.getParameter("caterType");
        //学校类型（学制），0:托儿所，1:托幼园，2:托幼小，3:幼儿园，4:幼小，5:幼小初，6:幼小初高，7:小学，8:初级中学，9:高级中学，10:完全中学，11:九年一贯制学校，12:十二年一贯制学校，13:职业初中，14:中等职业学校，15:工读学校，16:特殊教育学校，17:其他
        String schType = request.getParameter("schType");
        //菜单名称
        String menuName = request.getParameter("menuName");
        //经营模式（供餐类型），0:自营，1:外包-现场加工，2:外包-快餐配送
        String optMode = request.getParameter("optMode");
        //留样单位
        String rsUnit = request.getParameter("rsUnit");
        //页号
        String page = request.getParameter("page");
        if (page == null)
            page = "1";
        //分页大小
        String pageSize = request.getParameter("pageSize");
        if (pageSize == null)
            pageSize = "20";

        //区域名称 格式：[“1”,”2”……]
        String distNames = request.getParameter("distNames");
        //所属 格式：[“1”,”2”……]
        String subLevels = request.getParameter("subLevels");
        //主管部门 格式：[“1”,”2”……]
        String compDeps = request.getParameter("compDeps");
        //学校性质 格式：[“1”,”2”……]
        String schProps = request.getParameter("schProps");
        //餐别 格式：[“1”,”2”……]
        String caterTypes = request.getParameter("caterTypes");
        //学校类型 格式：[“1”,”2”……]
        String schTypes = request.getParameter("schTypes");
        //菜单名称 格式：[“1”,”2”……]
        String menuNames = request.getParameter("menuNames");
        //经营模式 格式：[“1”,”2”……]
        String optModes = request.getParameter("optModes");
        String departmentId = request.getParameter("departmentId");
        String departmentIds = request.getParameter("departmentIds");
        String reserveStatus = request.getParameter("reserveStatus");

        logger.info("输入参数：" + "token = " + token + ", repastStartDate = " + repastStartDate
                + ", repasEndDate = " + repasEndDate + ", distName = " + distName
                + ", prefCity = " + prefCity + ", province = " + province
                + ", subLevel = " + subLevel + ", compDep = " + compDep
                + ", schGenBraFlag = " + schGenBraFlag + ", subDistName = " + subDistName
                + ", fblMb = " + fblMb + ", schProp = " + schProp
                + ", ppName = " + ppName + ", rmcName = " + rmcName
                + ", rsFlag = " + rsFlag + ", caterType = " + caterType
                + ", schType = " + schType + ", menuName = " + menuName
                + ", optMode = " + optMode + ", rsUnit = " + rsUnit
                + ", distNames = " + distNames + ", subLevels = " + subLevels
                + ", compDeps = " + compDeps + ", caterTypes = " + caterTypes
                + ", schProps = " + schProps + ", optModes = " + optModes
                + ", schTypes = " + schTypes + ", menuNames = " + menuNames
                + ", page = " + page + ", pageSize = " + pageSize);
        //验证授权
        isAuth = AppModConfig.verifyAuthCode2(token, db2Service, codes);
        //菜品留样详情列表应用模型函数
        if (isAuth)
            drdDto = drdAppMod.appModFunc(token, repastStartDate, repasEndDate, distName,
                    prefCity, province, subLevel, compDep, schGenBraFlag, subDistName,
                    fblMb, schProp, ppName, rmcName, rsFlag, caterType, schType, menuName,
                    optMode, rsUnit,
                    distNames, subLevels, compDeps, schProps, caterTypes, schTypes, menuNames, optModes,
                    departmentId, departmentIds, reserveStatus, null,
                    page, pageSize, db1Service, db2Service, saasService);
        else
            logger.info("授权码：" + token + "，验证失败！");
        //设置响应数据
        if (drdDto != null) {
            try {
                strResp = objectMapper.writeValueAsString(drdDto);
                logger.info(strResp);
            } catch (JsonProcessingException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        } else {
            //获取异常响应
            code = codes[0];
            logger.info("错误码：code = " + code);
            IOTHttpRspVO excepResp = AppModConfig.getExcepResp(code);
            try {
                strResp = objectMapper.writeValueAsString(excepResp);
                logger.info(strResp);
            } catch (JsonProcessingException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }

        return strResp;
    }

    /**
     * 3.2.36 - 导出菜品留样详情列表
     *
     * @param request
     * @return
     */
    @RequestMapping(value = "/v1/expDishRsDets", method = RequestMethod.GET)
    public String v1_expDishRsDets(HttpServletRequest request) {
        //初始化响应数据
        String strResp = null;
        ExpDishRsDetsDTO edrdDto = null;
        boolean isAuth = false;
        int code = 0;
        int[] codes = new int[1];
        //授权码
        String token = AppModConfig.GetHeadJsonReq(request, "Authorization");
        //就餐开始日期
        String repastStartDate = request.getParameter("startSubDate");
        //就餐结束日期
        String repasEndDate = request.getParameter("endSubDate");
        //区域名称
        String distName = request.getParameter("distName");
        if (distName == null) {
            distName = request.getParameter("distname");
        }
        //地级城市
        String prefCity = request.getParameter("prefCity");
        if (prefCity == null) {
            prefCity = request.getParameter("prefcity");
        }
        //省或直辖市
        String province = request.getParameter("province");
        //所属，0:其他，1:部属，2:市属，3: 区属
        String subLevel = request.getParameter("subLevel");
        //主管部门，0:市教委，1:商委，2:教育部
        String compDep = request.getParameter("compDep");
        //总分校标识，0:无，1:总校，2:分校
        String schGenBraFlag = request.getParameter("schGenBraFlag");
        //所属区域名称
        String subDistName = request.getParameter("subDistName");
        //证件主体，0:学校，1:外包
        String fblMb = request.getParameter("fblMb");
        //学校性质，0:公办，1:民办，2:其他
        String schProp = request.getParameter("schProp");
        //项目点名称
        String ppName = request.getParameter("ppName");
        //团餐公司名称
        String rmcName = request.getParameter("rmcName");
        //是否留样标识，0:未留样，1:已留样
        String rsFlag = request.getParameter("rsFlag");
        //餐别，0:早餐，1:午餐，2:晚餐，3:午点，4:早点
        String caterType = request.getParameter("caterType");
        //学校类型（学制），0:托儿所，1:托幼园，2:托幼小，3:幼儿园，4:幼小，5:幼小初，6:幼小初高，7:小学，8:初级中学，9:高级中学，10:完全中学，11:九年一贯制学校，12:十二年一贯制学校，13:职业初中，14:中等职业学校，15:工读学校，16:特殊教育学校，17:其他
        String schType = request.getParameter("schType");
        //菜单名称
        String menuName = request.getParameter("menuName");
        //经营模式（供餐类型），0:自营，1:外包-现场加工，2:外包-快餐配送
        String optMode = request.getParameter("optMode");
        //留样单位
        String rsUnit = request.getParameter("rsUnit");

        //区域名称 格式：[“1”,”2”……]
        String distNames = request.getParameter("distNames");
        //所属 格式：[“1”,”2”……]
        String subLevels = request.getParameter("subLevels");
        //主管部门 格式：[“1”,”2”……]
        String compDeps = request.getParameter("compDeps");
        //学校性质 格式：[“1”,”2”……]
        String schProps = request.getParameter("schProps");
        //餐别 格式：[“1”,”2”……]
        String caterTypes = request.getParameter("caterTypes");
        //学校类型 格式：[“1”,”2”……]
        String schTypes = request.getParameter("schTypes");
        //菜单名称 格式：[“1”,”2”……]
        String menuNames = request.getParameter("menuNames");
        //经营模式 格式：[“1”,”2”……]
        String optModes = request.getParameter("optModes");

        String departmentId = request.getParameter("departmentId");
        String departmentIds = request.getParameter("departmentIds");
        String reserveStatus = request.getParameter("reserveStatus");

        logger.info("输入参数：" + "token = " + token + ", repastStartDate = " + repastStartDate
                + ", repasEndDate = " + repasEndDate + ", distName = " + distName
                + ", prefCity = " + prefCity + ", province = " + province
                + ", subLevel = " + subLevel + ", compDep = " + compDep
                + ", schGenBraFlag = " + schGenBraFlag + ", subDistName = " + subDistName
                + ", fblMb = " + fblMb + ", schProp = " + schProp + ", ppName = " + ppName
                + ", rmcName = " + rmcName + ", rsFlag = " + rsFlag + ", caterType = " + caterType
                + ", schType = " + schType + ", menuName = " + menuName
                + ", optMode = " + optMode + ", rsUnit = " + rsUnit
                + ", distNames = " + distNames + ", subLevels = " + subLevels
                + ", compDeps = " + compDeps + ", caterTypes = " + caterTypes
                + ", schProps = " + schProps + ", optModes = " + optModes
                + ", schTypes = " + schTypes + ", menuNames = " + menuNames);
        //验证授权
        isAuth = AppModConfig.verifyAuthCode2(token, db2Service, codes);
        //导出菜品留样详情列表应用模型函数
        if (isAuth)
            edrdDto = edrdAppMod.appModFunc(token, repastStartDate, repasEndDate, distName, prefCity,
                    province, subLevel, compDep, schGenBraFlag, subDistName, fblMb, schProp, ppName,
                    rmcName, rsFlag, caterType, schType, menuName, optMode, rsUnit,
                    distNames, subLevels, compDeps, schProps, caterTypes, schTypes, menuNames, optModes,
                    departmentId, departmentIds, reserveStatus,
                    db1Service, db2Service, saasService);
        else
            logger.info("授权码：" + token + "，验证失败！");
        //设置响应数据
        if (edrdDto != null) {
            try {
                strResp = objectMapper.writeValueAsString(edrdDto);
                logger.info(strResp);
            } catch (JsonProcessingException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        } else {
            //获取异常响应
            code = codes[0];
            logger.info("错误码：code = " + code);
            IOTHttpRspVO excepResp = AppModConfig.getExcepResp(code);
            try {
                strResp = objectMapper.writeValueAsString(excepResp);
                logger.info(strResp);
            } catch (JsonProcessingException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }

        return strResp;
    }

    /**
     * 3.2.37 - 餐厨垃圾学校回收列表
     *
     * @param request
     * @return
     */
    @RequestMapping(value = "/v1/kwSchRecs", method = RequestMethod.GET)
    public String v1_kwSchRecs(HttpServletRequest request) {
        //初始化响应数据
        String strResp = null;
        KwSchRecsDTO ksrDto = null;
        boolean isAuth = false;
        int code = 0;
        int[] codes = new int[1];
        //授权码
        String token = AppModConfig.GetHeadJsonReq(request, "Authorization");
        //回收开始日期
        String recStartDate = request.getParameter("startSubDate");
        //回收结束日期
        String recEndDate = request.getParameter("endSubDate");
        //区域名称
        String distName = request.getParameter("distName");
        if (distName == null) {
            distName = request.getParameter("distname");
        }
        //地级城市
        String prefCity = request.getParameter("prefCity");
        if (prefCity == null) {
            prefCity = request.getParameter("prefcity");
        }
        //省或直辖市
        String province = request.getParameter("province");
        //页号
        String page = request.getParameter("page");
        if (page == null) {
            page = "1";
        }
        //分页大小
        String pageSize = request.getParameter("pageSize");
        if (pageSize == null) {
            pageSize = "20";
        }
        //区域名称(多个[1,3])
        String distNames = request.getParameter("distNames");
        logger.info("输入参数：" + "token = " + token + ", recStartDate = " + recStartDate
                + ", recEndDate = " + recEndDate + ", distName = " + distName
                + ", prefCity = " + prefCity + ", province = " + province
                + ", distNames = " + distNames
                + ", page = " + page + ", pageSize = " + pageSize);
        //验证授权
        isAuth = AppModConfig.verifyAuthCode2(token, db2Service, codes);
        //餐厨垃圾学校回收列表应用模型函数
        if (isAuth)
            ksrDto = ksrAppMod.appModFunc(token, recStartDate, recEndDate, distName, prefCity, province,
                    distNames,
                    page, pageSize, db1Service, db2Service, dbHiveRecyclerWasteService);
        else
            logger.info("授权码：" + token + "，验证失败！");
        //设置响应数据
        if (ksrDto != null) {
            try {
                strResp = objectMapper.writeValueAsString(ksrDto);
                logger.info(strResp);
            } catch (JsonProcessingException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        } else {
            //获取异常响应
            code = codes[0];
            logger.info("错误码：code = " + code);
            IOTHttpRspVO excepResp = AppModConfig.getExcepResp(code);
            try {
                strResp = objectMapper.writeValueAsString(excepResp);
                logger.info(strResp);
            } catch (JsonProcessingException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }

        return strResp;
    }

    /**
     * 3.2.38 - 导出餐厨垃圾学校回收列表
     *
     * @param request
     * @return
     */
    @RequestMapping(value = "/v1/expKwSchRecs", method = RequestMethod.GET)
    public String v1_expKwSchRecs(HttpServletRequest request) {
        //初始化响应数据
        String strResp = null;
        ExpKwSchRecsDTO eksrDto = null;
        boolean isAuth = false;
        int code = 0;
        int[] codes = new int[1];
        //授权码
        String token = AppModConfig.GetHeadJsonReq(request, "Authorization");
        //回收开始日期
        String recStartDate = request.getParameter("startSubDate");
        //回收结束日期
        String recEndDate = request.getParameter("endSubDate");
        //区域名称
        String distName = request.getParameter("distName");
        if (distName == null) {
            distName = request.getParameter("distname");
        }
        //地级城市
        String prefCity = request.getParameter("prefCity");
        if (prefCity == null) {
            prefCity = request.getParameter("prefcity");
        }
        //省或直辖市
        String province = request.getParameter("province");

        //区域名称(多个[1,3])
        String distNames = request.getParameter("distNames");

        logger.info("输入参数：" + "token = " + token + ", recStartDate = " + recStartDate
                + ", recEndDate = " + recEndDate + ", distName = " + distName
                + ", prefCity = " + prefCity + ", province = " + province
                + ", distNames = " + distNames);
        //验证授权
        isAuth = AppModConfig.verifyAuthCode2(token, db2Service, codes);
        //导出餐厨垃圾学校回收列表应用模型函数
        if (isAuth)
            eksrDto = eksrAppMod.appModFunc(token, recStartDate, recEndDate, distName, prefCity, province, distNames,
                    db1Service, db2Service, dbHiveRecyclerWasteService);
        else
            logger.info("授权码：" + token + "，验证失败！");
        //设置响应数据
        if (eksrDto != null) {
            try {
                strResp = objectMapper.writeValueAsString(eksrDto);
                logger.info(strResp);
            } catch (JsonProcessingException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        } else {
            //获取异常响应
            code = codes[0];
            logger.info("错误码：code = " + code);
            IOTHttpRspVO excepResp = AppModConfig.getExcepResp(code);
            try {
                strResp = objectMapper.writeValueAsString(excepResp);
                logger.info(strResp);
            } catch (JsonProcessingException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }

        return strResp;
    }

    /**
     * 3.2.39 - 回收单位编码列表
     *
     * @param request
     * @return
     */
    @RequestMapping(value = "/v1/recUnitCodes", method = RequestMethod.GET)
    public String v1_recUnitCodes(HttpServletRequest request) {
        //初始化响应数据
        String strResp = null;
        RecUnitCodesDTO rucDto = null;
        boolean isAuth = false;
        int code = 0;
        int[] codes = new int[1];
        //授权码
        String token = AppModConfig.GetHeadJsonReq(request, "Authorization");
        //区域名称
        String distName = request.getParameter("distName");
        if (distName == null)
            distName = request.getParameter("distname");
        //地级城市
        String prefCity = request.getParameter("prefCity");
        if (prefCity == null)
            prefCity = request.getParameter("prefcity");
        //省或直辖市
        String province = request.getParameter("province");
        //页号
        String page = request.getParameter("page");
        if (page == null) {
            page = "1";
        }
        //分页大小
        String pageSize = request.getParameter("pageSize");
        if (pageSize == null) {
            pageSize = String.valueOf(AppModConfig.maxPageSize);
        }
        logger.info("输入参数：" + "token = " + token + ", distName = " + distName + ", prefCity = " + prefCity + ", province = " + province + ", page = " + page + ", pageSize = " + pageSize);
        //验证授权
        isAuth = AppModConfig.verifyAuthCode2(token, db2Service, codes);
        //回收单位编码列表应用模型函数
        if (isAuth)
            rucDto = reucAppMod.appModFunc(distName, prefCity, province, page, pageSize, db1Service, saasService);
        else
            logger.info("授权码：" + token + "，验证失败！");
        //设置响应数据
        if (rucDto != null) {
            try {
                strResp = objectMapper.writeValueAsString(rucDto);
                logger.info(strResp);
            } catch (JsonProcessingException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        } else {
            //获取异常响应
            code = codes[0];
            logger.info("错误码：code = " + code);
            IOTHttpRspVO excepResp = AppModConfig.getExcepResp(code);
            try {
                strResp = objectMapper.writeValueAsString(excepResp);
                logger.info(strResp);
            } catch (JsonProcessingException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }

        return strResp;
    }

    /**
     * 3.2.40 - 回收人编码列表
     *
     * @param request
     * @return
     */
    @RequestMapping(value = "/v1/recPersonCodes", method = RequestMethod.GET)
    public String v1_recPersonCodes(HttpServletRequest request) {
        //初始化响应数据
        String strResp = null;
        RecPersonCodesDTO rpcDto = null;
        boolean isAuth = false;
        int code = 0;
        int[] codes = new int[1];
        //授权码
        String token = AppModConfig.GetHeadJsonReq(request, "Authorization");
        //区域名称
        String distName = request.getParameter("distName");
        if (distName == null) {
            distName = request.getParameter("distname");
        }
        //地级城市
        String prefCity = request.getParameter("prefCity");
        if (prefCity == null) {
            prefCity = request.getParameter("prefcity");
        }
        //省或直辖市
        String province = request.getParameter("province");
        //页号
        String page = request.getParameter("page");
        if (page == null) {
            page = "1";
        }
        //分页大小
        String pageSize = request.getParameter("pageSize");
        if (pageSize == null) {
            pageSize = String.valueOf(AppModConfig.maxPageSize);
        }
        logger.info("输入参数：" + "token = " + token + ", distName = " + distName + ", prefCity = " + prefCity + ", province = " + province + ", page = " + page + ", pageSize = " + pageSize);
        //验证授权
        isAuth = AppModConfig.verifyAuthCode2(token, db2Service, codes);
        //回收人编码列表应用模型函数
        if (isAuth)
            rpcDto = rpcAppMod.appModFunc(distName, prefCity, province, page, pageSize, db1Service, saasService);
        else
            logger.info("授权码：" + token + "，验证失败！");
        //设置响应数据
        if (rpcDto != null) {
            try {
                strResp = objectMapper.writeValueAsString(rpcDto);
                logger.info(strResp);
            } catch (JsonProcessingException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        } else {
            //获取异常响应
            code = codes[0];
            logger.info("错误码：code = " + code);
            IOTHttpRspVO excepResp = AppModConfig.getExcepResp(code);
            try {
                strResp = objectMapper.writeValueAsString(excepResp);
                logger.info(strResp);
            } catch (JsonProcessingException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }

        return strResp;
    }

    /**
     * 2.3.41 - 学校餐厨垃圾详情列表
     *
     * @param request
     * @return
     */
    @RequestMapping(value = "/v1/schKwDets", method = RequestMethod.GET)
    public String v1_schKwDets(HttpServletRequest request) {
        //初始化响应数据
        String strResp = null;
        SchKwDetsDTO skdDto = null;
        boolean isAuth = false;
        int code = 0;
        int[] codes = new int[1];
        //授权码
        String token = AppModConfig.GetHeadJsonReq(request, "Authorization");
        //回收开始日期
        String recStartDate = request.getParameter("startSubDate");
        //回收结束日期
        String recEndDate = request.getParameter("endSubDate");
        //项目点名称
        String ppName = request.getParameter("ppName");
        //所属，0:其他，1:部属，2:市属，3: 区属
        String subLevel = request.getParameter("subLevel");
        //主管部门，0:市教委，1:商委，2:教育部
        String compDep = request.getParameter("compDep");
        //总分校标识，0:无，1:总校，2:分校
        String schGenBraFlag = request.getParameter("schGenBraFlag");
        //所属区域名称
        String subDistName = request.getParameter("subDistName");
        //证件主体，0:学校，1:外包
        String fblMb = request.getParameter("fblMb");
        //学校性质，0:公办，1:民办，2:其他
        String schProp = request.getParameter("schProp");
        //区域名称
        String distName = request.getParameter("distName");
        if (distName == null)
            distName = request.getParameter("distname");
        //地级城市
        String prefCity = request.getParameter("prefCity");
        if (prefCity == null)
            prefCity = request.getParameter("prefcity");
        //省或直辖市
        String province = request.getParameter("province");
        //学校类型（学制），0:托儿所，1:托幼园，2:托幼小，3:幼儿园，4:幼小，5:幼小初，6:幼小初高，7:小学，8:初级中学，9:高级中学，10:完全中学，11:九年一贯制学校，12:十二年一贯制学校，13:职业初中，14:中等职业学校，15:工读学校，16:特殊教育学校，17:其他
        String schType = request.getParameter("schType");
        //团餐公司名称
        String rmcName = request.getParameter("rmcName");
        //回收单位
        String recComany = request.getParameter("recComany");
        //回收人
        String recPerson = request.getParameter("recPerson");
        //页号
        String page = request.getParameter("page");
        if (page == null)
            page = "1";
        //分页大小
        String pageSize = request.getParameter("pageSize");
        if (pageSize == null)
            pageSize = "20";

        //区域名称 格式：[“1”,”2”……]
        String distNames = request.getParameter("distNames");
        //所属 格式：[“1”,”2”……]
        String subLevels = request.getParameter("subLevels");
        //主管部门 格式：[“1”,”2”……]
        String compDeps = request.getParameter("compDeps");
        //学校性质 格式：[“1”,”2”……]
        String schProps = request.getParameter("schProps");
        //学校类型 格式：[“1”,”2”……]
        String schTypes = request.getParameter("schTypes");

        logger.info("输入参数：" + "token = " + token + ", recStartDate = " + recStartDate
                + ", recEndDate = " + recEndDate + ", ppName = " + ppName
                + ", subLevel = " + subLevel + ", compDep = " + compDep
                + ", schGenBraFlag = " + schGenBraFlag + ", subDistName = " + subDistName
                + ", fblMb = " + fblMb + ", schProp = " + schProp + ", distName = " + distName
                + ", prefCity = " + prefCity + ", province = " + province
                + ", schType = " + schType + ", rmcName = " + rmcName + ", recComany = " + recComany
                + ", distNames = " + distNames + ", subLevels = " + subLevels
                + ", compDeps = " + compDeps
                + ", schProps = " + schProps
                + ", schTypes = " + schTypes
                + ", recPerson = " + recPerson + ", page = " + page + ", pageSize = " + pageSize);
        //验证授权
        isAuth = AppModConfig.verifyAuthCode2(token, db2Service, codes);
        //学校餐厨垃圾详情列表应用模型函数
        if (isAuth)
            skdDto = skdAppMod.appModFunc(token, recStartDate, recEndDate, ppName, distName,
                    prefCity, province, schType, rmcName, recComany, recPerson, schProp,
                    subLevel, compDep,
                    distNames, subLevels, compDeps, schProps, schTypes,
                    page, pageSize, db1Service, db2Service, saasService, dbHiveRecyclerWasteService);
        else
            logger.info("授权码：" + token + "，验证失败！");
        //设置响应数据
        if (skdDto != null) {
            try {
                strResp = objectMapper.writeValueAsString(skdDto);
                logger.info(strResp);
            } catch (JsonProcessingException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        } else {
            //获取异常响应
            code = codes[0];
            logger.info("错误码：code = " + code);
            IOTHttpRspVO excepResp = AppModConfig.getExcepResp(code);
            try {
                strResp = objectMapper.writeValueAsString(excepResp);
                logger.info(strResp);
            } catch (JsonProcessingException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }

        return strResp;
    }

    /**
     * 2.3.42 - 导出学校餐厨垃圾详情列表
     *
     * @param request
     * @return
     */
    @RequestMapping(value = "/v1/expSchKwDets", method = RequestMethod.GET)
    public String v1_expSchKwDets(HttpServletRequest request) {
        //初始化响应数据
        String strResp = null;
        ExpSchKwDetsDTO eskdDto = null;
        boolean isAuth = false;
        int code = 0;
        int[] codes = new int[1];
        //授权码
        String token = AppModConfig.GetHeadJsonReq(request, "Authorization");
        //回收开始日期
        String recStartDate = request.getParameter("startSubDate");
        //回收结束日期
        String recEndDate = request.getParameter("endSubDate");
        //项目点名称
        String ppName = request.getParameter("ppName");
        //所属，0:其他，1:部属，2:市属，3: 区属
        String subLevel = request.getParameter("subLevel");
        //主管部门，0:市教委，1:商委，2:教育部
        String compDep = request.getParameter("compDep");
        //总分校标识，0:无，1:总校，2:分校
        String schGenBraFlag = request.getParameter("schGenBraFlag");
        //所属区域名称
        String subDistName = request.getParameter("subDistName");
        //证件主体，0:学校，1:外包
        String fblMb = request.getParameter("fblMb");
        //学校性质，0:公办，1:民办，2:其他
        String schProp = request.getParameter("schProp");
        //区域名称
        String distName = request.getParameter("distName");
        if (distName == null)
            distName = request.getParameter("distname");
        //地级城市
        String prefCity = request.getParameter("prefCity");
        if (prefCity == null)
            prefCity = request.getParameter("prefcity");
        //省或直辖市
        String province = request.getParameter("province");
        //学校类型（学制），0:托儿所，1:托幼园，2:托幼小，3:幼儿园，4:幼小，5:幼小初，6:幼小初高，7:小学，8:初级中学，9:高级中学，10:完全中学，11:九年一贯制学校，12:十二年一贯制学校，13:职业初中，14:中等职业学校，15:工读学校，16:特殊教育学校，17:其他
        String schType = request.getParameter("schType");
        //团餐公司名称
        String rmcName = request.getParameter("rmcName");
        //回收单位
        String recComany = request.getParameter("recComany");
        //回收人
        String recPerson = request.getParameter("recPerson");

        //区域名称 格式：[“1”,”2”……]
        String distNames = request.getParameter("distNames");
        //所属 格式：[“1”,”2”……]
        String subLevels = request.getParameter("subLevels");
        //主管部门 格式：[“1”,”2”……]
        String compDeps = request.getParameter("compDeps");
        //学校性质 格式：[“1”,”2”……]
        String schProps = request.getParameter("schProps");
        //学校类型 格式：[“1”,”2”……]
        String schTypes = request.getParameter("schTypes");

        logger.info("输入参数：" + "token = " + token + ", recStartDate = " + recStartDate + ", recEndDate = " + recEndDate
                + ", ppName = " + ppName + ", subLevel = " + subLevel + ", compDep = " + compDep
                + ", schGenBraFlag = " + schGenBraFlag + ", subDistName = " + subDistName
                + ", fblMb = " + fblMb + ", schProp = " + schProp + ", distName = " + distName
                + ", prefCity = " + prefCity + ", province = " + province + ", schType = " + schType
                + ", rmcName = " + rmcName + ", recComany = " + recComany + ", recPerson = " + recPerson
                + ", distNames = " + distNames + ", subLevels = " + subLevels
                + ", compDeps = " + compDeps + ", schProps = " + schProps
                + ", schTypes = " + schTypes);
        //验证授权
        isAuth = AppModConfig.verifyAuthCode2(token, db2Service, codes);
        //导出学校餐厨垃圾详情列表应用模型函数
        if (isAuth)
            eskdDto = eskdAppMod.appModFunc(token, recStartDate, recEndDate, ppName, distName,
                    prefCity, province, schType, rmcName, recComany, recPerson, schProp,
                    subLevel, compDep,
                    distNames, subLevels, compDeps, schProps, schTypes,
                    db1Service, db2Service, saasService, dbHiveRecyclerWasteService);
        else
            logger.info("授权码：" + token + "，验证失败！");
        //设置响应数据
        if (eskdDto != null) {
            try {
                strResp = objectMapper.writeValueAsString(eskdDto);
                logger.info(strResp);
            } catch (JsonProcessingException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        } else {
            //获取异常响应
            code = codes[0];
            logger.info("错误码：code = " + code);
            IOTHttpRspVO excepResp = AppModConfig.getExcepResp(code);
            try {
                strResp = objectMapper.writeValueAsString(excepResp);
                logger.info(strResp);
            } catch (JsonProcessingException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }

        return strResp;
    }

    /**
     * 2.3.43 - 餐厨垃圾团餐公司回收列表
     *
     * @param request
     * @return
     */
    @RequestMapping(value = "/v1/kwRmcRecs", method = RequestMethod.GET)
    public String v1_kwRmcRecs(HttpServletRequest request) {
        //初始化响应数据
        String strResp = null;
        KwRmcRecsDTO krrDto = null;
        boolean isAuth = false;
        int code = 0;
        int[] codes = new int[1];
        //授权码
        String token = AppModConfig.GetHeadJsonReq(request, "Authorization");
        //回收开始日期
        String recStartDate = request.getParameter("startSubDate");
        //回收结束日期
        String recEndDate = request.getParameter("endSubDate");
        //区域名称
        String distName = request.getParameter("distName");
        if (distName == null)
            distName = request.getParameter("distname");
        //地级城市
        String prefCity = request.getParameter("prefCity");
        if (prefCity == null)
            prefCity = request.getParameter("prefcity");
        //省或直辖市
        String province = request.getParameter("province");
        //页号
        String page = request.getParameter("page");
        if (page == null)
            page = "1";
        //分页大小
        String pageSize = request.getParameter("pageSize");
        if (pageSize == null)
            pageSize = "20";

        //区域名称(多个[1,3])
        String distNames = request.getParameter("distNames");

        logger.info("输入参数：" + "token = " + token + ", recStartDate = " + recStartDate
                + ", recEndDate = " + recEndDate + ", distName = " + distName
                + ", prefCity = " + prefCity + ", province = " + province
                + ", distNames = " + distNames
                + ", page = " + page + ", pageSize = " + pageSize);
        //验证授权
        isAuth = AppModConfig.verifyAuthCode2(token, db2Service, codes);
        //餐厨垃圾团餐公司回收列表应用模型函数
        if (isAuth)
            krrDto = krrAppMod.appModFunc(token, recStartDate, recEndDate, distName, prefCity, province,
                    distNames,
                    page, pageSize, db1Service, db2Service, dbHiveRecyclerWasteService);
        else
            logger.info("授权码：" + token + "，验证失败！");
        //设置响应数据
        if (krrDto != null) {
            try {
                strResp = objectMapper.writeValueAsString(krrDto);
                logger.info(strResp);
            } catch (JsonProcessingException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        } else {
            //获取异常响应
            code = codes[0];
            logger.info("错误码：code = " + code);
            IOTHttpRspVO excepResp = AppModConfig.getExcepResp(code);
            try {
                strResp = objectMapper.writeValueAsString(excepResp);
                logger.info(strResp);
            } catch (JsonProcessingException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }

        return strResp;
    }

    /**
     * 2.3.44 - 导出餐厨垃圾团餐公司回收列表
     *
     * @param request
     * @return
     */
    @RequestMapping(value = "/v1/expKwRmcRecs", method = RequestMethod.GET)
    public String v1_expKwRmcRecs(HttpServletRequest request) {
        //初始化响应数据
        String strResp = null;
        ExpKwRmcRecsDTO ekrrDto = null;
        boolean isAuth = false;
        int code = 0;
        int[] codes = new int[1];
        //授权码
        String token = AppModConfig.GetHeadJsonReq(request, "Authorization");
        //回收开始日期
        String recStartDate = request.getParameter("startSubDate");
        //回收结束日期
        String recEndDate = request.getParameter("endSubDate");
        //区域名称
        String distName = request.getParameter("distName");
        if (distName == null)
            distName = request.getParameter("distname");
        //地级城市
        String prefCity = request.getParameter("prefCity");
        if (prefCity == null)
            prefCity = request.getParameter("prefcity");
        //省或直辖市
        String province = request.getParameter("province");

        //区域名称(多个[1,3])
        String distNames = request.getParameter("distNames");

        logger.info("输入参数：" + "token = " + token + ", recStartDate = " + recStartDate
                + ", recEndDate = " + recEndDate + ", distName = " + distName
                + ", prefCity = " + prefCity + ", province = " + province
                + ", distNames = " + distNames);
        //验证授权
        isAuth = AppModConfig.verifyAuthCode2(token, db2Service, codes);
        //导出餐厨垃圾团餐公司回收列表应用模型函数
        if (isAuth)
            ekrrDto = ekrrAppMod.appModFunc(token, recStartDate, recEndDate, distName, prefCity, province,
                    distNames,
                    db1Service, db2Service, dbHiveRecyclerWasteService);
        else
            logger.info("授权码：" + token + "，验证失败！");
        //设置响应数据
        if (ekrrDto != null) {
            try {
                strResp = objectMapper.writeValueAsString(ekrrDto);
                logger.info(strResp);
            } catch (JsonProcessingException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        } else {
            //获取异常响应
            code = codes[0];
            logger.info("错误码：code = " + code);
            IOTHttpRspVO excepResp = AppModConfig.getExcepResp(code);
            try {
                strResp = objectMapper.writeValueAsString(excepResp);
                logger.info(strResp);
            } catch (JsonProcessingException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }

        return strResp;
    }

    /**
     * 2.3.45 - 团餐公司餐厨垃圾详情列表
     *
     * @param request
     * @return
     */
    @RequestMapping(value = "/v1/rmcKwDets", method = RequestMethod.GET)
    public String v1_rmcKwDets(HttpServletRequest request) {
        //初始化响应数据
        String strResp = null;
        RmcKwDetsDTO rkdDto = null;
        boolean isAuth = false;
        int code = 0;
        int[] codes = new int[1];
        //授权码
        String token = AppModConfig.GetHeadJsonReq(request, "Authorization");
        //回收开始日期
        String recStartDate = request.getParameter("startSubDate");
        //回收结束日期
        String recEndDate = request.getParameter("endSubDate");
        //区域名称
        String distName = request.getParameter("distName");
        if (distName == null)
            distName = request.getParameter("distname");
        //地级城市
        String prefCity = request.getParameter("prefCity");
        if (prefCity == null)
            prefCity = request.getParameter("prefcity");
        //省或直辖市
        String province = request.getParameter("province");
        //项目点名称
        String ppName = request.getParameter("ppName");
        //团餐公司名称
        String rmcName = request.getParameter("rmcName");
        //回收单位
        String recComany = request.getParameter("recComany");
        //回收人
        String recPerson = request.getParameter("recPerson");
        //页号
        String page = request.getParameter("page");
        if (page == null)
            page = "1";
        //分页大小
        String pageSize = request.getParameter("pageSize");
        if (pageSize == null)
            pageSize = "20";

        //区域名称(多个[1,3])
        String distNames = request.getParameter("distNames");

        logger.info("输入参数：" + "token = " + token + ", recStartDate = " + recStartDate
                + ", recEndDate = " + recEndDate + ", distName = " + distName
                + ", prefCity = " + prefCity + ", province = " + province
                + ", ppName = " + ppName + ", rmcName = " + rmcName
                + ", recComany = " + recComany + ", recPerson = " + recPerson
                + ", distNames = " + distNames
                + ", page = " + page + ", pageSize = " + pageSize);
        //验证授权
        isAuth = AppModConfig.verifyAuthCode2(token, db2Service, codes);
        //团餐公司餐厨垃圾详情列表应用模型函数
        if (isAuth)
            rkdDto = rkdAppMod.appModFunc(token, recStartDate, recEndDate, distName,
                    prefCity, province, ppName, rmcName, recComany, recPerson, distNames,
                    page, pageSize, db1Service, db2Service, saasService, dbHiveRecyclerWasteService);
        else
            logger.info("授权码：" + token + "，验证失败！");
        //设置响应数据
        if (rkdDto != null) {
            try {
                strResp = objectMapper.writeValueAsString(rkdDto);
                logger.info(strResp);
            } catch (JsonProcessingException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        } else {
            //获取异常响应
            code = codes[0];
            logger.info("错误码：code = " + code);
            IOTHttpRspVO excepResp = AppModConfig.getExcepResp(code);
            try {
                strResp = objectMapper.writeValueAsString(excepResp);
                logger.info(strResp);
            } catch (JsonProcessingException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }

        return strResp;
    }

    /**
     * 2.3.46 - 导出团餐公司餐厨垃圾详情列表
     *
     * @param request
     * @return
     */
    @RequestMapping(value = "/v1/expRmcKwDets", method = RequestMethod.GET)
    public String v1_expRmcKwDets(HttpServletRequest request) {
        //初始化响应数据
        String strResp = null;
        ExpRmcKwDetsDTO erkdDto = null;
        boolean isAuth = false;
        int code = 0;
        int[] codes = new int[1];
        //授权码
        String token = AppModConfig.GetHeadJsonReq(request, "Authorization");
        //回收开始日期
        String recStartDate = request.getParameter("startSubDate");
        //回收结束日期
        String recEndDate = request.getParameter("endSubDate");
        //区域名称
        String distName = request.getParameter("distName");
        if (distName == null)
            distName = request.getParameter("distname");
        //地级城市
        String prefCity = request.getParameter("prefCity");
        if (prefCity == null)
            prefCity = request.getParameter("prefcity");
        //省或直辖市
        String province = request.getParameter("province");
        //项目点名称
        String ppName = request.getParameter("ppName");
        //团餐公司名称
        String rmcName = request.getParameter("rmcName");
        //回收单位
        String recComany = request.getParameter("recComany");
        //回收人
        String recPerson = request.getParameter("recPerson");

        //区域名称(多个[1,3])
        String distNames = request.getParameter("distNames");

        logger.info("输入参数：" + "token = " + token + ", recStartDate = " + recStartDate
                + ", recEndDate = " + recEndDate + ", distName = " + distName
                + ", prefCity = " + prefCity + ", province = " + province
                + ", ppName = " + ppName + ", rmcName = " + rmcName
                + ", recComany = " + recComany + ", recPerson = " + recPerson
                + ", distNames = " + distNames);
        //验证授权
        isAuth = AppModConfig.verifyAuthCode2(token, db2Service, codes);
        //导出学校餐厨垃圾详情列表应用模型函数
        if (isAuth)
            erkdDto = erkdAppMod.appModFunc(token, recStartDate, recEndDate, distName,
                    prefCity, province, ppName, rmcName, recComany, recPerson, distNames,
                    db1Service, db2Service, saasService, dbHiveRecyclerWasteService);
        else
            logger.info("授权码：" + token + "，验证失败！");
        //设置响应数据
        if (erkdDto != null) {
            try {
                strResp = objectMapper.writeValueAsString(erkdDto);
                logger.info(strResp);
            } catch (JsonProcessingException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        } else {
            //获取异常响应
            code = codes[0];
            logger.info("错误码：code = " + code);
            IOTHttpRspVO excepResp = AppModConfig.getExcepResp(code);
            try {
                strResp = objectMapper.writeValueAsString(excepResp);
                logger.info(strResp);
            } catch (JsonProcessingException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }

        return strResp;
    }

    /**
     * 2.3.47 - 废弃油脂列表
     *
     * @param request
     * @return
     */
    @RequestMapping(value = "/v1/wasteOils", method = RequestMethod.GET)
    public String v1_wasteOils(HttpServletRequest request) {
        //初始化响应数据
        String strResp = null;
        WasteOilsDTO wasDto = null;
        boolean isAuth = false;
        int code = 0;
        int[] codes = new int[1];
        //授权码
        String token = AppModConfig.GetHeadJsonReq(request, "Authorization");
        //回收开始日期
        String recStartDate = request.getParameter("startSubDate");
        //回收结束日期
        String recEndDate = request.getParameter("endSubDate");
        //区域名称
        String distName = request.getParameter("distName");
        if (distName == null)
            distName = request.getParameter("distname");
        //地级城市
        String prefCity = request.getParameter("prefCity");
        if (prefCity == null)
            prefCity = request.getParameter("prefcity");
        //省或直辖市
        String province = request.getParameter("province");
        //页号
        String page = request.getParameter("page");
        if (page == null)
            page = "1";
        //分页大小
        String pageSize = request.getParameter("pageSize");
        if (pageSize == null)
            pageSize = "20";
        logger.info("输入参数：" + "token = " + token + ", recStartDate = " + recStartDate + ", recEndDate = " + recEndDate + ", distName = " + distName + ", prefCity = " + prefCity + ", province = " + province + ", page = " + page + ", pageSize = " + pageSize);
        //验证授权
        isAuth = AppModConfig.verifyAuthCode2(token, db2Service, codes);
        //废弃油脂列表应用模型函数
        if (isAuth)
            wasDto = wasAppMod.appModFunc(recStartDate, recEndDate, distName, prefCity, province, page, pageSize, db1Service);
        else
            logger.info("授权码：" + token + "，验证失败！");
        //设置响应数据
        if (wasDto != null) {
            try {
                strResp = objectMapper.writeValueAsString(wasDto);
                logger.info(strResp);
            } catch (JsonProcessingException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        } else {
            //获取异常响应
            code = codes[0];
            logger.info("错误码：code = " + code);
            IOTHttpRspVO excepResp = AppModConfig.getExcepResp(code);
            try {
                strResp = objectMapper.writeValueAsString(excepResp);
                logger.info(strResp);
            } catch (JsonProcessingException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }

        return strResp;
    }

    /**
     * 废弃油脂详情列表(废弃)
     *
     * @param request
     * @return
     */
    @Deprecated
    @RequestMapping(value = "/v1/wasteOilDets", method = RequestMethod.GET)
    public String v1_wasteOilDets(HttpServletRequest request) {
        //初始化响应数据
        String strResp = null;
        WasteOilDetsDTO wodDto = null;
        boolean isAuth = false;
        int code = 0;
        int[] codes = new int[1];
        //授权码
        String token = AppModConfig.GetHeadJsonReq(request, "Authorization");
        //废弃油脂种类，0:废油，1:含油废水
        String woType = request.getParameter("woType");
        //团餐公司名称
        String rmcName = request.getParameter("rmcName");
        //回收单位
        String recComany = request.getParameter("recComany");
        //回收人
        String recPerson = request.getParameter("recPerson");
        //区域名称
        String distName = request.getParameter("distName");
        if (distName == null) {
            distName = request.getParameter("distname");
        }
        //地级城市
        String prefCity = request.getParameter("prefCity");
        if (prefCity == null) {
            prefCity = request.getParameter("prefcity");
        }
        //省或直辖市
        String province = request.getParameter("province");
        //页号
        String page = request.getParameter("page");
        if (page == null) {
            page = "1";
        }
        //分页大小
        String pageSize = request.getParameter("pageSize");
        if (pageSize == null) {
            pageSize = "20";
        }
        logger.info("输入参数：" + "token = " + token + ", woType = " + woType + ", rmcName = " + rmcName + ", recComany = " + recComany + ", recPerson = " + recPerson + ", distName = " + distName + ", prefCity = " + prefCity + ", province = " + province + ", page = " + page + ", pageSize = " + pageSize);
        //验证授权
        isAuth = AppModConfig.verifyAuthCode2(token, db2Service, codes);
        //废弃油脂详情列表应用模型函数
        if (isAuth)
            wodDto = wodAppMod.appModFunc(woType, rmcName, recComany, recPerson, distName, prefCity, province, page, pageSize, db1Service);
        else
            logger.info("授权码：" + token + "，验证失败！");
        //设置响应数据
        if (wodDto != null) {
            try {
                strResp = objectMapper.writeValueAsString(wodDto);
                logger.info(strResp);
            } catch (JsonProcessingException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        } else {
            //获取异常响应
            code = codes[0];
            logger.info("错误码：code = " + code);
            IOTHttpRspVO excepResp = AppModConfig.getExcepResp(code);
            try {
                strResp = objectMapper.writeValueAsString(excepResp);
                logger.info(strResp);
            } catch (JsonProcessingException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }

        return strResp;
    }

    /**
     * 2.3.47 - 废弃油脂种类编码列表
     *
     * @param searchDTO
     * @return
     */
    @AccessLog
    @RequestMapping(value = "/v1/wasteOilTypeCodes", method = RequestMethod.GET)
    public ApiResult<?> v1_wasteOilTypeCodes(@Validated WasteOilTypeCodeSearchDTO searchDTO) {

        String distCode = UserContextHandler.getDistrictCode();
        if (StringUtil.isNotNull(distCode) && StringUtil.isNumber(distCode)) {
            searchDTO.setDistName(distCode);
        }

        return ApiResultUtil.success(recoveryWasteOilsService.getWasteOilTypeCodes(searchDTO));
    }

    /**
     * 2.3.48 - 学校回收废弃油脂列表
     *
     * @param searchDTO
     * @return
     */
    @AccessLog
    @RequestMapping(value = "/v1/schRecWasteOils", method = RequestMethod.GET)
    public ApiResult<?> v1_schRecWasteOils(@Validated RecoveryWasteOilSummarySearchDTO searchDTO) {
        //获取区/县码
        String distCode = UserContextHandler.getDistrictCode();
        if (StringUtil.isNotNull(distCode) && StringUtil.isNumber(distCode)) {
            searchDTO.setDistName(distCode);
        }
        //hive数据库服务
        ApiResult<?> art = ApiResultUtil.success(recoveryWasteOilsService.getSchoolRecoveryWasteOilSummary(searchDTO, dbHiveRecyclerWasteService));
        //设置响应数据
        String strResp = null;
        if (art != null) {
            try {
                strResp = objectMapper.writeValueAsString(art);
                logger.info(strResp);
            } catch (JsonProcessingException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        } else {
            //获取异常响应
            logger.info("无响应数据！");
        }

        return art;
    }

    /**
     * 2.3.49 - 导出学校回收废弃油脂列表
     *
     * @param searchDTO
     * @return
     */
    @RequestMapping(value = "/v1/expSchRecWasteOils", method = RequestMethod.GET)
    public ApiResult<?> v1_expSchRecWasteOils(@Validated RecoveryWasteOilSummarySearchDTO searchDTO) {
        String distCode = UserContextHandler.getDistrictCode();
        if (StringUtil.isNotNull(distCode) && StringUtil.isNumber(distCode)) {
            searchDTO.setDistName(distCode);
        }

        ApiResult<?> art = ApiResultUtil.success(recoveryWasteOilsService.exportSchoolRecoveryWasteOilSummary(searchDTO, dbHiveRecyclerWasteService));

        //设置响应数据
        String strResp = null;
        if (art != null) {
            try {
                strResp = objectMapper.writeValueAsString(art);
                logger.info(strResp);
            } catch (JsonProcessingException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        } else {
            //获取异常响应
            logger.info("无响应数据！");
        }

        return art;
    }

    /**
     * 2.3.50 -	学校回收废弃油脂详情列表
     *
     * @param searchDTO
     * @return
     */
    @AccessLog
    @RequestMapping(value = "/v1/schWasteOilDets", method = RequestMethod.GET)
    public ApiResult<?> v1_schWasteOilDets(@Validated SchoolRecoveryWasteOilSearchDTO searchDTO) {

        String distCode = UserContextHandler.getDistrictCode();
        if (StringUtil.isNotNull(distCode) && StringUtil.isNumber(distCode)) {
            searchDTO.setDistName(Integer.valueOf(distCode));
        }

        ApiResult<?> art = ApiResultUtil.success(recoveryWasteOilsService.getSchoolRecoveryWasteOilDetails(searchDTO, db1Service, saasService, dbHiveRecyclerWasteService));
        //设置响应数据
        String strResp = null;
        if (art != null) {
            try {
                strResp = objectMapper.writeValueAsString(art);
                logger.info(strResp);
            } catch (JsonProcessingException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        } else {
            //获取异常响应
            logger.info("无响应数据！");
        }

        return art;
    }

    /**
     * 3.2.41.	项目点配货计划操作列表
     *
     * @param request
     * @return
     */
    @RequestMapping(value = "/v1/ppGsPlanOpts", method = RequestMethod.GET)
    public String v1_dishPpGsPlanOpts(HttpServletRequest request, @RequestParam Map<String, String> paramMap) {
        //初始化响应数据
        String strResp = null;
        PpGsPlanOptsDTO drsDto = null;
        boolean isAuth = false;
        int code = 0;
        int[] codes = new int[1];
        //授权码
        String token = AppModConfig.GetHeadJsonReq(request, "Authorization");
        //开始日期，默认为当天日期，格式：xxxx-xx-xx
        String startDate = request.getParameter("startSubDate");
        //结束日期，默认为当天日期，格式：xxxx-xx-xx
        String endDate = request.getParameter("endSubDate");
        //项目点名称
        String ppName = request.getParameter("ppName");

        //验收状态，0:待验收，1:已验收
        Integer acceptStatus = null;
        if (request.getParameter("acceptStatus") != null && !"".equals(request.getParameter("acceptStatus")) &&
                StringUtils.isNumeric(request.getParameter("acceptStatus"))) {
            acceptStatus = Integer.parseInt(request.getParameter("acceptStatus"));
        }
        //指派状态，0:未指派，1：已指派，2:已取消
        Integer assignStatus = null;
        if (request.getParameter("assignStatus") != null && !"".equals(request.getParameter("assignStatus")) &&
                StringUtils.isNumeric(request.getParameter("assignStatus"))) {
            assignStatus = Integer.parseInt(request.getParameter("assignStatus"));
        }

        //学校类型（学制），0:托儿所，1:托幼园，2:托幼小，3:幼儿园，
        // 4:幼小，5:幼小初，6:幼小初高，7:小学，8:初级中学，9:高级中学，
        // 10:完全中学，11:九年一贯制学校，12:十二年一贯制学校，13:职业初中，14:中等职业学校，
        // 15:工读学校，16:特殊教育学校，17:其他
        Integer schType = null;
        if (request.getParameter("schType") != null && !"".equals(request.getParameter("schType")) &&
                StringUtils.isNumeric(request.getParameter("schType"))) {
            schType = Integer.parseInt(request.getParameter("schType"));
        }

        //配送状态，0:未派送，1:配送中，2: 已配送
        Integer dispStatus = null;
        if (request.getParameter("dispStatus") != null && !"".equals(request.getParameter("dispStatus")) &&
                StringUtils.isNumeric(request.getParameter("dispStatus"))) {
            dispStatus = Integer.parseInt(request.getParameter("dispStatus"));
        }

        //区域名称
        String distName = request.getParameter("distName");
        if (distName == null) {
            distName = request.getParameter("distname");
        }
        //地级城市
        String prefCity = request.getParameter("prefCity");
        if (prefCity == null) {
            prefCity = request.getParameter("prefcity");
        }
        //省或直辖市
        String province = request.getParameter("province");
        //页号
        String page = request.getParameter("page");
        if (page == null) {
            page = "1";
        }
        //分页大小
        String pageSize = request.getParameter("pageSize");
        if (pageSize == null) {
            pageSize = "20";
        }

        //区域名称(多个[“1”,”2”……])
        String distNames = request.getParameter("distNames");
        //学校类型（学制[“1”,”2”……]）
        String schTypes = request.getParameter("schTypes");

        String departmentId = request.getParameter("departmentId");
        String departmentIds = request.getParameter("departmentIds");

        String plastatus = request.getParameter("plastatus");

        //所属，0:其他，1:部属，2:市属，3: 区属
        String subLevel = request.getParameter("subLevel");
        //主管部门，0:市教委，1:商委，2:教育部
        String compDep = request.getParameter("compDep");
        //所属 格式：[“1”,”2”……]
        String subLevels = request.getParameter("subLevels");
        //主管部门 格式：[“1”,”2”……]
        String compDeps = request.getParameter("compDeps");


        logger.info("输入参数：" + "token = " + token + ", startDate = " + startDate + ", endDate = " + endDate + ", ppName = "
                + ppName + ", acceptStatus = " + acceptStatus + ", assignStatus = " + assignStatus + ", dispStatus = " + dispStatus + ", schType = " + schType
                + ", distName = " + distName
                + ", prefCity = " + prefCity + ", province = " + province + ", schTypes = " + schTypes + ", distNames = " + distNames
                + ", page = " + page + ", pageSize = " + pageSize);
        //验证授权
        isAuth = AppModConfig.verifyAuthCode2(token, db2Service, codes);
        //菜品留样列表应用模型函数
        if (isAuth)
            drsDto = ppGsPlanOptsAppMod.appModFunc(token, startDate, endDate, ppName, acceptStatus, assignStatus, dispStatus,
                    schType, distName, prefCity, province, distNames, schTypes,
                    departmentIds, plastatus,
                    subLevel, compDep, subLevels, compDeps,
                    page, pageSize, db1Service, eduSchoolService, db2Service, dbHiveDishService);
        else
            logger.info("授权码：" + token + "，验证失败！");
        //设置响应数据
        if (drsDto != null) {
            try {
                strResp = objectMapper.writeValueAsString(drsDto);
                logger.info(strResp);
            } catch (JsonProcessingException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        } else {
            //获取异常响应
            code = codes[0];
            logger.info("错误码：code = " + code);
            IOTHttpRspVO excepResp = AppModConfig.getExcepResp(code);
            try {
                strResp = objectMapper.writeValueAsString(excepResp);
                logger.info(strResp);
            } catch (JsonProcessingException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }

        return strResp;
    }

    /**
     * 3.2.41.	项目点配货计划操作列表
     *
     * @param request
     * @return
     */
    @RequestMapping(value = "/v1/ppGsPlanOptsTwo", method = RequestMethod.GET)
    public PpGsPlanOptsDTO v1_dishPpGsPlanOptsTwo(HttpServletRequest request, @RequestParam Map<String, String> paramMap) {
        //初始化响应数据
        String strResp = null;
        PpGsPlanOptsDTO drsDto = null;
        boolean isAuth = false;
        int code = 0;
        int[] codes = new int[1];
        //授权码
        String token = AppModConfig.GetHeadJsonReq(request, "Authorization");
        //开始日期，默认为当天日期，格式：xxxx-xx-xx
        String startDate = request.getParameter("startSubDate");
        //结束日期，默认为当天日期，格式：xxxx-xx-xx
        String endDate = request.getParameter("endSubDate");
        //项目点名称
        String ppName = request.getParameter("ppName");

        //验收状态，0:待验收，1:已验收
        Integer acceptStatus = null;
        if (request.getParameter("acceptStatus") != null && !"".equals(request.getParameter("acceptStatus")) &&
                StringUtils.isNumeric(request.getParameter("acceptStatus"))) {
            acceptStatus = Integer.parseInt(request.getParameter("acceptStatus"));
        }
        //指派状态，0:未指派，1：已指派，2:已取消
        Integer assignStatus = null;
        if (request.getParameter("assignStatus") != null && !"".equals(request.getParameter("assignStatus")) &&
                StringUtils.isNumeric(request.getParameter("assignStatus"))) {
            assignStatus = Integer.parseInt(request.getParameter("assignStatus"));
        }

        //学校类型（学制），0:托儿所，1:托幼园，2:托幼小，3:幼儿园，4:幼小，5:幼小初，6:幼小初高，7:小学，8:初级中学，9:高级中学，10:完全中学，11:九年一贯制学校，12:十二年一贯制学校，13:职业初中，14:中等职业学校，15:工读学校，16:特殊教育学校，17:其他
        Integer schType = null;
        if (request.getParameter("schType") != null && !"".equals(request.getParameter("schType")) &&
                StringUtils.isNumeric(request.getParameter("schType"))) {
            schType = Integer.parseInt(request.getParameter("schType"));
        }

        //配送状态，0:未派送，1:配送中，2: 已配送
        Integer dispStatus = null;
        if (request.getParameter("dispStatus") != null && !"".equals(request.getParameter("dispStatus")) &&
                StringUtils.isNumeric(request.getParameter("dispStatus"))) {
            dispStatus = Integer.parseInt(request.getParameter("dispStatus"));
        }

        //区域名称
        String distName = request.getParameter("distName");
        if (distName == null) {
            distName = request.getParameter("distname");
        }
        //地级城市
        String prefCity = request.getParameter("prefCity");
        if (prefCity == null) {
            prefCity = request.getParameter("prefcity");
        }
        //省或直辖市
        String province = request.getParameter("province");
        //页号
        String page = request.getParameter("page");
        if (page == null) {
            page = "1";
        }
        //分页大小
        String pageSize = request.getParameter("pageSize");
        if (pageSize == null) {
            pageSize = "20";
        }

        //区域名称(多个[“1”,”2”……])
        String distNames = request.getParameter("distNames");
        //学校类型（学制[“1”,”2”……]）
        String schTypes = request.getParameter("schTypes");

        String departmentIds = request.getParameter("departmentIds");
        String plastatus = request.getParameter("plastatus");

        //所属，0:其他，1:部属，2:市属，3: 区属
        String subLevel = request.getParameter("subLevel");
        //主管部门，0:市教委，1:商委，2:教育部
        String compDep = request.getParameter("compDep");

        //所属 格式：[“1”,”2”……]
        String subLevels = request.getParameter("subLevels");
        //主管部门 格式：[“1”,”2”……]
        String compDeps = request.getParameter("compDeps");


        logger.info("输入参数：" + "token = " + token + ", startDate = " + startDate + ", endDate = " + endDate + ", ppName = "
                + ppName + ", acceptStatus = " + acceptStatus + ", assignStatus = " + assignStatus + ", dispStatus = " + dispStatus + ", schType = " + schType
                + ", distName = " + distName + ", distNames = " + distNames + ", schTypes = " + schTypes
                + ", prefCity = " + prefCity + ", province = " + province + ", page = " + page + ", pageSize = " + pageSize);
        //验证授权
        isAuth = AppModConfig.verifyAuthCode2(token, db2Service, codes);
        //菜品留样列表应用模型函数
        if (isAuth)
            drsDto = ppGsPlanOptsAppMod.appModFunc(token, startDate, endDate, ppName, acceptStatus, assignStatus, dispStatus,
                    schType, distName, prefCity, province, distNames, schTypes,
                    departmentIds, plastatus,
                    subLevel, compDep, subLevels, compDeps,
                    page, pageSize, db1Service, eduSchoolService, db2Service, dbHiveDishService);
        else
            logger.info("授权码：" + token + "，验证失败！");
        //设置响应数据
       /* if (drsDto != null) {
            try {
                strResp = objectMapper.writeValueAsString(drsDto);
                logger.info(strResp);
            } catch (JsonProcessingException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        } else {
            //获取异常响应
            code = codes[0];
            logger.info("错误码：code = " + code);
            IOTHttpRspVO excepResp = AppModConfig.getExcepResp(code);
            try {
                strResp = objectMapper.writeValueAsString(excepResp);
                logger.info(strResp);
            } catch (JsonProcessingException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }*/

        return drsDto;
    }

    /*
     * 3.2.42.	导出项目点配货计划操作列表
     *
     * @param request
     * @param response
     * @return
     */
    @RequestMapping(value = "/v1/expPpGsPlanOpts", method = RequestMethod.GET)
    public String v1_expPpGsPlanOpts(HttpServletRequest request, HttpServletResponse response) {
        //初始化响应数据
        String strResp = null;
        ExpPpGsPlanOptsDTO expPpGsPlanOptsDTO = null;
        boolean isAuth = false;
        int code = 0;
        int[] codes = new int[1];
        //授权码
        String token = AppModConfig.GetHeadJsonReq(request, "Authorization");
        //开始日期
        String startDate = request.getParameter("startSubDate");
        //结束日期
        String endDate = request.getParameter("endSubDate");

        //项目点名称
        String ppName = request.getParameter("ppName");

        //验收状态，0:待验收，1:已验收
        Integer acceptStatus = null;
        if (request.getParameter("acceptStatus") != null && !"".equals(request.getParameter("acceptStatus")) &&
                StringUtils.isNumeric(request.getParameter("acceptStatus"))) {
            acceptStatus = Integer.parseInt(request.getParameter("acceptStatus"));
        }
        //指派状态，0:未指派，1：已指派，2:已取消
        Integer assignStatus = null;
        if (request.getParameter("assignStatus") != null && !"".equals(request.getParameter("assignStatus")) &&
                StringUtils.isNumeric(request.getParameter("assignStatus"))) {
            assignStatus = Integer.parseInt(request.getParameter("assignStatus"));
        }

        //学校类型（学制），0:托儿所，1:托幼园，2:托幼小，3:幼儿园，4:幼小，5:幼小初，6:幼小初高，7:小学，8:初级中学，9:高级中学，10:完全中学，
        //11:九年一贯制学校，12:十二年一贯制学校，13:职业初中，14:中等职业学校，15:工读学校，16:特殊教育学校，17:其他
        Integer schType = null;
        if (request.getParameter("schType") != null && !"".equals(request.getParameter("schType")) &&
                StringUtils.isNumeric(request.getParameter("schType"))) {
            schType = Integer.parseInt(request.getParameter("schType"));
        }

        //配送状态，0:未派送，1:配送中，2: 已配送
        Integer dispStatus = null;
        if (request.getParameter("dispStatus") != null && !"".equals(request.getParameter("dispStatus")) &&
                StringUtils.isNumeric(request.getParameter("dispStatus"))) {
            dispStatus = Integer.parseInt(request.getParameter("dispStatus"));
        }

        //区域名称
        String distName = request.getParameter("distName");
        if (distName == null) {
            distName = request.getParameter("distname");
        }
        //地级城市
        String prefCity = request.getParameter("prefCity");
        if (prefCity == null) {
            prefCity = request.getParameter("prefcity");
        }

        //区域名称(多个[“1”,”2”……])
        String distNames = request.getParameter("distNames");
        //学校类型（学制[“1”,”2”……]）
        String schTypes = request.getParameter("schTypes");

        String departmentIds = request.getParameter("departmentIds");

        String plastatus = request.getParameter("plastatus");

        //所属，0:其他，1:部属，2:市属，3: 区属
        String subLevel = request.getParameter("subLevel");
        //主管部门，0:市教委，1:商委，2:教育部
        String compDep = request.getParameter("compDep");

        //所属 格式：[“1”,”2”……]
        String subLevels = request.getParameter("subLevels");
        //主管部门 格式：[“1”,”2”……]
        String compDeps = request.getParameter("compDeps");

        //省或直辖市
        String province = request.getParameter("province");
        logger.info("输入参数：" + "token = " + token + ", startDate = " + startDate + ", endDate = " + endDate
                + ", ppName = " + ppName + ", acceptStatus = " + acceptStatus + ", assignStatus = " + assignStatus
                + ", dispStatus = " + dispStatus + ", schType = " + schType + ", distName = " + distName
                + ", prefCity = " + prefCity + ", province = " + province
                + ", distNames = " + distNames + ", schTypes = " + schTypes);
        //验证授权
        isAuth = AppModConfig.verifyAuthCode2(token, db2Service, codes);
        //导出项目点排菜列表应用模型函数
        if (isAuth)
            expPpGsPlanOptsDTO = expPpGsPlanOptsAppMod.appModFunc(token, startDate, endDate, ppName, acceptStatus, assignStatus,
                    dispStatus, schType, distName, prefCity, province, distNames, schTypes,
                    departmentIds, plastatus,
                    subLevel, compDep, subLevels, compDeps,
                    db1Service, db2Service, dbHiveDishService);
        else
            logger.info("授权码：" + token + "，验证失败！");
        //设置响应数据
        if (expPpGsPlanOptsDTO != null) {
            try {
                strResp = objectMapper.writeValueAsString(expPpGsPlanOptsDTO);
                logger.info(strResp);
            } catch (JsonProcessingException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        } else {
            //获取异常响应
            code = codes[0];
            logger.info("错误码：code = " + code);
            IOTHttpRspVO excepResp = AppModConfig.getExcepResp(code);
            try {
                strResp = objectMapper.writeValueAsString(excepResp);
                logger.info(strResp);
            } catch (JsonProcessingException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }

        return strResp;
    }

    /*
     * 3.2.54.	导出项目点留样列表
     *
     * @param request
     * @param response
     * @return
     */
    @RequestMapping(value = "/v1/expPpRetSamples", method = RequestMethod.GET)
    public String v1_expPpRetSamples(HttpServletRequest request, HttpServletResponse response) {
        //初始化响应数据
        String strResp = null;
        ExpPpRetSamplesDTO expPpRetSamplesDTO = null;
        boolean isAuth = false;
        int code = 0;
        int[] codes = new int[1];
        //授权码
        String token = AppModConfig.GetHeadJsonReq(request, "Authorization");
        //开始日期
        String startDate = request.getParameter("startSubDate");
        //结束日期
        String endDate = request.getParameter("endSubDate");

        //项目点名称
        String ppName = request.getParameter("ppName");

        //是否留样标识，0:未留样，1:已留样
        Integer rsFlag = null;
        if (request.getParameter("rsFlag") != null && !"".equals(request.getParameter("rsFlag")) &&
                StringUtils.isNumeric(request.getParameter("rsFlag"))) {
            rsFlag = Integer.parseInt(request.getParameter("rsFlag"));
        }

        //学校类型（学制），0:托儿所，1:托幼园，2:托幼小，3:幼儿园，4:幼小，5:幼小初，6:幼小初高，7:小学，8:初级中学，9:高级中学，10:完全中学，
        //11:九年一贯制学校，12:十二年一贯制学校，13:职业初中，14:中等职业学校，15:工读学校，16:特殊教育学校，17:其他
        Integer schType = null;
        if (request.getParameter("schType") != null && !"".equals(request.getParameter("schType")) &&
                StringUtils.isNumeric(request.getParameter("schType"))) {
            schType = Integer.parseInt(request.getParameter("schType"));
        }


        //区域名称
        String distName = request.getParameter("distName");
        if (distName == null) {
            distName = request.getParameter("distname");
        }
        //地级城市
        String prefCity = request.getParameter("prefCity");
        if (prefCity == null) {
            prefCity = request.getParameter("prefcity");
        }
        //省或直辖市
        String province = request.getParameter("province");

        //区域名称(多个[“1”,”2”……])
        String distNames = request.getParameter("distNames");
        //学校类型（学制[“1”,”2”……]）
        String schTypes = request.getParameter("schTypes");

        String departmentIds = request.getParameter("departmentIds");
        String reserveStatus = request.getParameter("reserveStatus");

        //所属，0:其他，1:部属，2:市属，3: 区属
        String subLevel = request.getParameter("subLevel");
        //主管部门，0:市教委，1:商委，2:教育部
        String compDep = request.getParameter("compDep");

        //所属 格式：[“1”,”2”……]
        String subLevels = request.getParameter("subLevels");
        //主管部门 格式：[“1”,”2”……]
        String compDeps = request.getParameter("compDeps");

        logger.info("输入参数：" + "token = " + token + ", startDate = " + startDate + ", endDate = " + endDate
                + ", ppName = " + ppName + ", rsFlag = " + rsFlag + ", schType = " + schType
                + ", distName = " + distName
                + ", prefCity = " + prefCity + ", province = " + province
                + ", distNames = " + distNames + ", schTypes = " + schTypes);
        //验证授权
        isAuth = AppModConfig.verifyAuthCode2(token, db2Service, codes);
        //导出项目点排菜列表应用模型函数
        if (isAuth)
            expPpRetSamplesDTO = expPpRetSamplesAppMod.appModFunc(token, startDate, endDate, ppName, rsFlag,
                    schType, distName, prefCity, province, distNames, schTypes, departmentIds, reserveStatus,
                    subLevel, compDep, subLevels, compDeps,
                    db1Service, db2Service);
        else
            logger.info("授权码：" + token + "，验证失败！");
        //设置响应数据
        if (expPpRetSamplesDTO != null) {
            try {
                strResp = objectMapper.writeValueAsString(expPpRetSamplesDTO);
                logger.info(strResp);
            } catch (JsonProcessingException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        } else {
            //获取异常响应
            code = codes[0];
            logger.info("错误码：code = " + code);
            IOTHttpRspVO excepResp = AppModConfig.getExcepResp(code);
            try {
                strResp = objectMapper.writeValueAsString(excepResp);
                logger.info(strResp);
            } catch (JsonProcessingException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }

        return strResp;
    }

    /**
     * 3.2.51 -	导出学校回收废弃油脂详情列表
     *
     * @param searchDTO
     * @return
     */
    @RequestMapping(value = "/v1/expSchWasteOilDets", method = RequestMethod.GET)
    public ApiResult<?> v1_expSchWasteOilDets(HttpServletRequest request, @Validated SchoolRecoveryWasteOilSearchDTO searchDTO) {

        String distCode = UserContextHandler.getDistrictCode();
        if (StringUtil.isNotNull(distCode) && StringUtil.isNumber(distCode)) {
            searchDTO.setDistName(Integer.valueOf(distCode));
        }
        String token = AppModConfig.GetHeadJsonReq(request, "Authorization");
        ApiResult<?> art = ApiResultUtil.success(recoveryWasteOilsService.exportSchoolRecoveryWasteOilDetails(token, searchDTO, db1Service, saasService, db2Service, dbHiveRecyclerWasteService));
        //设置响应数据
        String strResp = null;
        if (art != null) {
            try {
                strResp = objectMapper.writeValueAsString(art);
                logger.info(strResp);
            } catch (JsonProcessingException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        } else {
            //获取异常响应
            logger.info("无响应数据！");
        }

        return art;
    }

    /**
     * 3.2.52 -	团餐公司回收废弃油脂列表
     *
     * @param searchDTO
     * @return
     */
    @AccessLog
    @RequestMapping(value = "/v1/rmcRecWasteOils", method = RequestMethod.GET)
    public ApiResult<?> v1_rmcRecWasteOils(@Validated RecoveryWasteOilSummarySearchDTO searchDTO) {

        String distCode = UserContextHandler.getDistrictCode();
        if (StringUtil.isNotNull(distCode) && StringUtil.isNumber(distCode)) {
            searchDTO.setDistName(distCode);
        }

        ApiResult<?> art = ApiResultUtil.success(recoveryWasteOilsService.getRmcRecoveryWasteOilSummary(searchDTO, dbHiveRecyclerWasteService));
        //设置响应数据
        String strResp = null;
        if (art != null) {
            try {
                strResp = objectMapper.writeValueAsString(art);
                logger.info(strResp);
            } catch (JsonProcessingException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        } else {
            //获取异常响应
            logger.info("无响应数据！");
        }

        return art;
    }

    /**
     * 3.2.53 -	导出团餐公司回收废弃油脂列表
     *
     * @param searchDTO
     * @return
     */
    @RequestMapping(value = "/v1/expRmcRecWasteOils", method = RequestMethod.GET)
    public ApiResult<?> v1_expRmcRecWasteOils(@Validated RecoveryWasteOilSummarySearchDTO searchDTO) {

        String distCode = UserContextHandler.getDistrictCode();
        if (StringUtil.isNotNull(distCode) && StringUtil.isNumber(distCode)) {
            searchDTO.setDistName(distCode);
        }

        ApiResult<?> art = ApiResultUtil.success(recoveryWasteOilsService.exportRmcRecoveryWasteOilSummary(searchDTO, dbHiveRecyclerWasteService));
        //设置响应数据
        String strResp = null;
        if (art != null) {
            try {
                strResp = objectMapper.writeValueAsString(art);
                logger.info(strResp);
            } catch (JsonProcessingException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        } else {
            //获取异常响应
            logger.info("无响应数据！");
        }

        return art;
    }

    /**
     * 3.2.53.	项目点留样列表
     *
     * @param request
     * @return
     */
    @RequestMapping(value = "/v1/ppRetSamples", method = RequestMethod.GET)
    public String v1_PpRetSamples(HttpServletRequest request) {

        //初始化响应数据
        String strResp = null;
        PpRetSamplesDTO drsDto = null;
        boolean isAuth = false;
        int code = 0;
        int[] codes = new int[1];
        //授权码
        String token = AppModConfig.GetHeadJsonReq(request, "Authorization");
        //开始日期，默认为当天日期，格式：xxxx-xx-xx
        String startDate = request.getParameter("startSubDate");
        //结束日期，默认为当天日期，格式：xxxx-xx-xx
        String endDate = request.getParameter("endSubDate");
        //项目点名称
        String ppName = request.getParameter("ppName");

        //是否留样标识，0:未留样，1:已留样
        Integer rsFlag = null;
        if (request.getParameter("rsFlag") != null && !"".equals(request.getParameter("rsFlag")) &&
                StringUtils.isNumeric(request.getParameter("rsFlag"))) {
            rsFlag = Integer.parseInt(request.getParameter("rsFlag"));
        }

        //学校类型（学制），0:托儿所，1:托幼园，2:托幼小，3:幼儿园，4:幼小，5:幼小初，6:幼小初高，7:小学，8:初级中学，9:高级中学，10:完全中学，11:九年一贯制学校，12:十二年一贯制学校，13:职业初中，14:中等职业学校，15:工读学校，16:特殊教育学校，17:其他
        Integer schType = null;
        if (request.getParameter("schType") != null && !"".equals(request.getParameter("schType")) &&
                StringUtils.isNumeric(request.getParameter("schType"))) {
            schType = Integer.parseInt(request.getParameter("schType"));
        }

        //区域名称
        String distName = request.getParameter("distName");
        if (distName == null) {
            distName = request.getParameter("distname");
        }
        //地级城市
        String prefCity = request.getParameter("prefCity");
        if (prefCity == null) {
            prefCity = request.getParameter("prefcity");
        }
        //省或直辖市
        String province = request.getParameter("province");
        //页号
        String page = request.getParameter("page");
        if (page == null) {
            page = "1";
        }
        //分页大小
        String pageSize = request.getParameter("pageSize");
        if (pageSize == null) {
            pageSize = "20";
        }

        //区域名称(多个[“1”,”2”……])
        String distNames = request.getParameter("distNames");
        //学校类型（学制[“1”,”2”……]）
        String schTypes = request.getParameter("schTypes");

        String departmentIds = request.getParameter("departmentIds");
        String reserveStatus = request.getParameter("reserveStatus");

        //所属，0:其他，1:部属，2:市属，3: 区属
        String subLevel = request.getParameter("subLevel");
        //主管部门，0:市教委，1:商委，2:教育部
        String compDep = request.getParameter("compDep");

        //所属 格式：[“1”,”2”……]
        String subLevels = request.getParameter("subLevels");
        //主管部门 格式：[“1”,”2”……]
        String compDeps = request.getParameter("compDeps");


        logger.info("输入参数：" + "token = " + token + ", startDate = " + startDate + ", endDate = " + endDate + ", ppName = "
                + ppName + ", rsFlag = " + rsFlag + ", schType = " + schType
                + ", distName = " + distName + ", schTypes = " + schTypes + ", distNames = " + distNames
                + ", prefCity = " + prefCity + ", province = " + province + ", page = " + page + ", pageSize = " + pageSize);
        //验证授权
        isAuth = AppModConfig.verifyAuthCode2(token, db2Service, codes);
        //菜品留样列表应用模型函数
        if (isAuth)
            drsDto = ppRetSamplesAppMod.appModFunc(token, startDate, endDate, ppName, rsFlag,
                    schType, distName, prefCity, province, distNames, schTypes, departmentIds, reserveStatus,
                    subLevel, compDep, subLevels, compDeps,
                    page, pageSize, db1Service, eduSchoolService, db2Service);
        else
            logger.info("授权码：" + token + "，验证失败！");
        //设置响应数据
        if (drsDto != null) {
            try {
                strResp = objectMapper.writeValueAsString(drsDto);
                logger.info(strResp);
            } catch (JsonProcessingException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        } else {
            //获取异常响应
            code = codes[0];
            logger.info("错误码：code = " + code);
            IOTHttpRspVO excepResp = AppModConfig.getExcepResp(code);
            try {
                strResp = objectMapper.writeValueAsString(excepResp);
                logger.info(strResp);
            } catch (JsonProcessingException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }

        return strResp;
    }

    /**
     * 3.2.54 -	团餐公司回收废弃油脂详情列表
     *
     * @param searchDTO
     * @return
     */
    @AccessLog
    @RequestMapping(value = "/v1/rmcWasteOilDets", method = RequestMethod.GET)
    public ApiResult<?> v1_rmcWasteOilDets(@Validated RmcRecoveryWasteOilSearchDTO searchDTO) {

        String distCode = UserContextHandler.getDistrictCode();
        if (StringUtil.isNotNull(distCode) && StringUtil.isNumber(distCode)) {
            searchDTO.setDistName(Integer.valueOf(distCode));
        }

        ApiResult<?> art = ApiResultUtil.success(recoveryWasteOilsService.getRmcRecoveryWasteOilDetails(searchDTO, saasService, dbHiveRecyclerWasteService));
        //设置响应数据
        String strResp = null;
        if (art != null) {
            try {
                strResp = objectMapper.writeValueAsString(art);
                logger.info(strResp);
            } catch (JsonProcessingException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        } else {
            //获取异常响应
            logger.info("无响应数据！");
        }

        return art;
    }

    /**
     * 3.2.55 -	导出团餐公司回收废弃油脂详情列表
     *
     * @param searchDTO
     * @return
     */
    @RequestMapping(value = "/v1/expRmcWasteOilDets", method = RequestMethod.GET)
    public ApiResult<?> v1_expRmcWasteOilDets(HttpServletRequest request, @Validated RmcRecoveryWasteOilSearchDTO searchDTO) {

        String distCode = UserContextHandler.getDistrictCode();
        if (StringUtil.isNotNull(distCode) && StringUtil.isNumber(distCode)) {
            searchDTO.setDistName(Integer.valueOf(distCode));
        }
        String token = AppModConfig.GetHeadJsonReq(request, "Authorization");

        ApiResult<?> art = ApiResultUtil.success(recoveryWasteOilsService.exportRmcRecoveryWasteOilDetails(token, searchDTO, saasService, db2Service, dbHiveRecyclerWasteService));
        //设置响应数据
        String strResp = null;
        if (art != null) {
            try {
                strResp = objectMapper.writeValueAsString(art);
                logger.info(strResp);
            } catch (JsonProcessingException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        } else {
            //获取异常响应
            logger.info("无响应数据！");
        }

        return art;
    }

    //----------------------周报表------------------------------------------------------------------------

    /**
     * 排菜周报表
     *
     * @param request
     * @return
     */
    @RequestMapping(value = "/v1/ppDishWeek", method = RequestMethod.GET)
    public PpDishWeekOutDto ppDishWeek(HttpServletRequest request, @Validated AppTEduPlatoonTotalWObj inputParm) {
        logger.info("输入参数：" + inputParm.toString());
        String token = AppModConfig.GetHeadJsonReq(request, "Authorization");
        return ppDishWeekAppMod.appModFunc(token, inputParm, db1Service, db2Service, dbHiveDishService);
    }

    /**
     * 排菜周报表导出
     *
     * @param request
     * @return
     */
    @RequestMapping(value = "/v1/expPpDishWeek", method = RequestMethod.GET)
    public ApiResponse<ExpCommonDTO> expPpDishWeek(HttpServletRequest request, @Validated AppTEduPlatoonTotalWObj inputParm) {
        logger.info("输入参数：" + inputParm.toString());
        String token = AppModConfig.GetHeadJsonReq(request, "Authorization");
        return expPpDishWeekAppMod.appModFunc(token, inputParm, db1Service, db2Service, saasService, dbHiveDishService);
    }

    /**
     * 用料周报表
     *
     * @param request
     * @return
     */
    @RequestMapping(value = "/v1/ppMatConfirmWeek", method = RequestMethod.GET)
    public PpMatConfirmWeekOutDto ppMatConfirmWeek(HttpServletRequest request, @Validated AppTEduMaterialTotalWObj inputParm) {
        logger.info("输入参数：" + inputParm.toString());
        String token = AppModConfig.GetHeadJsonReq(request, "Authorization");
        return ppMatConfirmWeekAppMod.appModFunc(token, inputParm, db1Service, db2Service, dbHiveMatService);
    }

    /**
     * 用料周报表导出
     *
     * @param request
     * @return
     */
    @RequestMapping(value = "/v1/expPpMatConfirmWeek", method = RequestMethod.GET)
    public ApiResponse<ExpCommonDTO> expPpMatConfirmWeek(HttpServletRequest request, @Validated AppTEduMaterialTotalWObj inputParm) {
        logger.info("输入参数：" + inputParm.toString());
        String token = AppModConfig.GetHeadJsonReq(request, "Authorization");
        return new ExpPpMatConfirmWeekAppMod().appModFunc(token, inputParm, db1Service, db2Service, saasService, dbHiveMatService);
    }

    /**
     * 配货周报表
     *
     * @param request
     * @return
     */
    @RequestMapping(value = "/v1/ppGsPlanWeek", method = RequestMethod.GET)
    public PpGsPlanWeekOutDto ppGsPlanWeek(HttpServletRequest request, @Validated AppTEduLedgerMasterTotalWObj inputParm) {
        logger.info("输入参数：" + inputParm.toString());
        String token = AppModConfig.GetHeadJsonReq(request, "Authorization");
        return ppGsPlanWeekAppMod.appModFunc(token, inputParm, db1Service, db2Service, dbHiveGsService);
    }

    /**
     * 配货周报表导出
     *
     * @param request
     * @return
     */
    @RequestMapping(value = "/v1/expPpGsPlanWeek", method = RequestMethod.GET)
    public ApiResponse<ExpCommonDTO> expPpGsPlanWeek(HttpServletRequest request, @Validated AppTEduLedgerMasterTotalWObj inputParm) {
        logger.info("输入参数：" + inputParm.toString());
        String token = AppModConfig.GetHeadJsonReq(request, "Authorization");
        return new ExpPpGsPlanWeekAppMod().appModFunc(token, inputParm, db1Service, db2Service, saasService, dbHiveGsService);
    }

    /**
     * 留样周报表
     *
     * @param request
     * @return
     */
    @RequestMapping(value = "/v1/ppRetSamplesWeek", method = RequestMethod.GET)
    public PpRetSamplesWeekOutDto ppRetSamplesWeek(HttpServletRequest request, @Validated AppTEduReserveTotalW inputParm) {
        logger.info("输入参数：" + inputParm.toString());
        String token = AppModConfig.GetHeadJsonReq(request, "Authorization");
        return ppRetSamplesWeekAppMod.appModFunc(token, inputParm, db1Service, db2Service, dbHiveRetSamplesService);
    }

    /**
     * 留样周报表导出
     *
     * @param request
     * @return
     */
    @RequestMapping(value = "/v1/expPpRetSamplesWeek", method = RequestMethod.GET)
    public ApiResponse<ExpCommonDTO> expPpRetSamplesWeek(HttpServletRequest request, @Validated AppTEduReserveTotalW inputParm) {
        logger.info("输入参数：" + inputParm.toString());
        String token = AppModConfig.GetHeadJsonReq(request, "Authorization");
        return new ExpPpRetSamplesWeekAppMod().appModFunc(token, inputParm, db1Service, db2Service, saasService, dbHiveRetSamplesService);
    }
}
