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
import kotlin.random.Random

class Visualizer(
    val width: Int,
    val height: Int,
    val border: Int,
    val algorithm: Algorithm,
    length: Int,
    val shuffleMode: ShuffleMode,
    val speed: Double,
    val colored: Boolean
) : Game {

    private var state = State.START

    private var waitTimer = 0.0

    private var shuffleIndex = 0
    private var shuffled = false

    private val numbers = IntArray(length) { it + 1 }

    private lateinit var sorter: SortingAlgorithm

    private var sortTimer = 0.0

    private var swapA = 0
    private var swapB = 1

    private val metricsBox = Box(border.toDouble(), border.toDouble(), width / 4.0, height / 4.0)

    private var sortTime = 0.0
    private var visTime = 0.0

    override fun init(view: View) {
    }

    override fun update(view: View, time: Time, input: Input) {
        when (state) {
            State.START   -> updateStart(view, time, input)
            State.SHUFFLE -> updateShuffle(view, time, input)
            State.WAIT    -> updateWait(view, time, input)
            State.SORT    -> updateSort(view, time, input)
            State.RESULTS -> updateResults(view, time, input)
        }
    }

    private fun updateStart(view: View, time: Time, input: Input) {
        waitTimer += time.seconds

        if (waitTimer >= 2.0) {
            waitTimer = 0.0
            state = State.SHUFFLE
        }
    }

    private fun updateShuffle(view: View, time: Time, input: Input) {
        sortTimer += time.seconds

        if (sortTimer >= 0.001) {
            sortTimer -= 0.001

            when (shuffleMode) {
                ShuffleMode.RANDOM -> {
                    var j = shuffleIndex

                    while (j == shuffleIndex) {
                        j = Random.nextInt(numbers.size)
                    }

                    val t = numbers[shuffleIndex]
                    numbers[shuffleIndex] = numbers[j]
                    numbers[j] = t

                    if (shuffleIndex == numbers.lastIndex) {
                        waitTimer = 0.0
                        state = State.WAIT
                    }

                    shuffleIndex++
                }

                ShuffleMode.REVERSE -> {
                    val j = numbers.size - shuffleIndex - 1

                    val t = numbers[shuffleIndex]
                    numbers[shuffleIndex] = numbers[j]
                    numbers[j] = t

                    shuffleIndex++

                    if (shuffleIndex >= j) {
                        waitTimer = 0.0
                        state = State.WAIT
                    }
                }

                ShuffleMode.COMB    -> {
                    val j = numbers.size - shuffleIndex - 1

                    val t = numbers[shuffleIndex]
                    numbers[shuffleIndex] = numbers[j]
                    numbers[j] = t

                    shuffleIndex += 2

                    if (shuffleIndex >= j) {
                        waitTimer = 0.0
                        state = State.WAIT
                    }
                }

                ShuffleMode.SORTED  -> {
                    waitTimer = 0.0
                    state = State.WAIT
                }
            }
        }
    }

    private fun updateWait(view: View, time: Time, input: Input) {
        waitTimer += time.seconds

        if (waitTimer >= 2.0) {
            sorter = algorithm(this, numbers)

            waitTimer = 0.0
            sortTimer = 0.0
            state = State.SORT
        }
    }

    private fun updateSort(view: View, time: Time, input: Input) {
        if (sorter.numbers.isSorted) {
            swapA = -1
            swapB = -1

            state = State.RESULTS

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

    private fun updateResults(view: View, time: Time, input: Input) {}

    override fun render(view: View, renderer: Renderer) {
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

        renderer.drawString("${algorithm.fullName} Sort (${state.status})", metricsBox, 0.0, 0.0)
        renderer.drawString("Sort Time:   ${String.format("%.5fs", sortTime)}", metricsBox, 0.0, 0.1)
        renderer.drawString("Visual Time: ${String.format("%.5fs", visTime)}", metricsBox, 0.0, 0.2)
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

    override fun halt(view: View) {
    }

    private fun swap(i: Int, j: Int) {
        val temp = numbers[i]
        numbers[i] = numbers[j]
        numbers[j] = temp
    }

    private enum class State(val status: String) {
        START("Starting Order"),
        SHUFFLE("Shuffling..."),
        WAIT("Shuffled Order"),
        SORT("Sorting..."),
        RESULTS("Done!")
    }
}