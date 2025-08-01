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
    var resumes = ResumeManager.getAllNameIds()
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
            items(resumes) { (id, name) ->
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth().padding(8.dp)
                ) {
                    Text("$id: $name", modifier = Modifier.weight(1f))
                    IconButton(onClick = {
                        ResumeManager.deleteResume(id) // Implement this function
                        resumes = ResumeManager.getAllNameIds()
                    }) {
                        Icon(Icons.Default.Delete, "Delete")
                    }
                }
                HorizontalDivider()
            }
        }
    }

    // Add Resume Dialog
    if (showAddDialog) {
        var showNameError by remember { mutableStateOf(false) }
        var showDescriptionError by remember { mutableStateOf(false) }

        AlertDialog(
            onDismissRequest = { showAddDialog = false },
            title = { Text("Add Resume") },
            text = {
                Column {
                    OutlinedTextField(
                        value = newResumeName,
                        onValueChange = {
                            newResumeName = it
                            showNameError = false // Reset error on type
                        },
                        label = { Text("Resume Title") },
                        modifier = Modifier.fillMaxWidth(),
                        isError = showNameError,
                        supportingText = {
                            if (showNameError) {
                                Text("Title cannot be empty", color = MaterialTheme.colorScheme.error)
                            }
                        }
                    )
                    Spacer(Modifier.height(8.dp))
                    OutlinedTextField(
                        value = newResumeDescription,
                        onValueChange = {
                            newResumeDescription = it
                            showDescriptionError = false // Reset error on type
                        },
                        label = { Text("Resume Public Information") },
                        modifier = Modifier.fillMaxWidth(),
                        isError = showDescriptionError,
                        supportingText = {
                            if (showDescriptionError) {
                                Text("Description cannot be empty", color = MaterialTheme.colorScheme.error)
                            }
                        }
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val isNameEmpty = newResumeName.isBlank()
                        val isDescriptionEmpty = newResumeDescription.isBlank()

                        showNameError = isNameEmpty
                        showDescriptionError = isDescriptionEmpty

                        if (!isNameEmpty && !isDescriptionEmpty) {
                            ResumeManager.saveResume(newResumeDescription, newResumeName)
                            newResumeName = ""
                            newResumeDescription = ""
                            showAddDialog = false
                            resumes = ResumeManager.getAllNameIds() // Refresh the list
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