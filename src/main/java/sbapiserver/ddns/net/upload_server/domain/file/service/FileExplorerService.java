package sbapiserver.ddns.net.upload_server.domain.file.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import sbapiserver.ddns.net.upload_server.domain.file.dto.FileNodeDto;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Stream;


@Service
@RequiredArgsConstructor
public class FileExplorerService {

    private final PathValidService pathValidService;

    public List<FileNodeDto> getFileAndFolders(String relativePath) {
        Path base = pathValidService.resolveRelativePathAndValid(relativePath);

        try (Stream<Path> stream = Files.list(base)) {
            return stream.map(p -> {
                try {
                    if (Files.isDirectory(p)) {
                        return new FileNodeDto(
                                p.getFileName().toString(),
                                "FOLDER",
                                null,
                                Files.getLastModifiedTime(p).toString()
                        );
                    } else {
                        return new FileNodeDto(
                                p.getFileName().toString(),
                                "FILE",
                                Files.size(p),
                                Files.getLastModifiedTime(p).toString()
                        );
                    }
                } catch (IOException e) {
                    throw new RuntimeException("파일 정보 조회 실패", e);
                }
            }).toList();
        } catch (IOException e) {
            throw new RuntimeException("디렉토리 내 목록 조회 실패", e);
        }
    }

}

