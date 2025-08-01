import java.io.File
private val CONFIG_FILE = File("user_config.json")

fun main() {
    Config.loadConfig()
    while (true) {
        println("\nMenu: [1] Generate Cover Letter [2] Manage Resumes [3] Configure personal info [4] Exit")
        when (readlnOrNull()) {
            "1" -> coverLetterWorkflow()
            "2" -> processResumes()
            "3" -> { CONFIG_FILE.delete(); Config.loadConfig() }
            "4" -> return
            else -> println("Invalid option")
        }
    }
}

private fun processResumes() {
    println("\n Menu: [1] Add resume [2] view resume stats [3] Delete resume [4] Back")
    while (true) {
        when (readlnOrNull()) {
            "1" -> ResumeManager.addResume()
            //"2" -> ResumeManager.resumeStats()
            //"3" -> ResumeManager.deleteResume()
            "4" -> return
            else -> println("Invalid option")
        }
    }
}

private fun coverLetterWorkflow() {
    println("Paste job posting text:")
    val jobPosting = buildString {
        while (true) {
            val line = readlnOrNull() ?: break
            if (line.isEmpty()) break
            append(line).append("\n")
        }
    }.trim()

    println("Job posting recieved")
    val resumeId = AiClient.selectBestResume(
        jobPosting,
        ResumeManager.getAllDescriptions()
    )
    // Already printed for above

    val publicText = ResumeManager.getPublicText(resumeId)
    val coverLetter = AiClient.generateCoverLetter(jobPosting, publicText)
    println("\nGenerated Cover Letter:\n$coverLetter")

    val coverLetterId = coverLetterManager.getCoverLetterName(resumeId)
    val pdfPath = "coverLetters/cl_${resumeId}_${coverLetterId}.pdf"

    PdfGenerator.textToPdf(coverLetter, pdfPath)
    println("PDF saved to $pdfPath")
}