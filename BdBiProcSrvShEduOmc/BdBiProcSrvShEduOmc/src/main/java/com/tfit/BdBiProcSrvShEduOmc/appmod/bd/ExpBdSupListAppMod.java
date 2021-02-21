package com.tfit.BdBiProcSrvShEduOmc.appmod.bd;

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

import com.tfit.BdBiProcSrvShEduOmc.config.AppModConfig;
import com.tfit.BdBiProcSrvShEduOmc.config.SpringConfig;
import com.tfit.BdBiProcSrvShEduOmc.dto.bd.BdSupList;
import com.tfit.BdBiProcSrvShEduOmc.dto.bd.BdSupListDTO;
import com.tfit.BdBiProcSrvShEduOmc.dto.bd.ExpBdSupList;
import com.tfit.BdBiProcSrvShEduOmc.dto.bd.ExpBdSupListDTO;
import com.tfit.BdBiProcSrvShEduOmc.dto.user.UserSetColums;
import com.tfit.BdBiProcSrvShEduOmc.service.Db1Service;
import com.tfit.BdBiProcSrvShEduOmc.service.Db2Service;
import com.tfit.BdBiProcSrvShEduOmc.service.SaasService;
import com.tfit.BdBiProcSrvShEduOmc.util.BCDTimeUtil;
import com.tfit.BdBiProcSrvShEduOmc.util.CommonUtil;
import com.tfit.BdBiProcSrvShEduOmc.util.UniqueIdGen;

//导出基础数据供应商列表应用模型
public class ExpBdSupListAppMod {
	private static final Logger logger = LogManager.getLogger(ExpBdSupListAppMod.class.getName());
	
	//基础数据供应商列表应用模型
	private BdSupListAppMod bslAppMod = new BdSupListAppMod();
	
	//是否为真实数据标识
	private static boolean isRealData = true;
	//页号、页大小和总页数
	int curPageNum = 1, pageTotal = 1, pageSize = AppModConfig.maxPageSize;
	//报表文件资源路径
	String repFileResPath = "/expBdSupList/";
	//导出列名数组
	//String[] colNames = {"序号","供应商名称", "供应商类型", "省市", "区县", "详细地址", "营业执照编号", "注册资本", "食品经营许可证", "食品流通许可证", "食品生产许可证编号", "关联团餐公司数量"};
	String[] colNames = {"序号","供应商名称", "供应商类型", "详细地址", "营业执照编号",  "食品经营许可证", "食品流通许可证", "食品生产许可证编号"};
	//变量数据初始化
	String supplierName = null;
	String supplierType = null;
	String distName = null;
	String prefCity = null;
	String province = null;
	String blNo = null;
	String regCapital = null;
	String fblNo = null;
	String fcpNo = null;	
	String expFileUrl = "test1.txt";

	//模拟数据函数
	private ExpBdSupListDTO SimuDataFunc() {
		ExpBdSupListDTO ebslDto = new ExpBdSupListDTO();
		//设置返回数据
		ebslDto.setTime(BCDTimeUtil.convertNormalFrom(null));
		//列表元素设置
		ExpBdSupList expBdSupList = new ExpBdSupList();
		//赋值
		expBdSupList.setSupplierName(supplierName);
		expBdSupList.setSupplierType(supplierType);
		expBdSupList.setDistName(distName);
		expBdSupList.setPrefCity(prefCity);
		expBdSupList.setProvince(province);
		expBdSupList.setBlNo(blNo);
		expBdSupList.setRegCapital(regCapital);
		expBdSupList.setFblNo(fblNo);
		expBdSupList.setFcpNo(fcpNo);
		expBdSupList.setExpFileUrl(SpringConfig.repfile_srvdn+repFileResPath+expFileUrl);
		//设置模拟数据
		ebslDto.setExpBdSupList(expBdSupList);
		//消息ID
		ebslDto.setMsgId(AppModConfig.msgId);
		AppModConfig.msgId++;
		//消息id小于0判断
		AppModConfig.msgIdLessThan0Judge();

		return ebslDto;
	}
	
	//生成导出EXCEL文件
	public boolean expBdSupListExcel(String pathFileName, List<BdSupList> dataList,List<UserSetColums> userSetColumsList, String colNames[]) { 
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
		  	
        	int startRowIdx = 0;
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
			int startColumnIdx = 0;
			for (int i = 0; i < dataList.size(); i++) {
				startColumnIdx = 0;
				row = (Row) sheet.createRow(i + startRowIdx); 
				if(userSetColumsList !=null && userSetColumsList.size() > 0) {
					for(UserSetColums obj : userSetColumsList) {
					  if(obj != null && obj.isChecked()) {
						  if("sortNo".equals(obj.getKey()))
						  row.createCell(startColumnIdx++).setCellValue(i+1);
					    if("supplierName".equals(obj.getKey())) 
					      	row.createCell(startColumnIdx++).setCellValue(dataList.get(i).getSupplierName());                                     //供应商名称	
					    if("supplierType".equals(obj.getKey())) 
					      	row.createCell(startColumnIdx++).setCellValue(dataList.get(i).getSupplierType());                                     //供应商类型	
					    if("detAddress".equals(obj.getKey())) 
					      	row.createCell(startColumnIdx++).setCellValue(dataList.get(i).getDetAddress());                                       //详细地址	
					    if("blNo".equals(obj.getKey())) 
					      	row.createCell(startColumnIdx++).setCellValue(dataList.get(i).getBlNo());                                             //营业执照编号	
					    if("fblNo".equals(obj.getKey())) 
					      	row.createCell(startColumnIdx++).setCellValue(dataList.get(i).getRegCapital());                                       //注册资本	
					    if("fcpNo".equals(obj.getKey())) 
					      	row.createCell(startColumnIdx++).setCellValue(dataList.get(i).getFblNo());                                            //食品经营许可证	
					    if("fplNo".equals(obj.getKey())) 
					      	row.createCell(startColumnIdx++).setCellValue(dataList.get(i).getFcpNo());                                            //食品流通许可证	

					  }
					}
				  }else {
					  row.createCell(startColumnIdx++).setCellValue(i+1);
					row.createCell(startColumnIdx++).setCellValue(dataList.get(i).getSupplierName());                                     //供应商名称
					row.createCell(startColumnIdx++).setCellValue(dataList.get(i).getSupplierType());                                     //供应商类型
					row.createCell(startColumnIdx++).setCellValue(dataList.get(i).getDetAddress());                                       //详细地址
					row.createCell(startColumnIdx++).setCellValue(dataList.get(i).getBlNo());                                             //营业执照编号
					row.createCell(startColumnIdx++).setCellValue(dataList.get(i).getRegCapital());                                       //注册资本
					row.createCell(startColumnIdx++).setCellValue(dataList.get(i).getFblNo());                                            //食品经营许可证
					row.createCell(startColumnIdx++).setCellValue(dataList.get(i).getFcpNo());                                            //食品流通许可证
					row.createCell(startColumnIdx++).setCellValue(dataList.get(i).getFplNo());                                            //食品生产许可证编号
				  }
				//row.createCell(10).setCellValue(dataList.get(i).getRelRmcNum());                                       //关联团餐公司数量
				/*row.createCell(2).setCellValue(dataList.get(i).getProvince());                                         //省市
				row.createCell(3).setCellValue(dataList.get(i).getDistCounty());                                       //区县
*/
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
	
	//导出基础数据供应商列表模型函数
	public ExpBdSupListDTO appModFunc(String token, String supplierName, String supplierType, String distName, String prefCity, String province, String blNo, String regCapital, String fblNo, String fcpNo, Db1Service db1Service, Db2Service db2Service, SaasService saasService) {
		ExpBdSupListDTO ebslDto = null;
		if (isRealData) { // 真实数据
			String strCurPageNum = String.valueOf(curPageNum), strPageSize = String.valueOf(pageSize);
			BdSupListDTO bslDto = bslAppMod.appModFunc(token, supplierName, supplierType, distName, prefCity, province, blNo, regCapital, fblNo, fcpNo, strCurPageNum, strPageSize, db1Service, db2Service, saasService);
			if(bslDto != null) {
				int i, totalCount = bslDto.getPageInfo().getPageTotal();
				int pageCount = 0;
				List<BdSupList> expExcelList = new ArrayList<>();
				if(totalCount % pageSize == 0)
					pageCount = totalCount/pageSize;
				else
					pageCount = totalCount/pageSize + 1;
				//第一页数据
				if(bslDto.getBdSupList() != null) {
					expExcelList.addAll(bslDto.getBdSupList());
				}
				//后续页数据
				for(i = curPageNum+1; i <= pageCount; i++) {
					strCurPageNum = String.valueOf(i);
					BdSupListDTO curPdlDto = bslAppMod.appModFunc(token, supplierName, supplierType, distName, prefCity, province, blNo, regCapital, fblNo, fcpNo, strCurPageNum, strPageSize, db1Service, db2Service, saasService);
					if(curPdlDto.getBdSupList() != null) {
						expExcelList.addAll(curPdlDto.getBdSupList());
					}
				}
				//生成导出EXCEL文件
				String repFileName = repFileResPath + UniqueIdGen.uuid() + SpringConfig.repFileFormats[SpringConfig.curRepFileFrmtIdx];
				String pathFileName = SpringConfig.base_dir + repFileName;
				logger.info("导出文件路径：" + pathFileName);
				List<UserSetColums> userSetColumsList =CommonUtil.getUserSetColumList(token,"bdSupList", db2Service);
				boolean flag = expBdSupListExcel(pathFileName, expExcelList,userSetColumsList, colNames);
				if(flag) {
					//移动文件到其他目录
					//AppModConfig.moveFileToOtherFolder(pathFileName, SpringConfig.tomcatSrvDirs[1] + repFileResPath);
					ebslDto = new ExpBdSupListDTO();
					ExpBdSupList expBdSupList = new ExpBdSupList();
					//时戳
					ebslDto.setTime(BCDTimeUtil.convertNormalFrom(null));
					//导出信息
					expBdSupList.setSupplierName(supplierName);
					expBdSupList.setSupplierType(supplierType);
					expBdSupList.setDistName(distName);
					expBdSupList.setPrefCity(prefCity);
					expBdSupList.setProvince(province);
					expBdSupList.setBlNo(blNo);
					expBdSupList.setRegCapital(regCapital);
					expBdSupList.setFblNo(fblNo);
					expBdSupList.setFcpNo(fcpNo);
					expFileUrl = SpringConfig.repfile_srvdn + repFileName;
					logger.info("导出文件URL：" + expFileUrl);
					expBdSupList.setExpFileUrl(expFileUrl);
					ebslDto.setExpBdSupList(expBdSupList);
					//消息ID
					ebslDto.setMsgId(AppModConfig.msgId);
					AppModConfig.msgId++;
					// 消息id小于0判断
					AppModConfig.msgIdLessThan0Judge();
				}
			}
		} else { // 模拟数据
			// 模拟数据函数
			ebslDto = SimuDataFunc();
		}

		return ebslDto;
	}
}