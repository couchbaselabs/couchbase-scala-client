package com.couchbase.client.scala.error

import com.couchbase.client.core.CouchbaseException

class BucketDoesNotExistException(message: String) extends CouchbaseException(message)