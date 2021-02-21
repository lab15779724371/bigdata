package com.tfit.BdBiProcSrvShEduOmc.dto;

import java.util.ArrayList;
import java.util.List;

//描述：动态分页Bean
public class PageBean <T>{
	private List<T> allPageData;
	private List<T> curPageData;
	private Integer curPageNum = Integer.valueOf(1);
	private Integer pageSize = Integer.valueOf(20);
	private Integer totalCount;

	public int getPageCount() {
		if (this.totalCount.intValue() % this.pageSize.intValue() == 0) {
			return this.totalCount.intValue() / this.pageSize.intValue();
		}
		return this.totalCount.intValue() / this.pageSize.intValue() + 1;
	}
	
	//初始化所有页数据
	public PageBean() {
		
	}

	//初始化所有页数据
	public PageBean(List<T> allPageData) {
		this.allPageData = allPageData;
		this.totalCount = allPageData.size();
	}
	
	//初始化所有页数据
	public PageBean(List<T> allPageData, int page, int pageSize) {
		this.allPageData = allPageData;
		this.totalCount = allPageData.size();
		this.curPageNum = page;
		this.pageSize = pageSize;
	}

	//获取所有页数据
	public List<T> getAllPageData() {
		return this.allPageData;
	}
	
	//设置所有页数据
	public void setAllPageData(List<T> allPageData) {
		this.allPageData = allPageData;
		this.totalCount = allPageData.size();
	}
	
	//设置所有页数据
	public void setAllPageData(List<T> allPageData, int page, int pageSize) {
		this.allPageData = allPageData;
		this.totalCount = allPageData.size();
		this.curPageNum = page;
		this.pageSize = pageSize;
	}

	//获取页大小
	public Integer getPageSize() {
		return this.pageSize;
	}

	//设置页大小
	public void setPageSize(Integer pageSize) {
		this.pageSize = pageSize;
	}

	//获取总数据条数
	public Integer getTotalCount() {
		return this.totalCount;
	}

	//设置总数据条数
	public void setTotalCount(Integer totalCount) {
		this.totalCount = totalCount;
	}

	//获取当前页数据
	public List<T> getCurPageData() {
		curPageData = null;
		if((curPageNum-1)*pageSize < allPageData.size()) {
			curPageData = new ArrayList<T>();
			for(int i = (curPageNum-1)*pageSize; i < allPageData.size() && i < curPageNum*pageSize; i++) {
				T t = allPageData.get(i);
				curPageData.add(t);
			}
		}
		return curPageData;
	}
	
	//获取下一页数据
	public List<T> getNextPage() {
		this.curPageNum++;
		if (this.curPageNum.intValue() >= getPageCount()) {
			this.curPageNum = Integer.valueOf(getPageCount());
		}
		return getCurPageData();
	}

	//获取前一页数据
	public List<T> getPrevPage() {
		this.curPageNum--;
		if (this.curPageNum.intValue() <= 1) {
			this.curPageNum = Integer.valueOf(1);
		}
		return getCurPageData();
	}
	
	//按页号（从1开始）获取当前页数据
	public List<T> getCurPageData(int page) {
		curPageNum = page;		
		return getCurPageData();
	}

	//获取当前页号
	public Integer getCurPageNum() {
		return curPageNum;
	}

	//设置当前页号
	public void setCurPageNum(Integer curPageNum) {
		this.curPageNum = curPageNum;
	}	
}
