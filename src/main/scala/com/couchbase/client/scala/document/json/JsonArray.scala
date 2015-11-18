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
package com.couchbase.client.scala.document.json

import com.couchbase.client.scala.transcoder.JacksonTransformers

import scala.collection.mutable.ArrayBuffer


class JsonArray extends IndexedSeq[Any] {

  private val backingArray = ArrayBuffer.empty[Any]

  override def length: Int = backingArray.length

  override def apply(idx: Int): Any = backingArray.apply(idx)

  def +=(value: Any): JsonArray.this.type = {
    checkSupported(value)
    backingArray += value
    this
  }

  def -=(value: Any): JsonArray.this.type = {
    backingArray -= value
    this
  }

  override def toString(): String = {
    JacksonTransformers.MAPPER.writeValueAsString(backingArray)
  }

  private def checkSupported(value: Any) {
    value match {
      case _: String => ()
      case _: Boolean => ()
      case _: Int => ()
      case _: Double => ()
      case _: Float => ()
      case _: Long => ()
      case _: Short => ()
      case _: JsonObject => ()
      case _: JsonArray => ()
      case _ =>
        throw new IllegalArgumentException("Unsupported type for JsonObject: " + value.getClass)
    }
  }

}

object JsonArray {

  def apply(): JsonArray = new JsonArray
}