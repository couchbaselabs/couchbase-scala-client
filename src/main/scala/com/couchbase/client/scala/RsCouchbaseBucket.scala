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

import com.couchbase.client.core.CouchbaseCore
import com.couchbase.client.scala.document.{Document, JsonDocument}
import com.couchbase.client.scala.transcoder.Transcoder
import com.couchbase.client.scala.util.BucketHelper
import org.reactivestreams.Publisher
import rx.RxReactiveStreams
import RxReactiveStreams.toPublisher

class RsCouchbaseBucket(core: CouchbaseCore, _name: String) extends RsBucket {

  private val transcoders: Map[Class[_ <: Document[_]], Transcoder[_ <: Document[_], _]] = Map(
    BucketHelper.RAW_JSON_TRANSCODER.documentType() -> BucketHelper.RAW_JSON_TRANSCODER,
    BucketHelper.JSON_TRANSCODER.documentType() -> BucketHelper.JSON_TRANSCODER
  )

  override def name(): String = _name

  override def get(id: String): Publisher[JsonDocument] = get(id, classOf[JsonDocument])

  override def get[D <: Document[_]](id: String, target: Class[D]): Publisher[D] = {
    val t = transcoders.asInstanceOf[Map[Class[D], Transcoder[D, _]]]
    toPublisher(
      BucketHelper.get[D](core, id, name, target, BucketHelper.transcoderFor(target, t)))
  }

  override def exists(id: String): Publisher[Boolean] =
    toPublisher(BucketHelper.exists(core, id, name()))

  override def getFromReplica[D <: Document[_]](id: String, target: Class[D]): Publisher[D] = {
    val t = transcoders.asInstanceOf[Map[Class[D], Transcoder[D, _]]]
    toPublisher(BucketHelper
      .getFromReplica(core, id, name(), target, BucketHelper.transcoderFor(target, t)))
  }

  override def getFromReplica(id: String): Publisher[JsonDocument] = getFromReplica(id, classOf[JsonDocument])

  override def exists[D <: Document[_]](document: D): Publisher[Boolean] = exists(document.id)

  override def insert[D <: Document[_]](document: D): Publisher[D] = {
    val t = transcoders.asInstanceOf[Map[Class[D], Transcoder[D, _]]]
    toPublisher(BucketHelper.insert(core, document, name(), t))
  }


  override def replace[D <: Document[_]](document: D): Publisher[D] = {
    val t = transcoders.asInstanceOf[Map[Class[D], Transcoder[D, _]]]
    toPublisher(BucketHelper.replace(core, document, name(), t))
  }


  override def upsert[D <: Document[_]](document: D): Publisher[D] = {
    val t = transcoders.asInstanceOf[Map[Class[D], Transcoder[D, _]]]
    toPublisher(BucketHelper.upsert(core, document, name(), t))
  }

  override def remove(id: String): Publisher[JsonDocument] = {
    remove(JsonDocument(id, null, 0))
  }

  override def remove[D <: Document[_]](id: String, target: Class[D]): Publisher[D] = {
    val t = transcoders.asInstanceOf[Map[Class[D], Transcoder[D, _]]]
    val doc = BucketHelper.transcoderFor(target, t).newDocument(id, 0, 0, null)
    remove(doc)
  }

  override def remove[D <: Document[_]](document: D): Publisher[D] = {
    val t = transcoders.asInstanceOf[Map[Class[D], Transcoder[D, _]]]
    toPublisher(BucketHelper.remove(core, document, name(), t))
  }
}
