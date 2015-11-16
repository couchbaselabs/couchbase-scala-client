package com.couchbase.client.scala.document

import com.couchbase.client.core.message.kv.MutationToken

import scala.concurrent.duration.Duration

trait Document[T] {
  def id: String
  def content: Option[T]
  def expiry: Option[Duration]
  def cas: Option[Long]
  def mutationToken: Option[MutationToken]

  def copy(cas: Long, mutationToken: MutationToken): Document[T]
}