package kakkoiichris.sortvisualizer

import kakkoiichris.hypergame.Game
import kakkoiichris.hypergame.input.Input
import kakkoiichris.hypergame.input.Key
import kakkoiichris.hypergame.media.Renderer
import kakkoiichris.hypergame.state.StateManager
import kakkoiichris.hypergame.util.Time
import kakkoiichris.hypergame.view.Sketch
import kakkoiichris.hypergame.view.View
import java.awt.Color
import kotlin.math.ceil

class Visualizer(
    val width: Int,
    val height: Int,
    val border: Int,
    algorithm: Algorithm,
    count: Int,
    mode: Mode
) : Sketch(width + (border * 2), height + (border * 2), "Sort Visualizer") {

    private val numbers = IntArray(count) { it + 1 }

    private val algorithm = algorithm(numbers)

    private val speed = 1.0 / 10
    private var sortTimer = 0.0

    private var swapA = 0
    private var swapB = 1

    init {
        when (mode) {
            Mode.SHUFFLE -> numbers.shuffle()
            Mode.REVERSE -> numbers.reverse()
            Mode.SORTED  -> Unit
        }
    }

    override fun swapTo(view: View, game: Game) {
    }

    override fun swapFrom(view: View, game: Game) {
    }

    override fun update(view: View, game: Game, time: Time, input: Input) {
        if (algorithm.isSorted || numbers.isSorted) return

        sortTimer += time.seconds

        if (sortTimer < speed) return

        while (sortTimer >= speed) {
            algorithm.stepSort { a, b ->
                swapA = a
                swapB = b
            }

            sortTimer -= speed

            if (algorithm.isSorted || numbers.isSorted) break
        }
    }

    override fun render(view: View, game: Game, renderer: Renderer) {
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
                swapA -> Color.red
                swapB -> Color.cyan
                else  -> Color.white
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

    override fun halt(view: View, game: Game) {
    }
}