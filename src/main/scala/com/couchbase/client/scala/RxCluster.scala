package com.couchbase.client.scala

import rx.lang.scala.Observable

trait RxCluster {

  def openBucket(name: String, password: String): Observable[RxBucket]

}
