package com.tfit.BdBiProcSrvShEduOmc.util;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class SortList<E> {
	//sign函数，输出1、0、-1
	private int sign(float val1, float val2) {
		int retFlag = 0;
		float difVal = val2-val1;
		if(difVal < 0)
			retFlag = 1;
		else if(difVal > 0)
			retFlag = -1;
		
		return retFlag;
	}
	//sign函数，输出1、0、-1
	private int sign(int val1, int val2) {
		int retFlag = 0;
		int difVal = val2-val1;
		if(difVal < 0)
			retFlag = 1;
		else if(difVal > 0)
			retFlag = -1;
		
		return retFlag;
	}
	
    @SuppressWarnings({ "unchecked", "rawtypes" })
	public void Sort(List<E> list, final String method, final String sort) {
        Collections.sort(list, new Comparator() {
            public int compare(Object a, Object b) {
                int ret = 0;
                try {
                    Method m1 = ((E) a).getClass().getMethod(method, null);
                    Method m2 = ((E) b).getClass().getMethod(method, null);
                    if (sort != null && "desc".equals(sort))// 倒序
                        ret = m2.invoke(((E) b), null).toString().compareTo(m1.invoke(((E) a), null).toString());
                    else
                        // 正序
                        ret = m1.invoke(((E) a), null).toString().compareTo(m2.invoke(((E) b), null).toString());
                } catch (NoSuchMethodException ne) {
                    System.out.println(ne);
                } catch (IllegalAccessException ie) {
                    System.out.println(ie);
                } catch (InvocationTargetException it) {
                    System.out.println(it);
                }
                return ret;
            }
        });
    }
    
    @SuppressWarnings({ "unchecked", "rawtypes" })
	public  void Sort(List<E> list, final String method, final String sort, final String dataType) {
        Collections.sort(list, new Comparator() {
            public int compare(Object a, Object b) {
                int ret = 0;
                try {
                    Method m1 = ((E) a).getClass().getMethod(method, null);
                    Method m2 = ((E) b).getClass().getMethod(method, null);
                    if(dataType == null || dataType.equalsIgnoreCase("String")) {
                    	if (sort != null && "desc".equals(sort)) {   // 倒序 
                    		ret = m2.invoke(((E) b), null).toString().compareTo(m1.invoke(((E) a), null).toString());
                    	}
                    	else {    // 正序
                    		ret = m1.invoke(((E) a), null).toString().compareTo(m2.invoke(((E) b), null).toString());
                    	}
                    }
                    else if(dataType.equalsIgnoreCase("Float")) {
                    	float m1_val = Float.parseFloat(m1.invoke(((E) a), null).toString());
                		float m2_val = Float.parseFloat(m2.invoke(((E) b), null).toString());
                    	if (sort != null && "desc".equals(sort)) {   // 倒序                     	                    		
                    		ret = sign(m2_val, m1_val);
                    	}
                    	else {    // 正序                    		
                    		ret = sign(m1_val, m2_val);                    		
                    	}
                    }
                    else if(dataType.equalsIgnoreCase("Integer")) {
                    	int m1_val = Integer.parseInt(m1.invoke(((E) a), null).toString());
                		int m2_val = Integer.parseInt(m2.invoke(((E) b), null).toString());
                    	if (sort != null && "desc".equals(sort)) {   // 倒序                     		
                    		ret = sign(m2_val, m1_val);
                    	}
                    	else {    // 正序
                    		ret = sign(m1_val, m2_val);
                    	}
                    }
                } catch (NoSuchMethodException ne) {
                    System.out.println(ne);
                } catch (IllegalAccessException ie) {
                    System.out.println(ie);
                } catch (InvocationTargetException it) {
                    System.out.println(it);
                }
                return ret;
            }
        });
    }
    
    @SuppressWarnings({ "unchecked", "rawtypes" })
	public void Sort(List<E> list, final String[] methods, final String[] sorts, final String[] dataTypes) {
    	if(methods.length != 2 || sorts.length != 2 || dataTypes.length != 2 && list.size() <= 0)
    		return ;
        Collections.sort(list, new Comparator() {
            public int compare(Object a, Object b) {
                int ret = 0;
                try {
                    Method m10 = ((E) a).getClass().getMethod(methods[0], null);
                    Method m20 = ((E) b).getClass().getMethod(methods[0], null);
                    if(dataTypes[0] == null || dataTypes[0].equalsIgnoreCase("String")) {
                    	if (sorts[0] != null && "desc".equals(sorts[0])) {   // 倒序 
                    		ret = m20.invoke(((E) b), null).toString().compareTo(m10.invoke(((E) a), null).toString());
                    		if(ret == 0) {
                    			 Method m11 = ((E) a).getClass().getMethod(methods[1], null);
                                 Method m21 = ((E) b).getClass().getMethod(methods[1], null);
                                 ret = m21.invoke(((E) b), null).toString().compareTo(m11.invoke(((E) a), null).toString());
                    		}
                    	}
                    	else {    // 正序
                    		ret = m10.invoke(((E) a), null).toString().compareTo(m20.invoke(((E) b), null).toString());
                    		if(ret == 0) {
                    			Method m11 = ((E) a).getClass().getMethod(methods[1], null);
                                Method m21 = ((E) b).getClass().getMethod(methods[1], null);
                                ret = m11.invoke(((E) b), null).toString().compareTo(m21.invoke(((E) a), null).toString());
                    		}
                    	}
                    }
                    else if(dataTypes[0].equalsIgnoreCase("Float")) {
                    	float m10_val = Float.parseFloat(m10.invoke(((E) a), null).toString());
                		float m20_val = Float.parseFloat(m20.invoke(((E) b), null).toString());
                    	if (sorts[0] != null && "desc".equals(sorts[0])) {   // 倒序              
                    		ret = sign(m20_val, m10_val);
                    		if(ret == 0) {
                    			Method m11 = ((E) a).getClass().getMethod(methods[1], null);
                        		Method m21 = ((E) b).getClass().getMethod(methods[1], null);
                    			float m11_val = Float.parseFloat(m11.invoke(((E) a), null).toString());
                        		float m21_val = Float.parseFloat(m21.invoke(((E) b), null).toString());
                        		ret = sign(m21_val, m11_val);
                    		}                    		
                    	}
                    	else {    // 正序
                    		ret = sign(m10_val, m20_val);
                    		if(ret == 0) {
                    			Method m11 = ((E) a).getClass().getMethod(methods[1], null);
                        		Method m21 = ((E) b).getClass().getMethod(methods[1], null);
                    			float m11_val = Float.parseFloat(m11.invoke(((E) a), null).toString());
                        		float m21_val = Float.parseFloat(m21.invoke(((E) b), null).toString());
                        		ret = sign(m11_val, m21_val);
                    		}
                    	}
                    }
                    else if(dataTypes[0].equalsIgnoreCase("Integer")) {
                    	int m10_val = Integer.parseInt(m10.invoke(((E) a), null).toString());
                		int m20_val = Integer.parseInt(m20.invoke(((E) b), null).toString());
                    	if (sorts[0] != null && "desc".equals(sorts[0])) {   // 倒序                     		
                    		ret = sign(m20_val, m10_val);
                    		if(ret == 0) {
                    			Method m11 = ((E) a).getClass().getMethod(methods[1], null);
                        		Method m21 = ((E) b).getClass().getMethod(methods[1], null);
                    			int m11_val = Integer.parseInt(m11.invoke(((E) a), null).toString());
                        		int m21_val = Integer.parseInt(m21.invoke(((E) b), null).toString());
                        		ret = sign(m21_val, m11_val);
                    		}
                    	}
                    	else {    // 正序
                    		ret = sign(m10_val, m20_val);
                    		if(ret == 0) {
                    			Method m11 = ((E) a).getClass().getMethod(methods[1], null);
                        		Method m21 = ((E) b).getClass().getMethod(methods[1], null);
                    			int m11_val = Integer.parseInt(m11.invoke(((E) a), null).toString());
                        		int m21_val = Integer.parseInt(m21.invoke(((E) b), null).toString());
                        		ret = sign(m11_val, m21_val);
                    		}
                    	}
                    }
                } catch (NoSuchMethodException ne) {
                    System.out.println(ne);
                } catch (IllegalAccessException ie) {
                    System.out.println(ie);
                } catch (InvocationTargetException it) {
                    System.out.println(it);
                }
                return ret;
            }
        });
    }
    
    @SuppressWarnings({ "unchecked", "rawtypes" })
	public void Sort3Level(List<E> list, final String[] methods, final String[] sorts, final String[] dataTypes) {
    	if(methods.length != 3 || sorts.length != 3 || dataTypes.length != 3 && list.size() <= 0)
    		return ;
        Collections.sort(list, new Comparator() {
            public int compare(Object a, Object b) {
                int ret = 0;
                try {
                    Method m10 = ((E) a).getClass().getMethod(methods[0], null);
                    Method m20 = ((E) b).getClass().getMethod(methods[0], null);
                    if(dataTypes[0] == null || dataTypes[0].equalsIgnoreCase("String")) {
                    	if (sorts[0] != null && "desc".equals(sorts[0])) {   // 倒序 
                    		ret = m20.invoke(((E) b), null).toString().compareTo(m10.invoke(((E) a), null).toString());
                    		if(ret == 0) {
                    			 Method m11 = ((E) a).getClass().getMethod(methods[1], null);
                                 Method m21 = ((E) b).getClass().getMethod(methods[1], null);
                                 ret = m21.invoke(((E) b), null).toString().compareTo(m11.invoke(((E) a), null).toString());
                                 if(ret == 0) {
                        			 Method m12 = ((E) a).getClass().getMethod(methods[2], null);
                                     Method m22 = ((E) b).getClass().getMethod(methods[2], null);
                                     ret = m22.invoke(((E) b), null).toString().compareTo(m12.invoke(((E) a), null).toString());
                        		}
                    		}
                    	}
                    	else {    // 正序
                    		ret = m10.invoke(((E) a), null).toString().compareTo(m20.invoke(((E) b), null).toString());
                    		if(ret == 0) {
                    			Method m11 = ((E) a).getClass().getMethod(methods[1], null);
                                Method m21 = ((E) b).getClass().getMethod(methods[1], null);
                                ret = m11.invoke(((E) b), null).toString().compareTo(m21.invoke(((E) a), null).toString());
                                if(ret == 0) {
                        			Method m12 = ((E) a).getClass().getMethod(methods[2], null);
                                    Method m22 = ((E) b).getClass().getMethod(methods[2], null);
                                    ret = m12.invoke(((E) b), null).toString().compareTo(m22.invoke(((E) a), null).toString());
                        		}
                    		}
                    	}
                    }
                    else if(dataTypes[0].equalsIgnoreCase("Float")) {
                    	float m10_val = Float.parseFloat(m10.invoke(((E) a), null).toString());
                		float m20_val = Float.parseFloat(m20.invoke(((E) b), null).toString());
                    	if (sorts[0] != null && "desc".equals(sorts[0])) {   // 倒序             
                    		ret = sign(m20_val, m10_val);
                    		if(ret == 0) {
                    			Method m11 = ((E) a).getClass().getMethod(methods[1], null);
                        		Method m21 = ((E) b).getClass().getMethod(methods[1], null);
                    			float m11_val = Float.parseFloat(m11.invoke(((E) a), null).toString());
                        		float m21_val = Float.parseFloat(m21.invoke(((E) b), null).toString());
                        		ret = sign(m21_val, m11_val);
                        		if(ret == 0) {
                        			Method m12 = ((E) a).getClass().getMethod(methods[2], null);
                            		Method m22 = ((E) b).getClass().getMethod(methods[2], null);
                            		float m12_val = Float.parseFloat(m12.invoke(((E) a), null).toString());
                            		float m22_val = Float.parseFloat(m22.invoke(((E) b), null).toString());
                            		ret = sign(m22_val, m12_val);
                        		}
                    		}
                    	}
                    	else {    // 正序
                    		ret = sign(m10_val, m20_val);
                    		if(ret == 0) {
                    			Method m11 = ((E) a).getClass().getMethod(methods[1], null);
                        		Method m21 = ((E) b).getClass().getMethod(methods[1], null);
                    			float m11_val = Float.parseFloat(m11.invoke(((E) a), null).toString());
                        		float m21_val = Float.parseFloat(m21.invoke(((E) b), null).toString());
                        		ret = sign(m11_val, m21_val);
                        		if(ret == 0) {
                        			Method m12 = ((E) a).getClass().getMethod(methods[2], null);
                            		Method m22 = ((E) b).getClass().getMethod(methods[2], null);
                            		float m12_val = Float.parseFloat(m12.invoke(((E) a), null).toString());
                            		float m22_val = Float.parseFloat(m22.invoke(((E) b), null).toString());
                            		ret = sign(m12_val, m22_val);
                        		}
                    		}
                    	}
                    }
                    else if(dataTypes[0].equalsIgnoreCase("Integer")) {
                    	int m10_val = Integer.parseInt(m10.invoke(((E) a), null).toString());
                		int m20_val = Integer.parseInt(m20.invoke(((E) b), null).toString());
                    	if (sorts[0] != null && "desc".equals(sorts[0])) {   // 倒序                     		
                    		ret = sign(m20_val, m10_val);
                    		if(ret == 0) {
                    			Method m11 = ((E) a).getClass().getMethod(methods[1], null);
                        		Method m21 = ((E) b).getClass().getMethod(methods[1], null);
                    			int m11_val = Integer.parseInt(m11.invoke(((E) a), null).toString());
                        		int m21_val = Integer.parseInt(m21.invoke(((E) b), null).toString());
                        		ret = sign(m21_val, m11_val);
                        		if(ret == 0) {
                        			Method m12 = ((E) a).getClass().getMethod(methods[2], null);
                            		Method m22 = ((E) b).getClass().getMethod(methods[2], null);
                            		int m12_val = Integer.parseInt(m12.invoke(((E) a), null).toString());
                            		int m22_val = Integer.parseInt(m22.invoke(((E) b), null).toString());
                            		ret = sign(m22_val, m12_val);
                        		}
                    		}
                    	}
                    	else {    // 正序
                    		ret = sign(m10_val, m20_val);
                    		if(ret == 0) {
                    			Method m11 = ((E) a).getClass().getMethod(methods[1], null);
                        		Method m21 = ((E) b).getClass().getMethod(methods[1], null);
                    			int m11_val = Integer.parseInt(m11.invoke(((E) a), null).toString());
                        		int m21_val = Integer.parseInt(m21.invoke(((E) b), null).toString());
                        		ret = sign(m11_val, m21_val);
                        		if(ret == 0) {
                        			Method m12 = ((E) a).getClass().getMethod(methods[2], null);
                            		Method m22 = ((E) b).getClass().getMethod(methods[2], null);
                            		int m12_val = Integer.parseInt(m12.invoke(((E) a), null).toString());
                            		int m22_val = Integer.parseInt(m22.invoke(((E) b), null).toString());
                            		ret = sign(m12_val, m22_val);
                        		}
                    		}
                    	}
                    }
                } catch (NoSuchMethodException ne) {
                    System.out.println(ne);
                } catch (IllegalAccessException ie) {
                    System.out.println(ie);
                } catch (InvocationTargetException it) {
                    System.out.println(it);
                }
                return ret;
            }
        });
    }
}