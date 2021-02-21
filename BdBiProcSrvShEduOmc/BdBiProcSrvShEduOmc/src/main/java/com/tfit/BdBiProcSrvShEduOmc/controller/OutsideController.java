package com.tfit.BdBiProcSrvShEduOmc.controller;

import javax.servlet.http.HttpServletRequest;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tfit.BdBiProcSrvShEduOmc.annotation.CheckUserToken;
import com.tfit.BdBiProcSrvShEduOmc.appmod.im.DishRsDetsAppMod;
import com.tfit.BdBiProcSrvShEduOmc.appmod.im.outside.GsPlanOptDetsOutsideAppMod;
import com.tfit.BdBiProcSrvShEduOmc.appmod.im.outside.PpDishDetsOutsideAppMod;
import com.tfit.BdBiProcSrvShEduOmc.config.AppModConfig;
import com.tfit.BdBiProcSrvShEduOmc.dto.im.DishRsDetsDTO;
import com.tfit.BdBiProcSrvShEduOmc.dto.im.outside.GsPlanOptDetsOutsideDTO;
import com.tfit.BdBiProcSrvShEduOmc.dto.im.outside.PpDishDetsOutsideDTO;
import com.tfit.BdBiProcSrvShEduOmc.dto.outside.DishRsDetsInputDTO;
import com.tfit.BdBiProcSrvShEduOmc.dto.outside.GsPlanOptDetsInputDTO;
import com.tfit.BdBiProcSrvShEduOmc.dto.outside.PpDishDetsInputDTO;
import com.tfit.BdBiProcSrvShEduOmc.service.Db1Service;
import com.tfit.BdBiProcSrvShEduOmc.service.Db2Service;
import com.tfit.BdBiProcSrvShEduOmc.service.DbHiveDishService;
import com.tfit.BdBiProcSrvShEduOmc.service.DbHiveGsService;
import com.tfit.BdBiProcSrvShEduOmc.service.DbHiveMatService;
import com.tfit.BdBiProcSrvShEduOmc.service.DbHiveRecyclerWasteService;
import com.tfit.BdBiProcSrvShEduOmc.service.DbHiveWarnService;
import com.tfit.BdBiProcSrvShEduOmc.service.SaasService;
import com.tfit.BdBiProcSrvShEduOmc.util.CommonUtil;

/**
 * @Descritpion：业务数据-信息管理
 * @author: tianfang_infotech
 * @date: 2019/1/2 14:20
 */
@CheckUserToken
@RestController
@RequestMapping(value = "/biOptAnl")
public class OutsideController {

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

    @Autowired
    ObjectMapper objectMapper;
    
    /**
     * 项目点排菜详情列表应用模型
     */
    PpDishDetsOutsideAppMod pddAppMod = new PpDishDetsOutsideAppMod();
    
    /**
     * 配货计划操作详情列表应用模型
     */
    GsPlanOptDetsOutsideAppMod gpodAppMod = new GsPlanOptDetsOutsideAppMod();
    
    /**
     * 菜品留样详情列表应用模型
     */
    DishRsDetsAppMod drdAppMod = new DishRsDetsAppMod();
    
    
    /**
     * 项目点排菜详情列表
     *
     * @param request
     * @return
     */
    @RequestMapping(value = "/v1/outside/ppDishDets", method = RequestMethod.POST)
    public PpDishDetsOutsideDTO v1_ppDishDetsOutsideDTO(HttpServletRequest request,@RequestBody PpDishDetsInputDTO inputDTO) {
        //授权码
        String token = AppModConfig.GetHeadJsonReq(request, "Authorization");
        
        logger.info("输入参数：" + inputDTO==null?"":inputDTO.toString());
        //项目点排菜详情列表应用模型函数
        return pddAppMod.appModFunc(token, inputDTO.getStartSubDate(), inputDTO.getEndSubDate(), inputDTO.getSchoolList(),
        		db1Service, db2Service, saasService,dbHiveDishService);
    }
    
    /**
     * 配货计划操作详情列表
     *
     * @param request
     * @return
     */
    @RequestMapping(value = "/v1/outside/gsPlanOptDets", method = RequestMethod.POST)
    public GsPlanOptDetsOutsideDTO v1_gsPlanOptDets(HttpServletRequest request,@RequestBody GsPlanOptDetsInputDTO inputDTO) {
        //授权码
        String token = AppModConfig.GetHeadJsonReq(request, "Authorization");
        logger.info("输入参数：" + inputDTO==null?"":inputDTO.toString());
        //配货计划操作详情列表应用模型函数
        return gpodAppMod.appModFunc(token, inputDTO.getStartSubDate(), inputDTO.getEndSubDate(), 
            		inputDTO.getDistrBatNumberList(),
            		db1Service, db2Service, saasService,dbHiveGsService);
    }
    
    
    /**
     * 菜品留样详情列表
     *
     * @param request
     * @return
     */
    @RequestMapping(value = "/v1/outside/dishRsDets", method = RequestMethod.POST)
    public DishRsDetsDTO v1_dishRsDets(HttpServletRequest request,@RequestBody DishRsDetsInputDTO inputDTO) {
    	//授权码
        String token = AppModConfig.GetHeadJsonReq(request, "Authorization");
        //页号
        if (CommonUtil.isEmpty(inputDTO.getPage())) {
        	inputDTO.setPage("1");
        }
        //分页大小
        if (CommonUtil.isEmpty(inputDTO.getPageSize())) {
        	inputDTO.setPageSize("20");
        }
        
        logger.info("输入参数：" + inputDTO==null?"":inputDTO.toString());
        
        return drdAppMod.appModFunc(token,  inputDTO.getStartSubDate(), inputDTO.getEndSubDate(), inputDTO.getDistName(), 
        		inputDTO.getPrefCity(), inputDTO.getProvince(), inputDTO.getSubLevel(), inputDTO.getCompDep(), inputDTO.getSchGenBraFlag(), inputDTO.getSubDistName(), 
        		inputDTO.getFblMb(), inputDTO.getSchProp(), inputDTO.getPpName(), inputDTO.getRmcName(), 
        		inputDTO.getRsFlag(), inputDTO.getCaterType(), inputDTO.getSchType(), inputDTO.getMenuName(), 
        		inputDTO.getOptMode(), inputDTO.getRsUnit(),
            		inputDTO.getDistNames(),inputDTO.getSubLevels(),inputDTO.getCompDeps(),inputDTO.getSchProps(),
            		inputDTO.getCaterTypes(),inputDTO.getSchTypes(),inputDTO.getMenuNames(),inputDTO.getOptModes(),
            		inputDTO.getDepartmentId(),inputDTO.getDepartmentIds(),inputDTO.getReserveStatus(),
            		inputDTO.getMenuCaterTypeMap(),
            		inputDTO.getPage(), inputDTO.getPageSize(), db1Service, db2Service, saasService);
    }
}