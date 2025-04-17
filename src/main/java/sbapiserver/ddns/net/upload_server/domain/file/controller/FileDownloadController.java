package sbapiserver.ddns.net.upload_server.domain.file.controller;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import sbapiserver.ddns.net.upload_server.domain.file.service.FileDownloadService;
import sbapiserver.ddns.net.upload_server.domain.file.service.FileService;

import java.io.IOException;
import java.nio.file.Path;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/files/download")
@Tag(name = "파일 다운로드 API", description = "파일 다운로드 관련 기능")
public class FileDownloadController {

    private final FileDownloadService fileDownloadService;

    @Operation(summary = "파일 다운로드", description = "지정된 경로와 파일명을 기반으로 파일을 다운로드합니다.")
    @GetMapping
    public ResponseEntity<Resource> downloadFile(
            @Parameter(description = "다운로드할 파일의 경로 (한글 포함 시 URL 인코딩 필요: 예) %EC%83%88%20%ED%8F%B4%EB%8D%94), 상대경로로 인식한다. ex) a/b/c 절대경로로 입력 시 오류 발생 ex) /a/b/c")
            @RequestParam String path ,
            @Parameter(description = "다운로드할 파일명", required = true)
            @RequestParam String filename
    )throws IOException {
        Path file = fileDownloadService.resolveAndValidate(path, filename);
        Resource resource = fileDownloadService.loadAsResource(file);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + file.getFileName().toString() + "\"")
                .body(resource);
    }
}