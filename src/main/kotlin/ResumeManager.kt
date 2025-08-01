import java.io.File
import kotlin.toString
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

object ResumeManager {
    private const val MAX_RESUMES = 7
    private val storageDir = File("resumes").apply { mkdir() }
    private val jsonFile = File(storageDir, "resumes.json")
    private val gson = Gson()
    private val resumeType = object : TypeToken<List<Resume>>() {}.type

    data class Resume(
        val id: Int,
        val name: String,
        val publicText: String,
        val description: String,
    )

    fun addResume() {
        if (countResumes() >= MAX_RESUMES) {
            println("Max number of resumes reached")
            return
        }

        println("Enter a personal name for this resume:")
        val name = readlnOrNull()?.takeIf { it.isNotBlank() } ?: run {
            println("Invalid name. Resume creation canceled.")
            return
        }

        println("Paste all resume information you're comfortable sharing (end with an empty line):")
        val publicText = buildString {
            while (true) {
                val line = readlnOrNull() ?: break
                if (line.isEmpty()) break
                append(line).append("\n")
            }
        }.trim()

        if (publicText.isEmpty()) {
            println("No resume text provided. Creation canceled.")
            return
        }
        saveResume(publicText, name)
    }

    // For non terminal use
    fun saveResume(publicText: String, name: String) {
        if (countResumes() >= MAX_RESUMES) {
            println("Max number of resumes reached")
            return
        }

        val description = AiClient.summarizeResume(publicText)
        val newId = generateNextId()
        val newResume = Resume(newId, name, publicText, description)

        val currentResumes = loadResumes().toMutableList()
        currentResumes.add(newResume)
        saveResumes(currentResumes)
        println("Resume '$name' added successfully with ID: $newId")
    }

    private fun generateNextId(): Int {
        val resumes = loadResumes()
        return if (resumes.isEmpty()) 0 else resumes.maxOf { it.id } + 1
    }

    private fun loadResumes(): List<Resume> {
        return if (jsonFile.exists()) {
            gson.fromJson(jsonFile.readText(), resumeType) ?: emptyList()
        } else {
            emptyList()
        }
    }

    private fun saveResumes(resumes: List<Resume>) {
        jsonFile.writeText(gson.toJson(resumes))
    }

    private fun countResumes(): Int = loadResumes().size


    fun deleteResume(id: Int) {
        /* //Terminal stuff
        println("Which resume do you want to delete? Enter the ID (0 to cancel)")
        val input = readlnOrNull() ?: return
        if (input == "0") {
            println("Delete cancelled.")
            return
        }
        */
        val currentResumes = loadResumes().toMutableList()
        val initialSize = currentResumes.size

        // Remove resumes matching the specified ID
        currentResumes.removeAll { it.id == id }

        when {
            currentResumes.size < initialSize -> {
                saveResumes(currentResumes)
                println("Resume with ID $id deleted successfully.")
            }
            else -> println("Resume with ID $id not found.")
        }

    }



    fun getAllDescriptions(): List<String> {
        return loadResumes().map { it.description }
    }

    fun getAllNames(): List<String>{
        return loadResumes().map{it.name}
    }

    fun getAllNameIds(): List<Pair<Int, String>>{
        return loadResumes().map{it.id to it.name}
    }

    fun getPublicText(id: Int): String {
        return loadResumes().firstOrNull { it.id == id }?.publicText ?: ""
    }
}