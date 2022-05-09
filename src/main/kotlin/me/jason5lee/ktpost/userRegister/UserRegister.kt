package me.jason5lee.ktpost.userRegister

import me.jason5lee.ktpost.common.*
import me.jason5lee.resukt.Result

data class Command(
  val userName: UserName,
  val password: Password,
)

interface UserRegister : suspend (Command) -> Result<UserId, Failure> {
  override suspend fun invoke(command: Command): Result<UserId, Failure> =
    writeUser(command)

  suspend fun writeUser(input: Command): Result<UserId, Failure>
}

enum class Failure {
  UserNameExists
}
