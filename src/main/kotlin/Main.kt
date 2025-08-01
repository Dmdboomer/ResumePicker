import androidx.compose.runtime.*
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.window.singleWindowApplication

sealed class Screen {
    object CoverLetter : Screen()
    object ResumeManager : Screen()
    object PersonalConfig : Screen()
}

@OptIn(ExperimentalComposeUiApi::class)
fun main() = singleWindowApplication(title = "Cover Letter Generator") {
    System.setProperty("apple.awt.tsm.ignore", "true")
    var currentScreen by remember { mutableStateOf<Screen>(Screen.CoverLetter) }
    var configLoaded by remember { mutableStateOf(false) }
    var showSplash by remember { mutableStateOf(true) }

    // Initialize config in background

    LaunchedEffect(Unit) {
        configLoaded = Config.loadConfig()
        showSplash = false
    }


    if (showSplash) {
        // Show splash/loading screen while initializing
        LoadingScreen()
    } else if (!configLoaded) {
        ConfigPage(
            onComplete = {
                configLoaded = true
                currentScreen = Screen.CoverLetter
            }
        )
    } else {
        when (currentScreen) {
            is Screen.CoverLetter -> CoverLetterPage(
                onNavigateToResumes = { currentScreen = Screen.ResumeManager },
                onNavigateToConfig = { currentScreen = Screen.PersonalConfig }
            )

            is Screen.ResumeManager -> ResumeManagerPage(
                onBack = { currentScreen = Screen.CoverLetter },
                onNavigateToConfig = { currentScreen = Screen.PersonalConfig }
            )

            is Screen.PersonalConfig -> ConfigPage(
                onComplete = {
                    // After saving config, return to previous screen
                    currentScreen = Screen.CoverLetter
                }
            )
        }
    }
}

@Composable
fun LoadingScreen() { /* Loading indicator UI */ }
