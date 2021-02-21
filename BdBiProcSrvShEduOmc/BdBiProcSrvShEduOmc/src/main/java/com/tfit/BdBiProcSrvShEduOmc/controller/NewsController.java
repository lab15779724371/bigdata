package com.tfit.BdBiProcSrvShEduOmc.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.tfit.BdBiProcSrvShEduOmc.config.AppModConfig;
import com.tfit.BdBiProcSrvShEduOmc.dto.base.PagedDTO;
import com.tfit.BdBiProcSrvShEduOmc.dto.news.*;

import com.tfit.BdBiProcSrvShEduOmc.model.vo.BasicBdUser;
import com.tfit.BdBiProcSrvShEduOmc.model.vo.base.PagedList;
import com.tfit.BdBiProcSrvShEduOmc.service.INewsService;
import com.tfit.BdBiProcSrvShEduOmc.service.edubd.EduBdUserService;
import com.tfit.BdBiProcSrvShEduOmc.util.PagedUtil;
import lombok.extern.log4j.Log4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;

@RestController
@RequestMapping(value = "/news")
@Log4j
public class NewsController {

    @Autowired
    INewsService newsService;
    @Autowired
    EduBdUserService eduBdUserService;

    //新闻列表
    @RequestMapping(value = "/v1/queryNewsList", method = RequestMethod.GET)
    public String queryNewsList(PagedDTO pagedDTO) {
        // 页号、页大小和总页数
        //int curPageNum = 1, pageIndex = 1, pageSize = 20;
        NewsListDTO list = new NewsListDTO();
        list.setMsgId(1L);
        list.setTime(DateFormatUtils.format(new Date(), "yyyy-MM-dd HH:mm:ss"));
        List<NewsDTO> newsList = newsService.queryNewsList();
        PagedList pagedList = new PagedList(newsList, pagedDTO);
        list.setPagedList(pagedList);
        return back(list, "1000", "success");
    }

    //新闻详情页面
    @RequestMapping(value = "/v1/queryNewsById", method = RequestMethod.GET)
    public String queryNewsById(@RequestParam Long id) {
        return back(newsService.queryNewsById(id), "1000", "成功");

    }


    //新闻编辑功能页面
    @RequestMapping(value = "/v1/updateNews", method = RequestMethod.POST)
    public String updateNews(@RequestBody NewsReq req, HttpServletRequest request) {
        try {
            if (StringUtils.isBlank(req.getTitle())) {
                return back(null, "error", "新闻标题不能为空！");
            }
            if (req.getTitle().length() > 200) {
                return back(null, "error", "新闻标题字符长度不能大于200个字符！");
            }

            if (StringUtils.isBlank(req.getContext())) {
                return back(null, "error", "新闻内容不能为空！");
            }
            if (req.getContext().length() > 10000) {
                return back(null, "error", "新闻内容字符长度不能大于10000个字符！");
            }

            if (StringUtils.isBlank(req.getDataSource())) {
                return back(null, "error", "来源不能为空！");
            }
            if (req.getDataSource().length() > 50) {
                return back(null, "error", "来源字符长度不能大于50个字符！");
            }
            //授权码
            String token = AppModConfig.GetHeadJsonReq(request, "Authorization");
            if (token == null) {
                return back(null, "error", "对不起，您没有权限！");
            }
            BasicBdUser basicBdUser = eduBdUserService.getBasicBdUser(token);
            log.info(JSON.toJSONString(basicBdUser));
            if (basicBdUser == null) {
                return back(null, "error", "对不起，请重新登录！");
            }
            NewsDTO newsDTO = new NewsDTO();
            newsDTO.setId(req.getId());//新闻专题ID
            newsDTO.setTitle(req.getTitle());//新闻标题
            newsDTO.setContext(req.getContext());//新闻正文
            newsDTO.setDataSource(req.getDataSource());//新闻来源
            int result = newsService.updateNews(newsDTO);
            return back(result, "1000", "成功");
        } catch (Exception ex) {
            log.error("创建异常", ex);
            return back(null, "error", "创建异常，请联系管理员！");
        }
    }

    //新闻删除功能页面
    @RequestMapping(value = "/v1/deleteNews", method = RequestMethod.POST)
    public String deleteNews(@RequestBody NewsReq req, HttpServletRequest request) {
        try {
            //授权码
            String token = AppModConfig.GetHeadJsonReq(request, "Authorization");
            if (token == null) {
                return back(null, "error", "对不起，您没有权限！");
            }
            BasicBdUser basicBdUser = eduBdUserService.getBasicBdUser(token);
            log.info(JSON.toJSONString(basicBdUser));
            if (basicBdUser == null) {
                return back(null, "error", "对不起，请重新登录！");
            }
            int result = newsService.deleteNews(req);
            return back(result, "1000", "成功");
        } catch (Exception ex) {
            log.error("创建异常", ex);
            return back(null, "error", "创建异常，请联系管理员！");
        }


    }

    //创建新闻弹出页面
    @RequestMapping(value = "/v1/createNews", method = RequestMethod.POST)
    public String createNews(@RequestBody NewsReq req, HttpServletRequest request) {

        DataDTO data = null;
        try {

            if (StringUtils.isBlank(req.getTitle())) {
                return back(null, "error", "新闻标题不能为空！");
            }
            if (req.getTitle().length() > 200) {
                return back(null, "error", "新闻标题字符长度不能大于200个字符！");
            }

            if (StringUtils.isBlank(req.getContext())) {
                return back(null, "error", "新闻内容不能为空！");
            }
            if (req.getContext().length() > 10000) {
                return back(null, "error", "新闻内容字符长度不能大于10000个字符！");
            }

            if (StringUtils.isBlank(req.getDataSource())) {
                return back(null, "error", "来源不能为空！");
            }
            if (req.getDataSource().length() > 50) {
                return back(null, "error", "来源字符长度不能大于50个字符！");
            }
            //授权码
            String token = AppModConfig.GetHeadJsonReq(request, "Authorization");
            if (token == null) {
                return back(null, "error", "对不起，您没有权限！");
            }
            BasicBdUser basicBdUser = eduBdUserService.getBasicBdUser(token);
            log.info(JSON.toJSONString(basicBdUser));
            if (basicBdUser == null) {
                return back(null, "error", "对不起，请重新登录！");
            }
            NewsDTO newsDTO = new NewsDTO();
            newsDTO.setTitle(req.getTitle());
            newsDTO.setContext(req.getContext());
            newsDTO.setNewsTime(new Date());
            newsDTO.setCrePerson(basicBdUser.getId());
            newsDTO.setCreTime(new Date());
            newsDTO.setDataSource(req.getDataSource());
            newsService.createNews(newsDTO);
            return back(newsDTO, "1000", "成功");
        } catch (Exception ex) {
            log.error("创建异常", ex);
            return back(null, "error", "创建异常，请联系管理员！");
        }
    }


    //校验新增新闻权限
    @RequestMapping(value = "/v1/isCreateNews", method = RequestMethod.GET)
    public String isCreateNews(HttpServletRequest request) {
        //授权码
        String token = AppModConfig.GetHeadJsonReq(request, "Authorization");
        if (token == null) {
            return back(null, "error", "对不起，您没有权限！");
        }
        BasicBdUser basicBdUser = eduBdUserService.getBasicBdUser(token);
        log.info(JSON.toJSONString(basicBdUser));

        if (basicBdUser != null && basicBdUser.getOrgName().equals("市教委")) {
           return back(null, "success", "");
        }

        return back(null, "error", "对不起，您没有权限！");
    }


    /**
     * 组装返回数据
     *
     * @param data 返回内容
     * @return
     */
    private String back(Object data, String resCode, String resMsg) {
        NewsBackDTO backDTO = new NewsBackDTO();
        backDTO.setData(data);
        backDTO.setMsgId(1L);
        backDTO.setResCode(resCode);
        backDTO.setResMsg(resMsg);
        return JSON.toJSONString(backDTO);
    }

}
