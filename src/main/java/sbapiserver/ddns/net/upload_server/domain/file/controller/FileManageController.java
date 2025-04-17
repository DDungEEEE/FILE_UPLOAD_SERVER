package sbapiserver.ddns.net.upload_server.domain.file.controller;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import sbapiserver.ddns.net.upload_server.domain.file.service.FileManageService;


@RestController
@RequiredArgsConstructor
@RequestMapping("/api/files/manage")
@Tag(name = "파일 관리 API", description = "폴더 생성 등 파일 관리 기능")
public class FileManageController {
    private final FileManageService fileManageService;

    @Operation(summary = "폴더 생성", description = "지정한 경로에 새로운 폴더를 생성합니다.")
    @PostMapping("/folders")
    public ResponseEntity<Void> createFolder(
            @Parameter(description = "생성 폴더 경로, root = . or / or 공백 ,상대경로로 인식한다. ex) a/b/c 절대경로로 입력 시 오류 발생 ex) /a/b/c", required = true)
            @RequestParam String path,

            @Parameter(description = "생성할 폴더 이름", required = true)
            @RequestParam String folderName
    ) {
        fileManageService.createFolder(path, folderName);
        return ResponseEntity.ok().build();
    }
}
