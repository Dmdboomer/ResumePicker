
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import java.io.File

import kotlinx.coroutines.delay
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CoverLetterPage(
    onNavigateToResumes: () -> Unit,
    onNavigateToConfig: () -> Unit
) {
    var jobPostingText by remember { mutableStateOf("") }
    var coverLetter by remember { mutableStateOf<String?>(null) }
    var isLoading by remember { mutableStateOf(false) }

    // Resume selection state
    var resumeOptions by remember { mutableStateOf(emptyList<Pair<Int, String>>()) }
    var expanded by remember { mutableStateOf(false) }
    var selectedResume by remember {
        mutableStateOf<Pair<Int, String>?>(null) // null = auto-select
    }

    // Create coroutine scope tied to composition lifecycle
    val coroutineScope = rememberCoroutineScope()

    // Load resume names asynchronously
    LaunchedEffect(Unit) {
        resumeOptions = withContext(Dispatchers.IO) {
            ResumeManager.getAllNameIds()
        }
    }

    Column(modifier = Modifier.padding(16.dp)) {
        // Top Navigation
        Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
            Button(onClick = onNavigateToResumes) { Text("Resume Manager") }
            Button(onClick = onNavigateToConfig) { Text("Configure Profile") }
        }

        Spacer(Modifier.height(16.dp))

        // Resume Selection Dropdown
        Text("Select Resume:", style = MaterialTheme.typography.bodyLarge)
        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = { expanded = it }
        ) {
            TextField(
                modifier = Modifier.menuAnchor(),
                readOnly = true,
                value = selectedResume?.second ?: "Auto-Select (Recommended)",
                onValueChange = {},
                trailingIcon = {
                    ExposedDropdownMenuDefaults.TrailingIcon(expanded)
                },
                colors = ExposedDropdownMenuDefaults.textFieldColors()
            )
            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                // Auto-select option
                DropdownMenuItem(
                    text = { Text("Auto-Select (Recommended)") },
                    onClick = {
                        selectedResume = null
                        expanded = false
                    }
                )
                // Resume options
                resumeOptions.forEach { option ->
                    DropdownMenuItem(
                        text = { Text(option.second) },
                        onClick = {
                            selectedResume = option
                            expanded = false
                        }
                    )
                }
            }
        }

        Spacer(Modifier.height(16.dp))

        // Job Posting Input
        Text("Job Description:", style = MaterialTheme.typography.headlineSmall)
        OutlinedTextField(
            value = jobPostingText,
            onValueChange = { jobPostingText = it },
            modifier = Modifier
                .fillMaxWidth()
                .height(150.dp),
            placeholder = { Text("Paste job posting here...") }
        )

        Spacer(Modifier.height(16.dp))

        // Generate Button - Fixed with proper coroutine handling
        Button(
            onClick = {
                if (jobPostingText.isBlank()) return@Button

                isLoading = true
                coverLetter = null

                coroutineScope.launch {
                    try {
                        val resumeId = selectedResume?.first
                            ?: AiClient.selectBestResume(jobPostingText, ResumeManager.getAllDescriptions())

                        val publicText = ResumeManager.getPublicText(resumeId)
                        val coverLetter = withContext(Dispatchers.IO) {
                            AiClient.generateCoverLetter(jobPostingText, publicText)
                        }
                        println(coverLetter)
                        // Save PDF to coverLetters/
                        val coverLetterId = coverLetterManager.getCoverLetterName(resumeId)
                        val pdfPath = "coverLetters/cl_${resumeId}_${coverLetterId}.pdf"
                        PdfGenerator.textToPdf(coverLetter, pdfPath)

                    } catch (e: Exception) {
                        coverLetter = "Error: ${e.message}"
                    } finally {
                        isLoading = false
                    }
                }
            },
            enabled = jobPostingText.isNotBlank() && !isLoading
        ) {
            if (isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(16.dp),
                    color = MaterialTheme.colorScheme.onPrimary
                )
            } else {
                Text("Generate Cover Letter")
            }
        }

        // Display generated letter
        coverLetter?.let {
            Spacer(Modifier.height(16.dp))
            Text("Cover Letter:", style = MaterialTheme.typography.headlineSmall)
            Text(it)
        }
    }
}