package org.example.githubapirestclient.controller;

import lombok.RequiredArgsConstructor;
import org.example.githubapirestclient.response.RepoDetailsResponse;
import org.example.githubapirestclient.service.GitHubService;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.reactive.function.UnsupportedMediaTypeException;
import reactor.core.publisher.Flux;

@RestController
@RequestMapping("/api/github")
@RequiredArgsConstructor
public class GitHubController {

    private final GitHubService gitHubService;

    @GetMapping(value = "/repos/{username}", produces = MediaType.APPLICATION_JSON_VALUE)
    public Flux<RepoDetailsResponse> getRepoInfo(@PathVariable String username,
                                                 @RequestHeader("Accept") String acceptHeader) {
        if (!acceptHeader.equals(MediaType.APPLICATION_JSON_VALUE)) {
            throw new UnsupportedMediaTypeException("Only application/json is supported");
        }
        return gitHubService.getRepoInfo(username);
    }
}
