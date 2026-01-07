package com.wilker.order.infrastrucuture.dto.in;

import jakarta.validation.constraints.*;
import lombok.Builder;

import java.math.BigDecimal;

@Builder
public record ItemRequestDTO(

        @NotBlank(message = "O nome do produto é obrigatório")
        @Size(min = 10, max = 155, message = "O mínimo de caracteres é 10 e o máximo 155")
        String produtoNome,

        @NotNull(message = "A quantidade é obrigatório")
        @Min(value = 1, message = "O minímo da quantidade é 1")
        Integer quantidade,

        @NotNull(message = "O preço unitário é obrigatório")
        @DecimalMin(value = "0.01", inclusive = true, message = "O preço unitário deve ser maior que zero")
        @Digits(integer = 10, fraction = 2, message = "O preço unitário deve ter no máximo 10 dígitos e 2 casas decimais")
        BigDecimal precoUnitario
) {
}

