package com.communityHubSystem.communityHub.impls;

import com.communityHubSystem.communityHub.models.Reply;
import com.communityHubSystem.communityHub.repositories.ReplyRepository;
import com.communityHubSystem.communityHub.services.ReplyService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ReplyServiceImpl implements ReplyService {

    private final ReplyRepository replyRepository;

    @Override
    public Reply save(Reply reply) {
        return replyRepository.save(reply);
    }

    @Override
    public List<Reply> getAllRepliesByCommentId(Long id) {
        return replyRepository.findAllByCommentId(id);
    }
}
