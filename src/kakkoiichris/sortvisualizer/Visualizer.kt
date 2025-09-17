package kakkoiichris.sortvisualizer

import kakkoiichris.hypergame.Game
import kakkoiichris.hypergame.input.Input
import kakkoiichris.hypergame.media.Renderer
import kakkoiichris.hypergame.util.Time
import kakkoiichris.hypergame.view.View
import java.awt.Color
import kotlin.math.ceil

class Visualizer(
    val width: Int,
    val height: Int,
    val border: Int,
    algorithm: Algorithm,
    count: Int,
    mode: Mode,
    val speed: Double
) : Game {

    private val numbers = IntArray(count) { it + 1 }

    private val algorithm: SortingAlgorithm

    private var sortTimer = 0.0

    private var swapA = 0
    private var swapB = 1

    init {
        when (mode) {
            Mode.SHUFFLE -> numbers.shuffle()
            Mode.REVERSE -> numbers.reverse()
            Mode.SORTED  -> Unit
        }

        this.algorithm = algorithm(this, numbers)
    }

    override fun init(view: View<*>) {
    }

    override fun update(view: View<*>, time: Time, input: Input) {
        if (algorithm.numbers.isSorted) {
            swapA = -1
            swapB = -1
            return
        }

        sortTimer += time.seconds

        while (sortTimer >= speed) {
            algorithm.stepSort { a, b ->
                swapA = a
                swapB = b
            }

            sortTimer -= speed

            if (algorithm.numbers.isSorted) {
                break
            }
        }
    }

    override fun render(view: View<*>, renderer: Renderer) {
        renderer.clearRect(view.bounds)

        val barWidthDelta = width / numbers.size.toDouble()
        val barHeightDelta = height / numbers.size.toDouble()

        var barLeft = 0.0
        var barRight = barLeft + barWidthDelta

        renderer.push()
        renderer.translate(border, view.height - border)
        renderer.scale(1.0, -1.0)

        for ((i, num) in numbers.withIndex()) {
            renderer.color = when (i) {
                swapA   -> Color(127, 0, 0)
                swapB   -> Color(0, 0, 127)
                num - 1 -> Color(0, 127, 0)
                else    -> Color(127, 127, 127)
            }

            renderer.fillRect(
                barLeft.toInt(),
                0,
                ceil(barRight - barLeft).toInt(),
                ceil(num * barHeightDelta).toInt()
            )

            barLeft = barRight
            barRight += barWidthDelta
        }

        renderer.pop()
    }

    override fun halt(view: View<*>) {
    }
}