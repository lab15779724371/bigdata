package com.tfit.BdBiProcSrvShEduOmc.service.hdfs;

import java.io.IOException;
import java.util.List;

/**
 * @Descritpion：hdfs读写服务
 * @author: tianfang_infotech
 * @date: 2019/1/14 9:43
 */
public interface HdfsRWService {

    /**
     * 读取文件字符串
     *
     * @param fileName
     * @return
     */
    String readFile(String fileName) throws IOException;

    /**
     * 获取某目录下 hdfs 文件列表
     *
     * @param currentDirectory
     * @return
     */
    List<String> getHdfsFileList(String currentDirectory);
}
