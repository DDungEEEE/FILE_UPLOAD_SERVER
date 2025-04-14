package sbapiserver.ddns.net.upload_server.domain.file.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import sbapiserver.ddns.net.upload_server.domain.file.service.FileService;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

@Controller
@RequiredArgsConstructor
public class FileController {
    private final FileService fileService;

    @PostMapping("/create-folder")
    public String createFolder(@RequestParam("folderName") String folderName){
        fileService.createFolder(folderName);
        return "redirect:/";
    }

    @PostMapping("/upload")
    public String handleFileUpload(@RequestParam("file")MultipartFile file){
        fileService.saveFile(file);
        return "redirect:/";
    }

    @GetMapping("/download")
    public ResponseEntity<Resource> download(@RequestParam("filename") String filename)
            throws IOException {
        Path file = fileService.getRoot().resolve(filename).normalize();

        if (!Files.exists(file)) {
            return ResponseEntity.notFound().build();
        }

        Resource resource = new UrlResource(file.toUri());

        String encodedFileName = URLEncoder.encode(filename, StandardCharsets.UTF_8)
                .replaceAll("\\+", "%20");
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename*=UTF-8''" + encodedFileName)
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(resource);
    }
}
