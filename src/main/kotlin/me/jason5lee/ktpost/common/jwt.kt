package me.jason5lee.ktpost.common

import io.vertx.core.json.JsonObject
import io.vertx.ext.auth.jwt.JWTAuth
import io.vertx.ext.web.RoutingContext
import io.vertx.kotlin.coroutines.await
import me.jason5lee.resukt.Result
import me.jason5lee.resukt.fold
import java.time.Instant

class Auth(
  private val logger: mu.KLogger,
  private val jwtAuth: JWTAuth,
  private val issuer: String,
  private val validSecs: Long,
) {
  suspend fun getIdentity(ctx: RoutingContext): Result<Identity?, AuthFailure> {
    val headerContent = ctx.request().headers().get("Authorization") ?: return Result.success(null)
    if (!headerContent.startsWith("Bearer ")) {
      return Result.failure(AuthFailure.Unauthenticated)
    }

    val token = headerContent.substring(7)
    val user = try {
      jwtAuth.authenticate(
        JsonObject()
          .put("token", token)
          .put("issuer", issuer)
      )
        .await()
    } catch (e: io.vertx.core.impl.NoStackTraceThrowable) {
      return Result.failure(AuthFailure.Unauthenticated)
    } catch (e: RuntimeException) {
      return Result.failure(AuthFailure.Unauthenticated)
    }
    val principal = user.principal()

    return if (principal.containsKey("userId")) {
      principal.getString("userId")
        .let {
          parseId(it).fold(
            onSuccess = { id -> Result.success(Identity.User(UserId(id))) },
            onFailure = {
              logger.error { "invalid id in user token" }
              Result.failure(AuthFailure.Unauthenticated)
            }
          )
        }
    } else if (principal.containsKey("adminId")) {
      principal.getString("adminId")
        .let {
          parseId(it).fold(
            onSuccess = { id -> Result.success(Identity.Admin(AdminId(id))) },
            onFailure = {
              logger.error { "invalid id in user token" }
              Result.failure(AuthFailure.Unauthenticated)
            }
          )
        }
    } else {
      Result.failure(AuthFailure.Unauthenticated)
    }
  }

  fun getExpireTime(): Long = Instant.now().epochSecond + validSecs

  fun generateToken(
    exp: Long,
    userId: String? = null,
    adminId: String? = null
  ): String {
    val result = JsonObject()
      .put("iss", issuer)
      .put("exp", exp)
    if (userId != null) {
      result.put("userId", userId)
    }
    if (adminId != null) {
      result.put("adminId", adminId)
    }
    return jwtAuth.generateToken(result)
  }

  suspend fun authUserOnly(ctx: RoutingContext): Result<UserId, AuthFailure> {
    val headerContent =
      ctx.request().headers().get("Authorization") ?: return Result.failure(AuthFailure.Unauthenticated)
    if (!headerContent.startsWith("Bearer ")) {
      return Result.failure(AuthFailure.Unauthenticated)
    }

    val token = headerContent.substring(7)
    val user = try {
      jwtAuth.authenticate(
        JsonObject()
          .put("token", token)
          .put("issuer", issuer)
      )
        .await()
    } catch (e: io.vertx.core.impl.NoStackTraceThrowable) {
      return Result.failure(AuthFailure.Unauthenticated)
    }
    val principal = user.principal()

    return if (principal.containsKey("userId")) {
      principal.getString("userId")
        .let {
          parseId(it).fold(
            onSuccess = { id -> Result.success(UserId(id)) },
            onFailure = {
              logger.error { "invalid id in user token" }
              Result.failure(AuthFailure.Unauthenticated)
            }
          )
        }
    } else {
      Result.failure(AuthFailure.Unauthorized)
    }
  }
}

enum class AuthFailure {
  Unauthenticated,
  Unauthorized,
}
