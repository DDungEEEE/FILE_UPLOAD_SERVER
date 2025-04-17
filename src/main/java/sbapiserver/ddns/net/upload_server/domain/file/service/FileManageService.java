package sbapiserver.ddns.net.upload_server.domain.file.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import sbapiserver.ddns.net.upload_server.config.FileStorageProperties;

import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;

@Service
@RequiredArgsConstructor
public class FileManageService {

    private final FileStorageProperties fileStorageProperties;
    private final PathValidService pathValidService;

    public void createFolder(String relativePath, String folderName) {
        Path fullPath = pathValidService.resolveRelativePathAndValid(relativePath, folderName);

        try {
            Files.createDirectories(fullPath);
        } catch (IOException ex) {
            throw new RuntimeException("폴더 생성 실패: " + ex.getMessage(), ex);
        }
    }

    public void rename(String relativePath, String oldName, String newName) {
        Path rootPath = fileStorageProperties.getRootPath();
        Path source = rootPath.resolve(Paths.get(relativePath)).resolve(oldName).normalize();
        Path target = rootPath.resolve(Paths.get(relativePath)).resolve(newName).normalize();

        if (!source.startsWith(rootPath) || !target.startsWith(rootPath)) {
            throw new SecurityException("허용되지 않은 경로입니다.");
        }

        try {
            Files.move(source, target, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            throw new RuntimeException("이름 변경 실패: " + e.getMessage(), e);
        }
    }

    public void delete(String relativePath, String name) {
        Path rootPath = fileStorageProperties.getRootPath();
        Path target = rootPath.resolve(Paths.get(relativePath)).resolve(name).normalize();

        if (!target.startsWith(rootPath)) {
            throw new SecurityException("허용되지 않은 경로입니다.");
        }

        try {
            Files.walkFileTree(target, new SimpleFileVisitor<>() {
                @Override
                public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                    Files.delete(file);
                    return FileVisitResult.CONTINUE;
                }

                @Override
                public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
                    Files.delete(dir);
                    return FileVisitResult.CONTINUE;
                }
            });
        } catch (IOException e) {
            throw new RuntimeException("삭제 실패: " + e.getMessage(), e);
        }
    }

    public void move(String fromPath, String name, String toPath) {
        Path rootPath = fileStorageProperties.getRootPath();
        Path source = rootPath.resolve(Paths.get(fromPath)).resolve(name).normalize();
        Path target = rootPath.resolve(Paths.get(toPath)).resolve(name).normalize();

        if (!source.startsWith(rootPath) || !target.startsWith(rootPath)) {
            throw new SecurityException("허용되지 않은 경로입니다.");
        }

        try {
            Files.move(source, target, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            throw new RuntimeException("이동 실패: " + e.getMessage(), e);
        }
    }
}