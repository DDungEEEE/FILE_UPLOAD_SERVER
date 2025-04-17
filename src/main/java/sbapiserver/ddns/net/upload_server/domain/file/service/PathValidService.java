package sbapiserver.ddns.net.upload_server.domain.file.service;

import jakarta.annotation.Nullable;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import sbapiserver.ddns.net.upload_server.config.FileStorageProperties;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;

@Service
@RequiredArgsConstructor
public class PathValidService {
    private final FileStorageProperties fileStorageProperties;

    public Path resolveRelativePathAndValid(String relativePath){
        return resolveRelativePathAndValid(relativePath, null);
    }

    public Path resolveRelativePathAndValid(String relativePath, String filename) {
        String decoded = URLDecoder.decode(relativePath, StandardCharsets.UTF_8);
        Path relative = decoded.isBlank() || decoded.equals("/") ? Paths.get(".") : Paths.get(decoded);

        if (relative.isAbsolute()) {
            throw new SecurityException("절대 경로는 허용되지 않습니다.");
        }
        Path resolved = fileStorageProperties.getRootPath().resolve(relative);

        if (filename != null && !filename.isBlank()) {
            resolved = resolved.resolve(filename);
        }
        Path fullPath = resolved.normalize();
        validatePath(fullPath);
        return fullPath;
    }

    private void validatePath(Path fullPath) {
        Path rootPath = fileStorageProperties.getRootPath();
        if (!fullPath.startsWith(rootPath)) {
            throw new SecurityException("허용되지 않은 경로입니다.");
        }
    }

}
