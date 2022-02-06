package mb.ccbench.cli


import mu.KotlinLogging
import java.time.Instant
import java.time.OffsetDateTime
import java.time.format.DateTimeParseException
import java.util.*

/**
 * Manages properties.
 *
 * This is a static class that is loaded when the application is loaded.
 */
object PropertiesManager {

    private val log = KotlinLogging.logger {}
    private val properties = Properties()

    /** The application version. */
    val version: String get() = properties.getProperty("version")!!
    /** The application revision. */
    val revision: String get() = properties.getProperty("revision")!!
    /** The application full revision. */
    val fullRevision: String get() = properties.getProperty("full-revision")!!
    /** The application build time. */
    val buildTime: Instant? get() = tryParseOffsetDateTime(buildTimeString)
    /** The application build time, as a string. */
    val buildTimeString: String get() = properties.getProperty("build-time")!!

    /** The Java version. */
    val javaVersion: String get() = System.getProperty("java.version", null)!!
    /** The Java VM vendor. */
    val javaVmVendor: String get() = System.getProperty("java.vm.vendor", null)!!
    /** The Java VM version. */
    val javaVmVersion: String get() = System.getProperty("java.vm.version", null)!!

    /** The OS name. */
    val osName: String get() = System.getProperty("os.name", null)!!
    /** The OS version. */
    val osVersion: String get() = System.getProperty("os.version", null)!!
    /** The OS architecture. */
    val osArch: String get() = System.getProperty("os.arch", null)!!

    /** The name of the current user. */
    val userName: String get() = System.getProperty("user.name", null)!!
    /** The home directory of the current user. */
    val userHome: String get() = System.getProperty("user.home", null)!!
    /** The current working directory. */
    val workingDirectory: String get() = System.getProperty("user.dir", null)!!

    init {
        load()
    }

    /**
     * Loads the properties.
     */
    private fun load() {
        log.trace { "Loading properties..." }
        PropertiesManager::class.java.getResourceAsStream("/version.properties").use {
            properties.load(it)
        }
        log.debug { "Loaded properties:\n  " + properties.entries.joinToString("\n  ") { (k, v) -> "$k: $v" } }
    }

    /**
     * Gets a property from this properties manager,
     * or the system property, or the provided default value.
     *
     * @param key the property key to look for
     * @param default the default value; or `null`
     * @return the resulting value; or `null` if not found and no default value was provided
     */
    operator fun get(key: String, default: String? = null): String? = properties.getProperty(key) ?: System.getProperty(key, default)

    /**
     * Attempts to parse the given string as an offset date/time.
     *
     * @param s the string to parse
     * @return the resulting [Instant]; or `null` if parsing failed
     */
    private fun tryParseOffsetDateTime(s: String): Instant? {
        return try {
            OffsetDateTime.parse(s).toInstant()
        } catch (ex: DateTimeParseException) {
            null
        }
    }
}
