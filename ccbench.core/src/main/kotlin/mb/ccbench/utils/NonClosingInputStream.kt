package mb.ccbench.utils

import java.io.InputStream
import java.io.OutputStream

/**
 * A non-closing input stream wrapper.
 *
 * It is the responsibility of the code that creates an input stream to close it.
 * This wrapper prevents users of the input stream from closing it.
 */
class NonClosingInputStream(
    private val innerStream: InputStream,
) : InputStream() {

    override fun close() {
        // Non-closing.
    }

    override fun read(): Int {
        return innerStream.read()
    }

    override fun read(b: ByteArray): Int {
        return innerStream.read(b)
    }

    override fun read(b: ByteArray, off: Int, len: Int): Int {
        return innerStream.read(b, off, len)
    }

    override fun readAllBytes(): ByteArray {
        return innerStream.readAllBytes()
    }

    override fun readNBytes(len: Int): ByteArray {
        return innerStream.readNBytes(len)
    }

    override fun readNBytes(b: ByteArray?, off: Int, len: Int): Int {
        return innerStream.readNBytes(b, off, len)
    }

    override fun skip(n: Long): Long {
        return innerStream.skip(n)
    }

    override fun available(): Int {
        return innerStream.available()
    }

    override fun mark(readlimit: Int) {
        innerStream.mark(readlimit)
    }

    override fun reset() {
        innerStream.reset()
    }

    override fun markSupported(): Boolean {
        return innerStream.markSupported()
    }

    override fun transferTo(out: OutputStream?): Long {
        return innerStream.transferTo(out)
    }

    override fun toString(): String {
        return innerStream.toString()
    }
}