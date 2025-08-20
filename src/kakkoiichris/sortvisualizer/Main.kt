package kakkoiichris.sortvisualizer

fun main(args: Array<String>) {
    var algorithm = Algorithm.MERGE
    var count = 100
    var width = 1000
    var height = width / 16 * 10
    var border = 10
    var mode = Mode.SHUFFLE

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

    val visualizer = Visualizer(width, height, border, algorithm, count, mode)

    visualizer.open()
}