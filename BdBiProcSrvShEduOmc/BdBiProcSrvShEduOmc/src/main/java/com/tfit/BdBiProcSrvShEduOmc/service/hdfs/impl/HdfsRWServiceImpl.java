package com.tfit.BdBiProcSrvShEduOmc.service.hdfs.impl;

import com.tfit.BdBiProcSrvShEduOmc.service.hdfs.HdfsRWService;
import com.tfit.BdBiProcSrvShEduOmc.util.HdfsRWUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * @Descritpion：hdfs读写服务实现
 * @author: tianfang_infotech
 * @date: 2019/1/14 9:44
 */
@Slf4j
@Service
public class HdfsRWServiceImpl implements HdfsRWService {

    @Override
    public String readFile(String fileName) throws IOException {

        if (StringUtils.isEmpty(fileName)) {
            return null;
        }

        return HdfsRWUtil.readFile(fileName);
    }

    @Override
    public List<String> getHdfsFileList(String currentDirectory) {

        if (StringUtils.isEmpty(currentDirectory)) {
            return new ArrayList<>(0);
        }

        return HdfsRWUtil.getHdfsFileList(pathValidate(currentDirectory));
    }

    /**
     * 路径验证
     *
     * @param path
     * @return
     */
    private String pathValidate(String path) {
//        if (!path.startsWith(HDFSConstant.HDFS_DOMAIN_PROTOCAL_TYPE)) {
//            if (path.charAt(0) == HDFSConstant.CHAR_PATH_SYMBOL) {
//                path = HdfsRWUtil.HDFS_DOMAIN + path;
//            } else {
//                path = HdfsRWUtil.HDFS_DOMAIN + path;
//            }
//        }
        return path;
    }

}
