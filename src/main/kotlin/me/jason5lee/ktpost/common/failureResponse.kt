package me.jason5lee.ktpost.common

import io.vertx.ext.web.RoutingContext
import io.vertx.kotlin.coroutines.await

suspend inline fun RoutingContext.respondJson(statusCode: Int, body: Any) {
  response().statusCode = statusCode
  json(body).await()
}
