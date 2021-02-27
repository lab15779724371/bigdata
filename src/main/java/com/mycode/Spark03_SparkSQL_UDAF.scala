package com.mycode

import org.apache.spark.SparkConf
import org.apache.spark.sql.expressions.{MutableAggregationBuffer, UserDefinedAggregateFunction}
import org.apache.spark.sql.types.{DataType, LongType, StructField, StructType}
import org.apache.spark.sql.{Row, SparkSession}

/**
 * @author liuanbo
 * @creat 2021-01-23-21:04
 * @see 2194550857@qq.com
 *
 */
object Spark03_SparkSQL_UDAF {
  def main(args: Array[String]): Unit = {
    val conf = new SparkConf().setMaster("local[*]").setAppName("spark")

    val session = SparkSession.builder().config(conf).getOrCreate()

    val df = session.read.json("data/user.json")


    df.createTempView("user")

    session.udf.register("avgAvg", new MyAvgUDAF)

  }

  class MyAvgUDAF extends UserDefinedAggregateFunction {
    //输入数据的结构：Int
    override def inputSchema: StructType = {
      StructType {
        Array(
          StructField("age", LongType)
        )
      }
    }

    //缓冲区数据的结构 ：Buffer
    override def bufferSchema: StructType = {
      StructType(
        Array(
          StructField("total", LongType),
          StructField("count", LongType)
        )
      )
    }

    // 函数计算结果的数据类型：Out

    override def dataType: DataType = LongType

    // 函数的稳定性
    override def deterministic: Boolean = true

    //缓冲区初始化
    override def initialize(buffer: MutableAggregationBuffer): Unit = {
      buffer.update(0, 0L)
      buffer.update(1, 0L)
    }

    // 根据输入的值更新缓冲区数据
    override def update(buffer: MutableAggregationBuffer, input: Row): Unit = {
      buffer.update(0, buffer.getLong(0) + input.getLong(0))
      buffer.update(1, buffer.getLong(1) + 1)
    }

    // 缓冲区数据合并
    override def merge(buffer1: MutableAggregationBuffer, buffer2: Row): Unit = {
      buffer1.update(0, buffer1.getLong(0) + buffer2.getLong(0))
      buffer1.update(1, buffer1.getLong(1) + buffer2.getLong(1))
    }

    //计算平均值
    override def evaluate(buffer: Row): Any = {
      buffer.getLong(0) / buffer.getLong(1)
    }

  }

}
