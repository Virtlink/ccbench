package mb.codecompletion.bench.utils

import java.io.Writer

/**
 * A non-closing writer wrapper.
 *
 * It is the responsibility of the code that creates a writer to close it.
 * This wrapper prevents users of the writer from closing it.
 */
class NonClosingWriter(
    private val innerWriter: Writer
) : Writer() {

    override fun close() {
        flush()
        // Non-closing.
    }

    override fun flush() {
        innerWriter.flush()
    }

    override fun write(cbuf: CharArray, off: Int, len: Int) {
        innerWriter.write(cbuf, off, len)
    }

    override fun write(c: Int) {
        innerWriter.write(c)
    }

    override fun write(cbuf: CharArray) {
        innerWriter.write(cbuf)
    }

    override fun write(str: String) {
        innerWriter.write(str)
    }

    override fun write(str: String, off: Int, len: Int) {
        innerWriter.write(str, off, len)
    }

    override fun append(csq: CharSequence?): Writer {
        return innerWriter.append(csq)
    }

    override fun append(csq: CharSequence?, start: Int, end: Int): Writer {
        return innerWriter.append(csq, start, end)
    }

    override fun append(c: Char): Writer {
        return innerWriter.append(c)
    }

    override fun toString(): String {
        return innerWriter.toString()
    }
}
