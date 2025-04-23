package sbapiserver.ddns.net.upload_server.domain.file.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class FileChunkResDto {
    private int chunkIndex;
    private int totalChunks;
}
