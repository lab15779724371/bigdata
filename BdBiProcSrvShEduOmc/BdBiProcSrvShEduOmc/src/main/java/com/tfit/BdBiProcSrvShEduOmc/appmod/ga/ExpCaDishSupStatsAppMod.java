package com.tfit.BdBiProcSrvShEduOmc.appmod.ga;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

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
import com.tfit.BdBiProcSrvShEduOmc.dto.ga.CaDishSupStats;
import com.tfit.BdBiProcSrvShEduOmc.dto.ga.CaDishSupStatsDTO;
import com.tfit.BdBiProcSrvShEduOmc.dto.ga.ExpCaDishSupStats;
import com.tfit.BdBiProcSrvShEduOmc.dto.ga.ExpCaDishSupStatsDTO;
import com.tfit.BdBiProcSrvShEduOmc.service.Db1Service;
import com.tfit.BdBiProcSrvShEduOmc.service.Db2Service;
import com.tfit.BdBiProcSrvShEduOmc.service.DbHiveService;
import com.tfit.BdBiProcSrvShEduOmc.service.SaasService;
import com.tfit.BdBiProcSrvShEduOmc.util.BCDTimeUtil;
import com.tfit.BdBiProcSrvShEduOmc.util.CommonUtil;
import com.tfit.BdBiProcSrvShEduOmc.util.UniqueIdGen;
import com.tfit.BdBiProcSrvShEduOmc.util.export.RelectUtil;

//导出综合分析菜品供应统计列表应用模型
public class ExpCaDishSupStatsAppMod {
	private static final Logger logger = LogManager.getLogger(ExpCaDishSupStatsAppMod.class.getName());
	
	//综合分析菜品供应统计列表应用模型
	private CaDishSupStatsAppMod cmssAppMod = new CaDishSupStatsAppMod();
	
	//是否为真实数据标识
	private static boolean isRealData = true;
	//页号、页大小和总页数
	int curPageNum = 1, pageTotal = 1, pageSize = AppModConfig.maxPageSize;
	//报表文件资源路径
	String repFileResPath = "/expCaDishSupStats/";
	//导出列名数组
	String[] colNames = {"排序", "菜品名称", "类别", "供应份数"};
	int methodIndex =2;	
	
	
	//变量数据初始化		
	String repastStartDate = "2018-09-03";
	String repasEndDate = "2018-09-04";
	String distName = null;
	String prefCity = null;
	String province = "上海市";
	String schName = null;
	String dishType = null;
	String caterType = null;
	String expFileUrl = "test1.txt";
	
	//模拟数据函数
	private ExpCaDishSupStatsDTO SimuDataFunc() {
		ExpCaDishSupStatsDTO ecdssDto = new ExpCaDishSupStatsDTO();
		//设置返回数据
		ecdssDto.setTime(BCDTimeUtil.convertNormalFrom(null));
		//列表元素设置
		ExpCaDishSupStats expCaDishSupStats = new ExpCaDishSupStats();
		//赋值
		expCaDishSupStats.setRepastStartDate(repastStartDate);
		expCaDishSupStats.setRepasEndDate(repasEndDate);
		expCaDishSupStats.setDistName(distName);
		expCaDishSupStats.setPrefCity(prefCity);
		expCaDishSupStats.setProvince(province);
		expCaDishSupStats.setSchName(schName);
		expCaDishSupStats.setDishType(dishType);
		expCaDishSupStats.setCaterType(caterType);
		expCaDishSupStats.setExpFileUrl(SpringConfig.repfile_srvdn+repFileResPath+expFileUrl);
		//设置模拟数据
		ecdssDto.setExpCaDishSupStats(expCaDishSupStats);
		//消息ID
		ecdssDto.setMsgId(AppModConfig.msgId);
		AppModConfig.msgId++;
		//消息id小于0判断
		AppModConfig.msgIdLessThan0Judge();

		return ecdssDto;
	}
	
	//生成导出EXCEL文件
	public boolean expCaDishSupStatsExcel(String pathFileName, List<CaDishSupStats> dataList, String colNames[]) { 
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
			for (int i = 0; i < dataList.size(); i++) {
				row = (Row) sheet.createRow(i + startRowIdx);
				row.createCell(0).setCellValue(dataList.get(i).getSn());                                            //排序
				row.createCell(1).setCellValue(dataList.get(i).getDishName());                                      //标准名称
				row.createCell(2).setCellValue(dataList.get(i).getDishType());                                      //原料类别
				row.createCell(3).setCellValue(dataList.get(i).getSupNum() + " 份");                                 //实际数量
			}
			retFlag = CommonUtil.commonExportExcel(retFlag, wb, excelPath);
        }
        
        return retFlag;
    }
	
	//生成导出EXCEL文件(读取数据方式改变（优化，调用hive效率更高），支持6w以上导出，6w以上分多个sheet)
	public boolean expCaDishSupStatsExcelTwo(String pathFileName, List<CaDishSupStats> dataList, String colNames[]) { 
		boolean retFlag = true;
		Workbook wb = null;
        String excelPath = pathFileName, fileType = "";
        File file = new File(excelPath);
        int idx1 = excelPath.lastIndexOf(".xls"), idx2 = excelPath.lastIndexOf(".xlsx");
        /*if(dataList.size() > AppModConfig.maxPageSize)
        	return false;*/
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
            	 Map<Integer, List<CaDishSupStats>> sheetMap = RelectUtil.<CaDishSupStats>daData(dataList);
                 Set<Integer> keys = sheetMap.keySet();
                 Sheet sheet = null;
                 Row row = null;
                 Cell cell = null;
                 String[] colVals = new String[colNames.length];
                 
                 try {
          			int rowIndex=0;
          	        for (Iterator<Integer> iterator = keys.iterator(); iterator.hasNext();) {
          	            Integer SheetKey = iterator.next();
          	            sheet = wb.createSheet(("sheet"+(SheetKey+1)).toString());
          	            List<CaDishSupStats> sheetRows = sheetMap.get(SheetKey);
          	            rowIndex=0;
          	            
          				// 创建第一行（标题）
          				row = (Row) sheet.createRow(rowIndex++);
          				for (int i = 0; i < colNames.length; i++) {
          					cell = row.createCell(i);
          					try {
          						logger.info(colNames[i] + " ");
          						colVals[i] = new String(colNames[i].getBytes(), "utf-8");
          						cell.setCellValue(colNames[i]);
          					} catch (UnsupportedEncodingException e) {
          						logger.info("异常："+e.getMessage());
          						// TODO 自动生成的 catch 块
          						e.printStackTrace();
          					}
          				}
          				
          	            for (int i = 0, len = sheetRows.size(); i < len; i++) {
          	            	row = sheet.createRow(rowIndex++);
          					row.createCell(0).setCellValue(dataList.get(i).getSn());                                            //排序
          					row.createCell(1).setCellValue(dataList.get(i).getDishName());                                      //标准名称
          					row.createCell(2).setCellValue(dataList.get(i).getDishType());                                      //原料类别
          					row.createCell(3).setCellValue(dataList.get(i).getSupNum() + " 份");                                 //实际数量
          	            }
          	        }
          		} catch (IllegalArgumentException e) {
          			logger.info("异常："+e.getMessage());
          			// TODO Auto-generated catch block
          		} catch (SecurityException e) {
          			logger.info("异常："+e.getMessage());
          			// TODO Auto-generated catch block
          			e.printStackTrace();
          		} 
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
        //创建文件流导出
		retFlag = CommonUtil.commonExportExcel(retFlag, wb, excelPath);
		
        return retFlag;
    }
	
	//导出综合分析菜品供应统计列表模型函数
	public ExpCaDishSupStatsDTO appModFunc(String token, String repastStartDate, String repasEndDate, String distName, String prefCity, 
			String province, String schName, String dishType, String caterType, String dishName,
			Db1Service db1Service, Db2Service db2Service, SaasService saasService,DbHiveService dbHiveService) {
		ExpCaDishSupStatsDTO ecdssDto = null;
		if (isRealData) { // 真实数据
			String strCurPageNum = String.valueOf(curPageNum), strPageSize = String.valueOf(pageSize);
			if (repastStartDate == null || repasEndDate == null) {   // 按照当天日期获取数据
				repastStartDate = BCDTimeUtil.convertNormalDate(null);
				repasEndDate = repastStartDate;
			}
			//生成导出EXCEL文件
			String repFileName = repFileResPath + UniqueIdGen.uuid() + SpringConfig.repFileFormats[SpringConfig.curRepFileFrmtIdx];
			String pathFileName = SpringConfig.base_dir + repFileName;
			logger.info("导出文件路径：" + pathFileName);
			boolean flag = false;
			if(methodIndex == 1) {
				List<CaDishSupStats> expExcelList = getDataListFromRedis(token, repastStartDate, repasEndDate, distName,
						prefCity, province, schName, dishType, caterType, dishName, db1Service, db2Service, saasService,
						dbHiveService, strCurPageNum, strPageSize);
			    flag = expCaDishSupStatsExcel(pathFileName, expExcelList, colNames);
			}else if (methodIndex == 2) {
				List<CaDishSupStats> expExcelList = getDataListFromHive(token, repastStartDate, repasEndDate, distName,
						prefCity, province, schName, dishType, caterType, dishName, db1Service, db2Service, saasService,
						dbHiveService, strCurPageNum, strPageSize);
				flag = expCaDishSupStatsExcelTwo(pathFileName, expExcelList, colNames);
			}
			if(flag) {
				//移动文件到其他目录
				//AppModConfig.moveFileToOtherFolder(pathFileName, SpringConfig.tomcatSrvDirs[1] + repFileResPath);
				ecdssDto = new ExpCaDishSupStatsDTO();
				ExpCaDishSupStats expCaDishSupStats = new ExpCaDishSupStats();
				//时戳
				ecdssDto.setTime(BCDTimeUtil.convertNormalFrom(null));
				//导出信息
				expCaDishSupStats.setRepastStartDate(repastStartDate);
				expCaDishSupStats.setRepasEndDate(repasEndDate);
				expCaDishSupStats.setDistName(distName);
				expCaDishSupStats.setPrefCity(prefCity);
				expCaDishSupStats.setProvince(province);
				expCaDishSupStats.setSchName(schName);
				expCaDishSupStats.setDishType(dishType);
				expCaDishSupStats.setCaterType(caterType);
				expFileUrl = SpringConfig.repfile_srvdn + repFileName;
				logger.info("导出文件URL：" + expFileUrl);
				expCaDishSupStats.setExpFileUrl(expFileUrl);
				ecdssDto.setExpCaDishSupStats(expCaDishSupStats);
				//消息ID
				ecdssDto.setMsgId(AppModConfig.msgId);
				AppModConfig.msgId++;
				// 消息id小于0判断
				AppModConfig.msgIdLessThan0Judge();
			}
			
		} else { // 模拟数据
			// 模拟数据函数
			ecdssDto = SimuDataFunc();
		}

		return ecdssDto;
	}

	/**
	 * 适合从redis取数的读取方法（效率更高）
	 * @param token
	 * @param repastStartDate
	 * @param repasEndDate
	 * @param distName
	 * @param prefCity
	 * @param province
	 * @param schName
	 * @param dishType
	 * @param caterType
	 * @param dishName
	 * @param db1Service
	 * @param db2Service
	 * @param saasService
	 * @param dbHiveService
	 * @param strCurPageNum
	 * @param strPageSize
	 * @return
	 */
	private List<CaDishSupStats> getDataListFromRedis(String token, String repastStartDate, String repasEndDate,
			String distName, String prefCity, String province, String schName, String dishType, String caterType,
			String dishName, Db1Service db1Service, Db2Service db2Service, SaasService saasService,
			DbHiveService dbHiveService, String strCurPageNum, String strPageSize) {
		CaDishSupStatsDTO pdlDto = cmssAppMod.appModFunc(token, repastStartDate, repasEndDate, distName, prefCity, province, schName, 
				dishType, caterType, dishName, strCurPageNum, strPageSize, 
				db1Service, db2Service, saasService,dbHiveService);
		if(pdlDto != null) {
			
		}
		int i, totalCount = pdlDto.getPageInfo().getPageTotal();
		int pageCount = 0;
		List<CaDishSupStats> expExcelList = new ArrayList<>();
		if(totalCount % pageSize == 0)
			pageCount = totalCount/pageSize;
		else
			pageCount = totalCount/pageSize + 1;
		//第一页数据
		if(pdlDto.getCaDishSupStats() != null) {
			expExcelList.addAll(pdlDto.getCaDishSupStats());			
		}
		//后续页数据
		for(i = curPageNum+1; i <= pageCount; i++) {
			strCurPageNum = String.valueOf(i);
			CaDishSupStatsDTO curGpoDto = cmssAppMod.appModFunc(token, repastStartDate, repasEndDate, distName, prefCity, 
					province, schName, dishType, caterType, dishName, strCurPageNum, strPageSize, 
					db1Service, db2Service, saasService,dbHiveService);
			if(curGpoDto.getCaDishSupStats() != null) {
				expExcelList.addAll(curGpoDto.getCaDishSupStats());
			}
		}
		return expExcelList;
	}
	
	/**
	 * 适合从hive取数的读取方法（效率更高）
	 * @param token
	 * @param repastStartDate
	 * @param repasEndDate
	 * @param distName
	 * @param prefCity
	 * @param province
	 * @param schName
	 * @param dishType
	 * @param caterType
	 * @param dishName
	 * @param db1Service
	 * @param db2Service
	 * @param saasService
	 * @param dbHiveService
	 * @param strCurPageNum
	 * @param strPageSize
	 * @return
	 */
	private List<CaDishSupStats> getDataListFromHive(String token, String repastStartDate, String repasEndDate,
			String distName, String prefCity, String province, String schName, String dishType, String caterType,
			String dishName, Db1Service db1Service, Db2Service db2Service, SaasService saasService,
			DbHiveService dbHiveService, String strCurPageNum, String strPageSize) {
		List<CaDishSupStats> expExcelList = new ArrayList<CaDishSupStats>();
		
		CaDishSupStatsDTO pdlDto = cmssAppMod.appModFunc(token, repastStartDate, repasEndDate, distName, prefCity, province, schName, 
				dishType, caterType, dishName, "1", "1", 
				db1Service, db2Service, saasService,dbHiveService);
		if(pdlDto != null) {
			int totalCount = pdlDto.getPageInfo().getPageTotal();
			strCurPageNum = String.valueOf(1);
			strPageSize = String.valueOf(totalCount);
			CaDishSupStatsDTO curGpoDto = cmssAppMod.appModFunc(token, repastStartDate, repasEndDate, distName, prefCity, 
					province, schName, dishType, caterType, dishName, strCurPageNum, strPageSize, 
					db1Service, db2Service, saasService,dbHiveService);
			if(curGpoDto.getCaDishSupStats() != null) {
				expExcelList.addAll(curGpoDto.getCaDishSupStats());
			}
		}
		return expExcelList;
	}
}