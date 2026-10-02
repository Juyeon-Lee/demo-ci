package com.example.democi;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.charset.StandardCharsets;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class IndexController {

    private final Path countFile;

    public IndexController(@Value("${app.data-dir:data}") String dataDir) throws IOException {
        Path dataPath = Path.of(dataDir);
        Files.createDirectories(dataPath);
        this.countFile = dataPath.resolve("visitor-count.txt");
        if (!Files.exists(countFile)) {
            Files.writeString(countFile, "0", StandardCharsets.UTF_8);
        }
    }

    @GetMapping("/index")
    public synchronized String index() throws IOException {
        long n = Long.parseLong(Files.readString(countFile, StandardCharsets.UTF_8).trim());
        n++;
        Files.writeString(countFile, Long.toString(n), StandardCharsets.UTF_8);
        return "안녕하세요. 주연의 docker 학습용 페이지 입니다. 당신은 " + n + "번째 방문자입니다.";
    }
}
