package com.tfit.BdBiProcSrvShEduOmc.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Serializable;
import java.net.URLEncoder;
import java.util.UUID;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;

import javax.servlet.http.HttpServletResponse;

/**
 * 文件处理工具类.
 * 
 * @author jzm
 * @version 1.0
 */
public class FileUtils extends org.apache.commons.io.FileUtils {

    /**
     * 输出文件到磁盘
     * 
     * @param data
     * @param filePathAndName
     * @throws Exception
     */
    public static void writeFileToDisk(byte[] data, String filePath)
            throws Exception {
        // 文件输出流
        FileOutputStream fos = null;
        if (StringUtils.isNotEmpty(filePath) && null != data) {
            fos = new FileOutputStream(filePath);
            fos.write(data);
            fos.close();
        }
    }

    /**
     * 获得文件byte[]
     * 
     * @param filePath
     * @throws Exception
     * @throws Exception
     */
    public static byte[] readFileToByteArray(String filePath) throws Exception {
        File file = new File(filePath);
        InputStream in = new FileInputStream(file);
        try {
            return IOUtils.toByteArray(in);
        } finally {
            IOUtils.closeQuietly(in);
        }
    }

    /**
     * 获得随机文件名
     * 
     * @param fileName
     * @return
     */
    public static String getUUIDName(String fileName) {
        String[] split = fileName.split("\\.");
        String extendFile = "." + split[(split.length - 1)].toLowerCase();
        return UUID.randomUUID().toString() + extendFile;
    }

    /**
     * 获得随机文件ID
     * 
     * @param fileName
     * @return
     */
    public static String getUUID() {
        return UUID.randomUUID().toString();
    }

    /**
     * 获得文件名
     * 
     * @param fileName
     * @return
     */
    public static String getFileName(String fileName) {
        String[] split = fileName.split("\\.");
        return split[0];
    }

    /**
     * 获得网络文件名（带扩展名）
     * 
     * @param fileName
     * @return
     */
    public static String getUrlFileName(String url) {
        return url.substring(url.lastIndexOf("/") + 1);
    }

    /**
     * 获得文件类型
     * 
     * @param fileName
     * @return
     */
    public static String getFileTypeByName(String fileName) {
        String[] split = fileName.split("\\.");
        String extendFile = split[(split.length - 1)].toLowerCase();
        return extendFile;
    }

    /**
     * 创建文件
     * 
     * @param filePath
     *            文件路径
     * @param content
     *            文件内容
     * @param encoder
     *            文件编码
     * @return
     */
    public static void createFileWithEncoder(String filePath, String content,
            String encoder) throws Exception {
        // 创建文件输出流
        FileOutputStream fos = new FileOutputStream(filePath, false);
        OutputStreamWriter osw = new OutputStreamWriter(fos, encoder);
        osw.write(content);

        // 关闭流
        osw.close();
        fos.close();
    }

    /**
     * 创建目录
     * 
     * @param dirs
     *            文件路径
     * @return
     */
    public static void createDirs(String dirs) throws Exception {
        File file = new File(dirs);
        file.mkdirs();
    }

    /**
     * 保存文件到指定路径
     * 
     * @param f
     *            文件
     * @param path
     *            路径
     * @throws Exception
     */
    public static String save(File f, String path) throws Exception {
        File dest = new File(path);
        if (!dest.getParentFile().exists())
            dest.getParentFile().mkdirs();
        FileUtils.copyFile(f, dest);
        return path;
    }

    /**
     * 根据路径来删除文件
     * 
     * @param path
     * @throws IOException
     */
    public static void delete(String path) throws Exception {
        File dest = new File(path);
        if (dest.exists()) {
            FileUtils.forceDelete(dest);
        }
    }

    /**
     * 根据路径来删除文件
     * 
     * @param path
     * @throws IOException
     */
    public static void delete(File dest) throws Exception {
        if (dest.exists()) {
            FileUtils.forceDelete(dest);
        }
    }

    /**
     * 新建目录 可建立多级目录
     * 
     * @param folderPath
     *            String 如 c:/fqf/abc
     * @return boolean
     */
    public static void newFolders(String folderPath) {
        String dir[];
        try {
            String filePath = folderPath;
            filePath = filePath.toString();
            java.io.File myFilePath = new java.io.File(filePath);
            if (!myFilePath.exists()) {
                myFilePath.mkdir();
            }
            dir = folderPath.replace('\\', '/').split("/");
            String basePath = "";
            for (int i = 0; i < dir.length; i++) {
                basePath += dir[i] + File.separator;
                File subFile = new File(basePath);
                if (subFile.exists() == false)
                    subFile.mkdir();
            }
        } catch (Exception e) {
            System.out.println("新建目录操作出错");
            e.printStackTrace();
        }
    }

    /**
     * 新建文件
     * 
     * @param filePathAndName
     *            String 文件路径及名称 如c:/fqf.txt
     * @param fileContent
     *            String 文件内容
     * @return boolean
     */
    public static void newFile(String filePath, String fileName,
            String fileContent) {
        String filePathAndName = filePath;
        if (filePath.endsWith(File.separator)) {
            filePathAndName += fileName;
        } else {
            filePathAndName += File.separator + fileName;
        }
        File f = new File(filePath);
        if (!f.exists()) {
            f.mkdirs();
        }
        newFile(filePathAndName, fileContent);
    }

    /**
     * 新建文件
     * 
     * @param filePathAndName
     *            String 文件路径及名称 如c:/fqf.txt
     * @param fileContent
     *            String 文件内容
     * @return boolean
     */
    public static void newFile(String filePathAndName, String fileContent) {

        try {
            String filePath = filePathAndName;
            filePath = filePath.toString();
            File myFilePath = new File(filePath);
            if (!myFilePath.exists()) {
                myFilePath.createNewFile();
            }

            OutputStreamWriter out = new OutputStreamWriter(
                    new FileOutputStream(myFilePath), "UTF-8");
            out.write(fileContent);
            out.flush();
            out.close();

        } catch (Exception e) {
            System.out.println("新建文件操作出错");
            e.printStackTrace();

        }

    }

    /**
     * 把字节数组转换为对象
     * 
     * @param bytes
     * @return
     * @throws IOException
     * @throws ClassNotFoundException
     */
    public static final Object bytesToObject(byte[] bytes) throws IOException,
            ClassNotFoundException {
        ByteArrayInputStream in = new ByteArrayInputStream(bytes);
        ObjectInputStream oi = new ObjectInputStream(in);
        Object o = oi.readObject();
        oi.close();
        return o;
    }

    /**
     * 把可序列化对象转换成字节数组
     * 
     * @param s
     *            必须是可序列化的对象必须实现Serializable
     * @return
     * @throws IOException
     */
    public static final byte[] objectToBytes(Serializable s) throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        ObjectOutputStream ot = new ObjectOutputStream(out);
        ot.writeObject(s);
        ot.flush();
        ot.close();
        return out.toByteArray();
    }

    /**
     * 把16进制字符串转换成字节数组
     * 
     * @param hex
     * @return
     */
    public static byte[] hexStringToByte(String hex) {
        int len = (hex.length() / 2);
        byte[] result = new byte[len];
        char[] achar = hex.toCharArray();
        for (int i = 0; i < len; i++) {
            int pos = i * 2;
            result[i] = (byte) (toByte(achar[pos]) << 4 | toByte(achar[pos + 1]));
        }
        return result;
    }

    private static byte toByte(char c) {
        byte b = (byte) "0123456789ABCDEF".indexOf(c);
        return b;
    }

    /**
     * 把字节数组转换成16进制字符串
     * 
     * @param bArray
     * @return
     */
    public static final String bytesToHexString(byte[] bArray) {
        StringBuffer sb = new StringBuffer(bArray.length);
        String sTemp;
        for (int i = 0; i < bArray.length; i++) {
            sTemp = Integer.toHexString(0xFF & bArray[i]);
            if (sTemp.length() < 2)
                sb.append(0);
            sb.append(sTemp.toUpperCase());
        }
        return sb.toString();
    }

    /**
     * 
     * @param s将对象转换成16进制字符串
     * @return
     * @throws IOException
     */
    public static final String objectToHexString(Serializable s)
            throws IOException {
        return bytesToHexString(objectToBytes(s));
    }

    /**
     * 
     * @param hex
     *            将16进制字符串转换成对象
     * @return
     * @throws IOException
     * @throws ClassNotFoundException
     */
    public static final Object hexStringToObject(String hex)
            throws IOException, ClassNotFoundException {
        return bytesToObject(hexStringToByte(hex));
    }

    public static final void exportFile(OutputStream stream, File sourceFile) throws IOException {
        InputStream in = new FileInputStream(sourceFile);
        byte[] fileBytes = new byte[(int) sourceFile.length()]; //创建合适文件大小的数组
        in.read(fileBytes); //读取文件中的内容
        in.close();
        
        stream.write(fileBytes);
        stream.flush();
        stream.close();
    }

    public static final void exportFile(HttpServletResponse response, File sourceFile, String exportFileName) throws IOException {
        response.setCharacterEncoding("UTF-8");
        exportFileName = URLEncoder.encode(exportFileName, "UTF-8");
        response.addHeader("Content-Disposition", "attachment;filename=" + exportFileName);
        response.setContentType("application/vnd.ms-excel");

        OutputStream stream = response.getOutputStream();
        exportFile(stream, sourceFile);
    }
}