package com.mycode

import org.apache.spark.SparkConf
import org.apache.spark.sql.SparkSession

/**
 * @author liuanbo
 * @creat 2021-01-17-22:49
 * @see 2194550857@qq.com
 *
 */
object Spark01_SparkSQL_Basic {
  def main(args: Array[String]): Unit = {
    /**
     * 在session的底层就是connext，所以如果创建了session对象，就不需要创建sc对象
     * 另外在使用ds和df时需要导入一个隐式转换
     */

    //todo 创建sparksql的运行环境
    val conf = new SparkConf().setMaster("local[*]").setAppName("spark")

    val session = SparkSession.builder().config(conf).getOrCreate()


    //todo 执行逻辑操作
    //todo dataframe
    val frame = session.read.json("data/user.json")
    //dataframe =>sql
    frame.createTempView("user")

    session.sql("select age from user").show()
    session.sql("select avg(age) from user").show()

    //创建dataframe=>DSL
    /**
     * 在使用dataframe时需要涉及转换操作，需要导入转换规则
     */
    import session.implicits._
    frame.select("age", "username").show()
    frame.select($"age" + 1)
    frame.select('age + 1)

    //todo dataset
    /**
     * dataframe 其实是特定泛型的dataset
     */

    val seq = Seq(1, 2, 4)

    val ds1 = seq.toDS()

    ds1.show()

    //RDD <=>dataFrame
    val rdd = session.sparkContext.makeRDD(List((1, "zhangsan", 30), (2, "lisi", 35)))

    val df = rdd.toDF("id", "name", "age")

    val rdd1 = df.rdd

    // DataFrame <=> DataSet
    val ds = df.as[User]
    val df1 = ds.toDF()

    // RDD <=> DataSet
    val ds2 = rdd.map {
      case (id, name, age) => {
        User(id, name, age)
      }
    }.toDS()


    val rdd2 = ds2.rdd

    session.close()

  }

  case class User(id: Int, name: String, age: Int)

}
