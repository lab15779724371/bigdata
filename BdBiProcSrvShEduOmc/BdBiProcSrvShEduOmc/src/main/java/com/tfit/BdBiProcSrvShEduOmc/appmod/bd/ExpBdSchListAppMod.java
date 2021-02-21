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
import com.tfit.BdBiProcSrvShEduOmc.dto.bd.BdSchList;
import com.tfit.BdBiProcSrvShEduOmc.dto.bd.BdSchListDTO;
import com.tfit.BdBiProcSrvShEduOmc.dto.bd.ExpBdSchList;
import com.tfit.BdBiProcSrvShEduOmc.dto.bd.ExpBdSchListDTO;
import com.tfit.BdBiProcSrvShEduOmc.dto.user.UserSetColums;
import com.tfit.BdBiProcSrvShEduOmc.service.Db1Service;
import com.tfit.BdBiProcSrvShEduOmc.service.Db2Service;
import com.tfit.BdBiProcSrvShEduOmc.service.SaasService;
import com.tfit.BdBiProcSrvShEduOmc.util.BCDTimeUtil;
import com.tfit.BdBiProcSrvShEduOmc.util.CommonUtil;
import com.tfit.BdBiProcSrvShEduOmc.util.FtpUtil;
import com.tfit.BdBiProcSrvShEduOmc.util.UniqueIdGen;

//导出基础数据学校列表应用模型
public class ExpBdSchListAppMod {
	private static final Logger logger = LogManager.getLogger(ExpBdSchListAppMod.class.getName());
	
	//基础数据学校列表应用模型
	private BdSchListAppMod bslAppMod = new BdSchListAppMod();
	
	//是否为真实数据标识
	private static boolean isRealData = true;
	//页号、页大小和总页数
	int curPageNum = 1, pageTotal = 1, pageSize = AppModConfig.maxPageSize;
	//报表文件资源路径
	String repFileResPath = "/expBdSchList/";
	//导出列名数组
	String[] colNames = {"序号","学校规范名称", "总校/分校", "分校数量", "关联总校", "所在区", "地址", "统一社会信用代码",
			"学校学制", "学校性质","管理部门", "所属", "主管部门", "所属区", "" +
			"备注说明", "食品经营许可证主体", "供餐模式", "学生人数", "教职工人数", "法定代表人", "手机", "座机",
			"部门负责人姓名", "手机", "座机", "传真", "电子邮件", "项目联系人", "手机", "座机","证件名称",
			"图片", "经营单位", "许可证号", "发证机关", "发证日期", "有效日期", "关联单位名称"};
		
	//变量数据初始化
	String schName = null;
	String distName = null;
	String prefCity = null;
	String province = "上海市";
	String schType = null;
	String schProp = null;
	String isSetsem = null;
	String optMode = null;
	String relCompName = null;
	String genBraSch = null;
	String relGenSch = null;
	String subLevel = null;
	String compDep = null;
	String fblMb = null;
	String uscc = null;	
	String expFileUrl = "test1.txt";

	//模拟数据函数
	private ExpBdSchListDTO SimuDataFunc() {
		ExpBdSchListDTO ebslDto = new ExpBdSchListDTO();
		//设置返回数据
		ebslDto.setTime(BCDTimeUtil.convertNormalFrom(null));
		//列表元素设置
		ExpBdSchList expBdSchList = new ExpBdSchList();
		//赋值
		expBdSchList.setSchName(schName);
		expBdSchList.setDistName(distName);
		expBdSchList.setPrefCity(prefCity);
		expBdSchList.setProvince(province);
		expBdSchList.setSchType(schType);
		expBdSchList.setSchProp(schProp);
		expBdSchList.setIsSetsem(isSetsem);
		expBdSchList.setOptMode(optMode);
		expBdSchList.setRelCompName(relCompName);
		expBdSchList.setGenBraSch(genBraSch);
		expBdSchList.setRelGenSch(relGenSch);
		expBdSchList.setSubLevel(subLevel);
		expBdSchList.setCompDep(compDep);
		expBdSchList.setFblMb(fblMb);
		expBdSchList.setUscc(uscc);
		expBdSchList.setExpFileUrl(SpringConfig.repfile_srvdn+repFileResPath+expFileUrl);
		//设置模拟数据
		ebslDto.setExpBdSchList(expBdSchList);
		//消息ID
		ebslDto.setMsgId(AppModConfig.msgId);
		AppModConfig.msgId++;
		//消息id小于0判断
		AppModConfig.msgIdLessThan0Judge();

		return ebslDto;
	}
	
	//生成导出EXCEL文件
	public boolean expBdSchListExcel(String pathFileName, List<BdSchList> dataList,List<UserSetColums> userSetColumsList, String colNames[]) { 
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
					    if("schStdName".equals(obj.getKey())) 
					      	row.createCell(columnIdx++).setCellValue(dataList.get(i).getSchStdName());                                           //学校规范名称	
					    if("schGenBraFlag".equals(obj.getKey())) 
					      	row.createCell(columnIdx++).setCellValue(dataList.get(i).getSchGenBraFlag());                                        //总校/分校	
					    if("braCampusNum".equals(obj.getKey())) 
					      	row.createCell(columnIdx++).setCellValue(dataList.get(i).getBraCampusNum());                                         //分校数量	
					    if("relGenSchName".equals(obj.getKey())) 
					      	row.createCell(columnIdx++).setCellValue(dataList.get(i).getRelGenSchName());                                        //关联总校	
					    if("distName".equals(obj.getKey())) 
					      	row.createCell(columnIdx++).setCellValue(dataList.get(i).getDistName());                                             //所在区	
					    if("detailAddr".equals(obj.getKey())) 
					      	row.createCell(columnIdx++).setCellValue(dataList.get(i).getDetailAddr());                                           //地址	
					    if("uscc".equals(obj.getKey())) 
					      	row.createCell(columnIdx++).setCellValue(dataList.get(i).getUscc());                                                 //统一社会信用代码	
					    if("schType".equals(obj.getKey())) 
					      	row.createCell(columnIdx++).setCellValue(dataList.get(i).getSchType());                                              //学校学制	
					    if("schProp".equals(obj.getKey())) 
					      	row.createCell(columnIdx++).setCellValue(dataList.get(i).getSchProp());                                              //学校性质	
					    if("subLevel".equals(obj.getKey())) 
					    	row.createCell(columnIdx++).setCellValue(dataList.get(i).getSubLevel());                                             //所属
					    if("compDep".equals(obj.getKey())) 
					    	row.createCell(columnIdx++).setCellValue(dataList.get(i).getCompDep());                                             //主管部门	
					    if("subDistName".equals(obj.getKey())) 
					    	row.createCell(columnIdx++).setCellValue(dataList.get(i).getSubDistName());                                         //所属区	
					    if("departmentName".equals(obj.getKey())) 
					    	row.createCell(columnIdx++).setCellValue(dataList.get(i).getDepartmentName());                                              //管理部门	
					    if("remark".equals(obj.getKey())) 
					      	row.createCell(columnIdx++).setCellValue(dataList.get(i).getRemark());                                              //备注说明	
					    if("fblMb".equals(obj.getKey())) 
					      	row.createCell(columnIdx++).setCellValue(dataList.get(i).getFblMb());                                               //食品经营许可证主体	
					    if("optMode".equals(obj.getKey())) 
					      	row.createCell(columnIdx++).setCellValue(dataList.get(i).getOptMode());                                             //供餐模式	
					    if("studentNum".equals(obj.getKey())) 
					      	row.createCell(columnIdx++).setCellValue(dataList.get(i).getStudentNum());                                          //学生人数	
					    if("teacherNum".equals(obj.getKey())) 
					      	row.createCell(columnIdx++).setCellValue(dataList.get(i).getTeacherNum());                                          //教职工人数	
					    if("legalRep".equals(obj.getKey())) 
					      	row.createCell(columnIdx++).setCellValue(dataList.get(i).getLegalRep());                                            //法定代表人	
					    if("lrMobilePhone".equals(obj.getKey())) 
					      	row.createCell(columnIdx++).setCellValue(dataList.get(i).getLrMobilePhone());                                       //手机	
					    if("lrFixPhone".equals(obj.getKey())) 
					      	row.createCell(columnIdx++).setCellValue(dataList.get(i).getLrFixPhone());                                          //座机	
					    if("depHeadName".equals(obj.getKey())) 
					      	row.createCell(columnIdx++).setCellValue(dataList.get(i).getDepHeadName());                                         //部门负责人姓名	
					    if("dhnMobilePhone".equals(obj.getKey())) 
					      	row.createCell(columnIdx++).setCellValue(dataList.get(i).getDhnMobilePhone());                                      //手机	
					    if("dhnFixPhone".equals(obj.getKey())) 
					      	row.createCell(columnIdx++).setCellValue(dataList.get(i).getDhnFixPhone());                                         //座机	
					    if("dhnFax".equals(obj.getKey())) 
					      	row.createCell(columnIdx++).setCellValue(dataList.get(i).getDhnFax());                                              //传真	
					    if("dhnEmail".equals(obj.getKey())) 
					      	row.createCell(columnIdx++).setCellValue(dataList.get(i).getDhnEmail());                                            //电子邮件	
					    if("projContact".equals(obj.getKey())) 
					      	row.createCell(columnIdx++).setCellValue(dataList.get(i).getProjContact());                                         //项目联系人	
					    if("pcMobilePhone".equals(obj.getKey())) 
					      	row.createCell(columnIdx++).setCellValue(dataList.get(i).getPcMobilePhone());                                       //手机	
					    if("pcFixPhone".equals(obj.getKey())) 
					      	row.createCell(columnIdx++).setCellValue(dataList.get(i).getPcFixPhone());                                          //座机	
					    if("licName".equals(obj.getKey())) 
					      	row.createCell(columnIdx++).setCellValue(dataList.get(i).getLicName());                                             //证件名称	
					    if("schPic".equals(obj.getKey())) 
					      	row.createCell(columnIdx++).setCellValue(dataList.get(i).getSchPic());                                              //图片	
					    if("optUnit".equals(obj.getKey())) 
					      	row.createCell(columnIdx++).setCellValue(dataList.get(i).getOptUnit());                                             //经营单位	
					    if("licNo".equals(obj.getKey())) 
					      	row.createCell(columnIdx++).setCellValue(dataList.get(i).getLicNo());                                               //许可证号	
					    if("licIssueAuth".equals(obj.getKey())) 
					      	row.createCell(columnIdx++).setCellValue(dataList.get(i).getLicIssueAuth());                                        //发证机关	
					    if("licIssueDate".equals(obj.getKey())) 
					      	row.createCell(columnIdx++).setCellValue(dataList.get(i).getLicIssueDate());                                        //发证日期	
					    if("validDate".equals(obj.getKey())) 
					      	row.createCell(columnIdx++).setCellValue(dataList.get(i).getValidDate());                                           //有效日期	
					    if("relCompName".equals(obj.getKey())) 
					      	row.createCell(columnIdx++).setCellValue(dataList.get(i).getRelCompName());                                         //关联单位名称	

					  }
					}
				  }else {
					row.createCell(columnIdx++).setCellValue(i+1);
					row.createCell(columnIdx++).setCellValue(dataList.get(i).getSchStdName());                                           //学校规范名称
					row.createCell(columnIdx++).setCellValue(dataList.get(i).getSchGenBraFlag());                                        //总校/分校
					row.createCell(columnIdx++).setCellValue(dataList.get(i).getBraCampusNum());                                         //分校数量
					row.createCell(columnIdx++).setCellValue(dataList.get(i).getRelGenSchName());                                        //关联总校
					row.createCell(columnIdx++).setCellValue(dataList.get(i).getDistName());                                             //所在区
					row.createCell(columnIdx++).setCellValue(dataList.get(i).getDetailAddr());                                           //地址
					row.createCell(columnIdx++).setCellValue(dataList.get(i).getUscc());                                                 //统一社会信用代码
					row.createCell(columnIdx++).setCellValue(dataList.get(i).getSchType());                                              //学校学制
					row.createCell(columnIdx++).setCellValue(dataList.get(i).getSchProp());                                              //学校性质
					row.createCell(columnIdx++).setCellValue(dataList.get(i).getSubLevel());                                             //所属
					row.createCell(columnIdx++).setCellValue(dataList.get(i).getCompDep());                                             //主管部门
					row.createCell(columnIdx++).setCellValue(dataList.get(i).getSubDistName());                                         //所属区		
					row.createCell(columnIdx++).setCellValue(dataList.get(i).getDepartmentName());                                              //管理部门
					row.createCell(columnIdx++).setCellValue(dataList.get(i).getRemark());                                              //备注说明				
					row.createCell(columnIdx++).setCellValue(dataList.get(i).getFblMb());                                               //食品经营许可证主体				
					row.createCell(columnIdx++).setCellValue(dataList.get(i).getOptMode());                                             //供餐模式				
					row.createCell(columnIdx++).setCellValue(dataList.get(i).getStudentNum());                                          //学生人数				
					row.createCell(columnIdx++).setCellValue(dataList.get(i).getTeacherNum());                                          //教职工人数				
					row.createCell(columnIdx++).setCellValue(dataList.get(i).getLegalRep());                                            //法定代表人				
					row.createCell(columnIdx++).setCellValue(dataList.get(i).getLrMobilePhone());                                       //手机
					row.createCell(columnIdx++).setCellValue(dataList.get(i).getLrFixPhone());                                          //座机
					row.createCell(columnIdx++).setCellValue(dataList.get(i).getDepHeadName());                                         //部门负责人姓名
					row.createCell(columnIdx++).setCellValue(dataList.get(i).getDhnMobilePhone());                                      //手机
					row.createCell(columnIdx++).setCellValue(dataList.get(i).getDhnFixPhone());                                         //座机
					row.createCell(columnIdx++).setCellValue(dataList.get(i).getDhnFax());                                              //传真
					row.createCell(columnIdx++).setCellValue(dataList.get(i).getDhnEmail());                                            //电子邮件
					row.createCell(columnIdx++).setCellValue(dataList.get(i).getProjContact());                                         //项目联系人
					row.createCell(columnIdx++).setCellValue(dataList.get(i).getPcMobilePhone());                                       //手机
					row.createCell(columnIdx++).setCellValue(dataList.get(i).getPcFixPhone());                                          //座机
					row.createCell(columnIdx++).setCellValue(dataList.get(i).getLicName());                                             //证件名称
					row.createCell(columnIdx++).setCellValue(dataList.get(i).getSchPic());                                              //图片
					row.createCell(columnIdx++).setCellValue(dataList.get(i).getOptUnit());                                             //经营单位
					row.createCell(columnIdx++).setCellValue(dataList.get(i).getLicNo());                                               //许可证号
					row.createCell(columnIdx++).setCellValue(dataList.get(i).getLicIssueAuth());                                        //发证机关
					row.createCell(columnIdx++).setCellValue(dataList.get(i).getLicIssueDate());                                        //发证日期
					row.createCell(columnIdx++).setCellValue(dataList.get(i).getValidDate());                                           //有效日期
					row.createCell(columnIdx++).setCellValue(dataList.get(i).getRelCompName());                                         //关联单位名称
				  }
				//row.createCell(columnIdx++).setCellValue(AppModConfig.semSetIdToNameMap.get(dataList.get(i).getIsSetsem()));                                            //学期设置
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
	
	//导出基础数据学校列表模型函数
	public ExpBdSchListDTO appModFunc(String token, String schName, String distName, String prefCity, 
			String province, String schType, String schProp, String isSetsem, String optMode, 
			String relCompName, String schGenBraFlag, String relGenSchName, String subLevel, 
			String compDep, String fblMb, String uscc, String departmentId,
			Db1Service db1Service, Db2Service db2Service, SaasService saasService) {
		ExpBdSchListDTO ebslDto = null;
		if (isRealData) { // 真实数据
			String strCurPageNum = String.valueOf(curPageNum), strPageSize = String.valueOf(pageSize);
			BdSchListDTO bslDto = bslAppMod.appModFunc(token, schName, distName, prefCity, province, schType, schProp, 
					isSetsem, optMode, relCompName, schGenBraFlag, relGenSchName, subLevel, compDep, 
					fblMb, uscc,departmentId, strCurPageNum, strPageSize, db1Service, db2Service, saasService);
			if(bslDto != null) {
				int i, totalCount = bslDto.getPageInfo().getPageTotal();
				int pageCount = 0;
				List<BdSchList> expExcelList = new ArrayList<>();
				if(totalCount % pageSize == 0)
					pageCount = totalCount/pageSize;
				else
					pageCount = totalCount/pageSize + 1;
				//第一页数据
				if(bslDto.getBdSchList() != null) {
					expExcelList.addAll(bslDto.getBdSchList());
				}
				//后续页数据
				for(i = curPageNum+1; i <= pageCount; i++) {
					strCurPageNum = String.valueOf(i);
					BdSchListDTO curPdlDto = bslAppMod.appModFunc(token, schName, distName, prefCity, province, schType, schProp, 
							isSetsem, optMode, relCompName, schGenBraFlag, relGenSchName, subLevel, compDep, fblMb, uscc, departmentId,
							strCurPageNum, strPageSize, db1Service, db2Service, saasService);
					if(curPdlDto.getBdSchList() != null) {
						expExcelList.addAll(curPdlDto.getBdSchList());
					}
				}
				//生成导出EXCEL文件
				String repFileName = repFileResPath + UniqueIdGen.uuid() + SpringConfig.repFileFormats[SpringConfig.curRepFileFrmtIdx];
				String pathFileName = SpringConfig.base_dir + repFileName;
				logger.info("导出文件路径：" + pathFileName);
				List<UserSetColums> userSetColumsList =CommonUtil.getUserSetColumList(token,"bdSchList", db2Service);
				boolean flag = expBdSchListExcel(pathFileName, expExcelList,userSetColumsList, colNames);
				if(flag) {
					//移动文件到其他目录
					//AppModConfig.moveFileToOtherFolder(pathFileName, SpringConfig.tomcatSrvDirs[1] + repFileResPath);
					ebslDto = new ExpBdSchListDTO();
					ExpBdSchList expBdSchList = new ExpBdSchList();
					//时戳
					ebslDto.setTime(BCDTimeUtil.convertNormalFrom(null));
					//导出信息
					expBdSchList.setSchName(schName);
					expBdSchList.setDistName(distName);
					expBdSchList.setPrefCity(prefCity);
					expBdSchList.setProvince(province);
					expBdSchList.setSchType(schType);
					expBdSchList.setSchProp(schProp);
					expBdSchList.setIsSetsem(isSetsem);
					expBdSchList.setOptMode(optMode);
					expBdSchList.setRelCompName(relCompName);
					expBdSchList.setGenBraSch(schGenBraFlag);
					expBdSchList.setRelGenSch(relGenSchName);
					expBdSchList.setSubLevel(subLevel);
					expBdSchList.setCompDep(compDep);
					expBdSchList.setFblMb(fblMb);
					expBdSchList.setUscc(uscc);
					expFileUrl = SpringConfig.repfile_srvdn + repFileName;
					logger.info("导出文件URL：" + expFileUrl);
					expBdSchList.setExpFileUrl(expFileUrl);
					ebslDto.setExpBdSchList(expBdSchList);
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
