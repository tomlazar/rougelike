package games.support

import java.io.File
import java.nio.file.Files

class CsvReader {
    companion object {
        fun readCsv(path: String): Array<Array<String>> {
            return Files.readAllLines(File(path).toPath()).map { line: String -> line.split(',').toTypedArray() }.toTypedArray()
        }

        fun transpose(arr: Array<Array<String>>): Array<Array<String>> {
            return (0 until arr[0].size).map { i: Int ->
                arr.map { row: Array<String> -> row[i] }.toTypedArray()
            }.toTypedArray()
        }
    }
}