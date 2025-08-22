package kakkoiichris.sortvisualizer

import kakkoiichris.hypergame.view.Window

fun main(args: Array<String>) {
    var algorithm = Algorithm.INSERTION
    var count = 16
    var width = 500
    var height = width
    var border = 10
    var mode = Mode.REVERSE

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

            "-S" -> mode = Mode.valueOf(args[i++].uppercase())
        }
    }

    val window = Window<Visualizer>(width + (border * 2), height + (border * 2), title = "Sort Visualizer")

    val visualizer = Visualizer(width, height, border, algorithm, count, mode)

    window.open(visualizer)
}