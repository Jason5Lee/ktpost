package me.jason5lee.ktpost.adminLogin

import io.vertx.core.json.Json
import me.jason5lee.ktpost.common.*
import me.jason5lee.resukt.getOrElse

internal fun adminLoginApi(adminLogin: AdminLogin, auth: Auth) = api(
  path = { post("/admin/login") },
  needRequestBody = true
) { ctx ->
  @Suppress("unused")
  class RequestDto(
    val id: String,
    val password: String,
  )

  val req = Json.decodeValue(ctx.bodyAsString, RequestDto::class.java)
  val input = Command(
    id = parseId(req.id)
      .getOrElse { return@api ctx.respondJson(422, FailureBody(field = "id", reason = it)) }
      .let { AdminId(it) },
    password = Password.fromPlain(req.password)
      .getOrElse { return@api ctx.respondJson(422, FailureBody(field = "password", reason = it)) }
  )
  adminLogin(input).getOrElse { failure ->
    when (failure) {
      Failure.IdOrPasswordIncorrect -> return@api ctx.respondJson(403, FailureBody(reason = "id or password incorrect"))
    }
  }

  @Suppress("unused")
  class ResponseDto(
    val expire: Long,
    val token: String,
  )

  val expire = auth.getExpireTime()
  ctx.json(
    ResponseDto(
      expire = expire,
      token = auth.generateToken(
        exp = expire,
        adminId = req.id,
      )
    )
  )
}

