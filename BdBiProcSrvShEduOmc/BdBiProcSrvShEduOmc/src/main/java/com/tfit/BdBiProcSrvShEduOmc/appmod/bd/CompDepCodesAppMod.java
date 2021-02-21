package com.tfit.BdBiProcSrvShEduOmc.appmod.bd;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.tfit.BdBiProcSrvShEduOmc.config.AppModConfig;
import com.tfit.BdBiProcSrvShEduOmc.dto.NameCode;
import com.tfit.BdBiProcSrvShEduOmc.dto.bd.CompDepCodesDTO;
import com.tfit.BdBiProcSrvShEduOmc.service.Db1Service;
import com.tfit.BdBiProcSrvShEduOmc.util.BCDTimeUtil;
import com.tfit.BdBiProcSrvShEduOmc.util.CommonUtil;
import com.tfit.BdBiProcSrvShEduOmc.util.SortList;

//主管部门编码列表应用模型
public class CompDepCodesAppMod {
	//是否为真实数据标识
	private static boolean isRealData = true;
	
	//数组数据初始化
	String[] name_Array0 = {"其他"};
	String[] code_Array0 = {"0"};
	String[] name_Array1 = {"其他", "教育部"};
	String[] code_Array1 = {"0", "1"};
	String[] name_Array2 = {"其他", "市水务局（海洋局）", "市农委", "市交通委", "市科委", "市商务委", "市经信委", "市教委"};
	String[] code_Array2 = {"0", "1", "2", "3", "4", "5", "6", "7"};
	String[] name_Array3 = {"黄浦区教育局", "静安区教育局", "徐汇区教育局", "长宁区教育局", "普陀区教育局", "虹口区教育局", "杨浦区教育局", "闵行区教育局", "嘉定区教育局", 
			"宝山区教育局", "浦东新区教育局", "松江区教育局", "金山区教育局", "青浦区教育局", "奉贤区教育局", "崇明区教育局"};
	String[] code_Array3 = {"e6ee4acf-2c5b-11e6-b1e8-005056a5ed30", "e6ee4bd5-2c5b-11e6-b1e8-005056a5ed30",
			"e6ee4c4f-2c5b-11e6-b1e8-005056a5ed30", "e6ee4cb2-2c5b-11e6-b1e8-005056a5ed30", "e6ee4d17-2c5b-11e6-b1e8-005056a5ed30",
			"e6ee4d78-2c5b-11e6-b1e8-005056a5ed30", "e6ee4dd1-2c5b-11e6-b1e8-005056a5ed30", "e6ee4e3f-2c5b-11e6-b1e8-005056a5ed30",
			"e6ee4e97-2c5b-11e6-b1e8-005056a5ed30", "e6ee4eec-2c5b-11e6-b1e8-005056a5ed30", "e6ee4f43-2c5b-11e6-b1e8-005056a5ed30", 
			"e6ee4fa4-2c5b-11e6-b1e8-005056a5ed30", "e6ee4ffa-2c5b-11e6-b1e8-005056a5ed30", "e6ee5054-2c5b-11e6-b1e8-005056a5ed30", 
			"e6ee50ac-2c5b-11e6-b1e8-005056a5ed30", "e6ee5101-2c5b-11e6-b1e8-005056a5ed30"};
		
	//模拟数据函数
	private CompDepCodesDTO SimuDataFunc() {
		CompDepCodesDTO cdcDto = new CompDepCodesDTO();
		//时戳
		cdcDto.setTime(BCDTimeUtil.convertNormalFrom(null));
		//主管部门编码列表模拟数据
		List<NameCode> compDepCodes = new ArrayList<>();
		//赋值
		for (int i = 0; i < name_Array2.length; i++) {
			NameCode cdc = new NameCode();
			cdc.setName(name_Array2[i]);
			cdc.setCode(code_Array2[i]);			
			compDepCodes.add(cdc);
		}
		//设置数据
		cdcDto.setCompDepCodes(compDepCodes);
		//消息ID
		cdcDto.setMsgId(AppModConfig.msgId);
		AppModConfig.msgId++;
		// 消息id小于0判断
		AppModConfig.msgIdLessThan0Judge();
		
		return cdcDto;
	}
	
	// 主管部门编码列表模型函数
	public CompDepCodesDTO appModFunc(String distName, String prefCity, String province, String subLevel,String subLevels, Db1Service db1Service) {
		CompDepCodesDTO cdcDto = null;
		// 省或直辖市
		if(province == null)
			province = "上海市";
		if(isRealData) {       //真实数据
			cdcDto = new CompDepCodesDTO();
			int curSubLevel = -1;
			if(subLevel != null)
				curSubLevel = Integer.parseInt(subLevel);
			//时戳
			cdcDto.setTime(BCDTimeUtil.convertNormalFrom(null));
			//主管部门编码列表模拟数据
			List<NameCode> compDepCodes = new ArrayList<>();
			//赋值
			if(curSubLevel != -1) {
				getCompDepList(curSubLevel, compDepCodes);
			}else if (StringUtils.isNotEmpty(subLevels)) {
				List<Object> subLevelList = CommonUtil.changeStringToList(subLevels);
				if(subLevelList!=null && subLevelList.size()>0) {
					for(Object subLevelTemp: subLevelList) {
						if(subLevelTemp != null && StringUtils.isNumeric(subLevelTemp.toString())) {
							curSubLevel = Integer.parseInt(subLevelTemp.toString());
							getCompDepList(curSubLevel, compDepCodes);
						}
					}
				}
			}
	    	//排序
	    	SortList<NameCode> sortList = new SortList<NameCode>();  
	    	sortList.Sort(compDepCodes, "getCode", "desc");
			//设置数据
			cdcDto.setCompDepCodes(compDepCodes);
			//消息ID
			cdcDto.setMsgId(AppModConfig.msgId);
			AppModConfig.msgId++;
			// 消息id小于0判断
			AppModConfig.msgIdLessThan0Judge();
		}
		else {    //模拟数据
			//模拟数据函数
			cdcDto = SimuDataFunc();
		}		

		return cdcDto;
	}

	/**
	 * 根据所属获取对应的主管部门集合
	 * @param curSubLevel
	 * @param compDepCodes
	 */
	private void getCompDepList(int curSubLevel, List<NameCode> compDepCodes) {
		if(curSubLevel == 0) {
			for(String key : AppModConfig.compDepIdToNameMap0.keySet()) {
				NameCode cdc = new NameCode();
				cdc.setName(AppModConfig.compDepIdToNameMap0.get(key));
				cdc.setCode(key);
				cdc.setDoubleCode(curSubLevel+"_"+key);
				cdc.setDoubleName(AppModConfig.subLevelIdToNameMap.get(curSubLevel)+"-"+cdc.getName());
				compDepCodes.add(cdc);
			}
		}
		else if(curSubLevel == 1) {
			for(String key : AppModConfig.compDepIdToNameMap1.keySet()) {
				NameCode cdc = new NameCode();
				cdc.setName(AppModConfig.compDepIdToNameMap1.get(key));
				cdc.setCode(key);
				cdc.setDoubleCode(curSubLevel+"_"+key);
				cdc.setDoubleName(AppModConfig.subLevelIdToNameMap.get(curSubLevel)+"-"+cdc.getName());
				compDepCodes.add(cdc);
			}
		}
		else if(curSubLevel == 2) {
			for(String key : AppModConfig.compDepIdToNameMap2.keySet()) {
				NameCode cdc = new NameCode();
				cdc.setName(AppModConfig.compDepIdToNameMap2.get(key));
				cdc.setCode(key);
				cdc.setDoubleCode(curSubLevel+"_"+key);
				cdc.setDoubleName(AppModConfig.subLevelIdToNameMap.get(curSubLevel)+"-"+cdc.getName());
				compDepCodes.add(cdc);
			}
		}
		else if(curSubLevel == 3) {
			for(String key : AppModConfig.compDepIdToNameMap3.keySet()) {
				NameCode cdc = new NameCode();
				cdc.setName(AppModConfig.compDepIdToNameMap3.get(key));
				cdc.setCode(key);
				cdc.setDoubleCode(curSubLevel+"_"+key);
				cdc.setDoubleName(AppModConfig.subLevelIdToNameMap.get(curSubLevel)+"-"+cdc.getName());
				compDepCodes.add(cdc);
			}
		}
	}
}
