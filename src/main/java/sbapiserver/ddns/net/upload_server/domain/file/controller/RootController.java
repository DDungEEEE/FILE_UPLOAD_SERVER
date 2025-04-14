package sbapiserver.ddns.net.upload_server.domain.file.controller;


import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import sbapiserver.ddns.net.upload_server.domain.file.service.FileService;

import java.nio.file.Path;

@Controller
@RequiredArgsConstructor
public class RootController {
    private final FileService fileService;

    @GetMapping({"/", "/drive"})
    public String browse(@RequestParam(value = "path", required = false, defaultValue = "") String subPath, Model model) {
        Path currentPath = fileService.getRoot().resolve(subPath).normalize();

        // 보안 처리
        if (!currentPath.startsWith(fileService.getRoot())) {
            throw new SecurityException("잘못된 경로 접근");
        }

        model.addAttribute("folders", fileService.getSubFolders(currentPath));
        model.addAttribute("files", fileService.getFiles(currentPath));
        model.addAttribute("currentPath", subPath); // 현재 경로 유지용
        return "index";
    }
}
