package com.example.smartassist

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Create
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.smartassist.viewmodel.MainViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            SmartAssistTheme {
                SmartAssistApp()
            }
        }
    }
}

sealed class Screen(val route: String, val label: String, val icon: ImageVector) {
    object Summarize : Screen("summarize", "요약", Icons.Default.List)
    object Translate : Screen("translate", "번역", Icons.Default.Create)
    object Recipe : Screen("recipe", "레시피", Icons.Default.Star)
}

@Composable
fun SmartAssistApp() {
    val navController = rememberNavController()
    val viewModel: MainViewModel = viewModel()
    val screens = listOf(Screen.Summarize, Screen.Translate, Screen.Recipe)

    Scaffold(
        bottomBar = {
            NavigationBar {
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentRoute = navBackStackEntry?.destination?.route
                screens.forEach { screen ->
                    NavigationBarItem(
                        icon = { Icon(screen.icon, contentDescription = screen.label) },
                        label = { Text(screen.label) },
                        selected = currentRoute == screen.route,
                        onClick = {
                            navController.navigate(screen.route) {
                                popUpTo(navController.graph.startDestinationId) { saveState = true }
                                launchSingleTop = true
                                restoreState = true
                            }
                        }
                    )
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Screen.Summarize.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(Screen.Summarize.route) { SummarizeScreen(viewModel) }
            composable(Screen.Translate.route) { TranslateScreen(viewModel) }
            composable(Screen.Recipe.route) { RecipeScreen(viewModel) }
        }
    }
}

// ─── 공통 UI 컴포넌트 ───────────────────────────────────────────

@Composable
fun ResultCard(state: com.example.smartassist.viewmodel.UiState) {
    when {
        state.isLoading -> {
            Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(modifier = Modifier.padding(16.dp))
            }
        }
        state.error.isNotEmpty() -> {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer)
            ) {
                Text(
                    text = state.error,
                    modifier = Modifier.padding(16.dp),
                    color = MaterialTheme.colorScheme.onErrorContainer
                )
            }
        }
        state.result.isNotEmpty() -> {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer)
            ) {
                Text(
                    text = state.result,
                    modifier = Modifier.padding(16.dp),
                    color = MaterialTheme.colorScheme.onSecondaryContainer
                )
            }
        }
    }
}

// ─── 요약 화면 ──────────────────────────────────────────────────

@Composable
fun SummarizeScreen(viewModel: MainViewModel) {
    var inputText by remember { mutableStateOf("") }
    val state by viewModel.summarizeState.collectAsStateWithLifecycle()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text("📄 문서 요약", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)

        OutlinedTextField(
            value = inputText,
            onValueChange = { inputText = it },
            label = { Text("요약할 텍스트를 입력하세요") },
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp),
            maxLines = 10
        )

        Button(
            onClick = { if (inputText.isNotBlank()) viewModel.summarize(inputText) },
            modifier = Modifier.fillMaxWidth(),
            enabled = inputText.isNotBlank() && !state.isLoading
        ) {
            Text("요약하기")
        }

        ResultCard(state)
    }
}

// ─── 번역 화면 ──────────────────────────────────────────────────

@Composable
fun TranslateScreen(viewModel: MainViewModel) {
    var inputText by remember { mutableStateOf("") }
    var selectedLang by remember { mutableStateOf("영어") }
    val state by viewModel.translateState.collectAsStateWithLifecycle()
    val languages = listOf("영어", "일본어", "중국어", "스페인어", "프랑스어", "독일어")

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text("🌐 AI 번역", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)

        OutlinedTextField(
            value = inputText,
            onValueChange = { inputText = it },
            label = { Text("번역할 텍스트를 입력하세요") },
            modifier = Modifier
                .fillMaxWidth()
                .height(150.dp),
            maxLines = 6
        )

        Text("번역할 언어 선택", style = MaterialTheme.typography.labelLarge)
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
            languages.forEach { lang ->
                FilterChip(
                    selected = selectedLang == lang,
                    onClick = { selectedLang = lang },
                    label = { Text(lang) }
                )
            }
        }

        Button(
            onClick = { if (inputText.isNotBlank()) viewModel.translate(inputText, selectedLang) },
            modifier = Modifier.fillMaxWidth(),
            enabled = inputText.isNotBlank() && !state.isLoading
        ) {
            Text("번역하기")
        }

        ResultCard(state)
    }
}

// ─── 레시피 화면 ─────────────────────────────────────────────────

@Composable
fun RecipeScreen(viewModel: MainViewModel) {
    var ingredients by remember { mutableStateOf("") }
    val state by viewModel.recipeState.collectAsStateWithLifecycle()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text("🍽️ 냉장고 레시피", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)

        OutlinedTextField(
            value = ingredients,
            onValueChange = { ingredients = it },
            label = { Text("재료를 입력하세요 (예: 계란, 감자, 양파)") },
            modifier = Modifier
                .fillMaxWidth()
                .height(120.dp),
            maxLines = 4
        )

        Button(
            onClick = { if (ingredients.isNotBlank()) viewModel.getRecipe(ingredients) },
            modifier = Modifier.fillMaxWidth(),
            enabled = ingredients.isNotBlank() && !state.isLoading
        ) {
            Text("레시피 추천받기")
        }

        ResultCard(state)
    }
}

// ─── 테마 ────────────────────────────────────────────────────────

@Composable
fun SmartAssistTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = darkColorScheme(),
        content = content
    )
}