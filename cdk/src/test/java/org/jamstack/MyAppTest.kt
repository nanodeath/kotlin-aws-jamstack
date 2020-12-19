package org.jamstack

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature
import java.io.IOException
import software.amazon.awscdk.core.App
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.Test

class MyAppTest {
    @Test
    fun testStack() {
        val app = App()
        val stack = JamStack(app, "test", null)

        val actual = JSON.valueToTree<JsonNode>(app.synth().getStackArtifact(stack.artifactId).template)
        Assertions.assertThat(ObjectMapper().createObjectNode()).isNotEqualTo(actual)
    }

    companion object {
        private val JSON = ObjectMapper().configure(SerializationFeature.INDENT_OUTPUT, true)
    }
}