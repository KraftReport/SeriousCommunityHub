package com.communityHubSystem.communityHub.services;

import com.communityHubSystem.communityHub.models.Mention;

public interface MentionService {

    public void save(Mention mention);

   public   Mention getMention(Long id);
}
