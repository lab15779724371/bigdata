package com.tfit.BdBiProcSrvShEduOmc.service.impl;

import com.tfit.BdBiProcSrvShEduOmc.dao.mapper.edubd.TNewsMapper;
import com.tfit.BdBiProcSrvShEduOmc.dto.news.NewsDTO;
import com.tfit.BdBiProcSrvShEduOmc.dto.news.NewsReq;
import com.tfit.BdBiProcSrvShEduOmc.service.INewsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class NewsServiceImpl implements INewsService {
    @Autowired
    TNewsMapper tNewsMapper;

    @Override
    public List<NewsDTO> queryNewsList() {
        return tNewsMapper.queryNewsList();
    }

    @Override
    public NewsDTO queryNewsById(Long id) {
        return tNewsMapper.queryNewsById(id);
    }

    @Override
    public String createNews(NewsDTO newsDTO) {
        return tNewsMapper.createNews(newsDTO);
    }

    @Override
    public int updateNews(NewsDTO newsDTO) {
        return tNewsMapper.updateNews(newsDTO);
    }

    @Override
    public int deleteNews(NewsReq req) {
        return tNewsMapper.deleteNews(req.getId());
    }
}
