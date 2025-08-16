package kakkoiichris.sortvisualizer

import kotlin.random.Random

enum class Algorithm(val getter: (IntArray, IntRange) -> SortingAlgorithm) {
    BUBBLE({ numbers, range -> BubbleSort(numbers, range) }),
    COCKTAIL({ numbers, range -> CocktailSort(numbers, range) }),
    INSERTION({ numbers, range -> InsertionSort(numbers, range) }),
    SELECTION({ numbers, range -> SelectionSort(numbers, range) }),
    BOGO({ numbers, range -> BogoSort(numbers, range) });

    operator fun invoke(numbers: IntArray, range: IntRange) =
        getter(numbers, range)
}

sealed class SortingAlgorithm(val numbers: IntArray, val range: IntRange = numbers.indices) {
    abstract val isSorted: Boolean

    abstract fun stepSort(callback: (Int, Int) -> Unit)
}

class BubbleSort(numbers: IntArray, range: IntRange = numbers.indices) : SortingAlgorithm(numbers, range) {
    private var pos = range.first
    private var top = range.last

    override val isSorted get() = top == range.start

    override fun stepSort(callback: (Int, Int) -> Unit) {
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
            pos = range.start
        }
    }
}

class CocktailSort(numbers: IntArray, range: IntRange = numbers.indices) : SortingAlgorithm(numbers, range) {
    private var pos = 0
    private var dir = 1
    private var bottom = 0
    private var top = numbers.lastIndex

    override val isSorted get() = bottom >= top

    override fun stepSort(callback: (Int, Int) -> Unit) {
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
    }
}

class InsertionSort(numbers: IntArray, range: IntRange = numbers.indices) : SortingAlgorithm(numbers, range) {
    private var i = 1
    private var j = i - 1
    private var key = numbers[i]

    override val isSorted get() = i >= numbers.size

    override fun stepSort(callback: (Int, Int) -> Unit) {
        callback(j, i)

        if (j >= 0 && numbers[j] > key) {
            numbers[j + 1] = numbers[j]
            j--

            return
        }

        numbers[j + 1] = key

        if (i >= numbers.size) return

        i++

        if (isSorted) return

        key = numbers[i]
        j = i - 1
    }
}

class SelectionSort(numbers: IntArray, range: IntRange = numbers.indices) : SortingAlgorithm(numbers, range) {
    private var first = 0
    private var min = first
    private var pos = first + 1

    override val isSorted get() = first == numbers.size - 1

    override fun stepSort(callback: (Int, Int) -> Unit) {
        callback(pos, min)

        if (pos == numbers.size) {
            val nMin = numbers[min]
            numbers[min] = numbers[first]
            numbers[first] = nMin

            first++
            min = first
            pos = first + 1

            return
        }

        if (numbers[pos] < numbers[min]) {
            min = pos
        }

        pos++
    }
}

class BogoSort(numbers: IntArray, range: IntRange = numbers.indices) : SortingAlgorithm(numbers, range) {
    override val isSorted get() = numbers.isSorted

    override fun stepSort(callback: (Int, Int) -> Unit) {
        callback(Random.nextInt(numbers.size), Random.nextInt(numbers.size))

        numbers.shuffle()
    }
}

val IntArray.isSorted: Boolean
    get() {
        for (i in 1..<size) {
            if (this[i] < this[i - 1]) return false
        }

        return true
    }