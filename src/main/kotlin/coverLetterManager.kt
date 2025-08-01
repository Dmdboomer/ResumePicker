import java.io.File
import kotlin.toString
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

object coverLetterManager {
    private val storageDir = File("coverLetters/").apply { mkdir() }
    private val gson = Gson()

    data class coverLetterManager(
        val id: Int,
        val name: String,
        val publicText: String,
        val description: String,
    )

    fun getCoverLetterName(resumeId: Int): Int {
        val prefix = "cl_${resumeId}_"
        val usedIds = mutableSetOf<Int>()

        // Scan existing files matching the pattern
        storageDir.listFiles()?.forEach { file ->
            val baseName = file.nameWithoutExtension
            if (baseName.startsWith(prefix)) {
                val idPart = baseName.substring(prefix.length)
                if (idPart.matches(Regex("\\d+"))) {
                    usedIds.add(idPart.toInt())
                }
            }
        }

        // Find the smallest available ID starting from 1
        var newId = 1
        while (newId in usedIds) {
            newId++
        }

        return newId
    }

}