package com.couchbase.client.scala

import com.couchbase.client.scala.document.{Document, JsonDocument}
import rx.lang.scala.Observable


trait RxBucket {

  def name(): String

  def get(id: String): Observable[JsonDocument]

  def get[D <: Document[_]](id: String, target: Class[D]): Observable[D]

}
