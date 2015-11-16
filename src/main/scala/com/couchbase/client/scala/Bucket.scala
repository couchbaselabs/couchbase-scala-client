package com.couchbase.client.scala

import com.couchbase.client.scala.document.{Document, JsonDocument}

import scala.concurrent.duration.Duration

trait Bucket {

  def name(): String

  def rx(): RxBucket

  def get(id: String): Option[JsonDocument]

  def get(id: String, timeout: Duration): Option[JsonDocument]

  def get[D <: Document[_]](id: String, target: Class[D]): Option[D]

  def get[D <: Document[_]](id: String, target: Class[D], timeout: Duration): Option[D]

  def upsert[D <: Document[_]](document: D): D

  def upsert[D <: Document[_]](document: D, timeout: Duration): D

}
