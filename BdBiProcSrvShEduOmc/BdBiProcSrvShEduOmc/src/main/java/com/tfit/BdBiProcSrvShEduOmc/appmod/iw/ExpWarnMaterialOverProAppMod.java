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
import com.tfit.BdBiProcSrvShEduOmc.dto.iw.ExpWarnMaterialOverProDTO;
import com.tfit.BdBiProcSrvShEduOmc.dto.iw.WarnMaterialOverPro;
import com.tfit.BdBiProcSrvShEduOmc.dto.iw.WarnMaterialOverProDTO;
import com.tfit.BdBiProcSrvShEduOmc.service.Db1Service;
import com.tfit.BdBiProcSrvShEduOmc.service.Db2Service;
import com.tfit.BdBiProcSrvShEduOmc.service.DbHiveWarnService;
import com.tfit.BdBiProcSrvShEduOmc.service.SaasService;
import com.tfit.BdBiProcSrvShEduOmc.util.BCDTimeUtil;
import com.tfit.BdBiProcSrvShEduOmc.util.FtpUtil;
import com.tfit.BdBiProcSrvShEduOmc.util.UniqueIdGen;

//导出证照预警全部证件列表应用模型
public class ExpWarnMaterialOverProAppMod {
	private static final Logger logger = LogManager.getLogger(ExpWarnMaterialOverProAppMod.class.getName());
	
	//证照预警全部证件列表应用模型
	private WarnMaterialOverProAppMod walAppMod = new WarnMaterialOverProAppMod();
	
	//是否为真实数据标识
	private static boolean isRealData = true;
	//页号、页大小和总页数
	int curPageNum = 1, pageTotal = 1, pageSize = AppModConfig.maxPageSize;
	//报表文件资源路径
	String repFileResPath = "/expWarnMaterialOverPro/";
	//导出列名数组
	String[] colNames = {"预警周期", "管理部门", "预警总数", "警示中","已消除数", "预警处理率"};	
	
	//变量数据初始化	
	String startWarnDate = "2018-09-03";
	String endWarnDate = "2018-09-04";
	String distName = null;
	String prefCity = null;
	String province = "上海市";
	String expFileUrl = "test1.txt";
	
	//模拟数据函数
	private ExpWarnMaterialOverProDTO SimuDataFunc() {
		ExpWarnMaterialOverProDTO ewalDto = new ExpWarnMaterialOverProDTO();
		//设置返回数据
		ewalDto.setTime(BCDTimeUtil.convertNormalFrom(null));
		//列表元素设置
		ExpWarnAllLics expWarnMaterialOverPro = new ExpWarnAllLics();
		//赋值
		expWarnMaterialOverPro.setStartWarnDate(startWarnDate);
		expWarnMaterialOverPro.setEndWarnDate(endWarnDate);
		expWarnMaterialOverPro.setDistName(distName);
		expWarnMaterialOverPro.setPrefCity(prefCity);
		expWarnMaterialOverPro.setProvince(province);
		expWarnMaterialOverPro.setExpFileUrl(SpringConfig.repfile_srvdn+repFileResPath+expFileUrl);
		//设置模拟数据
		ewalDto.setExpWarnMaterialOverPro(expWarnMaterialOverPro);
		//消息ID
		ewalDto.setMsgId(AppModConfig.msgId);
		AppModConfig.msgId++;
		//消息id小于0判断
		AppModConfig.msgIdLessThan0Judge();

		return ewalDto;
	}
	
	//生成导出EXCEL文件
	public boolean expWarnMaterialOverProExcel(String pathFileName, List<WarnMaterialOverPro> dataList, String colNames[]) { 
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
			int totalWarnNum = 0, totalNoProcWarnNum = 0, totalElimWarnNum = 0;
			float totalWarnProcRate = (float) 0.0;
			for (int i = 0; i < dataList.size(); i++) {
				row = (Row) sheet.createRow(i + startRowIdx);
				row.createCell(0).setCellValue(dataList.get(i).getWarnPeriod());                                                                             //预警周期
				row.createCell(1).setCellValue(dataList.get(i).getDepartmentName());                                       //管理部门
				row.createCell(2).setCellValue(dataList.get(i).getTotalWarnNum());                                                                           //合计
				totalWarnNum += dataList.get(i).getTotalWarnNum();
				row.createCell(3).setCellValue(dataList.get(i).getNoProcWarnNum());                                                                          //未处理数
				totalNoProcWarnNum += dataList.get(i).getNoProcWarnNum();
				row.createCell(4).setCellValue(dataList.get(i).getElimWarnNum());                                                                            //已消除数
				totalElimWarnNum += dataList.get(i).getElimWarnNum();
				row.createCell(5).setCellValue(dataList.get(i).getWarnProcRate() + "%");                                                                            //预警处理率
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
					cell.setCellValue(totalElimWarnNum);
				else if(i == 5) 
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
	public ExpWarnMaterialOverProDTO appModFunc(String token, String startWarnDate, String endWarnDate, String distName, String prefCity, String province, 
			String departmentIds,
			Db1Service db1Service, Db2Service db2Service, SaasService saasService,DbHiveWarnService dbHiveWarnService) {
		ExpWarnMaterialOverProDTO ewalDto = null;
		if (isRealData) { // 真实数据
			if (startWarnDate == null || endWarnDate == null) {   // 按照当天日期获取数据
				startWarnDate = BCDTimeUtil.convertNormalDate(null);
				endWarnDate = startWarnDate;
			}
			WarnMaterialOverProDTO walDto = walAppMod.appModFunc(token, startWarnDate, endWarnDate, distName,
					prefCity, province, departmentIds,
					"-1", "-1", 
					db1Service, db2Service, dbHiveWarnService);
					
			if(walDto != null) {
				List<WarnMaterialOverPro> expExcelList = new ArrayList<>();
				if(walDto.getWarnMaterialOverPro() != null) {
					expExcelList.addAll(walDto.getWarnMaterialOverPro());
				}
				//生成导出EXCEL文件
				String repFileName = repFileResPath + UniqueIdGen.uuid() + SpringConfig.repFileFormats[SpringConfig.curRepFileFrmtIdx];
				String pathFileName = SpringConfig.base_dir + repFileName;
				logger.info("导出文件路径：" + pathFileName);
				boolean flag = expWarnMaterialOverProExcel(pathFileName, expExcelList, colNames);
				if(flag) {
					//移动文件到其他目录
					AppModConfig.moveFileToOtherFolder(pathFileName, SpringConfig.tomcatSrvDirs[1] + repFileResPath);
					ewalDto = new ExpWarnMaterialOverProDTO();
					ExpWarnAllLics expWarnMaterialOverPro = new ExpWarnAllLics();
					//时戳
					ewalDto.setTime(BCDTimeUtil.convertNormalFrom(null));
					//导出信息
					expWarnMaterialOverPro.setStartWarnDate(startWarnDate);
					expWarnMaterialOverPro.setEndWarnDate(endWarnDate);
					expWarnMaterialOverPro.setDistName(distName);
					expWarnMaterialOverPro.setPrefCity(prefCity);
					expWarnMaterialOverPro.setProvince(province);
					expFileUrl = SpringConfig.repfile_srvdn + repFileName;
					logger.info("导出文件URL：" + expFileUrl);
					expWarnMaterialOverPro.setExpFileUrl(expFileUrl);
					ewalDto.setExpWarnMaterialOverPro(expWarnMaterialOverPro);
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
