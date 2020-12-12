package ktopologyviz

import io.kotest.core.spec.style.StringSpec
import io.kotest.data.forAll
import io.kotest.data.headers
import io.kotest.data.row
import io.kotest.data.table
import io.kotest.matchers.shouldBe
import java.nio.file.Paths

class TopologyDescriptionParserKtTest : StringSpec({

    "Happy path test" {
        table(
            headers("topology", "dotfile"),
            row("source-sink.txt", "source-sink.dot"),
            row("map.txt", "map.dot"),
            row("complex.txt", "complex.dot"),
        ).forAll { topologyPath, dotFilePath ->

            val topology = Paths.get("src", "test", "resources", "topologies", topologyPath).toFile().readText()
            val dotFile = Paths.get("src", "test", "resources", "dot-files", dotFilePath).toFile().readText()

            convertTopologyToDot(topology).withoutSpacesAndNewLines() shouldBe dotFile.withoutSpacesAndNewLines()
        }
    }
})

fun String.withoutSpacesAndNewLines() = this
    .replace("\n", "")
    .replace(" ", "")
    .replace("\t", "")
