package com.tfit.BdBiProcSrvShEduOmc.appmod.im;

import java.io.ByteArrayOutputStream;import java.io.File;import com.tfit.BdBiProcSrvShEduOmc.util.FtpUtil;
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

import com.tfit.BdBiProcSrvShEduOmc.config.AppModConfig;
import com.tfit.BdBiProcSrvShEduOmc.config.SpringConfig;
import com.tfit.BdBiProcSrvShEduOmc.dto.im.ExpGsPlanOptDets;
import com.tfit.BdBiProcSrvShEduOmc.dto.im.ExpGsPlanOptDetsDTO;
import com.tfit.BdBiProcSrvShEduOmc.dto.im.GsPlanOptDets;
import com.tfit.BdBiProcSrvShEduOmc.dto.im.GsPlanOptDetsDTO;
import com.tfit.BdBiProcSrvShEduOmc.dto.user.UserSetColums;
import com.tfit.BdBiProcSrvShEduOmc.obj.base.DepartmentObj;
import com.tfit.BdBiProcSrvShEduOmc.service.Db1Service;
import com.tfit.BdBiProcSrvShEduOmc.service.Db2Service;
import com.tfit.BdBiProcSrvShEduOmc.service.DbHiveGsService;
import com.tfit.BdBiProcSrvShEduOmc.service.SaasService;
import com.tfit.BdBiProcSrvShEduOmc.util.BCDTimeUtil;
import com.tfit.BdBiProcSrvShEduOmc.util.CommonUtil;
import com.tfit.BdBiProcSrvShEduOmc.util.UniqueIdGen;

//导出配货计划操作详情列表应用模型
public class ExpGsPlanOptDetsAppMod {
	private static final Logger logger = LogManager.getLogger(ExpGsPlanOptDetsAppMod.class.getName());
	
	//配货计划操作详情列表应用模型
	private GsPlanOptDetsAppMod egpodAppMod = new GsPlanOptDetsAppMod();
	
	//是否为真实数据标识
	private static boolean isRealData = true;
	//页号、页大小和总页数
	int curPageNum = 1, pageTotal = 1, pageSize = AppModConfig.maxPageSize;
	//报表文件资源路径
	String repFileResPath = "/expGsPlanOptDets/";
	//导出列名数组
	String[] colNames = {"序号","配货日期","管理部门","验收上报日期","验收操作状态", "配货批次号","所在地","学校学制", "项目点",
			"指派状态", "配送状态", "验收状态","所属", "主管部门",  "总校/分校", "分校数量", "关联总校", 
			"食品经营许可证主体",  "办学性质",  "团餐公司", "配送类型", "配货方式"};
	//变量数据初始化
	String startDate = "2018-10-24";
	String endDate = "2018-10-24";
	String ppName = null;
	String distName = null;
	String prefCity = null;
	String province = "上海市";
	String rmcName = null;
	String assignStatus = null;
	String dispStatus = null;
	String acceptStatus = null;
	String distrBatNumber = null;
	String schType = null;
	String dispType = null;
	String dispMode = null;
	String sendFlag = null;
	String expFileUrl = "test1.txt";
	
	//模拟数据函数
	private ExpGsPlanOptDetsDTO SimuDataFunc() {
		ExpGsPlanOptDetsDTO egpodDto = new ExpGsPlanOptDetsDTO();
		//设置返回数据
		egpodDto.setTime(BCDTimeUtil.convertNormalFrom(null));
		//列表元素设置
		ExpGsPlanOptDets expGsPlanOptDets = new ExpGsPlanOptDets();
		//赋值
		expGsPlanOptDets.setStartDate(startDate);
		expGsPlanOptDets.setEndDate(endDate);
		expGsPlanOptDets.setPpName(ppName);
		expGsPlanOptDets.setDistName(distName);
		expGsPlanOptDets.setPrefCity(prefCity);
		expGsPlanOptDets.setProvince(province);
		expGsPlanOptDets.setRmcName(rmcName);
		expGsPlanOptDets.setAssignStatus(assignStatus);
		expGsPlanOptDets.setDispStatus(dispStatus);
		expGsPlanOptDets.setAcceptStatus(acceptStatus);
		expGsPlanOptDets.setDistrBatNumber(distrBatNumber);
		expGsPlanOptDets.setSchType(schType);
		expGsPlanOptDets.setDispType(dispType);
		expGsPlanOptDets.setDispMode(dispMode);
		expGsPlanOptDets.setSendFlag(sendFlag);
		expGsPlanOptDets.setExpFileUrl(SpringConfig.repfile_srvdn+repFileResPath+expFileUrl);
		//设置模拟数据
		egpodDto.setExpGsPlanOptDets(expGsPlanOptDets);
		//消息ID
		egpodDto.setMsgId(AppModConfig.msgId);
		AppModConfig.msgId++;
		//消息id小于0判断
		AppModConfig.msgIdLessThan0Judge();

		return egpodDto;
	}
	
	//生成导出EXCEL文件
	public boolean expGsPlanOptDetsExcel(String pathFileName, List<GsPlanOptDets> dataList, Map<String,DepartmentObj> deparmentMap,List<UserSetColums> userSetColumsList, String colNames[]) { 
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
			int startColumnIdx = 0;
			for (int i = 0; i < dataList.size(); i++) {
				startColumnIdx = 0;
				row = (Row) sheet.createRow(i + startRowIdx);
				
				//导出列名数组
				if(userSetColumsList !=null && userSetColumsList.size() > 0) {
					for(UserSetColums obj : userSetColumsList) {
					  if(obj != null && obj.isChecked()) {
						  if("sortNo".equals(obj.getKey())) 
								row.createCell(startColumnIdx++).setCellValue(i+1);//序号
					    if("distrDate".equals(obj.getKey())) 
					      	row.createCell(startColumnIdx++).setCellValue(dataList.get(i).getDistrDate());                                                    //配货日期	
					    if("departmentName".equals(obj.getKey())) 
					      	row.createCell(startColumnIdx++).setCellValue(deparmentMap.get(dataList.get(i).getDepartmentId())==null?"":deparmentMap.get(dataList.get(i).getDepartmentId()).getDepartmentName()); 	
					    if("acceptTime".equals(obj.getKey())) 
					      	row.createCell(startColumnIdx++).setCellValue(dataList.get(i).getAcceptTime());                                               //验收日期	
					    if("plaStatusName".equals(obj.getKey())) 
					      	row.createCell(startColumnIdx++).setCellValue(CommonUtil.isEmpty(dataList.get(i).getPlaStatus())?"无数据":AppModConfig.plaStatusIdToNameMap.get(dataList.get(i).getPlaStatus()));////验收操作状态 	
					    if("distrBatNumber".equals(obj.getKey())) 
					      	row.createCell(startColumnIdx++).setCellValue(dataList.get(i).getDistrBatNumber());                                               //配货批次号	
					    if("distName".equals(obj.getKey())) 
					      	row.createCell(startColumnIdx++).setCellValue(AppModConfig.distIdToNameMap.get(dataList.get(i).getDistName()));                  //所在地	
					    if("schType".equals(obj.getKey())) 
					      	row.createCell(startColumnIdx++).setCellValue(dataList.get(i).getSchType());                                                      //学校学制	
					    if("ppName".equals(obj.getKey())) 
					      	row.createCell(startColumnIdx++).setCellValue(dataList.get(i).getPpName());                                                       //项目点	
					    if("assignStatus".equals(obj.getKey())) 
					      	row.createCell(startColumnIdx++).setCellValue(AppModConfig.assignStatusIdToNameMap.get(dataList.get(i).getAssignStatus()));      //指派状态	
					    if("dispStatus".equals(obj.getKey())) 
					      	row.createCell(startColumnIdx++).setCellValue(AppModConfig.dispStatusIdToNameMap.get(dataList.get(i).getDispStatus()));          //配送状态	
					    if("acceptStatus".equals(obj.getKey())) 
					      	row.createCell(startColumnIdx++).setCellValue(AppModConfig.acceptStatusIdToNameMap.get(dataList.get(i).getAcceptStatus()));      //验收状态	
					    if("subLevel".equals(obj.getKey())) 
					      	row.createCell(startColumnIdx++).setCellValue(dataList.get(i).getSubLevel());                                                     //所属	
					    if("compDep".equals(obj.getKey())) 
					      	row.createCell(startColumnIdx++).setCellValue(dataList.get(i).getCompDep());                                                      //主管部门	
					    if("schGenBraFlag".equals(obj.getKey())) 
					      	row.createCell(startColumnIdx++).setCellValue(dataList.get(i).getSchGenBraFlag());                                                //总校/分校	
					    if("braCampusNum".equals(obj.getKey())) 
					      	row.createCell(startColumnIdx++).setCellValue(dataList.get(i).getBraCampusNum());                                                 //分校数量	
					    if("relGenSchName".equals(obj.getKey())) 
					      	row.createCell(startColumnIdx++).setCellValue(dataList.get(i).getRelGenSchName());                                                //关联总校	
					    if("fblMb".equals(obj.getKey())) 
					      	row.createCell(startColumnIdx++).setCellValue(dataList.get(i).getFblMb());                                                        //食品经营许可证主体	
					    if("schProp".equals(obj.getKey())) 
					      	row.createCell(startColumnIdx++).setCellValue(dataList.get(i).getSchProp());                                                     //办学性质	
					    if("rmcName".equals(obj.getKey())) 
					      	row.createCell(startColumnIdx++).setCellValue(dataList.get(i).getRmcName());                                                     //团餐公司	
					    if("dispType".equals(obj.getKey())) 
					      	row.createCell(startColumnIdx++).setCellValue(dataList.get(i).getDispType());                                                    //配送类型	
					    if("dispMode".equals(obj.getKey())) 
					      	row.createCell(startColumnIdx++).setCellValue(dataList.get(i).getDispMode());                                                    //配货方式	

					  }
					}
				  }else {
				    row.createCell(startColumnIdx++).setCellValue(i+1);
					row.createCell(startColumnIdx++).setCellValue(dataList.get(i).getDistrDate());                                                    //配货日期
					row.createCell(startColumnIdx++).setCellValue(deparmentMap.get(dataList.get(i).getDepartmentId())==null?"":deparmentMap.get(dataList.get(i).getDepartmentId()).getDepartmentName()); 
					row.createCell(startColumnIdx++).setCellValue(dataList.get(i).getAcceptTime());                                               //验收日期
					row.createCell(startColumnIdx++).setCellValue(CommonUtil.isEmpty(dataList.get(i).getPlaStatus())?"无数据":AppModConfig.plaStatusIdToNameMap.get(dataList.get(i).getPlaStatus()));////验收操作状态 
					row.createCell(startColumnIdx++).setCellValue(dataList.get(i).getDistrBatNumber());                                               //配货批次号
					row.createCell(startColumnIdx++).setCellValue(AppModConfig.distIdToNameMap.get(dataList.get(i).getDistName()));                  //所在地
					row.createCell(startColumnIdx++).setCellValue(dataList.get(i).getSchType());                                                      //学校学制
					row.createCell(startColumnIdx++).setCellValue(dataList.get(i).getPpName());                                                       //项目点
					row.createCell(startColumnIdx++).setCellValue(AppModConfig.assignStatusIdToNameMap.get(dataList.get(i).getAssignStatus()));      //指派状态
					row.createCell(startColumnIdx++).setCellValue(AppModConfig.dispStatusIdToNameMap.get(dataList.get(i).getDispStatus()));          //配送状态
					row.createCell(startColumnIdx++).setCellValue(AppModConfig.acceptStatusIdToNameMap.get(dataList.get(i).getAcceptStatus()));      //验收状态
					row.createCell(startColumnIdx++).setCellValue(dataList.get(i).getSubLevel());                                                     //所属
					row.createCell(startColumnIdx++).setCellValue(dataList.get(i).getCompDep());                                                      //主管部门
					row.createCell(startColumnIdx++).setCellValue(dataList.get(i).getSchGenBraFlag());                                                //总校/分校
					row.createCell(startColumnIdx++).setCellValue(dataList.get(i).getBraCampusNum());                                                 //分校数量
					row.createCell(startColumnIdx++).setCellValue(dataList.get(i).getRelGenSchName());                                                //关联总校
					row.createCell(startColumnIdx++).setCellValue(dataList.get(i).getFblMb());                                                        //食品经营许可证主体
					row.createCell(startColumnIdx++).setCellValue(dataList.get(i).getSchProp());                                                     //办学性质
					row.createCell(startColumnIdx++).setCellValue(dataList.get(i).getRmcName());                                                     //团餐公司
					row.createCell(startColumnIdx++).setCellValue(dataList.get(i).getDispType());                                                    //配送类型
					row.createCell(startColumnIdx++).setCellValue(dataList.get(i).getDispMode());                                                    //配货方式
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
	
	//导出配货计划操作详情列表模型函数
	public ExpGsPlanOptDetsDTO appModFunc(String token, String startDate, String endDate, String ppName, 
			String distName, String prefCity, String province, String subLevel, String compDep, 
			String schGenBraFlag, String subDistName, String fblMb, String schProp, String rmcName, 
			String assignStatus, String dispStatus, String acceptStatus, String distrBatNumber, String schType, 
			String dispType, String dispMode, String sendFlag,
			String distNames,String subLevels,String compDeps,String schProps,
			String schTypes,String dispStatuss,String assignStatuss,
			String departmentId,String departmentIds,String plastatus,
			Db1Service db1Service, Db2Service db2Service, SaasService saasService,
			DbHiveGsService dbHiveGsService) {
		ExpGsPlanOptDetsDTO egpodDto = null;
		if (isRealData) { // 真实数据
			String strCurPageNum = String.valueOf(curPageNum), strPageSize = String.valueOf(pageSize);
			if (startDate == null || endDate == null) {   // 按照当天日期获取数据
				startDate = BCDTimeUtil.convertNormalDate(null);
				endDate = startDate;
			}
			GsPlanOptDetsDTO pddDto = egpodAppMod.appModFunc(token, startDate, endDate, ppName, 
					distName, prefCity, province, subLevel, compDep, schGenBraFlag, subDistName, 
					fblMb, schProp, rmcName, assignStatus, dispStatus, acceptStatus, distrBatNumber, 
					schType, dispType, dispMode, sendFlag, 
					distNames,subLevels,compDeps,schProps,
					schTypes,dispStatuss,assignStatuss,
					departmentId,departmentIds,plastatus,null,
					strCurPageNum, strPageSize, 
					db1Service, db2Service, saasService,dbHiveGsService);
			if(pddDto != null) {
				int i, totalCount = pddDto.getPageInfo().getPageTotal();
				int pageCount = 0;
				List<GsPlanOptDets> expExcelList = new ArrayList<>();
				if(totalCount % pageSize == 0)
					pageCount = totalCount/pageSize;
				else
					pageCount = totalCount/pageSize + 1;
				//第一页数据
				if(pddDto.getGsPlanOptDets() != null) {
					expExcelList.addAll(pddDto.getGsPlanOptDets());			
				}
				//后续页数据
				for(i = curPageNum+1; i <= pageCount; i++) {
					strCurPageNum = String.valueOf(i);
					GsPlanOptDetsDTO curPdlDto = egpodAppMod.appModFunc(token, startDate, endDate, ppName, 
							distName, prefCity, province, subLevel, compDep, schGenBraFlag, subDistName, 
							fblMb, schProp, rmcName, assignStatus, dispStatus, acceptStatus, distrBatNumber, 
							schType, dispType, dispMode, sendFlag,
							distNames,subLevels,compDeps,schProps,
							schTypes,dispStatuss,assignStatuss,
							departmentId,departmentIds,plastatus,null,
							strCurPageNum, strPageSize, 
							db1Service, db2Service, saasService,dbHiveGsService);
					if(curPdlDto.getGsPlanOptDets() != null) {
						expExcelList.addAll(curPdlDto.getGsPlanOptDets());
					}
				}
				//生成导出EXCEL文件
				String repFileName = repFileResPath + UniqueIdGen.uuid() + SpringConfig.repFileFormats[SpringConfig.curRepFileFrmtIdx];
				String pathFileName = SpringConfig.base_dir + repFileName;
				logger.info("导出文件路径：" + pathFileName);
				
				List<DepartmentObj> deparmentList =  db1Service.getDepartmentObjList(new DepartmentObj(),null, -1, -1);	
				Map<String,DepartmentObj> deparmentMap = deparmentList.stream().collect(Collectors.toMap(DepartmentObj::getDepartmentId,(b)->b));
				
				List<UserSetColums> userSetColumsList =CommonUtil.getUserSetColumList(token,"gsPlanOptDets", db2Service);
				
				boolean flag = expGsPlanOptDetsExcel(pathFileName, expExcelList,deparmentMap,userSetColumsList,  colNames);
				if(flag) {
					//移动文件到其他目录
					//AppModConfig.moveFileToOtherFolder(pathFileName, SpringConfig.tomcatSrvDirs[1] + repFileResPath);
					egpodDto = new ExpGsPlanOptDetsDTO();
					ExpGsPlanOptDets expGsPlanOptDets = new ExpGsPlanOptDets();
					//时戳
					egpodDto.setTime(BCDTimeUtil.convertNormalFrom(null));
					//导出信息
					expGsPlanOptDets.setStartDate(startDate);
					expGsPlanOptDets.setEndDate(endDate);
					expGsPlanOptDets.setPpName(ppName);
					expGsPlanOptDets.setDistName(AppModConfig.distIdToNameMap.get(distName));
					expGsPlanOptDets.setPrefCity(prefCity);
					expGsPlanOptDets.setProvince(province);
					expGsPlanOptDets.setRmcName(rmcName);
					expGsPlanOptDets.setAssignStatus(assignStatus);
					expGsPlanOptDets.setDispStatus(dispStatus);
					expGsPlanOptDets.setAcceptStatus(acceptStatus);
					expGsPlanOptDets.setDistrBatNumber(distrBatNumber);
					expGsPlanOptDets.setSchType(schType);
					expGsPlanOptDets.setDispType(dispType);
					expGsPlanOptDets.setDispMode(dispMode);
					expGsPlanOptDets.setSendFlag(sendFlag);
					expFileUrl = SpringConfig.repfile_srvdn + repFileName;
					logger.info("导出文件URL：" + expFileUrl);
					expGsPlanOptDets.setExpFileUrl(expFileUrl);
					egpodDto.setExpGsPlanOptDets(expGsPlanOptDets);
					//消息ID
					egpodDto.setMsgId(AppModConfig.msgId);
					AppModConfig.msgId++;
					// 消息id小于0判断
					AppModConfig.msgIdLessThan0Judge();
				}
			}
		} else { // 模拟数据
			// 模拟数据函数
			egpodDto = SimuDataFunc();
		}

		return egpodDto;
	}
}
