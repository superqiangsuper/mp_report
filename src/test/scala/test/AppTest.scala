package test

import com.alibaba.fastjson.{JSON, JSONObject}
import com.jfbank.data.Utils.HttpUtils
import org.junit._

@Test
class AppTest {

    @Test
    def testOK() =  {

        println(123.toString)
        val jsonRes=new JSONObject()

        jsonRes.put("token","123")
        jsonRes.put("requestId","123")
        jsonRes.put("reportId","21")
        jsonRes.put("groupId","123")
        jsonRes.put("grantUserTotal","123")
        jsonRes.put("grantCouponTotal","123")
        jsonRes.put("activityUserTotal","123")
        jsonRes.put("activityAmount",123.01)
        jsonRes.put("activityYearAmount",123.01)
        jsonRes.put("activityUserCost",123.01)
        jsonRes.put("useCouponUserTotal","123")
        jsonRes.put("useCouponAmount",123.01)
        jsonRes.put("useCouponYearAmount",123.01)
        jsonRes.put("useCouponCost",123.00)

        val postStatus=HttpUtils.httpPostWithJson(jsonRes.toString,"http://10.40.2.178:18081/report/bundle/receive","utf-8")

        println(postStatus)
        if(postStatus){
            println("post data to mp_platform success")
        }

    }





}


