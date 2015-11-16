package com.couchbase.client.scala.document.json

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.scala.DefaultScalaModule
import com.fasterxml.jackson.module.scala.experimental.ScalaObjectMapper
import org.scalatest.{Matchers, FlatSpec}

class JsonObjectSpec extends FlatSpec with Matchers {

  val mapper = new ObjectMapper() with ScalaObjectMapper
  mapper.registerModule(DefaultScalaModule)

  "A JsonObject" should "be empty once created" in {
    val obj = JsonObject()

    assert(obj.isEmpty)
    assert(obj.get("key").isEmpty)
    assert(obj.toString == "{}")
  }

  it should "accept and encode scalar values" in  {
    val obj = JsonObject()

    obj += (
      ("str", "bar"),
      ("bool", true),
      ("int", 1),
      ("long", 42534636L),
      ("double", 553435.23432423)
    )

    val decoded = mapper.readValue[Map[String, Any]](obj.toString())
    assert(obj.toMap == decoded)
  }

  it should "accept and encode nested json objects" in {
    val obj = JsonObject()

    obj += (
      ("str", "foo"),
      ("nested", JsonObject() += (("bla", true)))
    )

    val decoded = mapper.readValue[Map[String, Any]](obj.toString())
    assert(obj.toMap == decoded)
  }

  it should "accept and encode nested json arrays" in {
    val obj = JsonObject()

    obj += (
      ("str", "foo"),
      ("nested", JsonArray()
        += (JsonObject() += (("foo", true), ("bar", 1234)))
      )
    )

    val decoded = mapper.readValue[Map[String, Any]](obj.toString())
    assert(obj.toMap == decoded)
  }

}
