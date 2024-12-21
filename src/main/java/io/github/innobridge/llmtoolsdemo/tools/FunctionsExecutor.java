package io.github.innobridge.llmtoolsdemo.tools;

import java.nio.channels.Pipe.SourceChannel;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

import io.github.innobridge.llmtools.exceptions.LLMFunctionException;
import io.github.innobridge.llmtools.models.response.ToolCallFunction;
import io.github.innobridge.llmtoolsdemo.function.LLMFunction;

// @Slf4j
@SuppressWarnings("rawtypes")
public class FunctionsExecutor {
    private List<ToolCallFunction> functionCalls;
    private final Map<String, LLMFunction> functionRepository;

    public FunctionsExecutor(List<ToolCallFunction> functionCalls, Map<String, LLMFunction> functionRepository) {
        this.functionCalls = functionCalls;
        this.functionRepository = functionRepository;
    }

    public Optional<?> executeFirst(Class clazz) {
        Optional<List<ToolCallFunction>> toolCallFunctions = getToolCallFunctions(clazz);
        if (toolCallFunctions.isEmpty()) {
            return Optional.empty();
        }
        List<ToolCallFunction> filteredCalls = toolCallFunctions.get();
        if (filteredCalls.isEmpty()) {
            return Optional.empty();
        }
        ToolCallFunction firstCall = filteredCalls.getFirst();

        LLMFunction<?, ?> function = functionRepository.get(FunctionConverter.getAnnotatedName(clazz));

        try {
            return Optional.of(function.apply(firstCall.getArguments()));
        } catch (LLMFunctionException e) {
            // Log the specific error if needed
            return Optional.empty();
        } catch (Exception e) {
            e.printStackTrace();
            return Optional.empty();
        }
    }

    private Optional<List<ToolCallFunction>> getToolCallFunctions(Class clazz) {
        String name = FunctionConverter.getAnnotatedName(clazz);
        if (functionCalls == null 
            || functionCalls.isEmpty() 
            || !functionRepository.containsKey(name)) {
            return Optional.empty();
        }
        
        List<ToolCallFunction> filteredCalls = functionCalls.stream()
            .filter(call -> call.getName().equals(name))
            .collect(Collectors.toList());
        if (filteredCalls.isEmpty()) {
            return Optional.empty();
        }
        return Optional.of(filteredCalls);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("FunctionsExecutor[\n");
        
        // Format function calls
        sb.append("  Function Calls:\n");
        if (functionCalls != null && !functionCalls.isEmpty()) {
            for (ToolCallFunction call : functionCalls) {
                sb.append("    - Name: ").append(call.getName())
                  .append(", Index: ").append(call.getIndex())
                  .append(", Arguments: ").append(call.getArguments())
                  .append("\n");
            }
        } else {
            sb.append("    (none)\n");
        }
        
        // Format function repository
        sb.append("  Function Repository:\n");
        if (functionRepository != null && !functionRepository.isEmpty()) {
            functionRepository.forEach((name, func) -> {
                sb.append("    - ").append(name)
                  .append(": ").append(func.getClass().getSimpleName())
                  .append("\n");
            });
        } else {
            sb.append("    (empty)\n");
        }
        
        sb.append("]");
        return sb.toString();
    }
    
}
