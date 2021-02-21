package com.tfit.BdBiProcSrvShEduOmc.appmod.im.week;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
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

import com.tfit.BdBiProcSrvShEduOmc.common.ApiResponse;
import com.tfit.BdBiProcSrvShEduOmc.config.AppModConfig;
import com.tfit.BdBiProcSrvShEduOmc.config.SpringConfig;
import com.tfit.BdBiProcSrvShEduOmc.dto.im.ExpCommonDTO;
import com.tfit.BdBiProcSrvShEduOmc.dto.im.week.AppTEduPlatoonTotalWObj;
import com.tfit.BdBiProcSrvShEduOmc.dto.im.week.PpDishWeekDto;
import com.tfit.BdBiProcSrvShEduOmc.dto.im.week.PpDishWeekOutDto;
import com.tfit.BdBiProcSrvShEduOmc.dto.user.UserSetColums;
import com.tfit.BdBiProcSrvShEduOmc.obj.base.DepartmentObj;
import com.tfit.BdBiProcSrvShEduOmc.service.Db1Service;
import com.tfit.BdBiProcSrvShEduOmc.service.Db2Service;
import com.tfit.BdBiProcSrvShEduOmc.service.DbHiveDishService;
import com.tfit.BdBiProcSrvShEduOmc.service.SaasService;
import com.tfit.BdBiProcSrvShEduOmc.util.CommonUtil;
import com.tfit.BdBiProcSrvShEduOmc.util.FtpUtil;
import com.tfit.BdBiProcSrvShEduOmc.util.UniqueIdGen;


//导出项目点排菜详情列表应用模型
public class ExpPpDishWeekAppMod {
	private static final Logger logger = LogManager.getLogger(ExpPpDishWeekAppMod.class.getName());
	
	//项目点排菜详情列表应用模型
	private PpDishWeekAppMod epddAppMod = new PpDishWeekAppMod();
	
	//是否为真实数据标识
	private static boolean isRealData = true;
	//页号、页大小和总页数
	int curPageNum = 1, pageTotal = 1, pageSize = AppModConfig.maxPageSize;
	//报表文件资源路径
	String repFileResPath = "/expPpDishWeek/";
	//导出列名数组
	String[] colNames = {"序号","日期","管理部门","学校学制","项目点名称", "应排菜天数", "已排菜天数", "未排菜天数", "规范录入", "补录", "逾期补录", 
			"无数据", "所在地", "地址", "项目联系人","手机","办学性质","所属","主管部门","所属区","食品经营许可证主体","供餐模式"};
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
	public boolean expPpDishDetsExcel(String pathFileName, List<PpDishWeekDto> result,Map<String,DepartmentObj> deparmentMap,List<UserSetColums> userSetColumsList, String colNames[]) {
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
			for (int i = 0; i < result.size(); i++) {
				startColumnIdx = 0;
				row = (Row) sheet.createRow(i + startRowIdx);			
				if(userSetColumsList !=null && userSetColumsList.size() > 0) {
					for(UserSetColums obj : userSetColumsList) {
					  if(obj != null && obj.isChecked()) {
						if("sortNo".equals(obj.getKey())) 
							row.createCell(startColumnIdx++).setCellValue(i+1);//序号
					    if("useDatePeriod".equals(obj.getKey())) 
					      	row.createCell(startColumnIdx++).setCellValue(result.get(i).getUseDatePeriod());                                            //日期	
					    if("departmentId".equals(obj.getKey())) 
							row.createCell(startColumnIdx++).setCellValue(deparmentMap.get(result.get(i).getDepartmentId(
									))==null?"":deparmentMap.get(result.get(i).getDepartmentId()).getDepartmentName());                                            //管理部门
					    if("levelName".equals(obj.getKey())) 
					      	row.createCell(startColumnIdx++).setCellValue(result.get(i).getLevelName());                                            //学校学制	
					    if("schoolName".equals(obj.getKey())) 
					      	row.createCell(startColumnIdx++).setCellValue(result.get(i).getSchoolName());                                              //项目点名称	
					    if("haveClassTotal".equals(obj.getKey())) 
					      	row.createCell(startColumnIdx++).setCellValue(result.get(i).getHaveClassTotal());                                              //应排菜天数	
					    if("havePlatoonTotal".equals(obj.getKey())) 
					      	row.createCell(startColumnIdx++).setCellValue(result.get(i).getHavePlatoonTotal());                                              //已排菜天数	
					    if("haveNoPlatoonTotal".equals(obj.getKey())) 
					      	row.createCell(startColumnIdx++).setCellValue(result.get(i).getHaveNoPlatoonTotal());                                              //未排菜天数	
					    if("guifanPlatoonTotal".equals(obj.getKey())) 
					      	row.createCell(startColumnIdx++).setCellValue(result.get(i).getGuifanPlatoonTotal());                                              //规范录入	
					    if("buluPlatoonTotal".equals(obj.getKey())) 
					      	row.createCell(startColumnIdx++).setCellValue(result.get(i).getBuluPlatoonTotal());                                              //补录	
					    if("yuqiPlatoonTotal".equals(obj.getKey())) 
					      	row.createCell(startColumnIdx++).setCellValue(result.get(i).getYuqiPlatoonTotal());                                              //逾期补录	
					    if("noPlatoonTotal".equals(obj.getKey())) 
					      	row.createCell(startColumnIdx++).setCellValue(result.get(i).getNoPlatoonTotal());                                              //无数据	
					    if("area".equals(obj.getKey())) 
					      	row.createCell(startColumnIdx++).setCellValue(AppModConfig.distIdToNameMap.get(result.get(i).getArea()));          //所在地	
					    if("address".equals(obj.getKey())) 
					      	row.createCell(startColumnIdx++).setCellValue(result.get(i).getAddress());                                          //地址	
					    if("foodSafetyPersion".equals(obj.getKey())) 
					      	row.createCell(startColumnIdx++).setCellValue(result.get(i).getFoodSafetyPersion());                                         //项目联系人	
					    if("foodSafetyMobilephone".equals(obj.getKey())) 
					      	row.createCell(startColumnIdx++).setCellValue(result.get(i).getFoodSafetyMobilephone());                                       //手机	
					    if("schoolNatureName".equals(obj.getKey())) 
					      	row.createCell(startColumnIdx++).setCellValue(result.get(i).getSchoolNatureName());                                            //办学性质	
					    if("departmentMasterName".equals(obj.getKey())) 
					      	row.createCell(startColumnIdx++).setCellValue(result.get(i).getDepartmentMasterName());                                            //所属	
					    if("departmentSlaveIdName".equals(obj.getKey())) 
					      	row.createCell(startColumnIdx++).setCellValue(result.get(i).getDepartmentSlaveIdName());                                             //主管部门	
					    if("schoolAreaName".equals(obj.getKey())) 
					      	row.createCell(startColumnIdx++).setCellValue(result.get(i).getSchoolAreaName());                                         //所属区	
					    if("licenseMainTypeName".equals(obj.getKey())) 
					      	row.createCell(startColumnIdx++).setCellValue(result.get(i).getLicenseMainTypeName());                                               //食品经营许可证主体	
					    if("licenseMainChildName".equals(obj.getKey())) 
					      	row.createCell(startColumnIdx++).setCellValue(result.get(i).getLicenseMainChildName());                                            //供餐模式	
					  }
					}
				  }else {
					row.createCell(startColumnIdx++).setCellValue(i+1);
					row.createCell(startColumnIdx++).setCellValue(result.get(i).getUseDatePeriod());                                            //日期
					row.createCell(startColumnIdx++).setCellValue(deparmentMap.get(result.get(i).getDepartmentId(
							))==null?"":deparmentMap.get(result.get(i).getDepartmentId()).getDepartmentName());                                            //管理部门
					row.createCell(startColumnIdx++).setCellValue(result.get(i).getLevelName());                                            //学校学制
					row.createCell(startColumnIdx++).setCellValue(result.get(i).getSchoolName());                                              //项目点名称
					row.createCell(startColumnIdx++).setCellValue(result.get(i).getHaveClassTotal());                                              //应排菜天数
					row.createCell(startColumnIdx++).setCellValue(result.get(i).getHavePlatoonTotal());                                              //已排菜天数
					row.createCell(startColumnIdx++).setCellValue(result.get(i).getHaveNoPlatoonTotal());                                              //未排菜天数
					row.createCell(startColumnIdx++).setCellValue(result.get(i).getGuifanPlatoonTotal());                                              //规范录入
					row.createCell(startColumnIdx++).setCellValue(result.get(i).getBuluPlatoonTotal());                                              //补录
					row.createCell(startColumnIdx++).setCellValue(result.get(i).getYuqiPlatoonTotal());                                              //逾期补录
					row.createCell(startColumnIdx++).setCellValue(result.get(i).getNoPlatoonTotal());                                              //无数据
					row.createCell(startColumnIdx++).setCellValue(AppModConfig.distIdToNameMap.get(result.get(i).getArea()));          //所在地
					row.createCell(startColumnIdx++).setCellValue(result.get(i).getAddress());                                          //地址
					row.createCell(startColumnIdx++).setCellValue(result.get(i).getFoodSafetyPersion());                                         //项目联系人
					row.createCell(startColumnIdx++).setCellValue(result.get(i).getFoodSafetyMobilephone());                                       //手机
					row.createCell(startColumnIdx++).setCellValue(result.get(i).getSchoolNatureName());                                            //办学性质
					row.createCell(startColumnIdx++).setCellValue(result.get(i).getDepartmentMasterName());                                            //所属
					row.createCell(startColumnIdx++).setCellValue(result.get(i).getDepartmentSlaveIdName());                                             //主管部门
					row.createCell(startColumnIdx++).setCellValue(result.get(i).getSchoolAreaName());                                         //所属区
					row.createCell(startColumnIdx++).setCellValue(result.get(i).getLicenseMainTypeName());                                               //食品经营许可证主体
					row.createCell(startColumnIdx++).setCellValue(result.get(i).getLicenseMainChildName());                                            //供餐模式
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
	
	//导出项目点排菜详情列表模型函数
	public ApiResponse<ExpCommonDTO> appModFunc(String token,AppTEduPlatoonTotalWObj inputParm,
			Db1Service db1Service, Db2Service db2Service, SaasService saasService,
			DbHiveDishService dbHiveDishService) {
		
		List<UserSetColums> userSetColumsList =CommonUtil.getUserSetColumList(token,"ppDishWeek", db2Service);
		ExpCommonDTO expPpDishDets = new ExpCommonDTO();
		if (isRealData) { // 真实数据
			inputParm.setPage("-1");	
			inputParm.setPageSize("-1");
			PpDishWeekOutDto pddDto = epddAppMod.appModFunc(token, inputParm, db1Service, db2Service, dbHiveDishService);
			if(pddDto != null) {
				List<PpDishWeekDto> expExcelList = pddDto.getPpDishWeekList();
				//生成导出EXCEL文件
				String repFileName = repFileResPath + UniqueIdGen.uuid() + SpringConfig.repFileFormats[SpringConfig.curRepFileFrmtIdx];
				String pathFileName = SpringConfig.base_dir + repFileName;
				logger.info("导出文件路径：" + pathFileName);
				if(expExcelList ==null) {
					expExcelList = new ArrayList<>();
				}
				
				List<DepartmentObj> deparmentList =  db1Service.getDepartmentObjList(new DepartmentObj(),null, -1, -1);	
				Map<String,DepartmentObj> deparmentMap = deparmentList.stream().collect(Collectors.toMap(DepartmentObj::getDepartmentId,(b)->b));
				
				
				
				boolean flag = expPpDishDetsExcel(pathFileName, expExcelList,deparmentMap,userSetColumsList, colNames);
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
