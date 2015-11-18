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
import com.couchbase.client.scala.document.{JsonDocument, Document}
import com.couchbase.client.scala.transcoder.Transcoder
import com.couchbase.client.scala.util.BucketHelper
import rx.lang.scala.Observable
import rx.lang.scala.JavaConversions._

class RxCouchbaseBucket(core: CouchbaseCore, _name: String) extends RxBucket {

  private val transcoders: Map[Class[_], Transcoder[_, _]] = Map(
    BucketHelper.RAW_JSON_TRANSCODER.documentType() -> BucketHelper.RAW_JSON_TRANSCODER,
    BucketHelper.JSON_TRANSCODER.documentType() -> BucketHelper.JSON_TRANSCODER
  )

  override def name(): String = _name

  override def get(id: String): Observable[JsonDocument] = get(id, classOf[JsonDocument])

  override def get[D <: Document[_]](id: String, target: Class[D]): Observable[D] =
    toScalaObservable(
      BucketHelper.get[D](core, id, name, target, BucketHelper.transcoderFor(target, transcoders)))


  override def insert[D <: Document[_]](document: D): Observable[D] =
    toScalaObservable(BucketHelper.insert(core, document, name(), transcoders))


  override def replace[D <: Document[_]](document: D): Observable[D] =
    toScalaObservable(BucketHelper.replace(core, document, name(), transcoders))


  override def upsert[D <: Document[_]](document: D): Observable[D] =
    toScalaObservable(BucketHelper.upsert(core, document, name(), transcoders))

}
