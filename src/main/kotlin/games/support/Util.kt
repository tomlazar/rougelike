package games.support

class Util {
    companion object {
        fun transpose(arr: Array<Array<String>>): Array<Array<String>> {
            return (0 until arr[0].size).map { i: Int ->
                arr.map { row: Array<String> -> row[i] }.toTypedArray()
            }.toTypedArray()
        }

        fun integersInRange(start: Double, stop: Double): IntRange {
            return start.toInt()..(stop.toInt() - (if (stop % 1.0 == 0.0) 1 else 0))
        }
    }
}