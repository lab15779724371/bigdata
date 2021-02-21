package com.tfit.BdBiProcSrvShEduOmc.appmod.im;

import java.io.ByteArrayOutputStream;
import java.io.ByteArrayOutputStream;import java.io.File;import com.tfit.BdBiProcSrvShEduOmc.util.FtpUtil;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.poi.hssf.usermodel.HSSFDataFormat;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.tfit.BdBiProcSrvShEduOmc.appmod.optanl.DishSumInfoAppMod;
import com.tfit.BdBiProcSrvShEduOmc.appmod.optanl.SchDishSitStatAppMod;
import com.tfit.BdBiProcSrvShEduOmc.config.AppModConfig;
import com.tfit.BdBiProcSrvShEduOmc.config.SpringConfig;
import com.tfit.BdBiProcSrvShEduOmc.dto.im.ExpDishSumInfo;
import com.tfit.BdBiProcSrvShEduOmc.dto.im.ExpDishSumInfoDTO;
import com.tfit.BdBiProcSrvShEduOmc.dto.optanl.DishSumInfoDTO;
import com.tfit.BdBiProcSrvShEduOmc.dto.optanl.SchDishSitStat;
import com.tfit.BdBiProcSrvShEduOmc.dto.optanl.SchDishSitStatDTO;
import com.tfit.BdBiProcSrvShEduOmc.service.Db1Service;
import com.tfit.BdBiProcSrvShEduOmc.service.Db2Service;
import com.tfit.BdBiProcSrvShEduOmc.service.DbHiveDishService;
import com.tfit.BdBiProcSrvShEduOmc.service.SaasService;
import com.tfit.BdBiProcSrvShEduOmc.util.BCDTimeUtil;
import com.tfit.BdBiProcSrvShEduOmc.util.FtpUtil;
import com.tfit.BdBiProcSrvShEduOmc.util.UniqueIdGen;

//3.2.5.	导出业务监管数据汇总统计报表
public class ExpDishSumInfoAppMod {
	private static final Logger logger = LogManager.getLogger(ExpDishSumInfoAppMod.class.getName());
	
	//3.2.9.	排菜汇总信息应用模型
	private DishSumInfoAppMod dishSumInfoAppMod = new DishSumInfoAppMod();
	
	//3.2.10.	学校排菜情况统计应用模型
	private SchDishSitStatAppMod schDishSitStatAppMod = new SchDishSitStatAppMod();
	
	//是否为真实数据标识
	private static boolean isRealData = true;
	//页号、页大小和总页数
	int curPageNum = 1, pageTotal = 1, pageSize = AppModConfig.maxPageSize;
	//报表文件资源路径
	String repFileResPath = "/expDishSumInfo/";
	//导出列名数组
	String[] colNames = {"所在地", "学校总数", "供餐学校", "已排菜学校", "未排菜学校", "排菜率",""};	
	//变量数据初始化
	String startDate = "2018-09-03";
	String endDate = "2018-09-04";
	String ppName = null;
	String distName = null;
	String prefCity = null;
	String province = "上海市";
	String rsFlag = null;
	String schType = null;
	String expFileUrl = "test1.txt";
	
	//模拟数据函数
	private ExpDishSumInfoDTO SimuDataFunc() {
		ExpDishSumInfoDTO eppGsPlanOptsDTO = new ExpDishSumInfoDTO();
		//设置返回数据
		eppGsPlanOptsDTO.setTime(BCDTimeUtil.convertNormalFrom(null));
		//列表元素设置
		ExpDishSumInfo expDishSumInfo = new ExpDishSumInfo();
		//赋值
		expDishSumInfo.setStartDate(startDate);
		expDishSumInfo.setEndDate(endDate);
		expDishSumInfo.setDistName(distName);
		expDishSumInfo.setPrefCity(prefCity);
		expDishSumInfo.setProvince(province);
		
		
		expDishSumInfo.setExpFileUrl(SpringConfig.repfile_srvdn+repFileResPath+expFileUrl);
		//设置模拟数据
		eppGsPlanOptsDTO.setExpDishSumInfo(expDishSumInfo);
		//消息ID
		eppGsPlanOptsDTO.setMsgId(AppModConfig.msgId);
		AppModConfig.msgId++;
		//消息id小于0判断
		AppModConfig.msgIdLessThan0Judge();

		return eppGsPlanOptsDTO;
	}
	
	//生成导出EXCEL文件
	public boolean expDishSumInfoExcel(String token,String pathFileName, String startDate,String endDate,
			Db1Service db1Service, Db2Service db2Service, SaasService saasService,DbHiveDishService dbHiveDishService) { 
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
        	Integer startRowIdx = 0;
        	String[] colVals = new String[colNames.length+1];
        	
			//加粗字体
		  	CellStyle style = AppModConfig.getExcellCellStyle(wb);
		  	
		  	//居左+加粗字体格式
		  	CellStyle styleAlignLeft = AppModConfig.getExcellCellStyleAlignLeft(wb);
		  	
		  	//浮点型，2位数字格式化格式（2位有效数字+%+边框）
		  	CellStyle cellStyleFloat = wb.createCellStyle();    
		  	cellStyleFloat.setDataFormat(HSSFDataFormat.getBuiltinFormat("0.00%"));
		  	cellStyleFloat.setBorderBottom(CellStyle.BORDER_THIN);
		  	cellStyleFloat.setBorderLeft(CellStyle.BORDER_THIN);
		  	cellStyleFloat.setBorderRight(CellStyle.BORDER_THIN);
		  	cellStyleFloat.setBorderTop(CellStyle.BORDER_THIN);
		  	
		  	//表格头部样式（加粗字体+边框）
		  	CellStyle cellStyleHeadBorder = AppModConfig.getExcellCellStyle(wb);    
		  	cellStyleHeadBorder.setBorderBottom(CellStyle.BORDER_THIN);
		  	cellStyleHeadBorder.setBorderLeft(CellStyle.BORDER_THIN);
		  	cellStyleHeadBorder.setBorderRight(CellStyle.BORDER_THIN);
		  	cellStyleHeadBorder.setBorderTop(CellStyle.BORDER_THIN);
		  	
		    //边框
		  	CellStyle styleBorder = AppModConfig.getExcellCellStyleBorder(wb);
		  	
		  	
		  	//文字水平垂直居中（表格文字）
		  	CellStyle styleVerAliCenter = AppModConfig.getExcellCellStyleBorder(wb);                  // 样式对象
	  		// 设置单元格的背景颜色为淡蓝色
		  	styleVerAliCenter.setFillForegroundColor(HSSFColor.PALE_BLUE.index);
		  	styleVerAliCenter.setVerticalAlignment(CellStyle.VERTICAL_CENTER);   // 垂直
		  	styleVerAliCenter.setAlignment(CellStyle.ALIGN_CENTER);              // 水平

	  		
		  	sheet.setColumnWidth(0, 100*50);
		  	sheet.setColumnWidth(1, 100*50);
		  	sheet.setColumnWidth(2, 100*50);
		  	sheet.setColumnWidth(3, 100*50);
		  	sheet.setColumnWidth(4, 100*50);
		  	sheet.setColumnWidth(5, 100*50);
		  	sheet.setColumnWidth(6, 100*50);
		  	
		  	//第一行
		  	//标题：学校排菜情况汇总统计报表
		  	String title = "学校排菜情况汇总统计报表";
		  	sheet.addMergedRegion(new CellRangeAddress(startRowIdx, startRowIdx, 0, 6));
		  	creatFullRow(sheet, startRowIdx++, colVals, style, title);
			//一、排菜日期：2019/04/08 - 2019/04/08
			title = "一、排菜日期："+startDate + " - " + endDate;
			sheet.addMergedRegion(new CellRangeAddress(startRowIdx, startRowIdx, 0, 6));
			creatFullRow(sheet, startRowIdx++, colVals, styleAlignLeft, title);
			
			//二、排菜情况汇总
			startRowIdx++;//和上一个表格之间留空格
			title = "二、排菜情况汇总";
			sheet.addMergedRegion(new CellRangeAddress(startRowIdx, startRowIdx, 0, 6));
			creatFullRow(sheet, startRowIdx++, colVals, styleAlignLeft, title);
			
            DishSumInfoDTO dishSumInfoDTO = dishSumInfoAppMod.appModFunc(token, distName, prefCity, province, startDate, endDate, db1Service, db2Service, saasService,dbHiveDishService);
            
            title="        ";
            String [] titls = {"学校数量：","供餐学校：","已排菜学校：","未排菜学校：","排菜率："};
            String [] units = {"所","所","所","所","%"};
            if(dishSumInfoDTO!=null && dishSumInfoDTO.getDishSumInfo() !=null) {
            	String[] values = {dishSumInfoDTO.getDishSumInfo().getTotalSchNum()+"",dishSumInfoDTO.getDishSumInfo().getMealSchNum()+"",dishSumInfoDTO.getDishSumInfo().getDishSchNum()+"",
            			dishSumInfoDTO.getDishSumInfo().getNoDishSchNum()+"",dishSumInfoDTO.getDishSumInfo().getDishRate()+""};
        		for(int i=0;i<titls.length;i++) {
    	    		title += titls[i]+values[i]+units[i]+"    ";
        		}
            }else {
        		for(int i=0;i<titls.length;i++) {
    	    		title += titls[i];
        		}
            }
    		sheet.addMergedRegion(new CellRangeAddress(startRowIdx, startRowIdx, 0, 6));
    		creatFullRow(sheet, startRowIdx++, colVals, styleAlignLeft, title);
    		
			//三、学校排菜情况统计
    		startRowIdx++;//和上一个表格之间留空格
			title = "三、学校排菜情况统计";
			sheet.addMergedRegion(new CellRangeAddress(startRowIdx, startRowIdx, 0, 6));
			creatFullRow(sheet, startRowIdx++, colVals, styleAlignLeft, title);
			//1.按学校所在区统计
			title = "1.按学校所在区统计";
			sheet.addMergedRegion(new CellRangeAddress(startRowIdx, startRowIdx, 0, 6));
			creatFullRow(sheet, startRowIdx++, colVals, styleAlignLeft, title);
			
			//创建一行
			int statMode = 0;
			String [] areColNames ={"所在地", "学校总数", "供餐学校", "已排菜学校", "未排菜学校", "排菜率"};
			startRowIdx = creatTable(token, pathFileName, startDate, endDate, db1Service, db2Service, saasService,dbHiveDishService,
					excelPath, fileType, sheet, startRowIdx++, colVals, style, statMode, areColNames,
					styleBorder,cellStyleFloat,cellStyleHeadBorder,styleVerAliCenter);
			//2.按办学性质统计
			startRowIdx++;//和上一个表格之间留空格
			title = "2.按办学性质统计";
			sheet.addMergedRegion(new CellRangeAddress(startRowIdx, startRowIdx, 0, 6));
			creatFullRow(sheet, startRowIdx++, colVals, styleAlignLeft, title);
			
			//列表
			statMode = 1;
			String [] natrueColNames ={"学校性质", "学校总数", "供餐学校", "已排菜学校", "未排菜学校", "排菜率"};
			startRowIdx = creatTable(token, pathFileName, startDate, endDate, db1Service, db2Service, saasService,dbHiveDishService,
					excelPath, fileType, sheet, startRowIdx++, colVals, style, statMode, natrueColNames,
					styleBorder,cellStyleFloat,cellStyleHeadBorder,styleVerAliCenter);
			//3.按学校学制统计
			startRowIdx++;//和上一个表格之间留空格
			title = "3.按学校学制统计";
			sheet.addMergedRegion(new CellRangeAddress(startRowIdx, startRowIdx, 0, 6));
			creatFullRow(sheet, startRowIdx++, colVals, styleAlignLeft, title);
			//列表
			statMode = 2;
			String [] levelColNames ={"分类","学制", "学校总数", "供餐学校", "已排菜学校", "未排菜学校", "排菜率"};
			startRowIdx = creatTable(token, pathFileName, startDate, endDate, db1Service, db2Service, saasService,dbHiveDishService,
					excelPath, fileType, sheet, startRowIdx++, colVals, style, statMode, levelColNames,
					styleBorder,cellStyleFloat,cellStyleHeadBorder,styleVerAliCenter);
			
			//4.按所属主管部门统计
			startRowIdx++;//和上一个表格之间留空格
			title = "4.按所属主管部门统计";
			sheet.addMergedRegion(new CellRangeAddress(startRowIdx, startRowIdx, 0, 6));
			creatFullRow(sheet, startRowIdx++, colVals, styleAlignLeft, title);
			
			//列表
			statMode = 3;
			String [] salveColNames ={"所属","主管部门", "学校总数", "供餐学校", "已排菜学校", "未排菜学校", "排菜率"};
			startRowIdx = creatTable(token, pathFileName, startDate, endDate, db1Service, db2Service, saasService,dbHiveDishService,
					excelPath, fileType, sheet, startRowIdx++, colVals, style, statMode, salveColNames,
					styleBorder,cellStyleFloat,cellStyleHeadBorder,styleVerAliCenter);
			
			logger.info("1 excelPath:"+excelPath);
			// 创建文件流
			OutputStream stream = null;
			try {
				stream = new FileOutputStream(excelPath);
				logger.info("2 stream:"+stream);
			} catch (FileNotFoundException e) {
				// TODO 自动生成的 catch 块
				e.printStackTrace();
			}
			
			if (stream != null) {
				// 写入数据
				try {
					ByteArrayOutputStream os = new ByteArrayOutputStream();wb.write(os);FtpUtil.ftpServer(pathFileName, os,repFileResPath);
					logger.info("3 write:");
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

	private Integer creatTable(String token, String pathFileName, String startDate, String endDate, Db1Service db1Service,
			Db2Service db2Service, SaasService saasService,DbHiveDishService dbHiveDishService, String excelPath, String fileType, Sheet sheet,
			int startRowIdx, String[] colVals, CellStyle style, int statMode, String[] areColNames,
			CellStyle styleBorder,CellStyle cellStyleFloat,CellStyle cellStyleHeadBorder,CellStyle styleVerAliCenter) {
		Row row;
		Cell cell;
		creatTableHead(sheet, startRowIdx++, colVals, style, areColNames,cellStyleHeadBorder);
		SchDishSitStatDTO areSchDishSitStatDTO = schDishSitStatAppMod.appModFunc(token,distName, prefCity, province, startDate, endDate,statMode, 
				db1Service,db2Service,saasService,null,dbHiveDishService);
		int rowCount = 0;
		if(areSchDishSitStatDTO != null) {
			List<SchDishSitStat> areaSchDishSitStatList = new ArrayList<SchDishSitStat>();
			if(areSchDishSitStatDTO !=null && areSchDishSitStatDTO.getSchDishSitStat()!=null) {
				areaSchDishSitStatList.addAll(areSchDishSitStatDTO.getSchDishSitStat());	
			}
			
			// 循环写入行数据
			int columIndex = 0;
			rowCount = areaSchDishSitStatList.size();
			
			Integer totalSchNum = 0;
			Integer noDishSchNum = 0;
			Integer mealSchNum = 0;
			Integer dishSchNum = 0;
			for (int i = 0; i < areaSchDishSitStatList.size(); i++) {
				columIndex = 0;
				if(startRowIdx+i==37) {
					sheet.addMergedRegion(new CellRangeAddress(startRowIdx+i, startRowIdx+i+8-1, 0, 0));
				}else if (startRowIdx+i==45) {
					sheet.addMergedRegion(new CellRangeAddress(startRowIdx+i, startRowIdx+i+9-1, 0, 0));
				}else if (startRowIdx+i==54) {
					sheet.addMergedRegion(new CellRangeAddress(startRowIdx+i, startRowIdx+i+4-1, 0, 0));
				}else if (startRowIdx+i==62) {
					sheet.addMergedRegion(new CellRangeAddress(startRowIdx+i, startRowIdx+i+18-1, 0, 0));
				}else if (startRowIdx+i==80) {
					sheet.addMergedRegion(new CellRangeAddress(startRowIdx+i, startRowIdx+i+9-1, 0, 0));
				}else if (startRowIdx+i==89) {
					sheet.addMergedRegion(new CellRangeAddress(startRowIdx+i, startRowIdx+i+3-1, 0, 0));
				}else if (startRowIdx+i==92) {
					sheet.addMergedRegion(new CellRangeAddress(startRowIdx+i, startRowIdx+i+2-1, 0, 0));
				}
				
				row = (Row) sheet.createRow(i + startRowIdx);
				if(statMode == 2 ||statMode == 3 ) {
					cell = row.createCell(columIndex++);
					cell.setCellValue(areaSchDishSitStatList.get(i).getStatClassName());
					cell.setCellStyle(styleVerAliCenter);
				}
				cell = row.createCell(columIndex++);
				cell.setCellValue(areaSchDishSitStatList.get(i).getStatPropName()); //所在地
				cell.setCellStyle(styleVerAliCenter);
				
				cell = row.createCell(columIndex++);
				cell.setCellValue(areaSchDishSitStatList.get(i).getTotalSchNum());  //学校总数
				cell.setCellStyle(styleBorder);
				
				cell = row.createCell(columIndex++);
				cell.setCellValue(areaSchDishSitStatList.get(i).getMealSchNum());   //供餐学校
				cell.setCellStyle(styleBorder);
				
				cell = row.createCell(columIndex++);
				cell.setCellValue(areaSchDishSitStatList.get(i).getDishSchNum());    //已排菜学校
				cell.setCellStyle(styleBorder);
				
				cell = row.createCell(columIndex++);
				cell.setCellValue(areaSchDishSitStatList.get(i).getNoDishSchNum());//未排菜学校
				cell.setCellStyle(styleBorder);
				
				cell = row.createCell(columIndex++);  //排菜率
				cell.setCellValue(areaSchDishSitStatList.get(i).getDishRate()/100);
				cell.setCellStyle(cellStyleFloat);
				
				//合计数据
				totalSchNum +=areaSchDishSitStatList.get(i).getTotalSchNum();
				noDishSchNum +=areaSchDishSitStatList.get(i).getNoDishSchNum();
				mealSchNum +=areaSchDishSitStatList.get(i).getMealSchNum();
				dishSchNum +=areaSchDishSitStatList.get(i).getDishSchNum();
			}
			
			//添加合计列
			row = (Row) sheet.createRow(startRowIdx + rowCount);
			columIndex = 0;
			if(statMode == 2 ||statMode == 3 ) {
				cell = row.createCell(columIndex++);
				cell.setCellValue("合计");
				cell.setCellStyle(styleVerAliCenter);
				
				cell = row.createCell(columIndex++);
				cell.setCellValue("---");
				cell.setCellStyle(styleVerAliCenter);
			}else {
				cell = row.createCell(columIndex++);
				cell.setCellValue("合计"); //所在地
				cell.setCellStyle(styleVerAliCenter);
			}
			cell = row.createCell(columIndex++);
			cell.setCellValue(totalSchNum);  //学校总数
			cell.setCellStyle(styleBorder);
			
			cell = row.createCell(columIndex++);
			cell.setCellValue(mealSchNum);   //供餐学校
			cell.setCellStyle(styleBorder);
			
			cell = row.createCell(columIndex++);
			cell.setCellValue(dishSchNum);    //已排菜学校
			cell.setCellStyle(styleBorder);
			
			cell = row.createCell(columIndex++);
			cell.setCellValue(noDishSchNum);//未排菜学校
			cell.setCellStyle(styleBorder);
			
			float dishRate = 0F;
			if(totalSchNum > 0) {
				dishRate = 100 * ((float) dishSchNum / (float) mealSchNum);
				BigDecimal bd = new BigDecimal(dishRate);
				dishRate = bd.setScale(2, BigDecimal.ROUND_HALF_UP).floatValue();
				if (dishRate > 100) {
					dishRate = 100;
				}
			}
			
			cell = row.createCell(columIndex++);  //排菜率
			cell.setCellValue(dishRate/100);
			cellStyleFloat.setAlignment(CellStyle.ALIGN_RIGHT);
			cell.setCellStyle(cellStyleFloat);
			
			

		}
		return startRowIdx + rowCount+1;
	}

	private void creatTableHead(Sheet sheet, int startRowIdx, String[] colVals, CellStyle style, String[] areColNames,CellStyle cellStyleHeadBorder) {
		Row row;
		Cell cell;
		row = (Row) sheet.createRow(startRowIdx++);
		for (int i = 0; i < areColNames.length; i++) {
			cell = row.createCell(i);
			try {
				logger.info(areColNames[i] + " ");
				colVals[i] = new String(areColNames[i].getBytes(), "utf-8");
				cell.setCellValue(areColNames[i]);
				cell.setCellStyle(cellStyleHeadBorder);
			} catch (UnsupportedEncodingException e) {
				// TODO 自动生成的 catch 块
				e.printStackTrace();
			}
		}
	}

	private void creatFullRow(Sheet sheet, int startRowIdx, String[] colVals, CellStyle style, String title) {
		Row row;
		Cell cell;
		row = (Row) sheet.createRow(startRowIdx++);
		row.createCell(0);
		cell = row.createCell(0);
		try {
			colVals[0] = new String(title.getBytes(), "utf-8");
			cell.setCellValue(title);
			cell.setCellStyle(style);
		} catch (UnsupportedEncodingException e) {
			// TODO 自动生成的 catch 块
			e.printStackTrace();
		}
	}
	
	//导出项目点排菜详情列表模型函数
	public ExpDishSumInfoDTO appModFunc(String token,String startDate,String endDate, 
			String distName, String prefCity, String province, Db1Service db1Service,
			 Db2Service db2Service, SaasService saasService,DbHiveDishService dbHiveDishService ) {
		ExpDishSumInfoDTO eppGsPlanOptsDTO = null;
		if (isRealData) { // 真实数据
			if (startDate == null || endDate == null) {   // 按照当天日期获取数据
				startDate = BCDTimeUtil.convertNormalDate(null);
				endDate = startDate;
			}
			
			//生成导出EXCEL文件
			String repFileName = repFileResPath + UniqueIdGen.uuid() + SpringConfig.repFileFormats[SpringConfig.curRepFileFrmtIdx];
			String pathFileName = SpringConfig.tomcatSrvDirs[1] + repFileName;
			logger.info("导出文件路径：" + pathFileName);
			boolean flag = expDishSumInfoExcel(token, pathFileName, startDate, endDate, db1Service, db2Service, saasService,dbHiveDishService);
			//将文件推送到tomcat服务
			//FtpUtil.testFtp1(repFileName);
			if(flag) {
				eppGsPlanOptsDTO = new ExpDishSumInfoDTO();
				ExpDishSumInfo expDishSumInfo = new ExpDishSumInfo();
				//时戳
				eppGsPlanOptsDTO.setTime(BCDTimeUtil.convertNormalFrom(null));
				//导出信息
				expDishSumInfo.setStartDate(startDate);
				expDishSumInfo.setEndDate(endDate);
				expDishSumInfo.setDistName(AppModConfig.distIdToNameMap.get(distName));
				expDishSumInfo.setPrefCity(prefCity);
				expDishSumInfo.setProvince(province);
				//导出文件URL
				String expFileUrl = SpringConfig.repfile_srvdn + repFileName;
				logger.info("导出文件URL：" + expFileUrl);
				expDishSumInfo.setExpFileUrl(expFileUrl);
				eppGsPlanOptsDTO.setExpDishSumInfo(expDishSumInfo);
				//消息ID
				eppGsPlanOptsDTO.setMsgId(AppModConfig.msgId);
				AppModConfig.msgId++;
				// 消息id小于0判断
				AppModConfig.msgIdLessThan0Judge();
			}
			
		} else { // 模拟数据
			// 模拟数据函数
			eppGsPlanOptsDTO = SimuDataFunc();
		}

		return eppGsPlanOptsDTO;
	}
}
