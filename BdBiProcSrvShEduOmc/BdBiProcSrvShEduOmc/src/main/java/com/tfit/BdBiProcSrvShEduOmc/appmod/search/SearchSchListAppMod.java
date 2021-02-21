package com.tfit.BdBiProcSrvShEduOmc.appmod.search;

import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.tfit.BdBiProcSrvShEduOmc.config.AppModConfig;
import com.tfit.BdBiProcSrvShEduOmc.dao.domain.edu.TEduDistrictDo;
import com.tfit.BdBiProcSrvShEduOmc.dto.PageInfo;
import com.tfit.BdBiProcSrvShEduOmc.dto.search.SearchSch;
import com.tfit.BdBiProcSrvShEduOmc.dto.search.SearchSchListDTO;
import com.tfit.BdBiProcSrvShEduOmc.service.Db1Service;
import com.tfit.BdBiProcSrvShEduOmc.service.Db2Service;
import com.tfit.BdBiProcSrvShEduOmc.service.DbHiveService;
import com.tfit.BdBiProcSrvShEduOmc.util.BCDTimeUtil;

//一键追溯-追溯详情应用模型
public class SearchSchListAppMod {
	private static final Logger logger = LogManager.getLogger(SearchSchListAppMod.class.getName());
	
	//二级排序条件
	final String[] methods = {"getDistName", "getSchType"};
	final String[] sorts = {"asc", "asc"};
	final String[] dataTypes = {"String", "String"};
	
	//是否为真实数据标识
	private static boolean isRealData = true;
	//页号、页大小和总页数
	int curPageNum = 1, pageTotal = 1, pageSize = 20, actPageSize = 0, attrCount = 14;
	
	//获取证件索引，0对应食品经营许可证、1对应餐饮服务许可证
	int getLicIndex(String[] keyVals) {
		int i, index = 0;
		i = AppModConfig.getVarValIndex(keyVals, "slictype");
		if(i != -1) {
			if(!keyVals[i].equalsIgnoreCase("null")) 
				index = 0;
			else
				index = 1;
		}
		
		return index;
	}
	
	// 基础数据学校列表函数
	SearchSchListDTO searchSchList(String distIdorSCName,String schName,DbHiveService dbHiveService) {
		SearchSchListDTO bslDto = new SearchSchListDTO();
		List<SearchSch> searchSchList = new ArrayList<>();
		
		searchSchList = dbHiveService.getSchList(schName, distIdorSCName,null,null, (curPageNum-1)*pageSize, curPageNum*pageSize);
		Integer distCount = dbHiveService.getSchCount(schName, distIdorSCName,null,null);
		bslDto.setDistCount(distCount);
    	//排序
    	//SortList<SearchSchList> sortList = new SortList<SearchSchList>();  
    	//sortList.Sort(searchSchList, methods, sorts, dataTypes);
		//时戳
		bslDto.setTime(BCDTimeUtil.convertNormalFrom(null));
		// 分页
		/*PageBean<SearchSch> pageBean = new PageBean<SearchSch>(searchSchList, curPageNum, pageSize);
		PageInfo pageInfo = new PageInfo();
		pageInfo.setPageTotal(pageBean.getTotalCount());
		pageInfo.setCurPageNum(curPageNum);
		bslDto.setPageInfo(pageInfo);
		//设置数据
		bslDto.setSearchSchList(pageBean.getCurPageData());*/
		
		PageInfo pageInfo = new PageInfo();
		pageInfo.setPageTotal(searchSchList == null ?0:searchSchList.size());
		pageInfo.setCurPageNum(1);
		bslDto.setPageInfo(pageInfo);
		//设置数据
		bslDto.setSearchSchList(searchSchList);
		
		//消息ID
		bslDto.setMsgId(AppModConfig.msgId);
		AppModConfig.msgId++;
		// 消息id小于0判断
		AppModConfig.msgIdLessThan0Judge();
		
		return bslDto;
	}	
	
	// 基础数据学校列表模型函数
	public SearchSchListDTO appModFunc(String token, String schName, String distName, String prefCity, 
			String province, String page, String pageSize,Db1Service db1Service,Db2Service db2Service,DbHiveService dbHiveService) {
		SearchSchListDTO bslDto = null;
		if(page != null)
			curPageNum = Integer.parseInt(page);
		if(pageSize != null)
			this.pageSize = Integer.parseInt(pageSize);
		if(isRealData) {       //真实数据
			// 省或直辖市
			if(province == null)
				province = "上海市";  		
			// 参数查找标识
			boolean bfind = false;
			String distIdorSCName = null;
			// 按不同参数形式处理
			if (distName != null && prefCity == null && province != null) {    // 按区域，省或直辖市处理
				List<TEduDistrictDo> tddList = db1Service.getListByDs1IdName();
				// 查找是否存在该区域和省市
				for (int i = 0; i < tddList.size(); i++) {
					TEduDistrictDo curTdd = tddList.get(i);
					if (curTdd.getId().compareTo(distName) == 0) {
						bfind = true;
						distIdorSCName = curTdd.getId();
						break;
					}
				}
				// 存在则获取数据
				if (bfind) {
					if(distIdorSCName == null)
						distIdorSCName = AppModConfig.getUserDataPermDistId(token, db1Service, db2Service);  //获取用户权限区域ID
					// 基础数据学校列表函数
					bslDto = searchSchList(distIdorSCName, schName, dbHiveService);		
				}
			} else if (distName == null && prefCity == null && province != null) { // 按省或直辖市处理
				if (province.compareTo("上海市") == 0) {
					bfind = true;
					distIdorSCName = null;
				}
				if (bfind) {
					if(distIdorSCName == null)
						distIdorSCName = AppModConfig.getUserDataPermDistId(token, db1Service, db2Service);  //获取用户权限区域ID
					// 基础数据学校列表函数
					bslDto = searchSchList(distIdorSCName, schName, dbHiveService);	
				}
			} else if (distName != null && prefCity != null && province != null) { // 按区域，地级市，省或直辖市处理

			} else if (distName == null && prefCity != null && province != null) { // 地级市，省或直辖市处理

			} else {
				logger.info("访问接口参数非法！");
			}															
		}
		else {    //模拟数据
			//模拟数据函数
			//bslDto = SimuDataFunc();
		}		

		return bslDto;
	}
}
