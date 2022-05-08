package me.jason5lee.ktpost.adminRegister

import io.vertx.core.json.Json
import me.jason5lee.ktpost.common.*
import me.jason5lee.resukt.getOrElse

fun adminRegisterApi(adminRegister: AdminRegister) = api(
  path = { post("/admin/register") },
  needRequestBody = true,
) { ctx ->
  class RequestDto(
    val password: String
  )

  val req = Json.decodeValue(ctx.bodyAsString, RequestDto::class.java)
  val input = Password.fromPlain(req.password).getOrElse {
    return@api ctx.respondJson(statusCode = 422, FailureBody(field = "password", reason = it))
  }
  val output = adminRegister(input)

  @Suppress("unused")
  class ResponseDto(
    val adminId: String,
  )

  ctx.json(
    ResponseDto(
      adminId = formatId(output.value),
    )
  )
}
