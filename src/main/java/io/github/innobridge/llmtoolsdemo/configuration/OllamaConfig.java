package io.github.innobridge.llmtoolsdemo.configuration;

import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

import io.github.innobridge.llmtools.client.OllamaClient;
import io.github.innobridge.llmtools.client.OllamaClientImpl;
import io.github.innobridge.llmtools.controller.OllamaController;
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
    public WeatherService weatherService() {
        return new WeatherService();
    }

    @Bean
    public Tools ollamaTools(OllamaClient ollamaClient, WeatherService weatherService) {
        return new OllamaTools(ollamaClient, List.of(weatherService));
    }
}
