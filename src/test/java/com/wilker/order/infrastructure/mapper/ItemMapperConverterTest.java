package com.wilker.order.infrastructure.mapper;

import com.wilker.order.infrastructure.dto.in.ItemRequestDTOFixture;
import com.wilker.order.infrastrucuture.dto.in.ItemRequestDTO;
import com.wilker.order.infrastrucuture.dto.out.ItemResponseDTO;
import com.wilker.order.infrastrucuture.entity.ItemEntity;
import com.wilker.order.infrastrucuture.mapper.ItemMapperConverter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class ItemMapperConverterTest {

    private ItemMapperConverter itemMapperConverter;
    private ItemEntity itemEntity;
    private ItemRequestDTO itemRequestDTO;

    @BeforeEach
    void setup(){

        itemMapperConverter = Mappers.getMapper(ItemMapperConverter.class);

        itemEntity = ItemEntity.builder()
                .id(1L)
                .produtoNome("Nootbook Gamer")
                .quantidade(2)
                .precoUnitario(BigDecimal.valueOf(1000.00))
                .subTotal(BigDecimal.valueOf(2000.00))
                .build();

        itemRequestDTO = ItemRequestDTOFixture.build(
                "Nootbook Gamer",
                2,
                BigDecimal.valueOf(1000.00));
    }

    @Test
    void deveConverterParaItemEntityComSucesso(){

        ItemEntity itemEntity = itemMapperConverter.paraItemEntity(itemRequestDTO);

        assertEquals(itemRequestDTO.produtoNome(), itemEntity.getProdutoNome());
        assertEquals(itemRequestDTO.quantidade(), itemEntity.getQuantidade());
        assertEquals(itemRequestDTO.precoUnitario(), itemEntity.getPrecoUnitario());

        assertNull(itemEntity.getId());
        assertNull(itemEntity.getSubTotal());
        assertNull(itemEntity.getPedidoEntity());

    }

    @Test
    void deveConverterParaItemResponseComSucesso(){

        ItemResponseDTO responseDTO = itemMapperConverter.paraItemResponse(itemEntity);

        assertEquals(responseDTO.id(), itemEntity.getId());
        assertEquals(responseDTO.produtoNome(), itemEntity.getProdutoNome());
        assertEquals(responseDTO.quantidade(), itemEntity.getQuantidade());
        assertEquals(responseDTO.precoUnitario(), itemEntity.getPrecoUnitario());
        assertEquals(responseDTO.subTotal(), itemEntity.getSubTotal());

        assertNull(itemEntity.getPedidoEntity());
    }



}
