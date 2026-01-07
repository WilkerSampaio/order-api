package com.wilker.order.service;

import com.wilker.order.infrastrucuture.dto.in.PedidoRequestDTO;
import com.wilker.order.infrastrucuture.dto.out.PedidoResponseDTO;
import com.wilker.order.infrastrucuture.entity.ItemEntity;
import com.wilker.order.infrastrucuture.entity.PedidoEntity;
import com.wilker.order.infrastrucuture.enums.StatusEnum;
import com.wilker.order.infrastrucuture.exception.ResourceNotFoundException;
import com.wilker.order.infrastrucuture.exception.StatusException;
import com.wilker.order.infrastrucuture.mapper.PedidoMapperConverter;
import com.wilker.order.infrastrucuture.repository.PedidoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.time.Clock;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PedidoService {

    private final PedidoRepository pedidoRepository;
    private final PedidoMapperConverter pedidoMapperConverter;
    private final Clock clock; // <--- Precisa do clock para ele usar o relogio que eu criei.

    public PedidoResponseDTO criarPedido(PedidoRequestDTO pedidoRequestDTO) {

        PedidoEntity pedidoEntity = pedidoMapperConverter.paraPedidoEntity(pedidoRequestDTO);

        pedidoEntity.setStatusEnum(StatusEnum.CRIADO);
        pedidoEntity.setDataCriacao(LocalDateTime.now(clock));

        BigDecimal valorTotal = BigDecimal.ZERO;

        if(pedidoEntity.getItensEntity() != null){
            for(ItemEntity itemEntity : pedidoEntity.getItensEntity()){

                 itemEntity.setPedidoEntity(pedidoEntity);

                 BigDecimal subTotal = itemEntity.getPrecoUnitario().multiply(BigDecimal.valueOf(itemEntity.getQuantidade()));

                 itemEntity.setSubTotal(subTotal);

                valorTotal = valorTotal.add(subTotal);

            }
        }
        pedidoEntity.setValorTotal(valorTotal);

        PedidoEntity salvo = pedidoRepository.save(pedidoEntity);

        return pedidoMapperConverter.paraPedidoResponse(salvo);

    }

    public List<PedidoResponseDTO> buscaPedidos(){

        List<PedidoEntity> pedidoEntityList = pedidoRepository.findAll();

        if(pedidoEntityList.isEmpty()){
            throw new ResourceNotFoundException("Nenhum pedido foi encontrado");
        }

        return pedidoMapperConverter.paraPedidoResponseList(pedidoEntityList);
    }

    public PedidoResponseDTO buscarPedidoPeloId(Long id){

        PedidoEntity pedidoEntity = buscaPedido(id);

        return pedidoMapperConverter.paraPedidoResponse(pedidoEntity);
    }
    public PedidoResponseDTO atualizaStatusPedido(Long id, StatusEnum statusEnum){

        PedidoEntity pedidoEntity =  buscaPedido(id);

        StatusEnum statusAtual = pedidoEntity.getStatusEnum();

        if(statusAtual == StatusEnum.CANCELADO){
            throw new StatusException("Não é possível alterar o status de pedidos cancelados");
        }

        if(statusEnum == StatusEnum.CANCELADO){

            pedidoEntity.setStatusEnum(StatusEnum.CANCELADO);
            return pedidoMapperConverter.paraPedidoResponse(pedidoRepository.save(pedidoEntity));
        }

        else if(statusAtual == StatusEnum.CRIADO && statusEnum == StatusEnum.PAGO){
            pedidoEntity.setStatusEnum(statusEnum);
        }

        else if(statusAtual == StatusEnum.PAGO && statusEnum == StatusEnum.ENVIADO){
            pedidoEntity.setStatusEnum(statusEnum);
        }
        else{
            throw new StatusException("Transição status inválida");
        }

        return pedidoMapperConverter.paraPedidoResponse(pedidoRepository.save(pedidoEntity));
    }

    @Transactional
    public void deletaPedidoPeloId(Long id){

        PedidoEntity pedidoEntity = buscaPedido(id);

        pedidoRepository.deleteById(id);

    }

    private PedidoEntity buscaPedido(Long id){

        return pedidoRepository.findById(id).orElseThrow(
                ()-> new ResourceNotFoundException("O pedido " + id + " não foi encontrado"));

    }
}





