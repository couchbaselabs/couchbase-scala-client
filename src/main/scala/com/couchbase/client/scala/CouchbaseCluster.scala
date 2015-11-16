package com.couchbase.client.scala

import java.util.concurrent.TimeUnit

import com.couchbase.client.core.CouchbaseCore
import com.couchbase.client.scala.util.ClusterHelper
import rx.Observable
import rx.functions.Func1

import scala.concurrent.duration.Duration


class CouchbaseCluster(nodes: List[String], core: CouchbaseCore) extends Cluster {

  val rxCluster = new RxCouchbaseCluster(nodes, core)
  ClusterHelper.initSeedNodes(core, nodes).toBlocking.single

  def this(nodes: List[String]) {
    this(nodes, new CouchbaseCore)
  }

  override def rx(): RxCluster = rxCluster

  override def openBucket(name: String, password: String, timeout: Duration): Bucket = {
    blockWithDuration(ClusterHelper.openBucket(core, name, password).map[Bucket](new Func1[Boolean, Bucket] {
      override def call(success: Boolean): Bucket = new CouchbaseBucket(core, name)
    }), timeout)
  }

  private def blockWithDuration[T](observable: Observable[T], timeout: Duration): T = {
    observable.timeout(timeout.toMillis, TimeUnit.MILLISECONDS).toBlocking.single
  }

}
