package com.couchbase.client.scala.transcoder

import com.couchbase.client.core.message.ResponseStatus
import com.couchbase.client.core.message.kv.MutationToken
import com.couchbase.client.deps.io.netty.buffer.{Unpooled, ByteBuf}
import com.couchbase.client.scala.document.JsonDocument
import com.couchbase.client.scala.document.json.JsonObject

import scala.concurrent.duration
import scala.concurrent.duration.Duration

class JsonTranscoder extends Transcoder[JsonDocument, JsonObject] {

  override def doEncode(document: JsonDocument): (ByteBuf, Int) = {
    val data = Unpooled.wrappedBuffer(JacksonTransformers.MAPPER.writeValueAsBytes(document.content.orNull))
    (data, 2 << 24)
  }

  override def doDecode(id: String, content: ByteBuf, cas: Long, expiry: Int, flags: Int, status: ResponseStatus): JsonDocument = {
    val length = content.readableBytes()

    var offset = 0
    val inputBytes = if (content.hasArray) {
      offset = content.arrayOffset() + content.readerIndex()
      content.array()
    } else {
      val bytes = Array.ofDim[Byte](length)
      content.getBytes(content.readerIndex(), bytes)
      bytes
    }

    val decoded = JacksonTransformers.MAPPER.readValue(inputBytes, offset, length, classOf[JsonObject])
    newDocument(id, decoded, cas, expiry, null)
  }

  override def newDocument(id: String, content: JsonObject, cas: Long, expiry: Int, mutationToken: MutationToken): JsonDocument = {
    JsonDocument(id, content, Duration(expiry, duration.SECONDS), cas, mutationToken)
  }

  override def documentType(): Class[JsonDocument] = classOf[JsonDocument]
}