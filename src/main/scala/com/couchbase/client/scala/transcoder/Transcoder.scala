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
import com.couchbase.client.deps.io.netty.buffer.ByteBuf
import com.couchbase.client.scala.document.Document
import com.couchbase.client.scala.error.TranscodingException

trait Transcoder[D <: Document[T], T] {

  def newDocument(id: String, content: T, cas: Long, expiry: Int, mutationToken: MutationToken): D

  def newDocument(id: String, cas: Long, expiry: Int, mutationToken: MutationToken): D = {
    newDocument(id, null.asInstanceOf[T], cas, expiry, mutationToken)
  }

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
