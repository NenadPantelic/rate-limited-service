# Rate limiting

## The problem

- Today, the scalability, availability and reliability are paramount for all web applications.
- When the app faces a high traffic that makes an impact to its stable functioning. A reasonable thinking when it comes for the first time is to make a vertical scaling (bump up the infra resources used by the app - memory, computational power, storage and network resources).
- still, if the problem keeps happening, there is a need for design changes. You must protect your service from accidental or deliberate fire in the hole made by the clients. (DoS attack caused by intent or by mistake).
- _Rate limiting_ can help make your API more reliable in the following scenarios:

1. One of your users is responsible for a spike in traffic, and you need to stay up for everyone else.
2. One of your users has a misbehaving script which is accidentally sending you a lot of requests. Or, even worse, one of your users is intentionally trying to overwhelm your servers.
3. A user is sending you a lot of lower-priority requests, and you want to make sure that it doesn’t affect your high-priority traffic. For example, users sending a high volume of requests for analytics data could affect critical transactions for other users.
   Something in your system has gone wrong internally, and as a result you can’t serve all of your regular traffic and need to drop low-priority requests.

**Rate Limiting** is a technique used to limit the amount of requests a client can send to
your server.

> Q: When to use rate limiting? <br>
> A: if your users can reduce the frequency of their API requests without affecting the outcome of their requests (if someone checks the the list of their followers on Instagram 50 times per second, it is very unlikely than number will change dramatically in a minute-window)

## A word or two about the load

Let's define the load in terms of two properties:

- number of in-flight requests
- the time a request spends waiting to be serviced

Another metrics surely are the properties like CPU usage or memory utilisation. Our rate limiting/load shedding system should have the following traits:

1. Limiting the number of concurrent requests

- the number of open requests in the system should be bounded so that the amount of work a server has to do does not pile up.
  In queuing theory, Little’s Law says that in the stationary (stable) state of a system, the following condition holds true:

`L = λ W`

```
L = Average number of requests being processed
λ = Average rate of incoming requests
W = Average time a request takes
```

2. Bounded queueing time

`R = W + S`

```
R = Total time taken to respond to a request
W = Time spent in the request queue
S = Time taken by the handler to process the request
```

- in simple terms, if you're waiting in a queue in front of the post, no matter if the post officer spends just 2 minutes to serve the client, if there are 10 people in front of you, you will wait much longer than 2 minutes.

- the time a request spends in the queue waiting for a server thread is often overlooked in services, primarily because the application does not maintain the request queue, it is done by the web server container like Jetty or Tomcat. However, when the load is high, the wait-time becomes the dominant factor. Just limiting the number of concurrent requests is not enough because requests still spend time waiting for service which can have an adverse effect on the quality of service.

3. Disaster recovery by unblocking clients

- once the system has detected the condition for shedding load, the service should communicate this to the client. This has a three-fold utility. Firstly, this helps clear out system resources like open TCP connections and request context in memory. Secondly, this provides a way to communicate to clients that the service is under load, which in the absence of a load-shedding strategy, could mean that the client never sees a response from the server and is forced to time out on its own. Thirdly, by closing client connections for old requests, it frees up resources that can be utilised to respond to new requests. Returning 429 or 503 status code, depending on the design, would be a good behavior of an API.

## Rate limiting and load shedding

- Rate limiting is a great strategy for day-to-day operations, but in some situations that won't help since some component will be down and the request won't be able to be processed at normal rate. Another example would be a situation where we a have a huge load, where some of the requests are extremely vital and resource intensive, while others are less important.
- In such a case, we can apply **load shedding**.

> Load shedding is a technique where we drop low-priority requests to make sure that critical actions are served properly.

## An example of applying the rate limiting

Stripe is a payment processing company. They have implemented 2 types of rate limiters and 2 types of load shedding.

### Request Rate Limiter

Restricts each user to n requests per second. However, they also built in the ability for a user to briefly burst above the cap to handle legitimate spikes in usage (e.g. during the flash sale).

### Concurrent Requests Limiter

Restricts each user to n API requests in progress at the same time. This helps Stripe managing the load of their CPU-intensive API endpoints.

Instead of saying "you can use our API 1000 times a second", this strategy says "you can only have 20 API requests being processed at the same time".

### Fleet Usage Load Shedder

- Stripe divides their traffic into two types: critical API methods and non-critical methods.
- Critical methods would be creating a charge (charging a customer for something), while a non-critical method is listing a charge(looking at past charges).
- Stripe always reserves a fraction of their infrastructure for critical requests. For an example, if they reserve 10% of the limit to serve such requests, then any non-critical request over the 90% allocation would be rejected with a 503 status code.

### Worker Utilization Load Shedder

- Since Stripe uses a set of workers to independently respond to incoming requests in parallel. Stripe divide the traffic coming to these workers into 4 categories:

  - Critical methods
  - POSTs
  - GETs
  - Test mode traffic (requests made internally made by their engineering in their day-to-day acitivities)

- If worker capacity goes below a certain threshold, Stripe will begin shedding less-critical requests, starting from test mode traffic.

## Rate limiter algorithms

**Token Bucket** - Every user gets a bucket with a certain amount of _tokens_. On each
request, tokens are removed from the bucket. If the bucket is empty, then the request is
rejected.
New tokens are added to the bucket at a certain threshold (every n seconds). The bucket
can hold a certain number of tokens, so if the bucket is full of tokens then no new tokens
will be added.

**Fixed Window** - The rate limiter uses a window size of n seconds for a user. Each
incoming request from the user will increment the counter for the window. If the
counter exceeds a certain threshold, then requests will be discarded.
After the n second window passes, a new window is created.

**Sliding Log** - The rate limiter track’s every user’s request in a time-stamped log. When a
new request comes in, the system calculates the sum of logs to determine the request
rate. If the request rate exceeds a certain threshold, then it is denied.
After a certain period of time, previous requests are discarded from the log.

### Running the demo project

Here I have built a demo project which relies on Token Bucket algorithm. The service represents a dummy quote generator service with two endpoints.

### Setup

- This service stores the data in MongoDB, so you need it up & running. You can simply do it by running the compose file.

```bash
docker compose up -d
```

- You can adjust the settings by changing the config in `application.properties` file. The important properties are the following:
  `rate-limit.strategy` - can have the values `PLAIN` (from scratch implementation), `BUCKET4J` (uses the [bucket4j lib](https://github.com/bucket4j/bucket4j)) or `NONE` (rate limiting turned off).

```bash
# PLAIN mode - capacity of the token bucket
rate-limit.token-bucket-params.FREE.capacity=100
# PLAIN mode - token bucket refill period in milliseconds
rate-limit.token-bucket-params.FREE.refill-interval-in-millis=3600000
# BUCKET4J mode - capacity of the token bucket
rate-limit.bandwidths.FREE.capacity=100
# BUCKET4J mode - token bucket refill period in minutes
rate-limit.bandwidths.FREE.refill-interval-in-minutes=60
```

**Note:** this project is implemented to demonstrate some concepts of the system design and it is used as a demo project for the course _Design of the information systems and databases_ at the University of Kragujevac (Faculty of Engineering).
