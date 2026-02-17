package com.connect.pairr.controller;

import com.connect.pairr.model.dto.ConversationResponse;
import com.connect.pairr.model.dto.MessageResponse;
import com.connect.pairr.model.dto.SendMessageRequest;
import com.connect.pairr.service.ChatService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "Chat", description = "1:1 messaging — send messages, list conversations, view history")
public class ChatController {

    private final ChatService chatService;

    @PostMapping("/messages")
    @Operation(summary = "Send a message", description = "Sends a message to another user. Creates a conversation if one doesn't exist.")
    public ResponseEntity<MessageResponse> sendMessage(
            @AuthenticationPrincipal UUID userId,
            @RequestBody @Valid SendMessageRequest request
    ) {
        return ResponseEntity.ok(chatService.sendMessage(userId, request));
    }

    @GetMapping("/conversations")
    @Operation(summary = "List your conversations", description = "Returns all conversations with last message preview")
    public ResponseEntity<List<ConversationResponse>> getConversations(
            @AuthenticationPrincipal UUID userId
    ) {
        return ResponseEntity.ok(chatService.getConversations(userId));
    }

    @GetMapping("/conversations/{conversationId}/messages")
    @Operation(summary = "Get message history", description = "Returns all messages in a conversation, ordered by time. You must be a participant.")
    public ResponseEntity<List<MessageResponse>> getMessages(
            @AuthenticationPrincipal UUID userId,
            @PathVariable UUID conversationId
    ) {
        return ResponseEntity.ok(chatService.getMessages(userId, conversationId));
    }
}
