package com.couchbase.client.scala.error

import com.couchbase.client.core.CouchbaseException

class InvalidPasswordException(message: String) extends CouchbaseException(message)
