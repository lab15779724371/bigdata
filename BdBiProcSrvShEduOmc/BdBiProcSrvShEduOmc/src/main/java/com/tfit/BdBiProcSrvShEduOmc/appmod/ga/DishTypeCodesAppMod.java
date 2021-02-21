package com.tfit.BdBiProcSrvShEduOmc.appmod.ga;

import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.tfit.BdBiProcSrvShEduOmc.config.AppModConfig;
import com.tfit.BdBiProcSrvShEduOmc.dao.domain.TProCategoryDo;
import com.tfit.BdBiProcSrvShEduOmc.dto.ga.DishTypeCodesDTO;
import com.tfit.BdBiProcSrvShEduOmc.dto.NameCode;
import com.tfit.BdBiProcSrvShEduOmc.service.Db1Service;
import com.tfit.BdBiProcSrvShEduOmc.service.SaasService;
import com.tfit.BdBiProcSrvShEduOmc.util.BCDTimeUtil;

//菜品类别编码列表应用模型
public class DishTypeCodesAppMod {
	private static final Logger logger = LogManager.getLogger(DishTypeCodesAppMod.class.getName());
	
	//是否为真实数据标识
	private static boolean isRealData = true;
	
	//数组数据初始化
	String[] name_Array = {"大荤", "中荤", "小荤"};
	String[] code_Array = {"1", "2", "3"};
	
	//模拟数据函数
	private DishTypeCodesDTO SimuDataFunc() {
		DishTypeCodesDTO dtcDto = new DishTypeCodesDTO();
		//时戳
		dtcDto.setTime(BCDTimeUtil.convertNormalFrom(null));
		//菜品类别编码列表模拟数据
		List<NameCode> dishTypeCodes = new ArrayList<>();
		//赋值
		for (int i = 0; i < name_Array.length; i++) {
			NameCode dtc = new NameCode();
			dtc.setName(name_Array[i]);
			dtc.setCode(code_Array[i]);
			dishTypeCodes.add(dtc);
		}
		//设置数据
		dtcDto.setDishTypeCodes(dishTypeCodes);
		//消息ID
		dtcDto.setMsgId(AppModConfig.msgId);
		AppModConfig.msgId++;
		// 消息id小于0判断
		AppModConfig.msgIdLessThan0Judge();
		
		return dtcDto;
	}
	
	// 菜品类别编码列表模型函数
	public DishTypeCodesDTO appModFunc(String distName, String prefCity, String province, Db1Service db1Service, SaasService saasService) {
		DishTypeCodesDTO dtcDto = null;
		// 省或直辖市
		if(province == null)
			province = "上海市";
		if(isRealData) {       //真实数据
			List<TProCategoryDo> temtdList = saasService.getAllDishTypes2();
			if(temtdList != null) {
				dtcDto = new DishTypeCodesDTO();
				//时戳
				dtcDto.setTime(BCDTimeUtil.convertNormalFrom(null));
				//菜品类别编码列表模拟数据
				List<NameCode> dishTypeCodes = new ArrayList<>();
				//赋值
				for (int i = 0; i < temtdList.size(); i++) {
					NameCode dtc = new NameCode();
					dtc.setName(temtdList.get(i).getCategoryName());
					dtc.setCode(temtdList.get(i).getCategoryName());
					dishTypeCodes.add(dtc);
				}
				//设置数据
				dtcDto.setDishTypeCodes(dishTypeCodes);
				//消息ID
				dtcDto.setMsgId(AppModConfig.msgId);
				AppModConfig.msgId++;
				// 消息id小于0判断
				AppModConfig.msgIdLessThan0Judge();
			}
			else {
				logger.info("获取菜品类别编码列表失败！");
			}
		}
		else {    //模拟数据
			//模拟数据函数
			dtcDto = SimuDataFunc();
		}		

		return dtcDto;
	}
}
