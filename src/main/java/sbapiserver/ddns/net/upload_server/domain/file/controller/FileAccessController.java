package sbapiserver.ddns.net.upload_server.domain.file.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import sbapiserver.ddns.net.upload_server.domain.file.service.FileService;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

@RequiredArgsConstructor
@Controller
public class FileAccessController {
    private final FileService fileService;

    @GetMapping("/download")
    public ResponseEntity<Resource> download(@RequestParam("filename") String filename, @RequestParam(required = false) String path)
            throws IOException {
        String decodedFilename = URLDecoder.decode(filename, StandardCharsets.UTF_8);
        String decodedPath = path != null ? URLDecoder.decode(path, StandardCharsets.UTF_8) : "";

        Path basePath = fileService.getRoot().resolve(decodedPath).normalize();
        Path file = basePath.resolve(decodedFilename).normalize();

        if (!file.startsWith(fileService.getRoot()) || !Files.exists(file)) {
            throw new FileNotFoundException("파일이 존재하지 않거나 잘못된 경로입니다.");
        }

        Resource resource = new UrlResource(file.toUri());

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + decodedFilename + "\"")
                .body(resource);
    }

    // 파일 좌클릭 -> 미리보기
    @GetMapping("/preview")
    public ResponseEntity<Resource> previewFile(@RequestParam String filename,
                                                @RequestParam(required = false) String path) throws IOException {
        String decodedFilename = URLDecoder.decode(filename, StandardCharsets.UTF_8);
        String decodedPath = path != null ? URLDecoder.decode(path, StandardCharsets.UTF_8) : "";

        Path file = fileService.getRoot()
                .resolve(decodedPath)
                .resolve(decodedFilename)
                .normalize();

        // 보안 체크
        if (!file.startsWith(fileService.getRoot()) || !Files.exists(file)) {
            throw new FileNotFoundException("파일이 존재하지 않거나 잘못된 경로입니다: " + file.toString());
        }

        Resource resource = new UrlResource(file.toUri());
        String contentType = Files.probeContentType(file);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_TYPE,
                        contentType != null ? contentType : MediaType.APPLICATION_OCTET_STREAM_VALUE)
                .body(resource);
    }
}
