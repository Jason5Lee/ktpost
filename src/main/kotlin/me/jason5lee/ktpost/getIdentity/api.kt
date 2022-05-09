package me.jason5lee.ktpost.getIdentity

import com.fasterxml.jackson.annotation.JsonInclude
import me.jason5lee.ktpost.common.*
import me.jason5lee.resukt.getOrElse

internal fun getIdentityApi(getIdentity: GetIdentity, auth: Auth) = api(
  path = { get("/identity") },
  needRequestBody = true,
) { ctx ->
  val input = auth.getIdentity(ctx).getOrElse { return@api ctx.respondAuthFailure(it) }
  val output = getIdentity(input)

  @Suppress("unused")
  class UserDto(
    val id: String,
    val name: String,
  )

  @Suppress("unused")
  class AdminDto(
    val id: String,
  )

  @Suppress("unused")
  @JsonInclude(JsonInclude.Include.NON_NULL)
  class ResponseDto(
    val user: UserDto? = null,
    val admin: AdminDto? = null,
  )

  ctx.json(
    when (output) {
      is IdentityInfo.Admin -> ResponseDto(
        admin = AdminDto(id = formatId(output.id.value)),
      )
      is IdentityInfo.User -> ResponseDto(
        user = UserDto(
          id = formatId(output.id.value),
          name = output.name.value
        )
      )
      null -> ResponseDto()
    }
  )
}
