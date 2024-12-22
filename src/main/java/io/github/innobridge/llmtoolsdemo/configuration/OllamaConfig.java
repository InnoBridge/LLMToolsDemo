package io.github.innobridge.llmtoolsdemo.configuration;

import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

import io.github.innobridge.llmtools.client.OllamaClient;
import io.github.innobridge.llmtools.client.OllamaClientImpl;
import io.github.innobridge.llmtools.controller.OllamaController;
import io.github.innobridge.llmtoolsdemo.function.BraveSearchService;
import io.github.innobridge.llmtoolsdemo.function.WeatherService;
import io.github.innobridge.llmtoolsdemo.tools.OllamaTools;
import io.github.innobridge.llmtoolsdemo.tools.Tools;

@Configuration
public class OllamaConfig {
    
    @Bean
    public OllamaClient ollamaClient(
            @Value("${ollama.baseurl}") String baseUrl
    ) {
        WebClient webClient = WebClient.builder()
                .baseUrl(baseUrl)
                .build();
                
        return new OllamaClientImpl(webClient);
    }

    @Bean
    public OllamaController ollamaController(OllamaClient ollamaClient) {
        return new OllamaController(ollamaClient);
    }

    @Bean
    public WeatherService weatherService(
        @Value("${weather.api.baseurl}") String baseUrl,
        @Value("${weather.api.key}") String apiKey
    ) {
        WebClient weatherClient = WebClient.builder()
                .baseUrl(baseUrl)
                .build();
        return new WeatherService(weatherClient, apiKey);
    }

    @Bean
    public BraveSearchService braveSearchService(
        @Value("${bravesearch.api.baseurl}") String baseUrl,
        @Value("${bravesearch.api.key}") String apiKey
    ) {
        WebClient braveSearchClient = WebClient.builder()
                .baseUrl(baseUrl)
                .build();
        return new BraveSearchService(apiKey, braveSearchClient);
    }

    @Bean
    public Tools ollamaTools(OllamaClient ollamaClient, 
                             WeatherService weatherService, 
                             BraveSearchService braveSearchService) {
        return new OllamaTools(ollamaClient, List.of(weatherService, braveSearchService));
    }

}
