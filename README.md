# ll


Situation when some API can't handle all incoming request require some
solutions. One of the most popular approaches is having queue before it,
but it can easily grow up and all incoming requests can wait in the queue really long timeout.
One more variant is add a token bucket or any similar algorithm.
For this we have to provide maximum system throughput by creating some refillable bucket with tokens.
If request can take one token, it will be processed. If there is no more tokens, so request will be rejected.

This solution doesn't require any configurations. Based on EWT (estimated wait time) and 
mean processing time, ll can estimate how much time new incoming request will wait to be processed.
If this estimation is too much for request it will be rejected.

It maybe helpful when some API face high load. 
Let's say important request's have big timeout, and some non-important have small timeout.
So, under load, important request can wait some time and
finally be processed while non-important will be rejected. 
