package mb.ccbench.cli

import com.github.ajalt.mordant.rendering.BorderStyle
import com.github.ajalt.mordant.rendering.TextAlign
import com.github.ajalt.mordant.rendering.TextColors
import com.github.ajalt.mordant.rendering.Widget
import com.github.ajalt.mordant.table.Borders
import com.github.ajalt.mordant.table.table
import com.github.ajalt.mordant.terminal.Terminal
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import java.util.*
import javax.inject.Inject


/**
 * Returns the version information.
 */
fun PropertiesManager.versionInfo(): String = """
    ------------------------------------------------------------
    CCBench $version ($revision)
    ------------------------------------------------------------

    Built on:     ${buildTime?.let { longDateTimeFormatter.format(it) } ?: buildTimeString }
    Revision:     $fullRevision

    JVM:          $javaVersion ($javaVmVendor $javaVmVersion)
    OS:           $osName $osVersion ($osArch)
    
    User:         $userName
    User home:    $userHome
    Working dir:  $workingDirectory
    
    Memory:       ${Runtime.getRuntime().maxMemory() / (1024 * 1024)} MB
    Processors:   ${Runtime.getRuntime().availableProcessors()}
    """.trimIndent()

fun PropertiesManager.userAgent(): String = "Labback/$version"

/**
 * Formats an instant as a long date/time with the system's default locale and time zone.
 */
private val longDateTimeFormatter = DateTimeFormatter
    .ofLocalizedDateTime(FormatStyle.LONG)
    .withLocale(Locale.getDefault())
    .withZone(ZoneId.systemDefault())

/**
 * Prints version info.
 */
class VersionInfo @Inject constructor(
    private val terminal: Terminal,
) {

    private val propertiesManager = PropertiesManager

    /**
     * Prints the version info to STDOUT.
     *
     * This automatically detects the capabilities of the terminal,
     * such as its support for colors and its size.
     */
    fun print() {
        terminal.println(getVersionTable())
    }

    /**
     * Writes the version info to a string.
     */
    fun renderToString(): String {
        return terminal.render(getVersionTable())
    }

    private fun getVersionTable(): Widget = propertiesManager.run {
        table {
            align = TextAlign.LEFT
            outerBorder = true
            borders = Borders.NONE
            borderStyle = BorderStyle.DOUBLE
            column(0) {
                style(TextColors.magenta, bold = true)
            }
            header {
                style(TextColors.red, bold = true)
                row {
                    borders = Borders.TOM_BOTTOM
                    cell("CCBench $revision") { columnSpan = 2 }
                }
            }
            body {
                rowStyles(TextColors.blue, TextColors.blue)
                row("Version:", version)
                row("Revision:", fullRevision)
                row("Built on:", buildTime?.let { longDateTimeFormatter.format(it) } ?: buildTimeString)
                row()
                row("JVM:", "$javaVersion ($javaVmVendor $javaVmVersion)")
                row("OS:", "$osName $osVersion ($osArch)")
                row()
                row("User:", userName)
                row("User home:", userHome)
                row("Working dir:", workingDirectory)
                row()
                row("Memory (MB):", Runtime.getRuntime().maxMemory() / (1024 * 1024))
                row("Processors:", Runtime.getRuntime().availableProcessors())
            }
        }
    }
}