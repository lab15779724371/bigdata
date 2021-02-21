package com.tfit.BdBiProcSrvShEduOmc.appmod.im;

import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.tfit.BdBiProcSrvShEduOmc.config.AppModConfig;
import com.tfit.BdBiProcSrvShEduOmc.dao.domain.edu.TEduDistrictDo;
import com.tfit.BdBiProcSrvShEduOmc.dto.NameCode;
import com.tfit.BdBiProcSrvShEduOmc.dto.im.DistListDTO;
import com.tfit.BdBiProcSrvShEduOmc.service.Db1Service;
import com.tfit.BdBiProcSrvShEduOmc.service.Db2Service;
import com.tfit.BdBiProcSrvShEduOmc.util.BCDTimeUtil;

//区域列表应用模型
public class DistListAppMod {
	private static final Logger logger = LogManager.getLogger(DistListAppMod.class.getName());
	
	//是否为真实数据标识
	private static boolean isRealData = true;
	
	//数组数据初始化
	String[] name_Array = {"徐汇区", "黄浦区", "静安区", "长宁区", "普陀区", "虹口区", "杨浦区", "闵行区", "嘉定区", "宝山区", "浦东新区", "松江区", "金山区", "青浦区", "奉贤区", "崇明区"};
	String[] code_Array = {"11", "1", "10", "12", "13", "14", "15", "16", "2", "3", "4", "5", "6", "7", "8", "9"};
		
	//模拟数据函数
	private DistListDTO SimuDataFunc() {
		DistListDTO dilDto = new DistListDTO();
		//时戳
		dilDto.setTime(BCDTimeUtil.convertNormalFrom(null));
		//区域列表模拟数据
		List<NameCode> distList = new ArrayList<>();
		//赋值
		for (int i = 0; i < name_Array.length; i++) {
			NameCode dil = new NameCode();
			dil.setName(name_Array[i]);
			dil.setCode(code_Array[i]);
			distList.add(dil);
		}
		//设置数据
		dilDto.setDistList(distList);
		//消息ID
		dilDto.setMsgId(AppModConfig.msgId);
		AppModConfig.msgId++;
		// 消息id小于0判断
		AppModConfig.msgIdLessThan0Judge();
		
		return dilDto;
	}
	
	// 区域列表模型函数
	public DistListDTO appModFunc(String token, String prefCity, String province, Db1Service db1Service, Db2Service db2Service) {
		DistListDTO dilDto = null;
		String distIdorSCName = null;
		// 省或直辖市
		if(province == null)
			province = "上海市";
		if(distIdorSCName == null)
			//distIdorSCName = AppModConfig.getUserDataPermDistId(token, db1Service, db2Service);  //获取用户权限区域ID
		if(isRealData) {       //真实数据
			List<TEduDistrictDo> tddList = db1Service.getListByDs1IdName();
			if(tddList != null) {
				dilDto = new DistListDTO();
				//时戳
				dilDto.setTime(BCDTimeUtil.convertNormalFrom(null));
				//区域列表模拟数据
				List<NameCode> distList = new ArrayList<>();
				//赋值
				for (int i = 0; i < tddList.size(); i++) {
					// 判断是否按区域获取排菜数据（distIdorSCName为空表示按省或直辖市级别获取数据）
					if (distIdorSCName != null) {
						if (!tddList.get(i).getId().equalsIgnoreCase(distIdorSCName))
							continue;
					}
					NameCode dil = new NameCode();
					dil.setName(tddList.get(i).getName());
					dil.setCode(tddList.get(i).getId());
					distList.add(dil);
				}
				//设置数据
				dilDto.setDistList(distList);
				//消息ID
				dilDto.setMsgId(AppModConfig.msgId);
				AppModConfig.msgId++;
				// 消息id小于0判断
				AppModConfig.msgIdLessThan0Judge();
			}
			else {
				logger.info("获取区域列表失败！");
			}
		}
		else {    //模拟数据
			//模拟数据函数
			dilDto = SimuDataFunc();
		}		

		return dilDto;
	}
}
