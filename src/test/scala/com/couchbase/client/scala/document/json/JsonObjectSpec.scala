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
