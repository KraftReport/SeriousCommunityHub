package com.communityHubSystem.communityHub.impls;

import com.communityHubSystem.communityHub.dto.CommentUpdateDto;
import com.communityHubSystem.communityHub.models.Comment;
import com.communityHubSystem.communityHub.models.Reply;
import com.communityHubSystem.communityHub.repositories.CommentRepository;
import com.communityHubSystem.communityHub.services.CommentService;
import com.communityHubSystem.communityHub.services.ReplyService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService {

    private final CommentRepository commentRepository;
    private final ReplyService replyService;

    @Override
    public Comment save(Comment comment) {
        return commentRepository.save(comment);
    }

    @Override
    public Comment findByPostId(Long id) {
        return commentRepository.findByPostId(id).orElse(null);
    }

    @Override
    public List<Comment> findCommentsByPostId(Long id) {
        return commentRepository.findAllByPostId(id);
    }

    @Override
    public Comment findById(Long id) {
        return commentRepository.findById(id).orElse(null);
    }

    @Override
    public void deleteComment(Long id) {
      List<Reply> replyList = replyService.getAllRepliesByCommentId(id);
      if(replyList.size() > 0) {
          for (Reply reply : replyList) {
              replyService.deleteReply(reply.getId());
          }
      }
      commentRepository.deleteById(id);
    }

    @Transactional
    @Override
    public void updateComment(CommentUpdateDto commentUpdateDto) {
       commentRepository.findById(commentUpdateDto.getId()).ifPresent(c -> c.setContent(commentUpdateDto.getContent()));
    }

}
