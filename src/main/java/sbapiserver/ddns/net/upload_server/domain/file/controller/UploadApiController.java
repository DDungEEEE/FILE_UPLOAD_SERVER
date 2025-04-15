package sbapiserver.ddns.net.upload_server.domain.file.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class UploadApiController {


    @PostMapping("/upload")
    public ResponseEntity<?> uploadFile(
            @RequestParam("file") MultipartFile file,
            @RequestParam("fullPath") String fullPath
    ) {
        try {
            Path folder = Paths.get(fullPath).normalize();
            Path destination = folder.resolve(file.getOriginalFilename());

            Files.createDirectories(folder); // 디렉토리 없으면 생성
            file.transferTo(destination.toFile()); // 파일 저장

            Map<String, Object> result = new HashMap<>();
            result.put("message", "업로드 성공");
            result.put("savedPath", destination.toString());
            result.put("success", true);

            return ResponseEntity.ok(result);

        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("message", "업로드 실패: " + fullPath);
            error.put("success", false);

            return ResponseEntity.status(500).body(error);
        }
    }

    @GetMapping("/download")
    public ResponseEntity<?> downloadFile(
            @RequestParam("path") String path,
            @RequestParam("filename") String filename
    ) {
        try {
            String decodedPath = URLDecoder.decode(path, StandardCharsets.UTF_8);
            String decodedFilename = URLDecoder.decode(filename, StandardCharsets.UTF_8);

            Path filePath = Paths.get(decodedPath).resolve(decodedFilename).normalize();

            if (!Files.exists(filePath)) {
                return ResponseEntity.status(404).body("파일이 존재하지 않습니다.");
            }

            Resource resource = new UrlResource(filePath.toUri());

            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION,
                            "attachment; filename=\"" + decodedFilename + "\"")
                    .body(resource);

        } catch (Exception e) {
            return ResponseEntity.status(500).body("다운로드 실패: " + e.getMessage());
        }
    }
}
