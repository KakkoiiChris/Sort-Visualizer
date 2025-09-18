package kakkoiichris.sortvisualizer

import kotlin.random.Random

enum class Algorithm(val fullName: String, val getter: (Visualizer, IntArray, IntRange) -> SortingAlgorithm) {
    BUBBLE("Bubble", { visualizer, numbers, range -> BubbleSort(visualizer, numbers, range) }),
    COCKTAIL("Cocktail Shaker", { visualizer, numbers, range -> CocktailSort(visualizer, numbers, range) }),
    INSERTION("Insertion", { visualizer, numbers, range -> InsertionSort(visualizer, numbers, range) }),
    SELECTION("Selection", { visualizer, numbers, range -> SelectionSort(visualizer, numbers, range) }),
    MERGE("Merge", { visualizer, numbers, range -> MergeSort(visualizer, numbers, range) }),
    COMB("Comb", { visualizer, numbers, range -> CombSort(visualizer, numbers, range) }),
    ODDEVEN("Odd Even", { visualizer, numbers, range -> OddEvenSort(visualizer, numbers, range) }),
    BOGO("Bogo", { visualizer, numbers, range -> BogoSort(visualizer, numbers, range) });

    operator fun invoke(visualizer: Visualizer, numbers: IntArray, range: IntRange = numbers.indices) =
        getter(visualizer, numbers, range)
}

sealed class SortingAlgorithm(
    val visualizer: Visualizer,
    val numbers: IntArray,
    val range: IntRange = numbers.indices
) {
    val sortSize get() = range.last - range.first + 1

    abstract val isSorted: Boolean

    abstract fun stepSort(callback: (Int, Int) -> Unit): Boolean
}

class BubbleSort(visualizer: Visualizer, numbers: IntArray, range: IntRange = numbers.indices) :
    SortingAlgorithm(visualizer, numbers, range) {
    private var pos = range.first
    private var top = range.last

    override val isSorted get() = top == range.first

    override fun stepSort(callback: (Int, Int) -> Unit): Boolean {
        callback(pos, pos + 1)

        val a = numbers[pos]
        val b = numbers[pos + 1]

        if (a > b) {
            numbers[pos] = b
            numbers[pos + 1] = a
        }

        pos++

        if (pos == top) {
            top--
            pos = range.first
        }

        return true
    }
}

class CocktailSort(visualizer: Visualizer, numbers: IntArray, range: IntRange = numbers.indices) :
    SortingAlgorithm(visualizer, numbers, range) {
    private var pos = 0
    private var dir = 1
    private var bottom = 0
    private var top = numbers.lastIndex

    override val isSorted get() = bottom >= top

    override fun stepSort(callback: (Int, Int) -> Unit): Boolean {
        callback(pos, pos + 1)

        val a = numbers[pos]
        val b = numbers[pos + 1]

        if (a > b) {
            numbers[pos] = b
            numbers[pos + 1] = a
        }

        pos += dir

        if (pos == top) {
            dir = -1

            top--
            pos--
        }
        else if (pos + 1 == bottom) {
            dir = 1

            bottom++
            pos++
        }

        return true
    }
}

class InsertionSort(visualizer: Visualizer, numbers: IntArray, range: IntRange = numbers.indices) :
    SortingAlgorithm(visualizer, numbers, range) {
    private var i = 1
    private var j = i - 1
    private var key = numbers[i]

    private var insertKey = true

    override val isSorted get() = i >= numbers.size

    override fun stepSort(callback: (Int, Int) -> Unit): Boolean {
        if (j >= range.first && numbers[j] > key) {
            numbers[j + 1] = numbers[j]
            j--

            callback(j, i)

            insertKey = true

            return true
        }

        if (insertKey) {
            numbers[j + 1] = key

            callback(j + 1, i)

            insertKey = false

            return true
        }

        i++
        key = numbers[i]
        j = i - 1

        callback(j, i)

        return true
    }
}

class SelectionSort(visualizer: Visualizer, numbers: IntArray, range: IntRange = numbers.indices) :
    SortingAlgorithm(visualizer, numbers, range) {
    private var first = 0
    private var min = first
    private var pos = first + 1

    override val isSorted get() = first == numbers.size - 1

    override fun stepSort(callback: (Int, Int) -> Unit): Boolean {
        callback(pos, min)

        if (pos == numbers.size) {
            val nMin = numbers[min]
            numbers[min] = numbers[first]
            numbers[first] = nMin

            first++
            min = first
            pos = first + 1

            return true
        }

        if (numbers[pos] < numbers[min]) {
            min = pos
        }

        pos++

        return true
    }
}

class MergeSort(visualizer: Visualizer, numbers: IntArray, range: IntRange = numbers.indices) :
    SortingAlgorithm(visualizer, numbers, range) {
    val aux = IntArray(numbers.size)

    val first get() = range.first
    val last get() = range.last

    var state = State.DIVIDE

    lateinit var leftSort: MergeSort
    lateinit var rightSort: MergeSort

    var leftSorted = false
    var rightSorted = false

    var mergePos = 0
    var mergeA = 0
    var mergeB = 0

    override val isSorted get() = mergePos == last

    override fun stepSort(callback: (Int, Int) -> Unit): Boolean {
        return when (state) {
            State.DIVIDE     -> stepDivide(callback)
            State.SORT_LEFT  -> stepSortLeft(callback)
            State.SORT_RIGHT -> stepSortRight(callback)
            State.MERGE      -> stepMerge(callback)
        }
    }

    private fun stepDivide(callback: (Int, Int) -> Unit): Boolean {
        if (sortSmall(callback)) {
            return true
        }

        val (leftRange, rightRange) = range.divide()

        callback(leftRange.first, rightRange.first)

        leftSort = MergeSort(visualizer, numbers, leftRange)
        rightSort = MergeSort(visualizer, numbers, rightRange)

        state = State.SORT_LEFT

        stepSortLeft(callback)

        return false
    }

    private fun sortSmall(callback: (Int, Int) -> Unit): Boolean {
        callback(first, last)

        if (sortSize == 1) {
            return true
        }

        if (sortSize == 2) {
            callback(first, last)

            val numA = numbers[first]
            val numB = numbers[last]

            if (numA > numB) {
                numbers[first] = numB
                numbers[last] = numA
            }

            return true
        }

        return false
    }

    private fun stepSortLeft(callback: (Int, Int) -> Unit): Boolean {
        leftSorted = leftSort.stepSort(callback)

        if (leftSorted) {
            mergeA = leftSort.first
            mergePos = mergeA

            callback(mergeA, mergeA)

            state = State.SORT_RIGHT
        }

        return false
    }

    private fun stepSortRight(callback: (Int, Int) -> Unit): Boolean {
        rightSorted = rightSort.stepSort(callback)

        if (rightSorted) {
            mergeB = rightSort.first

            callback(mergeA, mergeB)

            copyToAux()

            state = State.MERGE
        }

        return false
    }

    private fun copyToAux() {
        for (i in range) {
            aux[i] = numbers[i]
        }
    }

    private fun stepMerge(callback: (Int, Int) -> Unit): Boolean {
        callback(mergeA, mergeB)

        if (mergeA <= leftSort.last && mergeB > rightSort.last) {
            numbers[mergePos++] = aux[mergeA++]
        }
        else if (mergeA > leftSort.last && mergeB <= rightSort.last) {
            numbers[mergePos++] = aux[mergeB++]
        }
        else if (aux[mergeA] < aux[mergeB]) {
            numbers[mergePos++] = aux[mergeA++]
        }
        else {
            numbers[mergePos++] = aux[mergeB++]
        }

        return mergeA > leftSort.last && mergeB > rightSort.last
    }

    private fun IntRange.divide(): Pair<IntRange, IntRange> {
        val middle = (endInclusive + start) / 2

        val leftRange = start..middle
        val rightRange = (middle + 1)..endInclusive

        return leftRange to rightRange
    }

    enum class State {
        DIVIDE,
        SORT_LEFT,
        SORT_RIGHT,
        MERGE
    }
}

class CombSort(visualizer: Visualizer, numbers: IntArray, range: IntRange = numbers.indices) :
    SortingAlgorithm(visualizer, numbers, range) {
    private val shrinkFactor = 1.2

    private var i = 0
    private var gap = (numbers.size / shrinkFactor).toInt()

    private val j get() = (i + gap).toInt()

    override val isSorted get() = gap < 1

    override fun stepSort(callback: (Int, Int) -> Unit): Boolean {
        callback(i, j)

        if (j > numbers.lastIndex) {
            gap = (gap / shrinkFactor).toInt()

            i = 0

            return true
        }

        if (numbers[i] > numbers[j]) {
            val t = numbers[i]
            numbers[i] = numbers[j]
            numbers[j] = t
        }

        i++

        return true
    }
}

class OddEvenSort(visualizer: Visualizer, numbers: IntArray, range: IntRange = numbers.indices) :
    SortingAlgorithm(visualizer, numbers, range) {
    private var odd = false

    override val isSorted: Boolean
        get() = false

    override fun stepSort(callback: (Int, Int) -> Unit): Boolean {
        var i = if (odd) 1 else 0

        while (i < numbers.lastIndex) {
            val j = i + 1

            if (numbers[i] > numbers[j]) {
                val t = numbers[i]
                numbers[i] = numbers[j]
                numbers[j] = t
            }

            i += 2
        }

        odd = !odd

        return true
    }
}

/*class HeapSort(visualizer: Visualizer, numbers:IntArray, range: IntRange = numbers.indices) : SortingAlgorithm(visualizer, numbers, range) {
    override val isSorted: Boolean
        get() = false

    override fun stepSort(callback: (Int, Int) -> Unit): Boolean {

    }

    private val Int.left get() = this * 2
    private val Int.right get() = (this * 2) + 1
    private val Int.parent get() = (this - 1) / 2

    private fun siftUp(end: Int): Int? {
        if (end > 0) {
            val parent = end.parent

            if (numbers[parent] < numbers[end]) {
                val t = numbers[parent]
                numbers[parent] = numbers[end]
                numbers[end] = t

                end = parent
            }
            else return true
        }

        return false
    }
}*/

class BogoSort(visualizer: Visualizer, numbers: IntArray, range: IntRange = numbers.indices) :
    SortingAlgorithm(visualizer, numbers, range) {
    override val isSorted get() = numbers.isSorted

    override fun stepSort(callback: (Int, Int) -> Unit): Boolean {
        callback(Random.nextInt(numbers.size), Random.nextInt(numbers.size))

        numbers.shuffle()

        return false
    }
}

val IntArray.isSorted: Boolean
    get() {
        for (i in 1..<size) {
            if (this[i] != this[i - 1] + 1) return false
        }

        return true
    }