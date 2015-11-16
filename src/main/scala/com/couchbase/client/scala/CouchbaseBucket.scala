package com.couchbase.client.scala

import java.util.concurrent.TimeUnit

import com.couchbase.client.core.CouchbaseCore
import com.couchbase.client.scala.document.{Document, JsonDocument}
import com.couchbase.client.scala.transcoder.{JsonTranscoder, Transcoder, RawJsonTranscoder}
import com.couchbase.client.scala.util.BucketHelper

import scala.concurrent.duration.Duration

class CouchbaseBucket(core: CouchbaseCore, name: String) extends Bucket {

  val transcoders: Map[Class[_], Transcoder[_, _]] = Map(
    BucketHelper.RAW_JSON_TRANSCODER.documentType() -> BucketHelper.RAW_JSON_TRANSCODER,
    BucketHelper.JSON_TRANSCODER.documentType() -> BucketHelper.JSON_TRANSCODER
  )

  override def rx(): RxBucket = new RxCouchbaseBucket(core, name)

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
      .get[D](core, id, name, target,  BucketHelper.transcoderFor(target, transcoders))
      .timeout(timeout.toMillis, TimeUnit.MILLISECONDS)
      .toBlocking
      .singleOrDefault(null.asInstanceOf[D]))
  }

}
