package com.couchbase.client.scala.transcoder

import com.couchbase.client.core.message.ResponseStatus
import com.couchbase.client.core.message.kv.MutationToken
import com.couchbase.client.deps.io.netty.buffer.ByteBuf
import com.couchbase.client.scala.document.Document
import com.couchbase.client.scala.error.TranscodingException

trait Transcoder[D <: Document[T], T] {

  def newDocument(id: String, content: T, cas: Long, expiry: Int, mutationToken: MutationToken): D
  def documentType(): Class[D]

  def doEncode(document: D) : (ByteBuf, Int)
  def doDecode(id: String, content: ByteBuf, cas: Long, expiry: Int, flags: Int, status: ResponseStatus): D

  def encode(document: D): (ByteBuf, Int) = {
    try {
      doEncode(document)
    } catch {
      case te: TranscodingException => throw te
      case e: Throwable => throw new TranscodingException("Could not encode document with ID " + document.id, e)
    }
  }

  def decode(id: String, content: ByteBuf, cas: Long, expiry: Int, flags: Int, status: ResponseStatus): D = {
    var release = shouldAutoReleaseOnDecode
    try {
      doDecode(id, content, cas, expiry, flags, status)
    } catch {
      case te: TranscodingException =>
        release = shouldAutoReleaseOnError
        throw te
      case e: Throwable =>
        release = shouldAutoReleaseOnError
        throw new TranscodingException("Could not decode document with ID " + id, e)
    } finally {
      if (content != null && release) {
        content.release()
      }
    }
  }

  def shouldAutoReleaseOnDecode: Boolean = true
  def shouldAutoReleaseOnError: Boolean = true

}
