package com.tfit.BdBiProcSrvShEduOmc.service;

import com.tfit.BdBiProcSrvShEduOmc.dto.news.NewsDTO;
import com.tfit.BdBiProcSrvShEduOmc.dto.news.NewsReq;

import java.util.List;

public interface INewsService {

    public List<NewsDTO> queryNewsList();

    public NewsDTO queryNewsById(Long id);

    public String createNews(NewsDTO newsDTO);

    /**
     * @param newsDTO
     * @return
     * @desc 编辑新闻
     */
    int updateNews(NewsDTO newsDTO);

    /**
     * @param req
     * @return
     * @desc 删除新闻
     */
    int deleteNews(NewsReq req);
}
