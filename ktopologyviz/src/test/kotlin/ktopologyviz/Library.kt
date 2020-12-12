package ktopologyviz

import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.shouldNotBe
import java.nio.file.Paths

class Library : FreeSpec({

    "Happy path" - {
        // arrange
        val topology = Paths.get("src", "test", "resources", "topologies", "complex.txt").toFile().readText()

        "Render to buffered image" {
            // act
            val result = renderTopology(topology)
            // assert
            result shouldNotBe null
        }

        "Save the topology diagram to disk" {
            // act
            val tempPath = Paths.get("build", "test-diagram.png")
            val result = saveTopologyImage(topology, tempPath.toAbsolutePath().toString())
            // assert
            result shouldNotBe null
        }
    }
})
