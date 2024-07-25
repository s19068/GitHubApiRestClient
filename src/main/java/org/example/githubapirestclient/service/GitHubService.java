package org.example.githubapirestclient.service;

import org.example.githubapirestclient.response.RepoDetailsResponse;
import reactor.core.publisher.Flux;

public interface GitHubService {
    Flux<RepoDetailsResponse> getRepoInfo(String username);
}
