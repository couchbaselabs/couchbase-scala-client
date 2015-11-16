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
