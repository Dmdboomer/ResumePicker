import Config.email
import Config.name
import Config.parseConfig
import Config.phone
import Config.geminiThingXd
import Config.saveConfig
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import java.io.File


import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.platform.LocalFocusManager

import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.*
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.input.TransformedText

private val CONFIG_FILE = File("user_config.json")

class MaskedApiKeyTransformation : VisualTransformation {
    override fun filter(text: AnnotatedString): TransformedText {
        val maskedText = buildAnnotatedString {
            if (text.isNotEmpty()) {
                append(text.take(2)) // Show first 2 chars
                repeat(text.length - 2) { append('•') } // Mask remaining chars
            } else {
                append(text)
            }
        }

        return TransformedText(
            text = maskedText,
            offsetMapping = object : OffsetMapping {
                // 1:1 mapping between original and masked positions
                override fun originalToTransformed(offset: Int): Int =
                    if (offset <= maskedText.length) offset else maskedText.length

                override fun transformedToOriginal(offset: Int): Int =
                    if (offset <= text.length) offset else text.length
            }
        )
    }
}

@Composable
fun ConfigPage(onComplete: () -> Unit) {
    val json = CONFIG_FILE.readText()
    val config = parseConfig(json).also {
        name = it.name
        email = it.email
        phone = it.phone
        geminiThingXd = it.geminiThingXd
    }
    var name by remember { mutableStateOf(name ) }
    var email by remember { mutableStateOf(email ) }
    var phone by remember { mutableStateOf(phone) }
    var geminiThingXd by remember { mutableStateOf(geminiThingXd) }

    var isFocused by remember { mutableStateOf(false) }
    val focusManager = LocalFocusManager.current

    Scaffold(
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(24.dp)
        ) {
            Text("Update your personal information", style = MaterialTheme.typography.titleLarge)

            Spacer(Modifier.height(16.dp))

            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Full Name") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(8.dp))

            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Email") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(8.dp))

            OutlinedTextField(
                value = phone,
                onValueChange = { phone = it },
                label = { Text("Phone") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(24.dp))

            OutlinedTextField(
                value = geminiThingXd,
                onValueChange = { geminiThingXd = it },
                label = { Text("A+P^I!K_E-Y") },
                modifier = Modifier
                    .fillMaxWidth()
                    .onFocusChanged { focusState ->
                        isFocused = focusState.isFocused
                    },
                visualTransformation = if (isFocused) PasswordVisualTransformation() else MaskedApiKeyTransformation(),
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                keyboardActions = KeyboardActions(onDone = { focusManager.clearFocus() })
            )

            Spacer(Modifier.height(24.dp))

            Button(
                onClick = {
                    val config = UserConfig(name, email, phone, geminiThingXd).also {
                        Config.name = it.name
                        Config.email = it.email
                        Config.phone = it.phone
                        geminiThingXd = it.geminiThingXd
                    }
                    saveConfig(config)
                    onComplete()
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Save Configuration")
            }
        }
    }
}
