package ktopologyviz
/**
 *  This file is ported from
 *  https://raw.githubusercontent.com/zz85/kafka-streams-viz/gh-pages/main.js
 */
fun processName(name: String): String {
    return name.replace("/-/g".toRegex(), "-\\n")
}

// converts kafka stream ascii topology description to DOT language
fun convertTopologyToDot(topology: String) : String {
    val lines = topology.split('\n').map { it.trim() }
    val results = arrayListOf<String>()
    val outside = arrayListOf<String>()
    val stores = arrayListOf<String>()
    val topics = arrayListOf<String>()
    var entityName: String? = null;

    // dirty but quick parsing
    for (line in lines) {
        val sub = "Sub-topology: ([0-9]*)".toRegex()
        var match = sub.matchEntire(line)?.groupValues

        if (match != null) {
            if (results.size != 0) results.add("}")
            results.add("""subgraph cluster_${match[1]} {
                label="${match[0]}";
                style=filled;
                color=lightgrey;
                node [style=filled,color=white];
                """)
            continue
        }

        val node = "(Source:|Processor:|Sink:)\\s+(\\S+)\\s+\\((topics|topic|stores):(.*)\\)".toRegex()
        match = node.matchEntire(line)?.groupValues

        if (match != null) {
            entityName = processName(match[2])
            val type = match[3] // source, processor or sink
            var linkedNames = match[4]
            linkedNames = linkedNames.replace("\\[|\\]".toRegex(), "")
            linkedNames.split(',').map { processName(it.trim()) }.forEach { linkedName ->
                if (linkedName.isEmpty()) {
                    // short circuit
                } else if (type == "topics") {
                    // from
                    outside.add(""""$linkedName" -> "$entityName";""")
                    topics.add(linkedName);
                } else if (type == "topic") {
                    // to
                    outside.add(""""$entityName" -> "$linkedName";""")
                    topics.add(linkedName);
                } else if (type == "stores") {
                    if (entityName.contains("JOIN")) {
                        outside.add(""""$linkedName" -> "$entityName";""")
                    } else {
                        outside.add(""""$entityName" -> "$linkedName";""")
                    }
                    stores.add(linkedName)
                }
            }
            continue
        }

        val arrows = "\\-\\-\\>\\s+(.*)\$".toRegex()
        match = arrows.matchEntire(line)?.groupValues

        if (match != null && entityName != null) {
            val targets = match[1];
            for (name in targets.split(',')) {
                val linkedName = processName(name.trim());
                if (linkedName == "none") continue
                results.add(""""$entityName" -> "$linkedName";""");
            }
        }
    }

    if (results.size != 0) results.add("}")

    val res = results.plus(outside).toMutableList()

    stores.forEach{node ->
        res.add(""""$node" [shape=cylinder];""")
    }

    topics.forEach{node ->
        res.add(""""$node" [shape=rect];""")
    }

    return """
    digraph G {
		label = "Kafka Streams Topology"
		${res.joinToString("\n")}
	}
    """.trimIndent()
}