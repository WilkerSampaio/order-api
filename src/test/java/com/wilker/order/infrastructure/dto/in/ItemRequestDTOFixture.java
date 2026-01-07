package com.wilker.order.infrastructure.dto.in;

import com.wilker.order.infrastrucuture.dto.in.ItemRequestDTO;

import java.math.BigDecimal;

public class ItemRequestDTOFixture {

    public static ItemRequestDTO build(String produtoNome, Integer quantidade, BigDecimal precoUnitario){
        return  new ItemRequestDTO(produtoNome, quantidade, precoUnitario);

    }
}
