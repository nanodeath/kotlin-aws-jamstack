package pages

import config.UrlConfig
import kotlinx.html.HEAD
import kotlinx.html.script
import kotlinx.html.unsafe

fun HEAD.commonJsConfig() {
    script {
        unsafe {
            +"const API_ROOT = \"${UrlConfig.apiRoot}\";"
        }
    }
}