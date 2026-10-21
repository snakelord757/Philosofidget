package ru.snakelord.philosofidget.quote

import kotlinx.coroutines.runBlocking
import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Protocol
import okhttp3.Request
import okhttp3.Response
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.Assert.*
import org.junit.Test
import retrofit2.HttpException
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory
import ru.snakelord.philosofidget.data.datasource.quote.network.QuoteService
import java.io.IOException

class QuoteServiceTest {
    @Test fun `Retrofit builds correct GET URL and decodes JSON response`() = runBlocking {
        val requests = mutableListOf<Request>()
        val service = service(200, requests)
        val quote = service.getQuote("getQuote", "json", 123, "en")
        assertEquals("A thought", quote.quoteText)
        assertEquals("Author", quote.quoteAuthor)
        assertEquals(1, requests.size)
        val request = requests.single()
        assertEquals("GET", request.method)
        assertEquals("/api/1.0/", request.url.encodedPath)
        assertEquals(setOf("method", "format", "key", "lang"), request.url.queryParameterNames)
        assertEquals("getQuote", request.url.queryParameter("method"))
        assertEquals("json", request.url.queryParameter("format"))
        assertEquals("123", request.url.queryParameter("key"))
        assertEquals("en", request.url.queryParameter("lang"))
    }

    @Test fun `non successful HTTP status is surfaced as HTTP exception`() = runBlocking {
        val error = runCatching { service(503, mutableListOf()).getQuote("getQuote", "json", 0, "ru") }.exceptionOrNull()
        assertTrue(error is HttpException)
        assertEquals(503, (error as HttpException).code())
    }

    @Test fun `successful HTTP response with malformed or incomplete JSON fails conversion`() = runBlocking {
        for (body in listOf("not json", "{}", "", """{"quoteText":null,"quoteAuthor":"","senderName":"","senderLink":"","quoteLink":""}""")) {
            val requests = mutableListOf<Request>()
            val error = runCatching { service(200, requests, body).getQuote("getQuote", "json", 0, "ru") }.exceptionOrNull()
            assertTrue("Expected a conversion error for body: $body, got $error", error is SerializationException)
            assertEquals(1, requests.size)
        }
    }

    @Test fun `transport error reaches suspend service caller with its original details`() = runBlocking {
        val failure = IOException("offline")
        val requests = mutableListOf<Request>()
        val error = runCatching {
            service(200, requests, failure = failure).getQuote("getQuote", "json", 0, "ru")
        }.exceptionOrNull()
        assertTrue(error is IOException)
        assertEquals(failure.message, error?.message)
        // Coroutine stacktrace recovery may wrap the original exception in a copy.
        assertSame(failure, generateSequence(error) { it.cause }.last())
        assertEquals(1, requests.size)
    }

    private fun service(
        code: Int,
        requests: MutableList<Request>,
        body: String = """{"quoteText":"A thought","quoteAuthor":"Author","senderName":"","senderLink":"","quoteLink":""}""",
        failure: IOException? = null,
    ): QuoteService {
        val client = OkHttpClient.Builder().addInterceptor { chain ->
            requests += chain.request()
            failure?.let { throw it }
            Response.Builder()
                .request(chain.request())
                .protocol(Protocol.HTTP_1_1)
                .code(code)
                .message(if (code == 200) "OK" else "Unavailable")
                .body(body.toResponseBody("application/json".toMediaType()))
                .build()
        }.build()
        return Retrofit.Builder()
            .baseUrl("https://example.invalid/api/")
            .client(client)
            .addConverterFactory(Json.asConverterFactory("application/json".toMediaType()))
            .build()
            .create(QuoteService::class.java)
    }
}
