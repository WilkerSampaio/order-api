package com.wilker.order.infrastrucuture.dto.in;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Builder;

import java.util.Set;

@Builder
public record PedidoRequestDTO(
        @NotBlank(message = "O nome do cliente é obrigatório")
        @Size(min = 10, max = 155, message = "O tamanho mínimo do nome é 10 e o máximo 155")
         String clienteNome,

         @NotEmpty(message = "É obrigatório ter 1 item no mínimo")
         Set<@Valid ItemRequestDTO> itens
) {
}
