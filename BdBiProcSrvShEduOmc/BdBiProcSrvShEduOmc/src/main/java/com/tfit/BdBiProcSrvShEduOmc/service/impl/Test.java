package com.tfit.BdBiProcSrvShEduOmc.service.impl;

public class Test {

    public static void main(String[] args) {
        StringBuffer sb = new StringBuffer();
        sb.append(" SELECT ");
        sb.append("     id schoolId,school_name schName, ");
        sb.append("     school_name schName, ");
        sb.append("     area distName,address detailAddr, ");
        sb.append("     social_credit_code uscc,address detailAddr, ");
        sb.append("     level_name schType,school_nature_name schProp,school_nature_sub_name, ");
        sb.append("     department_master_id subLevel,department_slave_id_name compDep,license_main_type fblMb, ");
        sb.append("     license_main_child optMode,corporation legalRep,food_safety_persion projContact, ");
        sb.append("     food_safety_mobilephone pcMobilePhone ");
        sb.append(" FROM ");
        sb.append("     t_edu_school_new ");
        sb.append(" where  1=1 ");
        

        sb.append(" order by distName asc,schType asc");

        System.out.println("sb.toString() = " + sb.toString());
    }
    
}
