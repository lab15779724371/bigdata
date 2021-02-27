package com.mycode

import org.apache.spark.SparkConf
import org.apache.spark.sql.expressions.Aggregator
import org.apache.spark.sql.{Encoder, Encoders, SparkSession, functions}

/**
 * @author liuanbo
 * @creat 2021-01-24-23:19
 * @see 2194550857@qq.com
 *
 */
object Spark03_SparkSQL_UDAF1 {
  def main(args: Array[String]): Unit = {
    val conf = new SparkConf().setMaster("local[*]").setAppName("rdd")
    val session = SparkSession.builder().config(conf).getOrCreate()

    val df = session.read.json("data/user.json")
    df.createOrReplaceTempView("user")

    session.udf.register("ageAvg", functions.udaf(new MyAvgUDAF()))

    session.sql("select ageAvg(age) from user").show()

    session.close()

  }

  /**
   * 自定义聚合类函数 ：计算年龄的平均值
   *      1. 继承org.apache.spark.sql.expressions.Aggregator, 定义泛型
   * IN:输入的1数据类型Long
   * Buff ：缓冲区数据类型 Buff
   * Out ：输出的数据类型 Long
   *
   *      2.重写方法 六个
   */
  case class Buff(var total: Long, var count: Long)

  class MyAvgUDAF extends Aggregator[Long, Buff, Long] {
    // z & zero : 初始值或零值
    // 缓冲区的初始化
    override def zero: Buff = {
      Buff(0L, 0L)
    }

    // 根据输入的数据更新缓冲区的数据
    override def reduce(buff: Buff, in: Long): Buff = {

      buff.total = buff.total + in
      buff.count = buff.count + 1
      buff


    }

    // 合并缓冲区
    override def merge(buff1: Buff, buff2: Buff): Buff = {
      buff1.total = buff1.total + buff2.total
      buff1.count = buff1.count + buff2.count
      buff1
    }
    //计算结果
    override def finish(buff: Buff): Long = {
      buff.total/buff.count
    }
    //缓冲区编码操作
    override def bufferEncoder: Encoder[Buff] = Encoders.product

    override def outputEncoder: Encoder[Long] = Encoders.scalaLong
  }


}
