package com.tfit.BdBiProcSrvShEduOmc.appmod.iw;

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

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.tfit.BdBiProcSrvShEduOmc.config.AppModConfig;
import com.tfit.BdBiProcSrvShEduOmc.config.SpringConfig;
import com.tfit.BdBiProcSrvShEduOmc.dto.iw.ExpWarnAllLics;
import com.tfit.BdBiProcSrvShEduOmc.dto.iw.ExpWarnAllLicsDTO;
import com.tfit.BdBiProcSrvShEduOmc.dto.iw.WarnAllLics;
import com.tfit.BdBiProcSrvShEduOmc.dto.iw.WarnAllLicsDTO;
import com.tfit.BdBiProcSrvShEduOmc.service.Db1Service;
import com.tfit.BdBiProcSrvShEduOmc.service.Db2Service;
import com.tfit.BdBiProcSrvShEduOmc.service.DbHiveWarnService;
import com.tfit.BdBiProcSrvShEduOmc.service.SaasService;
import com.tfit.BdBiProcSrvShEduOmc.util.BCDTimeUtil;
import com.tfit.BdBiProcSrvShEduOmc.util.CommonUtil;
import com.tfit.BdBiProcSrvShEduOmc.util.FtpUtil;
import com.tfit.BdBiProcSrvShEduOmc.util.UniqueIdGen;

//导出证照预警全部证件列表应用模型
public class ExpWarnAllLicsAppMod {
	private static final Logger logger = LogManager.getLogger(ExpWarnAllLicsAppMod.class.getName());
	
	//证照预警全部证件列表应用模型
	private WarnAllLicsAppMod walAppMod = new WarnAllLicsAppMod();
	
	//是否为真实数据标识
	private static boolean isRealData = true;
	//页号、页大小和总页数
	int curPageNum = 1, pageTotal = 1, pageSize = AppModConfig.maxPageSize;
	//报表文件资源路径
	String repFileResPath = "/expWarnAllLics/";
	//导出列名数组
	String[] colNames = {"预警周期", "所在地", "合计", "未处理数", "已驳回数", "审核中数", "已消除数", "预警处理率"};	
	String[] departmentColNames = {"预警周期", "管理部门", "合计", "未处理数", "已驳回数", "审核中数", "已消除数", "预警处理率"};
	
	//变量数据初始化	
	String startWarnDate = "2018-09-03";
	String endWarnDate = "2018-09-04";
	String distName = null;
	String prefCity = null;
	String province = "上海市";
	String expFileUrl = "test1.txt";
	
	//模拟数据函数
	private ExpWarnAllLicsDTO SimuDataFunc() {
		ExpWarnAllLicsDTO ewalDto = new ExpWarnAllLicsDTO();
		//设置返回数据
		ewalDto.setTime(BCDTimeUtil.convertNormalFrom(null));
		//列表元素设置
		ExpWarnAllLics expWarnAllLics = new ExpWarnAllLics();
		//赋值
		expWarnAllLics.setStartWarnDate(startWarnDate);
		expWarnAllLics.setEndWarnDate(endWarnDate);
		expWarnAllLics.setDistName(distName);
		expWarnAllLics.setPrefCity(prefCity);
		expWarnAllLics.setProvince(province);
		expWarnAllLics.setExpFileUrl(SpringConfig.repfile_srvdn+repFileResPath+expFileUrl);
		//设置模拟数据
		ewalDto.setExpWarnAllLics(expWarnAllLics);
		//消息ID
		ewalDto.setMsgId(AppModConfig.msgId);
		AppModConfig.msgId++;
		//消息id小于0判断
		AppModConfig.msgIdLessThan0Judge();

		return ewalDto;
	}
	
	//生成导出EXCEL文件
	public boolean expWarnAllLicsExcel(String schSelMode,String pathFileName, List<WarnAllLics> dataList, String colNames[]) { 
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
			int totalWarnNum = 0, totalNoProcWarnNum = 0, totalRejectWarnNum = 0, totalAuditWarnNum = 0, totalElimWarnNum = 0;
			float totalWarnProcRate = (float) 0.0;
			for (int i = 0; i < dataList.size(); i++) {
				row = (Row) sheet.createRow(i + startRowIdx);
				row.createCell(0).setCellValue(dataList.get(i).getWarnPeriod());                                                                             //预警周期
				if(CommonUtil.isNotEmpty(schSelMode) && "2".equals(schSelMode)) {
					row.createCell(1).setCellValue(dataList.get(i).getDepartmentName());                                             //管理部门
				}else {
					row.createCell(1).setCellValue(AppModConfig.distIdToNameMap.get(dataList.get(i).getDistName()));                                             //区
				}
				row.createCell(2).setCellValue(dataList.get(i).getTotalWarnNum());                                                                           //合计
				totalWarnNum += dataList.get(i).getTotalWarnNum();
				row.createCell(3).setCellValue(dataList.get(i).getNoProcWarnNum());                                                                          //未处理数
				totalNoProcWarnNum += dataList.get(i).getNoProcWarnNum();
				row.createCell(4).setCellValue(dataList.get(i).getRejectWarnNum());                                                                          //已驳回数
				totalRejectWarnNum += dataList.get(i).getRejectWarnNum();
				row.createCell(5).setCellValue(dataList.get(i).getAuditWarnNum());                                                                           //审核中数
				totalAuditWarnNum += dataList.get(i).getAuditWarnNum();
				row.createCell(6).setCellValue(dataList.get(i).getElimWarnNum());                                                                            //已消除数
				totalElimWarnNum += dataList.get(i).getElimWarnNum();
				row.createCell(7).setCellValue(dataList.get(i).getWarnProcRate() + "%");                                                                            //预警处理率
			}
			//合计全市预警处理率
			totalWarnProcRate = 0;
			if(totalWarnNum > 0) {
				totalWarnProcRate = 100 * ((float) totalElimWarnNum / (float) totalWarnNum);
				BigDecimal bd = new BigDecimal(totalWarnProcRate);
				totalWarnProcRate = bd.setScale(2, BigDecimal.ROUND_HALF_UP).floatValue();
				if (totalWarnProcRate > 100) {
					totalWarnProcRate = 100;
					totalWarnNum = totalElimWarnNum;
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
					cell.setCellValue(totalWarnNum);
				else if(i == 3)
					cell.setCellValue(totalNoProcWarnNum);
				else if(i == 4)
					cell.setCellValue(totalRejectWarnNum);
				else if(i == 5)
					cell.setCellValue(totalAuditWarnNum);
				else if(i == 6)
					cell.setCellValue(totalElimWarnNum);
				else if(i == 7) 
					cell.setCellValue(totalWarnProcRate + "%");
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
	
	//导出证照预警全部证件列表模型函数
	public ExpWarnAllLicsDTO appModFunc(String token, String startWarnDate, String endWarnDate, String distName, String prefCity, String province, 
			String distNames,String departmentIds,String schSelMode,
			Db1Service db1Service, Db2Service db2Service, SaasService saasService,DbHiveWarnService dbHiveWarnService) {
		ExpWarnAllLicsDTO ewalDto = null;
		if (isRealData) { // 真实数据
			String strCurPageNum = String.valueOf(curPageNum), strPageSize = String.valueOf(pageSize);
			if (startWarnDate == null || endWarnDate == null) {   // 按照当天日期获取数据
				startWarnDate = BCDTimeUtil.convertNormalDate(null);
				endWarnDate = startWarnDate;
			}
			WarnAllLicsDTO walDto = walAppMod.appModFunc(token, startWarnDate, endWarnDate, distName, prefCity, province, distNames,departmentIds,
					schSelMode,strCurPageNum, strPageSize, 
					db1Service, db2Service,dbHiveWarnService);
			if(walDto != null) {
				int i, totalCount = walDto.getPageInfo().getPageTotal();
				int pageCount = 0;
				List<WarnAllLics> expExcelList = new ArrayList<>();
				//if(totalCount % pageSize == 0)
				//	pageCount = totalCount/pageSize;
				//else
				//	pageCount = totalCount/pageSize + 1;
				////第一页数据
				//if(walDto.getWarnAllLics() != null) {
				//	expExcelList.addAll(walDto.getWarnAllLics());			
				//}
				//后续页数据
				//for(i = curPageNum+1; i <= pageCount; i++) {
				//	strCurPageNum = String.valueOf(i);
				WarnAllLicsDTO curGpoDto = walAppMod.appModFunc(token, startWarnDate, endWarnDate, distName, prefCity, province,
						distNames,departmentIds,schSelMode,
						"1", String.valueOf(totalCount), 
						db1Service, db2Service,dbHiveWarnService);
				if(curGpoDto.getWarnAllLics() != null) {
					expExcelList.addAll(curGpoDto.getWarnAllLics());
				}
				//}
				//生成导出EXCEL文件
				String repFileName = repFileResPath + UniqueIdGen.uuid() + SpringConfig.repFileFormats[SpringConfig.curRepFileFrmtIdx];
				String pathFileName = SpringConfig.base_dir + repFileName;
				logger.info("导出文件路径：" + pathFileName);
				boolean flag =false;
				if(CommonUtil.isNotEmpty(schSelMode) && "2".equals(schSelMode)) {
					flag = expWarnAllLicsExcel(schSelMode,pathFileName, expExcelList, departmentColNames);
				}else {
					flag = expWarnAllLicsExcel(schSelMode,pathFileName, expExcelList, colNames);
				}
				
				if(flag) {
					//移动文件到其他目录
					//AppModConfig.moveFileToOtherFolder(pathFileName, SpringConfig.tomcatSrvDirs[1] + repFileResPath);
					ewalDto = new ExpWarnAllLicsDTO();
					ExpWarnAllLics expWarnAllLics = new ExpWarnAllLics();
					//时戳
					ewalDto.setTime(BCDTimeUtil.convertNormalFrom(null));
					//导出信息
					expWarnAllLics.setStartWarnDate(startWarnDate);
					expWarnAllLics.setEndWarnDate(endWarnDate);
					expWarnAllLics.setDistName(distName);
					expWarnAllLics.setPrefCity(prefCity);
					expWarnAllLics.setProvince(province);
					expFileUrl = SpringConfig.repfile_srvdn + repFileName;
					logger.info("导出文件URL：" + expFileUrl);
					expWarnAllLics.setExpFileUrl(expFileUrl);
					ewalDto.setExpWarnAllLics(expWarnAllLics);
					//消息ID
					ewalDto.setMsgId(AppModConfig.msgId);
					AppModConfig.msgId++;
					// 消息id小于0判断
					AppModConfig.msgIdLessThan0Judge();
				}
			}
		} else { // 模拟数据
			// 模拟数据函数
			ewalDto = SimuDataFunc();
		}

		return ewalDto;
	}
}
