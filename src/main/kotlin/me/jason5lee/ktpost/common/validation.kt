package me.jason5lee.ktpost.common

interface StringValidator {
  fun validate(value: String, field: String)
}
class StringField private constructor(val value: String, val field: String) {
  override fun equals(other: Any?): Boolean {
    if (this === other) return true
    if (javaClass != other?.javaClass) return false

    other as StringField

    if (value != other.value) return false

    return true
  }

  override fun hashCode(): Int {
    return value.hashCode()
  }

  override fun toString(): String {
    return "StringField(value='$value', field='$field')"
  }

}
