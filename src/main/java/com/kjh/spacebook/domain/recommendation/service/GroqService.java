package com.kjh.spacebook.domain.recommendation.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kjh.spacebook.common.exception.BusinessException;
import com.kjh.spacebook.domain.recommendation.exception.RecommendationErrorCode;
import com.kjh.spacebook.domain.space.enums.SpaceType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class GroqService {

    @Value("${groq.api-key}")
    private String apiKey;

    private final ObjectMapper objectMapper;
    private final RestTemplate restTemplate;

    private static final String GROQ_URL = "https://api.groq.com/openai/v1/chat/completions";

    private static final String PROMPT = """
            사용자의 자연어 입력에서 공간 검색 조건을 추출해서 JSON으로 반환해.

            추출할 필드:
            - location: 위치/지역 (문자열, 없으면 null)
            - capacity: 인원 수 (숫자, 없으면 null)
            - spaceType: 공간 유형 (STUDY, PARTY, MEETING 중 하나, 없으면 null)
              - 공부/스터디/독서 → STUDY
              - 파티/모임/생일 → PARTY
              - 회의/미팅/세미나 → MEETING

            반드시 JSON만 반환해. 다른 텍스트는 절대 포함하지 마.

            예시:
            입력: "강남에서 3명이 미팅할 수 있는 곳"
            출력: {"location":"강남","capacity":3,"spaceType":"MEETING"}

            입력: "혼자 공부할 조용한 곳"
            출력: {"location":null,"capacity":1,"spaceType":"STUDY"}

            사용자 입력: "%s"
            """;

    public SearchCondition extractCondition(String query) {
        try {
            String prompt = String.format(PROMPT, query);

            Map<String, Object> requestBody = Map.of(
                    "model", "llama-3.3-70b-versatile",
                    "messages", List.of(
                            Map.of("role", "user", "content", prompt)
                    ),
                    "temperature", 0
            );

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setBearerAuth(apiKey);

            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);

            String response = restTemplate.postForObject(GROQ_URL, entity, String.class);

            return parseResponse(response);
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.error("Groq API 호출 실패: {}", e.getMessage());
            throw new BusinessException(RecommendationErrorCode.AI_API_FAILED);
        }
    }

    private SearchCondition parseResponse(String response) {
        try {
            JsonNode root = objectMapper.readTree(response);
            String text = root
                    .path("choices").get(0)
                    .path("message")
                    .path("content")
                    .asText();

            int start = text.indexOf("{");
            int end = text.lastIndexOf("}");
            String json = text.substring(start, end + 1);

            JsonNode result = objectMapper.readTree(json);

            String location = result.has("location") && !result.get("location").isNull()
                    ? result.get("location").asText() : null;

            Integer capacity = result.has("capacity") && !result.get("capacity").isNull()
                    ? result.get("capacity").asInt() : null;

            SpaceType spaceType = null;
            if (result.has("spaceType") && !result.get("spaceType").isNull()) {
                try {
                    spaceType = SpaceType.valueOf(result.get("spaceType").asText());
                } catch (IllegalArgumentException ignored) {
                }
            }

            return new SearchCondition(location, capacity, spaceType);
        } catch (Exception e) {
            log.error("Groq 응답 파싱 실패: {}", e.getMessage());
            throw new BusinessException(RecommendationErrorCode.AI_PARSE_FAILED);
        }
    }

    public record SearchCondition(
            String location,
            Integer capacity,
            SpaceType spaceType
    ) {}
}
