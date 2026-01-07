package com.wilker.order.infrastructure.dto.in;


import com.wilker.order.infrastrucuture.dto.in.ItemRequestDTO;
import com.wilker.order.infrastrucuture.dto.in.PedidoRequestDTO;

import java.util.Set;

public class PedidoRequestDTOFixture {

     public static PedidoRequestDTO build(String clienteNome, Set<ItemRequestDTO> itens){
         return new PedidoRequestDTO(clienteNome, itens);

     }

}
