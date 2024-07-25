package org.example.githubapirestclient.service;

import lombok.RequiredArgsConstructor;
import org.example.githubapirestclient.exception.UserNotFoundException;
import org.example.githubapirestclient.model.BranchInfo;
import org.example.githubapirestclient.model.Repository;
import org.example.githubapirestclient.response.RepoDetailsResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class GitHubServiceImpl implements GitHubService {

    private final WebClient webClient;

    public GitHubServiceImpl(WebClient githubWebClient) {
        this.webClient = githubWebClient;
    }

    @Override
    public Flux<RepoDetailsResponse> getRepoInfo(String username) {
        return webClient.get()
                .uri("/users/{username}/repos", username)
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, response -> {
                    if (response.statusCode() == HttpStatus.NOT_FOUND) {
                        return Mono.error(new UserNotFoundException("User not found"));
                    }
                    return Mono.error(new RuntimeException("Client error"));
                })
                .bodyToFlux(Repository.class)
                .filter(repo -> !repo.isFork())
                .flatMap(repo -> getBranches(username, repo.getName())
                        .collectList()
                        .map(branches -> new RepoDetailsResponse(repo.getName(), repo.getOwner().getLogin(), branches)));
    }

    private Flux<BranchInfo> getBranches(String username, String repoName) {
        return webClient.get()
                .uri("/repos/{username}/{repoName}/branches", username, repoName)
                .retrieve()
                .bodyToFlux(BranchInfo.class);
    }
}