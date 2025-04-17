package sbapiserver.ddns.net.upload_server.domain.file.service;

import org.springframework.stereotype.Service;
import org.springframework.util.FileSystemUtils;
import org.springframework.web.multipart.MultipartFile;
import sbapiserver.ddns.net.upload_server.config.FileStorageProperties;
import java.io.BufferedOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;


@Service
public class FileService {
    private final FileStorageProperties storageProperties;
    private final Path rootLocation;

    public FileService(FileStorageProperties storageProperties){
        this.storageProperties = storageProperties;
        this.rootLocation = storageProperties.getRootPath();
        initRoot();
    }

    private void initRoot(){
        try {
            Files.createDirectories(rootLocation);
        }catch (IOException e){
            throw new RuntimeException("루트 디렉터리 생성 실패", e);
        }
    }

    public Path getRoot(){
        return rootLocation;
    }

    public void createFolder(String relativePath, String folderName) {
        Path fullPath = rootLocation.resolve(Paths.get(relativePath)).resolve(folderName).normalize();
        if (!fullPath.startsWith(rootLocation)) {
            throw new SecurityException("허용되지 않은 경로입니다.");
        }
        try {
            Files.createDirectories(fullPath);
        } catch (IOException ex) {
            throw new RuntimeException("폴더 생성 실패", ex);
        }
    }

    public void saveChunk(String fileId, int chunkIndex, MultipartFile chunk) throws IOException {
        Path tempDir = Paths.get("upload-temp", fileId);
        Files.createDirectories(tempDir);
        System.out.println(chunkIndex);
        Path chunkPath = tempDir.resolve("chunk_" + chunkIndex);
        chunk.transferTo(chunkPath);
    }

    public boolean allChunksReceived(String fileId, int totalChunks) {
        Path tempDir = Paths.get("upload-temp", fileId);
        for (int i = 0; i < totalChunks; i++) {
            if (!Files.exists(tempDir.resolve("chunk_" + i))) return false;
        }
        return true;
    }

    public void mergeChunks(String fileId, int totalChunks, String originalFilename, String path) throws IOException {
        Path tempDir = Paths.get("upload-temp", fileId);
        Path uploadDir = rootLocation.resolve(path).normalize();
        Files.createDirectories(uploadDir);

        Path outputFile = uploadDir.resolve(originalFilename);
        try (OutputStream out = new BufferedOutputStream(Files.newOutputStream(outputFile))) {
            for (int i = 0; i < totalChunks; i++) {
                Files.copy(tempDir.resolve("chunk_" + i), out);
            }
        }
        FileSystemUtils.deleteRecursively(tempDir);
    }

}
