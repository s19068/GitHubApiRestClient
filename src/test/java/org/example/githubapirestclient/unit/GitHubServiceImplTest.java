package org.example.githubapirestclient.unit;

import org.example.githubapirestclient.exception.UserNotFoundException;
import org.example.githubapirestclient.model.BranchInfo;
import org.example.githubapirestclient.model.Repository;
import org.example.githubapirestclient.response.RepoDetailsResponse;
import org.example.githubapirestclient.service.GitHubServiceImpl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class GitHubServiceImplTest {

    @Mock
    private WebClient webClient;

    private GitHubServiceImpl gitHubService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        gitHubService = new GitHubServiceImpl(webClient);
    }

    @SuppressWarnings("unchecked")
    @Test
    void getRepoInfo_shouldReturnFilteredRepositories() {
        // Given
        String username = "testuser";
        Repository repo1 = new Repository("repo1", false, new Repository.Owner("testuser"));
        Repository repo2 = new Repository("repo2", true, new Repository.Owner("testuser"));
        Repository repo3 = new Repository("repo3", false, new Repository.Owner("testuser"));

        BranchInfo branch1 = new BranchInfo("main", new BranchInfo.Commit("abc123"));
        BranchInfo branch2 = new BranchInfo("develop", new BranchInfo.Commit("def456"));

        // Mock dla WebClient
        WebClient.RequestHeadersUriSpec requestHeadersUriSpec = mock(WebClient.RequestHeadersUriSpec.class);
        WebClient.RequestHeadersSpec requestHeadersSpec = mock(WebClient.RequestHeadersSpec.class);
        WebClient.ResponseSpec responseSpec = mock(WebClient.ResponseSpec.class);

        when(webClient.get()).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.uri(anyString(), any(Object[].class))).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.onStatus(any(), any())).thenReturn(responseSpec);

        // Mock dla zapytania o repozytoria
        when(responseSpec.bodyToFlux(Repository.class)).thenReturn(Flux.just(repo1, repo2, repo3));

        // Mock dla zapytań o branche
        when(responseSpec.bodyToFlux(BranchInfo.class))
                .thenReturn(Flux.just(branch1), Flux.just(branch2));

        Flux<RepoDetailsResponse> result = gitHubService.getRepoInfo(username);

        StepVerifier.create(result.sort((r1, r2) -> r1.repositoryName().compareTo(r2.repositoryName())))
                .expectNext(new RepoDetailsResponse("repo1", "testuser", List.of(branch1)))
                .expectNext(new RepoDetailsResponse("repo3", "testuser", List.of(branch2)))
                .verifyComplete();
    }

    @Test
    void getRepoInfo_shouldHandleUserNotFoundException() {
        WebClient webClient = mock(WebClient.class);
        WebClient.RequestHeadersUriSpec requestHeadersUriSpec = mock(WebClient.RequestHeadersUriSpec.class);
        WebClient.RequestHeadersSpec requestHeadersSpec = mock(WebClient.RequestHeadersSpec.class);
        WebClient.ResponseSpec responseSpec = mock(WebClient.ResponseSpec.class);

        GitHubServiceImpl gitHubService = new GitHubServiceImpl(webClient);

        String username = "nonexistentuser";

        when(webClient.get()).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.uri(anyString(), any(Object[].class))).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.onStatus(any(), any())).thenReturn(responseSpec);

        doAnswer(invocation -> {
            return Flux.error(new UserNotFoundException("User not found"));
        }).when(responseSpec).bodyToFlux(Repository.class);

        Flux<RepoDetailsResponse> result = gitHubService.getRepoInfo(username);

        StepVerifier.create(result)
                .expectError(UserNotFoundException.class)
                .verify();
    }
}