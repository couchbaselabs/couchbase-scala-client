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
package com.couchbase.client.scala

import com.couchbase.client.scala.document.{Document, JsonDocument}

import scala.concurrent.duration.Duration

trait Bucket {

  def name(): String

  def rx(): RxBucket

  def get(id: String): Option[JsonDocument]

  def get(id: String, timeout: Duration): Option[JsonDocument]

  def get[D <: Document[_]](id: String, target: Class[D]): Option[D]

  def get[D <: Document[_]](id: String, target: Class[D], timeout: Duration): Option[D]

  def insert[D <: Document[_]](document: D, timeout: Duration = null): D

  def upsert[D <: Document[_]](document: D, timeout: Duration = null): D

  def replace[D <: Document[_]](document: D, timeout: Duration = null): D

}
