package games.support

import java.io.File
import java.nio.file.Files

class CsvReader {
    companion object {
        fun readCsv(path: String): Array<Array<String>> {
            return Files.readAllLines(File(path).toPath()).map { line: String -> line.split(',').toTypedArray() }.toTypedArray()
        }


    }
}