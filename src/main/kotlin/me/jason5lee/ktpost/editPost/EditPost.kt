package me.jason5lee.ktpost.editPost

import me.jason5lee.ktpost.common.*
import me.jason5lee.resukt.Result
import me.jason5lee.resukt.andThen

data class Command(
  val creator: UserId,
  val id: PostId,
  val newContent: PostContent,
)

interface EditPost : suspend (Command) -> Result<Unit, Failure> {
  override suspend fun invoke(command: Command): Result<Unit, Failure> =
    checkCreatorAndPostType(
      command.id,
      command.creator,
      command.newContent,
    ).andThen {
      updatePost(command.id, command.newContent)
    }

  suspend fun checkCreatorAndPostType(
    post: PostId,
    expectedCreator: UserId,
    content: PostContent,
  ): Result<Unit, Failure>

  suspend fun updatePost(post: PostId, newContent: PostContent): Result<Unit, Failure>
}

enum class Failure {
  PostNotFound,
  NotCreator,
  PostTypeDiffers,
}
