package sbapiserver.ddns.net.upload_server.common;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import sbapiserver.ddns.net.upload_server.config.FileStorageProperties;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Component
public class RootPathProvider {

    private final FileStorageProperties storageProperties;
    private final Path rootLocation;

    public RootPathProvider(FileStorageProperties properties) {
        this.storageProperties = properties;
        this.rootLocation = properties.getRootPath();
        initRoot();
    }

    private void initRoot() {
        try {
            Files.createDirectories(rootLocation);
        } catch (IOException e) {
            throw new RuntimeException("루트 디렉터리 생성 실패", e);
        }
    }

    public Path getRootPath() {
        return rootLocation;
    }

    public Path resolveSafePath(String relativePath) {
        Path resolved = rootLocation.resolve(Paths.get(relativePath)).normalize();
        if (!resolved.startsWith(rootLocation)) {
            throw new SecurityException("허용되지 않은 경로입니다.");
        }
        return resolved;
    }
}
