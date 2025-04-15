package sbapiserver.ddns.net.upload_server.domain.file.controller;


import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import sbapiserver.ddns.net.upload_server.domain.file.service.FileService;

import java.io.File;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

@Controller
@RequiredArgsConstructor
public class RootController {
    private final FileService fileService;

    @GetMapping({"/", "/drive"})
    public String browse(@RequestParam(value = "path", required = false, defaultValue = "") String subPath, Model model) {
        String decodedPath = URLDecoder.decode(subPath, StandardCharsets.UTF_8);
        Path root = fileService.getRoot().toAbsolutePath().normalize();
        Path currentPath = root.resolve(decodedPath).normalize();

        if (!currentPath.startsWith(root)) {
            throw new SecurityException("잘못된 경로 접근");
        }

        model.addAttribute("folders", fileService.getSubFolders(currentPath));
        model.addAttribute("files", fileService.getFiles(currentPath));
        model.addAttribute("currentPath", decodedPath); // 그대로 사용

        Map<String, String> encodedPaths = new HashMap<>();
        for (Map<String, String> folder: fileService.getSubFolders(currentPath)) {
            String folderName =  folder.get("name");
            String fullPath = decodedPath.isEmpty() ? folderName : decodedPath + "/" + folderName;
            encodedPaths.put(folderName, URLEncoder.encode(fullPath, StandardCharsets.UTF_8));
        }
        model.addAttribute("encodedPaths", encodedPaths);

        return "index";
    }
}
