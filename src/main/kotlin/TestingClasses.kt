fun main() {
    try {
        val coverLetterText = """
            Cover letter:
            Dear Hiring Manager,
            
            My name is [name]. 
            Contact me at [phone_number] or [email]
        """.trimIndent()

        PdfGenerator.textToPdf(coverLetterText, "coverLetters/cl_1_1.pdf")
    } catch (e: Exception) {
        println("Error generating PDF: ${e.message}")
    }
}