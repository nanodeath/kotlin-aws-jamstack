import kotlinx.html.*

fun HEAD.externalStyleLink(url: String, integrity: String): Unit = link {
    rel = "stylesheet"
    href = url
    this.integrity = integrity
    attributes["crossorigin"] = "anonymous"
}

fun FlowOrPhrasingOrMetaDataContent.externalScript(
    src: String,
    integrity: String
): Unit = script {
    this.src = src
    this.integrity = integrity
    attributes["crossorigin"] = "anonymous"
}

