package com.couchbase.client.scala

import com.couchbase.client.core.CouchbaseCore
import com.couchbase.client.scala.document.{JsonDocument, Document}
import com.couchbase.client.scala.transcoder.Transcoder
import com.couchbase.client.scala.util.BucketHelper
import rx.lang.scala.Observable
import rx.lang.scala.JavaConversions._

class RxCouchbaseBucket(core: CouchbaseCore, _name: String) extends RxBucket {

  val transcoders: Map[Class[_], Transcoder[_, _]] = Map(
    BucketHelper.RAW_JSON_TRANSCODER.documentType() -> BucketHelper.RAW_JSON_TRANSCODER,
    BucketHelper.JSON_TRANSCODER.documentType() -> BucketHelper.JSON_TRANSCODER
  )


  override def name(): String = _name

  override def get(id: String): Observable[JsonDocument] = {
    get(id, classOf[JsonDocument])
  }

  override def get[D <: Document[_]](id: String, target: Class[D]): Observable[D] = {
    toScalaObservable(BucketHelper
      .get[D](core, id, name, target, BucketHelper.transcoderFor(target, transcoders)))
  }

}
