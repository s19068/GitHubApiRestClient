package org.example.githubapirestclient.integration.controller.service;

import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.junit5.WireMockExtension;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.reactive.server.WebTestClient;
import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;
import static org.hamcrest.Matchers.hasSize;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWebTestClient
class GitHubServiceIntegrationTest {

    @Autowired
    private WebTestClient webTestClient;

    @RegisterExtension
    static WireMockExtension wireMockServer = WireMockExtension.newInstance()
            .options(wireMockConfig().dynamicPort())
            .build();

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("github.api.base-url", wireMockServer::baseUrl);
    }

    @Test
    void shouldGetGithubRepoInfo() {
        String username = "testuser";

        // Stub dla listy repozytoriów
        wireMockServer.stubFor(WireMock.get(urlEqualTo("/users/" + username + "/repos"))
                .willReturn(aResponse()
                        .withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                        .withBody("""
                        [
                            {
                                "name": "repo1",
                                "fork": false,
                                "owner": {"login": "testuser"}
                            },
                            {
                                "name": "repo2",
                                "fork": true,
                                "owner": {"login": "testuser"}
                            }
                        ]
                        """)));

        // Stub dla branchy repo1
        wireMockServer.stubFor(WireMock.get(urlEqualTo("/repos/" + username + "/repo1/branches"))
                .willReturn(aResponse()
                        .withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                        .withBody("""
                        [
                            {
                                "name": "main",
                                "commit": {"sha": "abc123"}
                            }
                        ]
                        """)));

        webTestClient.get().uri("/api/github/repos/{username}", username)
                .header("Accept", MediaType.APPLICATION_JSON_VALUE)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$").isArray()
                .jsonPath("$").value(hasSize(1))
                .jsonPath("$[0].repositoryName").isEqualTo("repo1")
                .jsonPath("$[0].ownerLogin").isEqualTo("testuser")
                .jsonPath("$[0].branches").isArray()
                .jsonPath("$[0].branches").value(hasSize(1))
                .jsonPath("$[0].branches[0].name").isEqualTo("main")
                .jsonPath("$[0].branches[0].commit.sha").isEqualTo("abc123");
    }

    @Test
    void shouldHandleUserNotFoundError() {
        String username = "nonexistentuser";

        wireMockServer.stubFor(WireMock.get(urlEqualTo("/users/" + username + "/repos"))
                .willReturn(aResponse().withStatus(404)));

        webTestClient.get().uri("/api/github/repos/{username}", username)
                .header("Accept", MediaType.APPLICATION_JSON_VALUE)
                .exchange()
                .expectStatus().isNotFound();
    }

    @Test
    void shouldHandleUnsupportedMediaType() {
        String username = "testuser";

        webTestClient.get().uri("/api/github/repos/{username}", username)
                .header("Accept", MediaType.APPLICATION_XML_VALUE)
                .exchange()
                .expectStatus().isEqualTo(500);
    }
}