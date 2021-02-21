package com.tfit.BdBiProcSrvShEduOmc.controller;

import com.alibaba.fastjson.JSONObject;
import com.tfit.BdBiProcSrvShEduOmc.appmod.task.NoReadInfoNumMod;
import com.tfit.BdBiProcSrvShEduOmc.appmod.task.SysInfoDisplayMod;
import com.tfit.BdBiProcSrvShEduOmc.appmod.wh.NotReadWarehouseStatusMod;
import com.tfit.BdBiProcSrvShEduOmc.appmod.wh.WarehouseStatusMod;
import com.tfit.BdBiProcSrvShEduOmc.service.Db1Service;
import com.tfit.BdBiProcSrvShEduOmc.service.Db2Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

/**
 * @Description: 五级预警 入库 消息已读 未读
 * @Param: $
 * @returns: $
 * @Author: weihai_zhao
 * @Date: 2020-01-18
 */
@RestController
@RequestMapping(value = "/biOptAnl")
public class WarehouseWarnStatusController {
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


    //1.0 根据用户的读取状态获取未读或已读的列表
    @RequestMapping(value = "/v1/warehouseWarnInfoDisplay", method = RequestMethod.POST)
    public String v1_sysInfoDisplay(HttpServletRequest request, @RequestBody JSONObject jsonObject) {
        Integer readsStatus = jsonObject.getInteger("readsStatus");
        Integer page = jsonObject.getInteger("page");
        Integer pageSize = jsonObject.getInteger("pageSize");
        return new WarehouseStatusMod().appModFunc(request,page,pageSize,readsStatus,db1Service, db2Service);
    }

    //4.0.1 未读信息数
    @RequestMapping(value = "/v1/noReadwarehouseWarnNum", method = RequestMethod.POST)
    public String v1_noReadInfoNum(HttpServletRequest request) {
        return new NotReadWarehouseStatusMod().appModFunc(request, db1Service, db2Service);
    }

    /**
     * @Description: 根据标题id获取内容
     * @Param: [request]
     * @return: java.lang.String
     * @Author: jianghy
     * @Date: 2020/1/19
     * @Time: 11:09
     */
    @RequestMapping(value = "/v1/getWarnListByTitleId", method = RequestMethod.POST)
    public String getWarnListByTitleId(HttpServletRequest request, @RequestBody JSONObject jsonObject) { 
        String titleId = jsonObject.getString("titleId");
        Integer page = jsonObject.getInteger("page");
        Integer pageSize = jsonObject.getInteger("pageSize");
        return new WarehouseStatusMod().getWarnListByTitleId(request,titleId,page,pageSize,db1Service, db2Service);
    }
}
