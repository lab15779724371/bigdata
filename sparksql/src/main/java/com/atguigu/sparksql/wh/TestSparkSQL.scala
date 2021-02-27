package com.atguigu.sparksql.wh

import org.apache.spark.rdd.RDD
import org.apache.spark.sql.{DataFrame, Dataset, Row, SparkSession}
import org.apache.spark.{SparkConf, SparkContext}

object TestSparkSQL {
  def main(args: Array[String]): Unit = {
    val conf: SparkConf = new SparkConf().setMaster("local[*]").setAppName("SparkSQL")
    val sc: SparkContext = new SparkContext(conf)
    val spark: SparkSession = SparkSession.builder().config(conf).getOrCreate()
    import spark.implicits._

    val function: MyUDAF = new MyUDAF

    spark.udf.register("myavg", function)

    val actionRDD: RDD[UserVisitAction] = sc.textFile("user_visit_action.txt")
      .map(_.split("_"))
      .map(data => UserVisitAction(
        data(0),
        data(1),
        data(2),
        data(3),
        data(4),
        data(5),
        data(6),
        data(7),
        data(8),
        data(9),
        data(10),
        data(11),
        data(12)
      ))

    val ds: Dataset[UserVisitAction] = actionRDD.toDS

    ds.createTempView("uva")

//    ds.filter(x => x.city_id.toInt > 10).show

    val df: DataFrame = actionRDD.toDF()

    val rdd: RDD[Row] = df.rdd

    val rdd1: RDD[UserVisitAction] = ds.rdd

    val df2: DataFrame = ds.toDF()

    val ds2: Dataset[UserVisitAction] = df.as[UserVisitAction]

    spark.sql("select myavg(page_id) from uva").show

  }

}
