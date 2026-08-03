package com.dbg.mdm_offline_client.api

import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.http.isSuccess

class MdmApi {
    suspend fun fetchStatus(baseUrl: String): StatusResponse {
        val client = createHttpClient()
        try {
            val response = client.get(baseUrl.trimEnd('/') + "/status")
            if (!response.status.isSuccess()) {
                throw MdmApiException("HTTP ${response.status.value}")
            }
            return response.body()
        } finally {
            client.close()
        }
    }

    suspend fun register(baseUrl: String, request: RegisterRequest): RegisterResponse {
        val client = createHttpClient()
        try {
            val response = client.post(baseUrl.trimEnd('/') + "/register") {
                contentType(ContentType.Application.Json)
                setBody(request)
            }
            val body = runCatching { response.body<RegisterResponse>() }.getOrElse {
                throw MdmApiException(response.bodyAsText().ifBlank { "HTTP ${response.status.value}" })
            }
            if (!response.status.isSuccess() || !body.accepted) {
                throw MdmRegisterRejectedException(body.message.ifBlank { "rejected" })
            }
            return body
        } finally {
            client.close()
        }
    }
}

class MdmApiException(message: String) : Exception(message)

class MdmRegisterRejectedException(message: String) : Exception(message)
