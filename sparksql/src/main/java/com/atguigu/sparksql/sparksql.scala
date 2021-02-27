package com.atguigu.sparksql

import org.apache.spark.SparkConf
import org.apache.spark.sql._
import org.apache.spark.sql.expressions.Aggregator

import scala.collection.mutable
import scala.collection.mutable.ListBuffer


/**
 * @author liuanbo
 * @creat 2020-11-03-14:58
 * @see 2194550857@qq.com
 *
 */
object sparksql {
  def main(args: Array[String]): Unit = {
    //    System.setProperty("HADOOP_USER_NAME", "atguigu")
    /**
     * spark的hive的数据
     */
    //    val spark: SparkSession = SparkSession.builder().enableHiveSupport().master("local[*]").
    //      appName("sql-test01").
    //      config("spark.sql.warehouse.dir", "hdfs://hadoop102:8020/user/hive/warehouse").getOrCreate()
    //    spark.sql("select * from product_info").show()
    //    spark.stop()

    System.setProperty("HADOOP_USER_NAME", "atguigu")

    val conf = new SparkConf().setMaster("local[*]").setAppName("sparksql")
    val spark = new SparkSession.Builder().enableHiveSupport().config(conf).getOrCreate()
    spark.sql("use default")

    spark.sql(
      """
        |SELECT a.*, p.product_name, c.area, c.city_name
        |FROM user_visit_action a
        |	JOIN product_info p ON a.click_product_id = p.product_id
        |	JOIN city_info c ON a.city_id = c.city_id
        |WHERE a.click_product_id > -1
        |""".stripMargin).createTempView("t1")
    //注册
    spark.udf.register("cityRename", functions.udaf(new CityRemarkUDAF))
    //根据区域和成品名称分组
    spark.sql(
      """
        |
        |select
        |area,
        |product_name,
        |count(*) as clickCnt,
        |cityRemark(city_name) as city_remark
        |from t1
        |group by
        |area, product_name
        |
        |""".stripMargin).createTempView("t2")
    //分区排序
    spark.sql(
      """
        |
        |select
        |*,
        |rank() over(partition by area order by clickCnt desc) as rk
        |from
        |t2
        |
        |""".stripMargin).createTempView("t3")

    //求前三名
    spark.sql(
      """
        |
        |select
        |*
        |from t3
        |where rk<=3
        |
        |""".stripMargin).show(false)
    //自定义样例类
    //total表示总共的点击量
    //cityMap表示城市名称和其对应的点击量
    case class Buffer(var total: Long, var cityMap: mutable.Map[String, Long])
    /**
     * 1.自定义聚合函数：实现城市备注功能
     * 1.继承Aggregator，定义泛型。
     * IN  输入城市
     * BUF：Buffer =>[总点击数量 ，Map[城市，点击数量]，[城市，点击数量]]
     * OUT：备注信息
     * 2.重写六个方法
     */
    class CityRemarkUDAF extends Aggregator[String, Buffer, String] {

      //缓冲区初始化
      override def zero: Buffer = {
        Buffer(0, mutable.Map[String, Long]())
      }

      //更新缓冲区数据
      override def reduce(buff: Buffer, city: String): Buffer = {
        buff.total += 1
        val newCount = buff.cityMap.getOrElse(city, 0L) + 1
        buff.cityMap.update(city, newCount)
        buff
      }

      //合并缓冲区数据
      override def merge(buffer1: Buffer, buffer2: Buffer): Buffer = {
        buffer1.total += buffer2.total
        val map1 = buffer1.cityMap
        val map2 = buffer2.cityMap

        map2.foreach {
          case (city, cnt) => {
            val newCount = map1.getOrElse(city, 0L) + cnt
            map1.update(city, newCount)
          }

        }
        buffer1.cityMap = map1
        buffer1
      }

      override def finish(buff: Buffer): String = {
        val remarklist = ListBuffer[String]()

        val totalcnt = buff.total
        val cityMap = buff.cityMap

        //降序排列
        val cityCntList = cityMap.toList.sortWith(

          (left, right) => {
            left._2 > right._2
          }
        ).take(2)

        val hasMore = cityMap.size > 2
        var rsum: Long = 0
        cityCntList.foreach {
          case (city, cnt) => {
            val r = cnt * 100 / totalcnt
            remarklist.append(s"${city} ${r}%")
            rsum += r
          }
        }
        if (hasMore) {
          remarklist.append(s"其他 ${100 - rsum}")
        }
        remarklist.mkString(",")

      }

      override def bufferEncoder: Encoder[Buffer] = Encoders.product

      override def outputEncoder: Encoder[String] = Encoders.STRING


    }



    spark.stop()

  }
}
