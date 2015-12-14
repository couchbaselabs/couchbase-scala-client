package com.couchbase.client.scala

import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import akka.stream.scaladsl.{Sink, Source}
import akka.stream.testkit.scaladsl.TestSink
import akka.testkit.TestKitBase
import com.couchbase.client.scala.document.JsonDocument
import com.couchbase.client.scala.document.json.JsonObject
import com.couchbase.client.scala.util.SpecProperties
import org.reactivestreams.{Subscription, Subscriber}
import org.scalatest.time.Milliseconds
import org.scalatest.{Matchers, FlatSpec}
import rx.RxReactiveStreams

import RxReactiveStreams.toObservable

class RsBucketSpec extends FlatSpec with Matchers {

  val cluster = new RsCouchbaseCluster(List(SpecProperties.seedNode))
  val bucket: RsBucket = toObservable(
    cluster.openBucket(SpecProperties.bucket, SpecProperties.password)
  ).toBlocking.single()

  implicit val as = ActorSystem()
  implicit val mat = ActorMaterializer()



  "A Document" should "be upserted and loaded correctly" in {

    Source(bucket.upsert(JsonDocument("rsdoc-1", JsonObject())))
      .runWith(TestSink.probe[JsonDocument])
      .request(1)
      .expectNext(JsonDocument("rsdoc-1"))
      .expectComplete()
  }

}
