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
import com.tfit.BdBiProcSrvShEduOmc.dto.rc.DishWarnSumListRepsDto;
import com.tfit.BdBiProcSrvShEduOmc.dto.rc.DishWarnSumListRepsOutDto;
import com.tfit.BdBiProcSrvShEduOmc.obj.base.DepartmentObj;
import com.tfit.BdBiProcSrvShEduOmc.obj.warn.AppTEduNoPlatoonCollectD;
import com.tfit.BdBiProcSrvShEduOmc.service.Db1Service;
import com.tfit.BdBiProcSrvShEduOmc.service.Db2Service;
import com.tfit.BdBiProcSrvShEduOmc.service.DbHiveWarnService;
import com.tfit.BdBiProcSrvShEduOmc.util.CommonUtil;
import com.tfit.BdBiProcSrvShEduOmc.util.FtpUtil;
import com.tfit.BdBiProcSrvShEduOmc.util.UniqueIdGen;

//导出项目点排菜详情列表应用模型
public class ExpDishWarnSumListRepsAppMod {
	private static final Logger logger = LogManager.getLogger(ExpDishWarnSumListRepsAppMod.class.getName());
	
	//项目点排菜详情列表应用模型
	private DishWarnSumListRepsAppMod epddAppMod = new DishWarnSumListRepsAppMod();
	
	//是否为真实数据标识
	private static boolean isRealData = true;
	//页号、页大小和总页数
	int curPageNum = 1, pageTotal = 1, pageSize = AppModConfig.maxPageSize;
	//报表文件资源路径
	String repFileResPath = "/expDishWarnSumListReps/";
	//导出列名数组
	String[] colNames = {"序号", "区","学校名称","截止时间14点（提示）","截止时间16点（提醒）","截止时间17点（预警）","截止时间9点（督办）","截止时间11点（追责）","备注"};	
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
	public boolean expPpDishDetsExcel(String depamentmentId,Map<String,DepartmentObj> departmentMap,
			String pathFileName, List<DishWarnSumListRepsDto> result, String colNames[], 
			HttpServletResponse response) { 

		DepartmentObj departmentObj = new DepartmentObj();
		departmentObj.setDepartmentId("-1");
		departmentObj.setDepartmentName("全市");
		departmentMap.put("-1", departmentObj);
		
		
		//16个区 ：key：时间+区教育局  value 学校列表
		Map<String,List<DishWarnSumListRepsDto>> dataMap = new HashMap<>();
		
		//市属中职校：key：时间+区教育局  value 学校列表
		Map<String,List<DishWarnSumListRepsDto>> shzzxDataMap = new HashMap<>();
		for(DishWarnSumListRepsDto dataObj : result) {
			//如果没有预警信息，则不统计
			if(dataObj==null || !((dataObj.getWarnPrompt()!=null && dataObj.getWarnPrompt()==1) ||
					(dataObj.getWarnRemind()!=null && dataObj.getWarnRemind()==1) ||
					(dataObj.getWarnEarly()!=null && dataObj.getWarnEarly()==1) ||
					(dataObj.getWarnSupervise()!=null && dataObj.getWarnSupervise()==1) ||
					(dataObj.getWarnAccountability()!=null && dataObj.getWarnAccountability()==1) )) {
				continue;
			}
			
			if("20".equals(dataObj.getDepartmentId())) {
				List<DishWarnSumListRepsDto> dataList = shzzxDataMap.get(dataObj.getWarnDate()+"_"+dataObj.getDepartmentId());
				if(dataList == null) {
					dataList = new ArrayList<>();
				}
				dataList.add(dataObj);
				shzzxDataMap.put(dataObj.getWarnDate()+"_"+dataObj.getDepartmentId(), dataList);
			}else {
				List<DishWarnSumListRepsDto> dataList = dataMap.get(dataObj.getWarnDate()+"_"+dataObj.getDepartmentId());
				if(dataList == null) {
					dataList = new ArrayList<>();
				}
				dataList.add(dataObj);
				dataMap.put(dataObj.getWarnDate()+"_"+dataObj.getDepartmentId(), dataList);
			}
			
			//全市数据
			List<DishWarnSumListRepsDto> allDataList = dataMap.get(dataObj.getWarnDate()+"_-1");
			if(allDataList == null) {
				allDataList = new ArrayList<>();
			}
			allDataList.add(dataObj);
			dataMap.put(dataObj.getWarnDate()+"_-1", allDataList);
			
		}
		
		/**
		 * 16个区教育局
		 */
		Map<String,String> excelUrlMap = new HashMap<>();
		for(Map.Entry<String,List<DishWarnSumListRepsDto>> entry : dataMap.entrySet()) {
			
			String[] keys = entry.getKey().split("_");
			
			if(departmentMap.get(keys[1]) == null) {
				logger.info("**************************"+keys[1]);
				logger.info("**************************"+departmentMap.toString());
				continue;
			}
			
			String excelKey = keys[1]==null?"":departmentMap.get(keys[1]).getDepartmentName()+keys[0].replaceFirst("-", "年").replaceFirst("-", "月")+"日";
			
			excelKey += "排菜信息未上报学校信息汇总表";
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
	    		  	sheet.setColumnWidth(3, 100*60);
	    		  	sheet.setColumnWidth(4, 100*60);
	    		  	sheet.setColumnWidth(5, 100*60);
	    		  	sheet.setColumnWidth(6, 100*60);
	    		  	sheet.setColumnWidth(7, 100*60);
	    		  	sheet.setColumnWidth(8, 100*50);
	    		  	
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
				Integer warnPrompt = 0;
				Integer warnRemind = 0;
				Integer warnEarly = 0;
				Integer warnSupervise = 0;
				Integer warnAccountability = 0;
				for (int i = 0; i < entry.getValue().size(); i++) {
					startColumnIdx = 0;
					row = (Row) sheet.createRow(i + startRowIdx);				
					row.createCell(startColumnIdx++).setCellValue(i + startRowIdx);        //序号
					row.createCell(startColumnIdx++).setCellValue(entry.getValue().get(i).getDepartmentId()==null?"":departmentMap.get(entry.getValue().get(i).getDepartmentId()).getDepartmentName());  //区（市属中职校）
					row.createCell(startColumnIdx++).setCellValue(entry.getValue().get(i).getSchoolName());       //学校名称
					//提示预警
				    row.createCell(startColumnIdx++).setCellValue((entry.getValue().get(i).getWarnPrompt()!=null && entry.getValue().get(i).getWarnPrompt()==1)?"√":"/");
				    if(entry.getValue().get(i).getWarnPrompt()!=null && entry.getValue().get(i).getWarnPrompt()==1) {
				    	warnPrompt ++;
				    }
				    //提醒预警
				    row.createCell(startColumnIdx++).setCellValue((entry.getValue().get(i).getWarnRemind()!=null && entry.getValue().get(i).getWarnRemind()==1)?"√":"/");
				    if(entry.getValue().get(i).getWarnRemind()!=null && entry.getValue().get(i).getWarnRemind()==1) {
				    	warnRemind ++;
				    }
				    //预警
				    row.createCell(startColumnIdx++).setCellValue((entry.getValue().get(i).getWarnEarly()!=null && entry.getValue().get(i).getWarnEarly()==1)?"√":"/");
				    if(entry.getValue().get(i).getWarnEarly()!=null && entry.getValue().get(i).getWarnEarly()==1) {
				    	warnEarly ++;
				    }
				    //督办预警
				    row.createCell(startColumnIdx++).setCellValue((entry.getValue().get(i).getWarnSupervise()!=null && entry.getValue().get(i).getWarnSupervise()==1)?"√":"/");
				    if(entry.getValue().get(i).getWarnSupervise()!=null && entry.getValue().get(i).getWarnSupervise()==1) {
				    	warnSupervise ++;
				    }
				    //追责预警
				    row.createCell(startColumnIdx++).setCellValue((entry.getValue().get(i).getWarnAccountability()!=null && entry.getValue().get(i).getWarnAccountability()==1)?"√":"/");
				    if(entry.getValue().get(i).getWarnAccountability()!=null && entry.getValue().get(i).getWarnAccountability()==1) {
				    	warnAccountability ++;
				    }
					
					row.createCell(startColumnIdx++).setCellValue(entry.getValue().get(i).getRemark());        //备注
				}
				
			    //合计行
				startColumnIdx=0;
				row = (Row) sheet.createRow(entry.getValue().size()+1 + startRowIdx);				
				row.createCell(startColumnIdx++).setCellValue("");        //序号
				row.createCell(startColumnIdx++).setCellValue("");  //区（市属中职校）
				row.createCell(startColumnIdx++).setCellValue("合计");       //学校名称
				//提示预警
			    row.createCell(startColumnIdx++).setCellValue(warnPrompt);
			    //提醒预警
			    row.createCell(startColumnIdx++).setCellValue(warnRemind);
			    //预警
			    row.createCell(startColumnIdx++).setCellValue(warnEarly);
			    //督办预警
			    row.createCell(startColumnIdx++).setCellValue(warnSupervise);
			    //追责预警
			    row.createCell(startColumnIdx++).setCellValue(warnAccountability);
				row.createCell(startColumnIdx++).setCellValue("");        //备注

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
		
		/**
		 * 市属中职校
		 */
		
		for(Map.Entry<String,List<DishWarnSumListRepsDto>> entry : shzzxDataMap.entrySet()) {
			
			String[] keys = entry.getKey().split("_");
			
			
			if(entry.getValue() == null || entry.getValue().size()==0) {
				continue;
			}
					
			for(DishWarnSumListRepsDto dataObj : entry.getValue()) {
				String excelKey = keys[1]==null?"":departmentMap.get(keys[1]).getDepartmentName()+keys[0].replaceFirst("-", "年").replaceFirst("-", "月")+"日";
				excelKey += "排菜信息未上报市属中职校信息汇总表（"+dataObj.getSchoolName()+"）";
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
		    		  	sheet.setColumnWidth(3, 100*60);
		    		  	sheet.setColumnWidth(4, 100*60);
		    		  	sheet.setColumnWidth(5, 100*60);
		    		  	sheet.setColumnWidth(6, 100*60);
		    		  	sheet.setColumnWidth(7, 100*60);
		    		  	sheet.setColumnWidth(8, 100*50);
		    		  	
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
					
					Integer warnPrompt = 0;
					Integer warnRemind = 0;
					Integer warnEarly = 0;
					Integer warnSupervise = 0;
					Integer warnAccountability = 0;
					
					// 循环写入行数据
					startRowIdx++;
					startColumnIdx = 0;
					row = (Row) sheet.createRow(startRowIdx);				
					row.createCell(startColumnIdx++).setCellValue(startRowIdx);        //序号
					row.createCell(startColumnIdx++).setCellValue(dataObj.getDepartmentId()==null?"":departmentMap.get(dataObj.getDepartmentId()).getDepartmentName());  //区（市属中职校）
					row.createCell(startColumnIdx++).setCellValue(dataObj.getSchoolName());       //学校名称
					//提示预警
				    row.createCell(startColumnIdx++).setCellValue((dataObj.getWarnPrompt()!=null && dataObj.getWarnPrompt()==1)?"√":"/");
				    if(dataObj.getWarnPrompt()!=null && dataObj.getWarnPrompt()==1) {
				    	warnPrompt ++;
				    }
				    //提醒预警
				    row.createCell(startColumnIdx++).setCellValue((dataObj.getWarnRemind()!=null && dataObj.getWarnRemind()==1)?"√":"/");
				    if(dataObj.getWarnRemind()!=null && dataObj.getWarnRemind()==1) {
				    	warnRemind ++;
				    }
				    //预警
				    row.createCell(startColumnIdx++).setCellValue((dataObj.getWarnEarly()!=null && dataObj.getWarnEarly()==1)?"√":"/");
				    if(dataObj.getWarnEarly()!=null && dataObj.getWarnEarly()==1) {
				    	warnEarly ++;
				    }
				    //督办预警
				    row.createCell(startColumnIdx++).setCellValue((dataObj.getWarnSupervise()!=null && dataObj.getWarnSupervise()==1)?"√":"/");
				    if(dataObj.getWarnSupervise()!=null && dataObj.getWarnSupervise()==1) {
				    	warnSupervise ++;
				    }
				    //追责预警
				    row.createCell(startColumnIdx++).setCellValue((dataObj.getWarnAccountability()!=null && dataObj.getWarnAccountability()==1)?"√":"/");
				    if(dataObj.getWarnAccountability()!=null && dataObj.getWarnAccountability()==1) {
				    	warnAccountability ++;
				    }
					
					row.createCell(startColumnIdx++).setCellValue(dataObj.getRemark());        //备注
					
				    //合计行
					startColumnIdx=0;
					startRowIdx++;
					row = (Row) sheet.createRow(startRowIdx);				
					row.createCell(startColumnIdx++).setCellValue("");        //序号
					row.createCell(startColumnIdx++).setCellValue("");  //区（市属中职校）
					row.createCell(startColumnIdx++).setCellValue("合计");       //学校名称
					//提示预警
				    row.createCell(startColumnIdx++).setCellValue(warnPrompt);
				    //提醒预警
				    row.createCell(startColumnIdx++).setCellValue(warnRemind);
				    //预警
				    row.createCell(startColumnIdx++).setCellValue(warnEarly);
				    //督办预警
				    row.createCell(startColumnIdx++).setCellValue(warnSupervise);
				    //追责预警
				    row.createCell(startColumnIdx++).setCellValue(warnAccountability);
					row.createCell(startColumnIdx++).setCellValue("");        //备注

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
	public ApiResponse<ExpCommonDTO> appModFunc(String token,AppTEduNoPlatoonCollectD inputObj,
			Db1Service db1Service, Db2Service db2Service,
			DbHiveWarnService dbHiveWarnService, HttpServletResponse response) {
		ExpCommonDTO expPpDishDets = new ExpCommonDTO();
		if (isRealData) { // 真实数据
			inputObj.setPage("-1");	
			inputObj.setPageSize("-1");
			inputObj.setDepartmentMode("-1");
			DishWarnSumListRepsOutDto pddDto = epddAppMod.appModFunc(token, inputObj, db1Service, db2Service, dbHiveWarnService);
			if(pddDto != null) {
				List<DishWarnSumListRepsDto> expExcelList = pddDto.getDataList();
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
				
				boolean flag = expPpDishDetsExcel(departmentId,departmentMap,pathFileName, expExcelList, colNames,response);
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
	
	public static void main(String[] args) {
		
		System.out.println("2017-01-01".replaceFirst("-", "年").replaceFirst("-", "月")+"日");
	}
}
