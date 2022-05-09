package me.jason5lee.ktpost.getPost

import me.jason5lee.ktpost.common.*
import me.jason5lee.resukt.Result

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

enum class Failure {
  PostNotFound
}

interface GetPost : suspend (Query) -> Result<PostInfoForPage, Failure>
