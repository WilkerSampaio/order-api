package com.wilker.order.infrastructure.dto.out;

import com.wilker.order.infrastrucuture.dto.out.ItemResponseDTO;
import com.wilker.order.infrastrucuture.dto.out.PedidoResponseDTO;
import com.wilker.order.infrastrucuture.enums.StatusEnum;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Set;

public class PedidoResponseDTOFixture {

    public static PedidoResponseDTO build(Long id,
                                          String clienteNome,
                                          StatusEnum statusEnum,
                                          BigDecimal valorTotal,
                                          LocalDateTime dataCriacao,
                                          Set<ItemResponseDTO> itens){

        return new PedidoResponseDTO(id, clienteNome, statusEnum, valorTotal, dataCriacao, itens);
    }
}
