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

import java.util.concurrent.TimeUnit

import com.couchbase.client.core.CouchbaseCore
import com.couchbase.client.scala.document.{Document, JsonDocument}
import com.couchbase.client.scala.transcoder.Transcoder
import com.couchbase.client.scala.util.BucketHelper

import scala.concurrent.duration.Duration

class CouchbaseBucket(core: CouchbaseCore, _name: String) extends Bucket {

  private val transcoders: Map[Class[_], Transcoder[_, _]] = Map(
    BucketHelper.RAW_JSON_TRANSCODER.documentType() -> BucketHelper.RAW_JSON_TRANSCODER,
    BucketHelper.JSON_TRANSCODER.documentType() -> BucketHelper.JSON_TRANSCODER
  )

  override def rx(): RxBucket = new RxCouchbaseBucket(core, _name)


  override def name(): String = _name

  override def get(id: String): Option[JsonDocument] = {
    get(id, Duration("2500 ms"))
  }

  override def get(id: String, timeout: Duration): Option[JsonDocument] = {
    get(id, classOf[JsonDocument], timeout)
  }

  override def get[D <: Document[_]](id: String, target: Class[D]): Option[D] = {
    get(id, target, Duration("2500 ms"))
  }

  override def get[D <: Document[_]](id: String, target: Class[D], timeout: Duration): Option[D] = {
    Option(BucketHelper
      .get[D](core, id, name(), target,  BucketHelper.transcoderFor(target, transcoders))
      .timeout(timeout.toMillis, TimeUnit.MILLISECONDS)
      .toBlocking
      .singleOrDefault(null.asInstanceOf[D]))
  }

  override def upsert[D <: Document[_]](document: D, timeout: Duration = null): D = {
    BucketHelper
      .upsert(core, document, name(), transcoders)
      .timeout(Option(timeout).getOrElse(Duration("2500 ms")).toMillis, TimeUnit.MILLISECONDS)
      .toBlocking
      .single()
  }

  override def insert[D <: Document[_]](document: D, timeout: Duration = null): D = {
    BucketHelper
      .insert(core, document, name(), transcoders)
      .timeout(Option(timeout).getOrElse(Duration("2500 ms")).toMillis, TimeUnit.MILLISECONDS)
      .toBlocking
      .single()
  }

  override def replace[D <: Document[_]](document: D, timeout: Duration = null): D = {
    BucketHelper
      .replace(core, document, name(), transcoders)
      .timeout(Option(timeout).getOrElse(Duration("2500 ms")).toMillis, TimeUnit.MILLISECONDS)
      .toBlocking
      .single()
  }
}
