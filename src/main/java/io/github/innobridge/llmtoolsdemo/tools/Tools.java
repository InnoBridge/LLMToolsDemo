package io.github.innobridge.llmtoolsdemo.tools;

import java.util.List;

import io.github.innobridge.llmtools.models.request.ChatRequest;
import io.github.innobridge.llmtools.models.request.Tool;
import io.github.innobridge.llmtools.models.response.ChatResponse;
import io.github.innobridge.llmtools.models.response.ModelAndSize;
import io.github.innobridge.llmtools.models.response.SortOrder;

public interface Tools {
    boolean isToolsSupported(String modelName);

    List<String> getToolSupportingModels();

    List<ModelAndSize> getToolSupportingModelResponses(SortOrder sortOrder);

    ChatResponse functionCall(ChatRequest chatRequest, List<Tool> tools);
}
