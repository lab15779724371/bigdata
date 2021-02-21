package com.tfit.BdBiProcSrvShEduOmc.dto.news;

import com.tfit.BdBiProcSrvShEduOmc.dto.PageInfo;
import com.tfit.BdBiProcSrvShEduOmc.dto.im.WasteOils;
import com.tfit.BdBiProcSrvShEduOmc.model.vo.base.PagedList;
import lombok.Data;

import java.util.List;

@Data
public class NewsListDTO {

    String time;

    List<NewsDTO> newsList;

    long msgId;

    private PagedList pagedList;


}
