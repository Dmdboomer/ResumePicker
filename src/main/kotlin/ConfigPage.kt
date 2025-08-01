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

private val CONFIG_FILE = File("user_config.json")


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
                value = geminiThingXd.substring(0,3)+ "...",
                onValueChange = { geminiThingXd = it },
                label = { Text("A+P^I!K_E-Y") },
                modifier = Modifier.fillMaxWidth()
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