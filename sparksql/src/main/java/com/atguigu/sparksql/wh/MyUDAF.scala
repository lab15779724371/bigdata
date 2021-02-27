package com.atguigu.sparksql.wh

import org.apache.spark.sql.Row
import org.apache.spark.sql.expressions.{MutableAggregationBuffer, UserDefinedAggregateFunction}
import org.apache.spark.sql.types._

class MyUDAF extends UserDefinedAggregateFunction {

  override def inputSchema: StructType =
    new StructType().add("page_id", StringType)

  override def bufferSchema: StructType =
    new StructType().add("sum", LongType).add("count", IntegerType)

  override def dataType: DataType = DoubleType

  override def deterministic: Boolean = true

  override def initialize(buffer: MutableAggregationBuffer): Unit = {
    buffer(0) = 0L
    buffer(1) = 0
  }

  override def update(buffer: MutableAggregationBuffer, input: Row): Unit = {
    val pageId: Long = input.getString(0).toLong
    buffer(0) = buffer.getLong(0) + pageId
    buffer(1) = buffer.getInt(1) + 1

  }

  override def merge(buffer1: MutableAggregationBuffer, buffer2: Row): Unit = {
    buffer1(0) = buffer1.getLong(0) + buffer2.getLong(0)
    buffer1(1) = buffer1.getInt(1) + buffer2.getInt(1)
  }

  override def evaluate(buffer: Row): Any = buffer.getLong(0).toDouble / buffer.getInt(1)
}
