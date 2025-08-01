import Config.email
import Config.name
import Config.parseConfig
import Config.phone
import org.apache.pdfbox.pdmodel.PDDocument
import org.apache.pdfbox.pdmodel.PDPage
import org.apache.pdfbox.pdmodel.PDPageContentStream
import org.apache.pdfbox.pdmodel.font.PDType1Font
import org.apache.pdfbox.pdmodel.font.Standard14Fonts
import java.io.File

private val CONFIG_FILE = File("user_config.json")


object PdfGenerator {
    fun textToPdf(text: String, outputPath: String) {
        val document = PDDocument()
        val page = PDPage()
        document.addPage(page)

        PDPageContentStream(
            document,
            page,
            PDPageContentStream.AppendMode.APPEND,
            true
        ).use { stream ->
            // Fixed font initialization (PDFBox 3.x compatible)
            val boldFont = PDType1Font(Standard14Fonts.FontName.HELVETICA_BOLD)
            val regularFont = PDType1Font(Standard14Fonts.FontName.HELVETICA)

            stream.beginText()
            try {
                stream.setFont(regularFont, 12f) // Default font
                stream.newLineAtOffset(50f, 700f)

                val json = CONFIG_FILE.readText()
                val config = parseConfig(json).also {
                    name = it.name
                    email = it.email
                    phone = it.phone
                }
                // Fixed: Initialize config properties before use
                val processedText = text
                    .replace("[name]", name)
                    .replace("[phone_number]", phone)
                    .replace("[email]", email)

                for (line in processedText.lines()) {
                    if (line == "Cover letter:") {
                        stream.setFont(boldFont, 14f)
                    } else {
                        stream.setFont(regularFont, 12f)
                    }
                    stream.showText(line)
                    stream.newLineAtOffset(0f, -15f)
                }
            } finally {
                // Ensure endText() is always called
                stream.endText()
            }
        }

        document.save(outputPath)
        document.close()
    }
}