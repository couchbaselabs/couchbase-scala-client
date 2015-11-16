package com.couchbase.client.scala.document

import com.couchbase.client.core.message.kv.MutationToken

import scala.concurrent.duration.Duration

case class RawJsonDocument(
    id: String,
    content: Option[String] = None,
    expiry: Option[Duration] = None,
    cas: Option[Long] = None,
    mutationToken: Option[MutationToken] = None)
  extends Document[String]

object RawJsonDocument {
  def apply(id: String): RawJsonDocument =
    apply(id, null, null, 0, null)
  def apply(id: String, content: String): RawJsonDocument =
    apply(id, content, null, 0, null)
  def apply(id: String, content: String, expiry: Duration): RawJsonDocument =
    apply(id, content, expiry, 0, null)
  def apply(id: String, content: String, cas: Long): RawJsonDocument =
    apply(id, content, null, cas, null)
  def apply(id: String, content: String, expiry: Duration, cas: Long): RawJsonDocument =
    apply(id, content, expiry, cas, null)
  def apply(id: String, content: String, expiry: Duration, cas: Long, mutationToken: MutationToken):
    RawJsonDocument = RawJsonDocument(
      id,
      Option(content),
      if (expiry == null || expiry.length == 0) { None } else { Some(expiry) },
      if (cas == 0) { None } else { Some(cas) },
      Option(mutationToken)
    )
}