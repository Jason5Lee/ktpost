package me.jason5lee.ktpost.listPosts

import me.jason5lee.ktpost.common.*

data class Query(
    val offset: Offset,
    val size: Size,
)

data class PostInfo(
    val id: PostId,
    val title: PostTitle,
    val creator: Creator,
    val creation: Time,
)

data class Creator(
    val id: UserId,
    val name: UserName,
)
data class Output(
    val posts: List<PostInfo>,
)

interface ListPosts : suspend (Query) -> Output
