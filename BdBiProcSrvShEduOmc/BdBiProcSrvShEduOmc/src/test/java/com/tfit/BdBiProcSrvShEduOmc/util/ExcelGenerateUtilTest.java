package com.tfit.BdBiProcSrvShEduOmc.util;

import com.tfit.BdBiProcSrvShEduOmc.BaseTests;
import com.tfit.BdBiProcSrvShEduOmc.model.uo.TableUO;
import com.tfit.BdBiProcSrvShEduOmc.model.vo.RecoveryWasteOilSummaryVO;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

/**
 * @Descritpion：
 * @author: yanzhao_xu
 * @date: 2019/1/17 9:44
 */
public class ExcelGenerateUtilTest extends BaseTests {

    @Test
    public void generateSimpleExcelFile() {
        String fileName = "test3.xls";
        int columnCount = 3;

        List<RecoveryWasteOilSummaryVO> list = new ArrayList<>();

        RecoveryWasteOilSummaryVO oilsVO1 = new RecoveryWasteOilSummaryVO();
        oilsVO1.setCompDep("黄浦区");
        oilsVO1.setReFeq(1);
        oilsVO1.setRcNum(Float.valueOf("2.00"));
        list.add(oilsVO1);

        RecoveryWasteOilSummaryVO oilsVO2 = new RecoveryWasteOilSummaryVO();
        oilsVO2.setCompDep("静安区");
        oilsVO2.setReFeq(2);
        oilsVO2.setRcNum(Float.valueOf("4.00"));
        list.add(oilsVO2);

        RecoveryWasteOilSummaryVO oilsVO3 = new RecoveryWasteOilSummaryVO();
        oilsVO3.setCompDep("普陀区");
        oilsVO3.setReFeq(4);
        oilsVO3.setRcNum(Float.valueOf("8.00"));
        list.add(oilsVO3);

        RecoveryWasteOilSummaryVO oilsVO4 = new RecoveryWasteOilSummaryVO();
        oilsVO4.setCompDep("金山区");
        oilsVO4.setReFeq(8);
        oilsVO4.setRcNum(null);
        list.add(oilsVO4);


        TableUO tableUO = new TableUO(list.size(), columnCount);

        tableUO.setColumnNames(new String[]{"主管部门", "回收次数", "回收数量"});


        Object[][] data = new Object[list.size()][columnCount];

        for (int i = 0; i < list.size(); i++) {
            data[i][0] = list.get(i).getCompDep();
            data[i][1] = list.get(i).getReFeq();
            data[i][2] = list.get(i).getRcNum();
        }

        tableUO.setDataMatrix(data);

        ExcelGenerateUtil.generateSimpleExcelFile(fileName, tableUO);
    }

}