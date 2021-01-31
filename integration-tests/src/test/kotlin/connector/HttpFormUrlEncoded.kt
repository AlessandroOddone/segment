package connector

import connector.http.Field
import connector.http.FieldMap
import connector.http.FormUrlEncoded
import connector.http.POST
import connector.util.assertHttpLogMatches
import connector.util.hasRequestBody
import connector.util.runHttpTest
import io.ktor.http.ContentType
import io.ktor.http.Parameters
import io.ktor.http.Url
import io.ktor.http.parametersOf
import io.ktor.http.withCharset
import io.ktor.util.StringValues
import org.junit.Test

private val BASE_URL = Url("https://formUrlEncoded/")

@Service interface HttpFormUrlEncodedTestService {
  @POST("post")
  @FormUrlEncoded
  suspend fun formUrlEncodedTextField(@Field("f") text: String)

  @POST("post")
  @FormUrlEncoded
  suspend fun formUrlEncodedAnyField(@Field("f") any: Any)

  @POST("post")
  @FormUrlEncoded
  suspend fun formUrlEncodedStringIterableField(@Field("f") values: Iterable<String>)

  @POST("post")
  @FormUrlEncoded
  suspend fun formUrlEncodedAnyIterableField(@Field("f") values: Iterable<Any>)

  @POST("post")
  @FormUrlEncoded
  suspend fun formUrlEncodedStringCollectionField(@Field("f") values: Collection<String>)

  @POST("post")
  @FormUrlEncoded
  suspend fun formUrlEncodedAnyCollectionField(@Field("f") values: Collection<Any>)

  @POST("post")
  @FormUrlEncoded
  suspend fun formUrlEncodedStringListField(@Field("f") values: List<String>)

  @POST("post")
  @FormUrlEncoded
  suspend fun formUrlEncodedAnyListField(@Field("f") values: List<Any>)

  @POST("post")
  @FormUrlEncoded
  suspend fun formUrlEncodedStringSetField(@Field("f") values: Set<String>)

  @POST("post")
  @FormUrlEncoded
  suspend fun formUrlEncodedAnySetField(@Field("f") values: Set<Any>)

  @POST("post")
  @FormUrlEncoded
  suspend fun formUrlEncodedStringMap(@FieldMap map: Map<String, String>)

  @POST("post")
  @FormUrlEncoded
  suspend fun formUrlEncodedAnyMap(@FieldMap map: Map<String, Any>)

  @POST("post")
  @FormUrlEncoded
  suspend fun formUrlEncodedMapOfIterableString(@FieldMap map: Map<String, Iterable<String>>)

  @POST("post")
  @FormUrlEncoded
  suspend fun formUrlEncodedMapOfIterableAny(@FieldMap map: Map<String, Iterable<Any>>)

  @POST("post")
  @FormUrlEncoded
  suspend fun formUrlEncodedStringValues(@FieldMap stringValues: StringValues)

  @POST("post")
  @FormUrlEncoded
  suspend fun formUrlEncodedMultipleParameters(
    @Field("f1") text1: String,
    @Field("f1") any1: Any,
    @Field("f2") text2: String,
    @Field("f2") any2: Any,
  )

  @POST("post")
  @FormUrlEncoded
  suspend fun formUrlEncodedNullableTypes(
    @Field("f1") text: String?,
    @Field("f2") any: Any?,
    @Field("f3") list: List<Any?>?,
    @FieldMap stringValues: StringValues?,
    @FieldMap map1: Map<String, List<String?>?>?,
    @FieldMap map2: Map<String, Collection<Any?>?>?
  )
}

class HttpFormUrlEncoded {
  @Test fun `@FormUrlEncoded text @Field`() = runHttpTest {
    val service = HttpFormUrlEncodedTestService(BASE_URL, httpClient)
    service.formUrlEncodedTextField("value")
    assertHttpLogMatches {
      hasUrl("https://formUrlEncoded/post")
      hasRequestBody(
        "f=value",
        ContentType.Application.FormUrlEncoded.withCharset(Charsets.UTF_8)
      )
    }
  }

  @Test fun `@FormUrlEncoded Any @Field`() = runHttpTest {
    val service = HttpFormUrlEncodedTestService(BASE_URL, httpClient)
    service.formUrlEncodedAnyField(
      object : Any() {
        override fun toString() = "abc"
      }
    )
    assertHttpLogMatches {
      hasUrl("https://formUrlEncoded/post")
      hasRequestBody(
        "f=abc",
        ContentType.Application.FormUrlEncoded.withCharset(Charsets.UTF_8)
      )
    }
  }

  @Test fun `@FormUrlEncoded String Iterable @Field`() = runHttpTest {
    val service = HttpFormUrlEncodedTestService(BASE_URL, httpClient)

    service.formUrlEncodedStringIterableField(emptyList())
    service.formUrlEncodedStringCollectionField(listOf("1", "2", "3"))
    service.formUrlEncodedStringListField(listOf("4", "5", "6"))
    service.formUrlEncodedStringSetField(setOf("7"))

    assertHttpLogMatches(
      {
        hasUrl("https://formUrlEncoded/post")
        hasRequestBody(
          ByteArray(0),
          ContentType.Application.FormUrlEncoded.withCharset(Charsets.UTF_8)
        )
      },
      {
        hasUrl("https://formUrlEncoded/post")
        hasRequestBody(
          "f=1&f=2&f=3",
          ContentType.Application.FormUrlEncoded.withCharset(Charsets.UTF_8)
        )
      },
      {
        hasUrl("https://formUrlEncoded/post")
        hasRequestBody(
          "f=4&f=5&f=6",
          ContentType.Application.FormUrlEncoded.withCharset(Charsets.UTF_8)
        )
      },
      {
        hasUrl("https://formUrlEncoded/post")
        hasRequestBody(
          "f=7",
          ContentType.Application.FormUrlEncoded.withCharset(Charsets.UTF_8)
        )
      },
    )
  }

  @Test fun `@FormUrlEncoded Any Iterable @Field`() = runHttpTest {
    val service = HttpFormUrlEncodedTestService(BASE_URL, httpClient)

    service.formUrlEncodedAnyIterableField(emptyList())
    service.formUrlEncodedAnyCollectionField(
      listOf(
        "1",
        object : Any() {
          override fun toString(): String = "2"
        },
        "3"
      )
    )
    service.formUrlEncodedAnyListField(
      listOf(
        object : Any() {
          override fun toString(): String = "4"
        },
        "5",
        object : Any() {
          override fun toString(): String = "6"
        },
      )
    )
    service.formUrlEncodedAnySetField(
      setOf(
        object : Any() {
          override fun toString(): String = "7"
        },
      )
    )

    assertHttpLogMatches(
      {
        hasUrl("https://formUrlEncoded/post")
        hasRequestBody(
          ByteArray(0),
          ContentType.Application.FormUrlEncoded.withCharset(Charsets.UTF_8)
        )
      },
      {
        hasUrl("https://formUrlEncoded/post")
        hasRequestBody(
          "f=1&f=2&f=3",
          ContentType.Application.FormUrlEncoded.withCharset(Charsets.UTF_8)
        )
      },
      {
        hasUrl("https://formUrlEncoded/post")
        hasRequestBody(
          "f=4&f=5&f=6",
          ContentType.Application.FormUrlEncoded.withCharset(Charsets.UTF_8)
        )
      },
      {
        hasUrl("https://formUrlEncoded/post")
        hasRequestBody(
          "f=7",
          ContentType.Application.FormUrlEncoded.withCharset(Charsets.UTF_8)
        )
      },
    )
  }

  @Test fun `@FormUrlEncoded @FieldMap of Strings`() = runHttpTest {
    val service = HttpFormUrlEncodedTestService(BASE_URL, httpClient)

    service.formUrlEncodedStringMap(emptyMap())
    service.formUrlEncodedStringMap(mapOf("a" to "1"))
    service.formUrlEncodedStringMap(mapOf("a" to "1", "b" to "2"))

    assertHttpLogMatches(
      {
        hasUrl("https://formUrlEncoded/post")
        hasRequestBody(
          ByteArray(0),
          ContentType.Application.FormUrlEncoded.withCharset(Charsets.UTF_8)
        )
      },
      {
        hasUrl("https://formUrlEncoded/post")
        hasRequestBody(
          "a=1",
          ContentType.Application.FormUrlEncoded.withCharset(Charsets.UTF_8)
        )
      },
      {
        hasUrl("https://formUrlEncoded/post")
        hasRequestBody(
          "a=1&b=2",
          ContentType.Application.FormUrlEncoded.withCharset(Charsets.UTF_8)
        )
      }
    )
  }

  @Test fun `@FormUrlEncoded @FieldMap of Any`() = runHttpTest {
    val service = HttpFormUrlEncodedTestService(BASE_URL, httpClient)

    service.formUrlEncodedAnyMap(emptyMap())
    service.formUrlEncodedAnyMap(
      mapOf(
        "a" to object : Any() {
          override fun toString(): String = "1"
        }
      )
    )
    service.formUrlEncodedAnyMap(
      mapOf(
        "a" to object : Any() {
          override fun toString(): String = "1"
        },
        "b" to object : Any() {
          override fun toString(): String = "2"
        }
      )
    )

    assertHttpLogMatches(
      {
        hasUrl("https://formUrlEncoded/post")
        hasRequestBody(
          ByteArray(0),
          ContentType.Application.FormUrlEncoded.withCharset(Charsets.UTF_8)
        )
      },
      {
        hasUrl("https://formUrlEncoded/post")
        hasRequestBody(
          "a=1",
          ContentType.Application.FormUrlEncoded.withCharset(Charsets.UTF_8)
        )
      },
      {
        hasUrl("https://formUrlEncoded/post")
        hasRequestBody(
          "a=1&b=2",
          ContentType.Application.FormUrlEncoded.withCharset(Charsets.UTF_8)
        )
      }
    )
  }

  @Test fun `@FormUrlEncoded @FieldMap of String Iterable`() = runHttpTest {
    val service = HttpFormUrlEncodedTestService(BASE_URL, httpClient)

    service.formUrlEncodedMapOfIterableString(emptyMap())
    service.formUrlEncodedMapOfIterableString(
      mapOf(
        "a" to listOf("1"),
        "b" to listOf("2", "3")
      )
    )

    assertHttpLogMatches(
      {
        hasUrl("https://formUrlEncoded/post")
        hasRequestBody(
          ByteArray(0),
          ContentType.Application.FormUrlEncoded.withCharset(Charsets.UTF_8)
        )
      },
      {
        hasUrl("https://formUrlEncoded/post")
        hasRequestBody(
          "a=1&b=2&b=3",
          ContentType.Application.FormUrlEncoded.withCharset(Charsets.UTF_8)
        )
      }
    )
  }

  @Test fun `@FormUrlEncoded @FieldMap of Any Iterable`() = runHttpTest {
    val service = HttpFormUrlEncodedTestService(BASE_URL, httpClient)

    service.formUrlEncodedMapOfIterableAny(emptyMap())
    service.formUrlEncodedMapOfIterableAny(
      mapOf(
        "a" to listOf(
          object : Any() {
            override fun toString() = "1"
          }
        ),
        "b" to listOf(
          object : Any() {
            override fun toString() = "2"
          },
          object : Any() {
            override fun toString() = "3"
          }
        )
      )
    )

    assertHttpLogMatches(
      {
        hasUrl("https://formUrlEncoded/post")
        hasRequestBody(
          ByteArray(0),
          ContentType.Application.FormUrlEncoded.withCharset(Charsets.UTF_8)
        )
      },
      {
        hasUrl("https://formUrlEncoded/post")
        hasRequestBody(
          "a=1&b=2&b=3",
          ContentType.Application.FormUrlEncoded.withCharset(Charsets.UTF_8)
        )
      }
    )
  }

  @Test fun `@FormUrlEncoded StringValues @FieldMap`() = runHttpTest {
    val service = HttpFormUrlEncodedTestService(BASE_URL, httpClient)

    service.formUrlEncodedStringValues(StringValues.Empty)
    service.formUrlEncodedStringValues(Parameters.Empty)
    service.formUrlEncodedStringValues(
      parametersOf(
        "a" to listOf("1"),
        "b" to listOf("2", "3")
      )
    )

    assertHttpLogMatches(
      {
        hasUrl("https://formUrlEncoded/post")
        hasRequestBody(
          ByteArray(0),
          ContentType.Application.FormUrlEncoded.withCharset(Charsets.UTF_8)
        )
      },
      {
        hasUrl("https://formUrlEncoded/post")
        hasRequestBody(
          ByteArray(0),
          ContentType.Application.FormUrlEncoded.withCharset(Charsets.UTF_8)
        )
      },
      {
        hasUrl("https://formUrlEncoded/post")
        hasRequestBody(
          "a=1&b=2&b=3",
          ContentType.Application.FormUrlEncoded.withCharset(Charsets.UTF_8)
        )
      }
    )
  }

  @Test fun `@FormUrlEncoded multiple parameters`() = runHttpTest {
    val service = HttpFormUrlEncodedTestService(BASE_URL, httpClient)

    service.formUrlEncodedMultipleParameters("a", "b", "c", "d")

    assertHttpLogMatches {
      hasUrl("https://formUrlEncoded/post")
      hasRequestBody(
        "f1=a&f1=b&f2=c&f2=d",
        ContentType.Application.FormUrlEncoded.withCharset(Charsets.UTF_8)
      )
    }
  }

  @Test fun `@FormUrlEncoded nullable parameters`() = runHttpTest {
    val service = HttpFormUrlEncodedTestService(BASE_URL, httpClient)

    service.formUrlEncodedNullableTypes(null, null, null, null, null, null)
    service.formUrlEncodedNullableTypes(
      "a",
      "b",
      listOf("c", "d"),
      parametersOf("f1" to listOf("e", "f")),
      mapOf("f2" to listOf("g")),
      mapOf("f3" to setOf("h", "i"))
    )

    assertHttpLogMatches(
      {
        hasUrl("https://formUrlEncoded/post")
        hasRequestBody(
          ByteArray(0),
          ContentType.Application.FormUrlEncoded.withCharset(Charsets.UTF_8)
        )
      },
      {
        hasUrl("https://formUrlEncoded/post")
        hasRequestBody(
          "f1=a&f1=e&f1=f&f2=b&f2=g&f3=c&f3=d&f3=h&f3=i",
          ContentType.Application.FormUrlEncoded.withCharset(Charsets.UTF_8)
        )
      }
    )
  }

  @Test fun `@FormUrlEncoded iterables with null values`() = runHttpTest {
    val service = HttpFormUrlEncodedTestService(BASE_URL, httpClient)

    service.formUrlEncodedNullableTypes(
      null,
      null,
      listOf("a", null, "b"),
      null,
      mapOf("f2" to listOf(null, "c")),
      mapOf("f3" to setOf("d", null))
    )

    assertHttpLogMatches {
      hasUrl("https://formUrlEncoded/post")
      hasRequestBody(
        "f3=a&f3=b&f3=d&f2=c",
        ContentType.Application.FormUrlEncoded.withCharset(Charsets.UTF_8)
      )
    }
  }
}
