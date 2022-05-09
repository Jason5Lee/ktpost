package me.jason5lee.ktpost.adminLogin

import me.jason5lee.ktpost.common.*
import me.jason5lee.resukt.Result

data class Command(
  val id: AdminId,
  val password: Password,
)

interface AdminLogin : suspend (Command) -> Result<Unit, Failure>

enum class Failure {
  IdOrPasswordIncorrect,
}
