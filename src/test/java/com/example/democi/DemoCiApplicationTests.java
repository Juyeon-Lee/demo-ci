package com.example.democi;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;

import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class DemoCiApplicationTests {

    @LocalServerPort
    int port;

    @Test
    void contextLoads() {
    }

    @Test
    void indexRecordsClientIpAndAccessTime() throws Exception {
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder(URI.create("http://127.0.0.1:" + port + "/index"))
                .GET()
                .build();

        String first = client.send(request, HttpResponse.BodyHandlers.ofString()).body();
        String second = client.send(request, HttpResponse.BodyHandlers.ofString()).body();

        assertTrue(first.contains("1번째 방문자"));
        assertTrue(first.contains("접속 IP: 127.0.0.1"));
        assertTrue(first.contains("접속 시간:"));
        assertTrue(second.contains("2번째 방문자"));
    }

}
