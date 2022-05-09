package me.jason5lee.ktpost.createPost

import me.jason5lee.ktpost.common.*
import me.jason5lee.resukt.Result

data class Command(
  val creator: UserId,
  val title: PostTitle,
  val content: PostContent,
)

interface CreatePost : suspend (Command) -> Result<PostId, Failure>

enum class Failure {
  DuplicatedTitle
}
