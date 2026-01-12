package com.wilker.order.infrastructure.mapper;

import com.wilker.order.infrastructure.dto.in.PedidoRequestDTOFixture;
import com.wilker.order.infrastructure.dto.out.PedidoResponseDTOFixture;
import com.wilker.order.infrastrucuture.dto.in.PedidoRequestDTO;
import com.wilker.order.infrastrucuture.dto.out.PedidoResponseDTO;
import com.wilker.order.infrastrucuture.entity.PedidoEntity;
import com.wilker.order.infrastrucuture.enums.StatusEnum;
import com.wilker.order.infrastrucuture.mapper.PedidoMapperConverter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;


import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

public class PedidoMapperConverterTest {

    private PedidoRequestDTO pedidoRequestDTO;
    private PedidoEntity pedidoEntity;
    private PedidoMapperConverter pedidoMapperConverter;
    private List<PedidoEntity> pedidoEntityList;

    @BeforeEach
    void setup(){

        pedidoMapperConverter = Mappers.getMapper(PedidoMapperConverter.class);

        pedidoEntity = PedidoEntity.builder()
                .id(1L)
                .clienteNome("Marcos Sampaio")
                .statusEnum(StatusEnum.CRIADO)
                .valorTotal(BigDecimal.valueOf(100.00))
                .dataCriacao(LocalDateTime.of(2026, 2, 2,6,6,5,4))
                .itensEntity(Collections.emptySet())
                .build();

        pedidoRequestDTO = PedidoRequestDTOFixture.build("Marcos Sampaio", Collections.emptySet());

        pedidoEntityList = List.of(pedidoEntity);
    }

    @Test
    void deveConverterParaPedidoEntityComSucesso(){

        PedidoEntity entity =  pedidoMapperConverter.paraPedidoEntity(pedidoRequestDTO);

        assertEquals(pedidoRequestDTO.clienteNome(), entity.getClienteNome());
        assertEquals(0, entity.getItensEntity().size());

        assertNull(entity.getId());
        assertNull(entity.getStatusEnum());
        assertNull(entity.getValorTotal());
        assertNull(entity.getDataCriacao());
    }

    @Test
    void deveConverterParaPedidoResponseComSucesso(){

        PedidoResponseDTO responseDTO = pedidoMapperConverter.paraPedidoResponse(pedidoEntity);

        assertEquals(pedidoEntity.getId(), responseDTO.id());
        assertEquals(pedidoEntity.getStatusEnum(), responseDTO.statusEnum());
        assertEquals(pedidoEntity.getValorTotal(), responseDTO.valorTotal());
        assertEquals(pedidoEntity.getDataCriacao(), responseDTO.dataCriacao());
        assertEquals(0, responseDTO.itens().size());
    }

    @Test
    void deveConverterParaPedidoResponseListComSucesso(){

        List<PedidoResponseDTO> responseDTOList = pedidoMapperConverter.paraPedidoResponseList(pedidoEntityList);

        assertEquals(pedidoEntityList.get(0).getId(), responseDTOList.get(0).id());
        assertEquals(pedidoEntityList.get(0).getClienteNome(), responseDTOList.get(0).clienteNome());

        assertEquals(1, pedidoEntityList.size());

    }
}
