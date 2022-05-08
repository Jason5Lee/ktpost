package me.jason5lee.ktpost.createPost

import me.jason5lee.ktpost.common.PostContent
import me.jason5lee.ktpost.common.PostId
import me.jason5lee.ktpost.common.PostTitle
import me.jason5lee.ktpost.common.UserId
import me.jason5lee.resukt.*

data class Command(
    val creator: UserId,
    val title: PostTitle,
    val content: PostContent,
)

interface CreatePost : suspend (Command) -> Result<PostId, Failure>

sealed class Failure {
  object DuplicatedTitle : Failure()
}
