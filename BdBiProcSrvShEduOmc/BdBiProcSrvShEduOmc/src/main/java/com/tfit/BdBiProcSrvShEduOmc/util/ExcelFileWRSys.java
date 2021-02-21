package com.tfit.BdBiProcSrvShEduOmc.util;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.tfit.BdBiProcSrvShEduOmc.appmod.im.ExpPpDishListAppMod;
import com.tfit.BdBiProcSrvShEduOmc.config.SpringConfig;
import com.tfit.BdBiProcSrvShEduOmc.dto.im.PpDishList;
  
//从excel读取数据/往excel中写入 excel有表头，表头每列的内容对应实体类的属性 
public class ExcelFileWRSys {  
	private static final Logger logger = LogManager.getLogger(ExpPpDishListAppMod.class.getName());
	
	public static boolean expPpDishListExcel(String pathFileName, List<PpDishList> dataList, String colNames[]) { 
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
			// 添加样式
			Row row = null;
			Cell cell = null;
			CellStyle style = wb.createCellStyle(); // 样式对象
			// 设置单元格的背景颜色为淡蓝色
			style.setFillForegroundColor(HSSFColor.PALE_BLUE.index);
			style.setVerticalAlignment(CellStyle.VERTICAL_CENTER); // 垂直
			style.setAlignment(CellStyle.ALIGN_CENTER); // 水平
			style.setWrapText(true); // 指定当单元格内容显示不下时自动换行
			Font font = wb.createFont();
			font.setBoldweight(Font.BOLDWEIGHT_BOLD);
			font.setFontName("宋体");
			font.setFontHeight((short) 280);
			style.setFont(font);
			// 创建第一行
			row = sheet.createRow(0);
			for (int i = 0; i < colNames.length; i++) {
				cell = row.createCell(i);
				cell.setCellValue(colNames[i]);
				cell.setCellStyle(style); // 样式，居中
				sheet.setColumnWidth(i, 20 * 256);
			}
			row.setHeight((short) 540);
			// 循环写入行数据
			for (int i = 0; i < dataList.size(); i++) {
				row = (Row) sheet.createRow(i + 2);
				row.setHeight((short) 500);
				row.createCell(0).setCellValue((dataList.get(i)).getDishDate());
				row.createCell(1).setCellValue((dataList.get(i)).getDistName());
				row.createCell(2).setCellValue((dataList.get(i)).getDishRate());
				row.createCell(3).setCellValue((dataList.get(i)).getDishSchNum());
				row.createCell(4).setCellValue((dataList.get(i)).getMealSchNum());
				row.createCell(5).setCellValue((dataList.get(i)).getNoDishSchNum());
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
					//wb.write(stream);
					ByteArrayOutputStream os = new ByteArrayOutputStream();wb.write(os);FtpUtil.ftpServer(pathFileName, os,null);
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
	
	public class InsuraceExcelBean {
		String insuraceUser;
		String bankCardId;
		String idCard;
		String buyTime;
		String insEndTime;
		String insStartTime;
		String money;
		String type;
		
		public String getInsuraceUser() {
			return insuraceUser;
		}
		public void setInsuraceUser(String insuraceUser) {
			this.insuraceUser = insuraceUser;
		}
		public String getBankCardId() {
			return bankCardId;
		}
		public void setBankCardId(String bankCardId) {
			this.bankCardId = bankCardId;
		}
		public String getIdCard() {
			return idCard;
		}
		public void setIdCard(String idCard) {
			this.idCard = idCard;
		}
		public String getBuyTime() {
			return buyTime;
		}
		public void setBuyTime(String buyTime) {
			this.buyTime = buyTime;
		}
		public String getInsEndTime() {
			return insEndTime;
		}
		public void setInsEndTime(String insEndTime) {
			this.insEndTime = insEndTime;
		}
		public String getInsStartTime() {
			return insStartTime;
		}
		public void setInsStartTime(String insStartTime) {
			this.insStartTime = insStartTime;
		}
		public String getMoney() {
			return money;
		}
		public void setMoney(String money) {
			this.money = money;
		}
		public String getType() {
			return type;
		}
		public void setType(String type) {
			this.type = type;
		}
	}
    
	public static void writer(String path, String fileName, String fileType, List<InsuraceExcelBean> list, String titleRow[]) throws IOException { 
		Workbook wb = null; 
        String excelPath = path+File.separator+fileName+"."+fileType;
        File file = new File(excelPath);
        Sheet sheet =null;
        //创建工作文档对象   
        if (!file.exists()) {
            if (fileType.equals("xls")) {
                wb = new HSSFWorkbook();                
            } else if(fileType.equals("xlsx")) {  
            	wb = new XSSFWorkbook();
            } else {
                //throw new SimpleException("文件格式不正确");
            }
            //创建sheet对象   
            sheet = (Sheet) wb.createSheet("sheet1");  
            OutputStream outputStream = new FileOutputStream(excelPath);
            wb.write(outputStream);
            outputStream.flush();
            outputStream.close();
            
        } 
        else {
            if (fileType.equals("xls")) {  
                wb = new HSSFWorkbook();                  
            } else if(fileType.equals("xlsx")) { 
                wb = new XSSFWorkbook();                  
            } else {  
                //throw new SimpleException("文件格式不正确");
            }  
        }
         //创建sheet对象   
        if (sheet==null) {
            sheet = (Sheet) wb.createSheet("sheet1");  
        }
        
        //添加表头  
        Row row = sheet.createRow(0);
        Cell cell = row.createCell(0);
        row.setHeight((short) 540); 
        cell.setCellValue("被保险人员清单");    //创建第一行            
        CellStyle style = wb.createCellStyle(); // 样式对象      
        // 设置单元格的背景颜色为淡蓝色  
        style.setFillForegroundColor(HSSFColor.PALE_BLUE.index);         
        style.setVerticalAlignment(CellStyle.VERTICAL_CENTER);// 垂直      
        style.setAlignment(CellStyle.ALIGN_CENTER);// 水平   
        style.setWrapText(true);// 指定当单元格内容显示不下时自动换行       
        cell.setCellStyle(style); // 样式，居中        
        Font font = wb.createFont();  
        font.setBoldweight(Font.BOLDWEIGHT_BOLD);  
        font.setFontName("宋体");  
        font.setFontHeight((short) 280);  
        style.setFont(font);  
        // 单元格合并      
        // 四个参数分别是：起始行，起始列，结束行，结束列      
        sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, 7));  
        sheet.autoSizeColumn(5200);
        
        row = sheet.createRow(1);    //创建第二行    
        for(int i = 0;i < titleRow.length;i++){  
            cell = row.createCell(i);  
            cell.setCellValue(titleRow[i]);  
            cell.setCellStyle(style); // 样式，居中
            sheet.setColumnWidth(i, 20 * 256); 
        }  
        row.setHeight((short) 540); 

        //循环写入行数据   
        for (int i = 0; i < list.size(); i++) {  
            row = (Row) sheet.createRow(i+2);  
            row.setHeight((short) 500); 
            row.createCell(0).setCellValue(( list.get(i)).getInsuraceUser());
            row.createCell(1).setCellValue(( list.get(i)).getIdCard());
            row.createCell(2).setCellValue(( list.get(i)).getType());
            row.createCell(3).setCellValue(( list.get(i)).getBankCardId());
            row.createCell(4).setCellValue(( list.get(i)).getMoney());
            row.createCell(5).setCellValue(( list.get(i)).getBuyTime());
            row.createCell(6).setCellValue(( list.get(i)).getInsStartTime());
            row.createCell(7).setCellValue(( list.get(i)).getInsEndTime());
        }  
        
        //创建文件流   
        OutputStream stream = new FileOutputStream(excelPath);  
        //写入数据   
        //wb.write(stream);  
        ByteArrayOutputStream os = new ByteArrayOutputStream();wb.write(os);FtpUtil.ftpServer(excelPath, os,null);
        //关闭文件流   
        stream.close();  
    }      
    
    //测试
    public static void test(String[] args) throws IOException {  
        String path = SpringConfig.base_dir + "/";  
        String fileName = "被保险人员清单(新增)04";  
        String fileType = "xlsx";  
        List<InsuraceExcelBean> list = new ArrayList<>();
        ExcelFileWRSys efwrs = new ExcelFileWRSys();
        for(int i=0; i<6; i++){
        	InsuraceExcelBean bean = efwrs.new InsuraceExcelBean();  
            bean.setInsuraceUser("test"+i);
            bean.setBankCardId("4444444444"+i+","+"55544444444444"+i+","+"999999999999999"+i);
            bean.setIdCard("666666"+i);
            bean.setBuyTime("2016-05-06");
            bean.setInsEndTime("2016-05-07");
            bean.setInsStartTime("2017-05-06");
            bean.setMoney("20,000");
            bean.setType("储蓄卡");            
            list.add(bean);
        }
        String title[] = {"被保险人姓名","身份证号","账户类型","银行卡号","保险金额(元)","购买时间","保单生效时间","保单失效时间"};  
//        createExcel("E:/被保险人员清单(新增).xlsx","sheet1",fileType,title);  
        
        writer(path, fileName, fileType, list, title);  
    }  
}