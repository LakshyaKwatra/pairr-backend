package com.connect.pairr.controller;

import com.connect.pairr.model.dto.ConversationResponse;
import com.connect.pairr.model.dto.MessageResponse;
import com.connect.pairr.model.dto.SendMessageRequest;
import com.connect.pairr.service.ChatService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/chat")
@RequiredArgsConstructor
public class ChatController {

    private final ChatService chatService;

    @PostMapping("/messages")
    public ResponseEntity<MessageResponse> sendMessage(
            @AuthenticationPrincipal UUID userId,
            @RequestBody @Valid SendMessageRequest request
    ) {
        return ResponseEntity.ok(chatService.sendMessage(userId, request));
    }

    @GetMapping("/conversations")
    public ResponseEntity<List<ConversationResponse>> getConversations(
            @AuthenticationPrincipal UUID userId
    ) {
        return ResponseEntity.ok(chatService.getConversations(userId));
    }

    @GetMapping("/conversations/{conversationId}/messages")
    public ResponseEntity<List<MessageResponse>> getMessages(
            @AuthenticationPrincipal UUID userId,
            @PathVariable UUID conversationId
    ) {
        return ResponseEntity.ok(chatService.getMessages(userId, conversationId));
    }
}
