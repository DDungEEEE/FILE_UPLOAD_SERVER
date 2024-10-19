package sbapiserver.ddns.net.upload_server.controller;


import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Controller
public class FileUploadController {

    private static final String UPLOAD_DIR = "uploads";

    static {
        File uploadDir = new File(UPLOAD_DIR);
        if(!uploadDir.exists()){
            uploadDir.mkdir();
        }
    }

    @PostMapping("/upload")
    public String uploadFile(@RequestParam("file") MultipartFile file, Model model){
        String originalFilename = file.getOriginalFilename();
        Path path = Paths.get(UPLOAD_DIR, originalFilename);

        try{
            Files.write(path, file.getBytes());
        }catch (IOException e){
            e.printStackTrace();
            model.addAttribute("message", "파일 업로드 실패");
            return "error";
        }

        String fileUri = ServletUriComponentsBuilder.fromCurrentContextPath()
                .path("/uploads/")
                .path(originalFilename)
                .toUriString();

        model.addAttribute("message", "파일 업로드 성공 : " +fileUri);
        return "success";
    }

    @GetMapping("/download")
    public ResponseEntity<Resource> downloadFile(@RequestParam("filename") String filename){
        File file = new File(UPLOAD_DIR, filename);

        if(!file.exists()){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }

        Resource resource = new FileSystemResource(file);

        return ResponseEntity.ok().header(HttpHeaders.CONTENT_DISPOSITION, "attachment;" +
                "filename=\"" + filename +"\"")
                .body(resource);
    }
}
