package com.wilker.order.infrastrucuture.dto.out;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record ErrorResponseDTO(
        int status,
        String error,
        String message,
        String path,
        @JsonFormat(pattern = "dd/MM/yyyy HH:mm:ss")
        LocalDateTime timestamp

) {
}
