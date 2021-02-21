package com.tfit.BdBiProcSrvShEduOmc.util.export;

import java.io.OutputStream;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletResponse;

import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;

@SuppressWarnings("hiding")
public class ExportExcel<T> {
    /**
     * 导出多张excel表,解决xls格式行数65535的限制
     * @author OnlyOne
     * @param response
     * @param list 需要处理的list数据集合
     * @throws Exception
     */
    @SuppressWarnings("deprecation")
    public void doExcel(HttpServletResponse response,List<T> list,String fileName) throws Exception {
         OutputStream os = response.getOutputStream();//获取输出流 
        //response.reset();
        // 设置下载头部信息。Content-disposition为属性名。attachment表示以附件方式下载，如果要在页面中打开，则改为inline。filename为文件名
       // response.setHeader("Content-disposition", "attachment; filename=excell.xls");
        //response.setContentType("application/msexcel");
         
        Map<Integer, List<T>> sheetMap = RelectUtil.<T>daData(list);
        HSSFWorkbook wb = new HSSFWorkbook();
        Set<Integer> keys = sheetMap.keySet();
        for (Iterator<Integer> iterator = keys.iterator(); iterator.hasNext();) {
            Integer SheetKey = iterator.next();
            HSSFSheet sheet = wb.createSheet((fileName+SheetKey).toString());
            List<T> sheetRows = sheetMap.get(SheetKey);
            for (int i = 0, len = sheetRows.size(); i < len; i++) {
                T en = (T) sheetRows.get(i);
                List<Object> dataList = RelectUtil.<T>reflectEntity(en, en.getClass());
                HSSFRow row = sheet.createRow(i);
                row.createCell(0).setCellValue(String.valueOf(i));
                for(int m=0; m<dataList.size(); m++){
                    row.createCell(m+1).setCellValue(dataList.get(m).toString());
                }
            }
        }
        wb.write(os);
    } 
    
}