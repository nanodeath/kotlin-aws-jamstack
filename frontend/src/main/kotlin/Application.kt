import engine.Site
import pages.homepage
import java.nio.file.Paths

fun main(args: Array<String>) {
    val targetDir = args.first().let { Paths.get(it) }
    Site(targetDir).apply {
        homepage().writeTo("index.html")
    }
}
