package me.jason5lee.ktpost.deletePost

import me.jason5lee.ktpost.common.*
import me.jason5lee.resukt.Result
import me.jason5lee.resukt.whenFailure

data class Command(
  val caller: Identity?,
  val id: PostId,
)

interface DeletePost : suspend (Command) -> Result<Unit, Failure> {
  override suspend fun invoke(input: Command): Result<Unit, Failure> {
    val auth = when (input.caller) {
      is Identity.Admin -> true
      is Identity.User -> isCreator(input.caller.id, input.id).whenFailure { return it }
      null -> false
    }

    return if (auth) {
      deletePost(input.id)
    } else {
      Result.failure(Failure.Unauthorized)
    }
  }

  suspend fun isCreator(user: UserId, post: PostId): Result<Boolean, Failure>
  suspend fun deletePost(post: PostId): Result<Unit, Failure>
}

enum class Failure {
  Unauthorized,
  PostNotFound,
}
