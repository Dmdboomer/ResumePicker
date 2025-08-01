fun main() {
    try {
        val coverLetterText = """
            Cover letter:
            Dear Hiring Manager,
            sdfjkhsdf\n
            sdfsdfsdf\n
            4545454454545445454544545454454545445454544545454454545445454544545454454545445454544545454454545445454544545454
            4545454454545445454544545454
            
            My name is [name]. 
            Contact me at [phone_number] or [email]
        """.trimIndent()
        println(PdfGenerator.textToPdf(coverLetterText, "coverLetters/Test.pdf"))

    } catch (e: Exception) {
        println("Error generating PDF: ${e.message}")
    }
}