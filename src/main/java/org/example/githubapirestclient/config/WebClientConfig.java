package org.example.githubapirestclient.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfig {

    @Value("${github.api.base-url}")
    private String githubApiBaseUrl;

    @Value("${github.api.token}")
    private String githubApiToken;

    @Bean
    public WebClient githubWebClient() {
        return WebClient.builder()
                .baseUrl(githubApiBaseUrl)
                .defaultHeader("Authorization", "token " + githubApiToken)
                .defaultHeader("Accept", "application/vnd.github.v3+json")
                .build();
    }
}
