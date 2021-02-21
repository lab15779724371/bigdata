package com.tfit.BdBiProcSrvShEduOmc.dto;

public class PageInfo {

    public PageInfo() {

    }

    public PageInfo(Integer curPageNum, Integer pageTotal) {
        this.curPageNum = curPageNum;
        this.pageTotal = pageTotal;
    }

    Integer pageTotal;
    Integer curPageNum;

    public int getPageTotal() {
        return pageTotal;
    }

    public void setPageTotal(Integer pageTotal) {
        this.pageTotal = pageTotal;
    }

    public Integer getCurPageNum() {
        return curPageNum;
    }

    public void setCurPageNum(Integer curPageNum) {
        this.curPageNum = curPageNum;
    }
}
