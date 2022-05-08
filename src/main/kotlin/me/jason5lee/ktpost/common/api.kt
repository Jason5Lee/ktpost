package me.jason5lee.ktpost.common

import io.vertx.ext.web.RoutingContext

suspend fun RoutingContext.respondAuthFailure(authFailure: AuthFailure) {
  when (authFailure) {
    AuthFailure.Unauthenticated ->
      respondJson(
        statusCode = 401, // 401 Unauthorized actually means unauthenticated
        FailureBody(
          reason = "Unauthenticated",
        )
      )
    AuthFailure.Unauthorized ->
      respondJson(
        statusCode = 403, // 403 Forbidden actually means unauthorized
        FailureBody(
          reason = "Unauthorized",
        )
      )
  }
}
