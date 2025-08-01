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

import org.apache.pdfbox.pdmodel.font.PDFont

private val CONFIG_FILE = File("user_config.json")


object PdfGenerator {
    private const val PAGE_WIDTH = 612f // US Letter width in points
    private const val MARGIN_X = 50f
    private const val LINE_SPACING = 15f
    private const val MAX_LINE_WIDTH = PAGE_WIDTH - 2 * MARGIN_X

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
            val boldFont = PDType1Font(Standard14Fonts.FontName.HELVETICA_BOLD)
            val regularFont = PDType1Font(Standard14Fonts.FontName.HELVETICA)

            val json = CONFIG_FILE.readText()
            val config = parseConfig(json).also {
                name = it.name
                email = it.email
                phone = it.phone
            }
            // [Same config processing code as before]
            val processedText = text
                .replace("[name]", name)
                .replace("[phone_number]", phone)
                .replace("[email]", email)

            stream.beginText()
            stream.setFont(regularFont, 12f)
            stream.newLineAtOffset(MARGIN_X, 700f)

            for (line in processedText.lines()) {
                val (font, fontSize) = if (line == "Cover letter:") {
                    boldFont to 14f
                } else {
                    regularFont to 12f
                }

                wrapAndWriteText(stream, line, font, fontSize)
            }

            stream.endText()
        }

        document.save(outputPath)
        document.close()
    }

    private fun wrapAndWriteText(
        stream: PDPageContentStream,
        text: String,
        font: PDFont,
        fontSize: Float
    ) {
        val words = text.split("\\s+".toRegex()).filter { it.isNotEmpty() }
        if (words.isEmpty()) {
            writeNewLine(stream)
            return
        }

        val spaceWidth = font.getStringWidth(" ") / 1000f * fontSize
        var currentLine = StringBuilder()

        for (word in words) {
            val wordWidth = font.getStringWidth(word) / 1000f * fontSize
            val currentWidth = if (currentLine.isEmpty()) {
                wordWidth
            } else {
                font.getStringWidth(currentLine.toString()) / 1000f * fontSize + spaceWidth + wordWidth
            }

            if (currentWidth <= MAX_LINE_WIDTH) {
                if (currentLine.isNotEmpty()) currentLine.append(" ")
                currentLine.append(word)
            } else {
                if (currentLine.isNotEmpty()) {
                    writeTextLine(stream, currentLine.toString(), font, fontSize)
                    currentLine = StringBuilder(word)
                } else {
                    // Break long word into parts
                    writeBrokenWord(stream, word, font, fontSize)
                }
            }
        }

        if (currentLine.isNotEmpty()) {
            writeTextLine(stream, currentLine.toString(), font, fontSize)
        }
    }

    private fun writeTextLine(
        stream: PDPageContentStream,
        text: String,
        font: PDFont,
        fontSize: Float
    ) {
        stream.showText(text)
        writeNewLine(stream)
    }

    private fun writeNewLine(stream: PDPageContentStream) {
        stream.newLineAtOffset(0f, -LINE_SPACING)
    }

    private fun writeBrokenWord(
        stream: PDPageContentStream,
        word: String,
        font: PDFont,
        fontSize: Float
    ) {
        var startIdx = 0
        while (startIdx < word.length) {
            var endIdx = startIdx + 1
            while (endIdx <= word.length) {
                val segment = word.substring(startIdx, endIdx)
                val segmentWidth = font.getStringWidth(segment) / 1000f * fontSize

                if (segmentWidth > MAX_LINE_WIDTH) break
                endIdx++
            }

            if (endIdx == startIdx + 1) {
                // Force write at least one character
                writeTextLine(stream, word.substring(startIdx, startIdx + 1), font, fontSize)
                startIdx++
            } else {
                writeTextLine(stream, word.substring(startIdx, endIdx - 1), font, fontSize)
                startIdx = endIdx - 1
            }
        }
    }
}
