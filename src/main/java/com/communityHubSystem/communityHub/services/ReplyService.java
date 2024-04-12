package com.communityHubSystem.communityHub.services;

import com.communityHubSystem.communityHub.models.Reply;

import java.util.List;

public interface ReplyService {

    public Reply save(Reply reply);

  public List<Reply> getAllRepliesByCommentId(Long id);
}
