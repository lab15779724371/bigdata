package com.tfit.BdBiProcSrvShEduOmc.appmod.iw;

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
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.tfit.BdBiProcSrvShEduOmc.config.AppModConfig;
import com.tfit.BdBiProcSrvShEduOmc.config.SpringConfig;
import com.tfit.BdBiProcSrvShEduOmc.dto.iw.ExpWarnAllLicDets;
import com.tfit.BdBiProcSrvShEduOmc.dto.iw.ExpWarnMaterialOverProDetsDTO;
import com.tfit.BdBiProcSrvShEduOmc.dto.iw.WarnMaterialOverProDets;
import com.tfit.BdBiProcSrvShEduOmc.dto.iw.WarnMaterialOverProDetsDTO;
import com.tfit.BdBiProcSrvShEduOmc.obj.base.DepartmentObj;
import com.tfit.BdBiProcSrvShEduOmc.service.Db1Service;
import com.tfit.BdBiProcSrvShEduOmc.service.Db2Service;
import com.tfit.BdBiProcSrvShEduOmc.service.DbHiveWarnService;
import com.tfit.BdBiProcSrvShEduOmc.service.SaasService;
import com.tfit.BdBiProcSrvShEduOmc.util.BCDTimeUtil;
import com.tfit.BdBiProcSrvShEduOmc.util.CommonUtil;
import com.tfit.BdBiProcSrvShEduOmc.util.FtpUtil;
import com.tfit.BdBiProcSrvShEduOmc.util.UniqueIdGen;

//导出证照预警全部证件详情列表应用模型
public class ExpWarnMaterialOverProDetsAppMod {
	private static final Logger logger = LogManager.getLogger(ExpWarnMaterialOverProDetsAppMod.class.getName());
	
	//证照预警全部证件详情列表应用模型
	private WarnMaterialOverProDetsAppMod waldAppMod = new WarnMaterialOverProDetsAppMod();
	
	//是否为真实数据标识
	private static boolean isRealData = true;
	//页号、页大小和总页数
	int curPageNum = 1, pageTotal = 1, pageSize = AppModConfig.maxPageSize;
	//报表文件资源路径
	String repFileResPath = "/expWarnMaterialOverProDets/";
	//导出列名数组
	//String[] colNames = {"序号","预警日期", "区", "学校名称", "触发预警单位", "证件名称", "证件号码", "有效日期", "证件状况", "状态", "消除日期"};	
	String[] colNames = {"序号","预警日期", "区", "学校名称","管理部门", "触发预警单位","配送批次号","过保物料","生产日期","保质期至","车辆","司机信息","配送日期","状态", "消除日期"};
	
	//变量数据初始化
	String startWarnDate = "2018-11-05";
	String endWarnDate = "2018-11-05";
	String distName = null;
	String prefCity = null;
	String province = "上海市";
	String schName = null;
	String trigWarnUnit = null;
	String licType = null;
	String licAuditStatus = null;
	String startElimDate = null;
	String endElimDate = null;
	String startValidDate = null;
	String endValidDate = null;
	String licNo = null;
	String expFileUrl = "test1.txt";

	//模拟数据函数
	private ExpWarnMaterialOverProDetsDTO SimuDataFunc() {
		ExpWarnMaterialOverProDetsDTO ewaldDto = new ExpWarnMaterialOverProDetsDTO();
		//设置返回数据
		ewaldDto.setTime(BCDTimeUtil.convertNormalFrom(null));
		//列表元素设置
		ExpWarnAllLicDets expWarnMaterialOverProDets = new ExpWarnAllLicDets();
		//赋值
		expWarnMaterialOverProDets.setStartWarnDate(startWarnDate);
		expWarnMaterialOverProDets.setEndWarnDate(endWarnDate);
		expWarnMaterialOverProDets.setDistName(distName);
		expWarnMaterialOverProDets.setPrefCity(prefCity);
		expWarnMaterialOverProDets.setProvince(province);
		expWarnMaterialOverProDets.setSchName(schName);
		expWarnMaterialOverProDets.setTrigWarnUnit(trigWarnUnit);
		expWarnMaterialOverProDets.setLicType(licType);
		expWarnMaterialOverProDets.setLicAuditStatus(licAuditStatus);
		expWarnMaterialOverProDets.setStartElimDate(startElimDate);
		expWarnMaterialOverProDets.setEndElimDate(endElimDate);
		expWarnMaterialOverProDets.setStartValidDate(startValidDate);
		expWarnMaterialOverProDets.setEndValidDate(endValidDate);
		expWarnMaterialOverProDets.setLicNo(licNo);
		expWarnMaterialOverProDets.setExpFileUrl(SpringConfig.repfile_srvdn+repFileResPath+expFileUrl);
		//设置模拟数据
		ewaldDto.setExpWarnMaterialOverProDets(expWarnMaterialOverProDets);
		//消息ID
		ewaldDto.setMsgId(AppModConfig.msgId);
		AppModConfig.msgId++;
		//消息id小于0判断
		AppModConfig.msgIdLessThan0Judge();

		return ewaldDto;
	}
	
	//生成导出EXCEL文件
	public boolean expWarnMaterialOverProDetsExcel(String pathFileName, List<WarnMaterialOverProDets> dataList, String colNames[],Db1Service db1Service) { 
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
			
			List<DepartmentObj> deparmentList =  db1Service.getDepartmentObjList(new DepartmentObj(),null, -1, -1);	
			 Map<String,DepartmentObj> departmentMap = deparmentList.stream().collect(Collectors.toMap(DepartmentObj::getDepartmentId,(b)->b));
			// 循环写入行数据
			startRowIdx++;
			int columIdx=0;
			for (int i = 0; i < dataList.size(); i++) {
				columIdx =0;
				//String[] colNames = {"序号","预警日期", "区", "学校名称","管理部门", "触发预警单位","配送批次号","过保物料","生产日期","保质期至","车辆","司机信息","配送日期","状态", "消除日期"};
				row = (Row) sheet.createRow(i + startRowIdx);
				row.createCell(columIdx++).setCellValue(i+1);
				row.createCell(columIdx++).setCellValue(dataList.get(i).getWarnDate());                                           //预警日期
				row.createCell(columIdx++).setCellValue(AppModConfig.distIdToNameMap.get(dataList.get(i).getDistName()));         //区
				row.createCell(columIdx++).setCellValue(dataList.get(i).getSchName());                                            //学校名称
				row.createCell(columIdx++).setCellValue(departmentMap.get(dataList.get(i).getDepartmentId())==null?"":departmentMap.get(dataList.get(i).getDepartmentId()).getDepartmentName());                                            //管理部门
				row.createCell(columIdx++).setCellValue(dataList.get(i).getTrigWarnUnit());                                       //触发预警单位
				row.createCell(columIdx++).setCellValue(dataList.get(i).getBatchNo());//配送批次号
				row.createCell(columIdx++).setCellValue(dataList.get(i).getMaterialName());//过保物料
				row.createCell(columIdx++).setCellValue(dataList.get(i).getProductionDate());//生产日期
				row.createCell(columIdx++).setCellValue(dataList.get(i).getExpirationDate());//保质期至
				row.createCell(columIdx++).setCellValue(dataList.get(i).getCarCode());//车辆
				row.createCell(columIdx++).setCellValue(dataList.get(i).getDriverName());//司机信息
				row.createCell(columIdx++).setCellValue(dataList.get(i).getBatchDate());                                          //配送日期
				row.createCell(columIdx++).setCellValue(CommonUtil.isEmpty(dataList.get(i).getStatus())?"预警中":("1".equals(dataList.get(i).getStatus())?"已消除":"预警中"));//状态
				row.createCell(columIdx++).setCellValue(dataList.get(i).getElimDate());                                           //消除日期
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
	
	//导出证照预警全部证件详情列表模型函数
	public ExpWarnMaterialOverProDetsDTO appModFunc(String token, String startWarnDate, String endWarnDate, String distName, String prefCity,
			String province, String schName, String trigWarnUnit,String status,String departmentId,String departmentIds,
			Db1Service db1Service, Db2Service db2Service, SaasService saasService,DbHiveWarnService dbHiveWarnService) {
		ExpWarnMaterialOverProDetsDTO ewaldDto = null;
		if (isRealData) { // 真实数据
			if (startWarnDate == null || endWarnDate == null) {   // 按照当天日期获取数据
				startWarnDate = BCDTimeUtil.convertNormalDate(null);
				endWarnDate = startWarnDate;
			}
			WarnMaterialOverProDetsDTO waldDto = waldAppMod.appModFunc(token, startWarnDate, endWarnDate, 
					distName, prefCity, province, departmentId, departmentIds, schName, status, trigWarnUnit, 
					"-1", "-1", db1Service, db2Service, saasService, dbHiveWarnService);
			if(waldDto != null) {
				List<WarnMaterialOverProDets> expExcelList = new ArrayList<>();
				if(waldDto.getWarnMaterialOverProDets() != null) {
					expExcelList.addAll(waldDto.getWarnMaterialOverProDets());
				}
				//}
				//生成导出EXCEL文件
				String repFileName = repFileResPath + UniqueIdGen.uuid() + SpringConfig.repFileFormats[SpringConfig.curRepFileFrmtIdx];
				String pathFileName = SpringConfig.base_dir + repFileName;
				logger.info("导出文件路径：" + pathFileName);
				boolean flag = expWarnMaterialOverProDetsExcel(pathFileName, expExcelList, colNames,db1Service);
				if(flag) {
					//移动文件到其他目录
					AppModConfig.moveFileToOtherFolder(pathFileName, SpringConfig.tomcatSrvDirs[1] + repFileResPath);
					ewaldDto = new ExpWarnMaterialOverProDetsDTO();
					ExpWarnAllLicDets expWarnMaterialOverProDets = new ExpWarnAllLicDets();
					//时戳
					ewaldDto.setTime(BCDTimeUtil.convertNormalFrom(null));
					//导出信息
					expWarnMaterialOverProDets.setStartWarnDate(startWarnDate);
					expWarnMaterialOverProDets.setEndWarnDate(endWarnDate);
					expWarnMaterialOverProDets.setDistName(distName);
					expWarnMaterialOverProDets.setPrefCity(prefCity);
					expWarnMaterialOverProDets.setProvince(province);
					expWarnMaterialOverProDets.setSchName(schName);
					expWarnMaterialOverProDets.setTrigWarnUnit(trigWarnUnit);
					expWarnMaterialOverProDets.setLicType(licType);
					expWarnMaterialOverProDets.setLicAuditStatus(licAuditStatus);
					expWarnMaterialOverProDets.setStartElimDate(startElimDate);
					expWarnMaterialOverProDets.setEndElimDate(endElimDate);
					expWarnMaterialOverProDets.setStartValidDate(startValidDate);
					expWarnMaterialOverProDets.setEndValidDate(endValidDate);
					expWarnMaterialOverProDets.setLicNo(licNo);
					expFileUrl = SpringConfig.repfile_srvdn + repFileName;
					logger.info("导出文件URL：" + expFileUrl);
					expWarnMaterialOverProDets.setExpFileUrl(expFileUrl);
					ewaldDto.setExpWarnMaterialOverProDets(expWarnMaterialOverProDets);
					//消息ID
					ewaldDto.setMsgId(AppModConfig.msgId);
					AppModConfig.msgId++;
					// 消息id小于0判断
					AppModConfig.msgIdLessThan0Judge();
				}
			}
		} else { // 模拟数据
			// 模拟数据函数
			ewaldDto = SimuDataFunc();
		}

		return ewaldDto;
	}
}
