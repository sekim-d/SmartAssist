# 🤖 SmartAssist — AI-Powered Android Application

> Anthropic Claude API를 연동한 Android 앱 포트폴리오 프로젝트  
> LLM 기반의 문서 요약 · 다국어 번역 · 레시피 추천 기능을 하나의 앱으로 구현

---

## 📱 주요 기능

| 기능 | 설명 |
|------|------|
| 📄 **문서 요약** | 긴 텍스트를 입력하면 AI가 핵심 내용만 3~5줄로 요약 |
| 🌐 **다국어 번역** | 영어, 일본어, 중국어 등 6개 언어로 자연스러운 번역 |
| 🍽️ **레시피 추천** | 보유한 식재료를 입력하면 AI가 만들 수 있는 요리 추천 |

---

## 🛠 기술 스택

```
Language       Kotlin
UI Framework   Jetpack Compose + Material3
Architecture   MVVM (ViewModel + StateFlow)
Network        Retrofit2 + OkHttp3
AI Engine      Anthropic Claude API (claude-sonnet-4-20250514)
Async          Kotlin Coroutines + Flow
Min SDK        API 26 (Android 8.0)
```

---

## 🏗 프로젝트 구조

```
com.example.smartassist/
├── network/
│   └── ClaudeApiService.kt       # Retrofit 인터페이스 및 데이터 모델
├── repository/
│   └── ClaudeRepository.kt       # API 호출 및 프롬프트 관리
├── viewmodel/
│   └── MainViewModel.kt          # UI 상태 관리 (UiState)
└── MainActivity.kt               # UI 진입점, 화면 구성 (Compose)
```

---

## ⚙️ 아키텍처

```
[ UI Layer ]         [ Domain Layer ]       [ Data Layer ]
MainActivity    →    MainViewModel     →    ClaudeRepository
(Compose)            (StateFlow)             (Retrofit)
                                                  ↓
                                         Anthropic Claude API
```

MVVM 패턴을 적용하여 UI와 비즈니스 로직을 분리했습니다.  
`UiState` 데이터 클래스로 로딩/성공/에러 상태를 일원화하여 관리합니다.

---

## 🔌 Claude API 연동 방식

모든 AI 기능은 `ClaudeRepository` 단일 클래스에서 관리하며,  
기능별로 **프롬프트만 다르게** 구성하여 확장성을 높였습니다.

```kotlin
// 공통 API 호출 함수
private suspend fun callClaude(prompt: String): String {
    val request = ClaudeRequest(
        model = "claude-sonnet-4-20250514",
        messages = listOf(Message("user", prompt))
    )
    return api.sendMessage(request).content.firstOrNull()?.text ?: "응답 없음"
}

// 기능별 프롬프트 분리
suspend fun summarize(text: String)   = callClaude("다음 텍스트를 요약해줘: $text")
suspend fun translate(text: String)   = callClaude("$targetLang 로 번역해줘: $text")
suspend fun getRecipe(ingredients: String) = callClaude("재료: $ingredients 로 레시피 추천해줘")
```

---

## 📦 설치 및 실행

### 1. 프로젝트 클론
```bash
git clone https://github.com/본인계정/SmartAssist.git
cd SmartAssist
```

### 2. API 키 설정
`app/src/main/java/com/example/smartassist/network/ClaudeApiService.kt` 파일에서:
```kotlin
const val API_KEY = "여기에_Anthropic_API_키_입력"
```

> ⚠️ **주의**: API 키를 Git에 커밋하지 마세요. 실제 배포 시 `local.properties` 또는 환경 변수로 관리하세요.

### 3. 빌드 및 실행
```bash
./gradlew assembleDebug
```
또는 Android Studio에서 ▶ 버튼 클릭

---

## 📋 요구사항

- Android Studio Hedgehog 이상
- JDK 17 이상
- Android SDK API 26+
- [Anthropic API 키](https://console.anthropic.com) (유료, 사용량 기반 과금)

---

## 🔑 의존성

```kotlin
// Jetpack Compose
implementation(platform("androidx.compose:compose-bom:2024.02.00"))
implementation("androidx.compose.material3:material3")
implementation("androidx.navigation:navigation-compose:2.7.6")

// ViewModel
implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.7.0")

// Retrofit
implementation("com.squareup.retrofit2:retrofit:2.9.0")
implementation("com.squareup.retrofit2:converter-gson:2.9.0")
implementation("com.squareup.okhttp3:logging-interceptor:4.12.0")

// Coroutines
implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.3")
```

---

## 🚀 향후 개선 사항

- [ ] API 키 보안 강화 (`local.properties` 분리)
- [ ] 대화 히스토리 저장 (Room DB)
- [ ] 스트리밍 응답 지원 (실시간 타이핑 효과)
- [ ] 이미지 입력 지원 (사진 찍어서 레시피 추천)
- [ ] 다크/라이트 테마 전환

---

## 📄 라이선스

```
MIT License
Copyright (c) 2025
```

---

## 👤 개발자

| 항목 | 내용 |
|------|----|
| 이름 | 김성은 |
| 이메일 | cksdid0907@naver.com |
| GitHub | github.com/본인계정 |

---

> 이 프로젝트는 LLM API 연동 경험을 쌓기 위해 개인적으로 개발한 포트폴리오 프로젝트입니다.
