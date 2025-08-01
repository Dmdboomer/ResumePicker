plugins {
    kotlin("jvm") version "1.9.20"
    id("org.jetbrains.compose") version "1.6.0" // Update to latest version
    application
}

repositories {
    maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
    google()
    mavenCentral()
}

dependencies {
    implementation(compose.desktop.currentOs) // Main Compose dependency
    implementation(compose.material3) // For Material 3 components
    implementation(compose.materialIconsExtended) // For icons

    // Add these UI-related dependencies:
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-swing:1.8.1")
    implementation("com.squareup.okhttp3:okhttp:4.12.0")
    implementation("com.google.code.gson:gson:2.10.1")
    implementation("org.apache.pdfbox:pdfbox:3.0.4")
    implementation("io.github.cdimascio:dotenv-kotlin:6.4.1")
}

kotlin {
    jvmToolchain(17)
}

application {
    mainClass.set("MainKt")
}