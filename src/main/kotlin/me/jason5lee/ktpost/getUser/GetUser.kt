package me.jason5lee.ktpost.getUser

import me.jason5lee.ktpost.common.*
import me.jason5lee.resukt.Result

typealias Query = UserId

data class UserInfo(
  val name: UserName,
  val creation: Time,
)

interface GetUser : suspend (Query) -> Result<UserInfo, Failure>

enum class Failure {
  UserNotFound
}
