package com.tfit.BdBiProcSrvShEduOmc.model.vo.base;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.tfit.BdBiProcSrvShEduOmc.dto.PageInfo;
import com.tfit.BdBiProcSrvShEduOmc.dto.base.PagedDTO;
import com.tfit.BdBiProcSrvShEduOmc.util.PagedUtil;
import lombok.Data;
import org.springframework.util.CollectionUtils;

import java.util.List;

/**
 * @Descritpion：列表信息
 * @author: tianfang_infotech
 * @date: 2019/1/3 11:37
 */
@Data
public class PagedList<T> {

    public PagedList() {

    }

    public PagedList(List<T> list) {
        this.data = list;
    }

    public PagedList(List<T> pagedList, PageInfo pageInfo) {
        this.data = pagedList;
        this.pageInfo = pageInfo;
    }

    public PagedList(List<T> list, PagedDTO pagedDTO) {
        this.data = PagedUtil.paged(list, pagedDTO.getPage(), pagedDTO.getPageSize());
        if (CollectionUtils.isEmpty(list)) {
            //如果数据为空，不返回分页信息
            this.pageInfo = null;
        } else {
            this.pageInfo = new PageInfo(pagedDTO.getPage(), list.size());
        }
    }

    public PagedList(List<T> list, Integer pageIndex, Integer pageSize) {
        this.data = PagedUtil.paged(list, pageIndex, pageSize);
        if (CollectionUtils.isEmpty(list)) {
            //如果数据为空，不返回分页信息
            this.pageInfo = null;
        } else {
            this.pageInfo = new PageInfo(pageIndex, list.size());
        }
    }

    /**
     * 数据信息
     */
    private List<T> data;

    /**
     * 分页信息
     */
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private PageInfo pageInfo;
}
