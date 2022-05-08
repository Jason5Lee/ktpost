package me.jason5lee.ktpost.adminLogin

import me.jason5lee.ktpost.common.AdminId
import me.jason5lee.ktpost.common.Password
import me.jason5lee.resukt.*

data class Command(
  val id: AdminId,
  val password: Password,
)

interface AdminLogin : suspend (Command) -> Result<Unit, Failure>

enum class Failure {
  IdOrPasswordIncorrect,
}
