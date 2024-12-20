package io.github.innobridge.llmtoolsdemo.function;

import java.util.Map;
import java.util.function.Function;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import io.github.innobridge.llmtoolsdemo.tools.FunctionDefinition;

@FunctionDefinition(
    name = "get_current_weather",
    description = "Get the current weather in a given location"
)
public class WeatherService implements Function<WeatherService.Request, String> {
    @Override
    public String apply(Request request) {
        // Extract location from parameters
        String location = (String) request.location();
        if (location == null || location.trim().isEmpty()) {
            return "Error: Location parameter is required";
        }

        // For demonstration purposes, return a mock weather response
        // In a real implementation, you would make an API call to a weather service
        return String.format("Current weather in %s: 72°F, Partly Cloudy", location);
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

    /**
	 * Weather Function request.
	 */
	@JsonInclude(Include.NON_NULL)
	public record Request(
        @JsonProperty(required = true) 
        @JsonPropertyDescription("The city and state e.g. San Francisco, CA") 
        String location,
		@JsonProperty(required = true) 
        @JsonPropertyDescription("The format to return the weather in, e.g. 'celsius' or 'fahrenheit'") 
        Format format) {
	}
}
