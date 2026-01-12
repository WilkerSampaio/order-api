package com.wilker.order.infrastructure.dto.out;

import com.wilker.order.infrastrucuture.dto.out.ItemResponseDTO;

import java.math.BigDecimal;

public class ItemResponseDTOFixture {

    public static ItemResponseDTO build(Long id,
                                        String produtoNome,
                                        Integer quantidade,
                                        BigDecimal precoUnitario,
                                        BigDecimal subTotal){
        return new ItemResponseDTO(id, produtoNome, quantidade, precoUnitario, subTotal);

    }

}
