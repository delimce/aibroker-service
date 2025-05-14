package com.delimce.aibroker.infrastructure.controllers.llm;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.delimce.aibroker.application.llm.LlmChatService;
import com.delimce.aibroker.domain.dto.ApiResponse;
import com.delimce.aibroker.domain.dto.requests.llm.ModelRequest;
import com.delimce.aibroker.domain.ports.LoggerInterface;
import com.delimce.aibroker.infrastructure.controllers.BaseController;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/llm")
public class ChatRequestController extends BaseController {

    private final LlmChatService llmChatService;

    public ChatRequestController(LlmChatService llmChatService, LoggerInterface logger) {
        super(logger);
        this.llmChatService = llmChatService;
    }

    @PostMapping("/chat")
    public ResponseEntity<ApiResponse> chatRequest(@Valid @RequestBody ModelRequest request) {
        try {
            return ResponseEntity.ok(responseOk(llmChatService.execute(request)));
        } catch (IllegalArgumentException e) {
            return illegalArgumentExceptionResponse(e);
        } catch (Exception e) {
            return unhandledExceptionResponse(e);
        }
    }
}
