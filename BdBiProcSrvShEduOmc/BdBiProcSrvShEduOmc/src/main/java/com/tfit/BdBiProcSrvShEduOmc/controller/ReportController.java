package com.tfit.BdBiProcSrvShEduOmc.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.tfit.BdBiProcSrvShEduOmc.appmod.ExportExcelService;



/**
 * 
 * @author sunny
 *
 */
@RestController
@RequestMapping(value = "/biApp")
public class ReportController {
	
    @Autowired
    private ExportExcelService exportExcelService;
    
    /**
     * 导出Excel文档
     * @param response
     * @param jsonParams（输入参数）
     * @throws Exception
     */
	@RequestMapping("/reportRedisHash")
    public void exportReport(HttpServletResponse response,HttpServletRequest request) throws Exception {
		String key = request.getParameter("key");
        exportExcelService.doExport(response, key);
    }
}
