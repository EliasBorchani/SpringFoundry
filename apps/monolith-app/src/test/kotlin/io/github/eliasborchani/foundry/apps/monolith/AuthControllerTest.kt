package io.github.eliasborchani.foundry.apps.monolith

import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.post
import org.springframework.test.web.servlet.setup.MockMvcBuilders
import org.springframework.web.context.WebApplicationContext
import tools.jackson.databind.ObjectMapper
import kotlin.test.assertNotEquals

@SpringBootTest
class AuthControllerTest {

    @Autowired lateinit var webApplicationContext: WebApplicationContext
    @Autowired lateinit var objectMapper: ObjectMapper

    lateinit var mockMvc: MockMvc

    // Unique per test-instance; JUnit 5 creates a new instance per test method
    private val email = "test-${System.nanoTime()}@example.com"
    private val password = "s3cr3tP@ssw0rd"
    private val displayName = "Test User"

    @BeforeEach
    fun setup() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build()
    }

    @Test
    fun `register returns 201 with token pair`() {
        mockMvc.post("/auth/register") {
            contentType = MediaType.APPLICATION_JSON
            content = objectMapper.writeValueAsString(
                mapOf("email" to email, "displayName" to displayName, "password" to password)
            )
        }.andExpect {
            status { isCreated() }
            jsonPath("$.accessToken") { isNotEmpty() }
            jsonPath("$.refreshToken") { isNotEmpty() }
            jsonPath("$.tokenType") { value("Bearer") }
        }
    }

    @Test
    fun `login with valid credentials returns 200 with token pair`() {
        register()

        mockMvc.post("/auth/login") {
            contentType = MediaType.APPLICATION_JSON
            content = objectMapper.writeValueAsString(
                mapOf("email" to email, "password" to password)
            )
        }.andExpect {
            status { isOk() }
            jsonPath("$.accessToken") { isNotEmpty() }
            jsonPath("$.refreshToken") { isNotEmpty() }
        }
    }

    @Test
    fun `refresh returns 200 with a new access token`() {
        val loginTokens = register()
        val originalAccessToken = loginTokens["accessToken"] as String

        // Small sleep so the JWT exp timestamp differs (tokens issued within same second are identical)
        Thread.sleep(1_000)

        val refreshResult = mockMvc.post("/auth/refresh") {
            contentType = MediaType.APPLICATION_JSON
            content = objectMapper.writeValueAsString(
                mapOf("refreshToken" to loginTokens["refreshToken"])
            )
        }.andExpect {
            status { isOk() }
            jsonPath("$.accessToken") { isNotEmpty() }
        }.andReturn()

        val body = objectMapper.readValue(refreshResult.response.contentAsString, Map::class.java)
        assertNotEquals(originalAccessToken, body["accessToken"], "New access token must differ")
    }

    @Test
    fun `logout returns 204 and subsequent refresh returns 401`() {
        val loginTokens = register()
        val refreshToken = loginTokens["refreshToken"] as String

        mockMvc.post("/auth/logout") {
            contentType = MediaType.APPLICATION_JSON
            content = objectMapper.writeValueAsString(mapOf("refreshToken" to refreshToken))
        }.andExpect {
            status { isNoContent() }
        }

        mockMvc.post("/auth/refresh") {
            contentType = MediaType.APPLICATION_JSON
            content = objectMapper.writeValueAsString(mapOf("refreshToken" to refreshToken))
        }.andExpect {
            status { isUnauthorized() }
        }
    }

    @Test
    fun `login with wrong password returns 401`() {
        register()

        mockMvc.post("/auth/login") {
            contentType = MediaType.APPLICATION_JSON
            content = objectMapper.writeValueAsString(
                mapOf("email" to email, "password" to "wrong-password")
            )
        }.andExpect {
            status { isUnauthorized() }
        }
    }

    /** Helper: registers the test user and returns the response body as a map. */
    private fun register(): Map<*, *> {
        val result = mockMvc.post("/auth/register") {
            contentType = MediaType.APPLICATION_JSON
            content = objectMapper.writeValueAsString(
                mapOf("email" to email, "displayName" to displayName, "password" to password)
            )
        }.andReturn()
        return objectMapper.readValue(result.response.contentAsString, Map::class.java)
    }
}
