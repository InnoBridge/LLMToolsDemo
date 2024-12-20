package io.github.innobridge.llmtoolsdemo.controller;

import static io.github.innobridge.llmtools.constants.OllamaConstants.FUNCTION;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import io.github.innobridge.llmtools.client.OllamaClient;
import io.github.innobridge.llmtools.models.Message;
import io.github.innobridge.llmtools.models.request.ChatRequest;
import io.github.innobridge.llmtools.models.request.Tool;
import io.github.innobridge.llmtools.models.request.ToolFunction;
import io.github.innobridge.llmtools.models.response.ChatResponse;
import io.github.innobridge.llmtools.models.response.ModelAndSize;
import io.github.innobridge.llmtools.models.response.SortOrder;
import io.github.innobridge.llmtools.models.response.ToolCall;
import io.github.innobridge.llmtoolsdemo.function.WeatherService;
import io.github.innobridge.llmtoolsdemo.tools.FunctionConverter;
import io.github.innobridge.llmtoolsdemo.tools.Tools;

@RestController
@RequestMapping("/function")
public class FunctionController {

    @Autowired
    private OllamaClient ollamaClient;

    @Autowired
    private Tools ollamaTools;

    @GetMapping("/hello")
    public String hello() {
        var builder = ChatRequest.builder();
        builder.model("llama3.2");
        builder.messages(
            List.of(
                Message.builder()
                    .role("user")
                    .content("What is the weather today in Paris? and in New York?")
                    .build()
            )
        );
        builder.tools(
            List.of(
                new Tool(
                    FUNCTION,
                    new ToolFunction(
                        "getCurrentWeather",
                        "Get the current weather for a location",
                        ToolFunction.Parameters.builder("object")
                            .required(List.of("location", "format"))
                            .properties(
                                Map.of(
                                    "location", ToolFunction.Property.builder("string").description("The location to get the weather for, e.g. San Francisco, CA").build(),
                                    "format", ToolFunction.Property.builder("string").description("The format of the response, e.g. celsius or fahrenheit")
                                    .enumValues(List.of("celsius", "fahrenheit")).build()
                                )
                            ).build()
                        )
                    )
                )
        );
        builder.stream(false);

        return ollamaClient.chat(builder.build()).block().getMessage().getToolCalls().stream()
            .map(toolCall -> toolCall.getFunction().getName() + " : " + toolCall.getFunction().getArguments().toString() + "\n")
            .reduce("", String::concat);
    }

    @GetMapping("/hastool/{modelname}")
    public String hasTool(@PathVariable String modelname) {
        boolean hasTools = ollamaTools.isToolsSupported(modelname);
        
        
        return "Model: " + modelname + "\n" +
               "Has Tool Support: " + hasTools + "\n\n";    
    }

    @GetMapping("/toolmodels")
    public List<ModelAndSize> getToolModels(@RequestParam(required = false) SortOrder sortOrder) {
        return ollamaTools.getToolSupportingModelResponses(sortOrder);
    }

    @GetMapping("/functioncall")
    public ChatResponse functionCall(@RequestParam String model, @RequestParam String prompt) {

        var builder = ChatRequest.builder();
        builder.model(model);
        builder.messages(
            List.of(
                Message.builder()
                    .role("user")
                    .content(prompt) 
                    .build()
            )
        );
        builder.stream(false);

        ToolFunction toolFunction = FunctionConverter.convertToToolFunction(WeatherService.class);

        Tool tool = new Tool(
            FUNCTION,
            toolFunction
        );
        builder.tools(List.of(tool));
        return ollamaTools.functionCall(builder.build(), List.of(tool));        
    }

    @GetMapping("/convertToToolFunction")
    public ToolFunction convertToToolFunction() {
        return FunctionConverter.convertToToolFunction(WeatherService.class);
    }
}
