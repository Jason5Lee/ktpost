package me.jason5lee.ktpost.common

import kotlin.reflect.KClass

class Dependencies {
  private var dependencies = mutableMapOf<KClass<*>, Any>()

  @PublishedApi
  internal fun getByClass(clazz: KClass<*>): Any? = dependencies[clazz]

  @PublishedApi
  internal fun setByClass(clazz: KClass<*>, instance: Any) {
    dependencies[clazz] = instance
  }

  inline fun <reified T : Any> getDependency(): T =
    getByClass(T::class)?.let { it as T }
      ?: throw IllegalStateException("Dependency not found: ${T::class.qualifiedName}")

  inline fun <reified T : Any> setDependency(value: T) {
    setByClass(T::class, value)
  }
}
