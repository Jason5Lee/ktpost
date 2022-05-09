package me.jason5lee.ktpost.common

import me.jason5lee.resukt.Result
import java.net.URL

@JvmInline
value class Time(val utc: Long)

@JvmInline
value class UserId(val value: Long)

@JvmInline
value class AdminId(val value: Long)

sealed class Identity {
  data class User(val id: UserId) : Identity()
  data class Admin(val id: AdminId) : Identity()
}

@JvmInline
value class UserName private constructor(val value: String) {
  companion object {
    fun new(value: String): Result<UserName, String> =
      when {
        value.isEmpty() -> Result.failure("user name cannot be empty")
        value.length > 20 -> Result.failure("user name too long")
        value.length < 3 -> Result.failure("user name too short")
        !value.all { it.isLegalChar() } -> Result.failure("user name contains illegal character")
        else -> Result.success(UserName(value))
      }

    private fun Char.isLegalChar(): Boolean =
      isLetterOrDigit() || this == '-' || this == '_'
  }
}

@JvmInline
value class Password private constructor(private val plain: String) {
  companion object {
    fun fromPlain(value: String): Result<Password, String> =
      when {
        value.isEmpty() -> Result.failure("password empty")
        value.length < 5 -> Result.failure("password too short")
        value.length > 72 -> Result.failure("password too long")
        else -> Result.success(Password(value))
      }
  }

  fun encrypt(encryptor: Encryptor): String = encryptor.encrypt(plain)
  fun verifyEncrypted(encrypted: String): Boolean = Encryptor.verify(plain, encrypted)
}

@JvmInline
value class PostId(val value: Long)

@JvmInline
value class PostTitle private constructor(val value: String) {
  companion object {
    fun new(value: String): Result<PostTitle, String> = when {
      value.isEmpty() -> Result.failure("post title cannot be empty")
      value.length > 171 -> Result.failure("post title too long")
      else -> Result.success(PostTitle(value))
    }
  }
}

sealed class PostContent {
  data class Post(val text: String) : PostContent()

  data class Url(val url: URL) : PostContent() {
    companion object {
      fun create(value: String): Result<Url, String> = try {
        Result.success(Url(URL(value)))
      } catch (e: java.net.MalformedURLException) {
        Result.failure(e.toString())
      }
    }
  }
}

@JvmInline
value class Size private constructor(val value: Int) {
  companion object {
    private const val DEFAULT = 20
    private const val MAX = 500

    fun new(value: Int?): Result<Size, String> =
      if (value == null)
        Result.success(Size(DEFAULT))
      else if (value <= 0)
        Result.failure("size should be positive")
      else if (value > MAX)
        Result.failure("size too large")
      else Result.success(Size(value))
  }
}

@JvmInline
value class Offset private constructor(val value: Int) {
  companion object {
    fun new(value: Int?): Result<Offset, String> =
      if (value == null)
        Result.success(Offset(0))
      else if (value <= 0)
        Result.failure("offset should be positive")
      else Result.success(Offset(value))
  }
}
