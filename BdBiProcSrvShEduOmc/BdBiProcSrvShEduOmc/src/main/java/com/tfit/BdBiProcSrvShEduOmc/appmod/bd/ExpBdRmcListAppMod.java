package com.tfit.BdBiProcSrvShEduOmc.appmod.bd;

import java.io.ByteArrayOutputStream;
import java.io.File;
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
import com.tfit.BdBiProcSrvShEduOmc.dto.bd.BdRmcList;
import com.tfit.BdBiProcSrvShEduOmc.dto.bd.BdRmcListDTO;
import com.tfit.BdBiProcSrvShEduOmc.dto.bd.ExpBdRmcList;
import com.tfit.BdBiProcSrvShEduOmc.dto.bd.ExpBdRmcListDTO;
import com.tfit.BdBiProcSrvShEduOmc.dto.user.UserSetColums;
import com.tfit.BdBiProcSrvShEduOmc.service.Db1Service;
import com.tfit.BdBiProcSrvShEduOmc.service.Db2Service;
import com.tfit.BdBiProcSrvShEduOmc.service.SaasService;
import com.tfit.BdBiProcSrvShEduOmc.util.BCDTimeUtil;
import com.tfit.BdBiProcSrvShEduOmc.util.CommonUtil;
import com.tfit.BdBiProcSrvShEduOmc.util.FtpUtil;
import com.tfit.BdBiProcSrvShEduOmc.util.UniqueIdGen;

//导出基础数据团餐公司列表应用模型
public class ExpBdRmcListAppMod {
	private static final Logger logger = LogManager.getLogger(ExpBdRmcListAppMod.class.getName());
	
	//基础数据团餐公司列表应用模型
	private BdRmcListAppMod brlAppMod = new BdRmcListAppMod();
	
	//是否为真实数据标识
	private static boolean isRealData = true;
	//页号、页大小和总页数
	int curPageNum = 1, pageTotal = 1, pageSize = AppModConfig.maxPageSize;
	//报表文件资源路径
	String repFileResPath = "/expBdRmcList/";
	//导出列名数组
	String[] colNames = {"序号","企业名称", "所在地", "详细地址", "电子邮箱", "联系人", "手机号", "质量负责人",
			"联系电话", "关联项目点", "证照名称", "证照图片", "经营单位", "许可证号", "发证日期", "有效日期",
			"统一社会信用代码", "证照图片", "经营单位", "经营范围", "注册地址", "详细地址", "注册资本",
			"法人代表", "发证机关", "发证日期", "有效日期", "食品卫生许可证", "证照图片", "经营单位",
			"许可证号", "发证日期", "有效日期", "运输许可证", "证照图片", "经营单位", "许可证号", "发证日期", "有效日期", 
			"IOS认证证书", "证照图片", "经营单位", "许可证号", "发证日期", "有效日期"};
	
	//变量数据初始化
	String compName = null;
	String distName = null;
	String prefCity = null;
	String province = "上海市";
	String contact = null;
	String fblNo = null;
	String uscc = null;
	String regCapital = null;	
	String expFileUrl = "test1.txt";

	//模拟数据函数
	private ExpBdRmcListDTO SimuDataFunc() {
		ExpBdRmcListDTO ebrlDto = new ExpBdRmcListDTO();
		//设置返回数据
		ebrlDto.setTime(BCDTimeUtil.convertNormalFrom(null));
		//列表元素设置
		ExpBdRmcList expBdRmcList = new ExpBdRmcList();
		//赋值
		expBdRmcList.setCompName(compName);
		expBdRmcList.setDistName(distName);
		expBdRmcList.setPrefCity(prefCity);
		expBdRmcList.setProvince(province);
		expBdRmcList.setContact(contact);
		expBdRmcList.setFblNo(fblNo);
		expBdRmcList.setUscc(uscc);
		expBdRmcList.setRegCapital(regCapital);
		expBdRmcList.setExpFileUrl(SpringConfig.repfile_srvdn+repFileResPath+expFileUrl);
		//设置模拟数据
		ebrlDto.setExpBdRmcList(expBdRmcList);
		//消息ID
		ebrlDto.setMsgId(AppModConfig.msgId);
		AppModConfig.msgId++;
		//消息id小于0判断
		AppModConfig.msgIdLessThan0Judge();

		return ebrlDto;
	}
	
	//生成导出EXCEL文件
	public boolean expBdRmcListExcel(String pathFileName, List<BdRmcList> dataList,List<UserSetColums> userSetColumsList, String colNames[]) { 
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
			int columnIdx = 0;
			for (int i = 0; i < dataList.size(); i++) {
				columnIdx = 0;
				row = (Row) sheet.createRow(i + startRowIdx);
				if(userSetColumsList !=null && userSetColumsList.size() > 0) {
					for(UserSetColums obj : userSetColumsList) {
					  if(obj != null && obj.isChecked()) {
						  if("sortNo".equals(obj.getKey()))
						  row.createCell(columnIdx++).setCellValue(i+1);
					    if("compName".equals(obj.getKey())) 
					      	row.createCell(columnIdx++).setCellValue(dataList.get(i).getCompName());                                           //企业名称	
					    if("location".equals(obj.getKey())) 
					      	row.createCell(columnIdx++).setCellValue(AppModConfig.distIdToNameMap.get(dataList.get(i).getLocation()));         //所在地	
					    if("detailAddr".equals(obj.getKey())) 
					      	row.createCell(columnIdx++).setCellValue(dataList.get(i).getDetailAddr());                                         //详细地址	
					    if("email".equals(obj.getKey())) 
					      	row.createCell(columnIdx++).setCellValue(dataList.get(i).getEmail());                                              //电子邮箱	
					    if("contact".equals(obj.getKey())) 
					      	row.createCell(columnIdx++).setCellValue(dataList.get(i).getContact());                                            //联系人	
					    if("mobilePhone".equals(obj.getKey())) 
					      	row.createCell(columnIdx++).setCellValue(dataList.get(i).getMobilePhone());                                        //手机号	
					    if("qaLeader".equals(obj.getKey())) 
					      	row.createCell(columnIdx++).setCellValue(dataList.get(i).getQaLeader());                                           //质量负责人	
					    if("contactNo".equals(obj.getKey())) 
					      	row.createCell(columnIdx++).setCellValue(dataList.get(i).getContactNo());                                          //联系电话	
					    if("relPpNum".equals(obj.getKey())) 
					      	row.createCell(columnIdx++).setCellValue(dataList.get(i).getRelPpNum());                                           //关联项目点	
					    if("licName".equals(obj.getKey())) 
					      	row.createCell(columnIdx++).setCellValue(dataList.get(i).getLicName());                                            //证照名称	
					    if("licPic".equals(obj.getKey())) 
					      	row.createCell(columnIdx++).setCellValue(dataList.get(i).getLicPic());                                            //证照图片	
					    if("licOptUnit".equals(obj.getKey())) 
					      	row.createCell(columnIdx++).setCellValue(dataList.get(i).getLicOptUnit());                                        //经营单位	
					    if("licNo".equals(obj.getKey())) 
					      	row.createCell(columnIdx++).setCellValue(dataList.get(i).getLicNo());                                             //许可证号	
					    if("licIssueDate".equals(obj.getKey())) 
					    	row.createCell(columnIdx++).setCellValue(dataList.get(i).getLicIssueDate());                                      //有效日期	
					    if("licValidDate".equals(obj.getKey())) 
					      	row.createCell(columnIdx++).setCellValue(dataList.get(i).getLicValidDate());                                      //有效日期	
					    if("uscc".equals(obj.getKey())) 
					      	row.createCell(columnIdx++).setCellValue(dataList.get(i).getUscc());                                              //统一社会信用代码	
					    if("usccPic".equals(obj.getKey())) 
					      	row.createCell(columnIdx++).setCellValue(dataList.get(i).getUsccPic());                                           //证照图片	
					    if("usccOptUnit".equals(obj.getKey())) 
					      	row.createCell(columnIdx++).setCellValue(dataList.get(i).getUsccOptUnit());                                       //经营单位	
					    if("optScope".equals(obj.getKey())) 
					      	row.createCell(columnIdx++).setCellValue(dataList.get(i).getOptScope());                                          //经营范围	
					    if("regAddr".equals(obj.getKey())) 
					      	row.createCell(columnIdx++).setCellValue(dataList.get(i).getRegAddr());                                           //注册地址	
					    if("udetailAddr".equals(obj.getKey())) 
					      	row.createCell(columnIdx++).setCellValue(dataList.get(i).getUdetailAddr());                                       //详细地址	
					    if("regCapital".equals(obj.getKey())) 
					      	row.createCell(columnIdx++).setCellValue(dataList.get(i).getRegCapital());                                        //注册资本	
					    if("corpRepName".equals(obj.getKey())) 
					      	row.createCell(columnIdx++).setCellValue(dataList.get(i).getCorpRepName());                                       //法人代表	
					    if("ucssIssueAuth".equals(obj.getKey())) 
					      	row.createCell(columnIdx++).setCellValue(dataList.get(i).getUcssIssueAuth());                                     //发证机关	
					    if("ucssIssueDate".equals(obj.getKey())) 
					      	row.createCell(columnIdx++).setCellValue(dataList.get(i).getUcssIssueDate());                                     //发证日期	
					    if("ucssValidDate".equals(obj.getKey())) 
					      	row.createCell(columnIdx++).setCellValue(dataList.get(i).getUcssValidDate());                                     //有效日期	
					    if("fhlName".equals(obj.getKey())) 
					      	row.createCell(columnIdx++).setCellValue(dataList.get(i).getFhlName());                                           //食品卫生许可证	
					    if("fhlPic".equals(obj.getKey())) 
					      	row.createCell(columnIdx++).setCellValue(dataList.get(i).getFhlPic());                                            //证照图片	
					    if("fhlOptUnit".equals(obj.getKey())) 
					      	row.createCell(columnIdx++).setCellValue(dataList.get(i).getFhlOptUnit());                                        //经营单位	
					    if("fhlNo".equals(obj.getKey())) 
					      	row.createCell(columnIdx++).setCellValue(dataList.get(i).getFhlNo());                                             //许可证号	
					    if("fhlIssueDate".equals(obj.getKey())) 
					      	row.createCell(columnIdx++).setCellValue(dataList.get(i).getFhlIssueDate());                                      //发证日期	
					    if("fhlValidDate".equals(obj.getKey())) 
					      	row.createCell(columnIdx++).setCellValue(dataList.get(i).getFhlValidDate());                                      //有效日期	
					    if("tplName".equals(obj.getKey())) 
					      	row.createCell(columnIdx++).setCellValue(dataList.get(i).getTplName());                                           //运输许可证	
					    if("tplPic".equals(obj.getKey())) 
					      	row.createCell(columnIdx++).setCellValue(dataList.get(i).getTplNo());                                             //证照图片	
					    if("tplOptUnit".equals(obj.getKey())) 
					      	row.createCell(columnIdx++).setCellValue(dataList.get(i).getTplOptUnit());                                        //经营单位	
					    if("tplNo".equals(obj.getKey())) 
					      	row.createCell(columnIdx++).setCellValue(dataList.get(i).getTplNo());                                             //许可证号	
					    if("tplIssueDate".equals(obj.getKey())) 
					      	row.createCell(columnIdx++).setCellValue(dataList.get(i).getTplIssueDate());                                      //发证日期	
					    if("tplValidDate".equals(obj.getKey())) 
					      	row.createCell(columnIdx++).setCellValue(dataList.get(i).getTplValidDate());                                      //有效日期	
					    if("iosName".equals(obj.getKey())) 
					      	row.createCell(columnIdx++).setCellValue(dataList.get(i).getIosName());                                           //IOS认证证书	
					    if("iosPic".equals(obj.getKey())) 
					      	row.createCell(columnIdx++).setCellValue(dataList.get(i).getIosPic());                                            //证照图片	
					    if("iosOptUnit".equals(obj.getKey())) 
					      	row.createCell(columnIdx++).setCellValue(dataList.get(i).getIosOptUnit());                                        //经营单位	
					    if("iosNo".equals(obj.getKey())) 
					      	row.createCell(columnIdx++).setCellValue(dataList.get(i).getIosNo());                                             //许可证号	
					    if("iosIssueDate".equals(obj.getKey())) 
					      	row.createCell(columnIdx++).setCellValue(dataList.get(i).getIosIssueDate());                                      //发证日期	
					    if("iosValidDate".equals(obj.getKey())) 
					      	row.createCell(columnIdx++).setCellValue(dataList.get(i).getIosValidDate());                                      //有效日期	

					  }
					}
				  }else {
					row.createCell(columnIdx++).setCellValue(i+1);
					row.createCell(columnIdx++).setCellValue(dataList.get(i).getCompName());                                           //企业名称
					row.createCell(columnIdx++).setCellValue(AppModConfig.distIdToNameMap.get(dataList.get(i).getLocation()));         //所在地
					row.createCell(columnIdx++).setCellValue(dataList.get(i).getDetailAddr());                                         //详细地址
					row.createCell(columnIdx++).setCellValue(dataList.get(i).getEmail());                                              //电子邮箱
					row.createCell(columnIdx++).setCellValue(dataList.get(i).getContact());                                            //联系人
					row.createCell(columnIdx++).setCellValue(dataList.get(i).getMobilePhone());                                        //手机号
					row.createCell(columnIdx++).setCellValue(dataList.get(i).getQaLeader());                                           //质量负责人
					row.createCell(columnIdx++).setCellValue(dataList.get(i).getContactNo());                                          //联系电话
					row.createCell(columnIdx++).setCellValue(dataList.get(i).getRelPpNum());                                           //关联项目点
					
					
					row.createCell(columnIdx++).setCellValue(dataList.get(i).getLicName());                                            //证照名称
					row.createCell(columnIdx++).setCellValue(dataList.get(i).getLicPic());                                            //证照图片
					row.createCell(columnIdx++).setCellValue(dataList.get(i).getLicOptUnit());                                        //经营单位
					row.createCell(columnIdx++).setCellValue(dataList.get(i).getLicNo());                                             //许可证号
					row.createCell(columnIdx++).setCellValue(dataList.get(i).getLicIssueDate());                                      //发证日期
					row.createCell(columnIdx++).setCellValue(dataList.get(i).getLicValidDate());                                      //有效日期
					row.createCell(columnIdx++).setCellValue(dataList.get(i).getUscc());                                              //统一社会信用代码
					row.createCell(columnIdx++).setCellValue(dataList.get(i).getUsccPic());                                           //证照图片
					
					row.createCell(columnIdx++).setCellValue(dataList.get(i).getUsccOptUnit());                                       //经营单位
					row.createCell(columnIdx++).setCellValue(dataList.get(i).getOptScope());                                          //经营范围
					row.createCell(columnIdx++).setCellValue(dataList.get(i).getRegAddr());                                           //注册地址
					row.createCell(columnIdx++).setCellValue(dataList.get(i).getUdetailAddr());                                       //详细地址
					row.createCell(columnIdx++).setCellValue(dataList.get(i).getRegCapital());                                        //注册资本
					row.createCell(columnIdx++).setCellValue(dataList.get(i).getCorpRepName());                                       //法人代表
					row.createCell(columnIdx++).setCellValue(dataList.get(i).getUcssIssueAuth());                                     //发证机关
					row.createCell(columnIdx++).setCellValue(dataList.get(i).getUcssIssueDate());                                     //发证日期
					row.createCell(columnIdx++).setCellValue(dataList.get(i).getUcssValidDate());                                     //有效日期
					
					row.createCell(columnIdx++).setCellValue(dataList.get(i).getFhlName());                                           //食品卫生许可证
					row.createCell(columnIdx++).setCellValue(dataList.get(i).getFhlPic());                                            //证照图片
					row.createCell(columnIdx++).setCellValue(dataList.get(i).getFhlOptUnit());                                        //经营单位
					row.createCell(columnIdx++).setCellValue(dataList.get(i).getFhlNo());                                             //许可证号
					row.createCell(columnIdx++).setCellValue(dataList.get(i).getFhlIssueDate());                                      //发证日期
					row.createCell(columnIdx++).setCellValue(dataList.get(i).getFhlValidDate());                                      //有效日期
					row.createCell(columnIdx++).setCellValue(dataList.get(i).getTplName());                                           //运输许可证
					row.createCell(columnIdx++).setCellValue(dataList.get(i).getTplNo());                                             //证照图片
					
					row.createCell(columnIdx++).setCellValue(dataList.get(i).getTplOptUnit());                                        //经营单位
					row.createCell(columnIdx++).setCellValue(dataList.get(i).getTplNo());                                             //许可证号
					row.createCell(columnIdx++).setCellValue(dataList.get(i).getTplIssueDate());                                      //发证日期				
					row.createCell(columnIdx++).setCellValue(dataList.get(i).getTplValidDate());                                      //有效日期
					row.createCell(columnIdx++).setCellValue(dataList.get(i).getIosName());                                           //IOS认证证书
					row.createCell(columnIdx++).setCellValue(dataList.get(i).getIosPic());                                            //证照图片
					row.createCell(columnIdx++).setCellValue(dataList.get(i).getIosOptUnit());                                        //经营单位
					row.createCell(columnIdx++).setCellValue(dataList.get(i).getIosNo());                                             //许可证号
					row.createCell(columnIdx++).setCellValue(dataList.get(i).getIosIssueDate());                                      //发证日期
					row.createCell(columnIdx++).setCellValue(dataList.get(i).getIosValidDate());                                      //有效日期
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
	
	//导出基础数据团餐公司列表模型函数
	public ExpBdRmcListDTO appModFunc(String token, String compName, String distName, String prefCity, String province, String contact, String fblNo, String uscc, String regCapital, Db1Service db1Service, Db2Service db2Service, SaasService saasService) {
		ExpBdRmcListDTO ebrlDto = null;
		if (isRealData) { // 真实数据
			String strCurPageNum = String.valueOf(curPageNum), strPageSize = String.valueOf(pageSize);
			BdRmcListDTO brlDto = brlAppMod.appModFunc(token, compName, distName, prefCity, province, contact, fblNo, uscc, regCapital, strCurPageNum, strPageSize, db1Service, db2Service, saasService);
			if(brlDto != null) {
				int i, totalCount = brlDto.getPageInfo().getPageTotal();
				int pageCount = 0;
				List<BdRmcList> expExcelList = new ArrayList<>();
				if(totalCount % pageSize == 0)
					pageCount = totalCount/pageSize;
				else
					pageCount = totalCount/pageSize + 1;
				//第一页数据
				if(brlDto.getBdRmcList() != null) {
					expExcelList.addAll(brlDto.getBdRmcList());
				}
				//后续页数据
				for(i = curPageNum+1; i <= pageCount; i++) {
					strCurPageNum = String.valueOf(i);
					BdRmcListDTO curPdlDto = brlAppMod.appModFunc(token, compName, distName, prefCity, province, contact, fblNo, uscc, regCapital, strCurPageNum, strPageSize, db1Service, db2Service, saasService);
					if(curPdlDto.getBdRmcList() != null) {
						expExcelList.addAll(curPdlDto.getBdRmcList());
					}
				}
				//生成导出EXCEL文件
				String repFileName = repFileResPath + UniqueIdGen.uuid() + SpringConfig.repFileFormats[SpringConfig.curRepFileFrmtIdx];
				String pathFileName = SpringConfig.base_dir + repFileName;
				logger.info("导出文件路径：" + pathFileName);
				List<UserSetColums> userSetColumsList =CommonUtil.getUserSetColumList(token,"bdRmcList", db2Service);
				boolean flag = expBdRmcListExcel(pathFileName, expExcelList, userSetColumsList,colNames);
				if(flag) {
					//移动文件到其他目录
					//AppModConfig.moveFileToOtherFolder(pathFileName, SpringConfig.tomcatSrvDirs[1] + repFileResPath);
					ebrlDto = new ExpBdRmcListDTO();
					ExpBdRmcList expBdRmcList = new ExpBdRmcList();
					//时戳
					ebrlDto.setTime(BCDTimeUtil.convertNormalFrom(null));
					//导出信息
					expBdRmcList.setCompName(compName);
					expBdRmcList.setDistName(distName);
					expBdRmcList.setPrefCity(prefCity);
					expBdRmcList.setProvince(province);
					expBdRmcList.setContact(contact);
					expBdRmcList.setFblNo(fblNo);
					expBdRmcList.setUscc(uscc);
					expBdRmcList.setRegCapital(regCapital);
					expFileUrl = SpringConfig.repfile_srvdn + repFileName;
					logger.info("导出文件URL：" + expFileUrl);
					expBdRmcList.setExpFileUrl(expFileUrl);
					ebrlDto.setExpBdRmcList(expBdRmcList);
					//消息ID
					ebrlDto.setMsgId(AppModConfig.msgId);
					AppModConfig.msgId++;
					// 消息id小于0判断
					AppModConfig.msgIdLessThan0Judge();
				}
			}
		} else { // 模拟数据
			// 模拟数据函数
			ebrlDto = SimuDataFunc();
		}

		return ebrlDto;
	}
}
