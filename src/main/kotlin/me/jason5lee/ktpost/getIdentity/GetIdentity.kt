package me.jason5lee.ktpost.getIdentity

import me.jason5lee.ktpost.common.AdminId
import me.jason5lee.ktpost.common.Identity
import me.jason5lee.ktpost.common.UserId
import me.jason5lee.ktpost.common.UserName

sealed class IdentityInfo {
  data class User(val id: UserId, val name: UserName) : IdentityInfo()
  data class Admin(val id: AdminId) : IdentityInfo()
}

interface GetIdentity : suspend (Identity?) -> IdentityInfo? {
  override suspend fun invoke(input: Identity?): IdentityInfo? =
    when (input) {
      is Identity.User -> IdentityInfo.User(
          id = input.id,
          name = getUserName(input.id)
      )
      is Identity.Admin -> IdentityInfo.Admin(
          id = input.id,
      )
      null -> null
    }

  suspend fun getUserName(id: UserId): UserName
}
