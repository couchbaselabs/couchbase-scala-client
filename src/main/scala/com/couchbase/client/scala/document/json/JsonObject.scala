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

import scala.collection.mutable

class JsonObject extends mutable.Map[String, Any] {

  private val backingMap = mutable.Map.empty[String, Any]

  override def +=(kv: (String, Any)): JsonObject.this.type = {
    checkSupported(kv._2)
    backingMap += kv
    this
  }

  override def -=(key: String): JsonObject.this.type = {
    backingMap -= key
    this
  }

  override def get(key: String): Option[Any] = backingMap.get(key)

  override def iterator: Iterator[(String, Any)] = {
    backingMap.iterator
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

  override def empty: JsonObject = new JsonObject

  override def toString(): String =  {
    JacksonTransformers.MAPPER.writeValueAsString(backingMap)
  }

  def toMap: mutable.Map[String, Any] = {
    mutable.Map.empty[String, Any] ++= backingMap
  }

}

object JsonObject {
  def apply(): JsonObject = new JsonObject
}
