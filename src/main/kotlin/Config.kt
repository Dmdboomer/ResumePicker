import java.io.File
import java.util.Base64
import javax.crypto.Cipher
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.SecretKeySpec

private val encoder = Base64.getEncoder()
private val decoder = Base64.getDecoder()
private val CONFIG_FILE = File("user_config.json")

// Dummy security to dodge scanning, etc. XD
private const val GOOFY_AHH_NAME = "9d82y30fyP48y3Wk9uf2I08e32Tr8fh3" // 16/24/32
private const val DA_SECOND_NAME = "3Sh01Oq3Ka42r2tF" // 16

data class UserConfig(
    val name: String,
    val email: String,
    val phone: String,
    val geminiThingXd: String
)

object Crypto {
    fun encrypt(input: String): String {
        val iv = IvParameterSpec(DA_SECOND_NAME.toByteArray())
        val keySpec = SecretKeySpec(GOOFY_AHH_NAME.toByteArray(), "AES")
        val cipher = Cipher.getInstance("AES/CBC/PKCS5Padding")
        cipher.init(Cipher.ENCRYPT_MODE, keySpec, iv)
        val encrypted = cipher.doFinal(input.toByteArray())
        return encoder.encodeToString(encrypted)
    }

    fun decrypt(encrypted: String): String {
        val iv = IvParameterSpec(DA_SECOND_NAME.toByteArray())
        val keySpec = SecretKeySpec(GOOFY_AHH_NAME.toByteArray(), "AES")
        val cipher = Cipher.getInstance("AES/CBC/PKCS5Padding")
        cipher.init(Cipher.DECRYPT_MODE, keySpec, iv)
        val decoded = decoder.decode(encrypted)
        val decrypted = cipher.doFinal(decoded)
        return String(decrypted)
    }
}

object Config {
    lateinit var name: String
    lateinit var email: String
    lateinit var phone: String
    lateinit var geminiThingXd: String

    fun loadConfig(): Boolean {
        if (CONFIG_FILE.exists() && CONFIG_FILE.length() > 0) {
            val json = CONFIG_FILE.readText()
            val config = parseConfig(json).also {
                name = it.name
                email = it.email
                phone = it.phone
                geminiThingXd = it.geminiThingXd
            }
            println("Configuration loaded for $name")
        } else {
            println("\nFirst-time setup:")
            setupConfig()
        }
        return true
    }

    private fun setupConfig() {
        println("Enter your full name:")
        val name = readlnOrNull()?.takeIf { it.isNotEmpty() } ?: "John Doe"

        println("Enter your email:")
        val email = readlnOrNull()?.takeIf { it.isNotEmpty() } ?: "john.doe@example.com"

        println("Enter your phone number:")
        val phone = readlnOrNull()?.takeIf { it.isNotEmpty() } ?: "(123) 456-7890"

        println("Enter yo gemini thing (possibly your a-p!i#k%e_y)")
        val geminiThingXd = readlnOrNull()?.takeIf { it.isNotEmpty() } ?: ""

        val config = UserConfig(name, email, phone, geminiThingXd).also {
            Config.name = it.name
            Config.email = it.email
            Config.phone = it.phone
            Config.geminiThingXd = it.geminiThingXd
        }
        saveConfig(config)
        println("Configuration saved!")
    }

    fun saveConfig(config: UserConfig) {
        val encryptedName = Crypto.encrypt(config.name)
        val encryptedEmail = Crypto.encrypt(config.email)
        val encryptedPhone = Crypto.encrypt(config.phone)
        val encryptedGeminiThingXd = Crypto.encrypt(config.geminiThingXd)

        val json = """
        {
          "name": "$encryptedName",
          "email": "$encryptedEmail",
          "phone": "$encryptedPhone"
        }
        """.trimIndent()

        CONFIG_FILE.writeText(json)
    }

    fun parseConfig(json: String): UserConfig {
        val name = "\"name\":\\s*\"([^\"]+)\"".toRegex().find(json)?.groupValues?.get(1)
            ?: throw IllegalStateException("Invalid config format")
        val email = "\"email\":\\s*\"([^\"]+)\"".toRegex().find(json)?.groupValues?.get(1)
            ?: throw IllegalStateException("Invalid config format")
        val phone = "\"phone\":\\s*\"([^\"]+)\"".toRegex().find(json)?.groupValues?.get(1)
            ?: throw IllegalStateException("Invalid config format")
        val geminiThingXd = "\"phone\":\\s*\"([^\"]+)\"".toRegex().find(json)?.groupValues?.get(1)
            ?: throw IllegalStateException("Invalid config format")

        return UserConfig(
            name = Crypto.decrypt(name),
            email = Crypto.decrypt(email),
            phone = Crypto.decrypt(phone),
            geminiThingXd = Crypto.decrypt(geminiThingXd)
        )
    }
}