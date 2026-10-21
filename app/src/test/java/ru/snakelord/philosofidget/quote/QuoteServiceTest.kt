package ru.snakelord.philosofidget.quote

import kotlinx.coroutines.runBlocking
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

    private fun service(code: Int, requests: MutableList<Request>): QuoteService {
        val client = OkHttpClient.Builder().addInterceptor { chain ->
            requests += chain.request()
            Response.Builder()
                .request(chain.request())
                .protocol(Protocol.HTTP_1_1)
                .code(code)
                .message(if (code == 200) "OK" else "Unavailable")
                .body("""{"quoteText":"A thought","quoteAuthor":"Author","senderName":"","senderLink":"","quoteLink":""}""".toResponseBody("application/json".toMediaType()))
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
