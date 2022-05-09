package me.jason5lee.ktpost.userLogin

import me.jason5lee.ktpost.common.*
import me.jason5lee.resukt.Result
import me.jason5lee.resukt.whenFailure

data class Query(
  val userName: UserName,
  val password: Password,
)

interface UserLogin : suspend (Query) -> Result<UserId, Failure> {
  override suspend operator fun invoke(input: Query): Result<UserId, Failure> {
    val (id, encryptedPassword) = getUserIdAndEncryptedPasswordByName(input.userName)
      .whenFailure { return it }
    return if (input.password.verifyEncrypted(encryptedPassword)) {
      Result.success(id)
    } else {
      Result.failure(Failure.UserNameOrPasswordIncorrect)
    }
  }

  suspend fun getUserIdAndEncryptedPasswordByName(userName: UserName): Result<Pair<UserId, String>, Failure>
}

enum class Failure {
  UserNameOrPasswordIncorrect
}
