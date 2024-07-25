package org.example.githubapirestclient.response;

import lombok.Value;
import org.example.githubapirestclient.model.BranchInfo;

import java.util.List;

@Value
public class RepoDetailsResponse {
    String repositoryName;
    String ownerLogin;
    List<BranchInfo> branches;
}
