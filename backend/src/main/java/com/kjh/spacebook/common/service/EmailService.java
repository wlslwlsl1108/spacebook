package com.kjh.spacebook.common.service;

import com.kjh.spacebook.domain.reservation.entity.Reservation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmailService {
    private final RestTemplate restTemplate;

    @Value("${sendgrid.api-key}")
    private String apiKey;

    @Value("${sendgrid.from-email}")
    private String fromEmail;

    private static final String SENDGRID_API_URL = "https://api.sendgrid.com/v3/mail/send";
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    @Async
    public void sendReservationConfirm(String toEmail, Reservation reservation) {
        String subject = "[SpaceBook] 예약이 확정되었습니다.";
        String body = String.format(
                """
                안녕하세요, SpaceBook입니다.

                예약이 확정되었습니다.

                ■ 공간: %s
                ■ 일시: %s ~ %s
                ■ 인원: %d명
                ■ 총 금액: %,d원

                감사합니다.""",
                reservation.getSpace().getSpaceName(),
                reservation.getStartTime().format(FORMATTER),
                reservation.getEndTime().format(FORMATTER),
                reservation.getPeopleCount(),
                reservation.getTotalPrice()
        );

        send(toEmail, subject, body);
    }

    @Async
    public void sendReservationCancel(String toEmail, Reservation reservation) {
        String subject = "[SpaceBook] 예약이 취소되었습니다.";
        String body = String.format(
                """
                안녕하세요, SpaceBook입니다.

                예약이 취소되었습니다.

                ■ 공간: %s
                ■ 일시: %s ~ %s

                감사합니다.""",
                reservation.getSpace().getSpaceName(),
                reservation.getStartTime().format(FORMATTER),
                reservation.getEndTime().format(FORMATTER)
        );

        send(toEmail, subject, body);
    }

    private void send(String to, String subject, String body) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setBearerAuth(apiKey);

            Map<String, Object> requestBody = Map.of(
                    "personalizations", List.of(Map.of(
                            "to", List.of(Map.of("email", to))
                    )),
                    "from", Map.of(
                            "email", fromEmail,
                            "name", "SpaceBook"
                    ),
                    "subject", subject,
                    "content", List.of(Map.of(
                            "type", "text/plain",
                            "value", body
                    ))
            );

            HttpEntity<Map<String, Object>> request = new HttpEntity<>(requestBody, headers);
            restTemplate.postForEntity(SENDGRID_API_URL, request, String.class);
            log.info("이메일 발송 완료: {}", to);
        } catch (Exception e) {
            log.error("이메일 발송 실패: {}", to, e);
        }
    }
}
