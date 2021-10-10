package mb.ccbench.utils

import java.io.Reader
import java.io.Writer
import java.nio.CharBuffer

/**
 * A non-closing reader wrapper.
 *
 * It is the responsibility of the code that creates a reader to close it.
 * This wrapper prevents users of the reader from closing it.
 */
class NonClosingReader(
    private val innerReader: Reader
) : Reader() {

    override fun close() {
        // Non-closing.
    }
    override fun read(cbuf: CharArray, off: Int, len: Int): Int {
        return innerReader.read(cbuf, off, len)
    }

    override fun read(target: CharBuffer): Int {
        return innerReader.read(target)
    }

    override fun read(): Int {
        return innerReader.read()
    }

    override fun read(cbuf: CharArray): Int {
        return innerReader.read(cbuf)
    }

    override fun skip(n: Long): Long {
        return innerReader.skip(n)
    }

    override fun ready(): Boolean {
        return innerReader.ready()
    }

    override fun markSupported(): Boolean {
        return innerReader.markSupported()
    }

    override fun mark(readAheadLimit: Int) {
        innerReader.mark(readAheadLimit)
    }

    override fun reset() {
        innerReader.reset()
    }

    override fun transferTo(out: Writer?): Long {
        return innerReader.transferTo(out)
    }

    override fun toString(): String {
        return innerReader.toString()
    }
}
