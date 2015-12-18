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
package com.couchbase.client.scala.bucket

import com.couchbase.client.core.config.CouchbaseBucketConfig
import com.couchbase.client.core.message.cluster.{GetClusterConfigResponse, GetClusterConfigRequest}
import com.couchbase.client.core.{CouchbaseException, ClusterFacade}
import com.couchbase.client.core.message.ResponseStatus
import com.couchbase.client.core.message.kv.{ReplicaGetRequest, GetRequest, BinaryRequest, GetResponse}
import com.couchbase.client.scala.error.{CouchbaseOutOfMemoryException, TemporaryFailureException}
import rx.Observable
import rx.functions.Func1

import rx.lang.scala.ImplicitFunctionConversions._
import scala.collection.JavaConversions._

object ReplicaReader {

  def read(core: ClusterFacade, id: String, bucket: String): Observable[GetResponse] = {
    assembleRequests(core, id, bucket)
      .flatMap(scalaFunction1ToRxFunc1[BinaryRequest, Observable[GetResponse]](req =>
        core.send[GetResponse](req)
          .filter(scalaFunction1ToRxFunc1(response => {
            if (response.status().isSuccess) {
              true
            } else {
              val content = response.content()
              if (content != null && content.refCnt() > 0) {
                content.release()
              }

              response.status() match {
                case ResponseStatus.NOT_EXISTS => false
                case ResponseStatus.TEMPORARY_FAILURE | ResponseStatus.SERVER_BUSY =>
                  throw new TemporaryFailureException()
                case ResponseStatus.OUT_OF_MEMORY =>
                  throw new CouchbaseOutOfMemoryException()
                case _ => throw new CouchbaseException(response.status().toString)
              }
            }
          }))
          .onErrorResumeNext(GetResponseErrorHandler.INSTANCE)
      ))
  }

  private def assembleRequests(core: ClusterFacade, id: String, bucket: String): Observable[BinaryRequest] = {
    Observable
      .defer[GetClusterConfigResponse](scalaFunction0ToRxFunc0[Observable[GetClusterConfigResponse]](() =>
        core.send[GetClusterConfigResponse](new GetClusterConfigRequest())
      ))
      .map[Int](scalaFunction1ToRxFunc1[GetClusterConfigResponse, Int](
        _.config().bucketConfig(bucket).asInstanceOf[CouchbaseBucketConfig].numberOfReplicas()
      ))
      .flatMap(scalaFunction1ToRxFunc1[Int, Observable[BinaryRequest]](max => {
        Observable.from(new GetRequest(id, bucket) ::
          (0 until max)
            .map(i => new ReplicaGetRequest(id, bucket, (i + 1).toShort))
            .toList
        )
      }))
  }

}

class GetResponseErrorHandler extends Func1[Throwable, Observable[_ <: GetResponse]] {
  override def call(t: Throwable): Observable[_ <: GetResponse] = Observable.empty()
}

object GetResponseErrorHandler {
  val INSTANCE = new GetResponseErrorHandler()
}