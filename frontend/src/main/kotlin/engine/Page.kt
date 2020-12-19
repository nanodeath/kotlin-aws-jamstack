package engine

import kotlinx.html.HTML
import kotlinx.html.html
import kotlinx.html.stream.appendHTML
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths

abstract class Page(private val site: Site) {
    abstract fun HTML.apply()

    fun writeTo(path: String) = writeTo(Paths.get(path))
    fun writeTo(path: Path) {
        val fileToWrite = site.rootDir.resolve(path)
        Files.createDirectories(fileToWrite.parent)
        fileToWrite.toFile().bufferedWriter().use { writer ->
            writer.appendLine("<!DOCTYPE html>")
            writer.appendHTML().html {
                apply()
            }
        }
    }
}