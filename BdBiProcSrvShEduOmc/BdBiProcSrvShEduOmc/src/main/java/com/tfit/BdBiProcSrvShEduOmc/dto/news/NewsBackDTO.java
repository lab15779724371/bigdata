package com.tfit.BdBiProcSrvShEduOmc.dto.news;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;


@Data
public class NewsBackDTO<T>  implements Serializable {

    T data;
    long msgId;
    String resCode;
    String resMsg;
    Long timestap = System.currentTimeMillis();
    String version = "1.0";

}
