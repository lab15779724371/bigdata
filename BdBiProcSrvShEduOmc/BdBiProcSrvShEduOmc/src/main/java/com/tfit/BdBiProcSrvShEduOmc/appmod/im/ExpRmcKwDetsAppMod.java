package com.tfit.BdBiProcSrvShEduOmc.appmod.im;

import java.io.ByteArrayOutputStream;
import java.io.File;
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
import com.tfit.BdBiProcSrvShEduOmc.dto.im.ExpRmcKwDets;
import com.tfit.BdBiProcSrvShEduOmc.dto.im.ExpRmcKwDetsDTO;
import com.tfit.BdBiProcSrvShEduOmc.dto.im.RmcKwDets;
import com.tfit.BdBiProcSrvShEduOmc.dto.im.RmcKwDetsDTO;
import com.tfit.BdBiProcSrvShEduOmc.dto.user.UserSetColums;
import com.tfit.BdBiProcSrvShEduOmc.service.Db1Service;
import com.tfit.BdBiProcSrvShEduOmc.service.Db2Service;
import com.tfit.BdBiProcSrvShEduOmc.service.DbHiveRecyclerWasteService;
import com.tfit.BdBiProcSrvShEduOmc.service.SaasService;
import com.tfit.BdBiProcSrvShEduOmc.util.BCDTimeUtil;
import com.tfit.BdBiProcSrvShEduOmc.util.CommonUtil;
import com.tfit.BdBiProcSrvShEduOmc.util.FtpUtil;
import com.tfit.BdBiProcSrvShEduOmc.util.UniqueIdGen;

//导出团餐公司餐厨垃圾详情列表应用模型
public class ExpRmcKwDetsAppMod {
	private static final Logger logger = LogManager.getLogger(ExpRmcKwDetsAppMod.class.getName());
	
	//团餐公司餐厨垃圾详情列表应用模型
	private RmcKwDetsAppMod rkdAppMod = new RmcKwDetsAppMod();
	
	//是否为真实数据标识
	private static boolean isRealData = true;
	//页号、页大小和总页数
	int curPageNum = 1, pageTotal = 1, pageSize = AppModConfig.maxPageSize;
	//报表文件资源路径
	String repFileResPath = "/expRmcKwDets/";
	//导出列名数组
	String[] colNames = {"序号","回收日期", "所在地", "团餐公司", "数量（桶）", "回收单位", "回收人", "回收单据"};	
	//变量数据初始化
	String recStartDate = "2018-09-03";
	String recEndDate = "2018-09-04";
	String ppName = null;
	String distName = null;
	String prefCity = null;
	String province = "上海市";
	String schType = null;
	String rmcName = null;
	String recComany = null;
	String recPerson = null;
	String expFileUrl = "fc8bafe943214d65a67a7d8b93d0185a.xls";
	
	//模拟数据函数
	private ExpRmcKwDetsDTO SimuDataFunc() {
		ExpRmcKwDetsDTO erkdDto = new ExpRmcKwDetsDTO();
		//设置返回数据
		erkdDto.setTime(BCDTimeUtil.convertNormalFrom(null));
		//列表元素设置
		ExpRmcKwDets expRmcKwDets = new ExpRmcKwDets();
		//赋值
		expRmcKwDets.setRecStartDate(recStartDate);
		expRmcKwDets.setRecEndDate(recEndDate);
		expRmcKwDets.setDistName(distName);
		expRmcKwDets.setPrefCity(prefCity);
		expRmcKwDets.setProvince(province);
		expRmcKwDets.setPpName(ppName);
		expRmcKwDets.setRmcName(rmcName);
		expRmcKwDets.setRecComany(recComany);
		expRmcKwDets.setRecPerson(recPerson);
		expRmcKwDets.setExpFileUrl(SpringConfig.repfile_srvdn+repFileResPath+expFileUrl);
		//设置模拟数据
		erkdDto.setExpRmcKwDets(expRmcKwDets);
		//消息ID
		erkdDto.setMsgId(AppModConfig.msgId);
		AppModConfig.msgId++;
		//消息id小于0判断
		AppModConfig.msgIdLessThan0Judge();

		return erkdDto;
	}
	
	//生成导出EXCEL文件
	public boolean expRmcKwDetsExcel(String pathFileName, List<RmcKwDets> dataList,List<UserSetColums> userSetColumsList, String colNames[]) { 
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
			// 添加样式
			Row row = null;
			Cell cell = null;
			// 创建第一行
			row = (Row) sheet.createRow(startRowIdx);
			//获取excell单元风格
		  	CellStyle style = AppModConfig.getExcellCellStyle(wb);
		  	//动态列
		  	if(userSetColumsList !=null && userSetColumsList.size() > 0) {
		  		List<String> colNamesTempList = new ArrayList<String>(); //重新设置列
				for(UserSetColums obj : userSetColumsList) {
				  if(obj != null && obj.isChecked()) {
					  colNamesTempList.add(obj.getLabel());
				  }
			    }
				colNames = colNamesTempList.toArray(new String[colNamesTempList.size()]);
		  	}
		  	
		  	String[] colVals = new String[colNames.length];
		  	
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
			for (int i = 0; i < dataList.size(); i++) {
				row = (Row) sheet.createRow(i + startRowIdx);
				if(userSetColumsList !=null && userSetColumsList.size() > 0) {
					for(UserSetColums obj : userSetColumsList) {
					  if(obj != null && obj.isChecked()) {
							if("sortNo".equals(obj.getKey())) 
								row.createCell(0).setCellValue(i+1);//序号
					    if("recDate".equals(obj.getKey())) 
					      	row.createCell(1).setCellValue(dataList.get(i).getRecDate());                                         //回收日期	
					    if("distName".equals(obj.getKey())) 
					      	row.createCell(2).setCellValue(AppModConfig.distIdToNameMap.get(dataList.get(i).getDistName()));      //区	
					    if("rmcName".equals(obj.getKey())) 
					      	row.createCell(3).setCellValue(dataList.get(i).getRmcName());                                         //团餐公司	
					    if("recNum".equals(obj.getKey())) 
					      	row.createCell(4).setCellValue(dataList.get(i).getRecNum() + " 桶");                                   //数量	
					    if("recComany".equals(obj.getKey())) 
					      	row.createCell(5).setCellValue(dataList.get(i).getRecComany());                                       //回收单位	
					    if("recPerson".equals(obj.getKey())) 
					      	row.createCell(6).setCellValue(dataList.get(i).getRecPerson());                                       //回收人	
					    if("recBillNum".equals(obj.getKey())) 
					      	row.createCell(7).setCellValue(dataList.get(i).getRecBillNum());                                      //回收单据	

					  }
					}
				  }else {
					row.createCell(0).setCellValue(i+1);
					row.createCell(1).setCellValue(dataList.get(i).getRecDate());                                         //回收日期
					row.createCell(2).setCellValue(AppModConfig.distIdToNameMap.get(dataList.get(i).getDistName()));      //区					
					row.createCell(3).setCellValue(dataList.get(i).getRmcName());                                         //团餐公司
					row.createCell(4).setCellValue(dataList.get(i).getRecNum() + " 桶");                                   //数量
					row.createCell(5).setCellValue(dataList.get(i).getRecComany());                                       //回收单位
					row.createCell(6).setCellValue(dataList.get(i).getRecPerson());                                       //回收人
					row.createCell(7).setCellValue(dataList.get(i).getRecBillNum());                                      //回收单据
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
	
	//导出团餐公司餐厨垃圾详情列表模型函数
	public ExpRmcKwDetsDTO appModFunc(String token, String recStartDate, String recEndDate, String distName, 
			String prefCity, String province, String ppName, String rmcName, String recComany, String recPerson, 
			String distNames,
			Db1Service db1Service, Db2Service db2Service, SaasService saasService,
			DbHiveRecyclerWasteService dbHiveRecyclerWasteService) {
		ExpRmcKwDetsDTO erkdDto = null;
		if (isRealData) { // 真实数据
			String strCurPageNum = String.valueOf(curPageNum), strPageSize = String.valueOf(pageSize);
			if (recStartDate == null || recEndDate == null) {   // 按照当天日期获取数据
				recStartDate = BCDTimeUtil.convertNormalDate(null);
				recEndDate = recStartDate;
			}
			RmcKwDetsDTO rkdDto = rkdAppMod.appModFunc(token, recStartDate, recEndDate, distName, 
					prefCity, province, ppName, rmcName, recComany, recPerson, distNames,
					strCurPageNum, strPageSize, db1Service, db2Service, saasService,dbHiveRecyclerWasteService);
			if(rkdDto != null) {
				int i, totalCount = rkdDto.getPageInfo().getPageTotal();
				int pageCount = 0;
				List<RmcKwDets> expExcelList = new ArrayList<>();
				if(totalCount % pageSize == 0)
					pageCount = totalCount/pageSize;
				else
					pageCount = totalCount/pageSize + 1;
				//第一页数据
				if(rkdDto.getRmcKwDets() != null) {
					expExcelList.addAll(rkdDto.getRmcKwDets());
				}
				//后续页数据
				for(i = curPageNum+1; i <= pageCount; i++) {
					strCurPageNum = String.valueOf(i);
					RmcKwDetsDTO currkdDto = rkdAppMod.appModFunc(token, recStartDate, recEndDate, distName, prefCity, 
							province, ppName, rmcName, recComany, recPerson,  distNames,
							strCurPageNum, strPageSize, db1Service, db2Service, saasService,dbHiveRecyclerWasteService);
					if(currkdDto.getRmcKwDets() != null) {
						expExcelList.addAll(currkdDto.getRmcKwDets());
					}
				}
				//生成导出EXCEL文件
				String repFileName = repFileResPath + UniqueIdGen.uuid() + SpringConfig.repFileFormats[SpringConfig.curRepFileFrmtIdx];
				String pathFileName = SpringConfig.base_dir + repFileName;
				logger.info("导出文件路径：" + pathFileName);
				List<UserSetColums> userSetColumsList =CommonUtil.getUserSetColumList(token,"rmcKwDets", db2Service);
				boolean flag = expRmcKwDetsExcel(pathFileName, expExcelList,userSetColumsList, colNames);
				if(flag) {
					//移动文件到其他目录
					//AppModConfig.moveFileToOtherFolder(pathFileName, SpringConfig.tomcatSrvDirs[1] + repFileResPath);
					erkdDto = new ExpRmcKwDetsDTO();
					ExpRmcKwDets expRmcKwDets = new ExpRmcKwDets();
					//时戳
					erkdDto.setTime(BCDTimeUtil.convertNormalFrom(null));
					//导出信息
					expRmcKwDets.setRecStartDate(recStartDate);
					expRmcKwDets.setRecEndDate(recEndDate);
					expRmcKwDets.setDistName(distName);
					expRmcKwDets.setPrefCity(prefCity);
					expRmcKwDets.setProvince(province);
					expRmcKwDets.setPpName(ppName);
					expRmcKwDets.setRmcName(rmcName);
					expRmcKwDets.setRecComany(recComany);
					expRmcKwDets.setRecPerson(recPerson);
					expFileUrl = SpringConfig.repfile_srvdn + repFileName;
					logger.info("导出文件URL：" + expFileUrl);
					expRmcKwDets.setExpFileUrl(expFileUrl);
					erkdDto.setExpRmcKwDets(expRmcKwDets);
					//消息ID
					erkdDto.setMsgId(AppModConfig.msgId);
					AppModConfig.msgId++;
					// 消息id小于0判断
					AppModConfig.msgIdLessThan0Judge();
				}
			}
		} else { // 模拟数据
			// 模拟数据函数
			erkdDto = SimuDataFunc();
		}

		return erkdDto;
	}
}
