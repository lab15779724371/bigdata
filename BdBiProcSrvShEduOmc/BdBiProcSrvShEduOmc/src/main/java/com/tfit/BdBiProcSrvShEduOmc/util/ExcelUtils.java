package com.tfit.BdBiProcSrvShEduOmc.util;

/*import org.jxls.common.Context;
import org.jxls.util.JxlsHelper;*/
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.UUID;

/**
 * 根据模板生成Excel
 * @author chenchang
 * @date 2018年9月3日
 */
public class ExcelUtils {

    static Logger logger = LoggerFactory.getLogger(ExcelUtils.class);

    public static File generateExcel(Object vo, String excelTemplatePath) throws IOException {
    	 File tempFile;
         try (InputStream is = new FileInputStream(excelTemplatePath)) {
             tempFile = File.createTempFile(UUID.randomUUID().toString().replace("-", "").toLowerCase(), ".xls");
             try (OutputStream os = new FileOutputStream(tempFile)) {
                /* Context context = new Context();
                 context.putVar("data", vo);
                 JxlsHelper.getInstance().processTemplate(is, os, context);*/
             }
         }
         return tempFile;
    }

}
