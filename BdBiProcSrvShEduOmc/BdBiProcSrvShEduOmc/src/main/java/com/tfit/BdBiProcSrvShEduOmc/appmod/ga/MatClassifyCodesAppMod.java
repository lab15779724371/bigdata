package com.tfit.BdBiProcSrvShEduOmc.appmod.ga;

import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.tfit.BdBiProcSrvShEduOmc.config.AppModConfig;
import com.tfit.BdBiProcSrvShEduOmc.dao.domain.TBaseMaterialTypeDo;
import com.tfit.BdBiProcSrvShEduOmc.dto.ga.MatClassifyCodesDTO;
import com.tfit.BdBiProcSrvShEduOmc.dto.NameCode;
import com.tfit.BdBiProcSrvShEduOmc.service.Db1Service;
import com.tfit.BdBiProcSrvShEduOmc.service.SaasService;
import com.tfit.BdBiProcSrvShEduOmc.util.BCDTimeUtil;

//物料分类编码列表应用模型
public class MatClassifyCodesAppMod {
	private static final Logger logger = LogManager.getLogger(MatClassifyCodesAppMod.class.getName());
	
	//是否为真实数据标识
	private static boolean isRealData = true;
	
	//数组数据初始化
	String[] name_Array = {"蛋类产品", "蔬菜（木本植物）", "兽类、禽类和爬行类动物肉产品"};
	String[] code_Array = {"0", "1", "2"};
	
	//模拟数据函数
	private MatClassifyCodesDTO SimuDataFunc() {
		MatClassifyCodesDTO mccDto = new MatClassifyCodesDTO();
		//时戳
		mccDto.setTime(BCDTimeUtil.convertNormalFrom(null));
		//物料分类编码列表模拟数据
		List<NameCode> matClassifyCodes = new ArrayList<>();
		//赋值
		for (int i = 0; i < name_Array.length; i++) {
			NameCode mcc = new NameCode();
			mcc.setName(name_Array[i]);
			mcc.setCode(code_Array[i]);
			matClassifyCodes.add(mcc);
		}
		//设置数据
		mccDto.setMatClassifyCodes(matClassifyCodes);
		//消息ID
		mccDto.setMsgId(AppModConfig.msgId);
		AppModConfig.msgId++;
		// 消息id小于0判断
		AppModConfig.msgIdLessThan0Judge();
		
		return mccDto;
	}
	
	// 物料分类编码列表模型函数
	public MatClassifyCodesDTO appModFunc(String distName, String prefCity, String province, Db1Service db1Service, SaasService saasService) {
		MatClassifyCodesDTO mccDto = null;
		// 省或直辖市
		if(province == null)
			province = "上海市";
		if(isRealData) {       //真实数据
			List<TBaseMaterialTypeDo> tbmtList = saasService.getAllMatClassifyIdName();
			if(tbmtList != null) {
				mccDto = new MatClassifyCodesDTO();
				//时戳
				mccDto.setTime(BCDTimeUtil.convertNormalFrom(null));
				//物料分类编码列表模拟数据
				List<NameCode> matClassifyCodes = new ArrayList<>();
				//赋值
				for (int i = 0; i < tbmtList.size(); i++) {
					NameCode mcc = new NameCode();
					mcc.setName(tbmtList.get(i).getName());
					mcc.setCode(String.valueOf(tbmtList.get(i).getId()));
					matClassifyCodes.add(mcc);
				}
				//设置数据
				mccDto.setMatClassifyCodes(matClassifyCodes);
				//消息ID
				mccDto.setMsgId(AppModConfig.msgId);
				AppModConfig.msgId++;
				// 消息id小于0判断
				AppModConfig.msgIdLessThan0Judge();
			}
			else {
				logger.info("获取物料分类编码列表失败！");
			}
		}
		else {    //模拟数据
			//模拟数据函数
			mccDto = SimuDataFunc();
		}		

		return mccDto;
	}
}
