package com.wilker.order.infrastrucuture.dto.out;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.wilker.order.infrastrucuture.enums.StatusEnum;
import lombok.Builder;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Set;

@Builder
public record PedidoResponseDTO(
        Long id,
        String clienteNome,
        StatusEnum statusEnum,
        BigDecimal valorTotal,
        @JsonFormat(pattern = "dd/MM/yyyy HH:mm:ss")
        LocalDateTime dataCriacao,
        Set<ItemResponseDTO> itens
) {
}
