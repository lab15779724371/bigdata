package com.tfit.BdBiProcSrvShEduOmc.model.uo;

import lombok.Data;

import java.io.Serializable;

/**
 * @Descritpion：表工具模型
 * @author: tianfang_infotech
 * @date: 2019/1/16 18:37
 */
@Data
public class TableUO implements Serializable {

    public TableUO(int rowCount, int columnCount) {
        this.rowCount = rowCount;
        this.columnCount = columnCount;
        this.columnNames = new String[columnCount];
        this.dataMatrix = new Object[rowCount][columnCount];
    }

    /**
     * 列数
     */
    private int columnCount;

    /**
     * 行数
     */
    private int rowCount;

    /**
     * 表列数据
     */
    private String[] columnNames;

    /**
     * 表行数据
     */
    private Object[][] dataMatrix;

	public int getColumnCount() {
		return columnCount;
	}

	public void setColumnCount(int columnCount) {
		this.columnCount = columnCount;
	}

	public int getRowCount() {
		return rowCount;
	}

	public void setRowCount(int rowCount) {
		this.rowCount = rowCount;
	}

	public String[] getColumnNames() {
		return columnNames;
	}

	public void setColumnNames(String[] columnNames) {
		this.columnNames = columnNames;
	}

	public Object[][] getDataMatrix() {
		return dataMatrix;
	}

	public void setDataMatrix(Object[][] dataMatrix) {
		this.dataMatrix = dataMatrix;
	}
}
