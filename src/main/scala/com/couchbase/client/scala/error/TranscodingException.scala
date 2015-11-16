package com.couchbase.client.scala.error

import com.couchbase.client.core.CouchbaseException

/**
  * .
  *
  * @author Michael Nitschinger
  * @since
  */
class TranscodingException(message: String, cause: Throwable) extends CouchbaseException(message, cause) {

}
