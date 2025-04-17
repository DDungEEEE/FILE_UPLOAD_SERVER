package sbapiserver.ddns.net.upload_server.domain.file.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import sbapiserver.ddns.net.upload_server.domain.file.dto.FileNodeDto;
import sbapiserver.ddns.net.upload_server.domain.file.service.FileExplorerService;

import java.util.List;

@RestController
@RequestMapping("/api/files")
@RequiredArgsConstructor
@Tag(name = "File 탐색 API", description = "파일 및 폴더 목록 조회")
public class FileBrowseController {

    private final FileExplorerService fileExplorerService;

    @GetMapping
    @Operation(summary = "하위 목록 통합 조회", description = "지정한 경로의 폴더 및 파일을 한 번에 조회합니다.")
    public List<FileNodeDto> getChildren(
            @Parameter(description = "조회할 폴더 경로, root = . or / or 공백 ,상대경로로 인식한다. ex) a/b/c 절대경로로 입력 시 오류 발생 ex) /a/b/c", required = true)
            @RequestParam String path
    ) {
        return fileExplorerService.getFileAndFolders(path);
    }
}
