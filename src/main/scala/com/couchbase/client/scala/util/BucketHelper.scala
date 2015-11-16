package com.couchbase.client.scala.util

import com.couchbase.client.core.{CouchbaseException, CouchbaseCore}
import com.couchbase.client.core.message.ResponseStatus
import com.couchbase.client.core.message.kv.{UpsertResponse, UpsertRequest, GetRequest, GetResponse}
import com.couchbase.client.deps.io.netty.buffer.Unpooled
import com.couchbase.client.scala.document.Document
import com.couchbase.client.scala.error.{CouchbaseOutOfMemoryException, TemporaryFailureException}
import com.couchbase.client.scala.transcoder.{JsonTranscoder, RawJsonTranscoder, Transcoder}
import rx.Observable
import rx.functions.{Func1, Action1}

import rx.lang.scala.ImplicitFunctionConversions._

import scala.concurrent.duration.Duration
import scala.concurrent.duration._

object BucketHelper {

  val RAW_JSON_TRANSCODER = new RawJsonTranscoder()
  val JSON_TRANSCODER = new JsonTranscoder()

  private val NO_EXPIRY = Duration(0, SECONDS)

  def get[D <: Document[_]](core: CouchbaseCore, id: String, bucket: String, target: Class[D],
    transcoder: Transcoder[D, _]): Observable[D] = {
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

  def upsert[D <: Document[_]](core: CouchbaseCore, document: D, bucket: String, transcoders: Map[Class[_],
    Transcoder[_, _]]): Observable[D] = {
    val transcoder = transcoderFor(document.getClass.asInstanceOf[Class[D]], transcoders)
    val (content, flags) = transcoder.encode(document)
    val expiry = document.expiry.getOrElse(NO_EXPIRY).toSeconds.toInt

    Observable
      .just(bucket)
      .flatMap(scalaFunction1ToRxFunc1(bucket => core.send[UpsertResponse](
        new UpsertRequest(document.id, content, expiry, flags, bucket)
      )))
      .map(scalaFunction1ToRxFunc1(response => {
        if (response.content() != null && response.content().refCnt() > 0) {
          response.content().release()
        }

        if (response.status().isSuccess) {
          document.copy(response.cas(), response.mutationToken()).asInstanceOf[D]
        } else {
          response.status() match {
            //case ResponseStatus.TOO_BIG => throw new RequestTooBigException()
            //case ResponseStatus.EXISTS => throw new CASMismatchException()
            case ResponseStatus.TEMPORARY_FAILURE | ResponseStatus.SERVER_BUSY =>
              throw new TemporaryFailureException()
            case ResponseStatus.OUT_OF_MEMORY => throw new CouchbaseOutOfMemoryException()
            case _ => throw new CouchbaseException(response.status().toString)
          }
        }
      }))
  }

  def transcoderFor[D <: Document[_]](target: Class[D], transcoders: Map[Class[_],
    Transcoder[_, _]]): Transcoder[D, _] = {
    transcoders
      .getOrElse(target, throw new IllegalArgumentException(s"No Transcoder found for type: $target"))
      .asInstanceOf[Transcoder[D, _]]
  }

}
