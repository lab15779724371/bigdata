package com.tfit.BdBiProcSrvShEduOmc.dto.news;

import lombok.Data;

import java.util.Date;
@Data
public class NewsDTO {

    private Long id;
    private String title;
    private String context;
    private Date newsTime;
    private String dataSource;
    private Date creTime;
    private String crePerson;


}
