package com.tfit.BdBiProcSrvShEduOmc.util;

import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @Descritpion：列表分页工具
 * @author: tianfang_infotech
 * @date: 2019/1/7 9:39
 */
public class PagedUtil {

    /**
     * 分页处理器
     * @param list 泛型列表
     * @param pageIndex 当前页码
     * @param pageSize 页大小
     * @param <T> 分页后泛型列表
     * @return
     */
    public static <T> List<T> paged(List<T> list, Integer pageIndex, Integer pageSize){
        if(CollectionUtils.isEmpty(list)){
            return list;
        }

        return list.stream()
                .skip((pageIndex - 1) * pageSize)
                .limit(pageSize)
                .collect(Collectors.toList());
    }
}
