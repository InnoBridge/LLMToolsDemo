package io.github.innobridge.llmtoolsdemo.tools;

import java.util.List;
import java.util.Map;
import java.util.function.Function;

import io.github.innobridge.llmtools.models.response.ToolCallFunction;

@SuppressWarnings("rawtypes")
public class FunctionExecutor {
    private List<ToolCallFunction> functionCalls;
    private final Map<String, Function> functionRepository;

    public FunctionExecutor(List<ToolCallFunction> functionCalls, Map<String, Function> functionRepository) {
        this.functionCalls = functionCalls;
        this.functionRepository = functionRepository;
    }


    
}
