package com.jfbank.data.Utils

import com.alibaba.fastjson.{JSON, JSONObject}
import org.apache.commons.httpclient.params.HttpMethodParams
import org.apache.http.client.methods.HttpPost
import org.apache.http.entity.StringEntity
import org.apache.http.impl.client.HttpClients
import org.apache.http.util.EntityUtils
import org.slf4j.{Logger, LoggerFactory}


/**
  * Created by zhangzhiqiang on 2017/7/12.
  */
object HttpUtils {

  var logger: Logger = LoggerFactory.getLogger(this.getClass)

  /**
    *
    * @param params  发送的内容
    * @param url     请求地址
    * @param charset 字符集
    * @return 返回远程相应结果
    */
  def httpPostWithJson(params: String, url: String, charset: String): Boolean = {
    var isSuccess = false

    try {
     val httpClient = HttpClients.createDefault
     val httpPost = new HttpPost(url)
     val entity=new StringEntity(params,"utf-8")
     entity.setContentType("application/json;charset=utf-8")

      httpPost.setEntity(entity)

      val response = httpClient.execute(httpPost)

      if (response != null) {
        val resEntity = response.getEntity
        if(resEntity !=null){
          val resJson= EntityUtils.toString(resEntity,charset)
          logger.info(resJson)
          val statusCode=JSON.parseObject(resJson).get("status")
          if(statusCode==StatusUtils.COMPLETE) isSuccess = true
        }

      }
    } catch {
      case ex: Exception =>
        logger.error("request mp_report error, exception:", ex)
        isSuccess = false
        return isSuccess
    }
    isSuccess
  }

}
