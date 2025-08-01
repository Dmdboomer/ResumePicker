
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import java.io.File

import kotlinx.coroutines.delay


@Composable
fun CoverLetterPage(
    onNavigateToResumes: () -> Unit,
    onNavigateToConfig: () -> Unit
) {
    var jobPostingText by remember { mutableStateOf("") }
    var coverLetter by remember { mutableStateOf<String?>(null) }
    var isLoading by remember { mutableStateOf(false) }

    Column(modifier = Modifier.padding(16.dp)) {
        // Top Navigation
        Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
            Button(onClick = onNavigateToResumes) {
                Text("Resume Manager")
            }
            Button(onClick = onNavigateToConfig) {
                Text("Configure Profile")
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

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

        Spacer(modifier = Modifier.height(16.dp))

        // Generate Button
        Button(
            onClick = {
                isLoading = true
                // Ai api calls
                val selectedResumeId = AiClient.selectBestResume(jobPostingText, ResumeManager.getAllDescriptions())
                val publicText = ResumeManager.getPublicText(selectedResumeId)
                val coverLetter = AiClient.generateCoverLetter(jobPostingText, publicText)

                print(coverLetter)
                isLoading = false

            },
            enabled = jobPostingText.isNotBlank()
        ) {
            if (isLoading) CircularProgressIndicator(Modifier.size(16.dp))
            else Text("Generate Cover Letter")
        }

        // Display generated letter
        coverLetter?.let {
            Spacer(modifier = Modifier.height(16.dp))
            Text("Cover Letter:", style = MaterialTheme.typography.headlineSmall)
            Text(it)
        }
    }
}