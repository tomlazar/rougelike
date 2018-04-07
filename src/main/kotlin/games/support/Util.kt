package games.support

import java.io.File
import java.nio.file.Files

class Util {
    companion object {
        fun readCsv(path: String): Array<Array<String>> {
            return Files.readAllLines(File(path).toPath()).map { line: String -> line.split(',', '\t').toTypedArray() }.toTypedArray()
        }

        fun transpose(arr: Array<Array<String>>): Array<Array<String>> {
            return if (arr.isEmpty()) {
                arr.clone()
            } else {
                (0 until arr[0].size).map { i: Int ->
                    arr.map { row: Array<String> -> row[i] }.toTypedArray()
                }.toTypedArray()
            }
        }

        fun integersInRange(start: Double, stop: Double): IntRange {
            return start.toInt()..(stop.toInt() - (if (stop % 1.0 == 0.0) 1 else 0))
        }
    }
}