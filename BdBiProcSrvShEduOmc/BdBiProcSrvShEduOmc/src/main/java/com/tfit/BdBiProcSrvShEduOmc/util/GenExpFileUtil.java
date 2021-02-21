package com.tfit.BdBiProcSrvShEduOmc.util;

import java.io.ByteArrayOutputStream;import java.io.File;import com.tfit.BdBiProcSrvShEduOmc.util.FtpUtil;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.util.Date;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.tfit.BdBiProcSrvShEduOmc.config.AppModConfig;
import com.tfit.BdBiProcSrvShEduOmc.model.uo.TableUO;

//生成导出文件工具
public class GenExpFileUtil {
	private static final Logger logger = LogManager.getLogger(GenExpFileUtil.class.getName());
	
	//生成导出文件
	public static boolean genExpFile(String pathFileName, TableUO tableUO) {
		boolean retFlag = true;
		int idx = pathFileName.lastIndexOf(".");
		String fileType = "csv";
		if(idx != -1)
			fileType = pathFileName.substring(idx+1, pathFileName.length());
		if(fileType.equalsIgnoreCase("xls") || fileType.equalsIgnoreCase("xlsx")) {   //excell文件
			//生成导出EXCEL文件
			retFlag = expExcelFile(pathFileName, tableUO);
		}
		else if(fileType.equalsIgnoreCase("csv")) {  //csv文件
			
		}
		else if(fileType.equalsIgnoreCase("doc") || fileType.equalsIgnoreCase("docx")) {    //word文件
			
		}
		
		return retFlag;
	}
	
	//生成导出EXCEL文件
	public static boolean expExcelFile(String pathFileName, TableUO tableUO) { 
		int rowCount = tableUO.getRowCount();
		if(rowCount > AppModConfig.maxPageSize)
			return false;
		boolean retFlag = true;
		Workbook wb = null;
        String excelPath = pathFileName, fileType = "";
        File file = new File(excelPath);
        Sheet sheet = null;
        int idx1 = excelPath.lastIndexOf(".xls"), idx2 = excelPath.lastIndexOf(".xlsx");
        if(idx1 != -1)
        	fileType = excelPath.substring(idx1+1);
        else if(idx2 != -1)
        	fileType = excelPath.substring(idx2+1);
        //创建工作文档对象   
        if (!file.exists()) {      //excel文件不存在
            if (fileType.equals("xls")) {
                wb = new HSSFWorkbook();                
            } else if(fileType.equals("xlsx")) {  
            	wb = new XSSFWorkbook();
            } else {
            	retFlag = false;
            }
            //创建sheet对象   
            if(retFlag) {
            	sheet = (Sheet) wb.createSheet("sheet1");  
            	OutputStream outputStream = null;
				try {
					outputStream = new FileOutputStream(excelPath);
				} catch (FileNotFoundException e) {
					// TODO 自动生成的 catch 块
					e.printStackTrace();
				}
				if(outputStream != null) {
					try {
						wb.write(outputStream);
					} catch (IOException e) {
						// TODO 自动生成的 catch 块
						e.printStackTrace();
					}
					try {
						outputStream.flush();
					} catch (IOException e) {
						// TODO 自动生成的 catch 块
						e.printStackTrace();
					}
					try {
						outputStream.close();
					} catch (IOException e) {
						// TODO 自动生成的 catch 块
						e.printStackTrace();
					}
				}
				else
					retFlag = false;
            }
            
        } 
        else {       //excel文件已存在
            if (fileType.equals("xls")) {  
                wb = new HSSFWorkbook();                  
            } else if(fileType.equals("xlsx")) { 
                wb = new XSSFWorkbook();                  
            } else {  
            	retFlag = false;
            }  
        }
        //创建sheet对象  
        if (sheet == null && retFlag) {
            sheet = (Sheet) wb.createSheet("sheet1");  
        }
        //写excel文件数据
        if(sheet != null && retFlag) {
        	int startRowIdx = 0;
        	String[] colNames = tableUO.getColumnNames();
        	String[] colVals = new String[colNames.length];
			// 添加样式
			Row row = null;
			Cell cell = null;
			// 创建第一行
			row = (Row) sheet.createRow(startRowIdx);
			//获取excell单元风格
		  	CellStyle style = AppModConfig.getExcellCellStyle(wb);
			for (int i = 0; i < colNames.length; i++) {
				cell = row.createCell(i);
				try {
					logger.info(colNames[i] + " ");
					colVals[i] = new String(colNames[i].getBytes(), "utf-8");
					cell.setCellValue(colNames[i]);
					cell.setCellStyle(style);
				} catch (UnsupportedEncodingException e) {
					// TODO 自动生成的 catch 块
					e.printStackTrace();
				}
			}
			// 循环写入行数据
			startRowIdx++;
			for (int i = 0; i < rowCount; i++) {
				row = (Row) sheet.createRow(i + startRowIdx);	
				 for (int j = 0; j < tableUO.getColumnCount(); j++) {
					 resolveRowCell(tableUO, row, i, j);
				 }
			}
			// 创建文件流
			OutputStream stream = null;
			try {
				stream = new FileOutputStream(excelPath);
			} catch (FileNotFoundException e) {
				// TODO 自动生成的 catch 块
				e.printStackTrace();
			}

			if (stream != null) {
				// 写入数据
				try {
					ByteArrayOutputStream os = new ByteArrayOutputStream();wb.write(os);FtpUtil.ftpServer(pathFileName, os,null);
				} catch (IOException e) {
					// TODO 自动生成的 catch 块
					e.printStackTrace();
				}
				// 关闭文件流
				try {
					stream.close();
				} catch (IOException e) {
					// TODO 自动生成的 catch 块
					e.printStackTrace();
				}
			} else
				retFlag = false;
        }
        
        return retFlag;
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
        } 
        else if (tableUO.getDataMatrix()[i][j] instanceof Float) {
            row.createCell(j).setCellValue((Float) tableUO.getDataMatrix()[i][j]);
        }
        else if (tableUO.getDataMatrix()[i][j] instanceof Double) {
            row.createCell(j).setCellValue((Double) tableUO.getDataMatrix()[i][j]);
        } 
        else if (tableUO.getDataMatrix()[i][j] instanceof Integer) {
            row.createCell(j).setCellValue((Integer) tableUO.getDataMatrix()[i][j]);
        } 
        else if (tableUO.getDataMatrix()[i][j] instanceof Date) {
            row.createCell(j).setCellValue((Date) tableUO.getDataMatrix()[i][j]);
        } 
        else if (tableUO.getDataMatrix()[i][j] == null) {
            row.createCell(j).setCellValue("");
        } 
        else {
            row.createCell(j).setCellValue(tableUO.getDataMatrix()[i][j].toString());
        }
    }
}
