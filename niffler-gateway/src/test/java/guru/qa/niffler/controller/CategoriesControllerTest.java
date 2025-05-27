package guru.qa.niffler.controller;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.core.WireMockConfiguration;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@ActiveProfiles("test")
class CategoriesControllerTest {

    private final WireMockServer wireMockServer = new WireMockServer(
            new WireMockConfiguration()
                    .port(8093)
                    .globalTemplating(true)
    );

    @BeforeEach
    void beforeEach() {
        wireMockServer.start();
    }

    @AfterEach
    void afterEach() {
        wireMockServer.shutdown();
    }

    @Autowired
    private MockMvc mockMvc;

    @Test
    void categoriesListShouldBeReturnedForCurrentUser() throws Exception {

        final String fixtureUser = "duck";

        wireMockServer.stubFor(WireMock.get(WireMock.urlPathEqualTo("/internal/categories/all"))
                .withQueryParam("username", WireMock.equalTo(fixtureUser))
                .withQueryParam("excludeArchived", WireMock.equalTo("false"))
                .willReturn(WireMock.okJson("""
                        [{"id":"{{randomValue type='UUID'}}",
                        "name":"Веселье",
                        "username":"{{request.query.username}}",
                        "archived":false},
                        {"id":"{{randomValue type='UUID'}}",
                        "name":"Магазины",
                        "username":"{{request.query.username}}",
                        "archived":true}]
                        """)));


        mockMvc.perform(get("/api/categories/all")
                        .with(jwt().jwt(c -> c.claim("sub", fixtureUser)))
                        .param("excludeArchived", "false"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].username").value(fixtureUser))
                .andExpect(jsonPath("$[0].name").value("Веселье"))
                .andExpect(jsonPath("$[0].archived").value(false))
                .andExpect(jsonPath("$[1].username").value(fixtureUser))
                .andExpect(jsonPath("$[1].name").value("Магазины"))
                .andExpect(jsonPath("$[1].archived").value(true));
    }

}