package kakkoiichris.sortvisualizer

import kakkoiichris.hypergame.view.Config
import kakkoiichris.hypergame.view.Window

fun main(args: Array<String>) {
    var algorithm = Algorithm.MAX_HEAP
    var border = 10
    var colored = true
    var width = 1000
    var height = width / 16 * 9
    var length = 800
    var shuffleMode = ShuffleMode.SORTED
    var speed = 1.0 / 100

    var i = 0

    while (i < args.size) {
        val arg = args[i++]
            .takeIf { it.startsWith('-') }
            ?.uppercase()
            ?.last()
            ?: continue

        when (arg) {
            'A' -> algorithm = Algorithm.valueOf(args[i++].uppercase())

            'B' -> border = args[i++].toInt()

            'C' -> colored = true

            'D' -> {
                val dimensions = args[i++]
                    .split(',')
                    .map { it.toInt() }

                width = dimensions[0]
                height = dimensions[1]
            }

            'L' -> length = args[i++].toInt()

            'M' -> shuffleMode = ShuffleMode.valueOf(args[i++].uppercase())

            'S' -> speed = args[i++].toDouble()
        }
    }

    val window = Window(Config(width + (border * 2), height + (border * 2), title = "Sort Visualizer"))

    val visualizer = Visualizer(width, height, border, algorithm, length, shuffleMode, speed, colored)

    window.open(visualizer)
}