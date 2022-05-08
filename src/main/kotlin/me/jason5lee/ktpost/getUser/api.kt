package me.jason5lee.ktpost.getUser

import io.vertx.ext.web.RoutingContext
import me.jason5lee.ktpost.common.*
import me.jason5lee.resukt.getOrElse

internal fun getUserApi(getUser: GetUser) = api(
  path = { get("/user/:userId") },
) { ctx ->
  val input = UserId(
    parseId(ctx.pathParam("userId") ?: return@api ctx.respondNoUserId())
      .getOrElse { return@api ctx.respondUserNotFound() }
  )
  val output = getUser(input).getOrElse {
    return@api when(it) {
      is Failure.UserNotFound -> ctx.respondUserNotFound()
    }
  }

  @Suppress("unused")
  class ResponseDto(
    val userName: String,
    val creationTime: Long,
  )

  ctx.json(
    ResponseDto(
      userName = output.name.value,
      creationTime = output.creation.utc,
    )
  )
}

suspend fun RoutingContext.respondUserNotFound() = respondJson(404, FailureBody(reason = "user not found"))

suspend fun RoutingContext.respondNoUserId(): Unit =
  respondJson(
    statusCode = 400, // Bad Request
    FailureBody(
      reason = "parameter userId not provided"
    )
  )
