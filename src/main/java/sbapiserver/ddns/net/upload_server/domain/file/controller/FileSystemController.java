//package sbapiserver.ddns.net.upload_server.domain.file.controller;
//
//import io.swagger.v3.oas.annotations.Operation;
//import io.swagger.v3.oas.annotations.tags.Tag;
//import lombok.RequiredArgsConstructor;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.GetMapping;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RestController;
//import sbapiserver.ddns.net.upload_server.domain.file.service.FileService;
//
//@RestController
//@RequiredArgsConstructor
//@RequestMapping("/api/files/system")
//@Tag(name = "파일 시스템 API", description = "파일 서버 시스템 정보 관련 기능")
//public class FileSystemController {
//
//    private final FileService fileService;
//
//    @GetMapping("/root")
//    @Operation(summary = "루트 경로 조회", description = "서버에 설정된 루트 저장소 경로를 반환합니다.")
//    public ResponseEntity<String> getRootPath() {
//        return ResponseEntity.ok(fileService.getRoot().toString());
//    }
//}
