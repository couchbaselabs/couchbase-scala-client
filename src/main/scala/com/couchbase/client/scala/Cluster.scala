package com.couchbase.client.scala

import scala.concurrent.duration.Duration

trait Cluster {

  def rx(): RxCluster

  def openBucket(name: String, password: String): Bucket

  def openBucket(name: String, password: String, timeout: Duration): Bucket

}
