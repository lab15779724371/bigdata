package com.tfit.BdBiProcSrvShEduOmc;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableScheduling;

import com.tfit.BdBiProcSrvShEduOmc.config.SpringConfig;

@ComponentScan("com.tfit.BdBiProcSrvShEduOmc")
@MapperScan("com.tfit.BdBiProcSrvShEduOmc.dao")
@SpringBootApplication
@EnableScheduling
public class BdBiProcSrvShEduOmcApp 
{
	public static void main( String[] args )
    {
        SpringApplication.run(BdBiProcSrvShEduOmcApp.class, args);
        //运行配置
        SpringConfig.RunConfig();
    }
}
