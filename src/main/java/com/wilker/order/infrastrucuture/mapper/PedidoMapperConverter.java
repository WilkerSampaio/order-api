package com.wilker.order.infrastrucuture.mapper;

import com.wilker.order.infrastrucuture.dto.in.PedidoRequestDTO;
import com.wilker.order.infrastrucuture.dto.out.PedidoResponseDTO;
import com.wilker.order.infrastrucuture.entity.PedidoEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring", uses = ItemMapperConverter.class)
public interface PedidoMapperConverter {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "statusEnum", ignore = true)
    @Mapping(target = "valorTotal", ignore = true)
    @Mapping(target = "dataCriacao", ignore = true)
    @Mapping(target = "itensEntity", source = "itens")
    PedidoEntity paraPedidoEntity(PedidoRequestDTO pedidoRequestDTO);

    @Mapping(target = "itens", source = "itensEntity")
    PedidoResponseDTO paraPedidoResponse(PedidoEntity pedidoEntity);

    List<PedidoResponseDTO> paraPedidoResponseList(List<PedidoEntity> pedidoEntityList);
}

