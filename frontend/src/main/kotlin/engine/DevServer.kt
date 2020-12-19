package engine

import org.eclipse.jetty.http.MimeTypes
import org.eclipse.jetty.server.Server
import org.eclipse.jetty.server.ServerConnector
import org.eclipse.jetty.server.handler.ResourceHandler
import org.eclipse.jetty.util.resource.PathResource
import java.io.File

/**
 * Run this to serve up files locally.
 * ```
 * ./gradlew server
 * ```
 * ---
 * This DevServer isn't strictly necessary, either. If you navigate to the .html files in the build
 * directory, IntelliJ can open them up for you in a browser. See
 * [web-browsers](https://www.jetbrains.com/help/idea/configuring-third-party-tools.html#web-browsers).
 */
fun main(args: Array<String>) {
    val root = args.first()
    val port = args.getOrNull(1)?.toInt() ?: 8080
    val server = Server().apply {
        addConnector(ServerConnector(this).apply {
            this.host = "localhost"
            this.port = port
        })
    }
    server.handler = ResourceHandler().apply {
        isDirectoriesListed = false
        welcomeFiles = arrayOf("index.html")
        baseResource = PathResource(File(root))
        mimeTypes = MimeTypes().apply {
            addMimeMapping("html", MimeTypes.Type.TEXT_HTML_UTF_8.asString())
            addMimeMapping("js", "application/javascript;charset=utf-8")
            addMimeMapping("css", "text/css;charset=utf-8")
        }
    }
    server.start()
    val serverConnector = server.connectors.first() as ServerConnector
    println("Listening on http://${serverConnector.host}:${serverConnector.port}/")
    server.join()
}