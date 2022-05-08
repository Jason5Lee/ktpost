package me.jason5lee.ktpost.getUser

import me.jason5lee.ktpost.common.Time
import me.jason5lee.ktpost.common.UserId
import me.jason5lee.ktpost.common.UserName
import me.jason5lee.resukt.*

typealias Query = UserId

data class UserInfo(
    val name: UserName,
    val creation: Time,
)
interface GetUser : suspend (Query) -> Result<UserInfo, Failure>

sealed class Failure {
  object UserNotFound : Failure()
}
