package com.communityHubSystem.communityHub.impls;

import com.communityHubSystem.communityHub.dto.CommentUpdateDto;
import com.communityHubSystem.communityHub.models.Reply;
import com.communityHubSystem.communityHub.repositories.ReplyRepository;
import com.communityHubSystem.communityHub.services.NotificationService;
import com.communityHubSystem.communityHub.services.ReplyService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

    @Override
    public Reply findById(Long replyId) {
        return replyRepository.findById(replyId).orElse(null);
    }

    @Transactional
    @Override
    public void deleteReply(Long id) {
        replyRepository.deleteById(id);
    }

    @Transactional
    @Override
    public void updatedReply(CommentUpdateDto commentUpdateDto) {
        replyRepository.findById(commentUpdateDto.getId()).ifPresent(r -> r.setContent(commentUpdateDto.getContent()));
    }
}
