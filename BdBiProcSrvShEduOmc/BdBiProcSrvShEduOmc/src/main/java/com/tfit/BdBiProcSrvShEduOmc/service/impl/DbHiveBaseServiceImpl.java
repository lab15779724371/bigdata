package com.tfit.BdBiProcSrvShEduOmc.service.impl;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import javax.sql.DataSource;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowCallbackHandler;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.tfit.BdBiProcSrvShEduOmc.obj.base.TBaseMaterial;
import com.tfit.BdBiProcSrvShEduOmc.service.DbHiveBaseService;
import com.tfit.BdBiProcSrvShEduOmc.util.BCDTimeUtil;
import com.tfit.BdBiProcSrvShEduOmc.util.CommonUtil;

/**
 * 排菜相关hive库的查询
 * @author Administrator
 *
 */
@Service
public class DbHiveBaseServiceImpl implements DbHiveBaseService {
	private static final Logger logger = LogManager.getLogger(DbHiveBaseServiceImpl.class.getName());
	
	//额外数据源Hive
	@Autowired
	@Qualifier("dsHive")
	DataSource dataSourceHive;
	
	@Autowired
	@Qualifier("dsHive2")
	DataSource dataSourceHive2;
	
	//额外数据源Hive连接模板
	JdbcTemplate jdbcTemplateHive = null;
	
	//额外数据源Hive连接模板
	JdbcTemplate jdbcTemplateHive2 = null;
	
	//初始化处理标识，true表示已处理，false表示未处理
    boolean initProcFlag = false;
    
    //是否使用mybatis中间件
    boolean mybatisUseFlag = true;
    
    //初始化处理
  	@Scheduled(fixedRate = 60*60*1000)
  	public void initProc() {
  		if(initProcFlag)
  			return ;
  		initProcFlag = true;
  		logger.info("定时建立与 DataSource数据源dsHive对象表示的数据源的连接，时间：" + BCDTimeUtil.convertNormalFrom(null));
  		jdbcTemplateHive = new JdbcTemplate(dataSourceHive);
  		
  		jdbcTemplateHive2 = new JdbcTemplate(dataSourceHive2);
  	}
  	

    /**
    * 从数据库app_saas_v1的数据表t_base_material中根据条件查询数据列表
    * @param listYearMonth
    * @param startDate
    * @param endDateAddOne
    * @return
    */
    public List<TBaseMaterial> getTBaseMaterialList(TBaseMaterial inputObj,
    		Integer startNum,Integer endNum) {
        logger.info("[Enter dao method] {}-{}", "TBaseMaterialDao", "getTBaseMaterialList");
    	JdbcTemplate jdbcTemplateTemp  =DbHiveServiceImpl.getJdbcTemplateHive(jdbcTemplateHive, jdbcTemplateHive2, dataSourceHive);
		if(jdbcTemplateTemp == null) {
			return null;
		}
        StringBuffer sb = new StringBuffer();
        sb.append("select ");
        sb.append("id id, ");
        sb.append("  material_name materialName, parent_id parentId, other_names otherNames, type_id typeId, ");
        sb.append("  type_name typeName, calorie calorie, carbohydrate carbohydrate, fat fat, ");
        sb.append("  protein protein, dietary_fiber dietaryFiber, fibre fibre, vitamine_a vitamineA, ");
        sb.append("  vitamine_c vitamineC, vitamine_e vitamineE, carotene carotene, oryzanin oryzanin, ");
        sb.append("  lactochrome lactochrome, niacin niacin, cholesterol cholesterol, magnesium magnesium, ");
        sb.append("  calcium calcium, iron iron, zinc zinc, copper copper, ");
        sb.append("  manganese manganese, potassium potassium, phosphorus phosphorus, sodium sodium, ");
        sb.append("  selenium selenium, reviewed reviewed, refuse_reason refuseReason, creator creator, ");
        sb.append("  create_time createTime, updater updater, last_update_time lastUpdateTime, stat stat, ");
        sb.append("  source source ");
        sb.append(" from t_base_material " );
        sb.append(" where  1=1 ");
        getTBaseMaterialListCondition(inputObj,sb);
        if(startNum !=null && endNum !=null && startNum!=-1 &&  endNum != -1) {
        	sb.append(" limit "+endNum);
        }        logger.info("执行sql:"+sb.toString());
		return (List<TBaseMaterial>) jdbcTemplateTemp.query(sb.toString(), new RowMapper<TBaseMaterial>() {
			@Override
			public TBaseMaterial mapRow(ResultSet rs, int rowNum) throws SQLException {
				
				if(startNum!=null && endNum!=null && startNum!=-1 &&  endNum != -1 
						&& startNum!=null && endNum!=null && startNum!=-1 && endNum!=-1 && (rowNum <startNum || rowNum >= endNum)) {
					return null;
				}
								TBaseMaterial obj = new TBaseMaterial();

				obj.setId("-");
				if(CommonUtil.isNotEmpty(rs.getString("id"))) {
					obj.setId(rs.getString("id"));
				}
				obj.setMaterialName("-");
				if(CommonUtil.isNotEmpty(rs.getString("materialName"))) {
					obj.setMaterialName(rs.getString("materialName"));
				}
				obj.setParentId("-");
				if(CommonUtil.isNotEmpty(rs.getString("parentId"))) {
					obj.setParentId(rs.getString("parentId"));
				}
				obj.setOtherNames("-");
				if(CommonUtil.isNotEmpty(rs.getString("otherNames"))) {
					obj.setOtherNames(rs.getString("otherNames"));
				}
				obj.setTypeId("-");
				if(CommonUtil.isNotEmpty(rs.getString("typeId"))) {
					obj.setTypeId(rs.getString("typeId"));
				}
				obj.setTypeName("-");
				if(CommonUtil.isNotEmpty(rs.getString("typeName"))) {
					obj.setTypeName(rs.getString("typeName"));
				}
				obj.setCalorie(BigDecimal.ZERO.setScale(2,BigDecimal.ROUND_HALF_UP));
				if(CommonUtil.isNotEmpty(rs.getString("calorie"))) {
					obj.setCalorie(rs.getBigDecimal("calorie").setScale(2,BigDecimal.ROUND_HALF_UP));
				}
				obj.setCarbohydrate(BigDecimal.ZERO.setScale(2,BigDecimal.ROUND_HALF_UP));
				if(CommonUtil.isNotEmpty(rs.getString("carbohydrate"))) {
					obj.setCarbohydrate(rs.getBigDecimal("carbohydrate").setScale(2,BigDecimal.ROUND_HALF_UP));
				}
				obj.setFat(BigDecimal.ZERO.setScale(2,BigDecimal.ROUND_HALF_UP));
				if(CommonUtil.isNotEmpty(rs.getString("fat"))) {
					obj.setFat(rs.getBigDecimal("fat").setScale(2,BigDecimal.ROUND_HALF_UP));
				}
				obj.setProtein(BigDecimal.ZERO.setScale(2,BigDecimal.ROUND_HALF_UP));
				if(CommonUtil.isNotEmpty(rs.getString("protein"))) {
					obj.setProtein(rs.getBigDecimal("protein").setScale(2,BigDecimal.ROUND_HALF_UP));
				}
				obj.setDietaryFiber(BigDecimal.ZERO.setScale(2,BigDecimal.ROUND_HALF_UP));
				if(CommonUtil.isNotEmpty(rs.getString("dietaryFiber"))) {
					obj.setDietaryFiber(rs.getBigDecimal("dietaryFiber").setScale(2,BigDecimal.ROUND_HALF_UP));
				}
				obj.setFibre(BigDecimal.ZERO.setScale(2,BigDecimal.ROUND_HALF_UP));
				if(CommonUtil.isNotEmpty(rs.getString("fibre"))) {
					obj.setFibre(rs.getBigDecimal("fibre").setScale(2,BigDecimal.ROUND_HALF_UP));
				}
				obj.setVitamineA(BigDecimal.ZERO.setScale(2,BigDecimal.ROUND_HALF_UP));
				if(CommonUtil.isNotEmpty(rs.getString("vitamineA"))) {
					obj.setVitamineA(rs.getBigDecimal("vitamineA").setScale(2,BigDecimal.ROUND_HALF_UP));
				}
				obj.setVitamineC(BigDecimal.ZERO.setScale(2,BigDecimal.ROUND_HALF_UP));
				if(CommonUtil.isNotEmpty(rs.getString("vitamineC"))) {
					obj.setVitamineC(rs.getBigDecimal("vitamineC").setScale(2,BigDecimal.ROUND_HALF_UP));
				}
				obj.setVitamineE(BigDecimal.ZERO.setScale(2,BigDecimal.ROUND_HALF_UP));
				if(CommonUtil.isNotEmpty(rs.getString("vitamineE"))) {
					obj.setVitamineE(rs.getBigDecimal("vitamineE").setScale(2,BigDecimal.ROUND_HALF_UP));
				}
				obj.setCarotene(BigDecimal.ZERO.setScale(2,BigDecimal.ROUND_HALF_UP));
				if(CommonUtil.isNotEmpty(rs.getString("carotene"))) {
					obj.setCarotene(rs.getBigDecimal("carotene").setScale(2,BigDecimal.ROUND_HALF_UP));
				}
				obj.setOryzanin(BigDecimal.ZERO.setScale(2,BigDecimal.ROUND_HALF_UP));
				if(CommonUtil.isNotEmpty(rs.getString("oryzanin"))) {
					obj.setOryzanin(rs.getBigDecimal("oryzanin").setScale(2,BigDecimal.ROUND_HALF_UP));
				}
				obj.setLactochrome(BigDecimal.ZERO.setScale(2,BigDecimal.ROUND_HALF_UP));
				if(CommonUtil.isNotEmpty(rs.getString("lactochrome"))) {
					obj.setLactochrome(rs.getBigDecimal("lactochrome").setScale(2,BigDecimal.ROUND_HALF_UP));
				}
				obj.setNiacin(BigDecimal.ZERO.setScale(2,BigDecimal.ROUND_HALF_UP));
				if(CommonUtil.isNotEmpty(rs.getString("niacin"))) {
					obj.setNiacin(rs.getBigDecimal("niacin").setScale(2,BigDecimal.ROUND_HALF_UP));
				}
				obj.setCholesterol(BigDecimal.ZERO.setScale(2,BigDecimal.ROUND_HALF_UP));
				if(CommonUtil.isNotEmpty(rs.getString("cholesterol"))) {
					obj.setCholesterol(rs.getBigDecimal("cholesterol").setScale(2,BigDecimal.ROUND_HALF_UP));
				}
				obj.setMagnesium(BigDecimal.ZERO.setScale(2,BigDecimal.ROUND_HALF_UP));
				if(CommonUtil.isNotEmpty(rs.getString("magnesium"))) {
					obj.setMagnesium(rs.getBigDecimal("magnesium").setScale(2,BigDecimal.ROUND_HALF_UP));
				}
				obj.setCalcium(BigDecimal.ZERO.setScale(2,BigDecimal.ROUND_HALF_UP));
				if(CommonUtil.isNotEmpty(rs.getString("calcium"))) {
					obj.setCalcium(rs.getBigDecimal("calcium").setScale(2,BigDecimal.ROUND_HALF_UP));
				}
				obj.setIron(BigDecimal.ZERO.setScale(2,BigDecimal.ROUND_HALF_UP));
				if(CommonUtil.isNotEmpty(rs.getString("iron"))) {
					obj.setIron(rs.getBigDecimal("iron").setScale(2,BigDecimal.ROUND_HALF_UP));
				}
				obj.setZinc(BigDecimal.ZERO.setScale(2,BigDecimal.ROUND_HALF_UP));
				if(CommonUtil.isNotEmpty(rs.getString("zinc"))) {
					obj.setZinc(rs.getBigDecimal("zinc").setScale(2,BigDecimal.ROUND_HALF_UP));
				}
				obj.setCopper(BigDecimal.ZERO.setScale(2,BigDecimal.ROUND_HALF_UP));
				if(CommonUtil.isNotEmpty(rs.getString("copper"))) {
					obj.setCopper(rs.getBigDecimal("copper").setScale(2,BigDecimal.ROUND_HALF_UP));
				}
				obj.setManganese(BigDecimal.ZERO.setScale(2,BigDecimal.ROUND_HALF_UP));
				if(CommonUtil.isNotEmpty(rs.getString("manganese"))) {
					obj.setManganese(rs.getBigDecimal("manganese").setScale(2,BigDecimal.ROUND_HALF_UP));
				}
				obj.setPotassium(BigDecimal.ZERO.setScale(2,BigDecimal.ROUND_HALF_UP));
				if(CommonUtil.isNotEmpty(rs.getString("potassium"))) {
					obj.setPotassium(rs.getBigDecimal("potassium").setScale(2,BigDecimal.ROUND_HALF_UP));
				}
				obj.setPhosphorus(BigDecimal.ZERO.setScale(2,BigDecimal.ROUND_HALF_UP));
				if(CommonUtil.isNotEmpty(rs.getString("phosphorus"))) {
					obj.setPhosphorus(rs.getBigDecimal("phosphorus").setScale(2,BigDecimal.ROUND_HALF_UP));
				}
				obj.setSodium(BigDecimal.ZERO.setScale(2,BigDecimal.ROUND_HALF_UP));
				if(CommonUtil.isNotEmpty(rs.getString("sodium"))) {
					obj.setSodium(rs.getBigDecimal("sodium").setScale(2,BigDecimal.ROUND_HALF_UP));
				}
				obj.setSelenium(BigDecimal.ZERO.setScale(2,BigDecimal.ROUND_HALF_UP));
				if(CommonUtil.isNotEmpty(rs.getString("selenium"))) {
					obj.setSelenium(rs.getBigDecimal("selenium").setScale(2,BigDecimal.ROUND_HALF_UP));
				}
				obj.setReviewed(0);
				if(CommonUtil.isNotEmpty(rs.getString("reviewed"))) {
					obj.setReviewed(rs.getInt("reviewed"));
				}
				obj.setRefuseReason("-");
				if(CommonUtil.isNotEmpty(rs.getString("refuseReason"))) {
					obj.setRefuseReason(rs.getString("refuseReason"));
				}
				obj.setCreator("-");
				if(CommonUtil.isNotEmpty(rs.getString("creator"))) {
					obj.setCreator(rs.getString("creator"));
				}
				obj.setCreateTime("-");
				if(CommonUtil.isNotEmpty(rs.getString("createTime"))) {
					obj.setCreateTime(rs.getString("createTime"));
				}
				obj.setUpdater("-");
				if(CommonUtil.isNotEmpty(rs.getString("updater"))) {
					obj.setUpdater(rs.getString("updater"));
				}
				obj.setLastUpdateTime("-");
				if(CommonUtil.isNotEmpty(rs.getString("lastUpdateTime"))) {
					obj.setLastUpdateTime(rs.getString("lastUpdateTime"));
				}
				obj.setStat(0);
				if(CommonUtil.isNotEmpty(rs.getString("stat"))) {
					obj.setStat(rs.getInt("stat"));
				}
				obj.setSource(0);
				if(CommonUtil.isNotEmpty(rs.getString("source"))) {
					obj.setSource(rs.getInt("source"));
				}
				return obj;
			}
		});
    }
    
    

    /**
    * 从数据库app_saas_v1的数据表t_base_material中根据条件查询数据列表个数
    * @param listYearMonth
    * @param startDate
    * @param endDateAddOne
    * @return
    */
    public Integer getTBaseMaterialListCount(List<String> listYearMonth, String startDate,String endDateAddOne,
    		TBaseMaterial inputObj) {
        logger.info("[Enter dao method] {}-{}", "TBaseMaterialDao", "getTBaseMaterialListCount");
        Long daoStartTime = System.currentTimeMillis();
    	JdbcTemplate jdbcTemplateTemp  =DbHiveServiceImpl.getJdbcTemplateHive(jdbcTemplateHive, jdbcTemplateHive2, dataSourceHive);
		if(jdbcTemplateTemp == null) {
			return null;
		}
    	
    	final Integer[] dataCounts={0};
    	
    	StringBuffer sb = new StringBuffer();
        sb.append("select count(1) dataCount ");
        sb.append(" from t_base_material" );
        sb.append(" where 1=1  ");
        
        getTBaseMaterialListCondition(inputObj,sb);
        logger.info("执行sql:"+sb.toString());
        jdbcTemplateTemp.query(sb.toString(), new RowCallbackHandler() {   
        	public void processRow(ResultSet rs) throws SQLException {
        		dataCounts[0] = rs.getInt("dataCount");
        	}   
        });
        logger.info("Exec dao has takes {} millisecond.", System.currentTimeMillis() - daoStartTime);
        return dataCounts[0];
    }

    /**
    * 从数据库app_saas_v1的数据表t_base_material中根据条件查询数据列表条件
    * @param listYearMonth
    * @param startDate
    * @param endDateAddOne
    * @param sb
    */
    public void getTBaseMaterialListCondition(TBaseMaterial inputObj,
    StringBuffer sb) {
        logger.info("[Enter dao method] {}-{}", "TBaseMaterialDao", "getTBaseMaterialListCondition");
        Long daoStartTime = System.currentTimeMillis();
        
        //id
        if(CommonUtil.isNotEmpty(inputObj.getId())) {
        	sb.append(" and id = \"" + inputObj.getId()+"\"");
        }

        //materialName
        if(CommonUtil.isNotEmpty(inputObj.getMaterialName())) {
        	sb.append(" and material_name = \"" + inputObj.getMaterialName()+"\"");
        }

        //parentId
        if(CommonUtil.isNotEmpty(inputObj.getParentId())) {
        	sb.append(" and parent_id = \"" + inputObj.getParentId()+"\"");
        }

        //otherNames
        if(CommonUtil.isNotEmpty(inputObj.getOtherNames())) {
        	sb.append(" and other_names = \"" + inputObj.getOtherNames()+"\"");
        }

        //typeId
        if(CommonUtil.isNotEmpty(inputObj.getTypeId())) {
        	sb.append(" and type_id = \"" + inputObj.getTypeId()+"\"");
        }

        //typeName
        if(CommonUtil.isNotEmpty(inputObj.getTypeName())) {
        	sb.append(" and type_name = \"" + inputObj.getTypeName()+"\"");
        }

        //calorie
        if(inputObj.getCalorie() !=null ) {
        	sb.append(" and calorie = \"" + inputObj.getCalorie()+"\"");
        }

        //carbohydrate
        if(inputObj.getCarbohydrate() !=null ) {
        	sb.append(" and carbohydrate = \"" + inputObj.getCarbohydrate()+"\"");
        }

        //fat
        if(inputObj.getFat() !=null ) {
        	sb.append(" and fat = \"" + inputObj.getFat()+"\"");
        }

        //protein
        if(inputObj.getProtein() !=null ) {
        	sb.append(" and protein = \"" + inputObj.getProtein()+"\"");
        }

        //dietaryFiber
        if(inputObj.getDietaryFiber() !=null ) {
        	sb.append(" and dietary_fiber = \"" + inputObj.getDietaryFiber()+"\"");
        }

        //fibre
        if(inputObj.getFibre() !=null ) {
        	sb.append(" and fibre = \"" + inputObj.getFibre()+"\"");
        }

        //vitamineA
        if(inputObj.getVitamineA() !=null ) {
        	sb.append(" and vitamine_a = \"" + inputObj.getVitamineA()+"\"");
        }

        //vitamineC
        if(inputObj.getVitamineC() !=null ) {
        	sb.append(" and vitamine_c = \"" + inputObj.getVitamineC()+"\"");
        }

        //vitamineE
        if(inputObj.getVitamineE() !=null ) {
        	sb.append(" and vitamine_e = \"" + inputObj.getVitamineE()+"\"");
        }

        //carotene
        if(inputObj.getCarotene() !=null ) {
        	sb.append(" and carotene = \"" + inputObj.getCarotene()+"\"");
        }

        //oryzanin
        if(inputObj.getOryzanin() !=null ) {
        	sb.append(" and oryzanin = \"" + inputObj.getOryzanin()+"\"");
        }

        //lactochrome
        if(inputObj.getLactochrome() !=null ) {
        	sb.append(" and lactochrome = \"" + inputObj.getLactochrome()+"\"");
        }

        //niacin
        if(inputObj.getNiacin() !=null ) {
        	sb.append(" and niacin = \"" + inputObj.getNiacin()+"\"");
        }

        //cholesterol
        if(inputObj.getCholesterol() !=null ) {
        	sb.append(" and cholesterol = \"" + inputObj.getCholesterol()+"\"");
        }

        //magnesium
        if(inputObj.getMagnesium() !=null ) {
        	sb.append(" and magnesium = \"" + inputObj.getMagnesium()+"\"");
        }

        //calcium
        if(inputObj.getCalcium() !=null ) {
        	sb.append(" and calcium = \"" + inputObj.getCalcium()+"\"");
        }

        //iron
        if(inputObj.getIron() !=null ) {
        	sb.append(" and iron = \"" + inputObj.getIron()+"\"");
        }

        //zinc
        if(inputObj.getZinc() !=null ) {
        	sb.append(" and zinc = \"" + inputObj.getZinc()+"\"");
        }

        //copper
        if(inputObj.getCopper() !=null ) {
        	sb.append(" and copper = \"" + inputObj.getCopper()+"\"");
        }

        //manganese
        if(inputObj.getManganese() !=null ) {
        	sb.append(" and manganese = \"" + inputObj.getManganese()+"\"");
        }

        //potassium
        if(inputObj.getPotassium() !=null ) {
        	sb.append(" and potassium = \"" + inputObj.getPotassium()+"\"");
        }

        //phosphorus
        if(inputObj.getPhosphorus() !=null ) {
        	sb.append(" and phosphorus = \"" + inputObj.getPhosphorus()+"\"");
        }

        //sodium
        if(inputObj.getSodium() !=null ) {
        	sb.append(" and sodium = \"" + inputObj.getSodium()+"\"");
        }

        //selenium
        if(inputObj.getSelenium() !=null ) {
        	sb.append(" and selenium = \"" + inputObj.getSelenium()+"\"");
        }

        //reviewed
        if(inputObj.getReviewed() !=null && inputObj.getReviewed() != -1) {
        	sb.append(" and reviewed = \"" + inputObj.getReviewed()+"\"");
        }

        //refuseReason
        if(CommonUtil.isNotEmpty(inputObj.getRefuseReason())) {
        	sb.append(" and refuse_reason = \"" + inputObj.getRefuseReason()+"\"");
        }

        //creator
        if(CommonUtil.isNotEmpty(inputObj.getCreator())) {
        	sb.append(" and creator = \"" + inputObj.getCreator()+"\"");
        }

        //createTime
        if(inputObj.getCreateTime() !=null ) {
        	sb.append(" and create_time = \"" + inputObj.getCreateTime()+"\"");
        }

        //updater
        if(CommonUtil.isNotEmpty(inputObj.getUpdater())) {
        	sb.append(" and updater = \"" + inputObj.getUpdater()+"\"");
        }

        //lastUpdateTime
        if(inputObj.getLastUpdateTime() !=null ) {
        	sb.append(" and last_update_time = \"" + inputObj.getLastUpdateTime()+"\"");
        }

        //stat
        if(inputObj.getStat() !=null && inputObj.getStat() != -1) {
        	sb.append(" and stat = \"" + inputObj.getStat()+"\"");
        }

        //source
        if(inputObj.getSource() !=null && inputObj.getSource() != -1) {
        	sb.append(" and source = \"" + inputObj.getSource()+"\"");
        }

        logger.info("Exec dao has takes {} millisecond.", System.currentTimeMillis() - daoStartTime);
    }
}
