package me.jason5lee.ktpost.common

import at.favre.lib.crypto.bcrypt.BCrypt

class Encryptor(private val cost: Int) {
  private val bcrypt = BCrypt.withDefaults()

  fun encrypt(plain: String): String = String(bcrypt.hash(cost, plain.toByteArray(Charsets.UTF_8)), Charsets.UTF_8)

  companion object {
    private val verifier = BCrypt.verifyer()

    fun verify(password: String, encrypted: String): Boolean =
      verifier.verify(password.toByteArray(Charsets.UTF_8), encrypted.toByteArray(Charsets.UTF_8)).verified
  }
}
