package com.couchbase.client.scala

import com.couchbase.client.scala.document.{Document, RawJsonDocument}
import org.scalatest.{Matchers, FlatSpec}

import scala.concurrent.duration.Duration

class ClusterSpec extends FlatSpec with Matchers {

  "A Bucket" should "be opened correctly" in {
    val cluster = new CouchbaseCluster(List("127.0.0.1"))
    val bucket = cluster.openBucket("travel-sample", "", Duration("2500 ms"))

    val doc = bucket.get("airline_10")
    println(doc.get.content.get.get("name").get)
  }

}
