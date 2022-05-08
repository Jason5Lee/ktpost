package me.jason5lee.ktpost.userLogin

import io.vertx.kotlin.coroutines.await
import io.vertx.mysqlclient.MySQLPool
import io.vertx.sqlclient.Tuple
import me.jason5lee.ktpost.common.UserId
import me.jason5lee.ktpost.common.UserName
import me.jason5lee.ktpost.common.getNonNullLong
import me.jason5lee.ktpost.common.getNonNullString
import me.jason5lee.resukt.*

class Implementation(private val mysql: MySQLPool) : UserLogin {
  override suspend fun getUserIdAndEncryptedPasswordByName(userName: UserName): Result<Pair<UserId, String>, Failure> {
    val row = mysql.preparedQuery("SELECT user_id,password FROM users WHERE user_name=?")
      .execute(Tuple.of(userName.value))
      .await()
      .firstOrNull() ?: return Result.failure(Failure.UserNameOrPasswordIncorrect)
    return Result.success(Pair(UserId(row.getNonNullLong("user_id")), row.getNonNullString("password")))
  }
}
