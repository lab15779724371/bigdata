package com.tfit.BdBiProcSrvShEduOmc.appmod.im;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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
import com.tfit.BdBiProcSrvShEduOmc.config.SpringConfig;
import com.tfit.BdBiProcSrvShEduOmc.dto.im.ExpMatConfirms;
import com.tfit.BdBiProcSrvShEduOmc.dto.im.ExpMatConfirmsDTO;
import com.tfit.BdBiProcSrvShEduOmc.dto.im.MatConfirms;
import com.tfit.BdBiProcSrvShEduOmc.dto.im.MatConfirmsDTO;
import com.tfit.BdBiProcSrvShEduOmc.obj.base.DepartmentObj;
import com.tfit.BdBiProcSrvShEduOmc.service.Db1Service;
import com.tfit.BdBiProcSrvShEduOmc.service.Db2Service;
import com.tfit.BdBiProcSrvShEduOmc.service.DbHiveMatService;
import com.tfit.BdBiProcSrvShEduOmc.util.BCDTimeUtil;
import com.tfit.BdBiProcSrvShEduOmc.util.FtpUtil;
import com.tfit.BdBiProcSrvShEduOmc.util.UniqueIdGen;

//导出用料确认列表应用模型
public class ExpMatConfirmsAppMod {
	private static final Logger logger = LogManager.getLogger(ExpMatConfirmsAppMod.class.getName());
	
	//用料确认列表应用模型
	private MatConfirmsAppMod epdlAppMod = new MatConfirmsAppMod();
	
	//是否为真实数据标识
	private static boolean isRealData = true;
	//页号、页大小和总页数
	int curPageNum = 1, pageTotal = 1, pageSize = AppModConfig.maxPageSize;
	//报表文件资源路径
	String repFileResPath = "/expMatConfirms/";
	//导出列名数组
	String[] colNames0 = {"用料日期", "所属", "主管部门", "应确认学校食堂", "已确认用料学校食堂", "未确认用料学校食堂","确认率", "用料计划总数", "已确认用料计划", "未确认用料计划", "用料计划确认率"};
	String[] colNames1 = {"用料日期", "所在地", "应确认学校食堂", "已确认用料学校食堂", "未确认用料学校食堂","确认率","用料计划总数", "已确认用料计划", "未确认用料计划", "用料计划确认率"};	
	String[] colNames2 = {"用料日期", "管理部门","应确认学校食堂","已确认用料学校食堂",  "未确认用料学校食堂","确认率", "用料计划总数", "已确认用料计划", "未确认用料计划", "用料计划确认率"};	
	//变量数据初始化
	String startDate = "2018-09-03";
	String endDate = "2018-09-04";
	String distName = null;
	String prefCity = null;
	String province = "上海市";
	String expFileUrl = "test1.txt";
	
	//模拟数据函数
	private ExpMatConfirmsDTO SimuDataFunc() {
		ExpMatConfirmsDTO epdlDto = new ExpMatConfirmsDTO();
		//设置返回数据
		epdlDto.setTime(BCDTimeUtil.convertNormalFrom(null));
		//列表元素设置
		ExpMatConfirms expMatConfirms = new ExpMatConfirms();
		//赋值
		expMatConfirms.setStartDate(startDate);
		expMatConfirms.setEndDate(endDate);
		expMatConfirms.setDistName(distName);
		expMatConfirms.setPrefCity(prefCity);
		expMatConfirms.setProvince(province);
		expMatConfirms.setExpFileUrl(SpringConfig.repfile_srvdn+repFileResPath+expFileUrl);
		//设置模拟数据
		epdlDto.setExpMatConfirms(expMatConfirms);
		//消息ID
		epdlDto.setMsgId(AppModConfig.msgId);
		AppModConfig.msgId++;
		//消息id小于0判断
		AppModConfig.msgIdLessThan0Judge();

		return epdlDto;
	}
	
	//生成导出EXCEL文件按主管部门
	public boolean expMatConfirmsExcelByCompDep(String pathFileName, List<MatConfirms> dataList, String colNames[]) { 
		boolean retFlag = true;
		Workbook wb = null;
        String excelPath = pathFileName, fileType = "";
        File file = new File(excelPath);
        Sheet sheet = null;
        int idx1 = excelPath.lastIndexOf(".xls"), idx2 = excelPath.lastIndexOf(".xlsx");
        if(dataList.size() > AppModConfig.maxPageSize)
        	return false;
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
			String totalDishDate = "合计", totalDistName = "---";
			int totalDishSchNum = 0, totalNoConMatSchNum = 0, totalConMatSchNum = 0, totalMatPlanNum = 0, conMatPlanNum = 0, noConMatPlanNum = 0;
			float totalDishRate = (float) 0.0;
			int columIndex = 0;
			String subLevel = "";
			String compDep = "";
			
			float totalSchMatConRate = (float) 0.0;
			float schMatConRate = (float) 0.0;
			for (int i = 0; i < dataList.size(); i++) {
				columIndex = 0;
				row = (Row) sheet.createRow(i + startRowIdx);
				row.createCell(columIndex++).setCellValue(dataList.get(i).getMatUseDate());                                         //用料日期
				subLevel = dataList.get(i).getSubLevel();
				if(subLevel !=null && subLevel.indexOf(",")>=0) {
					subLevel = subLevel.substring(subLevel.indexOf(",")+1, subLevel.length());
				}
				row.createCell(columIndex++).setCellValue(subLevel);                                       //所属
				compDep = dataList.get(i).getCompDep();
				if(compDep !=null && compDep.indexOf(",")>=0) {
					compDep = compDep.substring(compDep.indexOf(",")+1, compDep.length());
				}
				row.createCell(columIndex++).setCellValue(compDep);                                        //主管部门
				row.createCell(columIndex++).setCellValue(dataList.get(i).getShouldAccSchNum());                                         //已排菜学校
				totalDishSchNum += dataList.get(i).getShouldAccSchNum();
				row.createCell(columIndex++).setCellValue(dataList.get(i).getConMatSchNum());                                       //已确认用料学校
				totalConMatSchNum += dataList.get(i).getConMatSchNum();
				row.createCell(columIndex++).setCellValue(dataList.get(i).getNoConMatSchNum());                                     //未确认用料学校
				totalNoConMatSchNum += dataList.get(i).getNoConMatSchNum();
				//用料计划确认率
				schMatConRate = 0;
				if(dataList.get(i).getShouldAccSchNum() > 0) {
					schMatConRate = 100 * ((float) dataList.get(i).getConMatSchNum() / (float) dataList.get(i).getShouldAccSchNum());
					BigDecimal bd = new BigDecimal(schMatConRate);
					schMatConRate = bd.setScale(2, BigDecimal.ROUND_HALF_UP).floatValue();
					if (schMatConRate > 100)
						schMatConRate = 100;
				}
				row.createCell(columIndex++).setCellValue(schMatConRate + "%");                                     //确认率
				
				row.createCell(columIndex++).setCellValue(dataList.get(i).getTotalMatPlanNum());                                    //用料计划总数
				totalMatPlanNum += dataList.get(i).getTotalMatPlanNum(); 
				row.createCell(columIndex++).setCellValue(dataList.get(i).getConMatPlanNum());                                      //已确认用料计划
				conMatPlanNum += dataList.get(i).getConMatPlanNum();
				row.createCell(columIndex++).setCellValue(dataList.get(i).getNoConMatPlanNum());                                    //未确认用料计划
				noConMatPlanNum += dataList.get(i).getNoConMatPlanNum();
				row.createCell(columIndex++).setCellValue(dataList.get(i).getMatConRate() + "%");                                   //确认率
			}
			//合计全市
			totalDishRate = 0;
			if(totalMatPlanNum > 0) {
				totalDishRate = 100 * ((float) conMatPlanNum / (float) totalMatPlanNum);
				BigDecimal bd = new BigDecimal(totalDishRate);
				totalDishRate = bd.setScale(2, BigDecimal.ROUND_HALF_UP).floatValue();
				if (totalDishRate > 100) {
					totalDishRate = 100;
					totalMatPlanNum = conMatPlanNum;
				}
			}
			
			//合计全市
			totalSchMatConRate = 0;
			if(totalDishSchNum > 0) {
				totalSchMatConRate = 100 * ((float) conMatPlanNum / (float) totalDishSchNum);
				BigDecimal bd = new BigDecimal(totalSchMatConRate);
				totalSchMatConRate = bd.setScale(2, BigDecimal.ROUND_HALF_UP).floatValue();
				if (totalSchMatConRate > 100) {
					totalSchMatConRate = 100;
				}
			}
			
			//创建合计一行
			startRowIdx += dataList.size();
			row = (Row) sheet.createRow(startRowIdx);
			for (int i = 0; i < colNames.length; i++) {
				cell = row.createCell(i);
				if(i == 0)
					cell.setCellValue(totalDishDate);
				else if(i == 1 || i == 2)
					cell.setCellValue(totalDistName);
				else if(i == 3)
					cell.setCellValue(totalDishSchNum);
				else if(i == 4)
					cell.setCellValue(totalConMatSchNum);
				else if(i == 5)
					cell.setCellValue(totalNoConMatSchNum);
				else if(i == 6)
					cell.setCellValue(totalSchMatConRate + "%");
				else if(i == 7)
					cell.setCellValue(totalMatPlanNum);
				else if(i == 8)
					cell.setCellValue(conMatPlanNum);
				else if(i == 9)
					cell.setCellValue(noConMatPlanNum);
				else if(i == 10) 
					cell.setCellValue(totalDishRate + "%");
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
					ByteArrayOutputStream os = new ByteArrayOutputStream();wb.write(os);FtpUtil.ftpServer(pathFileName, os,repFileResPath);
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
	
	//生成导出EXCEL文件按所在地
	public boolean expMatConfirmsExcelByLocality(String pathFileName, List<MatConfirms> dataList, String colNames[]) { 
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
			String totalDishDate = "合计", totalDistName = "---";
			int totalDishSchNum = 0, totalNoConMatSchNum = 0, totalConMatSchNum = 0, totalMatPlanNum = 0, conMatPlanNum = 0, noConMatPlanNum = 0;
			float totalDishRate = (float) 0.0;
			
			float totalSchMatConRate = (float) 0.0;
			float schMatConRate = (float) 0.0;
			
			for (int i = 0; i < dataList.size(); i++) {
				//{"用料日期", "所在地", "应确认学校食堂", "已确认用料学校食堂", "未确认用料学校食堂","确认率","用料计划总数", "已确认用料计划", "未确认用料计划", "用料计划确认率"};	
				row = (Row) sheet.createRow(i + startRowIdx);
				row.createCell(0).setCellValue(dataList.get(i).getMatUseDate());                                         //用料日期
				row.createCell(1).setCellValue(AppModConfig.distIdToNameMap.get(dataList.get(i).getDistName()));         //所在地		
				row.createCell(2).setCellValue(dataList.get(i).getShouldAccSchNum());                                         //应确认学校食堂
				totalDishSchNum += dataList.get(i).getShouldAccSchNum();
				row.createCell(3).setCellValue(dataList.get(i).getConMatSchNum());                                       //已确认用料学校
				totalConMatSchNum += dataList.get(i).getConMatSchNum();
				row.createCell(4).setCellValue(dataList.get(i).getNoConMatSchNum());                                     //未确认用料学校
				totalNoConMatSchNum += dataList.get(i).getNoConMatSchNum();
				
				//用料计划确认率
				schMatConRate = 0;
				if(dataList.get(i).getShouldAccSchNum() > 0) {
					schMatConRate = 100 * ((float) dataList.get(i).getConMatSchNum() / (float) dataList.get(i).getShouldAccSchNum());
					BigDecimal bd = new BigDecimal(schMatConRate);
					schMatConRate = bd.setScale(2, BigDecimal.ROUND_HALF_UP).floatValue();
					if (schMatConRate > 100)
						schMatConRate = 100;
				}
				row.createCell(5).setCellValue(schMatConRate + "%");                                     //确认率
				
				row.createCell(6).setCellValue(dataList.get(i).getTotalMatPlanNum());                                    //用料计划总数
				totalMatPlanNum += dataList.get(i).getTotalMatPlanNum(); 
				row.createCell(7).setCellValue(dataList.get(i).getConMatPlanNum());                                      //已确认用料计划
				conMatPlanNum += dataList.get(i).getConMatPlanNum();
				row.createCell(8).setCellValue(dataList.get(i).getNoConMatPlanNum());                                    //未确认用料计划
				noConMatPlanNum += dataList.get(i).getNoConMatPlanNum();
				row.createCell(9).setCellValue(dataList.get(i).getMatConRate() + "%");                                   //确认率
			}
			//合计全市
			totalDishRate = 0;
			if(totalMatPlanNum > 0) {
				totalDishRate = 100 * ((float) conMatPlanNum / (float) totalMatPlanNum);
				BigDecimal bd = new BigDecimal(totalDishRate);
				totalDishRate = bd.setScale(2, BigDecimal.ROUND_HALF_UP).floatValue();
				if (totalDishRate > 100) {
					totalDishRate = 100;
					totalMatPlanNum = conMatPlanNum;
				}
			}
			
			//合计全市
			totalSchMatConRate = 0;
			if(totalDishSchNum > 0) {
				totalSchMatConRate = 100 * ((float) conMatPlanNum / (float) totalDishSchNum);
				BigDecimal bd = new BigDecimal(totalSchMatConRate);
				totalSchMatConRate = bd.setScale(2, BigDecimal.ROUND_HALF_UP).floatValue();
				if (totalSchMatConRate > 100) {
					totalSchMatConRate = 100;
				}
			}
			
			//创建合计一行
			startRowIdx += dataList.size();
			row = (Row) sheet.createRow(startRowIdx);
			for (int i = 0; i < colNames.length; i++) {
				cell = row.createCell(i);
				if(i == 0)
					cell.setCellValue(totalDishDate);
				else if(i == 1)
					cell.setCellValue(totalDistName);
				else if(i == 2)
					cell.setCellValue(totalDishSchNum);
				else if(i == 3)
					cell.setCellValue(totalConMatSchNum);
				else if(i == 4)
					cell.setCellValue(totalNoConMatSchNum);
				else if(i == 5)
					cell.setCellValue(totalSchMatConRate + "%");
				else if(i == 6)
					cell.setCellValue(totalMatPlanNum);
				else if(i == 7)
					cell.setCellValue(conMatPlanNum);
				else if(i == 8)
					cell.setCellValue(noConMatPlanNum);
				else if(i == 9) 
					cell.setCellValue(totalDishRate + "%");
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
					ByteArrayOutputStream os = new ByteArrayOutputStream();wb.write(os);FtpUtil.ftpServer(pathFileName, os,repFileResPath);
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
	
	//生成导出EXCEL文件按所在地
	public boolean expMatConfirmsExcelByDepartment(String pathFileName, List<MatConfirms> dataList, Map<String,DepartmentObj> deparmentMap,String colNames[]) { 
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
			String totalDishDate = "合计", totalDistName = "---";
			int totalDishSchNum = 0, totalNoConMatSchNum = 0, totalConMatSchNum = 0, totalMatPlanNum = 0, conMatPlanNum = 0, noConMatPlanNum = 0;
			float totalDishRate = (float) 0.0;
			float totalSchMatConRate = (float) 0.0;
			
			float schMatConRate = (float) 0.0;
			for (int i = 0; i < dataList.size(); i++) {
				row = (Row) sheet.createRow(i + startRowIdx);
				row.createCell(0).setCellValue(dataList.get(i).getMatUseDate());                                         //用料日期
				row.createCell(1).setCellValue(deparmentMap.get(dataList.get(i).getDepartmentId())==null?"":deparmentMap.get(dataList.get(i).getDepartmentId()).getDepartmentName());//管理部门		
				row.createCell(2).setCellValue(dataList.get(i).getShouldAccSchNum());                                         //应确认学校食堂
				totalDishSchNum += dataList.get(i).getShouldAccSchNum();
				row.createCell(3).setCellValue(dataList.get(i).getConMatSchNum());                                       //已确认用料学校
				totalConMatSchNum += dataList.get(i).getConMatSchNum();
				row.createCell(4).setCellValue(dataList.get(i).getNoConMatSchNum());                                     //未确认用料学校
				totalNoConMatSchNum += dataList.get(i).getNoConMatSchNum();
				
				//用料计划确认率
				schMatConRate = 0;
				if(dataList.get(i).getShouldAccSchNum() > 0) {
					schMatConRate = 100 * ((float) dataList.get(i).getConMatSchNum() / (float) dataList.get(i).getShouldAccSchNum());
					BigDecimal bd = new BigDecimal(schMatConRate);
					schMatConRate = bd.setScale(2, BigDecimal.ROUND_HALF_UP).floatValue();
					if (schMatConRate > 100)
						schMatConRate = 100;
				}
				row.createCell(5).setCellValue(schMatConRate + "%");                                     //确认率
				
				
				
				row.createCell(6).setCellValue(dataList.get(i).getTotalMatPlanNum());                                    //用料计划总数
				totalMatPlanNum += dataList.get(i).getTotalMatPlanNum(); 
				row.createCell(7).setCellValue(dataList.get(i).getConMatPlanNum());                                      //已确认用料计划
				conMatPlanNum += dataList.get(i).getConMatPlanNum();
				row.createCell(8).setCellValue(dataList.get(i).getNoConMatPlanNum());                                    //未确认用料计划
				noConMatPlanNum += dataList.get(i).getNoConMatPlanNum();
				row.createCell(9).setCellValue(dataList.get(i).getMatConRate() + "%");                                   //确认率
			}
			//合计全市
			totalDishRate = 0;
			if(totalMatPlanNum > 0) {
				totalDishRate = 100 * ((float) conMatPlanNum / (float) totalMatPlanNum);
				BigDecimal bd = new BigDecimal(totalDishRate);
				totalDishRate = bd.setScale(2, BigDecimal.ROUND_HALF_UP).floatValue();
				if (totalDishRate > 100) {
					totalDishRate = 100;
					totalMatPlanNum = conMatPlanNum;
				}
			}
			
			//合计全市
			totalSchMatConRate = 0;
			if(totalDishSchNum > 0) {
				totalSchMatConRate = 100 * ((float) conMatPlanNum / (float) totalDishSchNum);
				BigDecimal bd = new BigDecimal(totalSchMatConRate);
				totalSchMatConRate = bd.setScale(2, BigDecimal.ROUND_HALF_UP).floatValue();
				if (totalSchMatConRate > 100) {
					totalSchMatConRate = 100;
				}
			}
			
			//创建合计一行
			startRowIdx += dataList.size();
			row = (Row) sheet.createRow(startRowIdx);
			for (int i = 0; i < colNames.length; i++) {
				cell = row.createCell(i);
				if(i == 0)
					cell.setCellValue(totalDishDate);
				else if(i == 1)
					cell.setCellValue(totalDistName);
				else if(i == 2)
					cell.setCellValue(totalDishSchNum);
				else if(i == 3)
					cell.setCellValue(totalConMatSchNum);
				else if(i == 4)
					cell.setCellValue(totalNoConMatSchNum);
				else if(i == 5)
					cell.setCellValue(totalSchMatConRate + "%");
				else if(i == 6)
					cell.setCellValue(totalMatPlanNum);
				else if(i == 7)
					cell.setCellValue(conMatPlanNum);
				else if(i == 8)
					cell.setCellValue(noConMatPlanNum);
				else if(i == 9) 
					cell.setCellValue(totalDishRate + "%");
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
					ByteArrayOutputStream os = new ByteArrayOutputStream();wb.write(os);FtpUtil.ftpServer(pathFileName, os,repFileResPath);
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
	
	//导出excell
	boolean exportExcell(String pathFileName, List<MatConfirms> dataList,Map<String,DepartmentObj> deparmentMap, String schSelMode) {
		boolean retFlag = true;
		int curSchSelMode = -1;
		if(schSelMode == null)
			schSelMode = "1";
		curSchSelMode = Integer.parseInt(schSelMode);
		//筛选学校模式
		if(curSchSelMode == 0) {    //按主管部门
			retFlag = expMatConfirmsExcelByCompDep(pathFileName, dataList, colNames0);
		}
		else if(curSchSelMode == 1) {  //按所在地
			retFlag = expMatConfirmsExcelByLocality(pathFileName, dataList, colNames1);
		}else if(curSchSelMode == 2) {  //按管理部门
			retFlag = expMatConfirmsExcelByDepartment(pathFileName, dataList,deparmentMap, colNames2);
		}     	
		
		return retFlag;
	}
	
	//导出用料确认列表模型函数
	public ExpMatConfirmsDTO appModFunc(String token, String startDate, String endDate, 
			String schSelMode, String subLevel, String compDep, String subDistName, 
			String distName, String prefCity, String province, 
			String subLevels,String compDeps,String distNames,String deaprtmentIds,
			Db1Service db1Service, Db2Service db2Service,DbHiveMatService dbHiveMatService) {
		ExpMatConfirmsDTO epdlDto = null;
		if (isRealData) { // 真实数据
			String strCurPageNum = String.valueOf(curPageNum), strPageSize = String.valueOf(pageSize);
			if (startDate == null || endDate == null) {   // 按照当天日期获取数据
				startDate = BCDTimeUtil.convertNormalDate(null);
				endDate = startDate;
			}
			MatConfirmsDTO pdlDto = epdlAppMod.appModFunc(token, startDate, endDate, 
					schSelMode, subLevel, compDep, subDistName, distName, prefCity, province, 
					subLevels,compDeps,distNames,deaprtmentIds,
					strCurPageNum, strPageSize, db1Service, db2Service,dbHiveMatService);
			if(pdlDto != null) {
				int i, totalCount = pdlDto.getPageInfo().getPageTotal();
				int pageCount = 0;
				List<MatConfirms> expExcelList = new ArrayList<>();
				if(totalCount % pageSize == 0)
					pageCount = totalCount/pageSize;
				else
					pageCount = totalCount/pageSize + 1;
				//第一页数据
				if(pdlDto.getMatConfirms() != null) {
					expExcelList.addAll(pdlDto.getMatConfirms());			
				}
				//后续页数据
				for(i = curPageNum+1; i <= pageCount; i++) {
					strCurPageNum = String.valueOf(i);
					MatConfirmsDTO curPdlDto = epdlAppMod.appModFunc(token, startDate, endDate, 
							schSelMode, subLevel, compDep, subDistName, distName, prefCity, province, 
							subLevels,compDeps,distNames,deaprtmentIds,
							strCurPageNum, strPageSize, db1Service, db2Service,dbHiveMatService);
					if(curPdlDto.getMatConfirms() != null) {
						expExcelList.addAll(curPdlDto.getMatConfirms());
					}
				}
				//生成导出EXCEL文件
				String repFileName = repFileResPath + UniqueIdGen.uuid() + SpringConfig.repFileFormats[SpringConfig.curRepFileFrmtIdx];
				String pathFileName = SpringConfig.base_dir + repFileName;
				logger.info("导出文件路径：" + pathFileName);
				List<DepartmentObj> deparmentList =  db1Service.getDepartmentObjList(new DepartmentObj(),null, -1, -1);	
				Map<String,DepartmentObj> deparmentMap = deparmentList.stream().collect(Collectors.toMap(DepartmentObj::getDepartmentId,(b)->b));
				boolean flag = exportExcell(pathFileName, expExcelList,deparmentMap, schSelMode);
				if(flag) {
					//移动文件到其他目录
					//AppModConfig.moveFileToOtherFolder(pathFileName, SpringConfig.tomcatSrvDirs[1] + repFileResPath);
					epdlDto = new ExpMatConfirmsDTO();
					ExpMatConfirms expMatConfirms = new ExpMatConfirms();
					//时戳
					epdlDto.setTime(BCDTimeUtil.convertNormalFrom(null));
					//导出信息
					expMatConfirms.setStartDate(startDate);
					expMatConfirms.setEndDate(endDate);
					expMatConfirms.setDistName(distName);
					expMatConfirms.setPrefCity(prefCity);
					expMatConfirms.setProvince(province);
					expFileUrl = SpringConfig.repfile_srvdn + repFileName;
					logger.info("导出文件URL：" + expFileUrl);
					expMatConfirms.setExpFileUrl(expFileUrl);
					epdlDto.setExpMatConfirms(expMatConfirms);
					//消息ID
					epdlDto.setMsgId(AppModConfig.msgId);
					AppModConfig.msgId++;
					// 消息id小于0判断
					AppModConfig.msgIdLessThan0Judge();
				}
			}
		} else { // 模拟数据
			// 模拟数据函数
			epdlDto = SimuDataFunc();
		}

		return epdlDto;
	}
}
