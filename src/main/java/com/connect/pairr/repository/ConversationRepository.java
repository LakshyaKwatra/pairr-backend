package com.connect.pairr.repository;

import com.connect.pairr.model.entity.Conversation;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ConversationRepository extends JpaRepository<Conversation, UUID> {

    @EntityGraph(attributePaths = {"participant1", "participant2"})
    Optional<Conversation> findByParticipant1IdAndParticipant2Id(UUID participant1Id, UUID participant2Id);

    @EntityGraph(attributePaths = {"participant1", "participant2"})
    @Query("""
        SELECT c FROM Conversation c
        WHERE c.participant1.id = :userId OR c.participant2.id = :userId
        ORDER BY c.lastMessageAt DESC NULLS LAST
    """)
    List<Conversation> findAllByParticipant(@Param("userId") UUID userId);
}
