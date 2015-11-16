package com.couchbase.client.scala.transcoder

import com.couchbase.client.core.message.ResponseStatus
import com.couchbase.client.core.message.kv.MutationToken
import com.couchbase.client.deps.io.netty.buffer.{ByteBufUtil, Unpooled, ByteBuf}
import com.couchbase.client.deps.io.netty.util.CharsetUtil
import com.couchbase.client.scala.document.RawJsonDocument

import scala.concurrent.duration
import scala.concurrent.duration.Duration


class RawJsonTranscoder extends Transcoder[RawJsonDocument, String] {

  override def doEncode(document: RawJsonDocument): (ByteBuf, Int) = {
    val source = document.content.getOrElse("")
    val target = Unpooled.buffer(source.length)
    ByteBufUtil.writeUtf8(target, source)
    (target, 2 << 24)
  }

  override def doDecode(id: String, content: ByteBuf, cas: Long, expiry: Int, flags: Int, status: ResponseStatus): RawJsonDocument = {
    newDocument(id, content.toString(CharsetUtil.UTF_8), cas, expiry, null)
  }

  override def newDocument(id: String, content: String, cas: Long, expiry: Int, mutationToken: MutationToken): RawJsonDocument = {
    RawJsonDocument(id, content, Duration(expiry, duration.SECONDS), cas, mutationToken)
  }

  override def documentType(): Class[RawJsonDocument] = classOf[RawJsonDocument]
}
