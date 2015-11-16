package com.couchbase.client.scala.util

object SpecProperties {

  val seedNode = System.getProperty("seedNode", "127.0.0.1")
  val bucket = System.getProperty("bucket", "default")
  val password = System.getProperty("password", "")
  val adminName = System.getProperty("adminName", "Administrator")
  val adminPassword = System.getProperty("adminPassword", "password")

}
