package kakkoiichris.sortvisualizer

import kakkoiichris.hypergame.view.Window

fun main(args: Array<String>) {
    var algorithm = Algorithm.MERGE
    var count = 750
    var width = 750
    var height = width
    var border = 10
    var mode = Mode.REVERSE
    var speed = 1.0 / 60

    var i = 0

    while (i < args.size) {
        val arg = args[i++]

        when (arg.uppercase()) {
            "-A" -> algorithm = Algorithm.valueOf(args[i++].uppercase())

            "-C" -> count = args[i++].toInt()

            "-D" -> {
                val dimensions = args[i++].split(',').map { it.toInt() }

                width = dimensions[0]
                height = dimensions[1]
            }

            "-M" -> mode = Mode.valueOf(args[i++].uppercase())

            "-S" -> speed = args[i++].toDouble()
        }
    }

    val window = Window<Visualizer>(width + (border * 2), height + (border * 2), title = "Sort Visualizer")

    val visualizer = Visualizer(width, height, border, algorithm, count, mode, speed)

    window.open(visualizer)
}