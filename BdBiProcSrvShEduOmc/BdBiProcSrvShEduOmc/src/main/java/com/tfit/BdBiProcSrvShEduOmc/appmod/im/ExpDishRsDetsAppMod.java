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
import com.tfit.BdBiProcSrvShEduOmc.dto.im.DishRsDets;
import com.tfit.BdBiProcSrvShEduOmc.dto.im.DishRsDetsDTO;
import com.tfit.BdBiProcSrvShEduOmc.dto.im.ExpDishRsDets;
import com.tfit.BdBiProcSrvShEduOmc.dto.im.ExpDishRsDetsDTO;
import com.tfit.BdBiProcSrvShEduOmc.dto.user.UserSetColums;
import com.tfit.BdBiProcSrvShEduOmc.obj.base.DepartmentObj;
import com.tfit.BdBiProcSrvShEduOmc.service.Db1Service;
import com.tfit.BdBiProcSrvShEduOmc.service.Db2Service;
import com.tfit.BdBiProcSrvShEduOmc.service.SaasService;
import com.tfit.BdBiProcSrvShEduOmc.util.BCDTimeUtil;
import com.tfit.BdBiProcSrvShEduOmc.util.CommonUtil;
import com.tfit.BdBiProcSrvShEduOmc.util.UniqueIdGen;

//导出菜品留样详情列表应用模型
public class ExpDishRsDetsAppMod {
	private static final Logger logger = LogManager.getLogger(ExpDishRsDetsAppMod.class.getName());
	
	//菜品留样列表应用模型
	private DishRsDetsAppMod drdAppMod = new DishRsDetsAppMod();
	
	//是否为真实数据标识
	private static boolean isRealData = true;
	//页号、页大小和总页数
	int curPageNum = 1, pageTotal = 1, pageSize = AppModConfig.maxPageSize;
	//报表文件资源路径
	String repFileResPath = "/expDishRsDets/";
	//导出列名数组
	String[] colNames = {"序号","就餐日期","管理部门", "项目点名称","是否留样","留样操作状态", "总校/分校", "分校数量", "所属", "主管部门", "关联总校", "所在地", "学校学制",
			"办学性质", "供餐类型", "团餐公司", "餐别", "菜单名称", "菜品名称", "菜品份数",  "留样数量", "留样上报时间", "留样说明","留样时间",
			"留样单位", "留样人"};
	
	//变量数据初始化
	String repastStartDate = "2018-09-03";
	String repasEndDate = "2018-09-04";
	String distName = null;
	String prefCity = null;
	String province = "上海市";
	String expFileUrl = "fc8bafe943214d65a67a7d8b93d0185a.xls";
	
	//模拟数据函数
	private ExpDishRsDetsDTO SimuDataFunc() {
		ExpDishRsDetsDTO edrdDto = new ExpDishRsDetsDTO();
		//设置返回数据
		edrdDto.setTime(BCDTimeUtil.convertNormalFrom(null));
		//列表元素设置
		ExpDishRsDets expDishRsDets = new ExpDishRsDets();
		//赋值
		expDishRsDets.setRepastStartDate(repastStartDate);
		expDishRsDets.setRepasEndDate(repasEndDate);
		expDishRsDets.setDistName(distName);
		expDishRsDets.setPrefCity(prefCity);
		expDishRsDets.setProvince(province);
		expDishRsDets.setExpFileUrl(SpringConfig.repfile_srvdn+repFileResPath+expFileUrl);
		//设置模拟数据
		edrdDto.setExpDishRsDets(expDishRsDets);
		//消息ID
		edrdDto.setMsgId(AppModConfig.msgId);
		AppModConfig.msgId++;
		//消息id小于0判断
		AppModConfig.msgIdLessThan0Judge();

		return edrdDto;
	}
	
	//生成导出EXCEL文件
	public boolean expDishRsDetsExcel(String pathFileName, List<DishRsDets> dataList,Map<String,DepartmentObj> deparmentMap,List<UserSetColums> userSetColumsList,  String colNames[]) { 
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
				
				if(userSetColumsList !=null && userSetColumsList.size() > 0) {
					for(UserSetColums obj : userSetColumsList) {
					  if(obj != null && obj.isChecked()) {
						if("sortNo".equals(obj.getKey())) 
								row.createCell(startColumnIdx++).setCellValue(i+1);//序号
					    if("repastDate".equals(obj.getKey())) 
					      	row.createCell(startColumnIdx++).setCellValue(dataList.get(i).getRepastDate());                                         //就餐日期	
					    if("departmentName".equals(obj.getKey())) 
					      	row.createCell(startColumnIdx++).setCellValue(deparmentMap.get(dataList.get(i).getDepartmentId())==null?"":deparmentMap.get(dataList.get(i).getDepartmentId()).getDepartmentName()); //管理部门	
					    if("ppName".equals(obj.getKey())) 
					      	row.createCell(startColumnIdx++).setCellValue(dataList.get(i).getPpName());                                             //项目点名称	
					    if("rsFlag".equals(obj.getKey())) 
					      	row.createCell(startColumnIdx++).setCellValue(dataList.get(i).getRsFlag()==1?"已留样":"未留样");                           //留样	
					    if("reserveStatusName".equals(obj.getKey())) 
					      	row.createCell(startColumnIdx++).setCellValue(CommonUtil.isEmpty(dataList.get(i).getReserveStatus())?"无数据":AppModConfig.plaStatusIdToNameMap.get(dataList.get(i).getReserveStatus())); //留样操作状态	
					    if("schGenBraFlag".equals(obj.getKey())) 
					      	row.createCell(startColumnIdx++).setCellValue(dataList.get(i).getSchGenBraFlag());                                      //总校/分校	
					    if("braCampusNum".equals(obj.getKey())) 
					      	row.createCell(startColumnIdx++).setCellValue(dataList.get(i).getBraCampusNum());                                       //分校数量	
					    if("subLevel".equals(obj.getKey())) 
					      	row.createCell(startColumnIdx++).setCellValue(dataList.get(i).getSubLevel());                                           //所属	
					    if("compDep".equals(obj.getKey())) 
					      	row.createCell(startColumnIdx++).setCellValue(dataList.get(i).getCompDep());                                            //主管部门	
					    if("relGenSchName".equals(obj.getKey())) 
					      	row.createCell(startColumnIdx++).setCellValue(dataList.get(i).getRelGenSchName());                                      //关联总校	
					    if("distName".equals(obj.getKey())) 
					      	row.createCell(startColumnIdx++).setCellValue(AppModConfig.distIdToNameMap.get(dataList.get(i).getDistName()));         //所在地	
					    if("schType".equals(obj.getKey())) 
					      	row.createCell(startColumnIdx++).setCellValue(dataList.get(i).getSchType());                                            //学校学制	
					    if("schProp".equals(obj.getKey())) 
					      	row.createCell(startColumnIdx++).setCellValue(dataList.get(i).getSchProp());                                            //办学性质	
					    if("optMode".equals(obj.getKey())) 
					      	row.createCell(startColumnIdx++).setCellValue(dataList.get(i).getOptMode());                                           //供餐类型	
					    if("rmcName".equals(obj.getKey())) 
					      	row.createCell(startColumnIdx++).setCellValue(dataList.get(i).getRmcName());                                           //团餐公司	
					    if("caterType".equals(obj.getKey())) 
					      	row.createCell(startColumnIdx++).setCellValue(dataList.get(i).getCaterType());                                         //餐别	
					    if("menuName".equals(obj.getKey())) 
					      	row.createCell(startColumnIdx++).setCellValue(dataList.get(i).getMenuName());                                          //菜单名称	
					    if("dishName".equals(obj.getKey())) 
					      	row.createCell(startColumnIdx++).setCellValue(dataList.get(i).getDishName());                                          //菜品名称	
					    if("dishNum".equals(obj.getKey())) 
					      	row.createCell(startColumnIdx++).setCellValue(dataList.get(i).getDishNum());                                           //菜品份数	
					    if("rsNum".equals(obj.getKey())) 
					      	row.createCell(startColumnIdx++).setCellValue(dataList.get(i).getRsNum());                                             //留样数量	
					    if("createtime".equals(obj.getKey())) 
					      	row.createCell(startColumnIdx++).setCellValue(dataList.get(i).getCreatetime());                                        //留样操作时间	
					    if("rsExplain".equals(obj.getKey())) 
					      	row.createCell(startColumnIdx++).setCellValue(dataList.get(i).getRsExplain());                                         //留样说明	
					    if("rsTime".equals(obj.getKey())) 
					      	row.createCell(startColumnIdx++).setCellValue(dataList.get(i).getRsTime());                                            //留样时间	
					    if("rsUnit".equals(obj.getKey())) 
					      	row.createCell(startColumnIdx++).setCellValue(dataList.get(i).getRsUnit());                                            //留样单位	
					    if("rsPerson".equals(obj.getKey())) 
					      	row.createCell(startColumnIdx++).setCellValue(dataList.get(i).getRsPerson());                                          //留样人	

					  }
					}
				  }else {
					  row.createCell(startColumnIdx++).setCellValue(i+1);
					row.createCell(startColumnIdx++).setCellValue(dataList.get(i).getRepastDate());                                         //就餐日期
					row.createCell(startColumnIdx++).setCellValue(deparmentMap.get(dataList.get(i).getDepartmentId())==null?"":deparmentMap.get(dataList.get(i).getDepartmentId()).getDepartmentName()); //管理部门
					row.createCell(startColumnIdx++).setCellValue(dataList.get(i).getPpName());                                             //项目点名称	
					row.createCell(startColumnIdx++).setCellValue(dataList.get(i).getRsFlag()==1?"已留样":"未留样");                           //留样
					row.createCell(startColumnIdx++).setCellValue(CommonUtil.isEmpty(dataList.get(i).getReserveStatus())?"无数据":AppModConfig.plaStatusIdToNameMap.get(dataList.get(i).getReserveStatus())); //留样操作状态
					row.createCell(startColumnIdx++).setCellValue(dataList.get(i).getSchGenBraFlag());                                      //总校/分校
					row.createCell(startColumnIdx++).setCellValue(dataList.get(i).getBraCampusNum());                                       //分校数量
					row.createCell(startColumnIdx++).setCellValue(dataList.get(i).getSubLevel());                                           //所属
					row.createCell(startColumnIdx++).setCellValue(dataList.get(i).getCompDep());                                            //主管部门	
					row.createCell(startColumnIdx++).setCellValue(dataList.get(i).getRelGenSchName());                                      //关联总校
					row.createCell(startColumnIdx++).setCellValue(AppModConfig.distIdToNameMap.get(dataList.get(i).getDistName()));         //所在地					
					row.createCell(startColumnIdx++).setCellValue(dataList.get(i).getSchType());                                            //学校学制
					row.createCell(startColumnIdx++).setCellValue(dataList.get(i).getSchProp());                                            //办学性质
					row.createCell(startColumnIdx++).setCellValue(dataList.get(i).getOptMode());                                           //供餐类型
					row.createCell(startColumnIdx++).setCellValue(dataList.get(i).getRmcName());                                           //团餐公司
					row.createCell(startColumnIdx++).setCellValue(dataList.get(i).getCaterType());                                         //餐别
					row.createCell(startColumnIdx++).setCellValue(dataList.get(i).getMenuName());                                          //菜单名称
					row.createCell(startColumnIdx++).setCellValue(dataList.get(i).getDishName());                                          //菜品名称
					row.createCell(startColumnIdx++).setCellValue(dataList.get(i).getDishNum());                                           //菜品份数
					row.createCell(startColumnIdx++).setCellValue(dataList.get(i).getRsNum());                                             //留样数量
					row.createCell(startColumnIdx++).setCellValue(dataList.get(i).getCreatetime());                                        //留样操作时间
					row.createCell(startColumnIdx++).setCellValue(dataList.get(i).getRsExplain());                                         //留样说明
					row.createCell(startColumnIdx++).setCellValue(dataList.get(i).getRsTime());                                            //留样时间
					row.createCell(startColumnIdx++).setCellValue(dataList.get(i).getRsUnit());                                            //留样单位
					row.createCell(startColumnIdx++).setCellValue(dataList.get(i).getRsPerson());                                          //留样人
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
	
	//导出菜品留样列表模型函数
	public ExpDishRsDetsDTO appModFunc(String token, String repastStartDate, String repasEndDate, String distName, String prefCity, 
			String province, String subLevel, String compDep, String schGenBraFlag, String subDistName, 
			String fblMb, String schProp, String ppName, String rmcName, String rsFlag, String caterType, 
			String schType, String menuName, String optMode, String rsUnit, 
			String distNames,String subLevels,String compDeps,String schProps,String caterTypes,
			String schTypes,String menuNames,String optModes,
			String departmentId,String departmentIds,String reserveStatus,
			Db1Service db1Service, Db2Service db2Service, SaasService saasService) {
		ExpDishRsDetsDTO edrdDto = null;
		if (isRealData) { // 真实数据
			String strCurPageNum = String.valueOf(curPageNum), strPageSize = String.valueOf(pageSize);
			if (repastStartDate == null || repasEndDate == null) {   // 按照当天日期获取数据
				repastStartDate = BCDTimeUtil.convertNormalDate(null);
				repasEndDate = repastStartDate;
			}
			DishRsDetsDTO drdDto = drdAppMod.appModFunc(token, repastStartDate, repasEndDate, distName, 
					prefCity, province, subLevel, compDep, schGenBraFlag, subDistName, fblMb, schProp, 
					ppName, rmcName, rsFlag, caterType, schType, menuName, optMode, rsUnit, 
					distNames,subLevels,compDeps,schProps,caterTypes,schTypes,menuNames,optModes,
					departmentId,departmentIds,reserveStatus,null,
					strCurPageNum, strPageSize, db1Service, db2Service, saasService);
			if(drdDto != null) {
				int i, totalCount = drdDto.getPageInfo().getPageTotal();
				int pageCount = 0;
				List<DishRsDets> expExcelList = new ArrayList<>();
				if(totalCount % pageSize == 0)
					pageCount = totalCount/pageSize;
				else
					pageCount = totalCount/pageSize + 1;
				//第一页数据
				if(drdDto.getDishRsDets() != null) {
					expExcelList.addAll(drdDto.getDishRsDets());
				}
				//后续页数据
				for(i = curPageNum+1; i <= pageCount; i++) {
					strCurPageNum = String.valueOf(i);
					DishRsDetsDTO curPdlDto = drdAppMod.appModFunc(token, repastStartDate, repasEndDate, distName, 
							prefCity, province, subLevel, compDep, schGenBraFlag, subDistName, fblMb, schProp, ppName, 
							rmcName, rsFlag, caterType, schType, menuName, optMode, rsUnit, 
							distNames,subLevels,compDeps,schProps,caterTypes,schTypes,menuNames,optModes,
							departmentId,departmentIds,reserveStatus,null,
							strCurPageNum, strPageSize, db1Service, db2Service, saasService);
					if(curPdlDto.getDishRsDets() != null) {
						expExcelList.addAll(curPdlDto.getDishRsDets());
					}
				}
				//生成导出EXCEL文件
				String repFileName = repFileResPath + UniqueIdGen.uuid() + SpringConfig.repFileFormats[SpringConfig.curRepFileFrmtIdx];
				String pathFileName = SpringConfig.base_dir + repFileName;
				logger.info("导出文件路径：" + pathFileName);
				List<DepartmentObj> deparmentList =  db1Service.getDepartmentObjList(new DepartmentObj(),null, -1, -1);	
				Map<String,DepartmentObj> deparmentMap = deparmentList.stream().collect(Collectors.toMap(DepartmentObj::getDepartmentId,(b)->b));
				List<UserSetColums> userSetColumsList =CommonUtil.getUserSetColumList(token,"dishRsDets", db2Service);
				boolean flag = expDishRsDetsExcel(pathFileName, expExcelList,deparmentMap,userSetColumsList,  colNames);
				if(flag) {
					//移动文件到其他目录
					//AppModConfig.moveFileToOtherFolder(pathFileName, SpringConfig.tomcatSrvDirs[1] + repFileResPath);
					edrdDto = new ExpDishRsDetsDTO();
					ExpDishRsDets expDishRsDets = new ExpDishRsDets();
					//时戳
					edrdDto.setTime(BCDTimeUtil.convertNormalFrom(null));
					//导出信息
					expDishRsDets.setRepastStartDate(repastStartDate);
					expDishRsDets.setRepasEndDate(repasEndDate);
					expDishRsDets.setDistName(distName);
					expDishRsDets.setPrefCity(prefCity);
					expDishRsDets.setProvince(province);
					expFileUrl = SpringConfig.repfile_srvdn + repFileName;
					logger.info("导出文件URL：" + expFileUrl);
					expDishRsDets.setExpFileUrl(expFileUrl);
					edrdDto.setExpDishRsDets(expDishRsDets);
					//消息ID
					edrdDto.setMsgId(AppModConfig.msgId);
					AppModConfig.msgId++;
					// 消息id小于0判断
					AppModConfig.msgIdLessThan0Judge();
				}
			}
		} else { // 模拟数据
			// 模拟数据函数
			edrdDto = SimuDataFunc();
		}

		return edrdDto;
	}
}