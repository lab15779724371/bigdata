package com.mycode

import org.apache.spark.SparkConf
import org.apache.spark.sql.SparkSession

/**
 * @author liuanbo
 * @creat 2021-01-28-0:48
 * @see 2194550857@qq.com
 *
 */
object
Spark06_SparkSQL_Test1 {
  def main(args: Array[String]): Unit = {
    System.setProperty("HADOOP_USER_NAME", "root")
    val conf = new SparkConf().setMaster("local[*]").setAppName("sparksql")

    val session = SparkSession.builder().config(conf).getOrCreate()

    session.sql(
      """
        |
        |select
        |
        |from (
        |select
        |
        |)
        |
        |
        |
        |
        |
        |
        |
        |""".stripMargin)


  }
}
