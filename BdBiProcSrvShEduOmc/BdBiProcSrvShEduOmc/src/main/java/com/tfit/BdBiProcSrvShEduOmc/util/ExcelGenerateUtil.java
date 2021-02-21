package com.tfit.BdBiProcSrvShEduOmc.util;

import com.tfit.BdBiProcSrvShEduOmc.config.SpringConfig;
import com.tfit.BdBiProcSrvShEduOmc.constant.GlobalConstant;
import com.tfit.BdBiProcSrvShEduOmc.exception.BusinessException;
import com.tfit.BdBiProcSrvShEduOmc.model.uo.TableUO;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.ByteArrayOutputStream;import java.io.File;import com.tfit.BdBiProcSrvShEduOmc.util.FtpUtil;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Date;

/**
 * @Descritpion：Excel生成工具类
 * @author: tianfang_infotech
 * @date: 2019/1/16 17:36
 */
public class ExcelGenerateUtil {

    private static final Logger logger = LogManager.getLogger(ExcelGenerateUtil.class);
    private static final String EXCEL_FORMAT_XLS = SpringConfig.repFileFormats[0];
    private static final String EXCEL_FORMAT1_XLSX = SpringConfig.repFileFormats[1];
    private static final String SHEET_NAME_DEFAULT = "sheet1";
    /**
     * 生成Excel默认目录
     */
    private static final String GENERATE_EXCEL_DIRECTORY_DEFAULT = SpringConfig.tomcatSrvDirs[1];

    static {
        /**
         * 创建目录文件夹
         */
        File dirFile = new File(GENERATE_EXCEL_DIRECTORY_DEFAULT);
        if (!dirFile.exists()) {
            dirFile.mkdirs();
        }
    }

    /**
     * 生成简单EXCEL文件
     *
     * @param pathFileName
     * @param tableUO
     * @return
     */
    public static void generateSimpleExcelFile(String pathFileName, TableUO tableUO) {
        Workbook workbook;
        String pathModel = "%s%s";

        if (!pathFileName.startsWith(GlobalConstant.PATH_SPLIT_SYMBOL)) {
            pathModel = "%s/%s";
        }

        String fullPath = String.format(pathModel, GENERATE_EXCEL_DIRECTORY_DEFAULT, pathFileName);

        File file = new File(fullPath);
        if (file.exists()) {
            //如果文件存在，则返回
            return;
        }

        File dirFile = new File(file.getParent());
        if (!dirFile.exists()) {
            //创建文件所在目录
            dirFile.mkdirs();
        }

        if (pathFileName.lastIndexOf(EXCEL_FORMAT1_XLSX) != -1) {
            workbook = new XSSFWorkbook();
        } else if (pathFileName.lastIndexOf(EXCEL_FORMAT_XLS) != -1) {
            workbook = new HSSFWorkbook();
        } else {
            throw new IllegalArgumentException(String.format("参数 pathFileName：%s 非Excel文件格式。", pathFileName));
        }

        //创建sheet对象
        Sheet sheet = workbook.createSheet(SHEET_NAME_DEFAULT);

        //写excel文件数据，创建第一行
        Row row = sheet.createRow(0);
        Cell cell;
        for (int i = 0; i < tableUO.getColumnCount(); i++) {
            cell = row.createCell(i);
            cell.setCellValue(tableUO.getColumnNames()[i]);
            logger.info(tableUO.getColumnNames()[i] + " ");
        }

        //循环写入行数据
        for (int i = 0; i < tableUO.getRowCount(); i++) {
            row = sheet.createRow(i + 1);
            for (int j = 0; j < tableUO.getColumnCount(); j++) {
                resolveRowCell(tableUO, row, i, j);
            }
        }

        //创建文件流
        OutputStream stream = null;
        try {
            stream = new FileOutputStream(fullPath);
            //workbook.write(stream);
            ByteArrayOutputStream os = new ByteArrayOutputStream();workbook.write(os);FtpUtil.ftpServer(pathFileName, os,null);
            stream.flush();
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
            throw new BusinessException(e.getMessage(), e);
        } finally {
            if (stream != null) {
                try {
                    //关闭文件流
                    stream.close();
                } catch (IOException e) {
                    logger.error(e.getMessage(), e);
                }
            }
        }

        logger.info("Excel导出文件生成路径：" + fullPath);
    }

    /**
     * 解析单元格值
     *
     * @param tableUO
     * @param row
     * @param i
     * @param j
     */
    private static void resolveRowCell(TableUO tableUO, Row row, int i, int j) {
        if (tableUO.getDataMatrix()[i][j] instanceof String) {
            row.createCell(j).setCellValue(tableUO.getDataMatrix()[i][j].toString());
        } else if (tableUO.getDataMatrix()[i][j] instanceof Double) {
            row.createCell(j).setCellValue((Double) tableUO.getDataMatrix()[i][j]);
        } else if (tableUO.getDataMatrix()[i][j] instanceof Integer) {
            row.createCell(j).setCellValue((Integer) tableUO.getDataMatrix()[i][j]);
        } else if (tableUO.getDataMatrix()[i][j] instanceof Date) {
            row.createCell(j).setCellValue((Date) tableUO.getDataMatrix()[i][j]);
        } else if (tableUO.getDataMatrix()[i][j] == null) {
            row.createCell(j).setCellValue("");
        } else {
            row.createCell(j).setCellValue(tableUO.getDataMatrix()[i][j].toString());
        }
    }
}
