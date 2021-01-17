package connector.util

import com.tschuchort.compiletesting.KotlinCompilation
import com.tschuchort.compiletesting.SourceFile
import com.tschuchort.compiletesting.symbolProcessors
import connector.processor.ConnectorProcessor
import java.io.File

fun SourceFile.runTestCompilation(block: TestCompilationContext.() -> Unit) {
  val compilation = KotlinCompilation().apply {
    symbolProcessors = listOf(ConnectorProcessor())
    classpaths = listOf(
      File("../connector-runtime-core/build/classes/kotlin/jvm/main"),
      File("../connector-runtime-http/build/classes/kotlin/jvm/main")
    )
    sources = listOf(this@runTestCompilation)
  }
  block(TestCompilationContextImpl(compilation.compile()))
}

interface TestCompilationContext {
  fun assertKspErrors(vararg expectedErrors: KspError)

  infix fun String.atLine(line: Int) = KspError(line, this)
}

data class KspError(val line: Int, val message: String) {
  override fun toString() = "(line $line) '$message'"

  companion object {
    fun parse(message: String): KspError? {
      if (!message.startsWith("e: [ksp]")) {
        return null
      }
      return message
        .split(":", limit = 3)
        .last()
        .split(":", limit = 2)
        .let { KspError(line = it[0].toInt(), message = it[1].trimStart()) }
    }
  }
}

private class TestCompilationContextImpl(
  private val compilationResult: KotlinCompilation.Result
) : TestCompilationContext {
  override fun assertKspErrors(vararg expectedErrors: KspError) {
    val kspErrors = compilationResult.messages
      .lineSequence()
      .mapNotNull { KspError.parse(it) }
      .toList()

    val kspErrorLogsFormatted by lazy {
      kspErrors.joinToString("\n- ", prefix = "- ")
    }

    if (kspErrors.size != expectedErrors.size) {
      throw AssertionError(
        "Expected ${expectedErrors.size} KSP error logs," +
          " but ${kspErrors.size} were found:\n$kspErrorLogsFormatted"
      )
    }

    expectedErrors.groupBy { it.message }.forEach { (expectedErrorMessage, expectedOccurrences) ->
      val actualOccurrences = kspErrors.filter { it.message == expectedErrorMessage }
      when {
        expectedOccurrences == actualOccurrences -> {
        } // do nothing

        expectedOccurrences.isNotEmpty() && actualOccurrences.isEmpty() -> throw AssertionError(
          "Expected KSP error was not logged: '$expectedErrorMessage'\n" +
            "The following KSP error logs were collected:\n$kspErrorLogsFormatted\n"
        )

        else -> throw AssertionError(
          "KSP error was expected at line${if (expectedOccurrences.size > 1) "s" else ""} " +
            "${expectedOccurrences.joinToString { it.line.toString() }}, but was found at " +
            "${actualOccurrences.joinToString { it.line.toString() }}: '$expectedErrorMessage'\n" +
            "The following KSP error logs were collected:\n$kspErrorLogsFormatted\n"
        )
      }
    }
  }
}
