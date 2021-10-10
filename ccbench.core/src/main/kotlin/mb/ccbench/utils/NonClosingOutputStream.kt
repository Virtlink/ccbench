package mb.ccbench.utils

import java.io.OutputStream

/**
 * A non-closing output stream wrapper.
 *
 * It is the responsibility of the code that creates an output stream to close it.
 * This wrapper prevents users of the output stream from closing it.
 */
class NonClosingOutputStream(
    private val innerStream: OutputStream,
) : OutputStream() {

    override fun close() {
        flush()
        // Non-closing.
    }

    override fun write(b: Int) {
        innerStream.write(b)
    }

    override fun write(b: ByteArray) {
        innerStream.write(b)
    }

    override fun write(b: ByteArray, off: Int, len: Int) {
        innerStream.write(b, off, len)
    }

    override fun flush() {
        innerStream.flush()
    }

    override fun toString(): String {
        return innerStream.toString()
    }
}