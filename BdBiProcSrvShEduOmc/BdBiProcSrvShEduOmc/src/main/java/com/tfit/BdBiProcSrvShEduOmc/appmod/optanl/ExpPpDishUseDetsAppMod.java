package com.tfit.BdBiProcSrvShEduOmc.appmod.optanl;

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

import com.tfit.BdBiProcSrvShEduOmc.common.ApiResponse;
import com.tfit.BdBiProcSrvShEduOmc.config.AppModConfig;
import com.tfit.BdBiProcSrvShEduOmc.config.SpringConfig;
import com.tfit.BdBiProcSrvShEduOmc.dto.im.ExpCommonDTO;
import com.tfit.BdBiProcSrvShEduOmc.dto.optanl.PpDishDets;
import com.tfit.BdBiProcSrvShEduOmc.dto.optanl.PpDishUseDets;
import com.tfit.BdBiProcSrvShEduOmc.dto.optanl.PpDishUseDetsDTO;
import com.tfit.BdBiProcSrvShEduOmc.service.Db1Service;
import com.tfit.BdBiProcSrvShEduOmc.service.Db2Service;
import com.tfit.BdBiProcSrvShEduOmc.service.DbHiveDishService;
import com.tfit.BdBiProcSrvShEduOmc.service.SaasService;
import com.tfit.BdBiProcSrvShEduOmc.util.UniqueIdGen;

//导出项目点排菜详情列表应用模型
public class ExpPpDishUseDetsAppMod {
	private static final Logger logger = LogManager.getLogger(ExpPpDishUseDetsAppMod.class.getName());
	
	//项目点排菜详情列表应用模型
	private PpDishUseDetsAppMod epddAppMod = new PpDishUseDetsAppMod();
	
	//是否为真实数据标识
	private static boolean isRealData = true;
	//页号、页大小和总页数
	int curPageNum = 1, pageTotal = 1, pageSize = AppModConfig.maxPageSize;
	//报表文件资源路径
	String repFileResPath = "/expPpDishDets/";
	//导出列名数组
	String[] colNames = {"序号","日期", "所在地","管理部门","项目点名称","学制", "办学性质", "团餐公司", "供餐", "不供餐原因", "排菜", "用料确认", 
			"指派", "配送", "验收", "留样"};	
	//变量数据初始化
	String startDate = "2018-09-03";
	String endDate = "2018-09-04";
	String ppName = null;
	String distName = null;
	String prefCity = null;
	String province = "上海市";
	int dishFlag = -1;
	String rmcName = null;
	int schType = -1;
	int mealFlag = -1;
	int optMode = -1;
	int sendFlag = -1;
	String expFileUrl = "test1.txt";
	
	//模拟数据函数
	private ApiResponse<ExpCommonDTO> SimuDataFunc() {
		//列表元素设置
		ExpCommonDTO expPpDishDets = new ExpCommonDTO();
		//赋值
		expPpDishDets.setStartDate(startDate);
		expPpDishDets.setEndDate(endDate);
		expPpDishDets.setPpName(ppName);
		expPpDishDets.setDistName(distName);
		expPpDishDets.setPrefCity(prefCity);
		expPpDishDets.setProvince(province);
		expPpDishDets.setSendFlag(sendFlag);
		expPpDishDets.setExpFileUrl(SpringConfig.repfile_srvdn+repFileResPath+expFileUrl);
		return new ApiResponse<>(expPpDishDets);
	}
	
	//生成导出EXCEL文件
	public boolean expPpDishDetsExcel(String pathFileName, List<PpDishUseDets> result, String colNames[]) { 
		boolean retFlag = true;
		Workbook wb = null;
        String excelPath = pathFileName, fileType = "";
        File file = new File(excelPath);
        Sheet sheet = null;
        int idx1 = excelPath.lastIndexOf(".xls"), idx2 = excelPath.lastIndexOf(".xlsx");
        if(result!=null && result.size() > AppModConfig.maxPageSize)
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
        	int startColumnIdx = 0;
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
			for (int i = 0; i < result.size(); i++) {
				startColumnIdx = 0;
				row = (Row) sheet.createRow(i + startRowIdx);row.createCell(startColumnIdx++).setCellValue(i+1);				
				row.createCell(startColumnIdx++).setCellValue(result.get(i).getDishDate());                                            //日期
				row.createCell(startColumnIdx++).setCellValue(AppModConfig.distIdToNameMap.get(result.get(i).getDistName()));          //所在地
				row.createCell(startColumnIdx++).setCellValue(result.get(i).getCompDep());                                          //管理部门
				row.createCell(startColumnIdx++).setCellValue(result.get(i).getPpName());                                         //项目点名称
				row.createCell(startColumnIdx++).setCellValue(result.get(i).getSchType());                                            //学制
				row.createCell(startColumnIdx++).setCellValue(result.get(i).getSchProp());                                            //办学性质
				row.createCell(startColumnIdx++).setCellValue(result.get(i).getRmcName());                                            //团餐公司
				row.createCell(startColumnIdx++).setCellValue((1==result.get(i).getMealFlag())?"√":"×");       //供餐
				row.createCell(startColumnIdx++).setCellValue(result.get(i).getReason());       //不供餐原因
				row.createCell(startColumnIdx++).setCellValue((1==result.get(i).getDishFlag())?"√":"×");       //排菜
				row.createCell(startColumnIdx++).setCellValue((1==result.get(i).getMaterialStatus())?"√":"×");       //用料确认
				row.createCell(startColumnIdx++).setCellValue((1==result.get(i).getAssignPlanStatus())?"√":"×");       //指派
				row.createCell(startColumnIdx++).setCellValue((1==result.get(i).getDispPlanStatus())?"√":"×");       //配送
				row.createCell(startColumnIdx++).setCellValue((1==result.get(i).getAcceptStatus())?"√":"×");       //验收
				row.createCell(startColumnIdx++).setCellValue((1==result.get(i).getHaveReserve())?"√":"×");       //留样
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
	
	//导出项目点排菜详情列表模型函数
	public ApiResponse<ExpCommonDTO> appModFunc(String token,PpDishDets ppDishDets,
			Db1Service db1Service, Db2Service db2Service, SaasService saasService,
			DbHiveDishService dbHiveDishService) {
		ExpCommonDTO expPpDishDets = new ExpCommonDTO();
		if (isRealData) { // 真实数据
			ppDishDets.setPage("-1");	
			ppDishDets.setPageSize("-1");
			PpDishUseDetsDTO pddDto = epddAppMod.appModFunc(token, ppDishDets, 
					db1Service, db2Service, saasService, dbHiveDishService);
			if(pddDto != null) {
				List<PpDishUseDets> expExcelList = pddDto.getPpDishDets();
				//生成导出EXCEL文件
				String repFileName = repFileResPath + UniqueIdGen.uuid() + SpringConfig.repFileFormats[SpringConfig.curRepFileFrmtIdx];
				String pathFileName = SpringConfig.base_dir + repFileName;
				logger.info("导出文件路径：" + pathFileName);
				if(expExcelList ==null) {
					expExcelList = new ArrayList<>();
				}
				boolean flag = expPpDishDetsExcel(pathFileName, expExcelList, colNames);
				if(flag) {
					//移动文件到其他目录
					//AppModConfig.moveFileToOtherFolder(pathFileName, SpringConfig.tomcatSrvDirs[1] + repFileResPath);
					//导出信息
					expPpDishDets.setStartDate(startDate);
					expPpDishDets.setEndDate(endDate);
					expPpDishDets.setPpName(ppName);
					expPpDishDets.setDistName(AppModConfig.distIdToNameMap.get(distName));
					expPpDishDets.setPrefCity(prefCity);
					expPpDishDets.setProvince(province);
					expFileUrl = SpringConfig.repfile_srvdn + repFileName;
					logger.info("导出文件URL：" + expFileUrl);
					expPpDishDets.setExpFileUrl(expFileUrl);
				}
			}
		} else { // 模拟数据
			// 模拟数据函数
			return SimuDataFunc();
		}

		return new ApiResponse<>(expPpDishDets);
	}
}
