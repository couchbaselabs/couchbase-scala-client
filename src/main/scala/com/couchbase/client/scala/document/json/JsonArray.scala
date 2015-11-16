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