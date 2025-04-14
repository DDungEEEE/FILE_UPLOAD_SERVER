package sbapiserver.ddns.net.upload_server.domain.file.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import sbapiserver.ddns.net.upload_server.config.FileStorageProperties;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

@Service
public class FileService {
    private final FileStorageProperties storageProperties;
    private final Path rootLocation;

    public FileService(FileStorageProperties storageProperties){
        this.storageProperties = storageProperties;
        this.rootLocation = storageProperties.getRootPath();
        initRoot(); // 시작 시 디렉토리 있는지 검사 -> 없으면 생성
    }

    private void initRoot(){
        try {
            Files.createDirectories(rootLocation);
        }catch (IOException e){
            throw new RuntimeException("루트 디렉터리 생성 실패", e);
        }
    }

    public void createFolder(String folderName){
        try {
            if (!StringUtils.hasText(folderName)) {
                throw new IllegalStateException("폴더 이름이 비어있습니다.");
            }

            Path newFolderPath = rootLocation.resolve(folderName).normalize();

            // 루트 밖으로 나가는 공격 방지
            if (!newFolderPath.startsWith(rootLocation)) {
                throw new SecurityException("허용되지 않은 경로입니다.");
            }
            Files.createDirectories(newFolderPath);
        }catch (IOException ex){
            throw new RuntimeException("폴더 생성 실패: " + ex.getMessage(), ex);
        }
    }

    public void saveFile(MultipartFile file){
        System.out.println("실행됨");
        try{
            if(file.isEmpty()){
                throw new IllegalArgumentException("업로드 된 파일이 비어있습니다.");
            }

            Path destinationFile = rootLocation.resolve(file.getOriginalFilename()).normalize();

            if(!destinationFile.startsWith(rootLocation)){
                throw new SecurityException("허용되지 않은 경로 접근");
            }
            file.transferTo(destinationFile.toFile());
        }catch (IOException e){
            throw new RuntimeException("파일 저장 실패: " + e.getMessage(), e);
        }
    }
    public List<Map<String, String>> getAllFolders() {
        // 실제 구현 전까지는 더미 데이터로 처리
        try (Stream<Path> stream = Files.list(rootLocation)) {
            return stream
                    .filter(Files::isDirectory)
                    .map(path -> Map.of("name", path.getFileName().toString()))
                    .toList();
        } catch (IOException e) {
            throw new RuntimeException("폴더 목록 조회 실패", e);
        }
    }

    public List<Map<String, String>> getAllFiles() {
        try (Stream<Path> stream = Files.list(rootLocation)) {
            return stream
                    .filter(Files::isRegularFile)
                    .map(path -> Map.of("originalFileName", path.getFileName().toString()))
                    .toList();
        } catch (IOException e) {
            throw new RuntimeException("파일 목록 조회 실패", e);
        }
    }

    public List<Map<String, String>> getSubFolders(Path path) {
        try (Stream<Path> stream = Files.list(path)) {
            return stream
                    .filter(Files::isDirectory)
                    .map(p -> Map.of("name", p.getFileName().toString()))
                    .toList();
        } catch (IOException e) {
            throw new RuntimeException("폴더 목록 조회 실패", e);
        }
    }

    public List<Map<String, String>> getFiles(Path path) {
        try (Stream<Path> stream = Files.list(path)) {
            return stream
                    .filter(Files::isRegularFile)
                    .map(p -> Map.of("originalFileName", p.getFileName().toString()))
                    .toList();
        } catch (IOException e) {
            throw new RuntimeException("파일 목록 조회 실패", e);
        }
    }

    public Path getRoot(){
        return rootLocation;
    }
}
