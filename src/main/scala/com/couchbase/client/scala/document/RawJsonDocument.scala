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
package com.couchbase.client.scala.document

import com.couchbase.client.core.message.kv.MutationToken

import scala.concurrent.duration.Duration

case class RawJsonDocument(
    id: String,
    content: Option[String] = None,
    expiry: Option[Duration] = None,
    cas: Option[Long] = None,
    mutationToken: Option[MutationToken] = None)
  extends Document[String] {

  override def copy(cas: Long, mutationToken: MutationToken): RawJsonDocument = {
    new RawJsonDocument(id, content, expiry, if (cas == 0) { None } else { Some(cas) }, Option(mutationToken))
  }

}

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