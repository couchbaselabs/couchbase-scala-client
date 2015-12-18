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
import scala.collection.JavaConversions._

import scala.concurrent.duration.Duration

class CouchbaseBucket(core: CouchbaseCore, _name: String) extends Bucket {

  private val transcoders: Map[Class[_ <: Document[_]], Transcoder[_ <: Document[_], _]] = Map(
    BucketHelper.RAW_JSON_TRANSCODER.documentType() -> BucketHelper.RAW_JSON_TRANSCODER,
    BucketHelper.JSON_TRANSCODER.documentType() -> BucketHelper.JSON_TRANSCODER
  )

  private val FIXME_DURATION = Duration("2500 ms")

  override def rx(): RxBucket = new RxCouchbaseBucket(core, _name)


  override def name(): String = _name

  override def get(id: String): Option[JsonDocument] = {
    get(id, FIXME_DURATION)
  }

  override def get(id: String, timeout: Duration): Option[JsonDocument] = {
    get(id, classOf[JsonDocument], timeout)
  }

  override def get[D <: Document[_]](id: String, target: Class[D]): Option[D] = {
    get(id, target, FIXME_DURATION)
  }

  override def get[D <: Document[_]](id: String, target: Class[D], timeout: Duration): Option[D] = {
        Option(BucketHelper
          .get[D](core, id, name(), target, BucketHelper.transcoderFor(target,
            transcoders.asInstanceOf[Map[Class[D], Transcoder[D, _]]]))
          .timeout(Option(timeout).getOrElse(FIXME_DURATION).toMillis, TimeUnit.MILLISECONDS)
          .toBlocking
          .singleOrDefault(null.asInstanceOf[D]))
  }


  override def upsert[D <: Document[_]](document: D, timeout: Duration = null): D = {
    val t = transcoders.asInstanceOf[Map[Class[D], Transcoder[D, _]]]
    BucketHelper
      .upsert(core, document, name(), t)
      .timeout(Option(timeout).getOrElse(FIXME_DURATION).toMillis, TimeUnit.MILLISECONDS)
      .toBlocking
      .single()
  }

  override def insert[D <: Document[_]](document: D, timeout: Duration = null): D = {
    val t = transcoders.asInstanceOf[Map[Class[D], Transcoder[D, _]]]
    BucketHelper
      .insert(core, document, name(), t)
      .timeout(Option(timeout).getOrElse(FIXME_DURATION).toMillis, TimeUnit.MILLISECONDS)
      .toBlocking
      .single()
  }

  override def replace[D <: Document[_]](document: D, timeout: Duration = null): D = {
    val t = transcoders.asInstanceOf[Map[Class[D], Transcoder[D, _]]]
    BucketHelper
      .replace(core, document, name(), t)
      .timeout(Option(timeout).getOrElse(FIXME_DURATION).toMillis, TimeUnit.MILLISECONDS)
      .toBlocking
      .single()
  }

  override def remove(id: String): JsonDocument = remove(id, FIXME_DURATION)

  override def remove(id: String, timeout: Duration): JsonDocument = {
    remove(JsonDocument(id, null, 0))
  }

  override def remove[D <: Document[_]](id: String, target: Class[D]): D = remove(id, target, null)

  override def remove[D <: Document[_]](id: String, target: Class[D], timeout: Duration): D = {
    val t = transcoders.asInstanceOf[Map[Class[D], Transcoder[D, _]]]
    val doc = BucketHelper.transcoderFor(target, t).newDocument(id, 0, 0, null)
    remove(doc, timeout)
  }

  override def remove[D <: Document[_]](document: D): D = remove(document, null)

  override def remove[D <: Document[_]](document: D, timeout: Duration): D = {
    val t = transcoders.asInstanceOf[Map[Class[D], Transcoder[D, _]]]
    BucketHelper
      .remove(core, document, name(), t)
      .timeout(Option(timeout).getOrElse(FIXME_DURATION).toMillis, TimeUnit.MILLISECONDS)
      .toBlocking
      .single()
  }

  override def exists(id: String): Boolean = exists(id, null)

  override def exists[D <: Document[_]](document: D): Boolean = exists(document, null)

  override def exists[D <: Document[_]](document: D, timeout: Duration): Boolean = exists(document.id, timeout)

  override def getFromReplica(id: String): Iterable[JsonDocument] = getFromReplica(id, FIXME_DURATION)

  override def getFromReplica(id: String, timeout: Duration): Iterable[JsonDocument] = getFromReplica(id, classOf[JsonDocument], timeout)

  override def getFromReplica[D <: Document[_]](id: String, target: Class[D]): Iterable[D] = getFromReplica(id, target, null)

  override def exists(id: String, timeout: Duration): Boolean = {
    BucketHelper.exists(core, id, name())
      .timeout(Option(timeout).getOrElse(FIXME_DURATION).toMillis, TimeUnit.MILLISECONDS)
      .toBlocking
      .single()
  }

  override def getFromReplica[D <: Document[_]](id: String, target: Class[D], timeout: Duration): Iterable[D] = {
    BucketHelper
      .getFromReplica(core, id, name(), target, BucketHelper.transcoderFor(target,
        transcoders.asInstanceOf[Map[Class[D], Transcoder[D, _]]]))
      .timeout(Option(timeout).getOrElse(FIXME_DURATION).toMillis, TimeUnit.MILLISECONDS)
      .toBlocking
      .toIterable
  }

}
