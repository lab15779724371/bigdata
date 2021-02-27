package com.kd

import org.apache.spark.SparkConf
import org.apache.spark.sql.SparkSession

/**
 * @author liuanbo
 * @creat 2021-01-17-21:45
 * @see 2194550857@qq.com
 *
 */
object sparksql {
  def main(args: Array[String]): Unit = {

    val conf = new SparkConf().setAppName("app").setMaster("local[*]")

    val session = SparkSession.builder().config(conf).getOrCreate()

    val dataFrame = session.read.json("data/user.json")
    dataFrame.show()
    //  dataFrame.createTempView("user")
    //
    //    apache.spark.sql("select * from user").show
    session.close()
  }
}
