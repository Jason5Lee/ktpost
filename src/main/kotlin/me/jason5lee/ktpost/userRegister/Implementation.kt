package me.jason5lee.ktpost.userRegister


import com.relops.snowflake.Snowflake
import io.vertx.kotlin.coroutines.await
import io.vertx.mysqlclient.MySQLPool
import io.vertx.sqlclient.Tuple
import me.jason5lee.ktpost.common.*
import me.jason5lee.resukt.Result
import me.jason5lee.resukt.ext.asFailure
import java.time.Instant

class Implementation(private val mysql: MySQLPool, private val encryptor: Encryptor, private val snowflake: Snowflake) :
  UserRegister {
  override suspend fun writeUser(input: Command): Result<UserId, Failure> {
    val userName = input.userName.value
    val password = input.password.encrypt(encryptor)
    val userId = snowflake.next()

    try {
      mysql.preparedQuery("INSERT INTO users (user_id, user_name, password, creation_time) VALUES (?, ?, ?, ?)")
        .execute(Tuple.of(userId, userName, password, Instant.now().toEpochMilli()))
        .await()
    } catch (e: io.vertx.mysqlclient.MySQLException) {
      e.message?.let { msg ->
        if (msg.contains("Duplicate entry") && msg.contains("for key 'idx_user_name'")) {
          return Failure.UserNameExists.asFailure()
        }
      }
      throw e
    }
    return Result.success(UserId(userId))
  }

}
