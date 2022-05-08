package me.jason5lee.ktpost.getIdentity

import io.vertx.kotlin.coroutines.await
import io.vertx.mysqlclient.MySQLPool
import io.vertx.sqlclient.Tuple
import me.jason5lee.ktpost.common.UserId
import me.jason5lee.ktpost.common.UserName
import me.jason5lee.ktpost.common.getStringModel

class Implementation(private val mysql: MySQLPool) : GetIdentity {
  override suspend fun getUserName(id: UserId): UserName =
    mysql.preparedQuery("SELECT user_name FROM users WHERE user_id=?")
      .execute(Tuple.of(id.value))
      .await()
      .first()
      .getStringModel("user_name") { UserName.new(it) }
}
