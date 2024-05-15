package com.communityHubSystem.communityHub.services;

import com.communityHubSystem.communityHub.models.React;
import com.communityHubSystem.communityHub.models.Type;

import java.util.List;

public interface ReactService {

    public React save(React react);

    public boolean findByPostIdAndUserId(Long id, Long id1);

    public List<React> findByPostId(Long id);

    public React findReactByPostIdAndUserId(Long id, Long id1);

   public void updatedReact(Long id, Type type);

    void removeReactType(Long id);

    public React findById(Long id);

    public React findReactByUserIdAndCommentId(Long id, Long commentId,Long postId);

   public void modifyReact(Long id, Type type);

  public React findReactByUserIdAndPostIdAndCommentId(Long userId, Long postId, Long id);

   public React getReact(Long id, Long id1, Long id2, Long id3);

   public React findReactByCommentId(Long id);

   public void deleteById(Long id);

    public React findByReplyId(Long id);

    public List<React> findByEventId(Long id);

    public React findByUserIdAndEventId(Long id, Long id1);

  public    List<React> findByUserId(Long id);

    List<React> findReactByCommentIdAndPostId(Long id);

   public Long finReactByCommentIdIsNullAndPostIdAndReplyIdIsNull(Long id);
}
