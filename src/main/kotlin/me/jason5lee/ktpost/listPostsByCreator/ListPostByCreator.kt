package me.jason5lee.ktpost.listPostsByCreator

import me.jason5lee.ktpost.common.*
import me.jason5lee.resukt.*

data class Query(
    val creator: UserId,
    val offset: Offset,
    val size: Size,
)

data class PostInfo(
    val id: PostId,
    val title: PostTitle,
    val creation: Time,
)

interface ListPostByCreator : suspend (Query) -> Result<List<PostInfo>, Failure>

enum class Failure {
  CreatorNotFound,
}
