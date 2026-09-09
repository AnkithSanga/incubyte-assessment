package com.incubyte.salary.web.dto;

import java.util.Map;

public record AssistantQueryResponse(
    String query,
    String answer,
    String category,
    Map<String, Object> dataPoints
) {}
