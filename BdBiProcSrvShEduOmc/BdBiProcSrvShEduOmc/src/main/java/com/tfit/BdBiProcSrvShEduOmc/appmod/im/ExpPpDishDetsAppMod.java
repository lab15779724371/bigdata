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
import com.tfit.BdBiProcSrvShEduOmc.dto.im.ExpPpDishDets;
import com.tfit.BdBiProcSrvShEduOmc.dto.im.ExpPpDishDetsDTO;
import com.tfit.BdBiProcSrvShEduOmc.dto.im.PpDishDets;
import com.tfit.BdBiProcSrvShEduOmc.dto.im.PpDishDetsDTO;
import com.tfit.BdBiProcSrvShEduOmc.dto.user.UserSetColums;
import com.tfit.BdBiProcSrvShEduOmc.model.uo.TableUO;
import com.tfit.BdBiProcSrvShEduOmc.obj.base.DepartmentObj;
import com.tfit.BdBiProcSrvShEduOmc.service.Db1Service;
import com.tfit.BdBiProcSrvShEduOmc.service.Db2Service;
import com.tfit.BdBiProcSrvShEduOmc.service.DbHiveDishService;
import com.tfit.BdBiProcSrvShEduOmc.service.SaasService;
import com.tfit.BdBiProcSrvShEduOmc.util.BCDTimeUtil;
import com.tfit.BdBiProcSrvShEduOmc.util.CommonUtil;
import com.tfit.BdBiProcSrvShEduOmc.util.UniqueIdGen;

//导出项目点排菜详情列表应用模型
public class ExpPpDishDetsAppMod {
	private static final Logger logger = LogManager.getLogger(ExpPpDishDetsAppMod.class.getName());
	
	//项目点排菜详情列表应用模型
	private PpDishDetsAppMod epddAppMod = new PpDishDetsAppMod();
	
	//是否为真实数据标识
	private static boolean isRealData = true;
	//页号、页大小和总页数
	int curPageNum = 1, pageTotal = 1, pageSize = AppModConfig.maxPageSize;
	//报表文件资源路径
	String repFileResPath = "/expPpDishDets/";
	//导出列名数组
	/*String[] colNames = {"序号","排菜日期", "项目点名称","地址","项目联系人","手机", "总校/分校", "分校数量", "关联总校", "所属", "主管部门", "所属区", 
			 "所在地", "学校学制", "办学性质", "是否供餐", "供餐模式", "团餐公司", "是否排菜","排菜操作日期"};	*/
	
	String[] colNames = {"序号","排菜日期","管理部门","所在地", "学校学制", "办学性质","项目点名称","地址","项目联系人","手机","是否供餐","不供餐原因",
			"是否排菜","所属", "主管部门", "所属区", "排菜上报日期","操作状态","总校/分校", "分校数量", "关联总校",  
			"食品经营许可证主体", "供餐模式", "团餐公司"};	
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
	private ExpPpDishDetsDTO SimuDataFunc() {
		ExpPpDishDetsDTO epddDto = new ExpPpDishDetsDTO();
		//设置返回数据
		epddDto.setTime(BCDTimeUtil.convertNormalFrom(null));
		//列表元素设置
		ExpPpDishDets expPpDishDets = new ExpPpDishDets();
		//赋值
		expPpDishDets.setStartDate(startDate);
		expPpDishDets.setEndDate(endDate);
		expPpDishDets.setPpName(ppName);
		expPpDishDets.setDistName(distName);
		expPpDishDets.setPrefCity(prefCity);
		expPpDishDets.setProvince(province);
		expPpDishDets.setDishFlag(dishFlag);
		expPpDishDets.setRmcName(rmcName);
		expPpDishDets.setSchType(schType);
		expPpDishDets.setMealFlag(mealFlag);
		expPpDishDets.setOptMode(optMode);
		expPpDishDets.setSendFlag(sendFlag);
		expPpDishDets.setExpFileUrl(SpringConfig.repfile_srvdn+repFileResPath+expFileUrl);
		//设置模拟数据
		epddDto.setExpPpDishDets(expPpDishDets);
		//消息ID
		epddDto.setMsgId(AppModConfig.msgId);
		AppModConfig.msgId++;
		//消息id小于0判断
		AppModConfig.msgIdLessThan0Judge();

		return epddDto;
	}
	
	//生成导出EXCEL文件
	public boolean expPpDishDetsExcel(String pathFileName, List<PpDishDets> result,Map<String,DepartmentObj> deparmentMap,List<UserSetColums> userSetColumsList,  String colNames[]) { 
		boolean retFlag = true;
		Workbook wb = null;
        String excelPath = pathFileName, fileType = "";
        File file = new File(excelPath);
        Sheet sheet = null;
        int idx1 = excelPath.lastIndexOf(".xls"), idx2 = excelPath.lastIndexOf(".xlsx");
        if(result.size() > AppModConfig.maxPageSize)
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
				
				/*String[] colNames = {"序号","排菜日期", "项目点名称","地址","项目联系人","手机", "总校/分校", "分校数量", "关联总校", "所属", "主管部门", "所属区", 
				 "所在地", "学校学制", "办学性质", "是否供餐", "供餐模式", "团餐公司", "是否排菜","排菜操作日期"};	
		
		String[] colNames = {"序号","排菜日期","管理部门","所在地", "学校学制", "办学性质","项目点名称","地址","项目联系人","手机","是否供餐","不供餐原因",
				"是否排菜","所属", "主管部门", "所属区", "排菜操作日期","操作状态","总校/分校", "分校数量", "关联总校",  
				"供餐模式", "团餐公司"};	*/
				if(userSetColumsList !=null && userSetColumsList.size() > 0) {
					for(UserSetColums obj : userSetColumsList) {
					  if(obj != null && obj.isChecked()) {
						  if("sortNo".equals(obj.getKey())) 
								row.createCell(startColumnIdx++).setCellValue(i+1);//序号
					    if("dishDate".equals(obj.getKey())) 
					      	row.createCell(startColumnIdx++).setCellValue(result.get(i).getDishDate());                                            //排菜日期	
					    if("departmentId".equals(obj.getKey())) 
					      	row.createCell(startColumnIdx++).setCellValue(deparmentMap.get(result.get(i).getDepartmentId())==null?"":deparmentMap.get(result.get(i).getDepartmentId()).getDepartmentName()); //管理部门	
					    if("distName".equals(obj.getKey())) 
					      	row.createCell(startColumnIdx++).setCellValue(AppModConfig.distIdToNameMap.get(result.get(i).getDistName()));          //所在地	
					    if("schType".equals(obj.getKey())) 
					      	row.createCell(startColumnIdx++).setCellValue(result.get(i).getSchType());                                            //学校学制	
					    if("schProp".equals(obj.getKey())) 
					      	row.createCell(startColumnIdx++).setCellValue(result.get(i).getSchProp());                                            //办学性质	
					    if("ppName".equals(obj.getKey())) 
					      	row.createCell(startColumnIdx++).setCellValue(result.get(i).getPpName());                                              //项目点名称	
					    if("detailAddr".equals(obj.getKey())) 
					      	row.createCell(startColumnIdx++).setCellValue(result.get(i).getDetailAddr());                                          //地址	
					    if("projContact".equals(obj.getKey())) 
					      	row.createCell(startColumnIdx++).setCellValue(result.get(i).getProjContact());                                         //项目联系人	
					    if("pcMobilePhone".equals(obj.getKey())) 
					      	row.createCell(startColumnIdx++).setCellValue(result.get(i).getPcMobilePhone());                                       //手机	
					    if("mealFlag".equals(obj.getKey())) 
					      	row.createCell(startColumnIdx++).setCellValue(AppModConfig.isMealIdToNameMap.get(result.get(i).getMealFlag()));       //是否供餐	
					    if("reason".equals(obj.getKey())) 
					      	row.createCell(startColumnIdx++).setCellValue(result.get(i).getReason());       //不供餐原因	
					    if("dishFlag".equals(obj.getKey())) 
					      	row.createCell(startColumnIdx++).setCellValue(AppModConfig.isDishIdToNameMap.get(result.get(i).getDishFlag()));       //是否排菜	
					    if("subLevel".equals(obj.getKey())) 
					      	row.createCell(startColumnIdx++).setCellValue(result.get(i).getSubLevel());                                            //所属	
					    if("compDep".equals(obj.getKey())) 
					      	row.createCell(startColumnIdx++).setCellValue(result.get(i).getCompDep());                                             //主管部门	
					    if("subDistName".equals(obj.getKey())) 
					      	row.createCell(startColumnIdx++).setCellValue(result.get(i).getSubDistName());                                         //所属区	
					    if("createtime".equals(obj.getKey())) 
					      	row.createCell(startColumnIdx++).setCellValue(result.get(i).getCreatetime());       //排菜操作日期	
					    if("plaStatus".equals(obj.getKey())) 
					      	row.createCell(startColumnIdx++).setCellValue(CommonUtil.isEmpty(result.get(i).getPlaStatus())?"-":AppModConfig.plaStatusIdToNameMap.get(result.get(i).getPlaStatus()));//操作作状态	
					    if("schGenBraFlag".equals(obj.getKey())) 
					      	row.createCell(startColumnIdx++).setCellValue(result.get(i).getSchGenBraFlag());                                       //总校/分校	
					    if("braCampusNum".equals(obj.getKey())) 
					      	row.createCell(startColumnIdx++).setCellValue(result.get(i).getBraCampusNum());                                        //分校数量	
					    if("relGenSchName".equals(obj.getKey())) 
					      	row.createCell(startColumnIdx++).setCellValue(result.get(i).getRelGenSchName());                                       //关联总校	
					    if("fblMb".equals(obj.getKey())) 
					      	row.createCell(startColumnIdx++).setCellValue(result.get(i).getFblMb());                                               //食品经营许可证主体	
					    if("optMode".equals(obj.getKey())) 
					      	row.createCell(startColumnIdx++).setCellValue(result.get(i).getOptMode());                                            //供餐模式	
					    if("rmcName".equals(obj.getKey())) 
					      	row.createCell(startColumnIdx++).setCellValue(result.get(i).getRmcName());                                            //团餐公司	
					  }
					}
				  }else {
					  row.createCell(startColumnIdx++).setCellValue(i+1);
					row.createCell(startColumnIdx++).setCellValue(result.get(i).getDishDate());                                            //排菜日期
					row.createCell(startColumnIdx++).setCellValue(deparmentMap.get(result.get(i).getDepartmentId())==null?"":deparmentMap.get(result.get(i).getDepartmentId()).getDepartmentName()); //管理部门
					row.createCell(startColumnIdx++).setCellValue(AppModConfig.distIdToNameMap.get(result.get(i).getDistName()));          //所在地
					row.createCell(startColumnIdx++).setCellValue(result.get(i).getSchType());                                            //学校学制
					row.createCell(startColumnIdx++).setCellValue(result.get(i).getSchProp());                                            //办学性质
					row.createCell(startColumnIdx++).setCellValue(result.get(i).getPpName());                                              //项目点名称
					row.createCell(startColumnIdx++).setCellValue(result.get(i).getDetailAddr());                                          //地址
					row.createCell(startColumnIdx++).setCellValue(result.get(i).getProjContact());                                         //项目联系人
					row.createCell(startColumnIdx++).setCellValue(result.get(i).getPcMobilePhone());                                       //手机
					row.createCell(startColumnIdx++).setCellValue(AppModConfig.isMealIdToNameMap.get(result.get(i).getMealFlag()));       //是否供餐
					row.createCell(startColumnIdx++).setCellValue(result.get(i).getReason());       //不供餐原因
					row.createCell(startColumnIdx++).setCellValue(AppModConfig.isDishIdToNameMap.get(result.get(i).getDishFlag()));       //是否排菜
					row.createCell(startColumnIdx++).setCellValue(result.get(i).getSubLevel());                                            //所属
					row.createCell(startColumnIdx++).setCellValue(result.get(i).getCompDep());                                             //主管部门
					row.createCell(startColumnIdx++).setCellValue(result.get(i).getSubDistName());                                         //所属区
					row.createCell(startColumnIdx++).setCellValue(result.get(i).getCreatetime());       //排菜操作日期
					row.createCell(startColumnIdx++).setCellValue(CommonUtil.isEmpty(result.get(i).getPlaStatus())?"-":AppModConfig.plaStatusIdToNameMap.get(result.get(i).getPlaStatus()));//操作作状态
					row.createCell(startColumnIdx++).setCellValue(result.get(i).getSchGenBraFlag());                                       //总校/分校
					row.createCell(startColumnIdx++).setCellValue(result.get(i).getBraCampusNum());                                        //分校数量
					row.createCell(startColumnIdx++).setCellValue(result.get(i).getRelGenSchName());                                       //关联总校
					row.createCell(startColumnIdx++).setCellValue(result.get(i).getFblMb());                                               //食品经营许可证主体
					row.createCell(startColumnIdx++).setCellValue(result.get(i).getOptMode());                                            //供餐模式
					row.createCell(startColumnIdx++).setCellValue(result.get(i).getRmcName());                                            //团餐公司
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
	
    private TableUO genTableUO(List<PpDishDets> result, String[] colNames) {
        int columnCount = colNames.length;
        //行数
        int rowCount = result.size();
        //表格信息
        TableUO tableUO = new TableUO(rowCount, columnCount);
        //设置列名
        tableUO.setColumnNames(colNames);
        //生成对象数据
        Object[][] data = new Object[rowCount][columnCount];
        for (int i = 0; i < result.size(); i++) {            
            data[i][0]  = result.get(i).getDishDate();                                           //排菜日期
            data[i][1]  = result.get(i).getPpName();                                             //项目点名称
            data[i][2]  = result.get(i).getSchGenBraFlag();                                      //总校/分校
            data[i][3]  = result.get(i).getBraCampusNum();                                       //分校数量
            data[i][4]  = result.get(i).getRelGenSchName();                                      //关联总校
            data[i][5]  = result.get(i).getSubLevel();                                           //所属
            data[i][6]  = result.get(i).getCompDep();                                            //主管部门
            data[i][7]  = result.get(i).getSubDistName();                                        //所属区
            data[i][8]  = result.get(i).getFblMb();                                              //食品经营许可证主体
            data[i][9]  = AppModConfig.distIdToNameMap.get(result.get(i).getDistName());         //所在地
            data[i][10] = result.get(i).getSchType();                                            //学校学制
            data[i][11] = result.get(i).getSchProp();                                            //办学性质
            data[i][12] = AppModConfig.isMealIdToNameMap.get(result.get(i).getMealFlag());       //是否供餐
            data[i][13] = result.get(i).getOptMode();                                            //供餐模式
            data[i][14] = result.get(i).getRmcName();                                            //团餐公司
            data[i][15] = AppModConfig.isDishIdToNameMap.get(result.get(i).getDishFlag());       //是否排菜
        }
        //设置数据举证
        tableUO.setDataMatrix(data);

        return tableUO;
    }
	
	//导出项目点排菜详情列表模型函数
	public ExpPpDishDetsDTO appModFunc(String token, String startDate, String endDate, String ppName, String distName, 
			String prefCity, String province, String subLevel, String compDep, String schGenBraFlag, String subDistName, 
			String fblMb, String schProp, String dishFlag, String rmcName, String schType, String mealFlag, String optMode, 
			String sendFlag, 
			String distNames,String subLevels,String compDeps,String schProps,String schTypes,
			String optModes,String subDistNames,String departmentId,String departmentIds,String plastatus,String reason,
			Db1Service db1Service, Db2Service db2Service, SaasService saasService,
			DbHiveDishService dbHiveDishService) {
		ExpPpDishDetsDTO epddDto = null;
		if (isRealData) { // 真实数据
			List<UserSetColums> userSetColumsList =CommonUtil.getUserSetColumList(token,"ppDishDets", db2Service);
			String strCurPageNum = String.valueOf(curPageNum), strPageSize = String.valueOf(pageSize);
			if (startDate == null || endDate == null) {   // 按照当天日期获取数据
				startDate = BCDTimeUtil.convertNormalDate(null);
				endDate = startDate;
			}
			PpDishDetsDTO pddDto = epddAppMod.appModFunc(token, startDate, endDate, ppName, 
					distName, prefCity, province, subLevel, compDep, schGenBraFlag, subDistName, 
					fblMb, schProp, dishFlag, rmcName, schType, mealFlag, optMode, sendFlag, 
					distNames,subLevels,compDeps,schProps,schTypes,optModes,subDistNames,
					departmentId,departmentIds,plastatus,reason,null,
					strCurPageNum, strPageSize, db1Service, db2Service, saasService,dbHiveDishService);
			if(pddDto != null) {
				int i, totalCount = pddDto.getPageInfo().getPageTotal();
				int pageCount = 0;
				List<PpDishDets> expExcelList = new ArrayList<>();
				if(totalCount % pageSize == 0)
					pageCount = totalCount/pageSize;
				else
					pageCount = totalCount/pageSize + 1;
				//第一页数据
				if(pddDto.getPpDishDets() != null) {
					expExcelList.addAll(pddDto.getPpDishDets());			
				}
				//后续页数据
				for(i = curPageNum+1; i <= pageCount; i++) {
					strCurPageNum = String.valueOf(i);
					PpDishDetsDTO curPdlDto = epddAppMod.appModFunc(token, startDate, endDate, ppName, 
							distName, prefCity, province, subLevel, compDep, schGenBraFlag, subDistName, 
							fblMb, schProp, dishFlag, rmcName, schType, mealFlag, optMode, sendFlag, 
							distNames,subLevels,compDeps,schProps,schTypes,optModes,subDistNames,
							departmentId,departmentIds,plastatus,reason,null,
							strCurPageNum, strPageSize, db1Service, db2Service, saasService,dbHiveDishService);
					if(curPdlDto.getPpDishDets() != null) {
						expExcelList.addAll(curPdlDto.getPpDishDets());
					}
				}
				//生成导出EXCEL文件
				String repFileName = repFileResPath + UniqueIdGen.uuid() + SpringConfig.repFileFormats[SpringConfig.curRepFileFrmtIdx];
				String pathFileName = SpringConfig.base_dir + repFileName;
				logger.info("导出文件路径：" + pathFileName);
				
				List<DepartmentObj> deparmentList =  db1Service.getDepartmentObjList(new DepartmentObj(),null, -1, -1);	
				Map<String,DepartmentObj> deparmentMap = deparmentList.stream().collect(Collectors.toMap(DepartmentObj::getDepartmentId,(b)->b));
				boolean flag = expPpDishDetsExcel(pathFileName, expExcelList,deparmentMap,userSetColumsList,  colNames);
				if(flag) {
					//移动文件到其他目录
					//AppModConfig.moveFileToOtherFolder(pathFileName, SpringConfig.tomcatSrvDirs[1] + repFileResPath);
					epddDto = new ExpPpDishDetsDTO();
					ExpPpDishDets expPpDishDets = new ExpPpDishDets();
					//时戳
					epddDto.setTime(BCDTimeUtil.convertNormalFrom(null));
					//导出信息
					expPpDishDets.setStartDate(startDate);
					expPpDishDets.setEndDate(endDate);
					expPpDishDets.setPpName(ppName);
					expPpDishDets.setDistName(AppModConfig.distIdToNameMap.get(distName));
					expPpDishDets.setPrefCity(prefCity);
					expPpDishDets.setProvince(province);
					if(dishFlag != null)
						expPpDishDets.setDishFlag(Integer.parseInt(dishFlag));
					else
						expPpDishDets.setDishFlag(null);
					expPpDishDets.setRmcName(rmcName);
					if(schType != null)
						expPpDishDets.setSchType(Integer.parseInt(schType));
					else
						expPpDishDets.setSchType(null);
					if(mealFlag != null)
						expPpDishDets.setMealFlag(Integer.parseInt(mealFlag));
					else
						expPpDishDets.setMealFlag(null);
					if(optMode != null)
						expPpDishDets.setOptMode(Integer.parseInt(optMode));
					else
						expPpDishDets.setOptMode(null);
					if(sendFlag != null)
						expPpDishDets.setSendFlag(Integer.parseInt(sendFlag));
					else
						expPpDishDets.setSendFlag(null);
					expFileUrl = SpringConfig.repfile_srvdn + repFileName;
					logger.info("导出文件URL：" + expFileUrl);
					expPpDishDets.setExpFileUrl(expFileUrl);
					epddDto.setExpPpDishDets(expPpDishDets);
					//消息ID
					epddDto.setMsgId(AppModConfig.msgId);
					AppModConfig.msgId++;
					// 消息id小于0判断
					AppModConfig.msgIdLessThan0Judge();
				}
			}
		} else { // 模拟数据
			// 模拟数据函数
			epddDto = SimuDataFunc();
		}

		return epddDto;
	}
}
