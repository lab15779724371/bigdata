package com.tfit.BdBiProcSrvShEduOmc.appmod.rc;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.servlet.http.HttpServletResponse;

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
import com.tfit.BdBiProcSrvShEduOmc.dto.rc.DishWarnListRepsDto;
import com.tfit.BdBiProcSrvShEduOmc.dto.rc.DishWarnListRepsInputDto;
import com.tfit.BdBiProcSrvShEduOmc.dto.rc.DishWarnListRepsOutDto;
import com.tfit.BdBiProcSrvShEduOmc.obj.base.DepartmentObj;
import com.tfit.BdBiProcSrvShEduOmc.service.Db1Service;
import com.tfit.BdBiProcSrvShEduOmc.service.Db2Service;
import com.tfit.BdBiProcSrvShEduOmc.service.DbHiveWarnService;
import com.tfit.BdBiProcSrvShEduOmc.util.CommonUtil;
import com.tfit.BdBiProcSrvShEduOmc.util.FtpUtil;
import com.tfit.BdBiProcSrvShEduOmc.util.UniqueIdGen;

//导出项目点排菜详情列表应用模型
public class ExpDishWarnListRepsAppMod {
	private static final Logger logger = LogManager.getLogger(ExpDishWarnListRepsAppMod.class.getName());
	
	//项目点排菜详情列表应用模型
	private DishWarnListRepsAppMod epddAppMod = new DishWarnListRepsAppMod();
	
	//是否为真实数据标识
	private static boolean isRealData = true;
	//页号、页大小和总页数
	int curPageNum = 1, pageTotal = 1, pageSize = AppModConfig.maxPageSize;
	//报表文件资源路径
	String repFileResPath = "/expDishWarnListReps/";
	//导出列名数组
	String[] colNames = {"序号", "区（市属中职校）","学校名称","截止时间","备注"};	
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
	public boolean expPpDishDetsExcel(String depamentmentId,Map<String,DepartmentObj> departmentMap,String date,String pathFileName, List<DishWarnListRepsDto> result, String colNames[], 
			HttpServletResponse response) { 
		
		String depamentmentName = "全市";
		if(CommonUtil.isNotEmpty(depamentmentId)) {
			depamentmentName = AppModConfig.compDepIdToNameMap3.get(depamentmentId);
		}

		Map<String,List<DishWarnListRepsDto>> dataMap = new HashMap<>();
		for(DishWarnListRepsDto dataObj : result) {
			List<DishWarnListRepsDto> dataList = dataMap.get(dataObj.getWarnLevelName());
			if(dataList == null) {
				dataList = new ArrayList<>();
			}
			dataList.add(dataObj);
			dataMap.put(dataObj.getWarnLevelName(), dataList);
		}
		
		Map<String,String> excelUrlMap = new HashMap<>();
		for(Map.Entry<String,List<DishWarnListRepsDto>> entry : dataMap.entrySet()) {
			
			String excelKey = depamentmentName;
			if("提示".equals(entry.getKey()) || "提醒".equals(entry.getKey()) || "预警".equals(entry.getKey())) {
				excelKey +=  "次日";
			}else {
				excelKey +=  "当日";
			}
			excelKey += "排菜未上报学校"+entry.getKey()+"信息汇总表" +date.replaceAll("-", "");
			excelKey += SpringConfig.repFileFormats[SpringConfig.curRepFileFrmtIdx];
			
			String repFileName = UniqueIdGen.uuid() ;
			repFileName += SpringConfig.repFileFormats[SpringConfig.curRepFileFrmtIdx];
			pathFileName = SpringConfig.base_dir +"/"+ repFileName;
			
			boolean retFlag = true;
			Workbook wb = null;
	        String excelPath = pathFileName, fileType = "";
	        File file = new File(excelPath);
	        Sheet sheet = null;
	        int idx1 = excelPath.lastIndexOf(".xls"), idx2 = excelPath.lastIndexOf(".xlsx");
	        if(entry.getValue()!=null && entry.getValue().size() > AppModConfig.maxPageSize)
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
	            	
	    		  	sheet.setColumnWidth(0, 100*20);
	    		  	sheet.setColumnWidth(1, 100*50);
	    		  	sheet.setColumnWidth(2, 100*100);
	    		  	sheet.setColumnWidth(3, 100*50);
	    		  	sheet.setColumnWidth(4, 100*50);
	    		  	
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
				
			  	//第一行
			  	/*//标题：学校排菜情况汇总统计报表
			  	String title = excelKey;
			  	sheet.addMergedRegion(new CellRangeAddress(startRowIdx, startRowIdx, 0, 6));
	    		//加粗字体
	    	  	CellStyle styleTitle = AppModConfig.getExcellCellStyle(wb);
			  	creatFullRow(sheet, startRowIdx++, colVals, styleTitle, title);*/
			  	
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
				for (int i = 0; i < entry.getValue().size(); i++) {
					startColumnIdx = 0;
					row = (Row) sheet.createRow(i + startRowIdx);				
					row.createCell(startColumnIdx++).setCellValue(i + startRowIdx);        //序号
					row.createCell(startColumnIdx++).setCellValue(entry.getValue().get(i).getDepartmentId()==null?"":departmentMap.get(entry.getValue().get(i).getDepartmentId()).getDepartmentName());  //区（市属中职校）
					row.createCell(startColumnIdx++).setCellValue(entry.getValue().get(i).getSchName());       //学校名称
					row.createCell(startColumnIdx++).setCellValue(entry.getValue().get(i).getWarnDate());      //截止时间
					row.createCell(startColumnIdx++).setCellValue(entry.getValue().get(i).getRemark());        //备注
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
				
				excelUrlMap.put(excelKey, repFileName);
	        }
		}
		
		
        
        
        try {
            String downloadFilename = "DistributionPics.zip";//文件的名称
            downloadFilename = URLEncoder.encode(downloadFilename, "UTF-8");//转换中文否则可能会产生乱码
            response.setContentType("application/octet-stream");// 指明response的返回对象是文件流
            response.setHeader("Content-Disposition", "attachment;filename=" + downloadFilename);// 设置在下载框默认显示的文件名
            ZipOutputStream zos = new ZipOutputStream(response.getOutputStream());
            
            for(Map.Entry<String,String> entry: excelUrlMap.entrySet()) {
            	 URL url = new URL(SpringConfig.repfile_srvdn+repFileResPath+entry.getValue());
                 zos.putNextEntry(new ZipEntry(entry.getKey()));
                 InputStream fis = url.openConnection().getInputStream();  
                 byte[] buffer = new byte[1024];    
                 int r = 0;    
                 while ((r = fis.read(buffer)) != -1) {    
                     zos.write(buffer, 0, r);    
                 }    
                 fis.close();  
            }
            	
            zos.flush();    
            zos.close();
        } catch (UnsupportedEncodingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } 
        
        
        return true;
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
	public ApiResponse<ExpCommonDTO> appModFunc(String token,DishWarnListRepsInputDto inputObj,
			Db1Service db1Service, Db2Service db2Service,
			DbHiveWarnService dbHiveWarnService, HttpServletResponse response) {
		ExpCommonDTO expPpDishDets = new ExpCommonDTO();
		if (isRealData) { // 真实数据
			inputObj.setPage("-1");	
			inputObj.setPageSize("-1");
			inputObj.setWarnLevel("");
			DishWarnListRepsOutDto pddDto = epddAppMod.appModFunc(token, inputObj, db1Service, db2Service, dbHiveWarnService);
			if(pddDto != null) {
				List<DishWarnListRepsDto> expExcelList = pddDto.getDataList();
				//生成导出EXCEL文件
				String repFileName = repFileResPath + UniqueIdGen.uuid() + SpringConfig.repFileFormats[SpringConfig.curRepFileFrmtIdx];
				String pathFileName = SpringConfig.base_dir + repFileName;
				logger.info("导出文件路径：" + pathFileName);
				if(expExcelList ==null) {
					expExcelList = new ArrayList<>();
				}
				
				String departmentId = AppModConfig.getUserDataPermDistId(token, db1Service, db2Service);  //获取用户权限区域ID
				if(CommonUtil.isNotEmpty(departmentId)) {
					inputObj.setDepartmentId(departmentId);
				}
				
				//管理部门
				//获取用户信息，用于匹配部门的编辑人
				Map<String,DepartmentObj> departmentMap = new HashMap<String,DepartmentObj>();
				List<DepartmentObj> deparmentList =  db1Service.getDepartmentObjList(new DepartmentObj(),null, -1, -1);	
				if(deparmentList != null) {
					departmentMap = deparmentList.stream().collect(Collectors.toMap(DepartmentObj::getDepartmentId,(b)->b));
				}
				
				boolean flag = expPpDishDetsExcel(departmentId,departmentMap,inputObj.getStartWarnDate(),pathFileName, expExcelList, colNames,response);
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
