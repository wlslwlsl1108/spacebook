package com.kjh.spacebook.domain.recommendation.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kjh.spacebook.common.exception.BusinessException;
import com.kjh.spacebook.domain.space.enums.SpaceType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.RestTemplate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class GroqServiceTest {

    @Mock
    private RestTemplate restTemplate;

    private GroqService groqService;

    @BeforeEach
    void setUp() {
        groqService = new GroqService(new ObjectMapper(), restTemplate);
        ReflectionTestUtils.setField(groqService, "apiKey", "test-key");
    }

    @Test
    @DisplayName("정상 응답 파싱 성공")
    void extractCondition_success() {
        // given
        String groqResponse = """
                {
                  "choices": [{
                    "message": {
                      "content": "{\\"location\\":\\"강남\\",\\"capacity\\":3,\\"spaceType\\":\\"MEETING\\"}"
                    }
                  }]
                }
                """;

        given(restTemplate.postForObject(anyString(), any(), eq(String.class)))
                .willReturn(groqResponse);

        // when
        GroqService.SearchCondition result = groqService.extractCondition("강남에서 3명 미팅");

        // then
        assertThat(result.location()).isEqualTo("강남");
        assertThat(result.capacity()).isEqualTo(3);
        assertThat(result.spaceType()).isEqualTo(SpaceType.MEETING);
    }

    @Test
    @DisplayName("null 필드가 포함된 응답 파싱 성공")
    void extractCondition_withNullFields() {
        // given
        String groqResponse = """
                {
                  "choices": [{
                    "message": {
                      "content": "{\\"location\\":null,\\"capacity\\":1,\\"spaceType\\":\\"STUDY\\"}"
                    }
                  }]
                }
                """;

        given(restTemplate.postForObject(anyString(), any(), eq(String.class)))
                .willReturn(groqResponse);

        // when
        GroqService.SearchCondition result = groqService.extractCondition("혼자 공부할 곳");

        // then
        assertThat(result.location()).isNull();
        assertThat(result.capacity()).isEqualTo(1);
        assertThat(result.spaceType()).isEqualTo(SpaceType.STUDY);
    }

    @Test
    @DisplayName("API 호출 실패 시 AI_API_FAILED 예외")
    void extractCondition_apiFail() {
        // given
        given(restTemplate.postForObject(anyString(), any(), eq(String.class)))
                .willThrow(new RuntimeException("API 호출 실패"));

        // when & then
        assertThatThrownBy(() -> groqService.extractCondition("강남 미팅"))
                .isInstanceOf(BusinessException.class);
    }

    @Test
    @DisplayName("응답 파싱 실패 시 AI_PARSE_FAILED 예외")
    void extractCondition_parseFail() {
        // given
        given(restTemplate.postForObject(anyString(), any(), eq(String.class)))
                .willReturn("invalid json");

        // when & then
        assertThatThrownBy(() -> groqService.extractCondition("강남 미팅"))
                .isInstanceOf(BusinessException.class);
    }
}
