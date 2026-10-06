package com.example.democi;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.servlet.http.HttpServletRequest;

@RestController
public class IndexController {

    private static final DateTimeFormatter ACCESSED_AT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
            .withZone(ZoneId.of("Asia/Seoul"));

    private final VisitLogRepository visitLogRepository;

    public IndexController(VisitLogRepository visitLogRepository) {
        this.visitLogRepository = visitLogRepository;
    }

    @GetMapping("/index")
    public String index(HttpServletRequest request) {
        String clientIp = request.getRemoteAddr();
        Instant accessedAt = Instant.now();
        visitLogRepository.save(new VisitLog(clientIp, accessedAt));
        long n = visitLogRepository.count();
        return "안녕하세요. 주연의 docker 학습용 최종 페이지 입니다. 당신은 " + n
                + "번째 방문자입니다. 접속 IP: " + clientIp
                + ", 접속 시간: " + ACCESSED_AT.format(accessedAt);
    }
}
