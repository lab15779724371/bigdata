package com.tfit.BdBiProcSrvShEduOmc.appmod.ga;

import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.tfit.BdBiProcSrvShEduOmc.config.AppModConfig;
import com.tfit.BdBiProcSrvShEduOmc.dto.NameCode;
import com.tfit.BdBiProcSrvShEduOmc.dto.PageBean;
import com.tfit.BdBiProcSrvShEduOmc.dto.PageInfo;
import com.tfit.BdBiProcSrvShEduOmc.dto.ga.MatNameCodesDTO;
import com.tfit.BdBiProcSrvShEduOmc.obj.base.TBaseMaterial;
import com.tfit.BdBiProcSrvShEduOmc.service.Db1Service;
import com.tfit.BdBiProcSrvShEduOmc.service.DbHiveBaseService;
import com.tfit.BdBiProcSrvShEduOmc.util.BCDTimeUtil;

//物料名称编码列表应用模型
public class MatNameCodesAppMod {
	private static final Logger logger = LogManager.getLogger(MatNameCodesAppMod.class.getName());
	
	//是否为真实数据标识
	private static boolean isRealData = true;
	//页号、页大小和总页数
	int curPageNum = 1, pageTotal = 1, pageSize = 20, actPageSize = 0, attrCount = 9;
	
	//数组数据初始化
	String[] name_Array = {"土鸡蛋", "西红柿"};
	String[] code_Array = {"4f5de487-8913-40c8-bcce-7a538be4ec29", "5389de487-8913-40c8-bcce-7a538be4ec29"};
		
	//模拟数据函数
	private MatNameCodesDTO SimuDataFunc() {
		MatNameCodesDTO mncDto = new MatNameCodesDTO();
		//时戳
		mncDto.setTime(BCDTimeUtil.convertNormalFrom(null));
		//物料名称编码列表模拟数据
		List<NameCode> matNameCodes = new ArrayList<>();
		//赋值
		for (int i = 0; i < name_Array.length; i++) {
			NameCode mnc = new NameCode();
			mnc.setName(name_Array[i]);
			mnc.setCode(code_Array[i]);
			matNameCodes.add(mnc);
		}
		//设置数据
		mncDto.setMatNameCodes(matNameCodes);
		//消息ID
		mncDto.setMsgId(AppModConfig.msgId);
		AppModConfig.msgId++;
		// 消息id小于0判断
		AppModConfig.msgIdLessThan0Judge();
		
		return mncDto;
	}
	
	// 物料名称编码列表模型函数
	public MatNameCodesDTO appModFunc(String distName, String prefCity, String province, String page, String pageSize, Db1Service db1Service, DbHiveBaseService dbHiveBaseService) {
		MatNameCodesDTO mncDto = null;
		this.curPageNum = Integer.parseInt(page);
		this.pageSize = Integer.parseInt(pageSize);
		if(isRealData) {       //真实数据
			//所有物料名称
			TBaseMaterial inputObj= new TBaseMaterial();
			List<TBaseMaterial> tbmsDoList = dbHiveBaseService.getTBaseMaterialList(inputObj,null,null);
			
			if(tbmsDoList != null) {
				mncDto = new MatNameCodesDTO();
				//时戳
				mncDto.setTime(BCDTimeUtil.convertNormalFrom(null));
				//物料名称编码列表模拟数据
				List<NameCode> matNameCodes = new ArrayList<>();
				//赋值
				for (int i = 0; i < tbmsDoList.size(); i++) {
					NameCode mnc = new NameCode();
					mnc.setName(tbmsDoList.get(i).getMaterialName());
					mnc.setCode(tbmsDoList.get(i).getMaterialName());			
					matNameCodes.add(mnc);
				}
				// 分页
				PageBean<NameCode> pageBean = new PageBean<NameCode>(matNameCodes, curPageNum, this.pageSize);
				PageInfo pageInfo = new PageInfo();
				pageInfo.setPageTotal(pageBean.getTotalCount());
				pageInfo.setCurPageNum(curPageNum);
				mncDto.setPageInfo(pageInfo);
				//设置数据
				mncDto.setMatNameCodes(pageBean.getCurPageData());
				//消息ID
				mncDto.setMsgId(AppModConfig.msgId);
				AppModConfig.msgId++;
				// 消息id小于0判断
				AppModConfig.msgIdLessThan0Judge();
			}
			else {
				logger.info("获取物料名称编码数据失败！");
			}
		}
		else {    //模拟数据
			//模拟数据函数
			mncDto = SimuDataFunc();
		}		

		return mncDto;
	}
}
