package com.tfit.BdBiProcSrvShEduOmc.controller;

import javax.servlet.http.HttpServletRequest;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

//check程序
@RestController
@RequestMapping(value = "/BdBiProcSrvShEduOmc")
public class CheckController {
  	//用于检查程序是否正常运行
  	@RequestMapping(value = "/healthCheck",method = RequestMethod.GET)
  	public String healthCheck(HttpServletRequest request)
  	{
  		return "Succee";
  	}
  	
  	//用于检查程序是否正常运行
  	@RequestMapping(value = "/offline",method = RequestMethod.GET)
  	public String offline(HttpServletRequest request)
  	{
  		return "Succee";
  	}
}