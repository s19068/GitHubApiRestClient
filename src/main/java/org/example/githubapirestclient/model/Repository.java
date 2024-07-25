package org.example.githubapirestclient.model;

import lombok.Data;

@Data
public class Repository {
    private String name;
    private boolean fork;
    private Owner owner;

    @Data
    public static class Owner {
        private String login;
    }
}
