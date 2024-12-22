package io.github.innobridge.llmtoolsdemo.function;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;

import io.github.innobridge.llmtools.exceptions.LLMFunctionException;
import io.github.innobridge.llmtoolsdemo.tools.FunctionDefinition;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;

@Service
@FunctionDefinition(
    name = "brave_search",
    description = "Search the web when data is not in training data"
)
public class BraveSearchService implements LLMFunction<BraveSearchService.Request, BraveSearchService.Response> {

    private final WebClient braveSearchClient;
    private final String apiKey;

    public BraveSearchService(String apiKey, WebClient braveSearchClient) {
        this.apiKey = apiKey;
        this.braveSearchClient = braveSearchClient;
    }

    @Override
    public Request fromArguments(Map<String, Object> arguments) {
        try {
            String query = (String) arguments.get("query");
            if (query == null || query.trim().isEmpty()) {
                throw new LLMFunctionException("Query is required");
            }
            return new Request(query);
        } catch (ClassCastException e) {
            throw new LLMFunctionException("Invalid argument type", e);
        }
    }

    @Override
    public Response apply(Map<String, Object> arguments) {
        return apply(fromArguments(arguments));    
    }

    @Override
    public Response apply(Request request) {
        if (request.query() == null || request.query().trim().isEmpty()) {
            throw new LLMFunctionException("Query is required");
        }

        try {
            return braveSearchClient.get()
                .uri(uriBuilder -> uriBuilder
                    .path("/res/v1/web/search")
                    .queryParam("q", request.query())
                    .queryParam("count", "3")
                    .build())
                .header("Accept", "application/json")
                .header("Accept-Encoding", "gzip")
                .header("X-Subscription-Token", apiKey)
                .retrieve()
                .bodyToMono(Response.class)
                .block();
        } catch (Exception e) {
            throw new LLMFunctionException("Failed to perform Brave search", e);
        }
    }

    @JsonInclude(Include.NON_NULL)
    record Request(
        @JsonProperty(required = true)
        @JsonPropertyDescription("The query to search for")
        String query
    ) {}

    record Response(
        @JsonProperty("web")
        WebResults web
    ) {
        record WebResults(
            @JsonProperty("results")
            List<WebResult> results
        ) {
            record WebResult(
                @JsonProperty("title")
                String title,
                @JsonProperty("url")
                String url,
                @JsonProperty("description")
                String description
            ) {}
        }
    }
}
