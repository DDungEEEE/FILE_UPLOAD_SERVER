package sbapiserver.ddns.net.upload_server.domain.file.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.util.FileSystemUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import sbapiserver.ddns.net.upload_server.domain.file.service.FileService;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Controller
@RequiredArgsConstructor
public class FileUploadController {

    private final FileService fileService;

    @PostMapping("/upload-chunk")
    public ResponseEntity<String> uploadChunk(
            @RequestParam("chunk") MultipartFile chunk,
            @RequestParam("chunkIndex") int chunkIndex,
            @RequestParam("totalChunks") int totalChunks,
            @RequestParam("fileId") String fileId,
            @RequestParam("originalFilename") String originalFilename,
            @RequestParam(value = "path", required = false, defaultValue = "") String path
    ) throws IOException {

        Path tempDir = Paths.get("upload-temp", fileId);
        Files.createDirectories(tempDir); // 누락된 부분
        Path chunkPath = tempDir.resolve("chunk_" + chunkIndex);
        chunk.transferTo(chunkPath);

        if (allChunksReceived(fileId, totalChunks)) {
            mergeChunks(fileId, totalChunks, originalFilename, path);
        }

        return ResponseEntity.ok("청크 저장 성공");
    }

    private boolean allChunksReceived(String fileId, int totalChunks) {
        Path tempDir = Paths.get("upload-temp", fileId);
        for (int i = 0; i < totalChunks; i++) {
            if (!Files.exists(tempDir.resolve("chunk_" + i))) {
                return false;
            }
        }
        return true;
    }

    private void mergeChunks(String fileId, int totalChunks, String originalFilename, String path) throws IOException {
        Path tempDir = Paths.get("upload-temp", fileId);
        Path uploadDir = Paths.get("uploads").resolve(path);
        Files.createDirectories(uploadDir);

        Path outputFile = uploadDir.resolve(originalFilename);

        try (OutputStream outputStream = new BufferedOutputStream(Files.newOutputStream(outputFile))) {
            for (int i = 0; i < totalChunks; i++) {
                Path chunkFile = tempDir.resolve("chunk_" + i);
                Files.copy(chunkFile, outputStream);
            }
        }

        FileSystemUtils.deleteRecursively(tempDir);
        System.out.println("병합 완료: " + outputFile.toAbsolutePath());
    }

    @PostMapping("/create-folder")
    public String createFolder(@RequestParam("folderName") String folderName,
                               @RequestParam(value = "path", required = false, defaultValue = "") String path) {
        fileService.createFolder(Paths.get(path, folderName).toString());
        return "redirect:/drive?path=" + URLEncoder.encode(path, StandardCharsets.UTF_8);
    }
}
