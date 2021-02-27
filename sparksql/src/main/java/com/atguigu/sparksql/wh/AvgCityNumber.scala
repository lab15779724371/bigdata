package com.atguigu.sparksql.wh

import org.apache.spark.rdd.RDD
import org.apache.spark.sql.{Dataset, SparkSession, functions}
import org.apache.spark.util.AccumulatorV2
import org.apache.spark.{SparkConf, SparkContext}

object AvgCityNumber {
  def main(args: Array[String]): Unit = {
    val conf: SparkConf = new SparkConf().setMaster("local[*]").setAppName("SparkSQL")
    val spark: SparkSession = SparkSession.builder().config(conf).getOrCreate()
    import spark.implicits._
    val sc: SparkContext = spark.sparkContext

    val func: MyUDAF2 = new MyUDAF2
    spark.udf.register("myavg", functions.udaf(func))

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


    //1. RDD
    val tuple: (String, Int) = actionRDD.map(x => (x.page_id, 1)).reduce {
      case ((pageId1, count1), (pageId2, count2)) => ((pageId1.toInt + pageId2.toInt).toString, count1 + count2)
    }

    val accu: MyAccu = new MyAccu
    sc.register(accu)
    actionRDD.foreach(accu.add)

    //    println(tuple._1.toDouble / tuple._2)
    //    println(accu.value)


//    ds.select(func.toColumn).show

    spark.sql("select myavg(page_id) from uva").show
  }
}


class MyAccu extends AccumulatorV2[UserVisitAction, Double] {

  var sumPageId: Long = 0L
  var count: Int = 0

  override def isZero: Boolean = {
    sumPageId == 0L && count == 0
  }

  override def copy(): AccumulatorV2[UserVisitAction, Double] = {
    val accu: MyAccu = new MyAccu
    accu.sumPageId = this.sumPageId
    accu.count = this.count
    accu
  }

  override def reset(): Unit = {
    sumPageId = 0L;
    count = 0
  }

  override def add(v: UserVisitAction): Unit = {
    sumPageId += v.page_id.toLong
    count += 1
  }

  override def merge(other: AccumulatorV2[UserVisitAction, Double]): Unit = {
    val accu: MyAccu = other.asInstanceOf[MyAccu]
    this.sumPageId += accu.sumPageId
    this.count += accu.count

  }

  override def value: Double = sumPageId.toDouble / count
}
