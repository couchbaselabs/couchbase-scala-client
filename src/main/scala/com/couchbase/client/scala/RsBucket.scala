package com.couchbase.client.scala

import com.couchbase.client.scala.document.{Document, JsonDocument}
import org.reactivestreams.Publisher

trait RsBucket {

  def name(): String

  def get(id: String): Publisher[JsonDocument]

  def get[D <: Document[_]](id: String, target: Class[D]): Publisher[D]

  def insert[D <: Document[_]](document: D): Publisher[D]

  def upsert[D <: Document[_]](document: D): Publisher[D]

  def replace[D <: Document[_]](document: D): Publisher[D]

  def remove(id: String): Publisher[JsonDocument]

  def remove[D <: Document[_]](id: String, target: Class[D]): Publisher[D]

  def remove[D <: Document[_]](document: D): Publisher[D]

}
