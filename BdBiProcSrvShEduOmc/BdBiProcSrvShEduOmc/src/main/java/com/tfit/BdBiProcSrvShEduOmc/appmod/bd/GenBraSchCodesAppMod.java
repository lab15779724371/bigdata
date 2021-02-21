package com.tfit.BdBiProcSrvShEduOmc.appmod.bd;

import java.util.ArrayList;
import java.util.List;

import com.tfit.BdBiProcSrvShEduOmc.config.AppModConfig;
import com.tfit.BdBiProcSrvShEduOmc.dto.bd.GenBraSchCodesDTO;
import com.tfit.BdBiProcSrvShEduOmc.dto.NameCode;
import com.tfit.BdBiProcSrvShEduOmc.service.Db1Service;
import com.tfit.BdBiProcSrvShEduOmc.util.BCDTimeUtil;

//总分校编码列表应用模型
public class GenBraSchCodesAppMod {
	//是否为真实数据标识
	private static boolean isRealData = true;
	
	//数组数据初始化
	String[] name_Array = {"无", "总校", "分校"};
	String[] code_Array = {"0", "1", "2"};
		
	//模拟数据函数
	private GenBraSchCodesDTO SimuDataFunc() {
		GenBraSchCodesDTO gbscDto = new GenBraSchCodesDTO();
		//时戳
		gbscDto.setTime(BCDTimeUtil.convertNormalFrom(null));
		//总分校编码列表模拟数据
		List<NameCode> genBraSchCodes = new ArrayList<>();
		//赋值
		for (int i = 0; i < name_Array.length; i++) {
			NameCode gbsc = new NameCode();
			gbsc.setName(name_Array[i]);
			gbsc.setCode(code_Array[i]);			
			genBraSchCodes.add(gbsc);
		}
		//设置数据
		gbscDto.setGenBraSchCodes(genBraSchCodes);
		//消息ID
		gbscDto.setMsgId(AppModConfig.msgId);
		AppModConfig.msgId++;
		// 消息id小于0判断
		AppModConfig.msgIdLessThan0Judge();
		
		return gbscDto;
	}
	
	// 总分校编码列表模型函数
	public GenBraSchCodesDTO appModFunc(String distName, String prefCity, String province, Db1Service db1Service) {
		GenBraSchCodesDTO gbscDto = null;
		// 省或直辖市
		if(province == null)
			province = "上海市";
		if(isRealData) {       //真实数据
			gbscDto = new GenBraSchCodesDTO();
			//时戳
			gbscDto.setTime(BCDTimeUtil.convertNormalFrom(null));
			//总分校编码列表模拟数据
			List<NameCode> genBraSchCodes = new ArrayList<>();
			//赋值
			for (int i = 0; i < name_Array.length; i++) {
				NameCode gbsc = new NameCode();
				gbsc.setName(name_Array[i]);
				gbsc.setCode(code_Array[i]);
				genBraSchCodes.add(gbsc);
			}
			//设置数据
			gbscDto.setGenBraSchCodes(genBraSchCodes);
			//消息ID
			gbscDto.setMsgId(AppModConfig.msgId);
			AppModConfig.msgId++;
			// 消息id小于0判断
			AppModConfig.msgIdLessThan0Judge();
		}
		else {    //模拟数据
			//模拟数据函数
			gbscDto = SimuDataFunc();
		}		

		return gbscDto;
	}
}
