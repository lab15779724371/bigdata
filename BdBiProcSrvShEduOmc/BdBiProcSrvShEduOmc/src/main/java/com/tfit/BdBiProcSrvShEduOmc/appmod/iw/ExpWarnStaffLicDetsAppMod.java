package com.tfit.BdBiProcSrvShEduOmc.appmod.iw;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

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
import com.tfit.BdBiProcSrvShEduOmc.dto.iw.ExpWarnStaffLicDets;
import com.tfit.BdBiProcSrvShEduOmc.dto.iw.ExpWarnStaffLicDetsDTO;
import com.tfit.BdBiProcSrvShEduOmc.dto.iw.WarnStaffLicDets;
import com.tfit.BdBiProcSrvShEduOmc.dto.iw.WarnStaffLicDetsDTO;
import com.tfit.BdBiProcSrvShEduOmc.dto.user.UserSetColums;
import com.tfit.BdBiProcSrvShEduOmc.obj.base.DepartmentObj;
import com.tfit.BdBiProcSrvShEduOmc.service.Db1Service;
import com.tfit.BdBiProcSrvShEduOmc.service.Db2Service;
import com.tfit.BdBiProcSrvShEduOmc.service.DbHiveWarnService;
import com.tfit.BdBiProcSrvShEduOmc.service.SaasService;
import com.tfit.BdBiProcSrvShEduOmc.util.BCDTimeUtil;
import com.tfit.BdBiProcSrvShEduOmc.util.CommonUtil;
import com.tfit.BdBiProcSrvShEduOmc.util.FtpUtil;
import com.tfit.BdBiProcSrvShEduOmc.util.UniqueIdGen;

//导出证照预警人员证件详情列表应用模型
public class ExpWarnStaffLicDetsAppMod {
	private static final Logger logger = LogManager.getLogger(ExpWarnStaffLicDetsAppMod.class.getName());
	
	//证照预警人员证件详情列表应用模型
	private WarnStaffLicDetsAppMod wsldAppMod = new WarnStaffLicDetsAppMod();
	
	//是否为真实数据标识
	private static boolean isRealData = true;
	//页号、页大小和总页数
	int curPageNum = 1, pageTotal = 1, pageSize = AppModConfig.maxPageSize;
	//报表文件资源路径
	String repFileResPath = "/expWarnStaffLicDets/";
	//导出列名数组
	//String[] colNames = {"序号","预警日期", "区", "团餐公司名称", "关联学校", "证件名称", "姓名", "证件号码", "有效日期", "证件状况", "状态", "消除日期"};	
	String[] colNames = {"序号","预警日期","管理部门", "区","学制","办学性质","学校名称","地址","联系人","手机号码", "触发预警单位", "证件名称","证件主体", "证件号码", "有效日期", "证件状况", "状态", "消除日期"};
	//变量数据初始化
	String startWarnDate = "2018-09-03";
	String endWarnDate = "2018-09-04";
	String distName = null;
	String prefCity = null;
	String province = "上海市";
	String rmcName = null;
	String licType = null;
	String licAuditStatus = null;
	String fullName = null;
	String startElimDate = null;
	String endElimDate = null;
	String startValidDate = null;
	String endValidDate = null;
	String licNo = null;
	String schName = null;
	String expFileUrl = "test1.txt";

	//模拟数据函数
	private ExpWarnStaffLicDetsDTO SimuDataFunc() {
		ExpWarnStaffLicDetsDTO ewsldDto = new ExpWarnStaffLicDetsDTO();
		//设置返回数据
		ewsldDto.setTime(BCDTimeUtil.convertNormalFrom(null));
		//列表元素设置
		ExpWarnStaffLicDets expWarnStaffLicDets = new ExpWarnStaffLicDets();
		//赋值
		expWarnStaffLicDets.setStartWarnDate(startWarnDate);
		expWarnStaffLicDets.setEndWarnDate(endWarnDate);
		expWarnStaffLicDets.setDistName(distName);
		expWarnStaffLicDets.setPrefCity(prefCity);
		expWarnStaffLicDets.setProvince(province);
		expWarnStaffLicDets.setRmcName(rmcName);
		expWarnStaffLicDets.setLicType(licType);
		expWarnStaffLicDets.setLicAuditStatus(licAuditStatus);
		expWarnStaffLicDets.setFullName(fullName);
		expWarnStaffLicDets.setStartElimDate(startElimDate);
		expWarnStaffLicDets.setEndElimDate(endElimDate);
		expWarnStaffLicDets.setStartValidDate(startValidDate);
		expWarnStaffLicDets.setEndValidDate(endValidDate);
		expWarnStaffLicDets.setLicNo(licNo);
		expWarnStaffLicDets.setSchName(schName);
		expWarnStaffLicDets.setExpFileUrl(SpringConfig.repfile_srvdn+repFileResPath+expFileUrl);
		//设置模拟数据
		ewsldDto.setExpWarnStaffLicDets(expWarnStaffLicDets);
		//消息ID
		ewsldDto.setMsgId(AppModConfig.msgId);
		AppModConfig.msgId++;
		//消息id小于0判断
		AppModConfig.msgIdLessThan0Judge();

		return ewsldDto;
	}
	
	//生成导出EXCEL文件
	public boolean expWarnStaffLicDetsExcel(Map<String, String> departmentMap,String pathFileName, List<WarnStaffLicDets> dataList,List<UserSetColums> userSetColumsList, String colNames[]) { 
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
        	int startColIdx = 0;
			// 添加样式
			Row row = null;
			Cell cell = null;
			// 创建第一行
			row = (Row) sheet.createRow(startRowIdx);
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
				} catch (UnsupportedEncodingException e) {
					// TODO 自动生成的 catch 块
					e.printStackTrace();
				}
			}
			// 循环写入行数据
			startRowIdx++;
			//String[] colNames = {"序号","预警日期", "区", "团餐公司名称", "关联学校", "证件名称", "姓名", "证件号码", "有效日期", "证件状况", "状态", "消除日期"};	
			//String[] colNames = {"序号","预警日期","管理部门", "区","学制","办学性质","学校名称","地址","联系人","手机号码", "触发预警单位", "证件名称","证件主体", "证件号码", "有效日期", "证件状况", "状态", "消除日期"};
			for (int i = 0; i < dataList.size(); i++) {
				startColIdx = 0;
				row = (Row) sheet.createRow(i + startRowIdx);
				if(userSetColumsList !=null && userSetColumsList.size() > 0) {
					for(UserSetColums obj : userSetColumsList) {
					  if(obj != null && obj.isChecked()) {
						if("sortNo".equals(obj.getKey())) 
						  row.createCell(startColIdx++).setCellValue(i+1);
					    if("warnDate".equals(obj.getKey())) 
					      	row.createCell(startColIdx++).setCellValue(dataList.get(i).getWarnDate());                                           //预警日期	
					    if("departmentId".equals(obj.getKey())) 
					      	row.createCell(startColIdx++).setCellValue(departmentMap.get(dataList.get(i).getDepartmentId()));                    //管理部门	
					    if("distName".equals(obj.getKey())) 
					      	row.createCell(startColIdx++).setCellValue(AppModConfig.distIdToNameMap.get(dataList.get(i).getDistName()));         //区	
					    if("schType".equals(obj.getKey())) 
					      	row.createCell(startColIdx++).setCellValue(dataList.get(i).getSchType());                                            //学校学制	
					    if("schProp".equals(obj.getKey())) 
					      	row.createCell(startColIdx++).setCellValue(dataList.get(i).getSchProp());                                            //办学性质	
					    if("schName".equals(obj.getKey())) 
					      	row.createCell(startColIdx++).setCellValue(dataList.get(i).getSchName());                                            //学校名称	
					    if("address".equals(obj.getKey())) 
					      	row.createCell(startColIdx++).setCellValue(dataList.get(i).getAddress());                                            //地址	
					    if("foodSafetyPersion".equals(obj.getKey())) 
					      	row.createCell(startColIdx++).setCellValue(dataList.get(i).getFoodSafetyPersion());                                  //联系人	
					    if("foodSafetyMobilephone".equals(obj.getKey())) 
					      	row.createCell(startColIdx++).setCellValue(dataList.get(i).getFoodSafetyMobilephone());                              //手机号	
					    if("trigWarnUnit".equals(obj.getKey())) 
					      	row.createCell(startColIdx++).setCellValue(dataList.get(i).getTrigWarnUnit());                                       //触发预警单位	
					    if("licName".equals(obj.getKey())) 
					      	row.createCell(startColIdx++).setCellValue(dataList.get(i).getLicName());                                            //证件名称	
					    if("fullName".equals(obj.getKey())) 
					      	row.createCell(startColIdx++).setCellValue(dataList.get(i).getFullName());                                           //证件主体	
					    if("licNo".equals(obj.getKey())) 
					      	row.createCell(startColIdx++).setCellValue(dataList.get(i).getLicNo());                                              //证件号码	
					    if("validDate".equals(obj.getKey())) 
					      	row.createCell(startColIdx++).setCellValue(dataList.get(i).getValidDate());                                          //有效日期	
					    if("licStatus".equals(obj.getKey())) 
					      	row.createCell(startColIdx++).setCellValue(dataList.get(i).getLicStatus());                                          //证件状况	
					    if("licAuditStatus".equals(obj.getKey())) 
					      	row.createCell(startColIdx++).setCellValue(AppModConfig.licAudStatusIdToNameMap.get(dataList.get(i).getLicAuditStatus()));//状态	
					    if("elimDate".equals(obj.getKey())) 
					      	row.createCell(startColIdx++).setCellValue(dataList.get(i).getElimDate());                                           //消除日期	

					  }
					}
				  }else {
					  row.createCell(startColIdx++).setCellValue(i+1);
						row.createCell(startColIdx++).setCellValue(dataList.get(i).getWarnDate());                                           //预警日期
						row.createCell(startColIdx++).setCellValue(departmentMap.get(dataList.get(i).getDepartmentId()));                    //管理部门
						row.createCell(startColIdx++).setCellValue(AppModConfig.distIdToNameMap.get(dataList.get(i).getDistName()));         //区
						row.createCell(startColIdx++).setCellValue(dataList.get(i).getSchType());                                            //学校学制
						row.createCell(startColIdx++).setCellValue(dataList.get(i).getSchProp());                                            //办学性质
						row.createCell(startColIdx++).setCellValue(dataList.get(i).getSchName());                                            //学校名称
						row.createCell(startColIdx++).setCellValue(dataList.get(i).getAddress());                                            //地址
						row.createCell(startColIdx++).setCellValue(dataList.get(i).getFoodSafetyPersion());                                  //联系人
						row.createCell(startColIdx++).setCellValue(dataList.get(i).getFoodSafetyMobilephone());                              //手机号
						row.createCell(startColIdx++).setCellValue(dataList.get(i).getTrigWarnUnit());                                       //触发预警单位
						row.createCell(startColIdx++).setCellValue(dataList.get(i).getLicName());                                            //证件名称
						row.createCell(startColIdx++).setCellValue(dataList.get(i).getFullName());                                           //证件主体
						row.createCell(startColIdx++).setCellValue(dataList.get(i).getLicNo());                                              //证件号码
						row.createCell(startColIdx++).setCellValue(dataList.get(i).getValidDate());                                          //有效日期
						row.createCell(startColIdx++).setCellValue(dataList.get(i).getLicStatus());                                          //证件状况
						row.createCell(startColIdx++).setCellValue(AppModConfig.licAudStatusIdToNameMap.get(dataList.get(i).getLicAuditStatus()));//状态
						row.createCell(startColIdx++).setCellValue(dataList.get(i).getElimDate());                                           //消除日期
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
	
	//导出证照预警人员证件详情列表模型函数
	public ExpWarnStaffLicDetsDTO appModFunc(String token, String startWarnDate, String endWarnDate, String distName, 
			String prefCity, String province, String rmcName, String licType, String licStatus, String licAuditStatus, String licAuditStatuss, 
			String fullName, String startElimDate, String endElimDate, String startValidDate, String endValidDate, String licNo, 
			String schName, 
			String departmentId,String departmentIds,String schType,String schProp,
			Db1Service db1Service, Db2Service db2Service, SaasService saasService,DbHiveWarnService dbHiveWarnService) {
		ExpWarnStaffLicDetsDTO ewsldDto = null;
		if (isRealData) { // 真实数据
			String strCurPageNum = String.valueOf(curPageNum), strPageSize = String.valueOf(pageSize);
			if (startWarnDate == null || endWarnDate == null) {   // 按照当天日期获取数据
				startWarnDate = BCDTimeUtil.convertNormalDate(null);
				endWarnDate = startWarnDate;
			}
			WarnStaffLicDetsDTO wsldDto = wsldAppMod.appModFunc(token, startWarnDate, endWarnDate, distName, 
					prefCity, province, rmcName, licType, licStatus, licAuditStatus,licAuditStatuss, fullName, startElimDate, 
					endElimDate, startValidDate, endValidDate, licNo, schName, 
					departmentId,departmentIds,schType,schProp,
					strCurPageNum, strPageSize, 
					db1Service, db2Service, saasService,dbHiveWarnService);
			if(wsldDto != null) {
				int i, totalCount = wsldDto.getPageInfo().getPageTotal();
				//int pageCount = 0;
				List<WarnStaffLicDets> expExcelList = new ArrayList<>();
				//if(totalCount % pageSize == 0)
				//	pageCount = totalCount/pageSize;
				//else
				//	pageCount = totalCount/pageSize + 1;
				////第一页数据
				//if(wsldDto.getWarnStaffLicDets() != null) {
				//	expExcelList.addAll(wsldDto.getWarnStaffLicDets());
				//}
				//后续页数据
				//for(i = curPageNum+1; i <= pageCount; i++) {
				//	strCurPageNum = String.valueOf(i);
				WarnStaffLicDetsDTO curPdlDto = wsldAppMod.appModFunc(token, startWarnDate, endWarnDate, distName, 
						prefCity, province, rmcName, licType, licStatus, licAuditStatus,licAuditStatuss, fullName, startElimDate, 
						endElimDate, startValidDate, endValidDate, licNo, schName, 
						departmentId,departmentIds,schType,schProp,
						String.valueOf(1), String.valueOf(totalCount), 
						db1Service, db2Service, saasService,dbHiveWarnService);
				if(curPdlDto.getWarnStaffLicDets() != null) {
					expExcelList.addAll(curPdlDto.getWarnStaffLicDets());
				}
				//}
				//生成导出EXCEL文件
				String repFileName = repFileResPath + UniqueIdGen.uuid() + SpringConfig.repFileFormats[SpringConfig.curRepFileFrmtIdx];
				String pathFileName = SpringConfig.base_dir + repFileName;
				logger.info("导出文件路径：" + pathFileName);
				Map<String, String> departmentMap = new LinkedHashMap<String,String>();
		    	DepartmentObj departmentObj = new DepartmentObj();
		    	departmentObj.setDepartmentId(departmentId);
				List<DepartmentObj> deparmentList =  db1Service.getDepartmentObjList(departmentObj,null, -1, -1);	
				
				if(deparmentList != null) {
					for(DepartmentObj department : deparmentList) {
						departmentMap.put(department.getDepartmentId(), department.getDepartmentName());
					}
				}
				List<UserSetColums> userSetColumsList =CommonUtil.getUserSetColumList(token,"warnStaffLicDets", db2Service);
				boolean flag = expWarnStaffLicDetsExcel(departmentMap,pathFileName, expExcelList,userSetColumsList,colNames);
				if(flag) {
					//移动文件到其他目录
					//AppModConfig.moveFileToOtherFolder(pathFileName, SpringConfig.tomcatSrvDirs[1] + repFileResPath);
					ewsldDto = new ExpWarnStaffLicDetsDTO();
					ExpWarnStaffLicDets expWarnStaffLicDets = new ExpWarnStaffLicDets();
					//时戳
					ewsldDto.setTime(BCDTimeUtil.convertNormalFrom(null));
					//导出信息
					expWarnStaffLicDets.setStartWarnDate(startWarnDate);
					expWarnStaffLicDets.setEndWarnDate(endWarnDate);
					expWarnStaffLicDets.setDistName(distName);
					expWarnStaffLicDets.setPrefCity(prefCity);
					expWarnStaffLicDets.setProvince(province);
					expWarnStaffLicDets.setRmcName(rmcName);
					expWarnStaffLicDets.setLicType(licType);
					expWarnStaffLicDets.setLicAuditStatus(licAuditStatus);
					expWarnStaffLicDets.setFullName(fullName);
					expWarnStaffLicDets.setStartElimDate(startElimDate);
					expWarnStaffLicDets.setEndElimDate(endElimDate);
					expWarnStaffLicDets.setStartValidDate(startValidDate);
					expWarnStaffLicDets.setEndValidDate(endValidDate);
					expWarnStaffLicDets.setLicNo(licNo);
					expWarnStaffLicDets.setSchName(schName);
					expFileUrl = SpringConfig.repfile_srvdn + repFileName;
					logger.info("导出文件URL：" + expFileUrl);
					expWarnStaffLicDets.setExpFileUrl(expFileUrl);
					ewsldDto.setExpWarnStaffLicDets(expWarnStaffLicDets);
					//消息ID
					ewsldDto.setMsgId(AppModConfig.msgId);
					AppModConfig.msgId++;
					// 消息id小于0判断
					AppModConfig.msgIdLessThan0Judge();
				}
			}
		} else { // 模拟数据
			// 模拟数据函数
			ewsldDto = SimuDataFunc();
		}

		return ewsldDto;
	}
}
