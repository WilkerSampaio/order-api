package com.wilker.order.infrastrucuture.mapper;

import com.wilker.order.infrastrucuture.dto.in.ItemRequestDTO;
import com.wilker.order.infrastrucuture.dto.out.ItemResponseDTO;
import com.wilker.order.infrastrucuture.entity.ItemEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ItemMapperConverter {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "subTotal", ignore = true)
    @Mapping(target = "pedidoEntity", ignore = true)
    ItemEntity paraItemEntity(ItemRequestDTO itemRequestDTO);

    ItemResponseDTO paraItemResponse(ItemEntity itemEntity);

}


