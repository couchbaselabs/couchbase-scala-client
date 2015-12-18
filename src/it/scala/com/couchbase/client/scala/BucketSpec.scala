/**
  * Copyright (C) 2015 Couchbase, Inc.
  *
  * Permission is hereby granted, free of charge, to any person obtaining a copy
  * of this software and associated documentation files (the "Software"), to deal
  * in the Software without restriction, including without limitation the rights
  * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
  * copies of the Software, and to permit persons to whom the Software is
  * furnished to do so, subject to the following conditions:
  *
  * The above copyright notice and this permission notice shall be included in
  * all copies or substantial portions of the Software.
  *
  * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
  * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
  * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
  * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
  * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING
  * FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALING
  * IN THE SOFTWARE.
  */
package com.couchbase.client.scala

import com.couchbase.client.scala.document.JsonDocument
import com.couchbase.client.scala.document.json.JsonObject
import com.couchbase.client.scala.error.DocumentAlreadyExistsException
import com.couchbase.client.scala.util.SpecProperties
import org.scalatest.{Matchers, FlatSpec}

class BucketSpec extends FlatSpec with Matchers {

  val cluster = new CouchbaseCluster(List(SpecProperties.seedNode))
  val bucket = cluster.openBucket(SpecProperties.bucket, SpecProperties.password)

  "A Document" should "be upserted and loaded correctly" in {
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

  it should "be removed after being upserted" in {
    val toStore = JsonDocument("my-doc2", JsonObject())
    val stored = bucket.upsert(toStore)

    bucket.exists("my-doc2") should equal (true)

    val loaded = bucket.get("my-doc2")
    stored.content should equal (loaded.get.content)
    stored.cas should equal (loaded.get.cas)

    val removed = bucket.remove("my-doc2")
    removed.id should equal (stored.id)
    bucket.get("my-doc2") should equal (None)

    bucket.exists("my-doc2") should equal (false)
  }

  it should "fail on double insert" in {
    bucket.upsert(JsonDocument("my-doc3", JsonObject()))
    intercept[DocumentAlreadyExistsException] { bucket.insert(JsonDocument("my-doc3", JsonObject())) }
  }

  it should "get data from master and/or replica" in {
    val toStore = JsonDocument("my-doc3", JsonObject())
    val stored = bucket.upsert(toStore)

    val loaded = bucket.getFromReplica("my-doc3").toList
    loaded.length should be >= 1
  }

}
