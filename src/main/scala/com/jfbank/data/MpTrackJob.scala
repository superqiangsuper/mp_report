package com.jfbank.data


import java.text.SimpleDateFormat
import java.util.Date

import com.alibaba.fastjson.{JSON, JSONObject}
import com.jfbank.data.Utils.HttpUtils
import com.jfbank.data.bean.RequestBean
import com.jfbank.data.dataToHive.UserGroupSqoop
import com.typesafe.config.{Config, ConfigFactory}
import org.apache.spark.sql.hive.HiveContext
import org.apache.spark.{SparkConf, SparkContext}
import org.slf4j.LoggerFactory
import spark.jobserver.{SparkJob, SparkJobValid, SparkJobValidation}

import scala.util.Random

/**
 * Created by zhangzhiqiang on 2017/7/6.
 */
object MpTrackJob extends SparkJob {
  val logger = LoggerFactory.getLogger(this.getClass)
  val json = new JSONObject()

  override def validate(sc: SparkContext, config: Config): SparkJobValidation = {

    //    Try(JSON.parseObject(config.getString("input.string")))
    //      .map(x=>SparkJobValid)
    //      .getOrElse(SparkJobValid)
    SparkJobValid

  }

  override def runJob(sc: SparkContext, config: Config): Any = {

    logger.info("job开始，解析json请求,requestId:" + config.getString("requestId") + ",reportId:" + config.getLong("reportId")
      + ",groupId:" + config.getLong("groupId").toString)

    val jsonConf = new JSONObject()
    val sdf: SimpleDateFormat = new SimpleDateFormat("yyyy-MM-dd")


    jsonConf.put("token", config.getString("token"))
    jsonConf.put("requestId", config.getString("requestId"))
    jsonConf.put("reportId", config.getLong("reportId"))
    jsonConf.put("groupId", config.getLong("groupId"))
    jsonConf.put("beginTime", sdf.format(new Date(config.getLong("beginTime"))))
    jsonConf.put("endTime", sdf.format(new Date(config.getLong("endTime"))))

    if (config.hasPath("couponId")) {
      jsonConf.put("couponId", config.getLong("couponId"))
    }
    jsonConf.put("callbackUrl", config.getString("callbackUrl"))


    //***********************************************************
    //1.从mysql抓数据
    val rb = getRequestBean(jsonConf)

    if (rb.getGroupId.equals(0l)) {
      //解析失败
      logger.info("请求解析失败。不存在用户组ID")
    } else {
      //解析成功

      val random=new Random()

      //测试专用
      json.put("token", rb.getToken.toString)
      json.put("requestId", rb.getRequestId.toString)
      json.put("reportId", rb.getReportId.toString)
      json.put("groupId", rb.getGroupId.toString)
      json.put("grantUserTotal", random.nextInt(3000).toString )
      json.put("grantCouponTotal", random.nextInt(3000).toString )
      json.put("activityUserTotal", random.nextInt(3000).toString )
      json.put("activityAmount", random.nextInt(3000).toDouble)
      json.put("activityYearAmount", random.nextInt(3000).toDouble)
      json.put("activityUserCost", random.nextInt(3000).toDouble)
      json.put("useCouponUserTotal", random.nextInt(3000).toString)
      json.put("useCouponAmount", random.nextInt(3000).toDouble)
      json.put("useCouponYearAmount", random.nextInt(3000).toDouble)
      json.put("useCouponCost", random.nextInt(3000).toDouble)
      logger.info(s"dataSummary success with result: $json")




     HttpUtils.httpPostWithJson(json.toString, rb.getCallbackUrl, "utf-8")



//
//      logger.info("请求解析成功")
//      val pullRes = pullGroupData(rb, new UserGroupSqoop)
//
//
//      if (pullRes) {
//        //抓取成功
//        logger.info("抓取数据成功 用户组ID :" + rb.getGroupId)
//        //2.开始运算
//        val jsonRes = calculate(sc, rb)
//
//        if (!jsonRes.isEmpty) {
//
//          logger.info("数据汇总成功")
//          //3.发送数据响应请求
//          val postStatus = HttpUtils.httpPostWithJson(jsonRes.toString, rb.getCallbackUrl, "utf-8")
//
//
//          if (postStatus) {
//            logger.info("数据发送给MP平台成功")
//          }
//
//        }
//
//      } else {
//        //抓取失败
//        logger.info("数据从mysql抓取失败 用户组ID :" + rb.getGroupId)
//      }


    }


  }

  val shell_path = "/home/zhangzhiqiang/job/mp_report/sqoop_table_from_mp.sh"

  /**
   * 拉取 对应GroupId的数据
   * @param requestBean
   * @param userGroupSqoop
   * @return
   */
  def pullGroupData(requestBean: RequestBean, userGroupSqoop: UserGroupSqoop): Boolean = {
    val gId = requestBean.getGroupId
    //    userGroupSqoop.setSqoopCode(s"sh -t -p 22 appuser@wallet-prod-hadoop-04 $shell_path $gId")
    userGroupSqoop.setSqoopCode(s"sh $shell_path $gId")
    userGroupSqoop.execSqoopCode()
  }

  /**
   * 计算结果
   * @param sc
   * @param rb
   * @return
   */
  def calculate(sc: SparkContext, rb: RequestBean): JSONObject = {

    val sqlContext = new HiveContext(sc)
    val gid = rb.getGroupId
    val cid = rb.getCouponId
    val beginTime = rb.getBeginTime
    val endTime = rb.getEndTime

    val sqlStr =
      s"""
         |SELECT coalesce(count(a.user_id),0) grantUserTotal,
         |       coalesce(sum(case when $cid=0 then 0 else 1 end),0) grantCouponTotal,
         |       coalesce(count(b.user_id),0) activityUserTotal,
         |       coalesce(sum(b.investor_amount),0) activityAmount,
         |       coalesce(sum(b.tx_annual_amount),0) activityYearAmount,
         |       coalesce(sum(b.activity_cost),0) activityUserCost,
         |       coalesce(count(DISTINCT CASE
         |                                   WHEN b.coupons_id=$cid THEN b.user_id
         |                               END),0) useCouponUserTotal,
         |       sum(CASE
         |               WHEN b.coupons_id=$cid THEN b.investor_amount
         |               ELSE 0
         |           END) useCouponAmount,
         |       sum(CASE
         |               WHEN b.coupons_id=$cid THEN tx_annual_amount
         |               ELSE 0
         |           END) useCouponYearAmount,
         |       sum(CASE
         |               WHEN b.coupons_id=$cid THEN b.activity_cost
         |               ELSE 0
         |           END) useCouponCost
         |FROM
         |  (SELECT get_json_object(params, '$$.memberId') user_id
         |   FROM mp.mp_group_item
         |   WHERE mp_gid=$gid ) a
         |LEFT JOIN
         |  (SELECT invest_time,
         |          user_id,
         |          investor_amount,
         |          coupons_id,
         |          tx_annual_amount,
         |          coalesce(tq_profit,0)+coalesce(jx_profit,0)+coalesce(mj_profit,0)+coalesce(high_rate_amount,0) activity_cost
         |   FROM dwi.dw_wk_invest
         |   WHERE is_normal_order=1
         |     AND invest_time BETWEEN $beginTime AND $endTime ) b ON a.user_id=b.user_id

     """.stripMargin

    logger.info(sqlStr)
    val result = sqlContext.sql(sqlStr).rdd.collect()

    logger.info("计算结果"+result(0).toString())
    if (result.size < 1) {
      logger.info("数据汇总失败")
    } else {


              json.put("token", rb.getToken.toString)
              json.put("requestId", rb.getRequestId.toString)
              json.put("reportId", rb.getReportId.toString)
              json.put("groupId", rb.getGroupId.toString)
              json.put("grantUserTotal", result(0).getAs[String](0))
              json.put("grantCouponTotal", result(0).getAs[String](1))
              json.put("activityUserTotal", result(0).getAs[String](2))
              json.put("activityAmount", result(0).getAs[Double](3))
              json.put("activityYearAmount", result(0).getAs[Double](4))
              json.put("activityUserCost", result(0).getAs[Double](5))
              json.put("useCouponUserTotal", result(0).getAs[String](6))
              json.put("useCouponAmount", result(0).getAs[Double](7))
              json.put("useCouponYearAmount", result(0).getAs[Double](8))
              json.put("useCouponCost", result(0).getAs[Double](9))
      logger.info(s"dataSummary success with result: $json")
    }
    json

  }

  def getRequestBean(json: JSONObject): RequestBean = {
    val rb = new RequestBean
    rb.setToken(json.getString("token"))
    rb.setRequestId(json.getString("requestId"))
    rb.setReportId(json.getLong("reportId"))
    rb.setGroupId(json.getLong("groupId"))
    rb.setBeginTime(json.getString("beginTime"))
    rb.setEndTime(json.getString("endTime"))
    rb.setCallbackUrl(json.getString("callbackUrl"))

    //判断groupid
    if (json.containsKey("couponId")) {
      rb.setCouponId(json.getLong("couponId"))
    }
    rb
  }




}
