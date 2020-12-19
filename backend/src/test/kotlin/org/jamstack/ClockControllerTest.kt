package org.jamstack

import org.assertj.core.api.Assertions.assertThat
import org.jamstack.engine.hasResponse
import org.junit.jupiter.api.Test
import software.amazon.awssdk.http.HttpStatusCode
import java.time.Clock
import java.time.Instant
import java.time.ZoneId

class ClockControllerTest {
    @Test
    fun works() {
        val clock = Clock.fixed(Instant.parse("2020-01-01T00:00:00Z"), ZoneId.of("Z"))
        val response = ClockController(clock).invoke()
        assertThat(response).hasResponse(HttpStatusCode.OK, """{"human":"2020-01-01T00:00:00Z","unixTimeMs":1577836800000}""")
    }
}
