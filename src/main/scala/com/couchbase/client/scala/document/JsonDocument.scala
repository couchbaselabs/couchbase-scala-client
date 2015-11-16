package com.couchbase.client.scala.document

import com.couchbase.client.core.message.kv.MutationToken
import com.couchbase.client.scala.document.json.JsonObject

import scala.concurrent.duration.Duration

case class JsonDocument(
  id: String,
  content: Option[JsonObject] = None,
  expiry: Option[Duration] = None,
  cas: Option[Long] = None,
  mutationToken: Option[MutationToken] = None)
  extends Document[JsonObject] {

  override def copy(cas: Long, mutationToken: MutationToken): JsonDocument = {
    new JsonDocument(id, content, expiry, if (cas == 0) { None } else { Some(cas) }, Option(mutationToken))
  }

}

object JsonDocument {
  def apply(id: String): JsonDocument =
    apply(id, null, null, 0, null)
  def apply(id: String, content: JsonObject): JsonDocument =
    apply(id, content, null, 0, null)
  def apply(id: String, content: JsonObject, expiry: Duration): JsonDocument =
    apply(id, content, expiry, 0, null)
  def apply(id: String, content: JsonObject, cas: Long): JsonDocument =
    apply(id, content, null, cas, null)
  def apply(id: String, content: JsonObject, expiry: Duration, cas: Long): JsonDocument =
    apply(id, content, expiry, cas, null)
  def apply(id: String, content: JsonObject, expiry: Duration, cas: Long, mutationToken: MutationToken):
  JsonDocument = JsonDocument(
    id,
    Option(content),
    if (expiry == null || expiry.length == 0) { None } else { Some(expiry) },
    if (cas == 0) { None } else { Some(cas) },
    Option(mutationToken)
  )
}