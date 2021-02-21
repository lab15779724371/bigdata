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
import com.tfit.BdBiProcSrvShEduOmc.dto.im.ExpDishRetSamples;
import com.tfit.BdBiProcSrvShEduOmc.dto.im.ExpDishRetSamplesDTO;
import com.tfit.BdBiProcSrvShEduOmc.obj.base.DepartmentObj;
import com.tfit.BdBiProcSrvShEduOmc.dto.im.DishRetSamples;
import com.tfit.BdBiProcSrvShEduOmc.dto.im.DishRetSamplesDTO;
import com.tfit.BdBiProcSrvShEduOmc.service.Db1Service;
import com.tfit.BdBiProcSrvShEduOmc.service.Db2Service;
import com.tfit.BdBiProcSrvShEduOmc.util.BCDTimeUtil;
import com.tfit.BdBiProcSrvShEduOmc.util.UniqueIdGen;

//导出菜品留样列表应用模型
public class ExpDishRetSamplesAppMod {
	private static final Logger logger = LogManager.getLogger(ExpDishRetSamplesAppMod.class.getName());
	
	//菜品留样列表应用模型
	private DishRetSamplesAppMod edrsAppMod = new DishRetSamplesAppMod();
	
	//是否为真实数据标识
	private static boolean isRealData = true;
	//页号、页大小和总页数
	int curPageNum = 1, pageTotal = 1, pageSize = AppModConfig.maxPageSize;
	//报表文件资源路径
	String repFileResPath = "/expDishRetSamples/";
	//导出列名数组
	String[] colNames0 = {"序号","就餐日期", "所属", "主管部门", "应留样学校食堂", "未留样学校食堂", "已留样学校食堂","学校食堂留样率",
			"规范录入","规范排菜率","补录","逾期补录","无数据", "应留样菜品数", "已留样菜品", "未留样菜品", "菜品留样率"};
	String[] colNames1 = {"序号","就餐日期", "所在地", "应留样学校食堂", "未留样学校食堂", "已留样学校食堂", "学校食堂留样率",
			"规范录入","规范排菜率","补录","逾期补录","无数据","应留样菜品数", "已留样菜品", "未留样菜品", "菜品留样率"};	
	String[] colNames2 = {"序号","就餐日期", "管理部门", "应留样学校食堂", "已留样学校食堂", "未留样学校食堂", "学校食堂留样率",
			"规范录入","规范排菜率","补录","逾期补录","无数据","应留样菜品数", "已留样菜品", "未留样菜品", "菜品留样率"};
	//变量数据初始化
	String repastStartDate = "2018-09-03";
	String repasEndDate = "2018-09-04";
	String distName = null;
	String prefCity = null;
	String province = "上海市";
	String expFileUrl = "fc8bafe943214d65a67a7d8b93d0185a.xls";
	
	//模拟数据函数
	private ExpDishRetSamplesDTO SimuDataFunc() {
		ExpDishRetSamplesDTO edrsDto = new ExpDishRetSamplesDTO();
		//设置返回数据
		edrsDto.setTime(BCDTimeUtil.convertNormalFrom(null));
		//列表元素设置
		ExpDishRetSamples expDishRetSamples = new ExpDishRetSamples();
		//赋值
		expDishRetSamples.setRepastStartDate(repastStartDate);
		expDishRetSamples.setRepasEndDate(repasEndDate);
		expDishRetSamples.setDistName(distName);
		expDishRetSamples.setPrefCity(prefCity);
		expDishRetSamples.setProvince(province);
		expDishRetSamples.setExpFileUrl(SpringConfig.repfile_srvdn+repFileResPath+expFileUrl);
		//设置模拟数据
		edrsDto.setExpDishRetSamples(expDishRetSamples);
		//消息ID
		edrsDto.setMsgId(AppModConfig.msgId);
		AppModConfig.msgId++;
		//消息id小于0判断
		AppModConfig.msgIdLessThan0Judge();

		return edrsDto;
	}
	
	//生成导出EXCEL文件按主管部门
	public boolean expDishRetSamplesExcelByCompDep(String pathFileName, List<DishRetSamples> dataList, String colNames[]) { 
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
			int totalDishSchNum = 0, totalNoRsSchNum = 0, totalRsSchNum = 0, totalMatPlanNum = 0, conMatPlanNum = 0, noConMatPlanNum = 0;
			int totalShouldRsSchNum = 0;
			float totalDishRate = (float) 0.0;
			float totalSchRsRate = (float) 0.0;
			int columIndex = 0;
			String subLevel = "";
			String compDep = "";
			
			//1 表示规范录入
			int totalStandardNum = 0;
			//2 表示补录
			int totalSupplementNum = 0;
			//3 表示逾期补录
			int totalBeOverdueNum = 0;
			//4 表示无数据
			int totalNoDataNum = 0;
			float standardRate = (float) 0.0;//规范排菜率
			
			for (int i = 0; i < dataList.size(); i++) {
				columIndex = 0;
				row = (Row) sheet.createRow(i + startRowIdx);
				row.createCell(columIndex ++ ).setCellValue(i + 1);                                                                   //序号
				row.createCell(columIndex ++ ).setCellValue(dataList.get(i).getRepastDate());                                        //就餐日期
				subLevel = dataList.get(i).getSubLevel();
				if(subLevel !=null && subLevel.indexOf(",")>=0) {
					subLevel = subLevel.substring(subLevel.indexOf(",")+1, subLevel.length());
				}
				row.createCell(columIndex ++ ).setCellValue(subLevel);                                                                //所属
				compDep = dataList.get(i).getCompDep();
				if(compDep !=null && compDep.indexOf(",")>=0) {
					compDep = compDep.substring(compDep.indexOf(",")+1, compDep.length());
				}
				row.createCell(columIndex ++ ).setCellValue(compDep);                                                                 //主管部门
				//row.createCell(columIndex ++ ).setCellValue(dataList.get(i).getDishSchNum());                                       //已排菜学校
				//totalDishSchNum += dataList.get(i).getDishSchNum();
				row.createCell(columIndex ++ ).setCellValue(dataList.get(i).getShouldRsSchNum());                                     //应留样学校
				totalShouldRsSchNum += dataList.get(i).getShouldRsSchNum();
				row.createCell(columIndex ++ ).setCellValue(dataList.get(i).getRsSchNum());                                           //已留样学校	
				totalRsSchNum += dataList.get(i).getRsSchNum();
				row.createCell(columIndex ++ ).setCellValue(dataList.get(i).getNoRsSchNum());                                         //未留样学校
				totalNoRsSchNum += dataList.get(i).getNoRsSchNum();
				row.createCell(columIndex ++ ).setCellValue(dataList.get(i).getSchRsRate() + "%");                                    //学校留样率
				
				row.createCell(columIndex++).setCellValue(dataList.get(i).getSchStandardNum());                                     //规范录入
				totalStandardNum += dataList.get(i).getSchStandardNum();
				row.createCell(columIndex++).setCellValue(dataList.get(i).getSchStandardRate() + "%");                              //规范排菜率
				row.createCell(columIndex++).setCellValue(dataList.get(i).getSchSupplementNum());                                  //补录
				totalSupplementNum += dataList.get(i).getSchSupplementNum();
				row.createCell(columIndex++).setCellValue(dataList.get(i).getSchBeOverdueNum());                                  //逾期补录
				totalBeOverdueNum += dataList.get(i).getSchBeOverdueNum();
				row.createCell(columIndex++).setCellValue(dataList.get(i).getSchNoDataNum());                                  //无数据
				totalNoDataNum += dataList.get(i).getSchNoDataNum();
				
				row.createCell(columIndex ++ ).setCellValue(dataList.get(i).getMenuNum());                                            //菜品数量
				totalMatPlanNum += dataList.get(i).getMenuNum(); 
				row.createCell(columIndex ++ ).setCellValue(dataList.get(i).getRsMenuNum());                                          //已留样菜品
				conMatPlanNum += dataList.get(i).getRsMenuNum();
				row.createCell(columIndex ++ ).setCellValue(dataList.get(i).getNoRsMenuNum());                                        //未留样菜品
				noConMatPlanNum += dataList.get(i).getNoRsMenuNum();
				row.createCell(columIndex ++ ).setCellValue(dataList.get(i).getRsRate() + "%");                                       //留样率
			}
			//合计全市学校留样率
			totalSchRsRate = 0;
			if(totalShouldRsSchNum > 0) {
				totalSchRsRate = 100 * ((float) totalRsSchNum / (float) totalShouldRsSchNum);
				BigDecimal bd = new BigDecimal(totalSchRsRate);
				totalSchRsRate = bd.setScale(2, BigDecimal.ROUND_HALF_UP).floatValue();
				if (totalSchRsRate > 100) {
					totalSchRsRate = 100;
				} 
			}
			
			//合计全市菜品留样率
			totalDishRate = 0;
			if(totalMatPlanNum > 0) {
				totalDishRate = 100 * ((float) conMatPlanNum / (float) totalMatPlanNum);
				BigDecimal bd = new BigDecimal(totalDishRate);
				totalDishRate = bd.setScale(2, BigDecimal.ROUND_HALF_UP).floatValue();
				if (totalDishRate > 100) {
					totalDishRate = 100;
				}
			}
			
			standardRate = 0;
			if(totalShouldRsSchNum > 0) {
				standardRate = 100 * ((float) totalStandardNum / (float) totalShouldRsSchNum);
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
				if(i == 0) {
					cell.setCellValue(startRowIdx);
				}else if(i == 1) {
					cell.setCellValue(totalDishDate);
				}else if(i == 2 || i == 3) {
					cell.setCellValue(totalDistName);
				}else if(i == 4) {
					cell.setCellValue(totalShouldRsSchNum);
				}else if(i == 5) {
					cell.setCellValue(totalRsSchNum);
				}else if(i == 6) {
					cell.setCellValue(totalNoRsSchNum);
				}else if(i == 7) {
					cell.setCellValue(totalSchRsRate + "%");
					
				}else if(i == 8) {
					cell.setCellValue(totalStandardNum);
				}else if(i == 9) {
					cell.setCellValue(standardRate + "%");
				}else if(i == 10) {
					cell.setCellValue(totalSupplementNum);
				}else if(i == 11) {
					cell.setCellValue(totalBeOverdueNum);
				}else if(i == 12) {
					cell.setCellValue(totalNoDataNum);
					
				}else if(i == 13) {
					cell.setCellValue(totalMatPlanNum);
				}else if(i == 14) {
					cell.setCellValue(conMatPlanNum);
				}else if(i == 15) {
					cell.setCellValue(noConMatPlanNum);
				}else if(i == 16)  {
					cell.setCellValue(totalDishRate + "%");
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
	public boolean expDishRetSamplesExcelByLocality(String pathFileName, List<DishRetSamples> dataList, String colNames[]) { 
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
			for (int i = 0; i < colNames.length; i++) {
				cell = row.createCell(i);
				try {
					logger.info(colNames[i] + " ");
					colVals[i] = new String(colNames[i].getBytes(), "utf-8");
					cell.setCellValue(colNames[i]);
				} catch (UnsupportedEncodingException e) {
					// TODO 自动生成的 catch 块
					e.printStackTrace();
				}
			}
			// 循环写入行数据
			startRowIdx++;
			String totalDishDate = "合计", totalDistName = "---";
			int totalDishSchNum = 0, totalNoRsSchNum = 0, totalRsSchNum = 0, totalMatPlanNum = 0, conMatPlanNum = 0, noConMatPlanNum = 0;
			int totalShouldRsSchNum =0;
			float totalDishRate = (float) 0.0;
			//学校验收率
			float totalSchRsRate = (float) 0.0;
			
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
			for (int i = 0; i < dataList.size(); i++) {
				columIndex = 0;
				row = (Row) sheet.createRow(i + startRowIdx);
				row.createCell(columIndex++).setCellValue(i+1);                                         //序号
				row.createCell(columIndex++).setCellValue(dataList.get(i).getRepastDate());                                         //就餐日期
				row.createCell(columIndex++).setCellValue(AppModConfig.distIdToNameMap.get(dataList.get(i).getDistName()));         //所在地				
				//row.createCell(columIndex++).setCellValue(dataList.get(i).getDishSchNum());                                       //已排菜学校
				//totalDishSchNum += dataList.get(i).getDishSchNum();
				row.createCell(columIndex++).setCellValue(dataList.get(i).getShouldRsSchNum());                                     //应留样学校
				totalShouldRsSchNum += dataList.get(i).getShouldRsSchNum();
				row.createCell(columIndex++).setCellValue(dataList.get(i).getRsSchNum());                                           //已留样学校	
				totalRsSchNum += dataList.get(i).getRsSchNum();
				row.createCell(columIndex++).setCellValue(dataList.get(i).getNoRsSchNum());                                         //未留样学校
				totalNoRsSchNum += dataList.get(i).getNoRsSchNum();
				row.createCell(columIndex++).setCellValue(dataList.get(i).getSchRsRate() + "%");                                    //学校留样率
				
				row.createCell(columIndex++).setCellValue(dataList.get(i).getSchStandardNum());                                     //规范录入
				totalStandardNum += dataList.get(i).getSchStandardNum();
				row.createCell(columIndex++).setCellValue(dataList.get(i).getSchStandardRate() + "%");                              //规范排菜率
				row.createCell(columIndex++).setCellValue(dataList.get(i).getSchSupplementNum());                                  //补录
				totalSupplementNum += dataList.get(i).getSchSupplementNum();
				row.createCell(columIndex++).setCellValue(dataList.get(i).getSchBeOverdueNum());                                  //逾期补录
				totalBeOverdueNum += dataList.get(i).getSchBeOverdueNum();
				row.createCell(columIndex++).setCellValue(dataList.get(i).getSchNoDataNum());                                  //无数据
				totalNoDataNum += dataList.get(i).getSchNoDataNum();
				
				row.createCell(columIndex++).setCellValue(dataList.get(i).getMenuNum());                                            //菜品数量
				totalMatPlanNum += dataList.get(i).getMenuNum(); 
				row.createCell(columIndex++).setCellValue(dataList.get(i).getRsMenuNum());                                          //已留样菜品
				conMatPlanNum += dataList.get(i).getRsMenuNum();
				row.createCell(columIndex++).setCellValue(dataList.get(i).getNoRsMenuNum());                                        //未留样菜品
				noConMatPlanNum += dataList.get(i).getNoRsMenuNum();
				row.createCell(columIndex++).setCellValue(dataList.get(i).getRsRate() + "%");                                       //留样率
			}
			//合计全市学校留样率
			totalSchRsRate = 0;
			if(totalShouldRsSchNum > 0) {
				totalSchRsRate = 100 * ((float) totalRsSchNum / (float) totalShouldRsSchNum);
				BigDecimal bd = new BigDecimal(totalSchRsRate);
				totalSchRsRate = bd.setScale(2, BigDecimal.ROUND_HALF_UP).floatValue();
				if (totalSchRsRate > 100) {
					totalSchRsRate = 100;
				}
			}
			
			//合计全市菜品留样率
			totalDishRate = 0;
			if(totalMatPlanNum > 0) {
				totalDishRate = 100 * ((float) conMatPlanNum / (float) totalMatPlanNum);
				BigDecimal bd = new BigDecimal(totalDishRate);
				totalDishRate = bd.setScale(2, BigDecimal.ROUND_HALF_UP).floatValue();
				if (totalDishRate > 100) {
					totalDishRate = 100;
				}
			}
			
			standardRate = 0;
			if(totalShouldRsSchNum > 0) {
				standardRate = 100 * ((float) totalStandardNum / (float) totalShouldRsSchNum);
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
					cell.setCellValue(startRowIdx);
				if(i == 1)
					cell.setCellValue(totalDishDate);
				else if(i == 2)
					cell.setCellValue(totalDistName);				
				else if(i == 3)
					cell.setCellValue(totalShouldRsSchNum);
				else if(i == 4)
					cell.setCellValue(totalRsSchNum);
				else if(i == 5)
					cell.setCellValue(totalNoRsSchNum);
				else if(i == 6)
					cell.setCellValue(totalSchRsRate + "%");
				
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
				
				else if(i == 12)
					cell.setCellValue(totalMatPlanNum);
				else if(i == 13)
					cell.setCellValue(conMatPlanNum);
				else if(i == 14)
					cell.setCellValue(noConMatPlanNum);
				else if(i == 15) 
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

			logger.info("stream:"+stream);
			//if (stream != null) {
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
			/*} else
				retFlag = false;*/
        }
        
        return retFlag;
    }
	
	//生成导出EXCEL文件按所在地
	public boolean expDishRetSamplesExcelByDepartment(String pathFileName, List<DishRetSamples> dataList, Map<String,DepartmentObj> deparmentMap, String colNames[]) { 
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
			for (int i = 0; i < colNames.length; i++) {
				cell = row.createCell(i);
				try {
					logger.info(colNames[i] + " ");
					colVals[i] = new String(colNames[i].getBytes(), "utf-8");
					cell.setCellValue(colNames[i]);
				} catch (UnsupportedEncodingException e) {
					// TODO 自动生成的 catch 块
					e.printStackTrace();
				}
			}
			// 循环写入行数据
			startRowIdx++;
			String totalDishDate = "合计", totalDistName = "---";
			int totalDishSchNum = 0, totalNoRsSchNum = 0, totalRsSchNum = 0, totalMatPlanNum = 0, conMatPlanNum = 0, noConMatPlanNum = 0;
			int totalShouldRsSchNum =0;
			float totalDishRate = (float) 0.0;
			//学校验收率
			float totalSchRsRate = (float) 0.0;
			
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
			for (int i = 0; i < dataList.size(); i++) {
				columIndex = 0;
				//{"序号","就餐日期", "管理部门", "应留样学校食堂", "已留样学校食堂", "未留样学校食堂", "学校食堂留样率","规范录入","规范排菜率","补录","逾期补录","无数据","应留样菜品数", "已留样菜品", "未留样菜品", "菜品留样率"};
				row = (Row) sheet.createRow(i + startRowIdx);
				row.createCell(columIndex++).setCellValue(i+1);                                         //序号
				row.createCell(columIndex++).setCellValue(dataList.get(i).getRepastDate());                                         //就餐日期
				row.createCell(columIndex++).setCellValue(deparmentMap.get(dataList.get(i).getDepartmentId())==null?"":deparmentMap.get(dataList.get(i).getDepartmentId()).getDepartmentName());         //管理部门				
				//row.createCell(columIndex++).setCellValue(dataList.get(i).getDishSchNum());                                       //已排菜学校
				//totalDishSchNum += dataList.get(i).getDishSchNum();
				row.createCell(columIndex++).setCellValue(dataList.get(i).getShouldRsSchNum());                                     //应留样学校
				totalShouldRsSchNum += dataList.get(i).getShouldRsSchNum();
				row.createCell(columIndex++).setCellValue(dataList.get(i).getRsSchNum());                                           //已留样学校	
				totalRsSchNum += dataList.get(i).getRsSchNum();
				row.createCell(columIndex++).setCellValue(dataList.get(i).getNoRsSchNum());                                         //未留样学校
				totalNoRsSchNum += dataList.get(i).getNoRsSchNum();
				row.createCell(columIndex++).setCellValue(dataList.get(i).getSchRsRate() + "%");                                    //学校留样率
				
				row.createCell(columIndex++).setCellValue(dataList.get(i).getSchStandardNum());                                     //规范录入
				totalStandardNum += dataList.get(i).getSchStandardNum();
				row.createCell(columIndex++).setCellValue(dataList.get(i).getSchStandardRate() + "%");                              //规范排菜率
				row.createCell(columIndex++).setCellValue(dataList.get(i).getSchSupplementNum());                                  //补录
				totalSupplementNum += dataList.get(i).getSchSupplementNum();
				row.createCell(columIndex++).setCellValue(dataList.get(i).getSchBeOverdueNum());                                  //逾期补录
				totalBeOverdueNum += dataList.get(i).getSchBeOverdueNum();
				row.createCell(columIndex++).setCellValue(dataList.get(i).getSchNoDataNum());                                  //无数据
				totalNoDataNum += dataList.get(i).getSchNoDataNum();
				
				row.createCell(columIndex++).setCellValue(dataList.get(i).getMenuNum());                                            //菜品数量
				totalMatPlanNum += dataList.get(i).getMenuNum(); 
				row.createCell(columIndex++).setCellValue(dataList.get(i).getRsMenuNum());                                          //已留样菜品
				conMatPlanNum += dataList.get(i).getRsMenuNum();
				row.createCell(columIndex++).setCellValue(dataList.get(i).getNoRsMenuNum());                                        //未留样菜品
				noConMatPlanNum += dataList.get(i).getNoRsMenuNum();
				row.createCell(columIndex++).setCellValue(dataList.get(i).getRsRate() + "%");                                       //留样率
			}
			//合计全市菜品留样率
			totalSchRsRate = 0;
			if(totalShouldRsSchNum > 0) {
				totalSchRsRate = 100 * ((float) totalRsSchNum / (float) totalShouldRsSchNum);
				BigDecimal bd = new BigDecimal(totalSchRsRate);
				totalSchRsRate = bd.setScale(2, BigDecimal.ROUND_HALF_UP).floatValue();
				if (totalSchRsRate > 100) {
					totalSchRsRate = 100;
				}
			}
			
			//合计全市学校留样率
			totalDishRate = 0;
			if(totalMatPlanNum > 0) {
				totalDishRate = 100 * ((float) conMatPlanNum / (float) totalMatPlanNum);
				BigDecimal bd = new BigDecimal(totalDishRate);
				totalDishRate = bd.setScale(2, BigDecimal.ROUND_HALF_UP).floatValue();
				if (totalDishRate > 100) {
					totalDishRate = 100;
				}
			}
			
			standardRate = 0;
			if(totalShouldRsSchNum > 0) {
				standardRate = 100 * ((float) totalStandardNum / (float) totalShouldRsSchNum);
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
					cell.setCellValue(startRowIdx);
				if(i == 1)
					cell.setCellValue(totalDishDate);
				else if(i == 2)
					cell.setCellValue(totalDistName);				
				else if(i == 3)
					cell.setCellValue(totalShouldRsSchNum);
				else if(i == 4)
					cell.setCellValue(totalRsSchNum);
				else if(i == 5)
					cell.setCellValue(totalNoRsSchNum);
				else if(i == 6)
					cell.setCellValue(totalSchRsRate+ "%");
				
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
				
				else if(i == 12)
					cell.setCellValue(totalMatPlanNum);
				else if(i == 13)
					cell.setCellValue(conMatPlanNum);
				else if(i == 14)
					cell.setCellValue(noConMatPlanNum);
				else if(i == 15) 
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

			logger.info("stream:"+stream);
			//if (stream != null) {
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
			/*} else
				retFlag = false;*/
        }
        
        return retFlag;
    }
	
	//导出excell
	boolean exportExcell(String pathFileName, List<DishRetSamples> dataList,Map<String,DepartmentObj> deparmentMap,  String schSelMode) {
		boolean retFlag = true;
		int curSchSelMode = -1;
		if(schSelMode == null)
			schSelMode = "1";
		curSchSelMode = Integer.parseInt(schSelMode);
		//筛选学校模式
		if(curSchSelMode == 0) {    //按主管部门
			retFlag = expDishRetSamplesExcelByCompDep(pathFileName, dataList, colNames0);
		}
		else if(curSchSelMode == 1) {  //按所在地
			retFlag = expDishRetSamplesExcelByLocality(pathFileName, dataList, colNames1);
		}else if(curSchSelMode == 2) {  //按管理部门
			retFlag = expDishRetSamplesExcelByDepartment(pathFileName, dataList,deparmentMap, colNames2);
		}       	
		
		return retFlag;
	}
	
	//导出菜品留样列表模型函数
	public ExpDishRetSamplesDTO appModFunc(String token, String repastStartDate, String repasEndDate, String schSelMode, String subLevel, String compDep,
			String subDistName, String distName, String prefCity, String province, String subLevels,String compDeps,String distNames,
			String departmentIds,
			Db1Service db1Service, Db2Service db2Service) {
		ExpDishRetSamplesDTO edrsDto = null;
		if (isRealData) { // 真实数据
			String strCurPageNum = String.valueOf(curPageNum), strPageSize = String.valueOf(pageSize);
			if (repastStartDate == null || repasEndDate == null) {   // 按照当天日期获取数据
				repastStartDate = BCDTimeUtil.convertNormalDate(null);
				repasEndDate = repastStartDate;
			}
			DishRetSamplesDTO pdlDto = edrsAppMod.appModFunc(token, repastStartDate, repasEndDate, schSelMode, subLevel, compDep, subDistName,
					distName, prefCity, province, subLevels,compDeps,distNames,
					departmentIds,
					strCurPageNum, strPageSize, db1Service, db2Service);
			if(pdlDto != null) {
				int i, totalCount = pdlDto.getPageInfo().getPageTotal();
				int pageCount = 0;
				List<DishRetSamples> expExcelList = new ArrayList<>();
				if(totalCount % pageSize == 0)
					pageCount = totalCount/pageSize;
				else
					pageCount = totalCount/pageSize + 1;
				//第一页数据
				if(pdlDto.getDishRetSamples() != null) {
					expExcelList.addAll(pdlDto.getDishRetSamples());			
				}
				//后续页数据
				for(i = curPageNum+1; i <= pageCount; i++) {
					strCurPageNum = String.valueOf(i);
					DishRetSamplesDTO curPdlDto = edrsAppMod.appModFunc(token, repastStartDate, repasEndDate, schSelMode, 
							subLevel, compDep, subDistName, distName, prefCity, province,subLevels,compDeps,distNames,
							departmentIds,
							strCurPageNum, strPageSize, db1Service, db2Service);
					if(curPdlDto.getDishRetSamples() != null) {
						expExcelList.addAll(curPdlDto.getDishRetSamples());
					}
				}
				
				List<DepartmentObj> deparmentList =  db1Service.getDepartmentObjList(new DepartmentObj(),null, -1, -1);	
				Map<String,DepartmentObj> deparmentMap = deparmentList.stream().collect(Collectors.toMap(DepartmentObj::getDepartmentId,(b)->b));
				
				//生成导出EXCEL文件
				String repFileName = repFileResPath + UniqueIdGen.uuid() + SpringConfig.repFileFormats[SpringConfig.curRepFileFrmtIdx];
				String pathFileName = SpringConfig.base_dir + repFileName;
				logger.info("导出文件路径：" + pathFileName);
				boolean flag = exportExcell(pathFileName, expExcelList, deparmentMap,schSelMode);
				if(flag) {
					//移动文件到其他目录
					////AppModConfig.moveFileToOtherFolder(pathFileName, SpringConfig.tomcatSrvDirs[1] + repFileResPath);
					edrsDto = new ExpDishRetSamplesDTO();
					ExpDishRetSamples expDishRetSamples = new ExpDishRetSamples();
					//时戳
					edrsDto.setTime(BCDTimeUtil.convertNormalFrom(null));
					//导出信息
					expDishRetSamples.setRepastStartDate(repastStartDate);
					expDishRetSamples.setRepasEndDate(repasEndDate);
					expDishRetSamples.setDistName(distName);
					expDishRetSamples.setPrefCity(prefCity);
					expDishRetSamples.setProvince(province);
					expFileUrl = SpringConfig.repfile_srvdn + repFileName;
					logger.info("导出文件URL：" + expFileUrl);
					expDishRetSamples.setExpFileUrl(expFileUrl);
					edrsDto.setExpDishRetSamples(expDishRetSamples);
					//消息ID
					edrsDto.setMsgId(AppModConfig.msgId);
					AppModConfig.msgId++;
					// 消息id小于0判断
					AppModConfig.msgIdLessThan0Judge();
				}
			}
		} else { // 模拟数据
			// 模拟数据函数
			edrsDto = SimuDataFunc();
		}

		return edrsDto;
	}

}