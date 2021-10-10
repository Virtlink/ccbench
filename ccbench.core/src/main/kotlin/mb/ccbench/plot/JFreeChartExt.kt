package mb.ccbench.plot

import org.jfree.chart.JFreeChart
import org.jfree.pdf.PDFDocument
import java.awt.Rectangle
import java.io.OutputStream
import java.nio.file.Path
import kotlin.io.path.outputStream

/**
 * Writes a JFreeChart to a PDF stream.
 *
 * The stream is not closed by this method.
 *
 * @param outputStream the output stream to write to
 * @param width the width of the document
 * @param height the height of the document
 */
fun JFreeChart.writeToPdf(outputStream: OutputStream, width: Int, height: Int) {
    val doc = PDFDocument()
    val bounds = Rectangle(width, height)
    val page = doc.createPage(bounds)
    val g2 = page.graphics2D
    this.draw(g2, bounds)
    outputStream.write(doc.pdfBytes)
}

/**
 * Writes a JFreeChart to a PDF file.
 *
 * @param file the file to write to
 * @param width the width of the document
 * @param height the height of the document
 */
fun JFreeChart.writeToPdf(file: Path, width: Int, height: Int) {
    file.outputStream().use { outputStream ->
        writeToPdf(outputStream, width, height)
    }
}