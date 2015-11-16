package com.couchbase.client.scala

import com.couchbase.client.core.CouchbaseCore
import com.couchbase.client.scala.util.ClusterHelper
import rx.lang.scala.Observable
import rx.lang.scala.JavaConversions._

class RxCouchbaseCluster(nodes: List[String], core: CouchbaseCore) extends RxCluster {

  ClusterHelper.initSeedNodes(core, nodes).toBlocking.single

  def this(nodes: List[String]) {
    this(nodes, new CouchbaseCore)
  }

  override def openBucket(name: String, password: String): Observable[RxBucket] = {
   toScalaObservable(ClusterHelper.openBucket(core, name, password))
    .map(success => new RxCouchbaseBucket(core, name))
  }

}
