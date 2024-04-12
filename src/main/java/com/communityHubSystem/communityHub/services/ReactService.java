package com.communityHubSystem.communityHub.services;

import com.communityHubSystem.communityHub.models.React;

import java.util.List;

public interface ReactService {

    public React save(React react);

    public boolean findByPostIdAndUserId(Long id, Long id1);

    public List<React> findByPostId(Long id);
}
