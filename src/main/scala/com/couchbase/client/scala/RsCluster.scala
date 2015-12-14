package com.couchbase.client.scala

import org.reactivestreams.Publisher

trait RsCluster {

  def openBucket(name: String, password: String): Publisher[RsBucket]

}
