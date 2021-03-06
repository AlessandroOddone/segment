package dev.aoddon.connector.codegen.util

import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.ParameterizedTypeName
import com.squareup.kotlinpoet.TypeName

internal fun TypeName.classNameOrThrow(): ClassName = when (this) {
  is ClassName -> this
  is ParameterizedTypeName -> rawType
  else -> throw IllegalArgumentException("Can't get class name of $this")
}

internal fun TypeName.classNameOrNull(): ClassName? = try {
  classNameOrThrow()
} catch (_: Exception) {
  null
}

internal fun TypeName.nonNull(): TypeName = if (isNullable) copy(nullable = false) else this
