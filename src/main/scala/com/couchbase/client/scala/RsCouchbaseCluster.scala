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
package com.couchbase.client.scala

import com.couchbase.client.core.CouchbaseCore
import com.couchbase.client.scala.util.ClusterHelper
import org.reactivestreams.Publisher
import rx.RxReactiveStreams
import rx.functions.Func1
import RxReactiveStreams.toPublisher

class RsCouchbaseCluster(nodes: Seq[String], core: CouchbaseCore) extends RsCluster {

  ClusterHelper.initSeedNodes(core, nodes.toList).toBlocking.single

  def this(nodes: Seq[String]) {
    this(nodes, new CouchbaseCore)
  }

  override def openBucket(name: String, password: String): Publisher[RsBucket] = {
    toPublisher(
      ClusterHelper.openBucket(core, name, password).map[RsBucket](new Func1[Boolean, RsBucket] {
        override def call(success: Boolean): RsBucket = new RsCouchbaseBucket(core, name)
      })
    )
  }

}

object RsCouchbaseCluster {
  def apply(): RsCouchbaseCluster = new RsCouchbaseCluster(List("127.0.0.1"))
  def apply(nodes: Seq[String]): RsCouchbaseCluster = new RsCouchbaseCluster(nodes)
}