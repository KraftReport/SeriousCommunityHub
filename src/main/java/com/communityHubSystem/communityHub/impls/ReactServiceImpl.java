package com.communityHubSystem.communityHub.impls;

import com.communityHubSystem.communityHub.models.React;
import com.communityHubSystem.communityHub.models.Type;
import com.communityHubSystem.communityHub.repositories.ReactRepository;
import com.communityHubSystem.communityHub.services.ReactService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ReactServiceImpl implements ReactService {

    private final ReactRepository reactRepository;

    @Transactional
    @Override
    public React save(React react) {
        return reactRepository.save(react);
    }

    @Override
    public boolean findByPostIdAndUserId(Long id, Long id1) {
        var react = reactRepository.findReactByUserIdAndPostIdAndCommentIdAndReplyId(id1,id,null,null);
        if(react != null){
            return true;
        }
        return false;
    }

    @Override
    public List<React> findByPostId(Long id) {
        return reactRepository.findByPostId(id);
    }

    @Override
    public React findReactByPostIdAndUserId(Long id, Long id1) {
        return reactRepository.findReactByUserIdAndPostIdAndCommentIdAndReplyId(id1,id,null,null);
    }

    @Transactional
    @Override
    public void updatedReact(Long id, Type type) {
         reactRepository.findById(id).ifPresent(r -> r.setType(type));
    }

    @Transactional
    @Override
    public void removeReactType(Long id) {
        reactRepository.findById(id).ifPresent(r -> r.setType(Type.OTHER));
    }

    @Override
    public React findById(Long id) {
        return reactRepository.findById(id).orElse(null);
    }

    @Override
    public React findReactByUserIdAndCommentId(Long id, Long commentId,Long postId) {
        return reactRepository.findReactByUserIdAndPostIdAndCommentIdAndReplyId(id,postId,commentId,null);
    }

    @Transactional
    @Override
    public void modifyReact(Long id, Type type) {
        reactRepository.findById(id).ifPresent(r -> r.setType(type));
    }

    @Override
    public React findReactByUserIdAndPostIdAndCommentId(Long userId, Long postId, Long commentId) {
        return reactRepository.findReactByUserIdAndPostIdAndCommentIdAndReplyId(userId,postId,commentId,null);
    }

    @Override
    public React getReact(Long id, Long id1, Long id2, Long id3) {
        return reactRepository.findReactByUserIdAndPostIdAndCommentIdAndReplyId(id,id1,id2,id3);
    }
}
