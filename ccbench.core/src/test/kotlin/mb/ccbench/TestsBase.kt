package mb.ccbench

import mu.KotlinLogging
import java.io.InputStream
import java.io.OutputStream
import java.nio.file.AtomicMoveNotSupportedException
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.StandardCopyOption
import java.util.zip.ZipEntry
import java.util.zip.ZipInputStream

class TestsBase {

    private val log = KotlinLogging.logger {}

    /**
     * Copies a resource from this JAR to the specified path.
     *
     * @param source the source path of the resource
     * @param target the target path of the resource
     */
    protected fun copyResource(source: String, target: Path) {
        TestsBase::class.java.getResourceAsStream(source)?.use { input ->
            input.copyToFileAtomic(target)
        } ?: throw IllegalStateException("Resource not found: $source")
    }

    /**
     * Extracts a ZIP resource from this JAR to the specified path.
     *
     * @param source the source path of the ZIP resource
     * @param target the target path to which to extract the ZIP resource
     */
    protected fun extractZipResource(source: String, target: Path) {
        TestsBase::class.java.getResourceAsStream(source)?.use { input ->
            ZipInputStream(input).use { zip ->
                lateinit var entry: ZipEntry
                while (zip.nextEntry?.also { entry = it } != null) {
                    val outPath: Path = target.resolve(entry.name)
                    if (entry.isDirectory) {
                        Files.createDirectories(outPath)
                    } else {
                        zip.copyToFileAtomic(outPath)
                    }
                }
            }
        } ?: log.warn { "Resource not found: $source" }
    }
    /**
     * Copies the content of an stream to the specified path atomically,
     * replacing any existing file that may be present.
     *
     * @param dest the destination path
     * @param bufferSize the default buffer size
     * @return the number of bytes copied
     * @throws AtomicMoveNotSupportedException if the atomic move cannot be performed
     */
    fun InputStream.copyToFileAtomic(dest: Path, bufferSize: Int = DEFAULT_BUFFER_SIZE): Long
            = writeToFileAtomic(dest) {
        this.copyTo(it, bufferSize)
    }

    /**
     * Writes the content written to an output stream to the specified path atomically,
     * replacing any existing file that may be present.
     *
     * @param dest the destination path
     * @param operation the operation that writes to the output stream
     * @return the result of the operation
     * @throws AtomicMoveNotSupportedException if the atomic move cannot be performed
     */
    fun <R> writeToFileAtomic(dest: Path, operation: (OutputStream) -> R): R {
        val tmpPath = kotlin.io.path.createTempFile()
        val result = Files.newOutputStream(tmpPath).use { operation(it) }
        Files.move(tmpPath, dest, StandardCopyOption.ATOMIC_MOVE, StandardCopyOption.REPLACE_EXISTING)
        return result
    }
}