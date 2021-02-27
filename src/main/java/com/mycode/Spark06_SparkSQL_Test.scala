package com.mycode

import org.apache.spark.SparkConf
import org.apache.spark.sql.SparkSession

/**
 * @author liuanbo
 * @creat 2021-01-28-0:25
 * @see 2194550857@qq.com
 *
 */
object Spark06_SparkSQL_Test {
  def main(args: Array[String]): Unit = {
    System.setProperty("HADOOP_USER_NAME", "root")

    val conf = new SparkConf().setMaster("local[*]").setAppName("sparksql")

    val session = SparkSession.builder().config(conf).getOrCreate()

    session.sql(" use atguigu")

    //准备数据
    session.sql(
      """
        |CREATE TABLE `user_visit_action`(
        |`date` string,
        |`user_id` bigint,
        |`session_id` string,
        |`page_id` bigint,
        |`action_time` string,
        |`search_keyword` string,
        |`click_category_id` bigint,
        |`click_product_id` bigint,
        |`order_category_ids` string,
        |`order_product_ids` string,
        |`pay_category_ids` string,
        |`pay_product_ids` string,
        |`city_id` bigint )
        | row format delimited fields terminated by '\t'
        |
        |""".stripMargin)

    session.sql(
      """
        |load data local inpath 'datas/user_visit_action.txt' into table atguigu.user_visit_action
        |""".stripMargin)


    session.sql(
      """
        |CREATE TABLE `product_info`(
        |  `product_id` bigint,
        |  `product_name` string,
        |  `extend_info` string)
        |row format delimited fields terminated by '\t'
            """.stripMargin)


    session.sql(
      """
        |load local data inpath 'datas/city_info.txt' into table atguigu.city_info
        |""".stripMargin)


    session.sql(
      """
        |select * from city_info
        |""".stripMargin).show()

  }
}
