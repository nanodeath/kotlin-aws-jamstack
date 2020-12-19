package pages

import engine.*
import externalScript
import externalStyleLink
import kotlinx.html.*

fun Site.homepage() = object : Page(this) {
    override fun HTML.apply() {
        lang = "en"
        head {
            charset("utf-8")
            meta("viewport", "width=device-width, initial-scale=1, shrink-to-fit=no")
            externalStyleLink(
                "https://stackpath.bootstrapcdn.com/bootstrap/4.3.1/css/bootstrap.min.css",
                "sha384-ggOyR0iXCbMQv3Xipma34MD+dH/1fQ784/j6cY/iJTQUOhcWr7x9JvoRxT2MZw1T"
            )
            title("Hello, JamStack!")
            commonJsConfig()
        }
        body {
            bootstrap {
                div(classes = "container") {
                    row {
                        column {
                            h1 {
                                +"Hello, JamStack"
                            }
                        }
                    }
                    row {
                        column {
                            div {
                                +"This page was loaded at "
                                span {
                                    id = "time"
                                    +"TBD..."
                                }
                                +", according to ClockController."
                            }
                        }
                    }

                    row(marginTop(3)) {
                        column {
                            formGroup {
                                label {
                                    +"Enter some text you want reversed by ReverseStringController:"
                                    br
                                    textInput {
                                        id = "textToReverse"
                                        placeholder = "Text to reverse"
                                    }
                                }
                                button(classes = "btn btn-primary") {
                                    id = "reverseButton"
                                    +"Reverse!"
                                }
                            }

                            div {
                                +"Reversed result: "
                                span {
                                    id = "reversedResult"
                                    +"..."
                                }
                            }
                        }
                    }
                }
            }

            // Optional JavaScript
            externalScript(
                "https://code.jquery.com/jquery-3.3.1.slim.min.js",
                "sha384-q8i/X+965DzO0rT7abK41JStQIAqVgRVzpbzo5smXKp4YfRvH+8abtTE1Pi6jizo"
            )
            externalScript(
                "https://cdnjs.cloudflare.com/ajax/libs/popper.js/1.14.7/umd/popper.min.js",
                "sha384-UO2eT0CpHqdSJQ6hJty5KVphtPhzWj9WO1clHTMGa3JDZwrnQq4sF86dIHNDz0W1"
            )
            externalScript(
                "https://stackpath.bootstrapcdn.com/bootstrap/4.3.1/js/bootstrap.min.js",
                "sha384-JjSmVgyd0p3pXB1rRibZUAYoIIy6OrQ6VrjIEaFf/nJGzIxFDsf4x0xIM+B07jRM"
            )
            script(src = "/homepage.js") {}
        }
    }
}
