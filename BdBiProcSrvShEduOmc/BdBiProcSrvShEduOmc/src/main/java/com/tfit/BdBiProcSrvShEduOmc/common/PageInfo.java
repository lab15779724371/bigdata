package com.tfit.BdBiProcSrvShEduOmc.common;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

/**
 * 接口返回统一结构
 * @author Administrator
 *
 * @param <T>
 */
@JsonInclude(Include.ALWAYS)
public class PageInfo<T> implements Serializable {

    private static final long serialVersionUID = 7408790903212368997L;

    private int pageTotal;
    private int curPageNum;
    
    private T data;

    public PageInfo() {}

    public PageInfo(T obj) {
        this.data = obj;
    }
    
    public PageInfo(T obj,int curPageNum, int pageTotal) {
        this.curPageNum = curPageNum;
        this.pageTotal = pageTotal;
        this.data = obj;
    }

	public int getPageTotal() {
		return pageTotal;
	}

	public void setPageTotal(int pageTotal) {
		this.pageTotal = pageTotal;
	}

	public int getCurPageNum() {
		return curPageNum;
	}

	public void setCurPageNum(int curPageNum) {
		this.curPageNum = curPageNum;
	}

	public T getData() {
		return data;
	}

	public void setData(T data) {
		this.data = data;
	}


}