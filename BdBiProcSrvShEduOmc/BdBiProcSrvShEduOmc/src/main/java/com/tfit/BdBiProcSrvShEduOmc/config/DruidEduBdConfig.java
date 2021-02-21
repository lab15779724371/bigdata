package com.tfit.BdBiProcSrvShEduOmc.config;

import com.alibaba.druid.pool.DruidDataSource;
import com.tfit.BdBiProcSrvShEduOmc.appmod.bd.BdDishListAppMod;

import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;

import javax.sql.DataSource;

/**
 * @Descritpion：Edu库配置
 * @author: tianfang_infotech
 * @date: 2019/1/9 15:16
 */
@Configuration
@MapperScan(basePackages = DruidEduBdConfig.PACKAGE_SCAN, sqlSessionFactoryRef = "sqlSessionFactoryEduBd")
public class DruidEduBdConfig {

    public static final String PACKAGE_SCAN = "com.tfit.BdBiProcSrvShEduOmc.dao.mapper.edubd";
    public static final String MAPPER_LOCATION = "classpath:mapper/edubd/*.xml";
    private static final Logger logger = LogManager.getLogger(BdDishListAppMod.class.getName());
    @Autowired
    private DruidCommonConfig druidCommonConfig;

    @Bean
    @ConfigurationProperties(prefix = "custom.datasource.ds2")
    public DataSource dataSourceEduBd() {

        DruidDataSource dataSource = new DruidDataSource();

        if (dataSource.getMinEvictableIdleTimeMillis() > dataSource.getMaxEvictableIdleTimeMillis()) {
            /**
             * setMinEvictableIdleTimeMillis 需先设置
             */
            dataSource.setMinEvictableIdleTimeMillis(druidCommonConfig.getMinEvictableIdleTimeMillis());
            dataSource.setMaxEvictableIdleTimeMillis(druidCommonConfig.getMaxEvictableIdleTimeMillis());
        }
        
        logger.info("dataSourceEduBd:DataSource dataSourceEduBd"+dataSource.getUrl());
        return dataSource;
    }

    /**
     * 数据源事务管理器
     *
     * @return
     */
    @Bean
    public DataSourceTransactionManager dataSourceTransactionManagerEduBd() {

        return new DataSourceTransactionManager(dataSourceEduBd());
    }

    /**
     * 创建Session
     *
     * @return
     * @throws Exception
     */
    @Bean
    public SqlSessionFactory sqlSessionFactoryEduBd() throws Exception {

        final SqlSessionFactoryBean factoryBean = new SqlSessionFactoryBean();

        factoryBean.setDataSource(dataSourceEduBd());

        factoryBean.setMapperLocations(new PathMatchingResourcePatternResolver().getResources(MAPPER_LOCATION));

        logger.info("sqlSessionFactoryEduBd factoryBean:"+factoryBean);
        return factoryBean.getObject();
    }

}

