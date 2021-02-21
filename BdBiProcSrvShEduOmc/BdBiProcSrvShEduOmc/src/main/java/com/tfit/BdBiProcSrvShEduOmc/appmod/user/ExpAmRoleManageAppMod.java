package com.tfit.BdBiProcSrvShEduOmc.appmod.user;

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
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.tfit.BdBiProcSrvShEduOmc.config.AppModConfig;
import com.tfit.BdBiProcSrvShEduOmc.config.SpringConfig;
import com.tfit.BdBiProcSrvShEduOmc.dto.user.AmRoleManage;
import com.tfit.BdBiProcSrvShEduOmc.dto.user.AmRoleManageDTO;
import com.tfit.BdBiProcSrvShEduOmc.dto.user.ExpAmRoleManage;
import com.tfit.BdBiProcSrvShEduOmc.dto.user.ExpAmRoleManageDTO;
import com.tfit.BdBiProcSrvShEduOmc.service.Db2Service;
import com.tfit.BdBiProcSrvShEduOmc.util.BCDTimeUtil;
import com.tfit.BdBiProcSrvShEduOmc.util.UniqueIdGen;

//导出角色管理应用模型
public class ExpAmRoleManageAppMod {
	private static final Logger logger = LogManager.getLogger(ExpAmRoleManageAppMod.class.getName());
	
	//角色管理应用模型
	private AmRoleManageAppMod armAppMod = new AmRoleManageAppMod();
	
	//是否为真实数据标识
	private static boolean isRealData = true;
	//页号、页大小和总页数
	int curPageNum = 1, pageTotal = 1, pageSize = AppModConfig.maxPageSize;
	//报表文件资源路径
	String repFileResPath = "/expAmRoleManage/";
	//导出列名数组
	String[] colNames = {"序号","角色", "类型", "描述", "创建人", "创建日期"};	
	
	//变量数据初始化	
	String roleName = null;
	String roleType = null;
	String expFileUrl = "test1.txt";

	//模拟数据函数
	private ExpAmRoleManageDTO SimuDataFunc() {
		ExpAmRoleManageDTO earmDto = new ExpAmRoleManageDTO();
		//设置返回数据
		earmDto.setTime(BCDTimeUtil.convertNormalFrom(null));
		//列表元素设置
		ExpAmRoleManage expAmRoleManage = new ExpAmRoleManage();
		//赋值
		expAmRoleManage.setRoleName(roleName);
		expAmRoleManage.setRoleType(roleType);
		expAmRoleManage.setExpFileUrl(SpringConfig.repfile_srvdn+repFileResPath+expFileUrl);
		//设置模拟数据
		earmDto.setExpAmRoleManage(expAmRoleManage);
		//消息ID
		earmDto.setMsgId(AppModConfig.msgId);
		AppModConfig.msgId++;
		//消息id小于0判断
		AppModConfig.msgIdLessThan0Judge();

		return earmDto;
	}
	
	//生成导出EXCEL文件
	public boolean expAmRoleManageExcel(String pathFileName, List<AmRoleManage> dataList, String colNames[]) { 
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
			// 循环写入行数据
			startRowIdx++;
			for (int i = 0; i < dataList.size(); i++) {
				row = (Row) sheet.createRow(i + startRowIdx);row.createCell(0).setCellValue(i+1);
				row.createCell(1).setCellValue(dataList.get(i).getRoleName());                                           //角色
				row.createCell(2).setCellValue(dataList.get(i).getRoleType());                                           //类型
				row.createCell(3).setCellValue(dataList.get(i).getRoleDiscrip());                                        //描述
				row.createCell(4).setCellValue(dataList.get(i).getCreator());                                            //创建人
				row.createCell(5).setCellValue(dataList.get(i).getCreateTime());                                         //创建日期
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
	
	//导出角色管理模型函数
	public ExpAmRoleManageDTO appModFunc(String token, String roleName, String roleType, Db2Service db2Service, int[] codes) {
		ExpAmRoleManageDTO earmDto = null;
		if (isRealData) { // 真实数据
			String strCurPageNum = String.valueOf(curPageNum), strPageSize = String.valueOf(pageSize);
			AmRoleManageDTO armDto = armAppMod.appModFunc(roleName, roleType, strCurPageNum, strPageSize, db2Service, codes);
			if(armDto != null) {
				int i, totalCount = armDto.getPageInfo().getPageTotal();
				int pageCount = 0;
				List<AmRoleManage> expExcelList = new ArrayList<>();
				if(totalCount % pageSize == 0)
					pageCount = totalCount/pageSize;
				else
					pageCount = totalCount/pageSize + 1;
				//第一页数据
				if(armDto.getAmRoleManage() != null) {
					expExcelList.addAll(armDto.getAmRoleManage());
				}
				//后续页数据
				for(i = curPageNum+1; i <= pageCount; i++) {
					strCurPageNum = String.valueOf(i);
					AmRoleManageDTO curPdlDto = armAppMod.appModFunc(roleName, roleType, strCurPageNum, strPageSize, db2Service, codes);
					if(curPdlDto.getAmRoleManage() != null) {
						expExcelList.addAll(curPdlDto.getAmRoleManage());
					}
				}
				//生成导出EXCEL文件
				String repFileName = repFileResPath + UniqueIdGen.uuid() + SpringConfig.repFileFormats[SpringConfig.curRepFileFrmtIdx];
				String pathFileName = SpringConfig.base_dir + repFileName;
				logger.info("导出文件路径：" + pathFileName);
				boolean flag = expAmRoleManageExcel(pathFileName, expExcelList, colNames);
				if(flag) {
					//移动文件到其他目录
					//AppModConfig.moveFileToOtherFolder(pathFileName, SpringConfig.tomcatSrvDirs[1] + repFileResPath);
					earmDto = new ExpAmRoleManageDTO();
					ExpAmRoleManage expAmRoleManage = new ExpAmRoleManage();
					//时戳
					earmDto.setTime(BCDTimeUtil.convertNormalFrom(null));
					//导出信息
					expAmRoleManage.setRoleName(roleName);
					expAmRoleManage.setRoleType(roleType);
					expFileUrl = SpringConfig.repfile_srvdn + repFileName;
					logger.info("导出文件URL：" + expFileUrl);
					expAmRoleManage.setExpFileUrl(expFileUrl);
					earmDto.setExpAmRoleManage(expAmRoleManage);
					//消息ID
					earmDto.setMsgId(AppModConfig.msgId);
					AppModConfig.msgId++;
					// 消息id小于0判断
					AppModConfig.msgIdLessThan0Judge();
				}
			}
		} else { // 模拟数据
			// 模拟数据函数
			earmDto = SimuDataFunc();
		}

		return earmDto;
	}
}