package kakkoiichris.sortvisualizer

import kakkoiichris.hypergame.view.Window

fun main(args: Array<String>) {
    var algorithm = Algorithm.ODDEVEN
    var colored = true
    var width = 1700
    var height = width / 16 * 9
    var border = 10
    var length = 1700
    var mode = Mode.SHUFFLE
    var speed = 1.0 / 100

    var i = 0

    while (i < args.size) {
        val arg = args[i++]

        when (arg.uppercase()) {
            "-A" -> algorithm = Algorithm.valueOf(args[i++].uppercase())

            "-C"->colored=true

            "-D" -> {
                val dimensions = args[i++].split(',').map { it.toInt() }

                width = dimensions[0]
                height = dimensions[1]
            }

            "-L" -> length = args[i++].toInt()

            "-M" -> mode = Mode.valueOf(args[i++].uppercase())

            "-S" -> speed = args[i++].toDouble()
        }
    }

    val window = Window<Visualizer>(width + (border * 2), height + (border * 2), title = "Sort Visualizer")

    val visualizer = Visualizer(width, height, border, algorithm, length, mode, speed, colored)

    window.open(visualizer)
}