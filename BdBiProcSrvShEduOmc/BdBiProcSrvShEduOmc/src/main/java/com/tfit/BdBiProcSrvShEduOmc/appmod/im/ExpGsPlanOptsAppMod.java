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
import com.tfit.BdBiProcSrvShEduOmc.dto.im.ExpGsPlanOpts;
import com.tfit.BdBiProcSrvShEduOmc.dto.im.ExpGsPlanOptsDTO;
import com.tfit.BdBiProcSrvShEduOmc.dto.im.GsPlanOpts;
import com.tfit.BdBiProcSrvShEduOmc.dto.im.GsPlanOptsDTO;
import com.tfit.BdBiProcSrvShEduOmc.obj.base.DepartmentObj;
import com.tfit.BdBiProcSrvShEduOmc.service.Db1Service;
import com.tfit.BdBiProcSrvShEduOmc.service.Db2Service;
import com.tfit.BdBiProcSrvShEduOmc.service.DbHiveGsService;
import com.tfit.BdBiProcSrvShEduOmc.util.BCDTimeUtil;
import com.tfit.BdBiProcSrvShEduOmc.util.UniqueIdGen;

//导出配货计划操作列表应用模型
public class ExpGsPlanOptsAppMod {
	private static final Logger logger = LogManager.getLogger(ExpGsPlanOptsAppMod.class.getName());
	
	//配货计划操作列表应用模型
	private GsPlanOptsAppMod gpoAppMod = new GsPlanOptsAppMod();
	
	//是否为真实数据标识
	private static boolean isRealData = true;
	//页号、页大小和总页数
	int curPageNum = 1, pageTotal = 1, pageSize = AppModConfig.maxPageSize;
	//报表文件资源路径
	String repFileResPath = "/expGsPlanOpts/";
	//导出列名数组
	String[] colNames0 = {"序号","配货日期", "所属", "主管部门", "应验收学校食堂","已验收学校食堂", "未验收学校食堂", "汇总验收率", 
			"规范录入","规范验收率","补录","逾期补录","无数据",
			"配货计划总数","未验收", "已验收", "验收率",
			"已指派", "指派率",};	
	String[] colNames1 = {"序号","配货日期", "所在地", "应验收学校食堂","已验收学校食堂", "未验收学校食堂", "汇总验收率", 
			"规范录入","规范验收率","补录","逾期补录","无数据",
			"配货计划总数","未验收", "已验收", "验收率",
			"已指派", "指派率"};	
	String[] colNames2 = {"序号","配货日期", "管理部门", "应验收学校食堂","已验收学校食堂", "未验收学校食堂", "汇总验收率",
			"规范录入","规范验收率","补录","逾期补录","无数据",
			"配货计划总数","未验收", "已验收", "验收率", "已指派", "指派率"};
	//变量数据初始化
	String startDate = "2018-09-03";
	String endDate = "2018-09-04";
	String distName = null;
	String prefCity = null;
	String province = "上海市";
	String expFileUrl = "test1.txt";
	
	//模拟数据函数
	private ExpGsPlanOptsDTO SimuDataFunc() {
		ExpGsPlanOptsDTO egpoDto = new ExpGsPlanOptsDTO();
		//设置返回数据
		egpoDto.setTime(BCDTimeUtil.convertNormalFrom(null));
		//列表元素设置
		ExpGsPlanOpts expExpGsPlanOpts = new ExpGsPlanOpts();
		//赋值
		expExpGsPlanOpts.setStartDate(startDate);
		expExpGsPlanOpts.setEndDate(endDate);
		expExpGsPlanOpts.setDistName(distName);
		expExpGsPlanOpts.setPrefCity(prefCity);
		expExpGsPlanOpts.setProvince(province);
		expExpGsPlanOpts.setExpFileUrl(SpringConfig.repfile_srvdn+repFileResPath+expFileUrl);
		//设置模拟数据
		egpoDto.setExpGsPlanOpts(expExpGsPlanOpts);
		//消息ID
		egpoDto.setMsgId(AppModConfig.msgId);
		AppModConfig.msgId++;
		//消息id小于0判断
		AppModConfig.msgIdLessThan0Judge();

		return egpoDto;
	}
	
	//生成导出EXCEL文件按主管部门
	public boolean expGsPlanOptsExcelByCompDep(String pathFileName, List<GsPlanOpts> dataList, String colNames[]) { 
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
			String totalDistrDate = "合计", totalDistName = "---";
			int totalTotalGsPlanNum = 0, totalAssignGsPlanNum = 0, totalDispGsPlanNum = 0, totalAcceptGsPlanNum = 0, totalNoAcceptGsPlanNum = 0;
			//应验收学校总数
			int totalShouldAcceptSchNum = 0;
			//未验收学校总数
			int totalNoAcceptSchNum = 0;
			//已验收学校总数
			int totalAcceptSchNum = 0;
			float totalAssignRate = (float) 0.0, totalDispRate = (float) 0.0, totalAcceptRate = (float) 0.0;
			//全市总学校验收率
			float totalSchAcceptRate = (float) 0.0;
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
				
				/*String[] colNames0 = {"序号","配货日期", "所属", "主管部门", "应验收学校食堂","已验收学校食堂", "未验收学校食堂", "汇总验收率",
						"规范录入","规范验收率","补录","逾期补录","无数据",
						"配货计划总数","未验收", "已验收", "验收率",
						"已指派", "指派率",};	*/
				
				columIndex = 0;
				row = (Row) sheet.createRow(i + startRowIdx);
				row.createCell(columIndex++).setCellValue(i+1);                                      //序号
				row.createCell(columIndex++).setCellValue(dataList.get(i).getDistrDate());                                      //配货日期
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
				//row.createCell(columIndex++).setCellValue(dataList.get(i).getDishSchNum());                                     //已排菜学校
				//row.createCell(columIndex++).setCellValue(dataList.get(i).getConSchNum());                                      //已确认学校
				row.createCell(columIndex++).setCellValue(dataList.get(i).getShouldAcceptSchNum());                               //应验收学校
				totalShouldAcceptSchNum += dataList.get(i).getShouldAcceptSchNum();
				row.createCell(columIndex++).setCellValue(dataList.get(i).getAcceptSchNum());                                     //已验收学校
				totalAcceptSchNum += dataList.get(i).getAcceptSchNum();
				row.createCell(columIndex++).setCellValue(dataList.get(i).getNoAcceptSchNum());                                   //未验收学校
				totalNoAcceptSchNum += dataList.get(i).getNoAcceptSchNum();
				row.createCell(columIndex++).setCellValue(dataList.get(i).getSchAcceptRate() + "%");                              //学校验收率
				
				row.createCell(columIndex++).setCellValue(dataList.get(i).getSchStandardNum());                                     //规范录入
				totalStandardNum += dataList.get(i).getSchStandardNum();
				row.createCell(columIndex++).setCellValue(dataList.get(i).getSchStandardRate() + "%");                              //规范排菜率
				row.createCell(columIndex++).setCellValue(dataList.get(i).getSchSupplementNum());                                  //补录
				totalSupplementNum += dataList.get(i).getSchSupplementNum();
				row.createCell(columIndex++).setCellValue(dataList.get(i).getSchBeOverdueNum());                                  //逾期补录
				totalBeOverdueNum += dataList.get(i).getSchBeOverdueNum();
				row.createCell(columIndex++).setCellValue(dataList.get(i).getSchNoDataNum());                                  //无数据
				totalNoDataNum += dataList.get(i).getSchNoDataNum();
				
				row.createCell(columIndex++).setCellValue(dataList.get(i).getTotalGsPlanNum());                                   //配货计划总数
				totalTotalGsPlanNum += dataList.get(i).getTotalGsPlanNum();
				row.createCell(columIndex++).setCellValue(dataList.get(i).getNoAcceptGsPlanNum());                               //未验收数
				totalNoAcceptGsPlanNum += dataList.get(i).getNoAcceptGsPlanNum();
				row.createCell(columIndex++).setCellValue(dataList.get(i).getAcceptGsPlanNum());                               //已验收数
				totalAcceptGsPlanNum += dataList.get(i).getAcceptGsPlanNum();
				row.createCell(columIndex++).setCellValue(dataList.get(i).getAcceptRate() + "%");                              //验收率
				
				row.createCell(columIndex++).setCellValue(dataList.get(i).getAssignGsPlanNum());                                //已指派数
				totalAssignGsPlanNum += dataList.get(i).getAssignGsPlanNum();
				row.createCell(columIndex++).setCellValue(dataList.get(i).getAssignRate() + "%");                               //指派率
				
			}
			//合计全市已指派率
			totalAssignRate = 0;
			if(totalTotalGsPlanNum > 0) {
				totalAssignRate = 100 * ((float) totalAssignGsPlanNum / (float) totalTotalGsPlanNum);
				BigDecimal bd = new BigDecimal(totalAssignRate);
				totalAssignRate = bd.setScale(2, BigDecimal.ROUND_HALF_UP).floatValue();
				if (totalAssignRate > 100) {
					totalAssignRate = 100;
					totalTotalGsPlanNum = totalAssignGsPlanNum;
				}
			}
			//合计全市已配送率
			totalDispRate = 0;
			if(totalTotalGsPlanNum > 0) {
				totalDispRate = 100 * ((float) totalDispGsPlanNum / (float) totalTotalGsPlanNum);
				BigDecimal bd = new BigDecimal(totalDispRate);
				totalDispRate = bd.setScale(2, BigDecimal.ROUND_HALF_UP).floatValue();
				if (totalDispRate > 100) {
					totalDispRate = 100;
					totalTotalGsPlanNum = totalDispGsPlanNum;
				}
			}
			//合计全市已验收率
			totalAcceptRate = 0;
			if(totalTotalGsPlanNum > 0) {
				totalAcceptRate = 100 * ((float) totalAcceptGsPlanNum / (float) totalTotalGsPlanNum);
				BigDecimal bd = new BigDecimal(totalAcceptRate);
				totalAcceptRate = bd.setScale(2, BigDecimal.ROUND_HALF_UP).floatValue();
				if (totalAcceptRate > 100) {
					totalAcceptRate = 100;
					totalTotalGsPlanNum = totalAcceptGsPlanNum;
				}
			}
			//合计全市学校验收率
			totalSchAcceptRate = 0;
			if(totalShouldAcceptSchNum > 0) {
				totalSchAcceptRate = 100 * ((float) totalAcceptSchNum / (float) totalShouldAcceptSchNum);
				BigDecimal bd = new BigDecimal(totalSchAcceptRate);
				totalSchAcceptRate = bd.setScale(2, BigDecimal.ROUND_HALF_UP).floatValue();
				if (totalSchAcceptRate > 100) {
					totalSchAcceptRate = 100;
					totalTotalGsPlanNum = totalAcceptGsPlanNum;
				}
			}
			
			standardRate = 0;
			if(totalShouldAcceptSchNum > 0) {
				standardRate = 100 * ((float) totalStandardNum / (float) totalShouldAcceptSchNum);
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
				else if(i == 1)
					cell.setCellValue(totalDistrDate);
				else if(i == 2 || i == 3)
					cell.setCellValue(totalDistName);
				else if(i == 4)
					cell.setCellValue(totalShouldAcceptSchNum);
				else if(i == 5)
					cell.setCellValue(totalAcceptSchNum);
				else if(i == 6)
					cell.setCellValue(totalNoAcceptSchNum);
				else if(i == 7)
					cell.setCellValue(totalSchAcceptRate + "%");
				
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
				
				else if(i == 13)
					cell.setCellValue(totalTotalGsPlanNum);
				else if(i == 14)
					cell.setCellValue(totalNoAcceptGsPlanNum);
				else if(i == 15)
					cell.setCellValue(totalAcceptGsPlanNum);
				else if(i == 16)
					cell.setCellValue(totalAcceptRate + "%");
				else if(i == 17)
					cell.setCellValue(totalAssignGsPlanNum);
				else if(i == 18)
					cell.setCellValue(totalAssignRate + "%");
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
	
	//生成导出EXCEL文件
	public boolean expGsPlanOptsExcelByLocality(String pathFileName, List<GsPlanOpts> dataList, String colNames[]) { 
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
			String totalDistrDate = "合计", totalDistName = "---";
			int totalTotalGsPlanNum = 0, totalAssignGsPlanNum = 0, totalDispGsPlanNum = 0, totalAcceptGsPlanNum = 0, totalNoAcceptGsPlanNum = 0;
			int totalShouldAcceptSchNum = 0;
			int totalNoAcceptSchNum = 0;
			int totalAcceptSchNum = 0;
			float totalAssignRate = (float) 0.0, totalDispRate = (float) 0.0, totalAcceptRate = (float) 0.0;
			//学校验收率
			int columIndex = 0;
			float totalSchAcceptRate = (float) 0.0;
			
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
				
				/*String[] colNames1 = {"序号","配货日期", "所在地", "应验收学校食堂","已验收学校食堂", "未验收学校食堂", "汇总验收率", 
						"规范录入","规范验收率","补录","逾期补录","无数据",
						"配货计划总数","未验收", "已验收", "验收率",
						"已指派", "指派率"};*/
				
				columIndex = 0;
				row = (Row) sheet.createRow(i + startRowIdx);
				row.createCell(columIndex++).setCellValue(i+1);                                     //序号
				row.createCell(columIndex++).setCellValue(dataList.get(i).getDistrDate());                                     //配货日期
				row.createCell(columIndex++).setCellValue(AppModConfig.distIdToNameMap.get(dataList.get(i).getDistName()));    //所在地	
				//row.createCell(columIndex++).setCellValue(dataList.get(i).getDishSchNum());                                    //已排菜学校
				//row.createCell(columIndex++).setCellValue(dataList.get(i).getConSchNum());                                     //已确认学校
				row.createCell(columIndex++).setCellValue(dataList.get(i).getShouldAcceptSchNum());                               //应验收学校
				totalShouldAcceptSchNum += dataList.get(i).getShouldAcceptSchNum();
				row.createCell(columIndex++).setCellValue(dataList.get(i).getAcceptSchNum());                                     //已验收学校
				totalAcceptSchNum += dataList.get(i).getAcceptSchNum();
				row.createCell(columIndex++).setCellValue(dataList.get(i).getNoAcceptSchNum());                                   //未验收学校
				totalNoAcceptSchNum += dataList.get(i).getNoAcceptSchNum();
				row.createCell(columIndex++).setCellValue(dataList.get(i).getSchAcceptRate() + "%");                              //学校验收率
				
				row.createCell(columIndex++).setCellValue(dataList.get(i).getSchStandardNum());                                     //规范录入
				totalStandardNum += dataList.get(i).getSchStandardNum();
				row.createCell(columIndex++).setCellValue(dataList.get(i).getSchStandardRate() + "%");                              //规范排菜率
				row.createCell(columIndex++).setCellValue(dataList.get(i).getSchSupplementNum());                                  //补录
				totalSupplementNum += dataList.get(i).getSchSupplementNum();
				row.createCell(columIndex++).setCellValue(dataList.get(i).getSchBeOverdueNum());                                  //逾期补录
				totalBeOverdueNum += dataList.get(i).getSchBeOverdueNum();
				row.createCell(columIndex++).setCellValue(dataList.get(i).getSchNoDataNum());                                  //无数据
				totalNoDataNum += dataList.get(i).getSchNoDataNum();
				
				row.createCell(columIndex++).setCellValue(dataList.get(i).getTotalGsPlanNum());                                //配货计划总数
				totalTotalGsPlanNum += dataList.get(i).getTotalGsPlanNum();
				row.createCell(columIndex++).setCellValue(dataList.get(i).getNoAcceptGsPlanNum());                               //未验收数
				totalNoAcceptGsPlanNum += dataList.get(i).getNoAcceptGsPlanNum();
				row.createCell(columIndex++).setCellValue(dataList.get(i).getAcceptGsPlanNum());                               //已验收数
				totalAcceptGsPlanNum += dataList.get(i).getAcceptGsPlanNum();
				row.createCell(columIndex++).setCellValue(dataList.get(i).getAcceptRate() + "%");                              //验收率
				
				row.createCell(columIndex++).setCellValue(dataList.get(i).getAssignGsPlanNum());                               //已指派数
				totalAssignGsPlanNum += dataList.get(i).getAssignGsPlanNum();
				row.createCell(columIndex++).setCellValue(dataList.get(i).getAssignRate() + "%");                              //指派率
			}
			//合计全市已指派率
			totalAssignRate = 0;
			if(totalTotalGsPlanNum > 0) {
				totalAssignRate = 100 * ((float) totalAssignGsPlanNum / (float) totalTotalGsPlanNum);
				BigDecimal bd = new BigDecimal(totalAssignRate);
				totalAssignRate = bd.setScale(2, BigDecimal.ROUND_HALF_UP).floatValue();
				if (totalAssignRate > 100) {
					totalAssignRate = 100;
					totalTotalGsPlanNum = totalAssignGsPlanNum;
				}
			}
			//合计全市已配送率
			totalDispRate = 0;
			if(totalTotalGsPlanNum > 0) {
				totalDispRate = 100 * ((float) totalDispGsPlanNum / (float) totalTotalGsPlanNum);
				BigDecimal bd = new BigDecimal(totalDispRate);
				totalDispRate = bd.setScale(2, BigDecimal.ROUND_HALF_UP).floatValue();
				if (totalDispRate > 100) {
					totalDispRate = 100;
					totalTotalGsPlanNum = totalDispGsPlanNum;
				}
			}
			//合计全市已验收率
			totalAcceptRate = 0;
			if(totalTotalGsPlanNum > 0) {
				totalAcceptRate = 100 * ((float) totalAcceptGsPlanNum / (float) totalTotalGsPlanNum);
				BigDecimal bd = new BigDecimal(totalAcceptRate);
				totalAcceptRate = bd.setScale(2, BigDecimal.ROUND_HALF_UP).floatValue();
				if (totalAcceptRate > 100) {
					totalAcceptRate = 100;
					totalTotalGsPlanNum = totalAcceptGsPlanNum;
				}
			}
			
			//合计全市学校验收率
			totalSchAcceptRate = 0;
			if(totalShouldAcceptSchNum > 0) {
				totalSchAcceptRate = 100 * ((float) totalAcceptSchNum / (float) totalShouldAcceptSchNum);
				BigDecimal bd = new BigDecimal(totalSchAcceptRate);
				totalSchAcceptRate = bd.setScale(2, BigDecimal.ROUND_HALF_UP).floatValue();
				if (totalSchAcceptRate > 100) {
					totalSchAcceptRate = 100;
					totalTotalGsPlanNum = totalAcceptGsPlanNum;
				}
			}
			
			standardRate = 0;
			if(totalShouldAcceptSchNum > 0) {
				standardRate = 100 * ((float) totalStandardNum / (float) totalShouldAcceptSchNum);
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
				else if(i == 1)
					cell.setCellValue(totalDistrDate);
				else if(i == 2)
					cell.setCellValue(totalDistName);
				else if(i == 3)
					cell.setCellValue(totalShouldAcceptSchNum);
				else if(i == 4)
					cell.setCellValue(totalAcceptSchNum);
				else if(i == 5)
					cell.setCellValue(totalNoAcceptSchNum);
				else if(i == 6)
					cell.setCellValue(totalSchAcceptRate + "%");
			
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
					cell.setCellValue(totalTotalGsPlanNum);
				else if(i == 13)
					cell.setCellValue(totalNoAcceptGsPlanNum);
				else if(i == 14)
					cell.setCellValue(totalAcceptGsPlanNum);
				else if(i == 15)
					cell.setCellValue(totalAcceptRate + "%");
				else if(i == 16)
					cell.setCellValue(totalAssignGsPlanNum);
				else if(i == 17)
					cell.setCellValue(totalAssignRate + "%");
				
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
	
	//生成导出EXCEL文件
	public boolean expGsPlanOptsExcelByDepartment(String pathFileName,Map<String,DepartmentObj> deparmentMap, List<GsPlanOpts> dataList, String colNames[]) { 
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
			String totalDistrDate = "合计", totalDistName = "---";
			int totalTotalGsPlanNum = 0, totalAssignGsPlanNum = 0, totalDispGsPlanNum = 0, totalAcceptGsPlanNum = 0, totalNoAcceptGsPlanNum = 0;
			int totalShouldAcceptSchNum = 0;
			int totalNoAcceptSchNum = 0;
			int totalAcceptSchNum = 0;
			float totalAssignRate = (float) 0.0, totalDispRate = (float) 0.0, totalAcceptRate = (float) 0.0;
			
			//1 表示规范录入
			int totalStandardNum = 0;
			//2 表示补录
			int totalSupplementNum = 0;
			//3 表示逾期补录
			int totalBeOverdueNum = 0;
			//4 表示无数据
			int totalNoDataNum = 0;
			
			//学校验收率
			int columIndex = 0;
			float totalSchAcceptRate = (float) 0.0;
			
			float standardRate = (float) 0.0;//规范排菜率
			for (int i = 0; i < dataList.size(); i++) {
				columIndex = 0;
				row = (Row) sheet.createRow(i + startRowIdx);
				row.createCell(columIndex++).setCellValue(i+1);                                     //序号
				row.createCell(columIndex++).setCellValue(dataList.get(i).getDistrDate());                                     //配货日期
				row.createCell(columIndex++).setCellValue(deparmentMap.get(dataList.get(i).getDepartmentId())==null?"":deparmentMap.get(dataList.get(i).getDepartmentId()).getDepartmentName());    //管理部门	
				row.createCell(columIndex++).setCellValue(dataList.get(i).getShouldAcceptSchNum());                               //应验收学校
				totalShouldAcceptSchNum += dataList.get(i).getShouldAcceptSchNum();
				row.createCell(columIndex++).setCellValue(dataList.get(i).getAcceptSchNum());                                     //已验收学校
				totalAcceptSchNum += dataList.get(i).getAcceptSchNum();
				row.createCell(columIndex++).setCellValue(dataList.get(i).getNoAcceptSchNum());                                   //未验收学校
				totalNoAcceptSchNum += dataList.get(i).getNoAcceptSchNum();
				row.createCell(columIndex++).setCellValue(dataList.get(i).getSchAcceptRate() + "%");                              //学校验收率
				
				row.createCell(columIndex++).setCellValue(dataList.get(i).getSchStandardNum());                                     //规范录入
				totalStandardNum += dataList.get(i).getSchStandardNum();
				row.createCell(columIndex++).setCellValue(dataList.get(i).getSchStandardRate() + "%");                              //规范排菜率
				row.createCell(columIndex++).setCellValue(dataList.get(i).getSchSupplementNum());                                  //补录
				totalSupplementNum += dataList.get(i).getSchSupplementNum();
				row.createCell(columIndex++).setCellValue(dataList.get(i).getSchBeOverdueNum());                                  //逾期补录
				totalBeOverdueNum += dataList.get(i).getSchBeOverdueNum();
				row.createCell(columIndex++).setCellValue(dataList.get(i).getSchNoDataNum());                                  //无数据
				totalNoDataNum += dataList.get(i).getSchNoDataNum();
				
				row.createCell(columIndex++).setCellValue(dataList.get(i).getTotalGsPlanNum());                                //配货计划总数
				totalTotalGsPlanNum += dataList.get(i).getTotalGsPlanNum();
				row.createCell(columIndex++).setCellValue(dataList.get(i).getNoAcceptGsPlanNum());                               //未验收数
				totalNoAcceptGsPlanNum += dataList.get(i).getNoAcceptGsPlanNum();
				row.createCell(columIndex++).setCellValue(dataList.get(i).getAcceptGsPlanNum());                               //已验收数
				totalAcceptGsPlanNum += dataList.get(i).getAcceptGsPlanNum();
				row.createCell(columIndex++).setCellValue(dataList.get(i).getAcceptRate() + "%");                              //验收率
				
				row.createCell(columIndex++).setCellValue(dataList.get(i).getAssignGsPlanNum());                               //已指派数
				totalAssignGsPlanNum += dataList.get(i).getAssignGsPlanNum();
				row.createCell(columIndex++).setCellValue(dataList.get(i).getAssignRate() + "%");                              //指派率
			}
			//合计全市已指派率
			totalAssignRate = 0;
			if(totalTotalGsPlanNum > 0) {
				totalAssignRate = 100 * ((float) totalAssignGsPlanNum / (float) totalTotalGsPlanNum);
				BigDecimal bd = new BigDecimal(totalAssignRate);
				totalAssignRate = bd.setScale(2, BigDecimal.ROUND_HALF_UP).floatValue();
				if (totalAssignRate > 100) {
					totalAssignRate = 100;
					totalTotalGsPlanNum = totalAssignGsPlanNum;
				}
			}
			//合计全市已配送率
			totalDispRate = 0;
			if(totalTotalGsPlanNum > 0) {
				totalDispRate = 100 * ((float) totalDispGsPlanNum / (float) totalTotalGsPlanNum);
				BigDecimal bd = new BigDecimal(totalDispRate);
				totalDispRate = bd.setScale(2, BigDecimal.ROUND_HALF_UP).floatValue();
				if (totalDispRate > 100) {
					totalDispRate = 100;
					totalTotalGsPlanNum = totalDispGsPlanNum;
				}
			}
			//合计全市已验收率
			totalAcceptRate = 0;
			if(totalTotalGsPlanNum > 0) {
				totalAcceptRate = 100 * ((float) totalAcceptGsPlanNum / (float) totalTotalGsPlanNum);
				BigDecimal bd = new BigDecimal(totalAcceptRate);
				totalAcceptRate = bd.setScale(2, BigDecimal.ROUND_HALF_UP).floatValue();
				if (totalAcceptRate > 100) {
					totalAcceptRate = 100;
					totalTotalGsPlanNum = totalAcceptGsPlanNum;
				}
			}
			
			//合计全市学校验收率
			totalSchAcceptRate = 0;
			if(totalShouldAcceptSchNum > 0) {
				totalSchAcceptRate = 100 * ((float) totalAcceptSchNum / (float) totalShouldAcceptSchNum);
				BigDecimal bd = new BigDecimal(totalSchAcceptRate);
				totalSchAcceptRate = bd.setScale(2, BigDecimal.ROUND_HALF_UP).floatValue();
				if (totalSchAcceptRate > 100) {
					totalSchAcceptRate = 100;
					totalTotalGsPlanNum = totalAcceptGsPlanNum;
				}
			}
			
			standardRate = 0;
			if(totalShouldAcceptSchNum > 0) {
				standardRate = 100 * ((float) totalStandardNum / (float) totalShouldAcceptSchNum);
				BigDecimal bd = new BigDecimal(standardRate);
				standardRate = bd.setScale(2, BigDecimal.ROUND_HALF_UP).floatValue();
				if (standardRate > 100)
					standardRate = 100;
			}
			
					//"配货计划总数","未验收", "已验收", "验收率", "已指派", "指派率"};*/
			
			//创建合计一行
			startRowIdx += dataList.size();
			row = (Row) sheet.createRow(startRowIdx);
			for (int i = 0; i < colNames.length; i++) {
				cell = row.createCell(i);
				if(i == 0)
					cell.setCellValue(startRowIdx);
				else if(i == 1)
					cell.setCellValue(totalDistrDate);
				else if(i == 2)
					cell.setCellValue(totalDistName);
				else if(i == 3)
					cell.setCellValue(totalShouldAcceptSchNum);
				else if(i == 4)
					cell.setCellValue(totalAcceptSchNum);
				else if(i == 5)
					cell.setCellValue(totalNoAcceptSchNum);
				else if(i == 6)
					cell.setCellValue(totalSchAcceptRate + "%");
				
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
					cell.setCellValue(totalTotalGsPlanNum);
				else if(i == 13)
					cell.setCellValue(totalNoAcceptGsPlanNum);
				else if(i == 14)
					cell.setCellValue(totalAcceptGsPlanNum);
				else if(i == 15)
					cell.setCellValue(totalAcceptRate + "%");
				else if(i == 16)
					cell.setCellValue(totalAssignGsPlanNum);
				else if(i == 17)
					cell.setCellValue(totalAssignRate + "%");

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
	boolean exportExcell(String pathFileName,Map<String,DepartmentObj> deparmentMap, List<GsPlanOpts> dataList, String schSelMode) {
		boolean retFlag = true;
		int curSchSelMode = -1;
		if(schSelMode == null)
			schSelMode = "1";
		curSchSelMode = Integer.parseInt(schSelMode);
		//筛选学校模式
		if(curSchSelMode == 0) {    //按主管部门
			retFlag = expGsPlanOptsExcelByCompDep(pathFileName, dataList, colNames0);
		}
		else if(curSchSelMode == 1) {  //按所在地
			retFlag = expGsPlanOptsExcelByLocality(pathFileName, dataList, colNames1);
		}else if(curSchSelMode == 2) {  //按管理部门
			retFlag = expGsPlanOptsExcelByDepartment(pathFileName, deparmentMap,dataList, colNames2);
		}     	
		
		return retFlag;
	}
	
	//导出配货计划操作列表模型函数
	public ExpGsPlanOptsDTO appModFunc(String token, String startDate, String endDate, String schSelMode, String subLevel, String compDep,
			String subDistName, String distName, String prefCity, String province,String subLevels,String compDeps,String distNames,
			String deaprtmentIds,
			Db1Service db1Service, Db2Service db2Service,DbHiveGsService dbHiveGsService) {
		ExpGsPlanOptsDTO egpoDto = null;
		if (isRealData) { // 真实数据
			String strCurPageNum = String.valueOf(curPageNum), strPageSize = String.valueOf(pageSize);
			if (startDate == null || endDate == null) {   // 按照当天日期获取数据
				startDate = BCDTimeUtil.convertNormalDate(null);
				endDate = startDate;
			}
			GsPlanOptsDTO pdlDto = gpoAppMod.appModFunc(token, startDate, endDate, schSelMode, 
					subLevel, compDep, subDistName, distName, prefCity, province,subLevels,compDeps,distNames,
					deaprtmentIds,
					strCurPageNum, strPageSize, db1Service, db2Service,dbHiveGsService);
			if(pdlDto != null) {
				int i, totalCount = pdlDto.getPageInfo().getPageTotal();
				int pageCount = 0;
				List<GsPlanOpts> expExcelList = new ArrayList<>();
				if(totalCount % pageSize == 0)
					pageCount = totalCount/pageSize;
				else
					pageCount = totalCount/pageSize + 1;
				//第一页数据
				if(pdlDto.getGsPlanOpts() != null) {
					expExcelList.addAll(pdlDto.getGsPlanOpts());			
				}
				//后续页数据
				for(i = curPageNum+1; i <= pageCount; i++) {
					strCurPageNum = String.valueOf(i);
					GsPlanOptsDTO curGpoDto = gpoAppMod.appModFunc(token, startDate, endDate, schSelMode, 
							subLevel, compDep, subDistName, distName, prefCity, province,subLevels,compDeps,distNames,
							deaprtmentIds,
							strCurPageNum, strPageSize, db1Service, db2Service,dbHiveGsService);
					if(curGpoDto.getGsPlanOpts() != null) {
						expExcelList.addAll(curGpoDto.getGsPlanOpts());
					}
				}
				//生成导出EXCEL文件
				String repFileName = repFileResPath + UniqueIdGen.uuid() + SpringConfig.repFileFormats[SpringConfig.curRepFileFrmtIdx];
				String pathFileName = SpringConfig.base_dir + repFileName;
				logger.info("导出文件路径：" + pathFileName);
				
				List<DepartmentObj> deparmentList =  db1Service.getDepartmentObjList(new DepartmentObj(),null, -1, -1);	
				Map<String,DepartmentObj> deparmentMap = deparmentList.stream().collect(Collectors.toMap(DepartmentObj::getDepartmentId,(b)->b));
				
				boolean flag = exportExcell(pathFileName,deparmentMap, expExcelList, schSelMode);
				if(flag) {
					//移动文件到其他目录
					//AppModConfig.moveFileToOtherFolder(pathFileName, SpringConfig.tomcatSrvDirs[1] + repFileResPath);
					egpoDto = new ExpGsPlanOptsDTO();
					ExpGsPlanOpts expGsPlanOpts = new ExpGsPlanOpts();
					//时戳
					egpoDto.setTime(BCDTimeUtil.convertNormalFrom(null));
					//导出信息
					expGsPlanOpts.setStartDate(startDate);
					expGsPlanOpts.setEndDate(endDate);
					expGsPlanOpts.setDistName(distName);
					expGsPlanOpts.setPrefCity(prefCity);
					expGsPlanOpts.setProvince(province);
					expFileUrl = SpringConfig.repfile_srvdn + repFileName;
					logger.info("导出文件URL：" + expFileUrl);
					expGsPlanOpts.setExpFileUrl(expFileUrl);
					egpoDto.setExpGsPlanOpts(expGsPlanOpts);
					//消息ID
					egpoDto.setMsgId(AppModConfig.msgId);
					AppModConfig.msgId++;
					// 消息id小于0判断
					AppModConfig.msgIdLessThan0Judge();
				}
			}
		} else { // 模拟数据
			// 模拟数据函数
			egpoDto = SimuDataFunc();
		}

		return egpoDto;
	}
}
