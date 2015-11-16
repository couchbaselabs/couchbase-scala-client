package com.couchbase.client.scala

import com.couchbase.client.scala.document.JsonDocument
import com.couchbase.client.scala.document.json.JsonObject
import com.couchbase.client.scala.util.SpecProperties
import org.scalatest.{Matchers, FlatSpec}

class BucketSpec extends FlatSpec with Matchers {

  "A Document" should "be upserted and loaded correctly" in {
    val cluster = new CouchbaseCluster(List(SpecProperties.seedNode))
    val bucket = cluster.openBucket(SpecProperties.bucket, SpecProperties.password)

    val toStore = JsonDocument("my-doc", JsonObject())
    val stored = bucket.upsert(toStore)

    stored.id should equal ("my-doc")
    stored.cas.get should not equal 0
    stored.expiry should equal (None)
    stored.content.get should equal (JsonObject())

    val loaded = bucket.get("my-doc")
    stored.content should equal (loaded.get.content)
    stored.cas should equal (loaded.get.cas)
  }

}
