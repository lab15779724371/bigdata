package com.tfit.BdBiProcSrvShEduOmc.appmod.im;

import java.util.ArrayList;
import java.util.List;

import com.tfit.BdBiProcSrvShEduOmc.config.AppModConfig;
import com.tfit.BdBiProcSrvShEduOmc.dao.domain.TEduBdUserDo;
import com.tfit.BdBiProcSrvShEduOmc.dto.NameCode;
import com.tfit.BdBiProcSrvShEduOmc.dto.im.SchSelModeCodesDTO;
import com.tfit.BdBiProcSrvShEduOmc.service.Db1Service;
import com.tfit.BdBiProcSrvShEduOmc.service.Db2Service;
import com.tfit.BdBiProcSrvShEduOmc.util.BCDTimeUtil;
import com.tfit.BdBiProcSrvShEduOmc.util.CommonUtil;

//学校筛选模式编码列表应用模型
public class SchSelModeCodesAppMod {
	// 是否为真实数据标识
	private static boolean isRealData = true;

	// 数组数据初始化
	String[] sjw_name_Array = { "按管理部门","按主管部门", "按所在地" };
	String[] sjw_code_Array = { "2","0", "1" };
	String[] are_name_Array = { "按管理部门","按所在地" };
	String[] are_code_Array = { "2","1" };
	
	String[] name_Array = { "按所在地" };
	String[] code_Array = { "1" };
	
	// 模拟数据函数
	private SchSelModeCodesDTO SimuDataFunc() {
		SchSelModeCodesDTO ssmcDto = new SchSelModeCodesDTO();
		// 时戳
		ssmcDto.setTime(BCDTimeUtil.convertNormalFrom(null));
		// 学校筛选模式编码列表模拟数据
		List<NameCode> schSelModeCodes = new ArrayList<>();
		// 赋值
		for (int i = 0; i < name_Array.length; i++) {
			NameCode ssmc = new NameCode();
			ssmc.setName(name_Array[i]);
			ssmc.setCode(code_Array[i]);
			schSelModeCodes.add(ssmc);
		}
		// 设置数据
		ssmcDto.setSchSelModeCodes(schSelModeCodes);
		// 消息ID
		ssmcDto.setMsgId(AppModConfig.msgId);
		AppModConfig.msgId++;
		// 消息id小于0判断
		AppModConfig.msgIdLessThan0Judge();

		return ssmcDto;
	}

	// 学校筛选模式编码列表模型函数
	public SchSelModeCodesDTO appModFunc(String token,String distName, String prefCity, String province,String mode,Db1Service db1Service,Db2Service db2Service) {
		SchSelModeCodesDTO ssmcDto = null;
		// 省或直辖市
		if (province == null)
			province = "上海市";
		if (isRealData) { // 真实数据
			
			if(CommonUtil.isNotEmpty(mode) && "1".equals(mode)) {
				name_Array = are_name_Array;
				code_Array = are_code_Array;
			}else {
				TEduBdUserDo tebuDo = db2Service.getBdUserInfoByToken(token);
				if(tebuDo.getOrgName().equals("市教委")) {
					name_Array = sjw_name_Array;
					code_Array = sjw_code_Array;
				}else {
					name_Array = are_name_Array;
					code_Array = are_code_Array;
				}
			}
			ssmcDto = new SchSelModeCodesDTO();
			// 时戳
			ssmcDto.setTime(BCDTimeUtil.convertNormalFrom(null));
			// 学校筛选模式编码列表模拟数据
			List<NameCode> schSelModeCodes = new ArrayList<>();
			// 赋值
			for (int i = 0; i < name_Array.length; i++) {
				NameCode ssmc = new NameCode();
				ssmc.setName(name_Array[i]);
				ssmc.setCode(code_Array[i]);
				schSelModeCodes.add(ssmc);
			}
			// 设置数据
			ssmcDto.setSchSelModeCodes(schSelModeCodes);
			// 消息ID
			ssmcDto.setMsgId(AppModConfig.msgId);
			AppModConfig.msgId++;
			// 消息id小于0判断
			AppModConfig.msgIdLessThan0Judge();
		} else { // 模拟数据
			// 模拟数据函数
			ssmcDto = SimuDataFunc();
		}

		return ssmcDto;
	}
}