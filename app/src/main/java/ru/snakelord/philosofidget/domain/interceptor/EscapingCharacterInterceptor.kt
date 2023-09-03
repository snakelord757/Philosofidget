package ru.snakelord.philosofidget.domain.interceptor

import okhttp3.Interceptor
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.Response
import okhttp3.ResponseBody.Companion.toResponseBody

class EscapingCharacterInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        val response = chain.proceed(request)
        val body = response.body?.string() ?: error("Unable to process empty response")
        return Response.Builder()
            .body(body.replace(ESCAPING_CHAR, EMPTY).toResponseBody(contentType = CONTENT_TYPE.toMediaType()))
            .request(request)
            .code(response.code)
            .protocol(response.protocol)
            .message(response.message)
            .build()
    }

    private companion object {
        const val CONTENT_TYPE = "application/json"
        const val ESCAPING_CHAR = "\\\'"
        const val EMPTY = ""
    }
}