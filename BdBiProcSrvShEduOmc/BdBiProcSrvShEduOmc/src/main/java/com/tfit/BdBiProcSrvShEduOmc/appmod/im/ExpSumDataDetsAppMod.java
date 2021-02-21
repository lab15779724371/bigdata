package com.tfit.BdBiProcSrvShEduOmc.appmod.im;

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
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.tfit.BdBiProcSrvShEduOmc.config.AppModConfig;
import com.tfit.BdBiProcSrvShEduOmc.config.SpringConfig;
import com.tfit.BdBiProcSrvShEduOmc.dto.im.SumDataDets;
import com.tfit.BdBiProcSrvShEduOmc.dto.im.SumDataDetsDTO;
import com.tfit.BdBiProcSrvShEduOmc.dto.im.ExpSumDataDets;
import com.tfit.BdBiProcSrvShEduOmc.dto.im.ExpSumDataDetsDTO;
import com.tfit.BdBiProcSrvShEduOmc.service.Db1Service;
import com.tfit.BdBiProcSrvShEduOmc.service.Db2Service;
import com.tfit.BdBiProcSrvShEduOmc.service.DbHiveDishService;
import com.tfit.BdBiProcSrvShEduOmc.service.DbHiveGsService;
import com.tfit.BdBiProcSrvShEduOmc.service.DbHiveRecyclerWasteService;
import com.tfit.BdBiProcSrvShEduOmc.service.DbHiveWarnService;
import com.tfit.BdBiProcSrvShEduOmc.util.BCDTimeUtil;
import com.tfit.BdBiProcSrvShEduOmc.util.UniqueIdGen;

//导出汇总数据详情列表应用模型
public class ExpSumDataDetsAppMod {
	private static final Logger logger = LogManager.getLogger(ExpSumDataDetsAppMod.class.getName());
	
	//导出汇总数据详情列表应用模型
	private SumDataDetsAppMod sddAppMod = new SumDataDetsAppMod();
	
	//是否为真实数据标识
	private static boolean isRealData = true;
	//页号、页大小和总页数
	int curPageNum = 1, pageTotal = 1, pageSize = AppModConfig.maxPageSize;
	//报表文件资源路径
	String repFileResPath = "/expSumDataDets/";
	//导出列名数组
	String[] statNames = {"排菜数据", "证照逾期处理", "验收数据", "菜品留样", "餐厨垃圾回收", "废弃油脂回收"};	
	String[] colNames0 = {"序号","起止日期", "所属", "主管部门", "应排菜天数", "未排菜天数", "已排菜天数", "排菜率",
			"应验收学校","已验收学校","未验收学校","学校验收率","应验收配货计划", "待验收数", "已验收数", "验收率", 
			"应留样学校","已留样学校","未留样学校","学校留样率","菜品总数", "未留样数", "已留样数", "留样率",
			"预警总数", "待处理数", "已消除数", "处理率", 
			"学校回收", "学校回收"};
	String[] colNames1 = {"序号","起止日期", "所在地", "应排菜天数", "未排菜天数", "已排菜天数", "排菜率", 
			"应验收学校","已验收学校","未验收学校","学校验收率","应验收配货计划", "待验收数", "已验收数", "验收率", 
			"应留样学校","已留样学校","未留样学校","学校留样率","菜品总数", "未留样数", "已留样数", "留样率",
			"预警总数", "待处理数", "已消除数", "处理率", 
			"回收合计", "学校回收", "团餐公司回收", "回收合计", "学校回收", "团餐公司回收"};
		
	//变量数据初始化
	String startDate = "2018-09-03";
	String endDate = "2018-09-04";
	String schSelMode = null;
	String subLevel = null;
	String compDep = null;
	String subDistName = null;
	String distName = null;
	String prefCity = null;
	String province = null;
	String expFileUrl = "fc8bafe943214d65a67a7d8b93d0185a.xls";
		
	//模拟数据函数
	private ExpSumDataDetsDTO SimuDataFunc() {
		ExpSumDataDetsDTO sddDto = new ExpSumDataDetsDTO();
		//设置返回数据
		sddDto.setTime(BCDTimeUtil.convertNormalFrom(null));
		//列表元素设置
		ExpSumDataDets expSumDataDets = new ExpSumDataDets();
		//赋值
		expSumDataDets.setStartDate(startDate);
		expSumDataDets.setEndDate(endDate);		
		expSumDataDets.setSchSelMode(schSelMode);
		expSumDataDets.setSubLevel(subLevel);
		expSumDataDets.setCompDep(compDep);
		expSumDataDets.setSubDistName(subDistName);		
		expSumDataDets.setDistName(distName);
		expSumDataDets.setPrefCity(prefCity);
		expSumDataDets.setProvince(province);
		expSumDataDets.setExpFileUrl(SpringConfig.repfile_srvdn+repFileResPath+expFileUrl);
		//设置模拟数据
		sddDto.setExpSumDataDets(expSumDataDets);
		//消息ID
		sddDto.setMsgId(AppModConfig.msgId);
		AppModConfig.msgId++;
		//消息id小于0判断
		AppModConfig.msgIdLessThan0Judge();

		return sddDto;
	}
	
	//生成导出EXCEL文件
	public boolean expSumDataDetsExcel(String pathFileName, List<SumDataDets> dataList, int schSelMode) { 
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
        	String[] colVals = null;
        	if(schSelMode == 0)    //按主管部门
        		colVals = new String[colNames0.length];
        	else if(schSelMode == 1)    //按所在地
        		colVals = new String[colNames1.length];
			// 添加样式
			Row row = null;
			Cell cell = null;
			// 创建第一行
			row = (Row) sheet.createRow(startRowIdx);
			//获取excell单元风格
		  	CellStyle style = AppModConfig.getExcellCellStyle(wb);
			if(schSelMode == 0) {    //按主管部门
				for (int i = 0; i < colNames0.length; i++) {
					cell = row.createCell(i);
					try {
						logger.info(colNames0[i] + " ");
						colVals[i] = new String(colNames0[i].getBytes(), "utf-8");
						cell.setCellValue(colNames0[i]);
						cell.setCellStyle(style);
					} catch (UnsupportedEncodingException e) {
						// TODO 自动生成的 catch 块
						e.printStackTrace();
					}
				}
			}
			else if(schSelMode == 1) {    //按所在地
				for (int i = 0; i < colNames1.length; i++) {
					cell = row.createCell(i);
					try {
						logger.info(colNames1[i] + " ");
						colVals[i] = new String(colNames1[i].getBytes(), "utf-8");
						cell.setCellValue(colNames1[i]);
					} catch (UnsupportedEncodingException e) {
						// TODO 自动生成的 catch 块
						e.printStackTrace();
					}
				}
			}
			// 循环写入行数据
			startRowIdx++;
			String totalDishDate = "合计", totalDistName = "---";
			float[] totalNumOrRates = new float[colNames1.length];
			int columIndex=0;
			for (int i = 0; i < dataList.size(); i++) {
				columIndex=0;
				row = (Row) sheet.createRow(i + startRowIdx);
				row.createCell(columIndex++).setCellValue(i+1);                                             //序号
				if(schSelMode == 0) {    //按主管部门
					row.createCell(columIndex++).setCellValue(dataList.get(i).getDishDate());                                             //起止日期
					row.createCell(columIndex++).setCellValue(dataList.get(i).getSubLevel());                                             //所属		
					row.createCell(columIndex++).setCellValue(dataList.get(i).getCompDep());                                              //主管部门
					row.createCell(columIndex++).setCellValue(dataList.get(i).getMealSchNum());                                           //应排菜天数
					totalNumOrRates[columIndex-1] += dataList.get(i).getMealSchNum();
					row.createCell(columIndex++).setCellValue(dataList.get(i).getNoDishSchNum());                                         //未排菜天数
					totalNumOrRates[columIndex-1] += dataList.get(i).getNoDishSchNum();
					row.createCell(columIndex++).setCellValue(dataList.get(i).getDishSchNum());                                           //已排菜天数
					totalNumOrRates[columIndex-1] += dataList.get(i).getDishSchNum();
					row.createCell(columIndex++).setCellValue(dataList.get(i).getDishRate());                                             //排菜率
					
					row.createCell(columIndex++).setCellValue(dataList.get(i).getShouldAcceptSchNum());                                   //应验收学校
					totalNumOrRates[columIndex-1] += dataList.get(i).getShouldAcceptSchNum();
					row.createCell(columIndex++).setCellValue(dataList.get(i).getNoAcceptSchNum());                                       //待验收学校
					totalNumOrRates[columIndex-1] += dataList.get(i).getNoAcceptSchNum();
					row.createCell(columIndex++).setCellValue(dataList.get(i).getAcceptSchNum());                                         //已验收学校
					totalNumOrRates[columIndex-1] += dataList.get(i).getAcceptSchNum();
					row.createCell(columIndex++).setCellValue(dataList.get(i).getSchAcceptRate());                                        //学校验收率
					
					row.createCell(columIndex++).setCellValue(dataList.get(i).getTotalGsPlanNum());                                       //配货计划总数
					totalNumOrRates[columIndex-1] += dataList.get(i).getTotalGsPlanNum();
					row.createCell(columIndex++).setCellValue(dataList.get(i).getNoAcceptGsPlanNum());                                    //待验收数
					totalNumOrRates[columIndex-1] += dataList.get(i).getNoAcceptGsPlanNum();
					row.createCell(columIndex++).setCellValue(dataList.get(i).getAcceptGsPlanNum());                                      //已验收数
					totalNumOrRates[columIndex-1] += dataList.get(i).getAcceptGsPlanNum();
					row.createCell(columIndex++).setCellValue(dataList.get(i).getAcceptRate());                                           //验收率
					
					row.createCell(columIndex++).setCellValue(dataList.get(i).getShouldRsSchNum());                                       //应留样学校
					totalNumOrRates[columIndex-1] += dataList.get(i).getShouldRsSchNum();
					row.createCell(columIndex++).setCellValue(dataList.get(i).getNoRsSchNum());                                         //未留样学校
					totalNumOrRates[columIndex-1] += dataList.get(i).getNoRsSchNum();
					row.createCell(columIndex++).setCellValue(dataList.get(i).getRsSchNum());                                             //已留样学校
					totalNumOrRates[columIndex-1] += dataList.get(i).getRsSchNum();
					row.createCell(columIndex++).setCellValue(dataList.get(i).getSchRsRate());                                            //学校留样率
					
					row.createCell(columIndex++).setCellValue(dataList.get(i).getTotalDishNum());                                        //菜品总数
					totalNumOrRates[columIndex-1] += dataList.get(i).getTotalDishNum();
					row.createCell(columIndex++).setCellValue(dataList.get(i).getNoRsDishNum());                                         //未留样数
					totalNumOrRates[columIndex-1] += dataList.get(i).getNoRsDishNum();
					row.createCell(columIndex++).setCellValue(dataList.get(i).getRsDishNum());                                           //已留样数
					totalNumOrRates[columIndex-1] += dataList.get(i).getRsDishNum();
					row.createCell(columIndex++).setCellValue(dataList.get(i).getRsRate());                                              //留样率
					row.createCell(columIndex++).setCellValue(dataList.get(i).getTotalWarnNum());                                        //预警总数
					totalNumOrRates[columIndex-1] += dataList.get(i).getTotalWarnNum();
					row.createCell(columIndex++).setCellValue(dataList.get(i).getNoProcWarnNum());                                       //待处理数
					totalNumOrRates[columIndex-1] += dataList.get(i).getNoProcWarnNum();
					row.createCell(columIndex++).setCellValue(dataList.get(i).getElimWarnNum());                                         //已消除数
					totalNumOrRates[columIndex-1] += dataList.get(i).getElimWarnNum();
					row.createCell(columIndex++).setCellValue(dataList.get(i).getWarnProcRate());                                        //处理率
					row.createCell(columIndex++).setCellValue(dataList.get(i).getKwSchRecNum());                                         //餐厨垃圾学校回收
					totalNumOrRates[columIndex-1] += dataList.get(i).getKwSchRecNum();
					row.createCell(columIndex++).setCellValue(dataList.get(i).getWoSchRecNum());                                         //废弃油脂学校回收
					totalNumOrRates[columIndex-1] += dataList.get(i).getWoSchRecNum();
				}
				else if(schSelMode == 1) {    //按所在地
					row.createCell(columIndex++).setCellValue(dataList.get(i).getDishDate());                                             //起止日期
					row.createCell(columIndex++).setCellValue(dataList.get(i).getDistName());                                             //所在区	
					row.createCell(columIndex++).setCellValue(dataList.get(i).getMealSchNum());                                           //应排菜天数
					totalNumOrRates[columIndex-1] += dataList.get(i).getMealSchNum();
					row.createCell(columIndex++).setCellValue(dataList.get(i).getNoDishSchNum());                                         //未排菜天数
					totalNumOrRates[columIndex-1] += dataList.get(i).getNoDishSchNum();
					row.createCell(columIndex++).setCellValue(dataList.get(i).getDishSchNum());                                           //已排菜天数
					totalNumOrRates[columIndex-1] += dataList.get(i).getDishSchNum();
					row.createCell(columIndex++).setCellValue(dataList.get(i).getDishRate());                                             //排菜率
					
					row.createCell(columIndex++).setCellValue(dataList.get(i).getShouldAcceptSchNum());                                   //应验收学校
					totalNumOrRates[columIndex-1] += dataList.get(i).getShouldAcceptSchNum();
					row.createCell(columIndex++).setCellValue(dataList.get(i).getNoAcceptSchNum());                                       //待验收学校
					totalNumOrRates[columIndex-1] += dataList.get(i).getNoAcceptSchNum();
					row.createCell(columIndex++).setCellValue(dataList.get(i).getAcceptSchNum());                                         //已验收学校
					totalNumOrRates[columIndex-1] += dataList.get(i).getAcceptSchNum();
					row.createCell(columIndex++).setCellValue(dataList.get(i).getSchAcceptRate());                                        //学校验收率
					
					row.createCell(columIndex++).setCellValue(dataList.get(i).getTotalGsPlanNum());                                       //配货计划总数
					totalNumOrRates[columIndex-1] += dataList.get(i).getTotalGsPlanNum();
					row.createCell(columIndex++).setCellValue(dataList.get(i).getNoAcceptGsPlanNum());                                    //待验收数
					totalNumOrRates[columIndex-1] += dataList.get(i).getNoAcceptGsPlanNum();
					row.createCell(columIndex++).setCellValue(dataList.get(i).getAcceptGsPlanNum());                                      //已验收数
					totalNumOrRates[columIndex-1] += dataList.get(i).getAcceptGsPlanNum();
					row.createCell(columIndex++).setCellValue(dataList.get(i).getAcceptRate());                                           //验收率
					
					row.createCell(columIndex++).setCellValue(dataList.get(i).getShouldRsSchNum());                                       //应留样学校
					totalNumOrRates[columIndex-1] += dataList.get(i).getShouldRsSchNum();
					row.createCell(columIndex++).setCellValue(dataList.get(i).getNoRsSchNum());                                         //未留样学校
					totalNumOrRates[columIndex-1] += dataList.get(i).getNoRsSchNum();
					row.createCell(columIndex++).setCellValue(dataList.get(i).getRsSchNum());                                             //已留样学校
					totalNumOrRates[columIndex-1] += dataList.get(i).getRsSchNum();
					row.createCell(columIndex++).setCellValue(dataList.get(i).getSchRsRate());                                            //学校留样率
					
					row.createCell(columIndex++).setCellValue(dataList.get(i).getTotalDishNum());                                        //菜品总数
					totalNumOrRates[columIndex-1] += dataList.get(i).getTotalDishNum();
					row.createCell(columIndex++).setCellValue(dataList.get(i).getNoRsDishNum());                                         //未留样数
					totalNumOrRates[columIndex-1] += dataList.get(i).getNoRsDishNum();
					row.createCell(columIndex++).setCellValue(dataList.get(i).getRsDishNum());                                           //已留样数
					totalNumOrRates[columIndex-1] += dataList.get(i).getRsDishNum();
					row.createCell(columIndex++).setCellValue(dataList.get(i).getRsRate());                                              //留样率
					row.createCell(columIndex++).setCellValue(dataList.get(i).getTotalWarnNum());                                        //预警总数
					totalNumOrRates[columIndex-1] += dataList.get(i).getTotalWarnNum();
					row.createCell(columIndex++).setCellValue(dataList.get(i).getNoProcWarnNum());                                       //待处理数
					totalNumOrRates[columIndex-1] += dataList.get(i).getNoProcWarnNum();
					row.createCell(columIndex++).setCellValue(dataList.get(i).getElimWarnNum());                                         //已消除数
					totalNumOrRates[columIndex-1] += dataList.get(i).getElimWarnNum();
					row.createCell(columIndex++).setCellValue(dataList.get(i).getWarnProcRate());                                        //处理率
					row.createCell(columIndex++).setCellValue(dataList.get(i).getTotalKwRecNum());                                       //餐厨垃圾回收合计
					totalNumOrRates[columIndex-1] += dataList.get(i).getTotalKwRecNum();
					row.createCell(columIndex++).setCellValue(dataList.get(i).getKwSchRecNum());                                         //餐厨垃圾学校回收
					totalNumOrRates[columIndex-1] += dataList.get(i).getKwSchRecNum();
					row.createCell(columIndex++).setCellValue(dataList.get(i).getKwRmcRecNum());                                         //餐厨垃圾团餐公司回收
					totalNumOrRates[columIndex-1] += dataList.get(i).getKwRmcRecNum();
					row.createCell(columIndex++).setCellValue(dataList.get(i).getTotalWoRecNum());                                       //废弃油脂回收合计
					totalNumOrRates[columIndex-1] += dataList.get(i).getTotalWoRecNum();
					row.createCell(columIndex++).setCellValue(dataList.get(i).getWoSchRecNum());                                         //废弃油脂学校回收
					totalNumOrRates[columIndex-1] += dataList.get(i).getWoSchRecNum();
					row.createCell(columIndex++).setCellValue(dataList.get(i).getWoRmcRecNum());                                         //废弃油脂团餐公司回收
					totalNumOrRates[columIndex-1] += dataList.get(i).getWoRmcRecNum();
				}
			}
			//合计
			if(schSelMode == 0) {    //按主管部门
				for(int i = 0; i < totalNumOrRates.length; i++) {
					if(i == 7 || i == 11 || i == 15 || i == 19 || i == 23 || i == 27) {
						if(totalNumOrRates[i-3] > 0) {
							totalNumOrRates[i] = 100 * totalNumOrRates[i-1]/totalNumOrRates[i-3];
							BigDecimal bd = new BigDecimal(totalNumOrRates[i]);
							totalNumOrRates[i] = bd.setScale(2, BigDecimal.ROUND_HALF_UP).floatValue();
							if (totalNumOrRates[i] > 100) {
								totalNumOrRates[i] = 100;
								totalNumOrRates[i-3] = totalNumOrRates[i-1];
							}
						}
					}
				}
			}
			else if(schSelMode == 1) {    //按所在地
				for(int i = 0; i < totalNumOrRates.length; i++) {
					if(i == 6 || i == 10 || i == 14 || i == 18 || i == 22 || i == 26) {
						if(totalNumOrRates[i-3] > 0) {
							totalNumOrRates[i] = 100 * totalNumOrRates[i-1]/totalNumOrRates[i-3];
							BigDecimal bd = new BigDecimal(totalNumOrRates[i]);
							totalNumOrRates[i] = bd.setScale(2, BigDecimal.ROUND_HALF_UP).floatValue();
							if (totalNumOrRates[i] > 100) {
								totalNumOrRates[i] = 100;
								totalNumOrRates[i-3] = totalNumOrRates[i-1];
							}
						}
					}
				}
			}			
			//创建合计一行
			startRowIdx += dataList.size();
			row = (Row) sheet.createRow(startRowIdx);
			row.createCell(0).setCellValue(startRowIdx); //序号
			if(schSelMode == 0) {    //按主管部门
				for (int i = 1; i < colNames0.length; i++) {
					cell = row.createCell(i);
					if(i == 1) 
						cell.setCellValue(totalDishDate);
					else if(i == 2 || i == 3)
						cell.setCellValue(totalDistName);
					else if(i == 7 || i == 11 || i == 15 || i == 19 || i == 23 || i == 27 ) {
						cell.setCellValue(totalNumOrRates[i]);
					}
					else {
						cell.setCellValue((int)(totalNumOrRates[i]));
					}
				}
			}
			else if(schSelMode == 1) {    //按所在地
				for (int i = 1; i < colNames1.length; i++) {
					cell = row.createCell(i);
					if(i == 1) 
						cell.setCellValue(totalDishDate);
					else if(i == 2)
						cell.setCellValue(totalDistName);
					else if(i == 6 || i == 10 || i == 14 || i == 18 || i == 22 || i == 26) {
						cell.setCellValue(totalNumOrRates[i]);
					}
					else {
						cell.setCellValue((int)(totalNumOrRates[i]));
					}
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
	
	//导出汇总数据详情列表模型函数
	public ExpSumDataDetsDTO appModFunc(String token, String startDate, String endDate, 
			String schSelMode, String subLevel, String compDep, String subDistName,
			String distName,String departmentId, String prefCity, String province, 
			String subLevels,String compDeps,String distNames,String departmentIds,
			Db1Service db1Service, Db2Service db2Service,DbHiveWarnService dbHiveWarnService,
			DbHiveRecyclerWasteService dbHiveRecyclerWasteService,
			DbHiveDishService dbHiveDishService,DbHiveGsService dbHiveGsService) {
		ExpSumDataDetsDTO sddDto = null;
		if (isRealData) { // 真实数据
			String strCurPageNum = String.valueOf(curPageNum), strPageSize = String.valueOf(pageSize);
			if (startDate == null || endDate == null) {   // 按照当天日期获取数据
				startDate = BCDTimeUtil.convertNormalDate(null);
				endDate = startDate;
			}
			SumDataDetsDTO pdlDto = sddAppMod.appModFunc(token, startDate, endDate, schSelMode, 
					subLevel, compDep, subDistName, distName,departmentId, prefCity, province,
					subLevels,compDeps,distNames,departmentIds,
					strCurPageNum, strPageSize, db1Service, db2Service,dbHiveWarnService,
					dbHiveRecyclerWasteService,dbHiveDishService,dbHiveGsService);
			if(pdlDto != null) {
				int i, totalCount = pdlDto.getPageInfo().getPageTotal();
				int pageCount = 0;
				List<SumDataDets> expExcelList = new ArrayList<>();
				if(totalCount % pageSize == 0)
					pageCount = totalCount/pageSize;
				else
					pageCount = totalCount/pageSize + 1;
				//第一页数据
				if(pdlDto.getSumDataDets() != null) {
					expExcelList.addAll(pdlDto.getSumDataDets());			
				}
				//后续页数据
				for(i = curPageNum+1; i <= pageCount; i++) {
					strCurPageNum = String.valueOf(i);
					SumDataDetsDTO curPdlDto = sddAppMod.appModFunc(token, startDate, endDate, schSelMode, 
							subLevel, compDep, subDistName, distName, departmentId,prefCity, province, 
							subLevels,compDeps,distNames,departmentIds,
							strCurPageNum, strPageSize, db1Service, db2Service,dbHiveWarnService,
							dbHiveRecyclerWasteService,dbHiveDishService,dbHiveGsService);
					if(curPdlDto.getSumDataDets() != null) {
						expExcelList.addAll(curPdlDto.getSumDataDets());
					}
				}
				//生成导出EXCEL文件
				String repFileName = repFileResPath + UniqueIdGen.uuid() + SpringConfig.repFileFormats[SpringConfig.curRepFileFrmtIdx];
				String pathFileName = SpringConfig.base_dir + repFileName;
				logger.info("导出文件路径：" + pathFileName);
				int curschSelMode = 0;
				if(schSelMode != null)
					curschSelMode = Integer.parseInt(schSelMode);
				boolean flag = expSumDataDetsExcel(pathFileName, expExcelList, curschSelMode);
				if(flag) {
					//移动文件到其他目录
					//AppModConfig.moveFileToOtherFolder(pathFileName, SpringConfig.tomcatSrvDirs[1] + repFileResPath);
					sddDto = new ExpSumDataDetsDTO();
					ExpSumDataDets expSumDataDets = new ExpSumDataDets();
					//时戳
					sddDto.setTime(BCDTimeUtil.convertNormalFrom(null));
					//导出信息
					expSumDataDets.setStartDate(startDate);
					expSumDataDets.setEndDate(endDate);		
					expSumDataDets.setSchSelMode(schSelMode);
					expSumDataDets.setSubLevel(subLevel);
					expSumDataDets.setCompDep(compDep);
					expSumDataDets.setSubDistName(subDistName);		
					expSumDataDets.setDistName(distName);
					expSumDataDets.setPrefCity(prefCity);
					expSumDataDets.setProvince(province);
					expFileUrl = SpringConfig.repfile_srvdn + repFileName;
					logger.info("导出文件URL：" + expFileUrl);
					expSumDataDets.setExpFileUrl(expFileUrl);
					sddDto.setExpSumDataDets(expSumDataDets);
					//消息ID
					sddDto.setMsgId(AppModConfig.msgId);
					AppModConfig.msgId++;
					// 消息id小于0判断
					AppModConfig.msgIdLessThan0Judge();
				}
			}
		} else { // 模拟数据
			// 模拟数据函数
			sddDto = SimuDataFunc();
		}

		return sddDto;
	}
}
