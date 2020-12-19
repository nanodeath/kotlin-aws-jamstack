package engine

import kotlinx.html.FlowContent
import kotlinx.html.div

class Bootstrap(val content: FlowContent)

fun FlowContent.bootstrap(cb: Bootstrap.() -> Unit) {
    Bootstrap(this).cb()
}

fun Bootstrap.row(classes: String? = null, cb: FlowContent.() -> Unit) {
    simpleDivHelper("row", classes, cb = cb)
}

fun Bootstrap.column(classes: String? = null, cb: FlowContent.() -> Unit) {
    simpleDivHelper("col", classes, cb = cb)
}

fun Bootstrap.card(classes: String? = null, cb: FlowContent.() -> Unit) {
    simpleDivHelper("card", classes, cb = cb)
}

fun Bootstrap.formGroup(classes: String? = null, cb: FlowContent.() -> Unit) {
    simpleDivHelper("formGroup", classes, cb = cb)
}

private fun Bootstrap.simpleDivHelper(vararg css: String?, cb: FlowContent.() -> Unit) {
    content.div(css.asSequence().filterNotNull().map(String::trim).joinToString(" ")) {
        cb()
    }
}

// Spacing helpers
// https://getbootstrap.com/docs/4.1/utilities/spacing/

fun marginTop(size: Int? = null) = "mt-${size ?: "auto"}"
fun marginBottom(size: Int? = null) = "mb-${size ?: "auto"}"
fun marginLeft(size: Int? = null) = "ml-${size ?: "auto"}"
fun marginRight(size: Int? = null) = "mr-${size ?: "auto"}"
fun marginX(size: Int? = null) = "mx-${size ?: "auto"}"
fun marginY(size: Int? = null) = "my-${size ?: "auto"}"
fun margin(size: Int? = null) = "m-${size ?: "auto"}"

fun paddingTop(size: Int) = "pt-${size}"
fun paddingBottom(size: Int) = "pb-${size}"
fun paddingLeft(size: Int) = "pl-${size}"
fun paddingRight(size: Int) = "pr-${size}"
fun paddingX(size: Int) = "px-${size}"
fun paddingY(size: Int) = "py-${size}"
fun padding(size: Int) = "p-${size}"
