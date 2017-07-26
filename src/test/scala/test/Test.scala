package test

import com.alibaba.fastjson.{JSON, JSONArray, JSONObject}
import com.jfbank.data.bean.RequestBean


/**
  * Created by zhangzhiqiang on 2017/7/5.
  */
object Test{

  def main(args: Array[String]): Unit = {
//
//    val json = "{\"user_details\":{\"data_column\":\"suuid\"},\"fliter\":{\"event\":\"where event='PageView' or event='ExEvent' or event='Consumption'\"}}"
//
//    //获取一级key，以及值
//    println(JSON.parseObject(JSON.parseObject(json).get("user_details").toString))
//
//    //获取二级key，以及值
//    println(JSON.parseObject(JSON.parseObject(json).get("user_details").toString).get("data_column").toString)
//
//    //获取一级key，以及值
//    println(JSON.parseObject(JSON.parseObject(json).get("fliter").toString))
//
//    //获取二级key，以及值
//    println("{\"couponIds\":\"4\",\"reportId\":8,\"requestId\":\"9e17d5fd4f574a13999e2889cf8a87df\",\"groupId\":10,\"callbackUrl\":\"http://10.40.2.178:8080/report/bundle/receive\",\"beginTime\":1230775200000,\"endTime\":1233367200000,\"token\":\"1231213\"}".parseObject(JSON.parseObject(json).get("fliter").toString).get("event").toString)
//   var requestBean= new RequestBean()
//    var list="[1,2]"
//
//
//
//   var value=JSON.parseArray(json.getString("couponsId")).getJSONObject(1)
//    println(value)



    val json="[{\"a\":\"b\"},{\"c\":\"d\"}]"
    val array = JSON.parseArray(json)
//
////   println(array.getJSONObject(0).getJSONObject("attribute").get("center"))
////
////   println(array.getJSONObject(1).getJSONArray("attribute").getJSONObject(1).get("vertex"))
//
//    println(array.get(2))

    var jo=new JSONObject()
    jo.put("key",array)
    println(jo.toString)

  }
}
