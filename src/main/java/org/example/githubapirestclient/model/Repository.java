package org.example.githubapirestclient.model;

import lombok.Data;

public record Repository(String name, boolean fork, Owner owner) {

    public record Owner(String login) {}
}