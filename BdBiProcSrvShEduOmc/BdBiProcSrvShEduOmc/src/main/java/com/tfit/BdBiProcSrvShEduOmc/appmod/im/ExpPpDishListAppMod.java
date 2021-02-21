package com.tfit.BdBiProcSrvShEduOmc.appmod.im;

import java.io.ByteArrayOutputStream;import java.io.File;import com.tfit.BdBiProcSrvShEduOmc.util.FtpUtil;
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
import com.tfit.BdBiProcSrvShEduOmc.dto.im.ExpPpDishList;
import com.tfit.BdBiProcSrvShEduOmc.dto.im.ExpPpDishListDTO;
import com.tfit.BdBiProcSrvShEduOmc.dto.im.PpDishList;
import com.tfit.BdBiProcSrvShEduOmc.dto.im.PpDishListDTO;
import com.tfit.BdBiProcSrvShEduOmc.obj.base.DepartmentObj;
import com.tfit.BdBiProcSrvShEduOmc.service.Db1Service;
import com.tfit.BdBiProcSrvShEduOmc.service.Db2Service;
import com.tfit.BdBiProcSrvShEduOmc.service.DbHiveDishService;
import com.tfit.BdBiProcSrvShEduOmc.util.BCDTimeUtil;
import com.tfit.BdBiProcSrvShEduOmc.util.UniqueIdGen;

//导出项目点排菜列表应用模型
public class ExpPpDishListAppMod {
	private static final Logger logger = LogManager.getLogger(ExpPpDishListAppMod.class.getName());
	
	//项目点排菜列表应用模型
	private PpDishListAppMod epdlAppMod = new PpDishListAppMod();
	
	//是否为真实数据标识
	private static boolean isRealData = true;
	//页号、页大小和总页数
	int curPageNum = 1, pageTotal = 1, pageSize = AppModConfig.maxPageSize;
	//报表文件资源路径
	String repFileResPath = "/expPpDishList/";
	//导出列名数组
	String[] colNames0 = {"排菜日期", "所属", "主管部门", "学校食堂数", "应排菜学校食堂数","未排菜学校食堂数", "已排菜学校食堂数",  "排菜率","规范录入","规范排菜率","补录","逾期补录","无数据"};	
	String[] colNames1 = {"排菜日期", "所在地", "学校食堂数", "应排菜学校食堂数","未排菜学校食堂数", "已排菜学校食堂数",  "排菜率","规范录入","规范排菜率","补录","逾期补录","无数据"};	
	String[] colNames2 = {"排菜日期", "管理部门", "应排菜学校食堂", "已排菜学校食堂", "未排菜学校食堂", "排菜率","规范录入","规范排菜率","补录","逾期补录","无数据"};
	
	//变量数据初始化
	String startDate = "2018-09-03";
	String endDate = "2018-09-04";
	String distName = null;
	String prefCity = null;
	String province = "上海市";
	String expFileUrl = "test1.txt";
	
	//模拟数据函数
	private ExpPpDishListDTO SimuDataFunc() {
		ExpPpDishListDTO epdlDto = new ExpPpDishListDTO();
		//设置返回数据
		epdlDto.setTime(BCDTimeUtil.convertNormalFrom(null));
		//列表元素设置
		ExpPpDishList expExpPpDishList = new ExpPpDishList();
		//赋值
		expExpPpDishList.setStartDate(startDate);
		expExpPpDishList.setEndDate(endDate);
		expExpPpDishList.setDistName(distName);
		expExpPpDishList.setPrefCity(prefCity);
		expExpPpDishList.setProvince(province);
		expExpPpDishList.setExpFileUrl(SpringConfig.repfile_srvdn+repFileResPath+expFileUrl);
		//设置模拟数据
		epdlDto.setExpPpDishList(expExpPpDishList);
		//消息ID
		epdlDto.setMsgId(AppModConfig.msgId);
		AppModConfig.msgId++;
		//消息id小于0判断
		AppModConfig.msgIdLessThan0Judge();

		return epdlDto;
	}
	
	//生成导出EXCEL文件按主管部门
	public boolean expPpDishListExcelByCompDep(String pathFileName, List<PpDishList> dataList, String colNames[]) { 
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
			int totalRegSchNum = 0, totalMealSchNum = 0, totalDishSchNum = 0, totalNoDishSchNum = 0;
			float totalDishRate = (float) 0.0;
			//1 表示规范录入
			int totalStandardNum = 0;
			//2 表示补录
			int totalSupplementNum = 0;
			//3 表示逾期补录
			int totalBeOverdueNum = 0;
			//4 表示无数据
			int totalNoDataNum = 0;
			float standardRate = (float) 0.0;//规范排菜率
			
			int columIndex = 0;
			String subLevel = "";
			String compDep = "";
			for (int i = 0; i < dataList.size(); i++) {
				columIndex = 0;
				row = (Row) sheet.createRow(i + startRowIdx);
				row.createCell(columIndex++).setCellValue(dataList.get(i).getDishDate());                                      //排菜周期
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
				
				row.createCell(columIndex++).setCellValue(dataList.get(i).getRegSchNum());                                     //学校数量
				totalRegSchNum += dataList.get(i).getRegSchNum();
				row.createCell(columIndex++).setCellValue(dataList.get(i).getMealSchNum());                                    //供餐天数
				totalMealSchNum += dataList.get(i).getMealSchNum(); 
				row.createCell(columIndex++).setCellValue(dataList.get(i).getNoDishSchNum());                                  //未排菜天数
				totalNoDishSchNum += dataList.get(i).getNoDishSchNum();
				row.createCell(columIndex++).setCellValue(dataList.get(i).getDishSchNum());                                    //排菜天数
				totalDishSchNum += dataList.get(i).getDishSchNum();
				row.createCell(columIndex++).setCellValue(dataList.get(i).getDishRate() + "%");                                //排菜率
				
				row.createCell(columIndex++).setCellValue(dataList.get(i).getStandardNum());                                     //规范录入
				totalStandardNum += dataList.get(i).getStandardNum();
				row.createCell(columIndex++).setCellValue(dataList.get(i).getStandardRate() + "%");                              //规范排菜率
				row.createCell(columIndex++).setCellValue(dataList.get(i).getSupplementNum());                                  //补录
				totalSupplementNum += dataList.get(i).getSupplementNum();
				row.createCell(columIndex++).setCellValue(dataList.get(i).getBeOverdueNum());                                  //逾期补录
				totalBeOverdueNum += dataList.get(i).getBeOverdueNum();
				row.createCell(columIndex++).setCellValue(dataList.get(i).getNoDataNum());                                  //无数据
				totalNoDataNum += dataList.get(i).getNoDataNum();
				
			}
			//合计全市排菜率
			totalDishRate = 0;
			if(totalMealSchNum > 0) {
				totalDishRate = 100 * ((float) totalDishSchNum / (float) totalMealSchNum);
				BigDecimal bd = new BigDecimal(totalDishRate);
				totalDishRate = bd.setScale(2, BigDecimal.ROUND_HALF_UP).floatValue();
				if (totalDishRate > 100) {
					totalDishRate = 100;
					totalMealSchNum = totalDishSchNum;
				}
			}
			
			standardRate = 0;
			if(totalMealSchNum > 0) {
				standardRate = 100 * ((float) totalStandardNum / (float) totalMealSchNum);
				BigDecimal bd = new BigDecimal(standardRate);
				standardRate = bd.setScale(2, BigDecimal.ROUND_HALF_UP).floatValue();
				if (standardRate > 100)
					standardRate = 100;
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
					cell.setCellValue(totalRegSchNum);
				else if(i == 4)
					cell.setCellValue(totalMealSchNum);
				else if(i == 5)
					cell.setCellValue(totalNoDishSchNum);
				else if(i == 6)
					cell.setCellValue(totalDishSchNum);
				else if(i == 7) 
					cell.setCellValue(totalDishRate + "%");
				else if(i == 8)
					cell.setCellValue(totalStandardNum);
				else if(i == 9)
					cell.setCellValue(standardRate + "%");
				else if(i == 10)
					cell.setCellValue(totalSupplementNum);
				else if(i == 11)
					cell.setCellValue(totalBeOverdueNum);
				else if(i == 12)
					cell.setCellValue(totalNoDataNum);
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
	public boolean expPpDishListExcelByLocality(String pathFileName, List<PpDishList> dataList, String colNames[]) { 
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
			int totalRegSchNum = 0, totalMealSchNum = 0, totalDishSchNum = 0, totalNoDishSchNum = 0;
			//1 表示规范录入
			int totalStandardNum = 0;
			//2 表示补录
			int totalSupplementNum = 0;
			//3 表示逾期补录
			int totalBeOverdueNum = 0;
			//4 表示无数据
			int totalNoDataNum = 0;
			float standardRate = (float) 0.0;//规范排菜率
			float totalDishRate = (float) 0.0;
			for (int i = 0; i < dataList.size(); i++) {
				row = (Row) sheet.createRow(i + startRowIdx);
				row.createCell(0).setCellValue(dataList.get(i).getDishDate());                                      //排菜周期
				row.createCell(1).setCellValue(AppModConfig.distIdToNameMap.get(dataList.get(i).getDistName()));    //区			
				row.createCell(2).setCellValue(dataList.get(i).getRegSchNum());                                     //学校数量
				totalRegSchNum += dataList.get(i).getRegSchNum();
				row.createCell(3).setCellValue(dataList.get(i).getMealSchNum());                                    //供餐天数
				totalMealSchNum += dataList.get(i).getMealSchNum(); 
				row.createCell(4).setCellValue(dataList.get(i).getNoDishSchNum());                                  //未排菜天数
				totalNoDishSchNum += dataList.get(i).getNoDishSchNum();
				row.createCell(5).setCellValue(dataList.get(i).getDishSchNum());                                    //排菜天数
				totalDishSchNum += dataList.get(i).getDishSchNum();
				row.createCell(6).setCellValue(dataList.get(i).getDishRate() + "%");                                //排菜率
				
				row.createCell(7).setCellValue(dataList.get(i).getStandardNum());                                     //规范录入
				totalStandardNum += dataList.get(i).getStandardNum();
				row.createCell(8).setCellValue(dataList.get(i).getStandardRate() + "%");                              //规范排菜率
				row.createCell(9).setCellValue(dataList.get(i).getSupplementNum());                                  //补录
				totalSupplementNum += dataList.get(i).getSupplementNum();
				row.createCell(10).setCellValue(dataList.get(i).getBeOverdueNum());                                  //逾期补录
				totalBeOverdueNum += dataList.get(i).getBeOverdueNum();
				row.createCell(11).setCellValue(dataList.get(i).getNoDataNum());                                  //无数据
				totalNoDataNum += dataList.get(i).getNoDataNum();
				
			}
			//合计全市排菜率
			totalDishRate = 0;
			if(totalMealSchNum > 0) {
				totalDishRate = 100 * ((float) totalDishSchNum / (float) totalMealSchNum);
				BigDecimal bd = new BigDecimal(totalDishRate);
				totalDishRate = bd.setScale(2, BigDecimal.ROUND_HALF_UP).floatValue();
				if (totalDishRate > 100) {
					totalDishRate = 100;
					totalMealSchNum = totalDishSchNum;
				}
			}
			
			standardRate = 0;
			if(totalMealSchNum > 0) {
				standardRate = 100 * ((float) totalStandardNum / (float) totalMealSchNum);
				BigDecimal bd = new BigDecimal(standardRate);
				standardRate = bd.setScale(2, BigDecimal.ROUND_HALF_UP).floatValue();
				if (standardRate > 100)
					standardRate = 100;
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
					cell.setCellValue(totalRegSchNum);
				else if(i == 3)
					cell.setCellValue(totalMealSchNum);
				else if(i == 4)
					cell.setCellValue(totalNoDishSchNum);
				else if(i == 5)
					cell.setCellValue(totalDishSchNum);
				else if(i == 6) 
					cell.setCellValue(totalDishRate + "%");
				else if(i == 7)
					cell.setCellValue(totalStandardNum);
				else if(i == 8)
					cell.setCellValue(standardRate + "%");
				else if(i == 9)
					cell.setCellValue(totalSupplementNum);
				else if(i == 10)
					cell.setCellValue(totalBeOverdueNum);
				else if(i == 11)
					cell.setCellValue(totalNoDataNum);
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
	public boolean expPpDishListExcelByDepartment(String pathFileName, List<PpDishList> dataList, Map<String,DepartmentObj> deparmentMap,String colNames[]) { 
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
			int totalMealSchNum = 0;
			int totalDishSchNum = 0;
			int totalNoDishSchNum = 0;
			//1 表示规范录入
			int totalStandardNum = 0;
			//2 表示补录
			int totalSupplementNum = 0;
			//3 表示逾期补录
			int totalBeOverdueNum = 0;
			//4 表示无数据
			int totalNoDataNum = 0;
			float standardRate = (float) 0.0;//规范排菜率
			
			float totalDishRate = (float) 0.0;
			
			
			int columIndex = 0;
			//{"排菜日期", "管理部门", "应排菜学校食堂", "已排菜学校食堂", "未排菜学校食堂", "排菜率","规范录入","规范排菜率","补录","逾期补录","无数据"};
			for (int i = 0; i < dataList.size(); i++) {
				columIndex = 0;
				row = (Row) sheet.createRow(i + startRowIdx);
				row.createCell(columIndex++).setCellValue(dataList.get(i).getDishDate());                                      //排菜日期
				row.createCell(columIndex++).setCellValue(deparmentMap.get(dataList.get(i).getDepartmentId())==null?"":deparmentMap.get(dataList.get(i).getDepartmentId()).getDepartmentName());    //管理部门			
				row.createCell(columIndex++).setCellValue(dataList.get(i).getMealSchNum());                                     //应排菜学校食堂
				totalMealSchNum += dataList.get(i).getMealSchNum();
				row.createCell(columIndex++).setCellValue(dataList.get(i).getDishSchNum());                                    //已排菜学校食堂
				totalDishSchNum += dataList.get(i).getDishSchNum(); 
				row.createCell(columIndex++).setCellValue(dataList.get(i).getNoDishSchNum());                                  //未排菜学校食堂
				totalNoDishSchNum += dataList.get(i).getNoDishSchNum();
				row.createCell(columIndex++).setCellValue(dataList.get(i).getDishRate() + "%");                                //排菜率
				
				row.createCell(columIndex++).setCellValue(dataList.get(i).getStandardNum());                                     //规范录入
				totalStandardNum += dataList.get(i).getStandardNum();
				row.createCell(columIndex++).setCellValue(dataList.get(i).getStandardRate() + "%");                              //规范排菜率
				row.createCell(columIndex++).setCellValue(dataList.get(i).getSupplementNum());                                  //补录
				totalSupplementNum += dataList.get(i).getSupplementNum();
				row.createCell(columIndex++).setCellValue(dataList.get(i).getBeOverdueNum());                                  //逾期补录
				totalBeOverdueNum += dataList.get(i).getBeOverdueNum();
				row.createCell(columIndex++).setCellValue(dataList.get(i).getNoDataNum());                                  //无数据
				totalNoDataNum += dataList.get(i).getNoDataNum();
				
			}
			//合计全市排菜率
			totalDishRate = 0;
			if(totalMealSchNum > 0) {
				totalDishRate = 100 * ((float) totalDishSchNum / (float) totalMealSchNum);
				BigDecimal bd = new BigDecimal(totalDishRate);
				totalDishRate = bd.setScale(2, BigDecimal.ROUND_HALF_UP).floatValue();
				if (totalDishRate > 100) {
					totalDishRate = 100;
					totalMealSchNum = totalDishSchNum;
				}
			}
			
			standardRate = 0;
			if(totalMealSchNum > 0) {
				standardRate = 100 * ((float) totalStandardNum / (float) totalMealSchNum);
				BigDecimal bd = new BigDecimal(standardRate);
				standardRate = bd.setScale(2, BigDecimal.ROUND_HALF_UP).floatValue();
				if (standardRate > 100)
					standardRate = 100;
			}
			
			//{"排菜日期", "管理部门", "应排菜学校食堂", "已排菜学校食堂", "未排菜学校食堂", "排菜率","规范录入","规范排菜率","补录","逾期补录","无数据"};
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
					cell.setCellValue(totalMealSchNum);
				else if(i == 3)
					cell.setCellValue(totalDishSchNum);
				else if(i == 4)
					cell.setCellValue(totalNoDishSchNum);
				else if(i == 5) 
					cell.setCellValue(totalDishRate + "%");
				else if(i == 6)
					cell.setCellValue(totalStandardNum);
				else if(i == 7)
					cell.setCellValue(standardRate + "%");
				else if(i == 8)
					cell.setCellValue(totalSupplementNum);
				else if(i == 9)
					cell.setCellValue(totalBeOverdueNum);
				else if(i == 10)
					cell.setCellValue(totalNoDataNum);
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
	boolean exportExcell(String pathFileName, List<PpDishList> dataList,Map<String,DepartmentObj> deparmentMap, String schSelMode) {
		boolean retFlag = true;
		int curSchSelMode = -1;
		if(schSelMode == null)
			schSelMode = "1";
		curSchSelMode = Integer.parseInt(schSelMode);
		//筛选学校模式
		if(curSchSelMode == 0) {    //按主管部门
			retFlag = expPpDishListExcelByCompDep(pathFileName, dataList, colNames0);
		}else if(curSchSelMode == 1) {  //按所在地
			retFlag = expPpDishListExcelByLocality(pathFileName, dataList, colNames1);
		}else if(curSchSelMode == 2) {  //按管理部门
			retFlag = expPpDishListExcelByDepartment(pathFileName, dataList,deparmentMap, colNames2);
		}    	
		
		return retFlag;
	}
	
	//导出项目点排菜列表模型函数
	public ExpPpDishListDTO appModFunc(String token, String startDate, String endDate, String schSelMode, 
			String subLevel, String compDep, String subDistName, String distName,String departmentId, String prefCity, 
			String province,String subLevels,String compDeps,String distNames,String departmentIds,
			Db1Service db1Service, Db2Service db2Service,DbHiveDishService dbHiveDishService) {
		ExpPpDishListDTO epdlDto = null;
		if (isRealData) { // 真实数据
			String strCurPageNum = String.valueOf(curPageNum), strPageSize = String.valueOf(pageSize);
			if (startDate == null || endDate == null) {   // 按照当天日期获取数据
				startDate = BCDTimeUtil.convertNormalDate(null);
				endDate = startDate;
			}
			PpDishListDTO pdlDto = epdlAppMod.appModFunc(token, startDate, endDate, schSelMode, 
					subLevel, compDep, subDistName, distName,departmentId, prefCity, province, 
					subLevels,compDeps,distNames,departmentIds,
					strCurPageNum, strPageSize, db1Service, db2Service,dbHiveDishService);
			if(pdlDto != null) {
				int i, totalCount = pdlDto.getPageInfo().getPageTotal();
				int pageCount = 0;
				List<PpDishList> expExcelList = new ArrayList<>();
				if(totalCount % pageSize == 0)
					pageCount = totalCount/pageSize;
				else
					pageCount = totalCount/pageSize + 1;
				//第一页数据
				if(pdlDto.getPpDishList() != null) {
					expExcelList.addAll(pdlDto.getPpDishList());			
				}
				//后续页数据
				for(i = curPageNum+1; i <= pageCount; i++) {
					strCurPageNum = String.valueOf(i);
					PpDishListDTO curPdlDto = epdlAppMod.appModFunc(token, startDate, schSelMode, 
							subLevel, compDep, subDistName, endDate, distName,departmentId, prefCity, province, 
							subLevels,compDeps,distNames,departmentIds,
							strCurPageNum, strPageSize, db1Service, db2Service,dbHiveDishService);
					if(curPdlDto.getPpDishList() != null) {
						expExcelList.addAll(curPdlDto.getPpDishList());
					}
				}
				
				List<DepartmentObj> deparmentList =  db1Service.getDepartmentObjList(new DepartmentObj(),null, -1, -1);	
				Map<String,DepartmentObj> deparmentMap = deparmentList.stream().collect(Collectors.toMap(DepartmentObj::getDepartmentId,(b)->b));
				
				//生成导出EXCEL文件
				String repFileName = repFileResPath + UniqueIdGen.uuid() + SpringConfig.repFileFormats[SpringConfig.curRepFileFrmtIdx];
				String pathFileName = SpringConfig.base_dir + repFileName;
				logger.info("导出文件路径：" + pathFileName);
				boolean flag = exportExcell(pathFileName, expExcelList,deparmentMap, schSelMode);
				if(flag) {
					//移动文件到其他目录
					//AppModConfig.moveFileToOtherFolder(pathFileName, SpringConfig.tomcatSrvDirs[1] + repFileResPath);
					epdlDto = new ExpPpDishListDTO();
					ExpPpDishList expPpDishList = new ExpPpDishList();
					//时戳
					epdlDto.setTime(BCDTimeUtil.convertNormalFrom(null));
					//导出信息
					expPpDishList.setStartDate(startDate);
					expPpDishList.setEndDate(endDate);
					expPpDishList.setDistName(distName);
					expPpDishList.setPrefCity(prefCity);
					expPpDishList.setProvince(province);
					expFileUrl = SpringConfig.repfile_srvdn + repFileName;
					logger.info("导出文件URL：" + expFileUrl);
					expPpDishList.setExpFileUrl(expFileUrl);
					epdlDto.setExpPpDishList(expPpDishList);
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
