package com.tfit.BdBiProcSrvShEduOmc.dao.mapper.edubd;

import com.tfit.BdBiProcSrvShEduOmc.dto.news.NewsDTO;

import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface TNewsMapper {

    public List<NewsDTO> queryNewsList();

    public NewsDTO queryNewsById(@Param("id") Long id);

    public String createNews(NewsDTO newsDTO);

    int deleteNews(@Param("id") Long id);

    int updateNews(NewsDTO newsDTO);

}
