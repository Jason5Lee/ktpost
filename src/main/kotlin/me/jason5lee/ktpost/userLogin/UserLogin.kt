package me.jason5lee.ktpost.userLogin

import me.jason5lee.ktpost.common.Password
import me.jason5lee.ktpost.common.UserId
import me.jason5lee.ktpost.common.UserName
import me.jason5lee.resukt.*

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

sealed class Failure {
  object UserNameOrPasswordIncorrect : Failure()
}
