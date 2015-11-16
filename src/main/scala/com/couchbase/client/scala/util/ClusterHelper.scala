package com.couchbase.client.scala.util

import com.couchbase.client.core.config.ConfigurationException
import com.couchbase.client.core.message.cluster.{OpenBucketRequest, OpenBucketResponse, SeedNodesRequest, SeedNodesResponse}
import com.couchbase.client.core.{CouchbaseCore, CouchbaseException}
import com.couchbase.client.scala.error.{BucketDoesNotExistException, InvalidPasswordException}
import rx.Observable
import rx.functions.Func1
import rx.lang.scala.ImplicitFunctionConversions._

import scala.collection.JavaConversions._

object ClusterHelper {

  def initSeedNodes(core: CouchbaseCore, nodes: List[String]): Observable[Boolean] = {
    Observable
      .just(nodes)
      .flatMap(scalaFunction1ToRxFunc1(nodes =>
        core.send[SeedNodesResponse](new SeedNodesRequest(nodes))
      ))
      .map(scalaFunction1ToRxFunc1(_.status().isSuccess))
  }

  def openBucket(core: CouchbaseCore, name: String, password: String): Observable[Boolean] = {
    Observable
      .just((name, password))
      .flatMap(scalaFunction1ToRxFunc1(pair =>
        core.send[OpenBucketResponse](new OpenBucketRequest(pair._1, pair._2))
      ))
      .map[Boolean](scalaFunction1ToRxFunc1(response => {
        if (!response.status().isSuccess) {
          throw new CouchbaseException("Could not open bucket.")
        }
        true
      }))
      .onErrorResumeNext(new Func1[Throwable, Observable[Boolean]] {
        override def call(t: Throwable): Observable[Boolean] = {
          t match {
            case _: ConfigurationException =>
              t.getCause match {
                case _: IllegalStateException if t.getCause.getMessage.contains("NOT_EXISTS") =>
                  Observable.error(new BucketDoesNotExistException("Bucket \""
                    + name + "\" does not exist."))
                case _: IllegalStateException if t.getCause.getMessage.contains("Unauthorized") =>
                  Observable.error(
                    new InvalidPasswordException("Passwords for bucket \"" + name
                      + "\" do not match.")
                  )
                case _ =>
                  Observable.error(t)
              }
            case _: CouchbaseException =>
              Observable.error(t)
            case _ =>
              Observable.error(new CouchbaseException(t))
          }
        }
      })
  }

}
