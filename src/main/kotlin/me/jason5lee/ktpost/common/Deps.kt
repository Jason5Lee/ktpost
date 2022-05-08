package me.jason5lee.ktpost.common

import com.relops.snowflake.Snowflake
import io.vertx.ext.auth.jwt.JWTAuth
import io.vertx.mysqlclient.MySQLPool

class Deps(
    val logger: mu.KLogger,
    val auth: JWTAuth,
    val issuer: String,
    val expireSecs: Long,
    val snowflake: Snowflake,
    val encryptor: Encryptor,
    private val mysql: MySQLPool?,
) {
  fun getMySql(): MySQLPool = mysql ?: throw Throwable("MySQL not set")
}
