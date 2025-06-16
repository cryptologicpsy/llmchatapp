package com.example.demo.services;

import java.time.Duration;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;

@Service
public class GPTService {

    private final WebClient webClient;
    private final AtomicInteger retryCount = new AtomicInteger(0);

    private final String apiKey = "Api_Key";

    public GPTService(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.baseUrl(
            "https://generativelanguage.googleapis.com/v1beta/models/gemini-2.0-flash:generateContent"
        ).build();
    }

    public Mono<String> askGPT(String prompt) {
        retryCount.set(0);

        GeminiRequest request = new GeminiRequest(
            Collections.singletonList(
                new ContentPart(Collections.singletonList(new ContentText(prompt)))
            )
        );

        return webClient.post()
                .uri(uriBuilder -> uriBuilder.queryParam("key", apiKey).build())
                .header("Content-Type", "application/json")
                .bodyValue(request)
                .retrieve()
                .bodyToMono(GeminiResponse.class)
                .map(response -> {
                    // Παίρνουμε το κείμενο από το πρώτο candidate, πρώτο part
                    if (response.candidates != null && !response.candidates.isEmpty()) {
                        GeminiContent content = response.candidates.get(0).content;
                        if (content != null && content.parts != null && !content.parts.isEmpty()) {
                            return content.parts.get(0).text;
                        }
                    }
                    return "No response from model.";
                })
                .retryWhen(Retry.fixedDelay(5, Duration.ofSeconds(1))
                        .filter(throwable -> throwable instanceof WebClientResponseException &&
                                ((WebClientResponseException) throwable).getStatusCode().value() == 429)
                        .doBeforeRetry(retrySignal -> {
                            retryCount.incrementAndGet();

                            Throwable failure = retrySignal.failure();
                            HttpHeaders headers = null;

                            if (failure instanceof WebClientResponseException) {
                                headers = ((WebClientResponseException) failure).getHeaders();
                            }

                            Duration delay;

                            if (headers != null && headers.containsKey("Retry-After")) {
                                String retryAfterValue = headers.getFirst("Retry-After");
                                delay = parseRetryAfter(retryAfterValue);
                            } else {
                                delay = Duration.ofSeconds((long) Math.pow(2, retryCount.get()));
                            }

                            System.out.printf("Retry attempt #%d after %d seconds due to 429\n", retryCount.get(), delay.getSeconds());

                            try {
                                Thread.sleep(delay.toMillis());
                            } catch (InterruptedException e) {
                                Thread.currentThread().interrupt();
                            }
                        })
                );
    }

    private Duration parseRetryAfter(String retryAfterValue) {
        try {
            long seconds = Long.parseLong(retryAfterValue);
            return Duration.ofSeconds(seconds);
        } catch (NumberFormatException e) {
            return Duration.ofSeconds(5);
        }
    }

    // DTOs για το request
    public static class GeminiRequest {
        private List<ContentPart> contents;

        public GeminiRequest(List<ContentPart> contents) {
            this.contents = contents;
        }

        public List<ContentPart> getContents() {
            return contents;
        }

        public void setContents(List<ContentPart> contents) {
            this.contents = contents;
        }
    }

    public static class ContentPart {
        private List<ContentText> parts;

        public ContentPart(List<ContentText> parts) {
            this.parts = parts;
        }

        public List<ContentText> getParts() {
            return parts;
        }

        public void setParts(List<ContentText> parts) {
            this.parts = parts;
        }
    }

    public static class ContentText {
        private String text;

        public ContentText(String text) {
            this.text = text;
        }

        public String getText() {
            return text;
        }

        public void setText(String text) {
            this.text = text;
        }
    }

    // DTOs για το response
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class GeminiResponse {
        public List<Candidate> candidates;
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Candidate {
        public GeminiContent content;
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class GeminiContent {
        public List<ContentText> parts;
    }
}
