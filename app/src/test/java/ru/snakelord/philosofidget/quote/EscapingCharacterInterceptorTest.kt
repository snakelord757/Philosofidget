package ru.snakelord.philosofidget.quote

import okhttp3.OkHttpClient
import okhttp3.Protocol
import okhttp3.Request
import okhttp3.Response
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.Assert.*
import org.junit.Test
import ru.snakelord.philosofidget.domain.interceptor.EscapingCharacterInterceptor
import java.io.IOException

class EscapingCharacterInterceptorTest {
    @Test fun `valid JSON and its legitimate escapes remain unchanged`() {
        val json = """{"quoteText":"Line\n\"quoted\" \\ path and don't","quoteAuthor":"Автор"}"""
        response(json).use { assertEquals(json, it.body.string()) }
    }

    @Test fun `invalid apostrophe escape is corrected without losing the apostrophe`() {
        val json = """{"quoteText":"Don\'t lose O\'Brien","quoteAuthor":"Author"}"""
        response(json).use {
            assertEquals("""{"quoteText":"Don't lose O'Brien","quoteAuthor":"Author"}""", it.body.string())
        }
    }

    @Test fun `response preserves status protocol and request`() {
        response("{}", 503, "Unavailable").use {
            assertEquals(503, it.code)
            assertEquals("Unavailable", it.message)
            assertEquals(Protocol.HTTP_1_1, it.protocol)
            assertEquals("https://example.invalid/quote", it.request.url.toString())
            val contentType = requireNotNull(it.body.contentType())
            assertEquals("application", contentType.type)
            assertEquals("json", contentType.subtype)
            assertEquals(Charsets.UTF_8, contentType.charset())
        }
    }

    @Test fun `response preserves cache and diagnostic headers`() {
        response("{}").use {
            assertEquals("max-age=60", it.header("Cache-Control"))
            assertEquals("request-123", it.header("X-Request-Id"))
        }
    }

    @Test fun `empty body is preserved`() {
        response("").use { assertEquals("", it.body.string()) }
    }

    @Test fun `transport failure is propagated unchanged`() {
        val failure = IOException("offline")
        val client = OkHttpClient.Builder()
            .addInterceptor(EscapingCharacterInterceptor())
            .addInterceptor { throw failure }
            .build()
        assertSame(failure, runCatching { client.newCall(request()).execute() }.exceptionOrNull())
    }

    // Terminal application interceptor returns an in-memory response; no DNS or network is used.
    private fun response(body: String, code: Int = 200, message: String = "OK"): Response =
        OkHttpClient.Builder()
            .addInterceptor(EscapingCharacterInterceptor())
            .addInterceptor { chain ->
                Response.Builder()
                    .request(chain.request())
                    .protocol(Protocol.HTTP_1_1)
                    .code(code)
                    .message(message)
                    .header("Cache-Control", "max-age=60")
                    .header("X-Request-Id", "request-123")
                    .body(body.toResponseBody())
                    .build()
            }
            .build()
            .newCall(request())
            .execute()

    private fun request(): Request = Request.Builder().url("https://example.invalid/quote").build()
}
