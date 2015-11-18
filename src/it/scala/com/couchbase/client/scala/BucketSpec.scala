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
