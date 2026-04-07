package com.example.smartassist.repository

import com.example.smartassist.network.API_KEY
import com.example.smartassist.network.ClaudeApiService
import com.example.smartassist.network.ClaudeRequest
import com.example.smartassist.network.Message
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class ClaudeRepository {

    private val api: ClaudeApiService by lazy {
        val logging = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }
        val client = OkHttpClient.Builder()
            .addInterceptor(logging)
            .build()

        Retrofit.Builder()
            .baseUrl("https://api.anthropic.com/")
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ClaudeApiService::class.java)
    }

    private suspend fun callClaude(prompt: String): String {
        val request = ClaudeRequest(
            messages = listOf(Message("user", prompt))
        )
        val response = api.sendMessage(request)
        return response.content.firstOrNull()?.text ?: "응답 없음"
    }

    suspend fun summarize(text: String): String {
        return callClaude(
            """
            다음 텍스트를 핵심 내용만 3~5줄로 요약해줘. 한국어로 답해줘.
            
            텍스트: $text
            """.trimIndent()
        )
    }

    suspend fun translate(text: String, targetLang: String): String {
        return callClaude(
            """
            다음 텍스트를 $targetLang 로 번역해줘. 번역문만 출력해줘.
            
            텍스트: $text
            """.trimIndent()
        )
    }

    suspend fun getRecipe(ingredients: String): String {
        return callClaude(
            """
            냉장고에 다음 재료들이 있어: $ingredients
            
            이 재료들로 만들 수 있는 요리 2~3가지를 추천해줘.
            각 요리마다 이름, 필요한 재료, 간단한 조리법을 알려줘. 한국어로 답해줘.
            """.trimIndent()
        )
    }
}
