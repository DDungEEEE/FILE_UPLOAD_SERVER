package sbapiserver.ddns.net.upload_server.common;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data @Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ErrorResponse {
    private String reason;
    private int status;

    public ErrorResponse(final ErrorCode errorCode, int status) {
        this.reason = errorCode.getReason();
        this.status = status;
    }
}
