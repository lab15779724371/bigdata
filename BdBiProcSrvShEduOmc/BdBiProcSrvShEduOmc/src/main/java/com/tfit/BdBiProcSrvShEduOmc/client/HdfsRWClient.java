package com.tfit.BdBiProcSrvShEduOmc.client;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FSDataInputStream;
import org.apache.hadoop.fs.FSDataOutputStream;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.tfit.BdBiProcSrvShEduOmc.util.FtpUtil;

//hdfs文件读写客户端
public class HdfsRWClient {	
	private static final Logger logger = LogManager.getLogger(HdfsRWClient.class.getName());
	
	//hdfs文件默认目录
	private static String defaultHdfsDir = "";
	//hdfs文件域名
	public static String hdfsDn = "hdfs://172.18.14.30:8020";
	//hdfs文件系统对象
	private static FileSystem curFs = null;
	
	//获取文件系统
	private static void getfileSystem() {
		if(curFs == null) {
			Configuration conf = new Configuration();
			conf.set("fs.hdfs.impl", org.apache.hadoop.hdfs.DistributedFileSystem.class.getName());
			URI uri_fs = null;
			try {
				uri_fs = new URI(hdfsDn);
			} catch (URISyntaxException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			if (uri_fs != null) {
				// 获取一个具体的文件系统对象
				try {
					curFs = FileSystem.get(
							// 创建一下HDFS文件系统的访问路径，就是Hadoop配置文件中的core-sit.xml中的HDFS文件系统的所在机器
							uri_fs,
							// 创建一个Hadoop的配置文件的类
							conf);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
    }
	
	//判断文件是否存在
	public static boolean isExistHdfs(String hdfsFileName) {
		boolean flag = false;
		//获取hdfs文件系统
        getfileSystem();
        //hdfs文件
        String srcFile = hdfsDn + defaultHdfsDir + hdfsFileName;
        if (curFs != null) {
        	Path path = new Path(srcFile);
        	try {
				flag = curFs.exists(path);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
        }
        if(flag)
        	logger.info("目录：" + srcFile + "，存在！");
        else
        	logger.info("目录：" + srcFile + "，不存在！");
		
		return flag;
	}
    
    //从HDFS文件系统下载文件，首先编写一个输入流，将内容输入到本地文件缓存，然后编写一个输出流，将内容输出到本地磁盘
    //srcPathFileName（源路径文件名），格式：/xx...xx/.../xx...xx/文件名
    //localPathName（本地路径名），格式：/xx...xx/.../xx...xx
    public static boolean hdfsFileDownload(String srcPathFileName, String localPathName,String localFileName) {
    	boolean flag = false;
    	int bufSize = 4096;
    	//获取hdfs文件系统
        getfileSystem();
        //文件下载
        String srcFile = hdfsDn + defaultHdfsDir + srcPathFileName, strInfo = null, localPathFileName = null;
		if (curFs != null) {
			// 构建一个输入流，将需要下载的文件写入到客户端的内存中
			FSDataInputStream in = null;
			try {
				Path path = new Path(srcFile);
				if(curFs.exists(path)) {
					in = curFs.open(path);
				}
			} catch (IllegalArgumentException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			// 构建一个输出流，将需要下载的文件从内存中写入到本地磁盘
			FileOutputStream out = null;
			try {
				String fileName = "";
				int idx = srcPathFileName.lastIndexOf("/");
				if(idx != -1) {
					fileName = srcPathFileName.substring(idx+1);
					if(localFileName !=null ) {
						localPathFileName = localPathName + "/" + localFileName;
					}else {
						localPathFileName = localPathName + "/" + fileName;
					}
				}
				if(localPathFileName != null) {
					out = new FileOutputStream(localPathFileName); 
				}
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			// 参数说明， in（代表输入流，读取HDFS文件系统的文件到本机内存中），out（代表输出流，将本机内存中的文件写入到本地磁盘中）
			// 4096（缓冲区大小），true（自动关闭流，如果不使用自动关闭的话需要手动关闭输入输出流）
			// 手动关闭输入输出流， IOUtils.closeStream(in)， IOUtils.closeStream(out)
			if(in != null && out != null) {
				flag = true;
				try {
					IOUtils.copyBytes(in, out, bufSize, true);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			if(flag)
				strInfo = "源文件：" + srcFile + "，目的文件：" + localPathFileName + "，下载成功！";
			else
				strInfo = "源文件：" + srcFile + "，目的文件：" + localPathFileName + "，下载失败！";
			logger.info(strInfo);
		}
		
		return flag;
    }
    
    //从HDFS文件系统下载文件，首先编写一个输入流，将内容输入到本地文件缓存，然后编写一个输出流，将内容输出到本地磁盘
    //srcPathFileName（源路径文件名），格式：/xx...xx/.../xx...xx/文件名
    //localPathName（本地路径名），格式：/xx...xx/.../xx...xx
    public static boolean hdfsFileDownloadToFTP(String srcPathFileName, String localPathName,String localFileName) {
    	boolean flag = false;
    	//获取hdfs文件系统
        getfileSystem();
        //文件下载
        String srcFile = hdfsDn + defaultHdfsDir + srcPathFileName, strInfo = null, localPathFileName = null;
		if (curFs != null) {
			// 构建一个输入流，将需要下载的文件写入到客户端的内存中
			FSDataInputStream in = null;
			try {
				Path path = new Path(srcFile);
				if(curFs.exists(path)) {
					in = curFs.open(path);
				}
			} catch (IllegalArgumentException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			// 构建一个输出流，将需要下载的文件从内存中写入到本地磁盘
			FileOutputStream out = null;
			try {
				String fileName = "";
				int idx = srcPathFileName.lastIndexOf("/");
				if(idx != -1) {
					fileName = srcPathFileName.substring(idx+1);
					if(localFileName !=null ) {
						localPathFileName = localPathName +  localFileName;
					}else {
						localPathFileName = localPathName +  fileName;
					}
				}
				if(localPathFileName != null) {
					out = new FileOutputStream(localPathFileName); 
				}
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			// 参数说明， in（代表输入流，读取HDFS文件系统的文件到本机内存中），out（代表输出流，将本机内存中的文件写入到本地磁盘中）
			// 4096（缓冲区大小），true（自动关闭流，如果不使用自动关闭的话需要手动关闭输入输出流）
			// 手动关闭输入输出流， IOUtils.closeStream(in)， IOUtils.closeStream(out)
			if(in != null && out != null) {
				flag = true;
				try {
				    
					byte[] contbuf = org.apache.commons.io.IOUtils.toByteArray(in.getWrappedStream());
					
					FtpUtil.ftpPicServer(localPathFileName, contbuf);
					
					if(out != null ) {
						out.close();
						out = null;
					}
			        
					if(in != null) {
						in.close();
						in = null;
					}
			        
					
					
					
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					logger.info("e:"+e.getMessage());
				}
			}
			if(flag)
				strInfo = "源文件：" + srcFile + "，目的文件：" + localPathFileName + "，下载成功！";
			else
				strInfo = "源文件：" + srcFile + "，目的文件：" + localPathFileName + "，下载失败！";
			logger.info(strInfo);
		}
		
		return flag;
    }
    
    //读取hdfs文件
    public static String readHdfsFile(String hdfsFileName) {
    	FileSystem fs = null;
    	String fileCont = null;
        try {
        	logger.info("读取的hdfs文件：" + hdfsDn + defaultHdfsDir + hdfsFileName);
            Configuration conf = new Configuration();                     // 加载配置文件
            conf.set("dfs.client.use.datanode.hostname", "true");         //"fs.hdfs.impl", org.apache.hadoop.hdfs.DistributedFileSystem.class.getName()); //
            URI uri = new URI(hdfsDn);                                    // 连接资源位置
            fs = FileSystem.get(uri,conf,"hadoop");                       // 创建文件系统实例对象            
            Path p= new Path(hdfsFileName);                               // 默认是读取/user/navy/下的指定文件
            FSDataInputStream fsin = fs.open(fs.getFileStatus(p).getPath());
            byte[] bs = new byte[1024 * 1024];
            int len = 0;
            while((len = fsin.read(bs)) != -1){
            }
            fsin.close();
            if(len > 0) {
            	fileCont = new String(bs, 0, len);
            }
        } catch (Exception e) {
        	logger.error("hdfs操作失败!!!", e);
        }
        
        return fileCont;
    }
    
    //上传文件到HDFS文件系统
    public static void testUpload() throws IllegalArgumentException, IOException{
    	int bufSize = 4096;
    	//获取hdfs文件系统
        getfileSystem();
		if (curFs != null) {
			// 构建一个输入流，将本机需要上传的文件写入到内存中
			FileInputStream in = new FileInputStream("/home/jizheng/myHdfs/shanghai_log.1531152001825");
			// 构建一个输出流，将客户端内存的数据写入到HDFS文件系统指定的路径中
			FSDataOutputStream out = curFs.create(new Path("/testMK/shanghai_log.1531152001825"), true);
			// 参数说明，in（代表输入流，读取HDFS文件系统的文件到本机内存中）， out（代表输出流，将本机内存中的文件写入到本地磁盘中）
			// 4096（缓冲区大小）， true（自动关闭流，如果不使用自动关闭的话需要手动关闭输入输出流）
			// 手动关闭输入输出流，IOUtils.closeStream(in)，IOUtils.closeStream(out)
			IOUtils.copyBytes(in, out, bufSize, true);
		}
    }
    
    //上传文件到HDFS文件系统
    public static boolean hdfsFileUpload(String localPathFileName, String distPathName) {
    	boolean flag = false;
    	int bufSize = 4096;
    	//获取hdfs文件系统
        getfileSystem();
        //文件上传
        String hdfsPathName = null, strInfo = null;
		if (curFs != null) {
			// 构建一个输入流，将本机需要上传的文件写入到内存中
			FileInputStream in = null;
			try {
				in = new FileInputStream(localPathFileName);
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			if (in != null) {
				String localFileName = "";
				int idx = localPathFileName.lastIndexOf("/");
				if (idx != -1) {
					localFileName = localPathFileName.substring(idx + 1);
				}
				// 构建一个输出流，将客户端内存的数据写入到HDFS文件系统指定的路径中
				if (!localFileName.isEmpty()) {
					hdfsPathName = hdfsDn + defaultHdfsDir + distPathName;
					Path path = new Path(hdfsPathName);
					try {
						if (curFs.exists(path)) {
							hdfsPathName += "/" + localFileName;
							FSDataOutputStream out = curFs.create(new Path(hdfsPathName), true);
							// 参数说明，in（代表输入流，读取HDFS文件系统的文件到本机内存中）， out（代表输出流，将本机内存中的文件写入到本地磁盘中）
							// 4096（缓冲区大小）， true（自动关闭流，如果不使用自动关闭的话需要手动关闭输入输出流）
							// 手动关闭输入输出流，IOUtils.closeStream(in)，IOUtils.closeStream(out)
							IOUtils.copyBytes(in, out, bufSize, true);
							flag = true;
						}
					} catch (IllegalArgumentException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		}
		if(flag)
			strInfo = "源文件：" + localPathFileName + "，目的文件：" + hdfsPathName + "，上传成功！";
		else
			strInfo = "源文件：" + localPathFileName + "，目的文件：" + hdfsPathName + "，上传失败！";
		logger.info(strInfo);
		
		return flag;
    }
    
    // 创建一个目录
    public static boolean hdfsMakeDir(String DirName) {
        boolean isSuccess = false;
        if(isExistHdfs(DirName))
    		return true;
        //获取hdfs文件系统
        getfileSystem();
        //目录名称
        String curDirName = null;
        if(DirName.charAt(0) == '/')
        	curDirName = hdfsDn + defaultHdfsDir + DirName;
        else
        	curDirName = hdfsDn + defaultHdfsDir + "/" + DirName;
		try {
			if(curFs != null) {
				isSuccess = curFs.mkdirs(new Path(curDirName));
			}
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        if(isSuccess)
        	logger.info("创建目录：" + curDirName + "，创建成功！");
        else
        	logger.info("创建目录：" + curDirName + "，创建失败！");
        
        return isSuccess;
    }
    
    // 删除目录/文件
    public static boolean hdfsDelDirFile(String DirName) {
    	boolean isSuccess = false;
    	//获取hdfs文件系统
        getfileSystem();
        //目录名称
        String curDirName = null;
        if(DirName.charAt(0) == '/')
        	curDirName = hdfsDn + defaultHdfsDir + DirName;
        else
        	curDirName = hdfsDn + defaultHdfsDir + "/" + DirName;
    	if(curFs != null) {
    		try {
    			// 指定要删除的目录
    			Path delPath = new Path(curDirName);
    			//是否使用递归删除
    			boolean isRecursion = true;
    			isSuccess = curFs.delete(delPath, isRecursion);
			} catch (IllegalArgumentException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
    	}
    	if(isSuccess)
        	logger.info("删除目录：" + hdfsDn + curDirName + "，删除成功！");
        else
        	logger.info("删除目录：" + hdfsDn + curDirName + "，删除失败！");
    	
    	return isSuccess;
    }
    
    //获取 hdfs文件列表
  	public static List<String> GetHdfsFileList(String curDir) {
  		List<String> hdfsList = null;
  		String hdfsDir = ""; //hdfs + "/" + curDir;
  		 if(curDir.charAt(0) == '/')
  			hdfsDir = hdfsDn + defaultHdfsDir + curDir;
         else
        	 hdfsDir = hdfsDn + defaultHdfsDir + "/" + curDir;
  		Configuration conf = new Configuration();
  		conf.set("fs.hdfs.impl", org.apache.hadoop.hdfs.DistributedFileSystem.class.getName());  
  		FileSystem fs = null;
		try {
			fs = FileSystem.get(URI.create(hdfsDir), conf);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
  		logger.info(URI.create(hdfsDir));
		if (hdfsDir != null) {
			FileStatus[] status = null;
			try {
				status = fs.listStatus(new Path(hdfsDir));
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IllegalArgumentException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			if (status != null) {
				hdfsList = new ArrayList<>();
				for (FileStatus file : status) {
					String curFileName = file.getPath().getName();
					hdfsList.add(curFileName);
					logger.info(hdfsDir + "/" + curFileName);
				}
			}
		}
		
		return hdfsList;
  	}
	
	//读取 hdfs文件
	public static void ReadFile(String hdfs) throws IOException {
		Configuration conf = new Configuration();
		conf.set("fs.hdfs.impl", org.apache.hadoop.hdfs.DistributedFileSystem.class.getName());  
		FileSystem fs = FileSystem.get(URI.create(hdfs), conf);
		logger.info(URI.create(hdfs));
		FileStatus[] status = fs.listStatus(new Path(hdfs));
		ByteArrayOutputStream bos;
		DataOutputStream dos;
		for (FileStatus file : status) {
			logger.info(file.getPath().getName());
			FSDataInputStream hdfsInStream = fs.open(file.getPath());
			byte[] ioBuffer = new byte[1024];
			int readLen = hdfsInStream.read(ioBuffer);
			bos = new ByteArrayOutputStream();
 			dos = new DataOutputStream(bos);
 			if(readLen != -1)
				dos.write(ioBuffer, 0, readLen);
			while (readLen != -1) {
				System.out.write(ioBuffer, 0, readLen);
				readLen = hdfsInStream.read(ioBuffer);
				if(readLen != -1)
					dos.write(ioBuffer, 0, readLen);
			}
			byte[] biCont = bos.toByteArray();
			String strCont = new String(biCont, 0, biCont.length);
			logger.info("\n" + "biCont = " + strCont + "\n");
			hdfsInStream.close();
		}
	}
	
	//按天时读取hdfs文件
	//@param 日期 date （格式：yyyy-MM-dd），时刻 hour （范围：0～23）
	public static List<String> getDateHourFileList(String date, int hour) {
		List<String> lineList = null;
		Configuration conf = new Configuration();
		conf.set("fs.hdfs.impl", org.apache.hadoop.hdfs.DistributedFileSystem.class.getName());
		String curHdfs = hdfsDn + defaultHdfsDir + "/" + date + "/" + String.format("%02d", hour);
		FileSystem fs = null;
		try {
			fs = FileSystem.get(URI.create(curHdfs), conf);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		logger.info(URI.create(curHdfs));
		if (fs != null) {
			FileStatus[] status = null;
			try {
				Path path = new Path(curHdfs);
				if(fs.exists(path))
					status = fs.listStatus(path);
				else
					logger.info("文件路径：" + curHdfs + "，不存在！");
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IllegalArgumentException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			ByteArrayOutputStream bos;
			DataOutputStream dos;
			if(status != null) {
				lineList = new ArrayList<>();
				for (FileStatus file : status) {
					logger.info(file.getPath().getName());
					FSDataInputStream hdfsInStream = null;
					try {
						hdfsInStream = fs.open(file.getPath());
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					if (hdfsInStream != null) {
						byte[] ioBuffer = new byte[1024];
						int readLen = 0;
						try {
							readLen = hdfsInStream.read(ioBuffer);
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						bos = new ByteArrayOutputStream();
						dos = new DataOutputStream(bos);
						if (readLen != -1) {
							try {
								dos.write(ioBuffer, 0, readLen);
							} catch (IOException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						}
						while (readLen != -1) {
							try {
								readLen = hdfsInStream.read(ioBuffer);
							} catch (IOException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
							if (readLen != -1) {
								try {
									dos.write(ioBuffer, 0, readLen);
								} catch (IOException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
							}
						}
						byte[] biCont = bos.toByteArray();
						String strCont = new String(biCont, 0, biCont.length);
						String[] strConts = strCont.split("\n");
						for(int i = 0; i < strConts.length; i++) {
							lineList.add(strConts[i]);
						}
						try {
							hdfsInStream.close();
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				}
			}
		}
		
		return lineList;
	}
	
	public static void test(String[] args) throws IOException {
		String hdfs = hdfsDn + defaultHdfsDir + "/18-07-10/00/";
		HdfsRWClient.ReadFile(hdfs);
	}
}
