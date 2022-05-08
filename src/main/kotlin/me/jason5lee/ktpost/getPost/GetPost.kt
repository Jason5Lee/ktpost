package me.jason5lee.ktpost.getPost

import me.jason5lee.ktpost.common.*
import me.jason5lee.resukt.*

data class CreatorInfo(
    val name: UserName,
    val id: UserId,
)

data class PostInfoForPage(
    val creator: CreatorInfo,
    val creation: Time,
    val lastModified: Time?,
    val title: PostTitle,
    val content: PostContent,
)

typealias Query = PostId

sealed class Failure {
  object PostNotFound : Failure()
}

interface GetPost : suspend (Query) -> Result<PostInfoForPage, Failure>
