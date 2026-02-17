package com.connect.pairr.service;

import com.connect.pairr.exception.*;
import com.connect.pairr.mapper.RatingMapper;
import com.connect.pairr.model.dto.AddRatingRequest;
import com.connect.pairr.model.dto.RatingResponse;
import com.connect.pairr.model.entity.Rating;
import com.connect.pairr.model.entity.Skill;
import com.connect.pairr.model.entity.User;
import com.connect.pairr.model.entity.UserSkill;
import com.connect.pairr.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RatingService {

    private final RatingRepository ratingRepository;
    private final UserRepository userRepository;
    private final SkillRepository skillRepository;
    private final UserSkillRepository userSkillRepository;

    @Transactional
    public RatingResponse submitRating(UUID fromUserId, AddRatingRequest request) {

        if (fromUserId.equals(request.toUserId())) {
            throw new SelfRatingException();
        }

        User fromUser = userRepository.findById(fromUserId)
                .orElseThrow(() -> new UserNotFoundException(fromUserId));

        User toUser = userRepository.findById(request.toUserId())
                .orElseThrow(() -> new UserNotFoundException(request.toUserId()));

        Skill skill = skillRepository.findById(request.skillId())
                .orElseThrow(() -> new SkillNotFoundException(request.skillId()));

        // Verify both users have the skill
        userSkillRepository.findByUserIdAndSkillId(fromUserId, request.skillId())
                .orElseThrow(() -> new RequesterSkillMissingException(request.skillId()));

        UserSkill toUserSkill = userSkillRepository.findByUserIdAndSkillId(request.toUserId(), request.skillId())
                .orElseThrow(() -> new SkillNotFoundException(request.skillId()));

        if (ratingRepository.existsByFromUserIdAndToUserIdAndSkillId(fromUserId, request.toUserId(), request.skillId())) {
            throw new DuplicateRatingException(request.toUserId(), request.skillId());
        }

        Rating rating = RatingMapper.toEntity(request, fromUser, toUser, skill);
        rating = ratingRepository.save(rating);

        // Recalculate per-skill rating for the rated user
        toUserSkill.setRating(ratingRepository.averageRatingByToUserIdAndSkillId(request.toUserId(), request.skillId()));
        userSkillRepository.save(toUserSkill);

        // Recalculate overall rating for the rated user
        toUser.setOverallRating(ratingRepository.averageRatingByToUserId(request.toUserId()));
        userRepository.save(toUser);

        return RatingMapper.toResponse(rating);
    }

    public List<RatingResponse> getRatingsForUser(UUID userId) {
        return ratingRepository.findAllByToUserId(userId).stream()
                .map(RatingMapper::toResponse)
                .toList();
    }

    public List<RatingResponse> getRatingsForUserSkill(UUID userId, UUID skillId) {
        return ratingRepository.findAllByToUserIdAndSkillId(userId, skillId).stream()
                .map(RatingMapper::toResponse)
                .toList();
    }
}
