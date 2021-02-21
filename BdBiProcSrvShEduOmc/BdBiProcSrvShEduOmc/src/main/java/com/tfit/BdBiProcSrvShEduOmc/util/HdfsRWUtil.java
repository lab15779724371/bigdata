package com.tfit.BdBiProcSrvShEduOmc.util;

import com.tfit.BdBiProcSrvShEduOmc.config.SpringConfig;
import com.tfit.BdBiProcSrvShEduOmc.constant.HDFSConstant;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FSDataInputStream;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IOUtils;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.*;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

/**
 * @Descritpion：HDFS读写工具
 * @author: tianfang_infotech
 * @date: 2019/1/14 11:32
 */
public class HdfsRWUtil {
	private static final Logger logger = LogManager.getLogger(HdfsRWUtil.class.getName());

    static {
        HDFS_DOMAIN = SpringConfig.hdfscluster0_url;
        HDFS_DIRECTORY_DEFAULT = SpringConfig.hdfscluster0_dir_default;
    }

    /**
     * hdfs 配置类
     */
    private final static String HDFS_CONFIG_NAME = "fs.hdfs.impl";

    /**
     * hdfs文件默认目录
     */
    private static String HDFS_DIRECTORY_DEFAULT;

    /**
     * hdfs 文件域名
     */
    public static String HDFS_DOMAIN;

    /**
     * hdfs 文件系统对象
     */
    private static FileSystem curFileSystem = null;

    /**
     * 读取文件信息
     *
     * @param hdfsFileName
     * @return
     * @throws IOException
     */
    public static String readFile(String hdfsFileName) throws IOException {

        Configuration conf = new Configuration();
        conf.set(HDFS_CONFIG_NAME, org.apache.hadoop.hdfs.DistributedFileSystem.class.getName());

        String path = HDFS_DOMAIN + HDFS_DIRECTORY_DEFAULT + hdfsFileName;

        FileSystem fs = FileSystem.get(URI.create(path), conf);

        logger.info(String.valueOf(URI.create(path)));

        FileStatus[] status = fs.listStatus(new Path(path));
        ByteArrayOutputStream bos;
        DataOutputStream dos;
        for (FileStatus file : status) {

            logger.info("\n => [HDFS_content_url] = {}\n", file.getPath().getName());

            FSDataInputStream inputStream = fs.open(file.getPath());

            byte[] ioBuffer = new byte[1024];

            try {

                int readLen = inputStream.read(ioBuffer);

                bos = new ByteArrayOutputStream();

                dos = new DataOutputStream(bos);

                if (readLen != -1) {
                    dos.write(ioBuffer, 0, readLen);
                }

                while (readLen != -1) {

                    System.out.write(ioBuffer, 0, readLen);

                    readLen = inputStream.read(ioBuffer);
                    if (readLen != -1) {
                        dos.write(ioBuffer, 0, readLen);
                    }
                }

                byte[] bytes = bos.toByteArray();

                String content = new String(bytes, 0, bytes.length);

                logger.info("\n => [HDFS_content] = {}\n" + content);

                return content;

            } catch (RuntimeException ex) {
                logger.error(ex.getMessage(), ex);
            } finally {
                inputStream.close();
            }
        }

        return null;
    }

    /**
     * @Description: 获取文件系统
     * @author:
     * @date: 2019/1/14 10:24
     */
    private static void getFileSystem() {
        if (curFileSystem == null) {
            Configuration conf = new Configuration();
            conf.set(HDFS_CONFIG_NAME, org.apache.hadoop.hdfs.DistributedFileSystem.class.getName());
            URI uri = null;

            try {
                uri = new URI(HDFS_DOMAIN);
            } catch (URISyntaxException e1) {
                logger.error(e1.getMessage(), e1);
            }
            if (uri != null) {
                // 获取一个具体的文件系统对象
                try {
                    curFileSystem = FileSystem.get(
                            // 创建一下HDFS文件系统的访问路径，就是Hadoop配置文件中的core-sit.xml中的HDFS文件系统的所在机器
                            uri,
                            // 创建一个Hadoop的配置文件的类
                            conf);
                } catch (IOException e) {
                    logger.error(e.getMessage(), e);
                }
            }
        }
    }

    /**
     * 判断文件是否存在
     *
     * @param hdfsFileName
     * @return
     */
    public static boolean isExistHdfs(String hdfsFileName) {
        boolean flag = false;
        //获取hdfs文件系统
        getFileSystem();

        //hdfs文件
        String srcFile = HDFS_DOMAIN + HDFS_DIRECTORY_DEFAULT + hdfsFileName;
        if (curFileSystem != null) {
            Path path = new Path(srcFile);
            try {
                flag = curFileSystem.exists(path);
            } catch (IOException e) {
                logger.error(e.getMessage(), e);
            }
        }

        if (flag) {
            logger.info("目录：{}，存在！", srcFile);
        } else {
            logger.info("目录：{}，不存在！", srcFile);
        }

        return flag;
    }

    /**
     * 从HDFS文件系统下载文件，首先编写一个输入流，将内容输入到本地文件缓存，然后编写一个输出流，将内容输出到本地磁盘
     * srcPathFileName（源路径文件名），格式：/xx...xx/.../xx...xx/文件名
     * localPathName（本地路径名），格式：/xx...xx/.../xx...xx
     *
     * @param srcPathFileName
     * @param localPathName
     * @return
     */
    public static boolean downloadHdfsFile(String srcPathFileName, String localPathName) {
        boolean flag = false;
        int bufSize = 4096;

        //获取hdfs文件系统
        getFileSystem();

        //文件下载
        String srcFile = HDFS_DOMAIN + HDFS_DIRECTORY_DEFAULT + srcPathFileName;
        String localPathFileName = null;

        if (curFileSystem != null) {
            // 构建一个输入流，将需要下载的文件写入到客户端的内存中
            FSDataInputStream in = null;
            try {
                Path path = new Path(srcFile);
                if (curFileSystem.exists(path)) {
                    in = curFileSystem.open(path);
                }
            } catch (IllegalArgumentException e) {
                logger.error(e.getMessage(), e);
            } catch (IOException e) {
                logger.error(e.getMessage(), e);
            }

            // 构建一个输出流，将需要下载的文件从内存中写入到本地磁盘
            FileOutputStream out = null;
            try {
                String fileName;
                int idx = srcPathFileName.lastIndexOf("/");
                if (idx != -1) {
                    fileName = srcPathFileName.substring(idx + 1);
                    localPathFileName = localPathName + "/" + fileName;
                }
                if (localPathFileName != null) {
                    out = new FileOutputStream(localPathFileName);
                }
            } catch (FileNotFoundException e) {
                logger.error(e.getMessage(), e);
            }
            // 参数说明， in（代表输入流，读取HDFS文件系统的文件到本机内存中），out（代表输出流，将本机内存中的文件写入到本地磁盘中）
            // 4096（缓冲区大小），true（自动关闭流，如果不使用自动关闭的话需要手动关闭输入输出流）
            // 手动关闭输入输出流， IOUtils.closeStream(in)， IOUtils.closeStream(out)
            if (in != null && out != null) {
                flag = true;
                try {
                    IOUtils.copyBytes(in, out, bufSize, true);
                } catch (IOException e) {
                    logger.error(e.getMessage(), e);
                }
            }

            if (flag) {
                logger.info("源文件：{}, 目的文件：{}, 下载成功！", srcFile, localPathFileName);
            } else {
                logger.info("源文件：{}, 目的文件：{}, 下载失败！", srcFile, localPathFileName);
            }
        }

        return flag;
    }

    /**
     * 获取 hdfs文件列表
     *
     * @param currentDirectory 当前目录
     * @return
     */
    public static List<String> getHdfsFileList(String currentDirectory) {
        List<String> hdfsList = new ArrayList<>();
        //hdfs + "/" + currentDirectory;
        String hdfsDir;

        if (currentDirectory.charAt(0) == HDFSConstant.CHAR_PATH_SYMBOL) {
            hdfsDir = HDFS_DOMAIN + HDFS_DIRECTORY_DEFAULT + currentDirectory;
        } else {
            hdfsDir = HDFS_DOMAIN + HDFS_DIRECTORY_DEFAULT + "/" + currentDirectory;
        }

        Configuration conf = new Configuration();
        conf.set(HDFS_CONFIG_NAME, org.apache.hadoop.hdfs.DistributedFileSystem.class.getName());
        FileSystem fs = null;
        try {
            fs = FileSystem.get(URI.create(hdfsDir), conf);
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
        }

        logger.info(String.valueOf(URI.create(hdfsDir)));

        if (hdfsDir != null) {
            FileStatus[] status = null;
            try {
                status = fs.listStatus(new Path(hdfsDir));
            } catch (FileNotFoundException e) {
                logger.error(e.getMessage(), e);
            } catch (IllegalArgumentException e) {
                logger.error(e.getMessage(), e);
            } catch (IOException e) {
                logger.error(e.getMessage(), e);
            }
            if (status != null) {
                for (FileStatus file : status) {
                    String curFileName = file.getPath().getName();
                    hdfsList.add(curFileName);
                    logger.info(hdfsDir + "/" + curFileName);
                }
            }
        }

        return hdfsList;
    }

    /**
     * 文件下载
     *
     * @param src
     * @param dst
     * @param conf
     * @return
     */
    private static boolean getFromHdfs(String src, String dst, Configuration conf) {
        Path dstPath = new Path(dst);
        try {
            FileSystem fileSystem = dstPath.getFileSystem(conf);
            fileSystem.copyToLocalFile(false, new Path(src), dstPath);
        } catch (IOException ie) {
            ie.printStackTrace();
            logger.error(ie.getMessage(), ie);
            return false;
        }
        return true;
    }
}