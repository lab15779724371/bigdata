package com.tfit.BdBiProcSrvShEduOmc.dto.news;

import lombok.Data;

@Data
public class NewsReq {
    private Long id;

    private String title;

    private String context;

    private String dataSource;
}
