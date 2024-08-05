package org.example.githubapirestclient.model;


public record BranchInfo(String name, Commit commit) {
    public record Commit(String sha) {}
}