package com.couchbase.client.scala.util

import com.couchbase.client.core.{CouchbaseException, CouchbaseCore}
import com.couchbase.client.core.message.ResponseStatus
import com.couchbase.client.core.message.kv.{GetRequest, GetResponse}
import com.couchbase.client.scala.document.Document
import com.couchbase.client.scala.error.{CouchbaseOutOfMemoryException, TemporaryFailureException}
import com.couchbase.client.scala.transcoder.{JsonTranscoder, RawJsonTranscoder, Transcoder}
import rx.Observable
import rx.functions.{Func1, Action1}

import rx.lang.scala.ImplicitFunctionConversions._

object BucketHelper {

  val RAW_JSON_TRANSCODER = new RawJsonTranscoder()
  val JSON_TRANSCODER = new JsonTranscoder()

  def get[D <: Document[_]](core: CouchbaseCore, id: String, bucket: String, target: Class[D], transcoder: Transcoder[D, _]): Observable[D] = {
    Observable
      .just((id, bucket))
      .flatMap[GetResponse](
        scalaFunction1ToRxFunc1((pair: (String, String)) => core.send[GetResponse](new GetRequest(pair._1, pair._2)))
      )
      .filter(scalaFunction1ToRxFunc1(res => {
        if (res.status().isSuccess) {
          true
        } else {
          val content = res.content()
          if (content != null && content.refCnt() > 0) {
            content.release()
          }

          res.status() match {
            case ResponseStatus.NOT_EXISTS => false
            case ResponseStatus.TEMPORARY_FAILURE | ResponseStatus.SERVER_BUSY =>
              throw new TemporaryFailureException
            case ResponseStatus.OUT_OF_MEMORY =>
              throw new CouchbaseOutOfMemoryException
            case _ =>
              throw new CouchbaseException(res.status().toString)
          }

          false
        }
      }))
      .map[D](scalaFunction1ToRxFunc1(res => {
        transcoder.decode(id, res.content(), res.cas(), 0, res.flags(), res.status())
      }))
  }

  def transcoderFor[D <: Document[_]](target: Class[D], transcoders: Map[Class[_], Transcoder[_, _]]): Transcoder[D, _] = {
    transcoders
      .getOrElse(target, throw new IllegalArgumentException(s"No Transcoder found for type: $target"))
      .asInstanceOf[Transcoder[D, _]]
  }

}
