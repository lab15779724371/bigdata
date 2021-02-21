package com.tfit.BdBiProcSrvShEduOmc.appmod;

import java.io.File;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.tfit.BdBiProcSrvShEduOmc.config.SpringConfig;
import com.tfit.BdBiProcSrvShEduOmc.dto.RedisHashDTO;
import com.tfit.BdBiProcSrvShEduOmc.service.RedisService;
import com.tfit.BdBiProcSrvShEduOmc.util.ExcelUtils;
import com.tfit.BdBiProcSrvShEduOmc.util.FileUtils;


/**
 * 
 * @author sunny
 *
 */
@Service
public class ExportExcelServiceImpl implements ExportExcelService {
	
	//Redis服务
	@Autowired
	RedisService redisService = new RedisService();

    @Override
    public void doExport(HttpServletResponse response, String key) throws Exception {
        @SuppressWarnings("rawtypes")
        ExcelReportType excelReportType = ExcelReportType.valueOf("REDIS_HASH_TEMPLATE");
        Object vo = this.getClass().getDeclaredMethod(excelReportType.getBuildVoMethod(), String.class).invoke(this, key);
        File tempFile = ExcelUtils.generateExcel(vo, excelReportType.getTemplatePath());
        FileUtils.exportFile(response, tempFile, "download.xls");
        tempFile.delete();
    }

    enum ExcelReportType {
    	/**
         * 导出excel匹配
         * @author Administrator
         *
         */
    	REDIS_HASH_TEMPLATE("/data/RedisHashTemplate.xls", "redisHashTemplate");
    	
        private String templatePath;
        private String buildVoMethod;

        ExcelReportType(String templatePath, String buildMethodName) {
            this.templatePath = templatePath;
            this.buildVoMethod = buildMethodName;
        }

        public String getTemplatePath() {
            return templatePath;
        }

        public String getBuildVoMethod() {
            return buildVoMethod;
        }
        
        
       

    }

    @SuppressWarnings({ "rawtypes", "unused" })
    private Object redisHashTemplate(String key) {
    	
    	Map<String, String>  redisHashMap = redisService.getHashByKey(SpringConfig.RedisConnPool.REDISCLUSTER1.value, SpringConfig.RedisDBIdx, key);
    	Map<String, Object> resultMap = new LinkedHashMap<>();
        List<RedisHashDTO> voList = new ArrayList<RedisHashDTO>();
        for(Map.Entry<String, String> entry : redisHashMap.entrySet()) {
        	RedisHashDTO vo1= new RedisHashDTO();
	        vo1.setKey(entry.getKey());
	        vo1.setValue(entry.getValue());
	        voList.add(vo1);
        }
        
        
        resultMap.put("list", voList);
        
        return resultMap;
    }
    

}
