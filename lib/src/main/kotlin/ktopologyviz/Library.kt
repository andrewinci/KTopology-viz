
package ktopologyviz

import guru.nidi.graphviz.engine.Format
import guru.nidi.graphviz.engine.Graphviz
import java.io.File

fun saveTopologyImage(topology: String, outFilePath: String) =
    runCatching { convertTopologyToDot(topology) }
        .mapCatching { Graphviz.fromString(it).width(1600).render(Format.PNG).toFile(File(outFilePath)) }

fun renderTopology(topology: String) =
    runCatching { convertTopologyToDot(topology) }
        .mapCatching { Graphviz.fromString(it).render(Format.SVG).toImage() }
