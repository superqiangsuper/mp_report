package com.jfbank.data.bean

/**
  * Created by zhangzhiqiang on 2017/7/10.
  */


import scala.beans.BeanProperty

 class RequestBean {
  @BeanProperty
  var token: String =""
  @BeanProperty
  var requestId: String =""
  @BeanProperty
  var reportId: Long=0l
  @BeanProperty
  var groupId: Long=0l
  @BeanProperty
  var beginTime: String=""
  @BeanProperty
  var endTime: String=""
  @BeanProperty
  var couponId: Long=0l
  @BeanProperty
   var callbackUrl: String =""

 }
