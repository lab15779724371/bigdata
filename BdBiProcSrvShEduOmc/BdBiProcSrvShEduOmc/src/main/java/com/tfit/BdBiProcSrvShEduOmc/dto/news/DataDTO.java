package com.tfit.BdBiProcSrvShEduOmc.dto.news;

import lombok.Data;

@Data
public class DataDTO {

    public DataDTO(String dataCode,String dataContent){
        this.dataCode = dataCode;
        this.dataContent = dataContent;
    }
    private String dataCode;

    private String dataContent;
}
