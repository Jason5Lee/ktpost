package me.jason5lee.ktpost.userLogin

import io.vertx.core.json.Json
import io.vertx.ext.web.RoutingContext
import me.jason5lee.ktpost.common.*
import me.jason5lee.resukt.getOrElse

fun userLoginApi(userLogin: UserLogin, auth: Auth) = api(
  path = { post("/login") },
  needRequestBody = true,
) { ctx ->
  class RequestDto(
    val userName: String,
    val password: String,
  )
  val req = Json.decodeValue(ctx.bodyAsString, RequestDto::class.java)
  val input = Query(
    userName = UserName.new(req.userName)
      .getOrElse { return@api ctx.respondInvalidUserName(it) },
    password = Password.fromPlain(req.password)
      .getOrElse { return@api ctx.respondInvalidPassword(it) },
  )
  val output = userLogin(input)
    .getOrElse { return@api ctx.respondFailure(it) }

  @Suppress("unused")
  class ResponseDto(
    val userId: String,
    val expire: Long,
    val token: String,
  )

  val idResp = formatId(output.value)

  val expire = auth.getExpireTime()
  ctx.json(ResponseDto(
    userId = idResp,
    expire = expire,
    token = auth.generateToken(
      exp = expire,
      userId = idResp,
    )
  ))
}

private suspend fun RoutingContext.respondInvalidUserName(reason: String) {
  respondJson(
    statusCode = 422,
    body = FailureBody(
      field = "userName",
      reason = reason,
    )
  )
}

private suspend fun RoutingContext.respondInvalidPassword(reason: String) {
  respondJson(
    statusCode = 422,
    body = FailureBody(
      field = "password",
      reason = reason,
    )
  )
}

private suspend fun RoutingContext.respondFailure(failure: Failure) {
  when (failure) {
    Failure.UserNameOrPasswordIncorrect -> respondJson(
      statusCode = 403,
      body = FailureBody(
        reason = "user name or password incorrect"
      )
    )
  }
}
