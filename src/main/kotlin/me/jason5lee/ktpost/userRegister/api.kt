package me.jason5lee.ktpost.userRegister

import io.vertx.core.json.Json
import me.jason5lee.ktpost.common.*
import me.jason5lee.resukt.getOrElse

internal fun userRegisterApi(userRegister: UserRegister) = api(
  path = { post("/register") },
  needRequestBody = true,
) { ctx ->
  class RequestDto(
    val userName: String,
    val password: String,
  )

  val req = Json.decodeValue(ctx.bodyAsString, RequestDto::class.java)
  val input = Command(
    userName = UserName.new(req.userName).getOrElse {
      return@api ctx.respondJson(statusCode = 422, FailureBody(field = "userName", reason = it))
    },
    password = Password.fromPlain(req.password).getOrElse {
      return@api ctx.respondJson(statusCode = 422, FailureBody(field = "password", reason = it))
    },
  )
  val output = userRegister(input)
    .getOrElse { failure ->
      when (failure) {
        Failure.UserNameExists -> return@api ctx.respondJson(
          statusCode = 409,
          FailureBody(field = "userName", reason = "UserName already exists")
        )
      }
    }

  @Suppress("unused")
  class ResponseDto(
    val userId: String,
  )
  ctx.json(
    ResponseDto(
      userId = formatId(output.value),
    )
  )
}
