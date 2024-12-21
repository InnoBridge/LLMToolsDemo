package io.github.innobridge.llmtoolsdemo.function;

import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriComponentsBuilder;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import io.github.innobridge.llmtools.exceptions.LLMFunctionException;
import io.github.innobridge.llmtoolsdemo.tools.FunctionDefinition;

@Service
@FunctionDefinition(
    name = "get_current_weather",
    description = "Get the current weather in a given location"
)
public class WeatherService implements LLMFunction<WeatherService.Request, String> {
    
    private final WebClient weatherClient;
    private final String apiKey;
    
    public WeatherService(WebClient weatherClient, String apiKey) {
        this.weatherClient = weatherClient;
        this.apiKey = apiKey;
    }

    @Override
    public Request fromArguments(Map<String, Object> arguments) {
        try {
            String location = (String) arguments.get("location");
            if (location == null || location.trim().isEmpty()) {
                throw new LLMFunctionException("Location is required");
            }
            Format format = Format.CELSIUS;  // Default format
            String formatStr = (String) arguments.get("format");
            if (formatStr != null && !formatStr.trim().isEmpty() && 
                formatStr.toLowerCase().equals("fahrenheit")) {
                format = Format.FAHRENHEIT;
            }
    
            return new Request(location, format);
        } catch (ClassCastException e) {
            throw new LLMFunctionException("Invalid argument type", e);
        }
    }

    @Override
    public String apply(Map<String, Object> arguments) {
        return apply(fromArguments(arguments));
    }

    @Override
    public String apply(Request request) {
        if (request.location() == null || request.location().trim().isEmpty()) {
            return "Error: Location parameter is required";
        }

        try {
            String uri = UriComponentsBuilder.fromPath("/current.json")
                    .queryParam("key", apiKey)
                    .queryParam("q", request.location())
                    .queryParam("aqi", "no")
                    .build()
                    .toUriString();

            WeatherResponse response = weatherClient.post()
                    .uri(uri)
                    .header("Content-Type", "application/json")
                    .bodyValue("")  // Empty body for POST request
                    .retrieve()
                    .bodyToMono(WeatherResponse.class)
                    .block();

            double temp = request.format() == Format.CELSIUS ? 
                    response.current().temp_c() : 
                    response.current().temp_f();
            String unit = request.format() == Format.CELSIUS ? "°C" : "°F";
            
            return String.format("Current weather in %s: %.1f%s, %s", 
                request.location(),
                temp,
                unit,
                response.current().condition().text());
        } catch (Exception e) {
            e.printStackTrace();
            return "Error fetching weather data: " + e.getMessage();
        }
    }

    public enum Format {
        CELSIUS("celsius"), 
        FAHRENHEIT("fahrenheit");

        public final String formatName;

        Format(String formatName) {
            this.formatName = formatName;
        }

        @Override
        public String toString() {
            return formatName;
        }
    }

    @JsonInclude(Include.NON_NULL)
    public record Request(
        @JsonProperty(required = true) 
        @JsonPropertyDescription("The city and state e.g. San Francisco, CA") 
        String location,
        @JsonProperty(required = true) 
        @JsonPropertyDescription("The format to return the weather in, e.g. 'celsius' or 'fahrenheit'") 
        Format format) {
    }
    
    private record WeatherResponse(
        Current current
    ) {
        private record Current(
            double temp_c,
            double temp_f,
            Condition condition
        ) {}

        private record Condition(
            String text
        ) {}
    }
}
