import com.google.gson.Gson
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import io.github.cdimascio.dotenv.dotenv
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response
import java.io.IOException
import java.util.concurrent.TimeUnit

object AiClient {
    private const val GEMINI_URL = "https://generativelanguage.googleapis.com/v1beta/models/gemini-2.0-flash:generateContent"
    private val dotenv = dotenv()
    private val GEMINI_API_KEY = dotenv["GEMINI_API_KEY"] ?: error("GEMINI_API_KEY missing in .env file")
    private val JSON_MEDIA_TYPE = "application/json; charset=utf-8".toMediaType()
    private val client = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)
        .build()

    private val gson = Gson()


    fun summarizeResume(resume: String): String{
        println("Started Summary")
        val prompt = """
            Summarize this resume with by going to each experience/section and 
            listing which skills, tools, experience they used in a format that is easy to compare later.
            Make sure that you seperate your talking, and response with a ||:
            $resume
        """.trimIndent()
        val response = callGemini(prompt, 1000, temperature = 0.1)
        println("Summary Complete")
        return response
    }

    fun selectBestResume(jobPosting: String, descriptions: List<String>): Int {
        require(descriptions.isNotEmpty()) { "Descriptions list cannot be empty" }
        println("Begin selection")
        val prompt = """
            Your output should ONLY consist of a single integer (no other text) which is the 0-based index 
            of the best resume for this job description. Based on these resume descriptions:
            
            ${descriptions.mapIndexed { i, desc -> "$i. $desc" }.joinToString("\n")}
            
            Job Posting:
            $jobPosting
        """.trimIndent()

        val response = callGemini(prompt, 5, 0.1)
        println("Selected Resume: + $response")
        return response.toIntOrNull()?.coerceIn(0 until descriptions.size) ?: 0
    }

    fun generateCoverLetter(jobPosting: String, publicResumeText: String): String {
        val prompt = """
            Based on this resume: ${publicResumeText.take(5000)},  
            generate a cover letter for this job posting: ${jobPosting.take(3000)}.
            Use placeholders: [name], [phone_number], [email]. Do not use any other placeholders except these 3
            Start with 'Cover letter:' and use \\n for new lines.
            Keep it under 300 words.
        """.trimIndent()

        return callGemini(prompt, 600, 0.7)
    }

    private fun callGemini(prompt: String, maxTokens: Int, temperature: Double): String {
        // Build Gemini request structure
        val content = JsonObject().apply {
            val partsArray = JsonArray().apply {
                add(JsonObject().apply {
                    addProperty("text", prompt)
                })
            }
            add("parts", partsArray)
        }

        val contentsArray = JsonArray().apply {
            add(content)
        }

        val generationConfig = JsonObject().apply {
            addProperty("maxOutputTokens", maxTokens)
            addProperty("temperature", temperature)
        }

        val requestBody = JsonObject().apply {
            add("contents", contentsArray)
            add("generationConfig", generationConfig)
        }

        val request = Request.Builder()
            .url(GEMINI_URL)
            .addHeader("x-goog-api-key", GEMINI_API_KEY)
            .addHeader("Content-Type", "application/json")
            .post(requestBody.toString().toRequestBody(JSON_MEDIA_TYPE))
            .build()

        client.newCall(request).execute().use { response ->
            if (!response.isSuccessful) {
                val errorBody = response.body?.string() ?: ""
                throw IOException("API request failed (${response.code}): ${response.message}\n$errorBody")
            }

            val responseBody = response.body?.string() ?: throw IOException("Empty response body")
            val parsed = gson.fromJson(responseBody, GeminiResponse::class.java)

            // Handle safety filters
            parsed.candidates?.firstOrNull()?.content?.parts?.firstOrNull()?.text
                ?.trim()
                ?.let { return it }

            throw IOException("No valid text generated. Safety filters may have blocked the response. $responseBody")
        }
    }

    fun generateHaiku(): String {
        val prompt = """
        Write me a beautiful haiku about nature.
        Follow the traditional 5-7-5 syllable structure.
        Separate the lines with \n.
        Do not include any other text or numbering.
        """.trimIndent()

        return callGemini(prompt, 50, 0.9)
    }

    // Gemini response data classes
    private data class GeminiResponse(
        val candidates: List<GeminiCandidate>?
    )

    private data class GeminiCandidate(
        val content: GeminiContent
    )

    private data class GeminiContent(
        val parts: List<GeminiPart>
    )

    private data class GeminiPart(
        val text: String
    )
}