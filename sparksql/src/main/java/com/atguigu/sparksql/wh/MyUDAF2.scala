package com.atguigu.sparksql.wh

import org.apache.spark.sql.expressions.Aggregator
import org.apache.spark.sql.{Encoder, Encoders}

case class AvgBuffer(var sss: Long, var count: Int)

class MyUDAF2 extends Aggregator[String, AvgBuffer, Double] {

  override def zero: AvgBuffer = {
    AvgBuffer(0L, 0)
  }

  override def reduce(b: AvgBuffer, a: String): AvgBuffer = {
    b.sss += a.toLong
    b.count += 1
    b
  }

  override def merge(b1: AvgBuffer, b2: AvgBuffer): AvgBuffer = {
    b1.sss += b2.sss
    b1.count += b2.count
    b1
  }

  override def finish(reduction: AvgBuffer): Double = {
    reduction.sss.toDouble / reduction.count
  }

  override def bufferEncoder: Encoder[AvgBuffer] = Encoders.product

  override def outputEncoder: Encoder[Double] = Encoders.scalaDouble
}
