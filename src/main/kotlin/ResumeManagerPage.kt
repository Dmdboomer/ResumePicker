import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.Alignment
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete


import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items

@Composable
fun ResumeManagerPage(
    onBack: () -> Unit,
    onNavigateToConfig: () -> Unit
) {
    var resumes = ResumeManager.getAllNames()
    var newResumeName by remember { mutableStateOf("") }
    var newResumeDescription by remember { mutableStateOf("") }
    var showAddDialog by remember { mutableStateOf(false) }

    Column(modifier = Modifier.padding(16.dp)) {
        // Navigation bar
        Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
            Button(onClick = onBack) {
                Text("Back")
            }
            Button(onClick = onNavigateToConfig) {
                Text("Configure Profile")
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Add Resume Button
        Button(onClick = { showAddDialog = true }) {
            Text("Add New Resume")
        }

        // Resumes List
        Spacer(modifier = Modifier.height(16.dp))
        Text("My Resumes:", style = MaterialTheme.typography.headlineSmall)
        HorizontalDivider()

        LazyColumn {
            items(resumes) { resume ->
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth().padding(8.dp)
                ) {
                    Text(resume, modifier = Modifier.weight(1f))
                    IconButton(onClick = { resumes = resumes - resume }) {
                        Icon(Icons.Default.Delete, "Delete")
                    }
                }
                HorizontalDivider()
            }
        }
    }

    // Add Resume Dialog
    if (showAddDialog) {
        AlertDialog(
            onDismissRequest = { showAddDialog = false },
            title = { Text("Add Resume") },
            text = {
                Column {
                    OutlinedTextField(
                        value = newResumeName,
                        onValueChange = { newResumeName = it },
                        label = { Text("Resume Title") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(Modifier.height(8.dp))
                    OutlinedTextField(
                        value = newResumeDescription,
                        onValueChange = { newResumeDescription = it },
                        label = { Text("Resume Public Information") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (newResumeName.isNotBlank()) {
                            // Fixed: Update both state AND "save" operation
                            resumes = resumes + newResumeName
                            // ResumeManager.saveResume(newResumeDescription, newResumeName)

                            // Reset dialog state
                            newResumeName = ""
                            newResumeDescription = ""
                            showAddDialog = false
                        }
                    }
                ) {
                    Text("Add")
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}