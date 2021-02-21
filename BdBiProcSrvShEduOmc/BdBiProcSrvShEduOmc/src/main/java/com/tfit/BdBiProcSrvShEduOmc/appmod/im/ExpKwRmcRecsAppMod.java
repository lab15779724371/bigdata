package com.tfit.BdBiProcSrvShEduOmc.appmod.im;

import java.io.ByteArrayOutputStream;import java.io.File;import com.tfit.BdBiProcSrvShEduOmc.util.FtpUtil;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

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
import com.tfit.BdBiProcSrvShEduOmc.dto.im.ExpKwRmcRecs;
import com.tfit.BdBiProcSrvShEduOmc.dto.im.ExpKwRmcRecsDTO;
import com.tfit.BdBiProcSrvShEduOmc.dto.im.KwRmcRecs;
import com.tfit.BdBiProcSrvShEduOmc.dto.im.KwRmcRecsDTO;
import com.tfit.BdBiProcSrvShEduOmc.service.Db1Service;
import com.tfit.BdBiProcSrvShEduOmc.service.Db2Service;
import com.tfit.BdBiProcSrvShEduOmc.service.DbHiveRecyclerWasteService;
import com.tfit.BdBiProcSrvShEduOmc.util.BCDTimeUtil;
import com.tfit.BdBiProcSrvShEduOmc.util.UniqueIdGen;

//导出餐厨垃圾团餐公司回收列表应用模型
public class ExpKwRmcRecsAppMod {
	private static final Logger logger = LogManager.getLogger(ExpKwRmcRecsAppMod.class.getName());
	
	//餐厨垃圾团餐公司回收列表应用模型
	private KwRmcRecsAppMod ekrrAppMod = new KwRmcRecsAppMod();
	
	//是否为真实数据标识
	private static boolean isRealData = true;
	//页号、页大小和总页数
	int curPageNum = 1, pageTotal = 1, pageSize = AppModConfig.maxPageSize;
	//报表文件资源路径
	String repFileResPath = "/expKwRmcRecs/";
	//导出列名数组
	String[] colNames = {"回收周期", "区", "回收次数", "回收数量"};	
	
	//变量数据初始化
	String recStartDate = "2018-09-03";
	String recEndDate = "2018-09-04";
	String distName = null;
	String prefCity = null;
	String province = "上海市";
	String expFileUrl = "fc8bafe943214d65a67a7d8b93d0185a.xls";
	
	//模拟数据函数
	private ExpKwRmcRecsDTO SimuDataFunc() {
		ExpKwRmcRecsDTO ekrrDto = new ExpKwRmcRecsDTO();
		//设置返回数据
		ekrrDto.setTime(BCDTimeUtil.convertNormalFrom(null));
		//列表元素设置
		ExpKwRmcRecs expKwRmcRecs = new ExpKwRmcRecs();
		//赋值
		expKwRmcRecs.setRecStartDate(recStartDate);
		expKwRmcRecs.setRecEndDate(recEndDate);
		expKwRmcRecs.setDistName(distName);
		expKwRmcRecs.setPrefCity(prefCity);
		expKwRmcRecs.setProvince(province);
		expKwRmcRecs.setExpFileUrl(SpringConfig.repfile_srvdn+repFileResPath+expFileUrl);
		//设置模拟数据
		ekrrDto.setExpKwRmcRecs(expKwRmcRecs);
		//消息ID
		ekrrDto.setMsgId(AppModConfig.msgId);
		AppModConfig.msgId++;
		//消息id小于0判断
		AppModConfig.msgIdLessThan0Judge();

		return ekrrDto;
	}
	
	//生成导出EXCEL文件
	public boolean expKwRmcRecsExcel(String pathFileName, List<KwRmcRecs> dataList, String colNames[]) { 
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
			String totalDate = "合计", totalDistName = "---";
			int totalRmcNum = 0, totalRcNum = 0;
			for (int i = 0; i < dataList.size(); i++) {
				row = (Row) sheet.createRow(i + startRowIdx);
				row.createCell(0).setCellValue(dataList.get(i).getRecDate());                                           //回收日期
				row.createCell(1).setCellValue(AppModConfig.distIdToNameMap.get(dataList.get(i).getDistName()));        //区					
				row.createCell(2).setCellValue(dataList.get(i).getRmcNum());                                            //学校数量
				totalRmcNum += dataList.get(i).getRmcNum();
				row.createCell(3).setCellValue(dataList.get(i).getRcNum() + "桶");                                       //回收数量
				totalRcNum += dataList.get(i).getRcNum();                                   
			}
			//创建合计一行
			startRowIdx += dataList.size();
			row = (Row) sheet.createRow(startRowIdx);
			for (int i = 0; i < colNames.length; i++) {
				cell = row.createCell(i);
				if(i == 0)
					cell.setCellValue(totalDate);
				else if(i == 1)
					cell.setCellValue(totalDistName);
				else if(i == 2)
					cell.setCellValue(totalRmcNum);
				else if(i == 3)
					cell.setCellValue(totalRcNum + "桶");
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
	
	//导出餐厨垃圾团餐公司回收列表模型函数
	public ExpKwRmcRecsDTO appModFunc(String token, String recStartDate, String recEndDate, 
			String distName, String prefCity, String province, String distNames,
			Db1Service db1Service, Db2Service db2Service,DbHiveRecyclerWasteService dbHiveRecyclerWasteService) {
		ExpKwRmcRecsDTO ekrrDto = null;
		if (isRealData) { // 真实数据
			String strCurPageNum = String.valueOf(curPageNum), strPageSize = String.valueOf(pageSize);
			if (recStartDate == null || recEndDate == null) {   // 按照当天日期获取数据
				recStartDate = BCDTimeUtil.convertNormalDate(null);
				recEndDate = recStartDate;
			}
			KwRmcRecsDTO pdlDto = ekrrAppMod.appModFunc(token, recStartDate, 
					recEndDate, distName, prefCity, province,distNames, 
					strCurPageNum, strPageSize, db1Service, db2Service,dbHiveRecyclerWasteService);
			if(pdlDto != null) {
				int i, totalCount = pdlDto.getPageInfo().getPageTotal();
				int pageCount = 0;
				List<KwRmcRecs> expExcelList = new ArrayList<>();
				if(totalCount % pageSize == 0)
					pageCount = totalCount/pageSize;
				else
					pageCount = totalCount/pageSize + 1;
				//第一页数据
				if(pdlDto.getKwRmcRecs() != null) {
					expExcelList.addAll(pdlDto.getKwRmcRecs());			
				}
				//后续页数据
				for(i = curPageNum+1; i <= pageCount; i++) {
					strCurPageNum = String.valueOf(i);
					KwRmcRecsDTO curPdlDto = ekrrAppMod.appModFunc(token, recStartDate, recEndDate,
							distName, prefCity, province,distNames, 
							strCurPageNum, strPageSize, db1Service, db2Service,dbHiveRecyclerWasteService);
					if(curPdlDto.getKwRmcRecs() != null) {
						expExcelList.addAll(curPdlDto.getKwRmcRecs());
					}
				}
				//生成导出EXCEL文件
				String repFileName = repFileResPath + UniqueIdGen.uuid() + SpringConfig.repFileFormats[SpringConfig.curRepFileFrmtIdx];
				String pathFileName = SpringConfig.base_dir + repFileName;
				logger.info("导出文件路径：" + pathFileName);
				boolean flag = expKwRmcRecsExcel(pathFileName, expExcelList, colNames);
				if(flag) {
					//移动文件到其他目录
					//AppModConfig.moveFileToOtherFolder(pathFileName, SpringConfig.tomcatSrvDirs[1] + repFileResPath);
					ekrrDto = new ExpKwRmcRecsDTO();
					ExpKwRmcRecs expKwRmcRecs = new ExpKwRmcRecs();
					//时戳
					ekrrDto.setTime(BCDTimeUtil.convertNormalFrom(null));
					//导出信息
					expKwRmcRecs.setRecStartDate(recStartDate);
					expKwRmcRecs.setRecEndDate(recEndDate);
					expKwRmcRecs.setDistName(distName);
					expKwRmcRecs.setPrefCity(prefCity);
					expKwRmcRecs.setProvince(province);
					expFileUrl = SpringConfig.repfile_srvdn + repFileName;
					logger.info("导出文件URL：" + expFileUrl);
					expKwRmcRecs.setExpFileUrl(expFileUrl);
					ekrrDto.setExpKwRmcRecs(expKwRmcRecs);
					//消息ID
					ekrrDto.setMsgId(AppModConfig.msgId);
					AppModConfig.msgId++;
					// 消息id小于0判断
					AppModConfig.msgIdLessThan0Judge();
				}
			}
		} else { // 模拟数据
			// 模拟数据函数
			ekrrDto = SimuDataFunc();
		}

		return ekrrDto;
	}
}
