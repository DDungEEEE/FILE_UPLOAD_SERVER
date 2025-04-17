package sbapiserver.ddns.net.upload_server.domain.file.controller;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import sbapiserver.ddns.net.upload_server.domain.file.service.FileService;

import java.io.IOException;

@RestController
@RequestMapping("/api/files/upload")
@RequiredArgsConstructor
@Tag(name = "File Upload API", description = "파일 업로드 및 병합")
public class FileUploadController {

    private final FileService fileService;
    @Operation(summary = "파일 청크 업로드", description = "대용량 파일을 분할하여 청크 단위로 업로드합니다. 마지막 청크인 경우 자동 병합됩니다.")
    @PostMapping(consumes = {"multipart/form-data"})
    public ResponseEntity<Void> uploadChunk(
            @Parameter(description = "파일 고유 식별자", required = true)
            @RequestPart String fileId,

            @Parameter(description = "청크 인덱스 (0부터 시작)", required = true)
            @RequestPart int chunkIndex,

            @Parameter(description = "전체 청크 개수", required = true)
            @RequestPart int totalChunks,

            @Parameter(description = "원본 파일명", required = true)
            @RequestPart String originalFilename,

            @Parameter(description = "저장할 폴더 경로, root = . or / or 공백 ,상대경로로 인식한다. ex) a/b/c 절대경로로 입력 시 오류 발생 ex) /a/b/c", required = true)
            @RequestPart String path,

            @Parameter(description = "업로드할 청크 데이터", required = true, content = @Content(mediaType = MediaType.MULTIPART_FORM_DATA_VALUE))
            @RequestPart MultipartFile chunk
    )throws IOException {
        System.out.println("------------------------------------------");
        fileService.saveChunk(fileId, chunkIndex, chunk);

        if (chunkIndex == totalChunks - 1 && fileService.allChunksReceived(fileId, totalChunks)) {
            System.out.println("실행중");
            fileService.mergeChunks(fileId, totalChunks, originalFilename, path);
        }

        return ResponseEntity.ok().build();
    }
}
