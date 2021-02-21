package com.tfit.BdBiProcSrvShEduOmc.controller;

import javax.servlet.http.HttpServletRequest;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tfit.BdBiProcSrvShEduOmc.config.AppModConfig;
import com.tfit.BdBiProcSrvShEduOmc.dto.IOTHttpRspVO;

//运营维护
@RestController
@RequestMapping(value = "/biOptMain")
public class OptMainController {
	private static final Logger logger = LogManager.getLogger(OptMainController.class.getName());
	
	@Autowired
	ObjectMapper objectMapper;
	
	//运行监测
  	@RequestMapping(value = "/v1/srvMonitor",method = RequestMethod.GET)
  	public String v1_homeInfoStat(HttpServletRequest request) {
  		//初始化响应数据
  		IOTHttpRspVO normResp = AppModConfig.getNormalResp(null);
  		String strResp = null;
  		//设置响应数据
  		if(normResp != null) {
  			try {
  				strResp = objectMapper.writeValueAsString(normResp);
  				logger.info(strResp);
  			} catch (JsonProcessingException e) {
  				// TODO Auto-generated catch block
  				e.printStackTrace();
  			}
  		}
  		
  		return strResp;
  	}
}
