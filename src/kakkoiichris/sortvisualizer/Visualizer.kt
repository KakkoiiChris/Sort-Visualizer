package kakkoiichris.sortvisualizer

import kakkoiichris.hypergame.Game
import kakkoiichris.hypergame.input.Input
import kakkoiichris.hypergame.media.Renderer
import kakkoiichris.hypergame.util.Time
import kakkoiichris.hypergame.util.math.Box
import kakkoiichris.hypergame.view.View
import java.awt.Color
import java.awt.Font
import kotlin.math.ceil

class Visualizer(
    val width: Int,
    val height: Int,
    val border: Int,
    val algorithm: Algorithm,
    count: Int,
    mode: Mode,
    val speed: Double,
    val colored: Boolean
) : Game {

    private val numbers = IntArray(count) { it + 1 }

    private val sorter: SortingAlgorithm

    private var sortTimer = 0.0

    private var swapA = 0
    private var swapB = 1

    private val metricsBox = Box(border.toDouble(), border.toDouble(), width / 4.0, height / 4.0)

    private var sortTime = 0.0
    private var visTime = 0.0

    init {
        when (mode) {
            Mode.SHUFFLE -> numbers.shuffle()
            Mode.REVERSE -> numbers.reverse()
            Mode.SORTED  -> Unit
        }

        this.sorter = algorithm(this, numbers)
    }

    override fun init(view: View<*>) {
    }

    override fun update(view: View<*>, time: Time, input: Input) {
        if (sorter.numbers.isSorted) {
            swapA = -1
            swapB = -1
            return
        }

        sortTimer += time.seconds

        visTime += time.seconds

        while (sortTimer >= speed) {
            sortTime += Time.count {
                sorter.stepSort { a, b ->
                    swapA = a
                    swapB = b
                }
            }

            sortTimer -= speed

            if (sorter.numbers.isSorted) {
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
            renderer.color = getBarColor(i, num)

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

        renderer.color = Color(0, 0, 0, 100)
        renderer.fillRect(metricsBox)

        renderer.color = Color.white
        renderer.font = Font("Consolas", Font.PLAIN, 20)

        renderer.drawString("${algorithm.fullName} Sort", metricsBox, 0.0, 0.0)
        renderer.drawString("Sort Time:   $sortTime", metricsBox, 0.0, 0.1)
        renderer.drawString("Visual Time: $visTime", metricsBox, 0.0, 0.2)
    }

    private fun getBarColor(i: Int, num: Int) = if (colored) {
        when (i) {
            swapA, swapB -> Color.black
            num - 1      -> Color(Color.HSBtoRGB((num - 1) / numbers.size.toFloat(), 1f, 1f))
            else         -> Color(Color.HSBtoRGB((num - 1) / numbers.size.toFloat(), 1f, 0.6f))
        }
    }
    else {
        when (i) {
            swapA   -> Color(127, 0, 0)
            swapB   -> Color(0, 0, 127)
            num - 1 -> Color(0, 127, 0)
            else    -> Color(127, 127, 127)
        }
    }

    override fun halt(view: View<*>) {
    }

    private fun swap(i: Int, j: Int) {
        val temp = numbers[i]
        numbers[i] = numbers[j]
        numbers[j] = temp
    }
}