Official Couchbase Scala SDK
============================

Welcome to our official Couchbase Scala client, the latest addition in the family of supported
client side drivers. It is currently a workin progress and once finished, will provide management,
CRUD and query facilities through both synchronous and asynchronous APIs.

Note that this driver builds on our proven [core-io](https://github.com/couchbase/couchbase-jvm-core) 
foundation like many other projects, so while the scala driver is quite young the underpinnings can 
be considered quite stable. If you are looking for something rock-solid and supported until this 
one is GA'ed, use the [java-client](https://github.com/couchbase/couchbase-java-client).

(Planned) Features
------------------
 - High-Performance Key/Value and Query (N1QL, Views) operations
 - Cluster-Awareness and automatic rebalance and failover handling
 - Asynchronous (through RxScala and Reactive Streams) and Synchronous APIs
 - Transparent Encryption Support
 - Cluster and Bucket level management facilities
 - Complete non-blocking stack through RxScala/Reactive Streams and Netty
 
Getting Help
------------
Since the project itself is currently emerging, the best way to get help is to ask through our
[official Forums](http://forums.couchbase.com), as well as raising a ticket in the github bugtracker. Once the project matures, 
we will add a regular JIRA project to track issues.

Finally, there is always our beloved #libcouchbase IRC channel!

Getting It
----------
Right now you need to build from source, since we haven't released a developer preview:

```
couchbase-scala-client $ sbt publish-m2
```

Using It
--------
Here is a quick sample which stores and loads a document. More to come.

```scala
val cluster = new CouchbaseCluster(List("127.0.0.1"))
val bucket = cluster.openBucket("default", "")

val stored = bucket.upsert(JsonDocument("my-doc", JsonObject()))
val loaded = bucket.get("my-doc")
println(loaded)
```