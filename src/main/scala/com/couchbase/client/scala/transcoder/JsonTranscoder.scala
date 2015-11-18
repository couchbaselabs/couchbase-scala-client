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