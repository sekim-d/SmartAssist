package com.example.smartassist.network

import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST
import com.example.smartassist.BuildConfig

// ⚠️ 여기에 본인 API 키 입력
const val API_KEY = BuildConfig.CLAUDE_API_KEY

data class Message(val role: String, val content: String)

data class ClaudeRequest(
    val model: String = "claude-sonnet-4-20250514",
    val max_tokens: Int = 1024,
    val messages: List<Message>
)

data class ContentBlock(val type: String, val text: String)
data class ClaudeResponse(val content: List<ContentBlock>)

interface ClaudeApiService {
    @Headers(
        "Content-Type: application/json",
        "anthropic-version: 2023-06-01",
        "x-api-key: $API_KEY"
    )
    @POST("v1/messages")
    suspend fun sendMessage(@Body request: ClaudeRequest): ClaudeResponse
}
