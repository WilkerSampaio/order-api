package com.wilker.order.infrastrucuture.dto.out;

import lombok.Builder;

import java.math.BigDecimal;

@Builder
public record ItemResponseDTO(
        Long id,
        String produtoNome,
        Integer quantidade,
        BigDecimal precoUnitario,
        BigDecimal subTotal

) {
}
