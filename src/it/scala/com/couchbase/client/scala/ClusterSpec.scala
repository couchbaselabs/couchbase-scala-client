package com.couchbase.client.scala

import com.couchbase.client.scala.util.SpecProperties
import org.scalatest.{Matchers, FlatSpec}

class ClusterSpec extends FlatSpec with Matchers {

  "A Bucket" should "be opened correctly" in {
    val cluster = new CouchbaseCluster(List(SpecProperties.seedNode))
    val bucket = cluster.openBucket(SpecProperties.bucket, SpecProperties.password)

    bucket.name() should be (SpecProperties.bucket)
  }

}
