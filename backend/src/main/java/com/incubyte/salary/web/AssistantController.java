package com.incubyte.salary.web;

import com.incubyte.salary.service.AssistantService;
import com.incubyte.salary.web.dto.AssistantQueryRequest;
import com.incubyte.salary.web.dto.AssistantQueryResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/assistant")
@Tag(name = "Compensation Assistant", description = "Conversational AI/deterministic query interface for HR compensation questions")
public class AssistantController {

    private final AssistantService assistantService;

    public AssistantController(AssistantService assistantService) {
        this.assistantService = assistantService;
    }

    @PostMapping("/query")
    @Operation(summary = "Ask a compensation question", description = "Submit a natural-language question about org pay (e.g. median salary, department comparison)")
    public AssistantQueryResponse askQuestion(@RequestBody AssistantQueryRequest request) {
        return assistantService.answerQuestion(request != null ? request.question() : "");
    }
}
