package mb.codecompletion.plot

import com.itextpdf.kernel.pdf.PdfWriter
import com.itextpdf.layout.Document
import org.jfree.chart.JFreeChart
import java.awt.Graphics2D
import java.awt.geom.Rectangle2D
import java.io.FileOutputStream
import java.io.OutputStream


//fun JFreeChart.writeToPdf(outputStream: OutputStream, width: Int, height: Int) {
//    var writer: PdfWriter? = null
//    val document = Document()
//    try {
//        writer = PdfWriter(outputStream)
//        document.open()
//        val contentByte: PdfContentByte = writer.getDirectContent()
//        val template: PdfTemplate = contentByte.createTemplate(width, height)
//        val graphics2d: Graphics2D = template.createGraphics(
//            width, height,
//            DefaultFontMapper()
//        )
//        val rectangle2d: Rectangle2D = Rectangle2D.Double(
//            0, 0, width.toDouble(),
//            height.toDouble()
//        )
//        chart.draw(graphics2d, rectangle2d)
//        graphics2d.dispose()
//        contentByte.addTemplate(template, 0, 0)
//    } catch (e: Exception) {
//        e.printStackTrace()
//    }
//    document.close()
//}