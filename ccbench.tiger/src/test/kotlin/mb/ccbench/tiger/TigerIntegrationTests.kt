package mb.ccbench.tiger

import mb.ccbench.tests.IntegrationTestsBase
import mu.KotlinLogging
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.io.TempDir
import java.nio.file.Path

class TigerIntegrationTests: IntegrationTestsBase() {

    private val log = KotlinLogging.logger {}

    @Test
    fun `integration test`(@TempDir tempDir: Path) {
        // Arrange
        val testName = "Tiger"
        val projectDir = tempDir.resolve("benchmark")
        val casesDir = tempDir.resolve("benchmark-cases")
        val resultsDir = tempDir.resolve("benchmark-results")
        extractZipResource("/benchmark.zip", projectDir)
        val component = createBenchComponent()

        // Act
        component.mainCommand.parse(listOf(
            "build",
            testName,
            "--project", projectDir.toString(),
            "--output", casesDir.toString(),
        ))

        // Act
        component.mainCommand.parse(listOf(
            "run",
            "tiger-test",
            "--project", projectDir.toString(),
            "--input", casesDir.resolve("$testName.yml").toString(),
            "--output", resultsDir.toString(),
            "--warmup", "5",
            "--sample", "50",
            "--seed", "12345",
        ))
    }
}