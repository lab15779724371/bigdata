package com.tfit.BdBiProcSrvShEduOmc.util.export;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.tfit.BdBiProcSrvShEduOmc.dto.ga.CaDishSupDets;

public class RelectUtil {
    public static <T> List<Object> reflectEntity(T model,Class<?> cals) throws NoSuchMethodException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, SecurityException, NoSuchFieldException{
        List<Object> list = new ArrayList<Object>();
        Field[] field = model.getClass().getDeclaredFields();        //获取实体类的所有属性，返回Field数组  
        for(int j=0 ; j<field.length ; j++){     //遍历所有属性
            String nam = field[j].getName();    //获取属性的名字
            String name = nam;
            name  = name.substring(0,1).toUpperCase()+name.substring(1);
            //String type = field[j].getGenericType().toString();    //获取属性的类型
            String type = model.getClass().toString(); 
            
            if(type.equals("class java.lang.String")){   //如果type是类类型，则前面包含"class "，后面跟类名
                Method m = model.getClass().getMethod("get"+name);
                String value = (String) m.invoke(model);    //调用getter方法获取属性值
                if(value != null){
                   list.add(value);
                }else{
                    list.add("");
                }
            }

            if(type.equals("class java.lang.Integer")){     
                Method m = model.getClass().getMethod("get"+name);
                Integer value = (Integer) m.invoke(model);
                if(value != null){
                    list.add(value);
                }else{
                    list.add("");
                }
            }

            if(type.equals("class java.lang.Short")){     
                Method m = model.getClass().getMethod("get"+name);
                Short value = (Short) m.invoke(model);
                if(value != null){
                    list.add(value);                    
                }else{
                    list.add("");
                }
            }       

            if(type.equals("class java.lang.Double")){     
                Method m = model.getClass().getMethod("get"+name);
                Double value = (Double) m.invoke(model);
                if(value != null){                    
                    list.add(value);  
                }else{
                    list.add("");
                }
            }                  

            if(type.equals("class java.lang.Boolean")){
                Method m = model.getClass().getMethod("get"+name);    
                Boolean value = (Boolean) m.invoke(model);
                if(value != null){                      
                    list.add(value);
                }else{
                    list.add("");
                }
            }

            if(type.equals("class java.util.Date")){
                Method m = model.getClass().getMethod("get"+name);                    
                Date value = (Date) m.invoke(model);
                if(value != null){
                    list.add(value);
                }else{
                    list.add("");
                }
            }        
            
            if(type.equals("class com.tfit.BdBiProcSrvShEduOmc.dto.ga.CaDishSupDets")){
                Method m = model.getClass().getMethod(name);                    
                CaDishSupDets value = (CaDishSupDets) m.invoke(model);
                if(value != null){
                    list.add(value);
                }else{
                    list.add("");
                }
            }    
        }
        return list;
    }
    /**
     * 此方法将数据集合按65000个进行分割成多个子集合
     * @author OnlyOne
     * @param list 需要处理的list数据集合
     * @return
     */
    public static <T> Map<Integer, List<T>> daData(List<T> list){
        int count = list.size()/65000;
        int yu = list.size() % 65000;
        Map<Integer, List<T>> map = new HashMap<Integer, List<T>>();
        for (int i = 0; i <= count; i++) {
            List<T> subList = new ArrayList<T>();
            if (i == count) {
                subList = list.subList(i * 65000, 65000 * i + yu);
            } else {
                subList = list.subList(i * 65000, 65000 * (i + 1));
            }
            map.put(i, subList);
        } 
        return map;
    }
}