package me.jason5lee.ktpost.adminRegister

import com.relops.snowflake.Snowflake
import io.vertx.kotlin.coroutines.await
import io.vertx.mysqlclient.MySQLPool
import io.vertx.sqlclient.Tuple
import me.jason5lee.ktpost.common.*

class Implementation(private val mysql: MySQLPool, private val encryptor: Encryptor, private val snowflake: Snowflake) :
  AdminRegister {
  override suspend fun invoke(password: Password): AdminId {
    val adminId = snowflake.next()
    val encryptedPwd = password.encrypt(encryptor)
    mysql.preparedQuery("INSERT INTO admins (admin_id, password) VALUES (?, ?)")
      .execute(Tuple.of(adminId, encryptedPwd))
      .await()
    return AdminId(adminId)
  }
}
