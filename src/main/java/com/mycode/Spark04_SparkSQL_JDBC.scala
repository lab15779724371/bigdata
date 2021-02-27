package com.mycode

import org.apache.spark.SparkConf
import org.apache.spark.sql.{SaveMode, SparkSession}

/**
 * @author liuanbo
 * @creat 2021-01-24-20:53
 * @see 2194550857@qq.com
 *
 */
object Spark04_SparkSQL_JDBC {
  def main(args: Array[String]): Unit = {
    val conf = new SparkConf().setMaster("local[*]").setAppName("sparksql")

    val session = SparkSession.builder().config(conf).getOrCreate()

    //读取mysql
    val df = session.read.format("jdbc").
      option("url", "jdbc:mysql://hadoop102:3306/sparksql")
      .option("driver", "com.mysql.jdbc.Driver")
      .option("user", "root")
      .option("password", "lab11230808")
      .option("dbtable", "user")
      .load()


    //    df.show()
    //保存数据
    df.write.format("jdbc")
      .option("url", "jdbc:mysql://hadoop102:3306/sparksql")
      .option("driver", "com.mysql.jdbc.Driver")
      .option("user", "root")
      .option("password", "lab11230808")
      .option("dbtable", "user1")
      .mode(SaveMode.Append)
      .save()


    session.close()

  }
}
