package com.mycode

import org.apache.spark.SparkConf
import org.apache.spark.sql.SparkSession

/**
 * @author liuanbo
 * @creat 2021-01-26-0:16
 * @see 2194550857@qq.com
 *
 */
object Spark05_SparkSQL_Hive {
  def main(args: Array[String]): Unit = {
    val conf = new SparkConf().setAppName("sparksql").setMaster("local[*]")

    val session = SparkSession.builder().config(conf).enableHiveSupport().getOrCreate()

    //使用SparkSql连接外置Hive
    //1.拷贝Hive-size.xml 文件到classpath
    //2.启动hive的延迟
    //3.增加对应的依赖关系（包含mysql驱动）

    session.sql("show tables").show()

    session.close()
  }
}
