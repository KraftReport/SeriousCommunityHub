package com.communityHubSystem.communityHub.repositories;

import com.communityHubSystem.communityHub.models.Invitation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface InvitationRepository extends JpaRepository<Invitation,Long> {

    List<Invitation> findByRecipientIdAndIsInvited(Long id, boolean isInvited);

    Invitation findByRecipientIdAndCommunityId(Long userId, Long id);

    List<Invitation> findByCommunityIdAndIsInvited(Long id, boolean b);

    List<Invitation> findByCommunityIdAndIsRemoved(Long id, boolean b);
}
