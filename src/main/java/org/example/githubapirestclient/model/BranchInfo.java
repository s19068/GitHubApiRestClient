package org.example.githubapirestclient.model;

import lombok.Data;

@Data
public class BranchInfo {
    private String name;
    private Commit commit;

    @Data
    public static class Commit {
        private String sha;
    }
}