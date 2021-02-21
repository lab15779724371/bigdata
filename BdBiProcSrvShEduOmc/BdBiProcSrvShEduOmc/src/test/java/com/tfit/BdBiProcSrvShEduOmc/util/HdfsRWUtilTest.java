package com.tfit.BdBiProcSrvShEduOmc.util;

import com.tfit.BdBiProcSrvShEduOmc.BaseTests;
import org.apache.hadoop.fs.FsUrlStreamHandlerFactory;
import org.apache.hadoop.io.IOUtils;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * @Descritpion：Hdfa读写测试
 * @author: yanzhao_xu
 * @date: 2019/1/18 10:57
 */
public class HdfsRWUtilTest extends BaseTests {

    @BeforeClass
    public static void beforeClass() {
        URL.setURLStreamHandlerFactory(new FsUrlStreamHandlerFactory());
    }

    @Test
    public void readFile() throws IOException {

        //HdfsRWUtil.readFile("");
    }

    @Test
    public void isExistHdfs() {
    }

    @Test
    public void downloadHdfsFile() {
    }

    @Test
    public void getHdfsFileList() {
    }

    public void test() {
        InputStream in = null;
        try {

            in = new URL("hdfs://192.168.28.136/output/test/part-r-00000").openStream();

            //in = new URL(HDFS_DOMAIN + HDFS_DIRECTORY_DEFAULT + "/18-07-10/00/").openStream();

            IOUtils.copyBytes(in, System.out, 2048, false);

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            IOUtils.closeStream(in);
        }
    }
}