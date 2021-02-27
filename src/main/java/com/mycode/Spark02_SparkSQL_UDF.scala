package com.mycode

import org.apache.spark.SparkConf
import org.apache.spark.sql.SparkSession

/**
 * @author liuanbo
 * @creat 2021-01-23-20:56
 * @see 2194550857@qq.com
 *
 */
object Spark02_SparkSQL_UDF {
  def main(args: Array[String]): Unit = {
    val conf = new SparkConf().setAppName("sparksql").setMaster("local[*]")

    val session = SparkSession.builder().config(conf).getOrCreate()

    val df = session.read.json("data/user.json")

    df.createTempView("user")

    session.udf.register("prefixName", (name: String) => {
      "Name" + name
    })
    session.sql("select age,prefixName(username) from user").show()

    session.close()

  }
}
