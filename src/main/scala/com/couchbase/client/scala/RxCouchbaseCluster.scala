package com.couchbase.client.scala

import com.couchbase.client.core.CouchbaseCore
import com.couchbase.client.scala.util.ClusterHelper
import rx.lang.scala.Observable
import rx.lang.scala.JavaConversions._

class RxCouchbaseCluster(nodes: Seq[String], core: CouchbaseCore) extends RxCluster {

  ClusterHelper.initSeedNodes(core, nodes.toList).toBlocking.single

  def this(nodes: Seq[String]) {
    this(nodes, new CouchbaseCore)
  }

  override def openBucket(name: String, password: String): Observable[RxBucket] = {
   toScalaObservable(ClusterHelper.openBucket(core, name, password))
    .map(success => new RxCouchbaseBucket(core, name))
  }

}

object RxCouchbaseCluster {

  def apply(): RxCouchbaseCluster = new RxCouchbaseCluster(List("127.0.0.1"))
  def apply(nodes: Seq[String]): RxCouchbaseCluster = new RxCouchbaseCluster(nodes)
}